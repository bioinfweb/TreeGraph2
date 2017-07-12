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


import info.bioinfweb.jphyloio.JPhyloIOEventReader;
import info.bioinfweb.jphyloio.ReadWriteParameterMap;
import info.bioinfweb.jphyloio.events.EdgeEvent;
import info.bioinfweb.jphyloio.events.JPhyloIOEvent;
import info.bioinfweb.jphyloio.events.LabeledIDEvent;
import info.bioinfweb.jphyloio.events.LinkedLabeledIDEvent;
import info.bioinfweb.jphyloio.events.NodeEvent;
import info.bioinfweb.jphyloio.events.meta.LiteralMetadataContentEvent;
import info.bioinfweb.jphyloio.events.meta.LiteralMetadataEvent;
import info.bioinfweb.jphyloio.events.meta.ResourceMetadataEvent;
import info.bioinfweb.jphyloio.events.meta.URIOrStringIdentifier;
import info.bioinfweb.jphyloio.events.type.EventContentType;
import info.bioinfweb.jphyloio.events.type.EventTopologyType;
import info.bioinfweb.jphyloio.formats.nexml.NeXMLEventReader;
import info.bioinfweb.jphyloio.utils.JPhyloIOReadingUtils;
import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.HiddenDataElement;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.TextElementData;
import info.bioinfweb.treegraph.document.Tree;
import info.bioinfweb.treegraph.document.format.Margin;
import info.bioinfweb.treegraph.document.io.AbstractDocumentReader;
import info.bioinfweb.treegraph.document.io.DocumentIterator;
import info.bioinfweb.treegraph.document.io.SingleDocumentIterator;
import info.bioinfweb.treegraph.document.metadata.LiteralMetadataNode;
import info.bioinfweb.treegraph.document.metadata.MetadataNode;
import info.bioinfweb.treegraph.document.metadata.MetadataPath;
import info.bioinfweb.treegraph.document.metadata.MetadataPathElement;
import info.bioinfweb.treegraph.document.metadata.MetadataTree;
import info.bioinfweb.treegraph.document.metadata.ResourceMetadataNode;
import info.bioinfweb.treegraph.document.nodebranchdata.BranchLengthAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeNameAdapter;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;



public class NeXMLReader extends AbstractDocumentReader {
	private JPhyloIOEventReader reader;
	private String currentTreeName;
	private List<Tree> trees = new ArrayList<Tree>();
	private List<String> names = new ArrayList<String>();
	private Map<String, Node> idToNodeMap = new HashMap<String, Node>();
	private List<String> possiblePaintStartIDs = new ArrayList<String>();
	private List<String> rootNodeIDs = new ArrayList<String>(); //TODO Mark all root nodes with icon label or something similar
	private QName literalPredicate = null;
//	private String currentColumnID = null;
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
	      		readTreeNetworkGroup(event.asLinkedLabeledIDEvent());
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
	
	
	private void readTreeNetworkGroup(LinkedLabeledIDEvent treeGroupEvent) throws XMLStreamException, IOException {
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
		
		JPhyloIOEvent event = reader.next();		
    while (!event.getType().getTopologyType().equals(EventTopologyType.END)) {  // It is assumed that events are correctly nested
    	MetadataPath path = new MetadataPath(true, ((event.getType().getContentType().equals(EventContentType.LITERAL_META) ? true : false)));
    	
    	if (event.getType().getContentType().equals(EventContentType.LITERAL_META) || event.getType().getContentType().equals(EventContentType.RESOURCE_META)) {
    		
    		readMetadata(event, path, node, node.getMetadataTree());
    		
//    		switch (event.getType().getContentType()) {    				
//    			case LITERAL_META:
//    				LiteralMetadataEvent literalEvent = event.asLiteralMetadataEvent();
//    				if (info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_IS_DECIMAL.equals(literalEvent.getPredicate().getURI())) {
//    					node.getData().setDecimal(JPhyloIOReadingUtils.readLiteralMetadataContentAsObject(reader, Number.class).doubleValue());
//    				} 
//    				else if (info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_NODE_ATTR_UNIQUE_NAME.equals(literalEvent.getPredicate().getURI())) {
//    					node.setUniqueName(JPhyloIOReadingUtils.readLiteralMetadataContentAsString(reader));
//    				}
//    				else if (info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_NODE_ATTR_EDGE_RADIUS.equals(literalEvent.getPredicate().getURI())) {
//    					node.getFormats().getCornerRadius().setInMillimeters(JPhyloIOReadingUtils.readLiteralMetadataContentAsObject(reader, Number.class).floatValue());
//    				}
//    				else {
//    					JPhyloIOReadingUtils.reachElementEnd(reader);
//      			}	
//    				break;
//      			
//    			case RESOURCE_META:
//    				ResourceMetadataEvent resourceEvent = event.asResourceMetadataEvent();
//    				if (info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_LEAF_MARGIN.equals(resourceEvent.getRel().getURI())) {
//    					setMargin(node.getFormats().getLeafMargin(), event);
//    				}
//    				else if (info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_.equals(resourceEvent.getRel().getURI())) {
//    					
//    				}
//    				else {
//    					JPhyloIOReadingUtils.reachElementEnd(reader);
//    				}
//    				break;
//    				
//					default:
//						JPhyloIOReadingUtils.reachElementEnd(reader);
//						break;
//    		}

    	}
    	else if (event.getType().getContentType().equals(EventContentType.LITERAL_META_CONTENT)) {
    		readLiteralContent(event.asLiteralMetadataContentEvent(), path, node.getMetadataTree()); 
    	}
      else {  // Possible additional element, which is not read
      	if (event.getType().getTopologyType().equals(EventTopologyType.START)) {  // SOLE and END events do not have to be processed here, because they have no further content.
      		JPhyloIOReadingUtils.reachElementEnd(reader);
    		}
      }
      event = reader.next();
    }
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
		
		JPhyloIOEvent event = reader.next();
    while (!event.getType().getTopologyType().equals(EventTopologyType.END)) {  // It is assumed that events are correctly nested
    	MetadataPath path = new MetadataPath(true, ((event.getType().getContentType().equals(EventContentType.LITERAL_META) ? true : false)));
    	
    	if (event.getType().getContentType().equals(EventContentType.LITERAL_META) || event.getType().getContentType().equals(EventContentType.RESOURCE_META)) {    		
    		readMetadata(event, path, targetNode.getAfferentBranch(), targetNode.getAfferentBranch().getMetadataTree());
    	}
    	else if (event.getType().getContentType().equals(EventContentType.LITERAL_META_CONTENT)) {
    		readLiteralContent(event.asLiteralMetadataContentEvent(), path, targetNode.getAfferentBranch().getMetadataTree()); 
    	}
    	else {  // Possible additional element, which is not read
      	if (event.getType().getTopologyType().equals(EventTopologyType.START)) {  // SOLE and END events do not have to be processed here, because they have no further content.
      		JPhyloIOReadingUtils.reachElementEnd(reader);
    		}
      }
      event = reader.next();
    }
  }
	
	
//	private String extractMetadataKey(URIOrStringIdentifier predicate) {
//		return predicate.getURI().getNamespaceURI() + predicate.getURI().getLocalPart();
//		//TODO Extend functionality of this method when more formats are supported. (Make use of string representation alternatively.)
//		//TODO Will a '/' separating namespace URI and local part always be present/necessary? 
//	}
	
	
	private void readMetadata(JPhyloIOEvent metaEvent, MetadataPath path, HiddenDataElement parent, MetadataTree tree) throws IOException {
		if (metaEvent.getType().getTopologyType().equals(EventTopologyType.START)) {
			
			if (tree.getChildren() == null) {
				tree = new MetadataTree(parent);
			}
			
			if (metaEvent.getType().getContentType().equals(EventContentType.RESOURCE_META)) {
				ResourceMetadataEvent resourceMeta = metaEvent.asResourceMetadataEvent();
				
				if ((resourceMeta.getHRef() != null) && (resourceMeta.getRel().getURI() != null)) {
					MetadataPathElement element = new MetadataPathElement(resourceMeta.getRel().getURI(), 0);
					storeMetaData(path, element);				
					
					MetadataNode metadataNode = tree.searchAndCreateNodeByPath(path, true);
					((ResourceMetadataNode)metadataNode).setURI(resourceMeta.getHRef());
//					storeMetaData(map, extractMetadataKey(resourceMeta.getRel()), new TextElementData(resourceMeta.getHRef().toString()));  // The whole predicate URI is used as a key here
				}
			}
			else if (metaEvent.getType().getContentType().equals(EventContentType.LITERAL_META)) {
				LiteralMetadataEvent literalMeta = metaEvent.asLiteralMetadataEvent();
				if (literalMeta.getPredicate().getURI() != null) {
					literalPredicate = literalMeta.getPredicate().getURI();
				}
			}
		}
		
		JPhyloIOEvent event = reader.next();
	    while (!event.getType().getTopologyType().equals(EventTopologyType.END)) {
	    	if (event.getType().getContentType().equals(EventContentType.LITERAL_META) || event.getType().getContentType().equals(EventContentType.RESOURCE_META)) {
	    		readMetadata(event, path, parent, tree);
	    	}
	    	else if (event.getType().getContentType().equals(EventContentType.LITERAL_META_CONTENT)) {
	    		readLiteralContent(event.asLiteralMetadataContentEvent(), path, tree);
	    	}
	    	else {  // Possible additional element, which is not read
	      	if (event.getType().getTopologyType().equals(EventTopologyType.START)) {  // SOLE and END events do not have to be processed here, because they have no further content.
	      		JPhyloIOReadingUtils.reachElementEnd(reader);
	    		}
	      }
	      event = reader.next();
	    }
	}
	
	
	private void readLiteralContent(LiteralMetadataContentEvent literalEvent, MetadataPath path, MetadataTree tree) throws IOException {
		StringBuffer content = new StringBuffer();
		TextElementData data = null;
		
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
		
		JPhyloIOEvent event = reader.peek();
    while (event.getType().getContentType().equals(EventContentType.LITERAL_META_CONTENT)) {
      event = reader.next();
      content.append(event.asLiteralMetadataContentEvent().getStringValue());  // Content can only be continued if it has only a string value      
    }
    
    if (data == null) {
    	data = new TextElementData(content.toString());
    }
    
		MetadataPathElement element = new MetadataPathElement(literalPredicate, 0);
		storeMetaData(path, element);
		
		MetadataNode metadataNode = tree.searchAndCreateNodeByPath(path, true);
		((LiteralMetadataNode)metadataNode).setValue(data);
	}
	
	
	private void storeMetaData(MetadataPath path, MetadataPathElement element) {
		if(!path.getElementList().contains(element)) {
			path.getElementList().add(element);
		}
		else {
			int index = element.getIndex();
			index++;
			MetadataPathElement newElement = new MetadataPathElement(element.getPredicateOrRel(), index);
			storeMetaData(path, newElement);
//			parameterMap.getApplicationLogger().addMessage("More than one value with the key \"" + currentColumnID + "\" was encountered for one node or branch."
//					+ " Only the first encountered value was imported.");
		}
	}
	
	
//	private void storeMetaData(HiddenDataMap map, String key, TextElementData data) {
//		if (!map.containsKey(key)) {
//			map.put(key, data);
//		}
//		else {
//			parameterMap.getApplicationLogger().addMessage("More than one value with the key \"" + currentColumnID + "\" was encountered for one node or branch."
//					+ " Only the first encountered value was imported.");
//		}
//	}
	

	@Override
	public DocumentIterator createIterator(BufferedInputStream stream) throws Exception {
		return new SingleDocumentIterator(read(stream));
	}
}
