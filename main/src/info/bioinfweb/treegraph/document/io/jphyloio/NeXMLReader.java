/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2011, 2013-2017  Ben Stöver, Sarah Wiechers, Kai Müller
 * <http://treegraph.bioinfweb.info/>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package info.bioinfweb.treegraph.document.io.jphyloio;


import java.awt.Color;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import info.bioinfweb.commons.io.W3CXSConstants;
import info.bioinfweb.jphyloio.JPhyloIOEventReader;
import info.bioinfweb.jphyloio.ReadWriteParameterMap;
import info.bioinfweb.jphyloio.events.EdgeEvent;
import info.bioinfweb.jphyloio.events.JPhyloIOEvent;
import info.bioinfweb.jphyloio.events.LabeledIDEvent;
import info.bioinfweb.jphyloio.events.NodeEvent;
import info.bioinfweb.jphyloio.events.meta.LiteralMetadataContentEvent;
import info.bioinfweb.jphyloio.events.meta.LiteralMetadataEvent;
import info.bioinfweb.jphyloio.events.meta.ResourceMetadataEvent;
import info.bioinfweb.jphyloio.events.type.EventContentType;
import info.bioinfweb.jphyloio.events.type.EventTopologyType;
import info.bioinfweb.jphyloio.formats.nexml.NeXMLEventReader;
import info.bioinfweb.jphyloio.formats.xtg.XTGConstants;
import info.bioinfweb.jphyloio.utils.JPhyloIOReadingUtils;
import info.bioinfweb.treegraph.document.Branch;
import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.HiddenDataElement;
import info.bioinfweb.treegraph.document.IconLabel;
import info.bioinfweb.treegraph.document.Label;
import info.bioinfweb.treegraph.document.Labels;
import info.bioinfweb.treegraph.document.Legend;
import info.bioinfweb.treegraph.document.Legends;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.PieChartLabel;
import info.bioinfweb.treegraph.document.PieChartLabel.SectionData;
import info.bioinfweb.treegraph.document.ScaleBar;
import info.bioinfweb.treegraph.document.TextElement;
import info.bioinfweb.treegraph.document.TextElementData;
import info.bioinfweb.treegraph.document.TextLabel;
import info.bioinfweb.treegraph.document.Tree;
import info.bioinfweb.treegraph.document.format.BranchFormats;
import info.bioinfweb.treegraph.document.format.GlobalFormats;
import info.bioinfweb.treegraph.document.format.GraphicalLabelFormats;
import info.bioinfweb.treegraph.document.format.IconLabelFormats;
import info.bioinfweb.treegraph.document.format.LegendFormats;
import info.bioinfweb.treegraph.document.format.LegendStyle;
import info.bioinfweb.treegraph.document.format.LineFormats;
import info.bioinfweb.treegraph.document.format.Margin;
import info.bioinfweb.treegraph.document.format.NodeFormats;
import info.bioinfweb.treegraph.document.format.PieChartLabelCaptionContentType;
import info.bioinfweb.treegraph.document.format.PieChartLabelCaptionLinkType;
import info.bioinfweb.treegraph.document.format.PieChartLabelFormats;
import info.bioinfweb.treegraph.document.format.ScaleBarAlignment;
import info.bioinfweb.treegraph.document.format.ScaleBarFormats;
import info.bioinfweb.treegraph.document.format.ScaleValue;
import info.bioinfweb.treegraph.document.format.TextFormats;
import info.bioinfweb.treegraph.document.format.TextOrientation;
import info.bioinfweb.treegraph.document.io.AbstractDocumentReader;
import info.bioinfweb.treegraph.document.io.DocumentIterator;
import info.bioinfweb.treegraph.document.io.SingleDocumentIterator;
import info.bioinfweb.treegraph.document.metadata.LiteralMetadataNode;
import info.bioinfweb.treegraph.document.metadata.MetadataNode;
import info.bioinfweb.treegraph.document.metadata.MetadataPath;
import info.bioinfweb.treegraph.document.metadata.MetadataPathElement;
import info.bioinfweb.treegraph.document.metadata.ResourceMetadataNode;
import info.bioinfweb.treegraph.document.nodebranchdata.BranchLengthAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeNameAdapter;



public class NeXMLReader extends AbstractDocumentReader {
	private static class DecimalAndLocaleStore {
		private String lang = null;
		private String country = null;
		private String variant = null;
		private String formatString = TextFormats.DEFAULT_DECIMAL_FORMAT_EXPR;

		public void setFormatString(String decimal) {
			this.formatString = decimal;
		}

		public void setLang(String lang) {
			this.lang = lang;
		}

		public void setCountry(String country) {
			this.country = country;
		}

		public void setVariant(String variant) {
			this.variant = variant;
		}
		
		public void setInTextFormats(TextFormats f) {
			Locale locale;
			DecimalFormat format;
			
			if (lang == null) {
				locale = TextFormats.DEFAULT_LOCALE;
			}
			else if (country == null) {
				locale = new Locale(lang);
			}
			else if (variant == null) {
				locale = new Locale(lang, country);
			}
			else {
				locale = new Locale(lang, country, variant);
			}
			format = new DecimalFormat(formatString);
			
			f.setLocale(locale);
			f.setDecimalFormat(format, locale);
		}
	}

	
	private class TextElementDataStore {
		private String text = null;
		private boolean decimal = false;
		
		public boolean isDecimal() {
			return decimal;
		}
		
		public void setDecimal(boolean decimal) {
			this.decimal = decimal;
		}
		
		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}

		public void setInTextElementData(TextElementData data) {
			if (getText() == null) {
				data.clear();
			}
			else if (isDecimal()) {
				try {
					data.setDecimal(Double.parseDouble(getText()));
				}
				catch (NumberFormatException e) {
					loadLogger.addWarning("The decimal value attribute \"" + getText() + "\" is malformed. It was imported as a " +
							"textual value instead.");
					data.setText(getText());
				}
			}
			else {
				data.setText(getText());
			}
		}
	}
	
		
	private JPhyloIOEventReader reader;
	private String currentTreeName;
	private List<Tree> trees = new ArrayList<Tree>();
	private List<String> names = new ArrayList<String>();
	private Map<String, Node> idToNodeMap = new HashMap<String, Node>();
	private List<String> possiblePaintStartIDs = new ArrayList<String>();
	private List<String> rootNodeIDs = new ArrayList<String>();  //TODO Mark all additional root nodes with icon label or something similar
	private NodeBranchDataAdapter nodeNameAdapter = NodeNameAdapter.getSharedInstance();
	private BranchLengthAdapter branchLengthAdapter = BranchLengthAdapter.getSharedInstance();
	

	public NeXMLReader() {
		super(false);
	}

	
	@Override
	public Document readDocument(BufferedInputStream stream) throws Exception {
		document = null;
		ReadWriteParameterMap parameters = new ReadWriteParameterMap();
		parameters.put(ReadWriteParameterMap.KEY_USE_OTU_LABEL, true);
		JPhyloIOTools.addTreeGraphObjectTranslators(parameters.getObjectTranslatorFactory(), true);
		reader = new NeXMLEventReader(stream, parameters);  //TODO Use JPhyloIOReader for other formats (currently not possible, due to exceptions)
		
		try {
			JPhyloIOEvent event;
			while (reader.hasNextEvent()) {
	      event = reader.next();
	      switch (event.getType().getContentType()) {
	      	case DOCUMENT:
	      		if (event.getType().getTopologyType().equals(EventTopologyType.START)) {
	      			document = createEmptyDocument();
	      		}
	      		else if (event.getType().getTopologyType().equals(EventTopologyType.END)) {
		          reader.close();
		          
		          if (!trees.isEmpty()) {
		          	document.setTree(trees.get(
		          			parameterMap.getTreeSelector().select(names.toArray(new String[names.size()]), trees)));
		          }
		          else {
		          	throw new IOException("The document did not contain any tree or no valid tree could be read from the document.");
		          }
		          
		          return document;
	      		}
	      		break;
	      	case TREE_NETWORK_GROUP:
	      		readTreeNetworkGroup(event);
	        	break;
	        default:
	        	if (event.getType().getTopologyType().equals(EventTopologyType.START)) {  // Possible additional element, which is not read. SOLE and END events do not have to be processed here, because they have no further content.
	        		JPhyloIOReadingUtils.reachElementEnd(reader);
	      		}
	        	break;
	      }
	    }
		}
		finally {
	    reader.close();
	    stream.close();
		}		
		trees.clear();
		return null;
	}
	
	
	private void readTreeNetworkGroup(JPhyloIOEvent treeGroupEvent) throws XMLStreamException, IOException {
		JPhyloIOEvent event = reader.next();  
    while (!event.getType().getTopologyType().equals(EventTopologyType.END)) {    	
    	if (event.getType().getContentType().equals(EventContentType.TREE)) { // Networks can not be displayed by TG and are therefore not read  		
    		readTree(event.asLabeledIDEvent());
    	}
    	else {  // Possible additional element, which is not read
      	if (event.getType().getTopologyType().equals(EventTopologyType.START)) {  // SOLE and END events do not have to be processed here, because they have no further content.
      		JPhyloIOReadingUtils.reachElementEnd(reader);
    		}
      }
      event = reader.next();
    }
  }
	
	
	private void readTree(LabeledIDEvent treeEvent) throws XMLStreamException, IOException {
		if ((treeEvent.getLabel() != null) && !treeEvent.getLabel().isEmpty()) {
			currentTreeName = treeEvent.getLabel();
    }
		else {
			currentTreeName = treeEvent.getID();
		}
		
		possiblePaintStartIDs.clear();
		idToNodeMap.clear();
    Tree tree = new Tree();
		
    JPhyloIOEvent event = reader.next();
    while (!event.getType().getTopologyType().equals(EventTopologyType.END)) {
    	if (event.getType().getContentType().equals(EventContentType.NODE)) {
    		readNode(event.asNodeEvent());
    	}
    	else if (event.getType().getContentType().equals(EventContentType.EDGE) || event.getType().getContentType().equals(EventContentType.ROOT_EDGE)) {
    		readEdge(event.asEdgeEvent());
    	}
    	else if (event.getType().getContentType().equals(EventContentType.RESOURCE_META)) {
    		readInternalTreeMetadata(tree);
    	}
    	else {  // Possible additional element, which is not read
      	if (event.getType().getTopologyType().equals(EventTopologyType.START)) {  // SOLE and END events do not have to be processed here, because they have no further content.
      		JPhyloIOReadingUtils.reachElementEnd(reader);
    		}
      }
      event = reader.next();
    }
    
    if (possiblePaintStartIDs.size() > 1) {
    	throw new IOException("More than one root node was found for the tree \"" + currentTreeName + "\", but this can not be displayed in TreeGraph 2.");
    }
    
    //TODO Check if all legend anchors are valid. See XTGReader.readAnchor(). (This cannot be done during loading of legends, since nodes are not loaded at that time.)
    tree.setPaintStart(idToNodeMap.get(possiblePaintStartIDs.get(0)));
    tree.assignUniqueNames();
    if (!rootNodeIDs.isEmpty()) {
    	tree.getFormats().setShowRooted(true);
    }
    trees.add(tree);
    names.add(currentTreeName);    
  }
	
	
	private void readNode(NodeEvent nodeEvent) throws XMLStreamException, IOException {
		Node node = Node.newInstanceWithBranch();

		nodeNameAdapter.setText(node, nodeEvent.getLabel());
		idToNodeMap.put(nodeEvent.getID(), node);
		possiblePaintStartIDs.add(nodeEvent.getID());
		
		if (nodeEvent.isRootNode()) {
			rootNodeIDs.add(nodeEvent.getID());
		}
		
    readMetadata(node, new MetadataPath(true, false));
	}
	
	
	private void readEdge(EdgeEvent edgeEvent) throws XMLStreamException, IOException {
		Node targetNode = idToNodeMap.get(edgeEvent.getTargetID());
		Node sourceNode = idToNodeMap.get(edgeEvent.getSourceID());		
		
		if (targetNode.getParent() == null) {
			targetNode.setParent(sourceNode);
			branchLengthAdapter.setDecimal(targetNode, edgeEvent.getLength());
			
			if (sourceNode != null) {
				sourceNode.getChildren().add(targetNode);
				possiblePaintStartIDs.remove(edgeEvent.getTargetID());  // Nodes that were not referenced as target are possible paint starts
			}
		}
		else {  // Edge is network edge
			throw new IOException("Multiple parent nodes were specified for the node \"" + edgeEvent.getTargetID() + "\" in the tree \"" + currentTreeName 
					+ "\", but networks can not be displayed by TreeGraph 2.");
		}
		
		readMetadata(targetNode.getAfferentBranch(), new MetadataPath(false, false));
	}


	private void readMetadata(HiddenDataElement paintableElement, MetadataPath parentPath) throws IOException {
 		Map<QName, Integer> resourceMap = new HashMap<QName, Integer>();
		Map<QName, Integer> literalMap = new HashMap<QName, Integer>();
		
		JPhyloIOEvent event = reader.next();
		while (!event.getType().getTopologyType().equals(EventTopologyType.END)) {  // It is assumed that events are correctly nested   			
			if (event.getType().getContentType().equals(EventContentType.RESOURCE_META)) {
				ResourceMetadataEvent resourceMeta = event.asResourceMetadataEvent();
				MetadataPath childPath = storePredicateAndIndexInMap(resourceMap, parentPath, resourceMeta.getRel().getURI(), event);
				
				if (resourceMeta.getRel().getURI().equals(TreeDataAdapter.PREDICATE_INTERNAL_DATA)) {  //TODO Future versions could additionally check if this predicate is encountered on the root level.
					if (paintableElement instanceof Node) {
						readInternalNodeMetadata((Node)paintableElement);
					}
					else {
						readInternalBranchMetadata((Branch)paintableElement);
					}
				}
				else {
					MetadataNode metadataNode = paintableElement.getMetadataTree().searchAndCreateNodeByPath(childPath, true);
					((ResourceMetadataNode)metadataNode).setURI(resourceMeta.getHRef());
					
					readMetadata(paintableElement, childPath);
				}
			}
			else if (event.getType().getContentType().equals(EventContentType.LITERAL_META)) {
				LiteralMetadataEvent literalMeta = event.asLiteralMetadataEvent();
				
				if (literalMeta.getPredicate().getURI() == null) {
					throw new InternalError("Handling string keys currently not implemented.");
				}
				
				MetadataPath childPath = storePredicateAndIndexInMap(literalMap, parentPath, literalMeta.getPredicate().getURI(), event);
				
				MetadataNode metadataNode = paintableElement.getMetadataTree().searchAndCreateNodeByPath(childPath, true);
//				((LiteralMetadataNode)metadataNode).setDatatype(literalMeta.getOriginalType().getURI()); //TODO Use as soon as value of LiteralMetadataNode is no longer TextElementData but Object.
				
				((LiteralMetadataNode)metadataNode).setValue(readLiteralMetadataContent());
				TextElementData data = ((LiteralMetadataNode)metadataNode).getValue();
				if (data.isDecimal()) {
					((LiteralMetadataNode)metadataNode).setDatatype(W3CXSConstants.DATA_TYPE_DOUBLE);
				}
				else {
					((LiteralMetadataNode)metadataNode).setDatatype(W3CXSConstants.DATA_TYPE_STRING);
				}
				

			}
			else {
				JPhyloIOReadingUtils.reachElementEnd(reader);
			}
			event = reader.next();
		}
	}


	private TextElementData readLiteralMetadataContent() throws IOException {
		boolean isFirst = true;		
		StringBuffer content = new StringBuffer();
		TextElementData data = null;
		JPhyloIOEvent event = reader.next();
		
		while (!event.getType().getTopologyType().equals(EventTopologyType.END)) {			
			if (event.getType().getContentType().equals(EventContentType.LITERAL_META_CONTENT)) {
				LiteralMetadataContentEvent literalEvent = event.asLiteralMetadataContentEvent();
				
				if (isFirst) {
					isFirst = false;					
					
					if (literalEvent.getObjectValue() != null) {
						if (literalEvent.getObjectValue() instanceof Number) {
							data = new TextElementData(((Number)literalEvent.getObjectValue()).doubleValue());
						}
						else {
							if (literalEvent.hasStringValue()) {
								content.append(literalEvent.getStringValue());
							}
							else {
								content.append(literalEvent.getObjectValue().toString());    	  
							}
						}
				  }
				  else {
				  	content.append(literalEvent.getStringValue());
				  }	
				}
				else {
					content.append(literalEvent.getStringValue());  // Content can only be continued if it has only a string value      
				}				
				event = reader.next();
			}
			else {
				JPhyloIOReadingUtils.reachElementEnd(reader);
			}
		}		
		
	  if (data == null) {
	  	data = new TextElementData(content.toString());
	  }
		return data;
	}
	
		
	private MetadataPath storePredicateAndIndexInMap(Map<QName, Integer> map, MetadataPath parentPath, QName predicate, JPhyloIOEvent event) {
		if (!map.containsKey(predicate)) {
			map.put(predicate, 0);
		}
		else {
			map.put(predicate, map.get(predicate) + 1);
		}
		MetadataPath childPath = new MetadataPath(parentPath.isNode(), ((event.getType().getContentType().equals(EventContentType.LITERAL_META) ? true : false)));
		childPath.getElementList().addAll(parentPath.getElementList());
		
		childPath.getElementList().add(new MetadataPathElement(predicate, map.get(predicate)));
		return childPath;
	}
	
	
	private void readInternalNodeMetadata(Node node) throws IOException {
		JPhyloIOEvent event = reader.next();
		NodeFormats f = node.getFormats();
		TextElementDataStore textElementDataStore = new TextElementDataStore();
		DecimalAndLocaleStore decimalAndLocalStore = new DecimalAndLocaleStore();
		
		while (!event.getType().getTopologyType().equals(EventTopologyType.END)) {
			if (event.getType().getContentType().equals(EventContentType.RESOURCE_META)) {
				ResourceMetadataEvent resourceMeta = event.asResourceMetadataEvent();
				if (XTGConstants.PREDICATE_LEAF_MARGIN.equals(resourceMeta.getRel().getURI())) {
					readMargin(node.getFormats().getLeafMargin());
				}
			}	
			else if (event.getType().getContentType().equals(EventContentType.LITERAL_META)) {				
				LiteralMetadataEvent literalMeta = event.asLiteralMetadataEvent();
				if (!readTextFormats(literalMeta, node, textElementDataStore) && !readLineFormats(literalMeta, f) && !readDecimalAndLocaleValues(literalMeta, decimalAndLocalStore)) {
					if (XTGConstants.PREDICATE_NODE_UNIQUE_NAME.equals(literalMeta.getPredicate().getURI())) {
						node.setUniqueName(JPhyloIOReadingUtils.readLiteralMetadataContentAsString(reader));   //TODO Convert unique node name to lower case.
					}
					else if (XTGConstants.PREDICATE_EDGE_RADIUS.equals(literalMeta.getPredicate().getURI())) {
						node.getFormats().getCornerRadius().setInMillimeters(JPhyloIOReadingUtils.readLiteralMetadataContentAsObject(reader, Number.class).floatValue());
					}
					else {
						JPhyloIOReadingUtils.reachElementEnd(reader);  // Consume end events of possible empty unknown literal metadata.
					}
				}
			}
			else {
				JPhyloIOReadingUtils.reachElementEnd(reader);
			}
			event = reader.next();
		}
		
		decimalAndLocalStore.setInTextFormats(f);
		if (!node.getData().isEmpty()) {
			textElementDataStore.setText(node.getData().toString());  // Possible texts defined by a (illegal) predicate are overwritten by the node name from the node event here.
			textElementDataStore.setInTextElementData(node.getData());
		}
	}
	
	
	private void readInternalBranchMetadata(Branch branch) throws IOException {
		JPhyloIOEvent event = reader.next();
		Labels labels = branch.getLabels();
				
		while (!event.getType().getTopologyType().equals(EventTopologyType.END)) {
			if (event.getType().getContentType().equals(EventContentType.RESOURCE_META)) {
				ResourceMetadataEvent resourceMeta = event.asResourceMetadataEvent();
				if (XTGConstants.PREDICATE_ICON_LABEL.equals(resourceMeta.getRel().getURI())) {
					IconLabel l = new IconLabel(labels);
					readIconLabel(l);
					labels.add(l);
				}
				else if (XTGConstants.PREDICATE_TEXT_LABEL.equals(resourceMeta.getRel().getURI())) {
					TextLabel l = new TextLabel(labels);
					readTextLabel(l);
					labels.add(l);
				}
				else if (XTGConstants.PREDICATE_PIE_CHART_LABEL.equals(resourceMeta.getRel().getURI())) {
					PieChartLabel l = new PieChartLabel(labels);
					readPieChartLabel(l);
					labels.add(l);
				}
			}	
			else if (event.getType().getContentType().equals(EventContentType.LITERAL_META)) {
				LiteralMetadataEvent literalMeta = event.asLiteralMetadataEvent();
				BranchFormats f = branch.getFormats();
				if (!readLineFormats(literalMeta, f)) {
					if (XTGConstants.PREDICATE_BRANCH_CONSTANT_WIDTH.equals(literalMeta.getPredicate().getURI())) {
						f.setConstantWidth(JPhyloIOReadingUtils.readLiteralMetadataContentAsObject(reader, Boolean.class).booleanValue());
					}
					else if (XTGConstants.PREDICATE_BRANCH_MIN_LENGTH.equals(literalMeta.getPredicate().getURI())) {
						f.getMinLength().setInMillimeters(JPhyloIOReadingUtils.readLiteralMetadataContentAsObject(reader, Number.class).floatValue());
					}
					else if (XTGConstants.PREDICATE_BRANCH_MIN_SPACE_ABOVE.equals(literalMeta.getPredicate().getURI())) {
						f.getMinSpaceAbove().setInMillimeters(JPhyloIOReadingUtils.readLiteralMetadataContentAsObject(reader, Number.class).floatValue());
					}
					else if (XTGConstants.PREDICATE_BRANCH_MIN_SPACE_BELOW.equals(literalMeta.getPredicate().getURI())) {
						f.getMinSpaceBelow().setInMillimeters(JPhyloIOReadingUtils.readLiteralMetadataContentAsObject(reader, Number.class).floatValue());
					}
					else {
						JPhyloIOReadingUtils.reachElementEnd(reader);  // Consume end events of possible empty unknown literal metadata.
					}
				}
			}
			else {
				JPhyloIOReadingUtils.reachElementEnd(reader);
			}
			event = reader.next();
		}
	}
	
	
	private void readInternalTreeMetadata(Tree tree) throws IOException {
		JPhyloIOEvent event = reader.next();
		Legends legends = tree.getLegends();
		
		while (!event.getType().getTopologyType().equals(EventTopologyType.END)) {
			if (event.getType().getContentType().equals(EventContentType.RESOURCE_META)) {
				ResourceMetadataEvent resourceMeta = event.asResourceMetadataEvent();
				if (XTGConstants.PREDICATE_GLOBAL_FORMATS.equals(resourceMeta.getRel().getURI())) {
					readGlobalFormats(tree.getFormats());
				}
				else if (XTGConstants.PREDICATE_DOCUMENT_MARGIN.equals(resourceMeta.getRel().getURI())) {
					readMargin(tree.getFormats().getDocumentMargin());
				}
//				else if (XTGConstants.PREDICATE_NODE_BRANCH_DATA_ADAPTERS.equals(resourceMeta.getRel().getURI())) {					
//					readNodeBranchDataAdapters(leafAdapter, supportAdapter);
//				}
				else if (XTGConstants.PREDICATE_SCALE_BAR.equals(resourceMeta.getRel().getURI())) {
					readScaleBar(tree.getScaleBar());
				}
				else if (XTGConstants.PREDICATE_LEGEND.equals(resourceMeta.getRel().getURI())) {					
					readLegends(legends);
				}
			}
			else {
				JPhyloIOReadingUtils.reachElementEnd(reader);  // Consume end events of possible empty unknown literal metadata.
			}
			event = reader.next();
		}	
	}
	
	
	private void readGlobalFormats(GlobalFormats f) throws IOException  {
		JPhyloIOEvent event = reader.next();
		
		while (!event.getType().getTopologyType().equals(EventTopologyType.END)) {
			if (event.getType().getContentType().equals(EventContentType.LITERAL_META)) {
				LiteralMetadataEvent literalMeta = event.asLiteralMetadataEvent();
				
				if (XTGConstants.PREDICATE_GLOBAL_FORMATS_BG_COLOR.equals(literalMeta.getPredicate().getURI())) {
					if (XTGConstants.DATA_TYPE_COLOR.equals(literalMeta.getOriginalType().getURI())) {
						try {
							f.setBackgroundColor(Color.decode(JPhyloIOReadingUtils.readLiteralMetadataContentAsString(reader)));
						}
						catch (IllegalArgumentException e) {}
					}
				}
				else if (XTGConstants.PREDICATE_GLOBAL_FORMATS_BRANCH_LENGTH_SCALE.equals(literalMeta.getPredicate().getURI())) {
					f.getBranchLengthScale().setInMillimeters(JPhyloIOReadingUtils.readLiteralMetadataContentAsObject(reader, Number.class).floatValue());
				}
				else if (XTGConstants.PREDICATE_GLOBAL_FORMATS_SHOW_SCALE_BAR.equals(literalMeta.getPredicate().getURI())) {
					f.setShowScaleBar(JPhyloIOReadingUtils.readLiteralMetadataContentAsObject(reader, Boolean.class).booleanValue());
				}
				else if (XTGConstants.PREDICATE_GLOBAL_FORMATS_ALIGN_TO_SUBTREE.equals(literalMeta.getPredicate().getURI())) {
					f.setAlignLegendsToSubtree(JPhyloIOReadingUtils.readLiteralMetadataContentAsObject(reader, Boolean.class).booleanValue());
				}
				else if (XTGConstants.PREDICATE_GLOBAL_FORMATS_POSITION_LABELS_TO_LEFT.equals(literalMeta.getPredicate().getURI())) {
					f.setPositionLabelsToLeft(JPhyloIOReadingUtils.readLiteralMetadataContentAsObject(reader, Boolean.class).booleanValue());
				}
				else {
					JPhyloIOReadingUtils.reachElementEnd(reader);  // Consume end events of possible empty unknown literal metadata.
				}
			}
			else {
				JPhyloIOReadingUtils.reachElementEnd(reader);
			}
			event = reader.next();
		}
	}
	
	
	private void readScaleBar(ScaleBar scaleBar) throws IOException {
		JPhyloIOEvent event = reader.next();		
		ScaleBarFormats f = scaleBar.getFormats();
		
		TextElementDataStore textElementDataStore = new TextElementDataStore();
		DecimalAndLocaleStore decimalAndLocalStore = new DecimalAndLocaleStore();
		
		while (!event.getType().getTopologyType().equals(EventTopologyType.END)) {
			if (event.getType().getContentType().equals(EventContentType.LITERAL_META)) {
				LiteralMetadataEvent literalMeta = event.asLiteralMetadataEvent();
				
				if (!readTextFormats(literalMeta, scaleBar, textElementDataStore) && !readDecimalAndLocaleValues(literalMeta, decimalAndLocalStore) && !readLineFormats(literalMeta, f)) {
					if (XTGConstants.PREDICATE_WIDTH.equals(literalMeta.getPredicate().getURI())) {
						f.getWidth().assign(JPhyloIOReadingUtils.readLiteralMetadataContentAsObject(reader, ScaleValue.class));
					}
					else if (XTGConstants.PREDICATE_HEIGHT.equals(literalMeta.getPredicate().getURI())) {
						f.getHeight().setInMillimeters(JPhyloIOReadingUtils.readLiteralMetadataContentAsObject(reader, Number.class).floatValue());
					}
					else if (XTGConstants.PREDICATE_SCALE_BAR_ALIGN.equals(literalMeta.getPredicate().getURI())) {
						f.setAlignment(JPhyloIOReadingUtils.readLiteralMetadataContentAsObject(reader, ScaleBarAlignment.class));
					}
					else if (XTGConstants.PREDICATE_SCALE_BAR_TREE_DISTANCE.equals(literalMeta.getPredicate().getURI())) {
						f.getTreeDistance().setInMillimeters(JPhyloIOReadingUtils.readLiteralMetadataContentAsObject(reader, Number.class).floatValue());
					}
					else if (XTGConstants.PREDICATE_SCALE_BAR_SMALL_INTERVAL.equals(literalMeta.getPredicate().getURI())) {
						f.setSmallInterval(JPhyloIOReadingUtils.readLiteralMetadataContentAsObject(reader, Number.class).floatValue());
					}
					else if (XTGConstants.PREDICATE_SCALE_BAR_LONG_INTERVAL.equals(literalMeta.getPredicate().getURI())) {
						f.setLongInterval(JPhyloIOReadingUtils.readLiteralMetadataContentAsObject(reader, Number.class).intValue());
					}
					else if (XTGConstants.PREDICATE_SCALE_BAR_START_LEFT.equals(literalMeta.getPredicate().getURI())) {
						f.setStartLeft(JPhyloIOReadingUtils.readLiteralMetadataContentAsObject(reader, Boolean.class).booleanValue());
					}
					else if (XTGConstants.PREDICATE_SCALE_BAR_INCREASING.equals(literalMeta.getPredicate().getURI())) {
						f.setIncreasing(JPhyloIOReadingUtils.readLiteralMetadataContentAsObject(reader, Boolean.class).booleanValue());
					}
					else {
						JPhyloIOReadingUtils.reachElementEnd(reader);  // Consume end events of possible empty unknown literal metadata.
					}
				}
			}
			else {
				JPhyloIOReadingUtils.reachElementEnd(reader);
			}
			event = reader.next();
		}
		textElementDataStore.setInTextElementData(scaleBar.getData());
		decimalAndLocalStore.setInTextFormats(f);
	}
	
	
	private void readLegends(Legends legends) throws IOException {
		Legend legend = new Legend(legends);
		JPhyloIOEvent event = reader.next();
		TextElementDataStore textElementDataStore = new TextElementDataStore();
		DecimalAndLocaleStore decimalAndLocalStore = new DecimalAndLocaleStore();
		
		while (!event.getType().getTopologyType().equals(EventTopologyType.END)) {
			if (event.getType().getContentType().equals(EventContentType.LITERAL_META)) {
				LiteralMetadataEvent literalMeta = event.asLiteralMetadataEvent();
				LegendFormats f = legend.getFormats();
				
				if (!readTextFormats(literalMeta, legend, textElementDataStore) && !readDecimalAndLocaleValues(literalMeta, decimalAndLocalStore) && !readLineFormats(literalMeta, f)) {
					if (XTGConstants.PREDICATE_LEGEND_ANCHOR_0.equals(literalMeta.getPredicate().getURI())) {
						f.setAnchorName(0, JPhyloIOReadingUtils.readLiteralMetadataContentAsString(reader));  //TODO Check of anchor is not null and convert unique node name to lower case.
					}
					else if (XTGConstants.PREDICATE_LEGEND_ANCHOR_1.equals(literalMeta.getPredicate().getURI())) {
						f.setAnchorName(1, JPhyloIOReadingUtils.readLiteralMetadataContentAsString(reader));  //TODO Convert unique node name to lower case.
					}
					else if (XTGConstants.PREDICATE_LEGEND_LEGEND_POSITION.equals(literalMeta.getPredicate().getURI())) {
						f.setPosition(JPhyloIOReadingUtils.readLiteralMetadataContentAsObject(reader, Number.class).intValue());
					}
					else if (XTGConstants.PREDICATE_LEGEND_MIN_TREE_DISTANCE.equals(literalMeta.getPredicate().getURI())) {
						f.getMinTreeDistance().setInMillimeters(JPhyloIOReadingUtils.readLiteralMetadataContentAsObject(reader, Number.class).floatValue());
					}
					else if (XTGConstants.PREDICATE_LEGEND_LEGEND_SPACING.equals(literalMeta.getPredicate().getURI())) {
						f.getSpacing().setInMillimeters(JPhyloIOReadingUtils.readLiteralMetadataContentAsObject(reader, Number.class).floatValue());
					}
					else if (XTGConstants.PREDICATE_LEGEND_LEGEND_STYLE.equals(literalMeta.getPredicate().getURI())) {
						f.setLegendStyle(JPhyloIOReadingUtils.readLiteralMetadataContentAsObject(reader, LegendStyle.class));
					}
					else if (XTGConstants.PREDICATE_LEGEND_ORIENTATION.equals(literalMeta.getPredicate().getURI())) {
						f.setOrientation(JPhyloIOReadingUtils.readLiteralMetadataContentAsObject(reader, TextOrientation.class));
					}
					else if (XTGConstants.PREDICATE_EDGE_RADIUS.equals(literalMeta.getPredicate().getURI())) {
						f.getCornerRadius().setInMillimeters(JPhyloIOReadingUtils.readLiteralMetadataContentAsObject(reader, Number.class).floatValue());
					}
					else {
						JPhyloIOReadingUtils.reachElementEnd(reader);  // Consume end events of possible empty unknown literal metadata.
					}
				}
			}
			else if (event.getType().getContentType().equals(EventContentType.RESOURCE_META)) {
				readMargin(legend.getFormats().getMargin());
			}
			else {
				JPhyloIOReadingUtils.reachElementEnd(reader);
			}
			event = reader.next();
		}		
		textElementDataStore.setInTextElementData(legend.getData());
		legends.insert(legend);
	}
	
	
	private boolean readLabelFormats(LiteralMetadataEvent literalMeta, Label label) throws IOException  {
		if (XTGConstants.PREDICATE_LABEL_LINE_NO.equals(literalMeta.getPredicate().getURI())) {
			label.getFormats().setLineNumber(JPhyloIOReadingUtils.readLiteralMetadataContentAsObject(reader, Number.class).intValue());
		}
		else if (XTGConstants.PREDICATE_LABEL_ABOVE.equals(literalMeta.getPredicate().getURI())) {
			label.getFormats().setAbove(JPhyloIOReadingUtils.readLiteralMetadataContentAsObject(reader, Boolean.class).booleanValue());
		}
		else if (XTGConstants.PREDICATE_LABEL_LINE_POS.equals(literalMeta.getPredicate().getURI())) {
			label.getFormats().setLinePosition(JPhyloIOReadingUtils.readLiteralMetadataContentAsObject(reader, Number.class).doubleValue());
		}
		else if (XTGConstants.PREDICATE_COLUMN_ID.equals(literalMeta.getPredicate().getURI())) {
			label.setID(JPhyloIOReadingUtils.readLiteralMetadataContentAsString(reader));
		}
		else {
			return false;
		}
		return true;
	}
	
	
	private boolean readGraphicalLabelDimensions(LiteralMetadataEvent literalMeta, GraphicalLabelFormats f) throws IOException  {
		if (XTGConstants.PREDICATE_WIDTH.equals(literalMeta.getPredicate().getURI())) {
			f.getWidth().setInMillimeters(JPhyloIOReadingUtils.readLiteralMetadataContentAsObject(reader, Number.class).floatValue());
		}
		else if (XTGConstants.PREDICATE_HEIGHT.equals(literalMeta.getPredicate().getURI())) {
			f.getHeight().setInMillimeters(JPhyloIOReadingUtils.readLiteralMetadataContentAsObject(reader, Number.class).floatValue());
		}
		else {
			return false;
		}
		return true;
	}

		
	private void readTextLabel(TextLabel label) throws IOException {
		JPhyloIOEvent event = reader.next();
		TextElementDataStore textElementDataStore = new TextElementDataStore();
		
		DecimalAndLocaleStore decimalAndLocalStore = new DecimalAndLocaleStore();
		
		while (!event.getType().getTopologyType().equals(EventTopologyType.END)) {
			if (event.getType().getContentType().equals(EventContentType.LITERAL_META)) {
				LiteralMetadataEvent literalMeta = event.asLiteralMetadataEvent();
				
				if (!readTextFormats(literalMeta, label, textElementDataStore) && !readLabelFormats(literalMeta, label) && !readDecimalAndLocaleValues(literalMeta, decimalAndLocalStore)) {
					JPhyloIOReadingUtils.reachElementEnd(reader);  // Consume end events of possible empty unknown literal metadata.					
				}
			}
			else if (event.getType().getContentType().equals(EventContentType.RESOURCE_META)) {
				readMargin(label.getFormats().getMargin());
			}
			else {
				JPhyloIOReadingUtils.reachElementEnd(reader);
			}
			event = reader.next();
		}
		decimalAndLocalStore.setInTextFormats(label.getFormats());
		textElementDataStore.setInTextElementData(label.getData());
	}
	
	
	private void readIconLabel(IconLabel label) throws IOException {
		JPhyloIOEvent event = reader.next();
		
		while (!event.getType().getTopologyType().equals(EventTopologyType.END)) {
			if (event.getType().getContentType().equals(EventContentType.LITERAL_META)) {
				LiteralMetadataEvent literalMeta = event.asLiteralMetadataEvent();
				IconLabelFormats f = label.getFormats();
				
				if (!readGraphicalLabelDimensions(literalMeta, f) && !readLabelFormats(literalMeta, label) && !readLineFormats(literalMeta, f)) {
					
					if (XTGConstants.PREDICATE_ICON_LABEL_ICON.equals(literalMeta.getPredicate().getURI())) {
						f.setIcon(JPhyloIOReadingUtils.readLiteralMetadataContentAsString(reader));
					}
					else if (XTGConstants.PREDICATE_ICON_LABEL_ICON_FILLED.equals(literalMeta.getPredicate().getURI())) {
						f.setIconFilled(JPhyloIOReadingUtils.readLiteralMetadataContentAsObject(reader, Boolean.class).booleanValue());
					}
					else {
						JPhyloIOReadingUtils.reachElementEnd(reader);  // Consume end events of possible empty unknown literal metadata.
					}
				}
			}
			else if (event.getType().getContentType().equals(EventContentType.RESOURCE_META)) {
				readMargin(label.getFormats().getMargin());
			}
			else {
				JPhyloIOReadingUtils.reachElementEnd(reader);
			}
			event = reader.next();
		}
	}
	
	
	private void readPieChartLabel(PieChartLabel label) throws IOException {
		JPhyloIOEvent event = reader.next();
		
		TextElementDataStore textElementDataStore = new TextElementDataStore();		
		DecimalAndLocaleStore decimalAndLocalStore = new DecimalAndLocaleStore();
		PieChartLabelFormats f = label.getFormats();
		
		while (!event.getType().getTopologyType().equals(EventTopologyType.END)) {
			if (event.getType().getContentType().equals(EventContentType.LITERAL_META)) {
				LiteralMetadataEvent literalMeta = event.asLiteralMetadataEvent();				
				
				if (!readGraphicalLabelDimensions(literalMeta, f) && !readLabelFormats(literalMeta, label) && !readLineFormats(literalMeta, f) 
						&& !readDecimalAndLocaleValues(literalMeta, decimalAndLocalStore) && !readTextFormats(literalMeta, label, textElementDataStore)) {
					
					if (XTGConstants.PREDICATE_PIE_CHART_LABEL_INTERNAL_LINES.equals(literalMeta.getPredicate().getURI())) {
						f.setShowInternalLines(JPhyloIOReadingUtils.readLiteralMetadataContentAsObject(reader, Boolean.class).booleanValue());
					}
					else if (XTGConstants.PREDICATE_PIE_CHART_LABEL_NULL_LINES.equals(literalMeta.getPredicate().getURI())) {
						f.setShowLinesForZero(JPhyloIOReadingUtils.readLiteralMetadataContentAsObject(reader, Boolean.class).booleanValue());
					}
					else if (XTGConstants.PREDICATE_PIE_CHART_LABEL_CAPTION_TYPE.equals(literalMeta.getPredicate().getURI())) {
						f.setCaptionsContentType(JPhyloIOReadingUtils.readLiteralMetadataContentAsObject(reader, PieChartLabelCaptionContentType.class));
					}
					else if (XTGConstants.PREDICATE_PIE_CHART_LABEL_CAPTION_LINK_TYPE.equals(literalMeta.getPredicate().getURI())) {
						f.setCaptionsLinkType(JPhyloIOReadingUtils.readLiteralMetadataContentAsObject(reader, PieChartLabelCaptionLinkType.class));
					}
					else if (XTGConstants.PREDICATE_PIE_CHART_LABEL_SHOW_TITLE.equals(literalMeta.getPredicate().getURI())) {
						f.setShowTitle(JPhyloIOReadingUtils.readLiteralMetadataContentAsObject(reader, Boolean.class).booleanValue());
					}
					else {
						JPhyloIOReadingUtils.reachElementEnd(reader);  // Consume end events of possible empty unknown literal metadata.					
					}
				}
			}			
			else if (event.getType().getContentType().equals(EventContentType.RESOURCE_META)) {
				ResourceMetadataEvent resourceMeta = event.asResourceMetadataEvent();
				if (XTGConstants.PREDICATE_DATA_IDS.equals(resourceMeta.getRel().getURI())) {
					readDataIDs(label, textElementDataStore);
				}
				else if (XTGConstants.PREDICATE_LABEL_MARGIN.equals(resourceMeta.getRel().getURI())) {
					readMargin(label.getFormats().getMargin());
				}
			}
			else {
				JPhyloIOReadingUtils.reachElementEnd(reader);
			}
			event = reader.next();
		}
		decimalAndLocalStore.setInTextFormats(f);
		textElementDataStore.setInTextElementData(label.getData());
	}
	
	
	private void readDataIDs(PieChartLabel label, TextElementDataStore textElementDataStore) throws IOException  {
		JPhyloIOEvent event = reader.next();
		while (!event.getType().getTopologyType().equals(EventTopologyType.END)) {
			if (event.getType().getContentType().equals(EventContentType.RESOURCE_META)) {
				readDataID(label);
			}
			else if (event.getType().getContentType().equals(EventContentType.LITERAL_META)) {
				LiteralMetadataEvent literalMeta = event.asLiteralMetadataEvent();
				if (!readTextFormats(literalMeta, label, textElementDataStore)) {
					JPhyloIOReadingUtils.reachElementEnd(reader);  // Consume end events of possible empty unknown literal metadata.		
				}				
			}
			else {
				JPhyloIOReadingUtils.reachElementEnd(reader);	
			}
			event = reader.next();
		}
	}
	
	
	private void readDataID(PieChartLabel label) throws IOException   {
		JPhyloIOEvent event = reader.next();
		PieChartLabelFormats f = label.getFormats();
		SectionData data = new SectionData("", "");
		
		while (!event.getType().getTopologyType().equals(EventTopologyType.END)) {
			if (event.getType().getContentType().equals(EventContentType.LITERAL_META)) {
				LiteralMetadataEvent literalMeta = event.asLiteralMetadataEvent();
				
				if (XTGConstants.PREDICATE_PIE_COLOR.equals(literalMeta.getPredicate().getURI())) {
					if (XTGConstants.DATA_TYPE_COLOR.equals(literalMeta.getOriginalType().getURI())) {
						try {
							f.addPieColor(Color.decode(JPhyloIOReadingUtils.readLiteralMetadataContentAsString(reader)));
						}
						catch (IllegalArgumentException e) {}
					}			
				}
				else if (XTGConstants.PREDICATE_DATA_ID_VALUE.equals(literalMeta.getPredicate().getURI())) {
					data.setValueColumnID(JPhyloIOReadingUtils.readLiteralMetadataContentAsString(reader));
				}
				else if (XTGConstants.PREDICATE_PIE_CAPTION.equals(literalMeta.getPredicate().getURI())) {
					data.setCaption(JPhyloIOReadingUtils.readLiteralMetadataContentAsString(reader));
				}
				else {
					JPhyloIOReadingUtils.reachElementEnd(reader);  // Consume end events of possible empty unknown literal metadata.		
				}
			}
			else {
				JPhyloIOReadingUtils.reachElementEnd(reader);
			}
			event = reader.next();
		}
		label.getSectionDataList().add(data);  //TODO Check if an ID was read.
	}
		
	
	private boolean readDecimalAndLocaleValues(LiteralMetadataEvent literalMeta, DecimalAndLocaleStore decimalAndLocalStore) throws IOException  {		
		if (XTGConstants.PREDICATE_DECIMAL_FORMAT.equals(literalMeta.getPredicate().getURI())) {
			decimalAndLocalStore.setFormatString(JPhyloIOReadingUtils.readLiteralMetadataContentAsString(reader));
		}
		else if (XTGConstants.PREDICATE_LOCALE_LANG.equals(literalMeta.getPredicate().getURI())) {
			decimalAndLocalStore.setLang(JPhyloIOReadingUtils.readLiteralMetadataContentAsString(reader));
		}
		else if (XTGConstants.PREDICATE_LOCALE_COUNTRY.equals(literalMeta.getPredicate().getURI())) {
			decimalAndLocalStore.setCountry(JPhyloIOReadingUtils.readLiteralMetadataContentAsString(reader));
		}
		else if (XTGConstants.PREDICATE_LOCALE_VARIANT.equals(literalMeta.getPredicate().getURI())) {
			decimalAndLocalStore.setVariant(JPhyloIOReadingUtils.readLiteralMetadataContentAsString(reader));
		}
		else {
			return false;
		}
		return true;
	}
		
	
	private boolean readTextFormats(LiteralMetadataEvent literalMeta, TextElement element, TextElementDataStore textElementDataStore)  throws IOException {
		TextFormats f = element.getFormats();
		
		
		if (XTGConstants.PREDICATE_TEXT_HEIGHT.equals(literalMeta.getPredicate().getURI())) {
			f.getTextHeight().setInMillimeters(JPhyloIOReadingUtils.readLiteralMetadataContentAsObject(reader, Number.class).floatValue());
		}
		else if (XTGConstants.PREDICATE_TEXT_COLOR.equals(literalMeta.getPredicate().getURI())) {
			Object objectValue = null;
			if (XTGConstants.DATA_TYPE_COLOR.equals(literalMeta.getOriginalType().getURI())) {
				try {
					objectValue = Color.decode(JPhyloIOReadingUtils.readLiteralMetadataContentAsString(reader));
					f.setTextColor((Color)objectValue);
				}
				catch (IllegalArgumentException e) {}
			}
		}
		else if (XTGConstants.PREDICATE_TEXT_STYLE.equals(literalMeta.getPredicate().getURI())) {
			f.setTextStyle(JPhyloIOReadingUtils.readLiteralMetadataContentAsObject(reader, Number.class).intValue());
		}
		else if (XTGConstants.PREDICATE_FONT_FAMILY.equals(literalMeta.getPredicate().getURI())) {
			f.setFontName(JPhyloIOReadingUtils.readLiteralMetadataContentAsString(reader));
		}		
		else if (XTGConstants.PREDICATE_TEXT.equals(literalMeta.getPredicate().getURI())) {
			textElementDataStore.setText(JPhyloIOReadingUtils.readLiteralMetadataContentAsString(reader));
		}
		else if (XTGConstants.PREDICATE_IS_DECIMAL.equals(literalMeta.getPredicate().getURI())) {
			textElementDataStore.setDecimal(JPhyloIOReadingUtils.readLiteralMetadataContentAsObject(reader, Boolean.class).booleanValue());
		}
		else {
			return false;
		}
		return true;
	}
	
	
	private boolean readLineFormats(LiteralMetadataEvent literalMeta, LineFormats f)  throws IOException {
		if (XTGConstants.PREDICATE_LINE_WIDTH.equals(literalMeta.getPredicate().getURI())) {
			f.getLineWidth().setInMillimeters(JPhyloIOReadingUtils.readLiteralMetadataContentAsObject(reader, Number.class).floatValue());
		}
		else if (XTGConstants.PREDICATE_LINE_COLOR.equals(literalMeta.getPredicate().getURI())) {
			Object objectValue = null;
			if (XTGConstants.DATA_TYPE_COLOR.equals(literalMeta.getOriginalType().getURI())) {
				try {
					objectValue = Color.decode(JPhyloIOReadingUtils.readLiteralMetadataContentAsString(reader));
					f.setLineColor((Color)objectValue);
				}
				catch (IllegalArgumentException e) {}
			}
		}
		else {
			return false;
		}
		return true;
	}
	
		
	private boolean readMarginValue(Margin margin, LiteralMetadataEvent literalMeta) throws IOException {		
		if (info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_MARGIN_LEFT.equals(literalMeta.getPredicate().getURI())) {
			margin.getLeft().setInMillimeters(JPhyloIOReadingUtils.readLiteralMetadataContentAsObject(reader, Number.class).floatValue());
		}
		else if (info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_MARGIN_TOP.equals(literalMeta.getPredicate().getURI())) {
			margin.getTop().setInMillimeters(JPhyloIOReadingUtils.readLiteralMetadataContentAsObject(reader, Number.class).floatValue());
		}
		else if (info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_MARGIN_RIGHT.equals(literalMeta.getPredicate().getURI())) {
			margin.getRight().setInMillimeters(JPhyloIOReadingUtils.readLiteralMetadataContentAsObject(reader, Number.class).floatValue());
		}
		else if (info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_MARGIN_BOTTOM.equals(literalMeta.getPredicate().getURI())) {
			margin.getBottom().setInMillimeters(JPhyloIOReadingUtils.readLiteralMetadataContentAsObject(reader, Number.class).floatValue());
		}		
		else {
			return false;
		}
		return true;
	}
		
	
	private void readMargin(Margin margin) throws IOException {
		JPhyloIOEvent event = reader.next();
		while (!event.getType().getTopologyType().equals(EventTopologyType.END)) {
			if (event.getType().getContentType().equals(EventContentType.LITERAL_META)) {
				LiteralMetadataEvent literalMeta = event.asLiteralMetadataEvent();				
				if (!readMarginValue(margin, literalMeta)) {
					JPhyloIOReadingUtils.reachElementEnd(reader);  // Consume end events of possible empty unknown literal metadata.					
				}
			}
			else {
				JPhyloIOReadingUtils.reachElementEnd(reader);
			}
			event = reader.next();
		}
	}
	
	
	@Override
	public DocumentIterator createIterator(BufferedInputStream stream) throws Exception {
		return new SingleDocumentIterator(read(stream));
	}	
	
	
//private void readNodeBranchDataAdapters(NodeBranchDataAdapter leafAdapter, NodeBranchDataAdapter supportAdapter) throws IOException {
//JPhyloIOEvent event = reader.next();		
//
//while (!event.getType().getTopologyType().equals(EventTopologyType.END)) {
//	if (event.getType().getContentType().equals(EventContentType.RESOURCE_META)) {
//		
//	}
//	else {
//		JPhyloIOReadingUtils.reachElementEnd(reader);	
//	}
//	event = reader.next();
//}
//}
//
//
//private void readNodeBranchDataAdaptersAdapter() throws IOException {
//JPhyloIOEvent event = reader.next();
//
//while (!event.getType().getTopologyType().equals(EventTopologyType.END)) {
//	if (event.getType().getContentType().equals(EventContentType.LITERAL_META)) {
//		LiteralMetadataEvent literalMeta = event.asLiteralMetadataEvent();
//		
//		if (XTGConstants.PREDICATE_NODE_BRANCH_DATA_ADAPTERS_ADAPTER_NAME.equals(literalMeta.getPredicate().getURI())) {
//		
//		}
//		else if (XTGConstants.PREDICATE_NODE_BRANCH_DATA_ADAPTERS_ADAPTER_PURPOSE.equals(literalMeta.getPredicate().getURI())) {
//
//		}
//		else {
//			JPhyloIOReadingUtils.reachElementEnd(reader);  // Consume end events of possible empty unknown literal metadata.		
//		}
//	}
//	else {
//		JPhyloIOReadingUtils.reachElementEnd(reader);
//	}
//	event = reader.next();
//}
//}
}
