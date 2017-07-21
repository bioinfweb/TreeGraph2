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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import info.bioinfweb.commons.io.XMLUtils;
import info.bioinfweb.jphyloio.JPhyloIO;
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
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.TextElementData;
import info.bioinfweb.treegraph.document.Tree;
import info.bioinfweb.treegraph.document.format.LineFormats;
import info.bioinfweb.treegraph.document.format.Margin;
import info.bioinfweb.treegraph.document.format.NodeFormats;
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
				String testString = "test";
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
	
	
	private boolean readTextFormats(LiteralMetadataEvent literalMeta, TextFormats f)  throws IOException {
		if (literalMeta.getPredicate().equals(XTGConstants.PREDICATE_TEXT_HEIGHT)) {
			f.getTextHeight().setInMillimeters(JPhyloIOReadingUtils.readLiteralMetadataContentAsObject(reader, Float.class));  //TODO NPE possible
		}
		else if (literalMeta.getPredicate().equals(XTGConstants.PREDICATE_TEXT_COLOR)) {
			f.setTextColor(JPhyloIOReadingUtils.readLiteralMetadataContentAsObject(reader, Color.class));
		}
		else if (literalMeta.getPredicate().equals(XTGConstants.PREDICATE_TEXT_STYLE)) {
			f.setTextStyle(JPhyloIOReadingUtils.readLiteralMetadataContentAsObject(reader, Integer.class));
		}
		else if (literalMeta.getPredicate().equals(XTGConstants.PREDICATE_FONT_FAMILY)) {
			f.setFontName(JPhyloIOReadingUtils.readLiteralMetadataContentAsString(reader));
		}
		else {
			return false;
		}
		return true;
	}
	
	
	private boolean readLineFormats(LiteralMetadataEvent literalMeta, LineFormats f)  throws IOException {
		if (literalMeta.getPredicate().equals(XTGConstants.PREDICATE_LINE_COLOR)) {
			f.setLineColor(JPhyloIOReadingUtils.readLiteralMetadataContentAsObject(reader, Color.class));
		}
		else if (literalMeta.getPredicate().equals(XTGConstants.PREDICATE_LINE_WIDTH)) {
			f.getLineWidth().setInMillimeters(JPhyloIOReadingUtils.readLiteralMetadataContentAsObject(reader, Float.class));
		}
		else {
			return false;
		}
		return true;
	}
	
	
	private void readMargin(Margin margin, NodeFormats f) throws IOException {		
	}
	
	
	private void readInternalNodeMetadata(Node node) throws IOException {
		JPhyloIOEvent event = reader.next();
		while (!event.getType().getTopologyType().equals(EventTopologyType.END)) {
			if (event.getType().getContentType().equals(EventContentType.RESOURCE_META)) {
				ResourceMetadataEvent resourceMeta = event.asResourceMetadataEvent();
				if (XTGConstants.PREDICATE_LEAF_MARGIN.equals(resourceMeta.getRel().getURI())) {
					readMargin(node.getFormats().getLeafMargin(), ...);
				}
			}	
			else if (event.getType().getContentType().equals(EventContentType.LITERAL_META)) {
				LiteralMetadataEvent literalMeta = event.asLiteralMetadataEvent();
				NodeFormats f = node.getFormats();
				if (!readTextFormats(literalMeta, f) && !readLineFormats(literalMeta, f)) {
					if (literalMeta.getPredicate().equals(XTGConstants.PREDICATE_TEXT)) {
						node.getData().setText(JPhyloIOReadingUtils.readLiteralMetadataContentAsString(reader));
					}
					else if (literalMeta.getPredicate().equals(XTGConstants.PREDICATE_)) {
						//...
					}
				}
			}
			else {
				JPhyloIOReadingUtils.reachElementEnd(reader);
			}
			event = reader.next();
		}
	}
	
	
	private void readInternaLiterallMetadataContent(HiddenDataElement node, QName predicate) throws IOException {
		JPhyloIOEvent event = reader.next();
		Object value = event.asLiteralMetadataContentEvent().getObjectValue();		
		
		while (!event.getType().getTopologyType().equals(EventTopologyType.END)) {
			if (reader.peek().getType().getContentType().equals(EventContentType.LITERAL_META_CONTENT)) {
				if (predicate.equals(XTGConstants.PREDICATE_MARGIN_LEFT)) {
					if (node instanceof Node) {
						((Node)node).getFormats().getLeafMargin().getLeft().setInMillimeters((Float) value);
					}
				} 
				else if (predicate.equals(XTGConstants.PREDICATE_MARGIN_TOP)) {
					if (node instanceof Node) {
						((Node)node).getFormats().getLeafMargin().getTop().setInMillimeters((float) value);
					}
				}
				else if (predicate.equals(XTGConstants.PREDICATE_MARGIN_RIGHT)) {
					if (node instanceof Node) {
						((Node)node).getFormats().getLeafMargin().getRight().setInMillimeters((float) value);
					}
				}
				else if (predicate.equals(XTGConstants.PREDICATE_MARGIN_BOTTOM)) {
					if (node instanceof Node) {
						((Node)node).getFormats().getLeafMargin().getBottom().setInMillimeters((float) value);
					}
				}
				else if (predicate.equals(XTGConstants.PREDICATE_LINE_WIDTH)) {
					if (node instanceof Node) {
						((Node)node).getFormats().getLineWidth().setInMillimeters((float) value);
					}
				}
				else if (predicate.equals(XTGConstants.PREDICATE_LINE_COLOR)) {
					if (node instanceof Node) {
						((Node)node).getFormats().setLineColor((Color)value);  //TODO Make sure that JPhyloIO uses ObjectTranslator for XTG color type also when reading non-XTG formats like NeXML.
					}
				}				
				else if (predicate.equals(XTGConstants.PREDICATE_NODE_ATTR_UNIQUE_NAME)) {
					((Node)node).setUniqueName((String) value);
				}
				else if (predicate.equals(XTGConstants.PREDICATE_NODE_ATTR_EDGE_RADIUS)) {
					((Node)node).getFormats().getCornerRadius().setInMillimeters((float) value);
				}
				else if (predicate.equals(XTGConstants.PREDICATE_IS_DECIMAL)) {
					if (node instanceof Node) {
					}
				}
//				else if (predicate.equals(XTGConstants.PREDICATE_TEXT_COLOR)) {
//					if (node instanceof Node) {
//						((Node)node).getFormats().setTextColor(XMLUtils.readColorAttr(value, predicate, Color.BLACK));
//					}
//				}
				else if (predicate.equals(XTGConstants.PREDICATE_TEXT_HEIGHT)) {
					if (node instanceof Node) {
						((Node)node).getFormats().getTextHeight().setInMillimeters((float) value);
					}
				}
				else if (predicate.equals(XTGConstants.PREDICATE_TEXT_STYLE)) {
					if (node instanceof Node) {
						((Node)node).getFormats().setTextStyle((int) value);
					}
				}
				else if (predicate.equals(XTGConstants.PREDICATE_FONT_FAMILY)) {
					if (node instanceof Node) {
						((Node)node).getFormats().setFontName((String) value);
					}
				}
		//		else if (predicate.equals(XTGConstants.PREDICATE_DECIMAL_FORMAT)) {
		//			if (node instanceof Node) {
		//				
		//			}
		//		}
		//		else if (predicate.equals(XTGConstants.PREDICATE_LOCALE_LANG)) {
		//			if (node instanceof Node) {
		//				((Node)node).getFormats().getLocale().getLanguage();
		//			}
		//		}
		//		else if (predicate.equals(XTGConstants.PREDICATE_LOCALE_COUNTRY)) {
		//			if (node instanceof Node) {
		//				((Node)node).getFormats().getLocale().getCountry();
		//			}
		//		}
		//		else if (predicate.equals(XTGConstants.PREDICATE_LOCALE_VARIANT)) {
		//			if (node instanceof Node) {
		//				((Node)node).getFormats().getLocale().getVariant();
		//			}
		//		}
				else if (predicate.equals(XTGConstants.PREDICATE_BRANCH_ATTR_CONSTANT_WIDTH)) {
					((Branch)node).getFormats().setConstantWidth((boolean) value);
				}
				else if (predicate.equals(XTGConstants.PREDICATE_BRANCH_ATTR_MIN_LENGTH)) {
					((Branch)node).getFormats().getMinLength().setInMillimeters((float) value);
				}
				else if (predicate.equals(XTGConstants.PREDICATE_BRANCH_ATTR_MIN_SPACE_ABOVE)) {
					((Branch)node).getFormats().getMinSpaceAbove().setInMillimeters((float) value);
				}
				else if (predicate.equals(XTGConstants.PREDICATE_BRANCH_ATTR_MIN_SPACE_BELOW)) {
					((Branch)node).getFormats().getMinSpaceBelow().setInMillimeters((float) value);
				}
			}
		}
	}
	

	@Override
	public DocumentIterator createIterator(BufferedInputStream stream) throws Exception {
		return new SingleDocumentIterator(read(stream));
	}
	
	
	public void setMargin(Margin margin, JPhyloIOEvent event) throws IOException {
		LiteralMetadataEvent literalMarginEvent = event.asLiteralMetadataEvent();
		if (info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_MARGIN_LEFT.equals(literalMarginEvent.getPredicate().getURI())) {
			margin.getLeft().setInMillimeters(JPhyloIOReadingUtils.readLiteralMetadataContentAsObject(reader, Number.class).floatValue());
		}
		else if (info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_MARGIN_TOP.equals(literalMarginEvent.getPredicate().getURI())) {
			margin.getTop().setInMillimeters(JPhyloIOReadingUtils.readLiteralMetadataContentAsObject(reader, Number.class).floatValue());
		}
		else if (info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_MARGIN_RIGHT.equals(literalMarginEvent.getPredicate().getURI())) {
			margin.getRight().setInMillimeters(JPhyloIOReadingUtils.readLiteralMetadataContentAsObject(reader, Number.class).floatValue());
		}
		else if (info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_MARGIN_BOTTOM.equals(literalMarginEvent.getPredicate().getURI())) {
			margin.getBottom().setInMillimeters(JPhyloIOReadingUtils.readLiteralMetadataContentAsObject(reader, Number.class).floatValue());
		}
	}
}
