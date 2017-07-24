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
import info.bioinfweb.treegraph.document.GraphicalLabel;
import info.bioinfweb.treegraph.document.HiddenDataElement;
import info.bioinfweb.treegraph.document.IconLabel;
import info.bioinfweb.treegraph.document.Label;
import info.bioinfweb.treegraph.document.Labels;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.PieChartLabel;
import info.bioinfweb.treegraph.document.TextElementData;
import info.bioinfweb.treegraph.document.TextLabel;
import info.bioinfweb.treegraph.document.Tree;
import info.bioinfweb.treegraph.document.format.BranchFormats;
import info.bioinfweb.treegraph.document.format.GraphicalLabelFormats;
import info.bioinfweb.treegraph.document.format.IconLabelFormats;
import info.bioinfweb.treegraph.document.format.LineFormats;
import info.bioinfweb.treegraph.document.format.Margin;
import info.bioinfweb.treegraph.document.format.NodeFormats;
import info.bioinfweb.treegraph.document.format.PieChartLabelFormats;
import info.bioinfweb.treegraph.document.format.TextFormats;
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
	private JPhyloIOEventReader reader;
	private String currentTreeName;
	private List<Tree> trees = new ArrayList<Tree>();
	private List<String> names = new ArrayList<String>();
	private Map<String, Node> idToNodeMap = new HashMap<String, Node>();
	private List<String> possiblePaintStartIDs = new ArrayList<String>();
	private List<String> rootNodeIDs = new ArrayList<String>(); //TODO Mark all root nodes with icon label or something similar
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
		          	Tree tree = trees.get(parameterMap.getTreeSelector().select(names.toArray(new String[names.size()]), trees));
		          	document.setTree(tree);
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
		
    JPhyloIOEvent event = reader.next();
    while (!event.getType().getTopologyType().equals(EventTopologyType.END)) {    	
    	if (event.getType().getContentType().equals(EventContentType.NODE)) {
    		readNode(event.asNodeEvent());
    	}
    	else if (event.getType().getContentType().equals(EventContentType.EDGE) || event.getType().getContentType().equals(EventContentType.ROOT_EDGE)) {
    		readEdge(event.asEdgeEvent());
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
    
    Tree tree = new Tree();
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
				
				if (resourceMeta.getRel().getURI().equals(TreeDataAdapter.PREDICATE_INTERNAL_DATA)) {
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
				((LiteralMetadataNode)metadataNode).setValue(readLiteralMetadataContent());
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
							content.append(literalEvent.getObjectValue().toString());    	  
						}
				  }
				  else {
				  	content.append(literalEvent.getStringValue());
				  }	
				}
				else {
					content.append(event.asLiteralMetadataContentEvent().getStringValue());  // Content can only be continued if it has only a string value      
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
		while (!event.getType().getTopologyType().equals(EventTopologyType.END)) {
			if (event.getType().getContentType().equals(EventContentType.RESOURCE_META)) {
				ResourceMetadataEvent resourceMeta = event.asResourceMetadataEvent();
				if (XTGConstants.PREDICATE_LEAF_MARGIN.equals(resourceMeta.getRel().getURI())) {
					event = reader.next();
					readMargin(node.getFormats().getLeafMargin(), event);
				}
				else if (XTGConstants.PREDICATE_TEXT_LABEL.equals(resourceMeta.getRel().getURI())) {
					event = reader.next();
					readTextFormats(event.asLiteralMetadataEvent(), node.getFormats());
//					readDecimalAndLocale(literalMeta, node);
					readLineFormats(event.asLiteralMetadataEvent(), node.getFormats());
				}
			}	
			else if (event.getType().getContentType().equals(EventContentType.LITERAL_META)) {
				LiteralMetadataEvent literalMeta = event.asLiteralMetadataEvent();
				NodeFormats f = node.getFormats();
				if (!readTextFormats(literalMeta, f) && !readLineFormats(literalMeta, f)) {
					if (literalMeta.getPredicate().equals(XTGConstants.PREDICATE_TEXT)) { //Predicate is used as both Resource and LiteralMetadata. Should a new QName be defined?
						node.getData().setText(JPhyloIOReadingUtils.readLiteralMetadataContentAsString(reader));
					}
					else if (literalMeta.getPredicate().equals(XTGConstants.PREDICATE_NODE_ATTR_UNIQUE_NAME)) {
						node.setUniqueName(JPhyloIOReadingUtils.readLiteralMetadataContentAsString(reader));
					}
					else if (literalMeta.getPredicate().equals(XTGConstants.PREDICATE_NODE_ATTR_EDGE_RADIUS)) {
						node.getFormats().getCornerRadius().setInMillimeters(JPhyloIOReadingUtils.readLiteralMetadataContentAsObject(reader, Number.class).floatValue());
					}
					else if (literalMeta.getPredicate().equals(XTGConstants.PREDICATE_IS_DECIMAL)) {
						
					}
				}
			}
			else {
				JPhyloIOReadingUtils.reachElementEnd(reader);
			}
			event = reader.next();
		}
	}
	
	
	private void readInternalBranchMetadata(Branch branch) throws IOException {
		JPhyloIOEvent event = reader.next();
		Labels labels = branch.getLabels();
		boolean above = true;			
		
		for (int lineNo = 0; lineNo < labels.lineCount(above); lineNo++) {
			for (int lineIndex = 0; lineIndex < labels.labelCount(above, lineNo); lineIndex++) {
				Label label = labels.get(above, lineNo, lineIndex);
				
				while (!event.getType().getTopologyType().equals(EventTopologyType.END)) {
					if (event.getType().getContentType().equals(EventContentType.RESOURCE_META)) {
						ResourceMetadataEvent resourceMeta = event.asResourceMetadataEvent();
						if (XTGConstants.PREDICATE_ICON_LABEL.equals(resourceMeta.getRel().getURI())) {
							event = reader.next();
							readIconLabels(event.asLiteralMetadataEvent(), ((IconLabel)label).getFormats());
							readLineFormats(event.asLiteralMetadataEvent(), branch.getLineFormats());
							readLabelAttributes(event.asLiteralMetadataEvent(), label);
							readLabelDimensions(event.asLiteralMetadataEvent(), ((GraphicalLabel)label).getFormats());
							
						}
						else if (XTGConstants.PREDICATE_TEXT_LABEL.equals(resourceMeta.getRel().getURI())) {
							event = reader.next();
							readTextFormats(event.asLiteralMetadataEvent(), ((TextLabel)label).getFormats()); //TODO Get correct formats.
//							readDecimalAndLocale(literalMeta, node);
							readLineFormats(event.asLiteralMetadataEvent(), branch.getLineFormats());

						}
						else if (XTGConstants.PREDICATE_PIE_CHART_LABEL.equals(resourceMeta.getRel().getURI())) {
							event = reader.next();
							readLineFormats(event.asLiteralMetadataEvent(), ((PieChartLabel)label).getFormats());
							readPieChartLabels(event.asLiteralMetadataEvent(), ((PieChartLabel)label).getFormats());
//							readLabelAttributes(literalMeta, f);
//							readLabelDimensions(literalMeta, f);
							
						}
						else if (XTGConstants.PREDICATE_DATA_IDS.equals(resourceMeta.getRel().getURI())) {
							event = reader.next();
						}
						else if (XTGConstants.PREDICATE_DATA_ID.equals(resourceMeta.getRel().getURI())) {
							event = reader.next();							
						}
						else if (XTGConstants.PREDICATE_LABEL_MARGIN.equals(resourceMeta.getRel().getURI())) {
							readMargin(label.getFormats().getMargin(), event);
						}
					}	
					else if (event.getType().getContentType().equals(EventContentType.LITERAL_META)) {
						LiteralMetadataEvent literalMeta = event.asLiteralMetadataEvent();
						BranchFormats f = branch.getFormats();
						if (!readTextFormats(literalMeta, ((TextLabel)label).getFormats()) && !readLineFormats(literalMeta, f)) {
							if (literalMeta.getPredicate().equals(XTGConstants.PREDICATE_BRANCH_ATTR_CONSTANT_WIDTH)) {
								f.setConstantWidth(JPhyloIOReadingUtils.readLiteralMetadataContentAsObject(reader, Boolean.class).booleanValue());
							}
							else if (literalMeta.getPredicate().equals(XTGConstants.PREDICATE_BRANCH_ATTR_MIN_LENGTH)) {
								f.getMinLength().setInMillimeters(JPhyloIOReadingUtils.readLiteralMetadataContentAsObject(reader, Number.class).floatValue());
							}
							else if (literalMeta.getPredicate().equals(XTGConstants.PREDICATE_BRANCH_ATTR_MIN_SPACE_ABOVE)) {
								f.getMinSpaceAbove().setInMillimeters(JPhyloIOReadingUtils.readLiteralMetadataContentAsObject(reader, Number.class).floatValue());
							}
							else if (literalMeta.getPredicate().equals(XTGConstants.PREDICATE_BRANCH_ATTR_MIN_SPACE_BELOW)) {
								f.getMinSpaceBelow().setInMillimeters(JPhyloIOReadingUtils.readLiteralMetadataContentAsObject(reader, Number.class).floatValue());
							}
						}
					}
					else {
						JPhyloIOReadingUtils.reachElementEnd(reader);
					}
					event = reader.next();
				}
			}
		}		
	}
	
	
	private boolean readIconLabels(LiteralMetadataEvent literalMeta, IconLabelFormats f) throws IOException  {
		if (XTGConstants.PREDICATE_ICON_LABEL_ATTR_ICON.equals(literalMeta.getPredicate().getURI())) {
			f.setIcon(JPhyloIOReadingUtils.readLiteralMetadataContentAsString(reader));
		}
		else if (XTGConstants.PREDICATE_ICON_LABEL_ATTR_ICON_FILLED.equals(literalMeta.getPredicate().getURI())) {
			f.setIconFilled(JPhyloIOReadingUtils.readLiteralMetadataContentAsObject(reader, Boolean.class).booleanValue());
		}
		else {
			return false;
		}
		return true;
	}
	
	
	private boolean readPieChartLabels(LiteralMetadataEvent literalMeta, PieChartLabelFormats f) throws IOException  {
		if (XTGConstants.PREDICATE_PIE_CHART_LABEL_ATTR_INTERNAL_LINES.equals(literalMeta.getPredicate().getURI())) {
			f.setShowInternalLines(JPhyloIOReadingUtils.readLiteralMetadataContentAsObject(reader, Boolean.class).booleanValue());
		}
		else if (XTGConstants.PREDICATE_PIE_CHART_LABEL_ATTR_NULL_LINES.equals(literalMeta.getPredicate().getURI())) {
			f.setShowLinesForZero(JPhyloIOReadingUtils.readLiteralMetadataContentAsObject(reader, Boolean.class).booleanValue());
		}
		else {
			return false;
		}
		return true;
	}
	
	
	private boolean readDecimalAndLocale(LiteralMetadataEvent literalMeta, HiddenDataElement paintableElement) throws IOException  { //TODO Finish writing
		Locale locale;		
		if (XTGConstants.PREDICATE_DECIMAL_FORMAT.equals(literalMeta.getPredicate().getURI())) {
			DecimalFormat decimalFormat = new DecimalFormat(JPhyloIOReadingUtils.readLiteralMetadataContentAsString(reader));
			
		}
		else if (XTGConstants.PREDICATE_LOCALE_LANG.equals(literalMeta.getPredicate().getURI())) {
			
		}
		else if (XTGConstants.PREDICATE_LOCALE_COUNTRY.equals(literalMeta.getPredicate().getURI())) {

		}
		else if (XTGConstants.PREDICATE_LOCALE_VARIANT.equals(literalMeta.getPredicate().getURI())) {

		}
		else {
			return false;
		}
		return true;
	}
	
	
	private boolean readTextFormats(LiteralMetadataEvent literalMeta, TextFormats f)  throws IOException { 
		if (XTGConstants.PREDICATE_TEXT_HEIGHT.equals(literalMeta.getPredicate().getURI())) {
			f.getTextHeight().setInMillimeters(JPhyloIOReadingUtils.readLiteralMetadataContentAsObject(reader, Number.class).floatValue());  //TODO NPE possible
		}
//		else if (XTGConstants.PREDICATE_TEXT_COLOR.equals(literalMeta.getPredicate().getURI())) {
//			f.setTextColor(JPhyloIOReadingUtils.readLiteralMetadataContentAsObject(reader, Color.class));
//		}
		else if (XTGConstants.PREDICATE_TEXT_STYLE.equals(literalMeta.getPredicate().getURI())) {
			f.setTextStyle(JPhyloIOReadingUtils.readLiteralMetadataContentAsObject(reader, Number.class).intValue());
		}
		else if (XTGConstants.PREDICATE_FONT_FAMILY.equals(literalMeta.getPredicate().getURI())) {
			f.setFontName(JPhyloIOReadingUtils.readLiteralMetadataContentAsString(reader));
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
//		else if (XTGConstants.PREDICATE_LINE_COLOR.equals(literalMeta.getPredicate().getURI())) {
//			f.setLineColor(JPhyloIOReadingUtils.readLiteralMetadataContentAsObject(reader, Color.class));
//		}
		else {
			return false;
		}
		return true;
	}
	
	
	public void readMargin(Margin margin, JPhyloIOEvent event) throws IOException {
		LiteralMetadataEvent literalMeta = event.asLiteralMetadataEvent();
		
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
	}
	
	
	public boolean readLabelAttributes(LiteralMetadataEvent literalMeta, Label f) throws IOException  {
		if (XTGConstants.PREDICATE_LABEL_ATTR_LINE_NO.equals(literalMeta.getPredicate().getURI())) {
			f.getFormats().setLineNumber(JPhyloIOReadingUtils.readLiteralMetadataContentAsObject(reader, Integer.class).intValue());
		}
		else if (XTGConstants.PREDICATE_LABEL_ATTR_ABOVE.equals(literalMeta.getPredicate().getURI())) {
			f.getFormats().setAbove(JPhyloIOReadingUtils.readLiteralMetadataContentAsObject(reader, Boolean.class).booleanValue());
		}
		else if (XTGConstants.PREDICATE_LABEL_ATTR_LINE_POS.equals(literalMeta.getPredicate().getURI())) {
			f.getFormats().setLinePosition(JPhyloIOReadingUtils.readLiteralMetadataContentAsObject(reader, Double.class).doubleValue());
		}
		else if (XTGConstants.PREDICATE_COLUMN_ID.equals(literalMeta.getPredicate().getURI())) {
			f.setID(JPhyloIOReadingUtils.readLiteralMetadataContentAsString(reader));
		}
		else {
			return false;
		}
		return true;
	}
	
	
	public boolean readLabelDimensions(LiteralMetadataEvent literalMeta, GraphicalLabelFormats f) throws IOException  {
		if (XTGConstants.PREDICATE_ICON_LABEL_ATTR_WIDTH.equals(literalMeta.getPredicate().getURI()) || XTGConstants.PREDICATE_PIE_CHART_LABEL_ATTR_WIDTH.equals(literalMeta.getPredicate().getURI())) {
			f.getWidth().setInMillimeters(JPhyloIOReadingUtils.readLiteralMetadataContentAsObject(reader, Float.class).floatValue());
		}
		else if (XTGConstants.PREDICATE_ICON_LABEL_ATTR_HEIGHT.equals(literalMeta.getPredicate().getURI()) || XTGConstants.PREDICATE_PIE_CHART_LABEL_ATTR_HEIGHT.equals(literalMeta.getPredicate().getURI())) {
			f.getHeight().setInMillimeters(JPhyloIOReadingUtils.readLiteralMetadataContentAsObject(reader, Float.class).floatValue());
		}
		else {
			return false;
		}
		return true;
	}
	

	@Override
	public DocumentIterator createIterator(BufferedInputStream stream) throws Exception {
		return new SingleDocumentIterator(read(stream));
	}	
}
