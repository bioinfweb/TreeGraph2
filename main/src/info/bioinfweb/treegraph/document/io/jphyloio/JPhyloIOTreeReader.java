/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2011, 2013-2019  Ben Stöver, Sarah Wiechers, Kai Müller
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


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import info.bioinfweb.commons.log.ApplicationLogger;
import info.bioinfweb.jphyloio.JPhyloIOEventReader;
import info.bioinfweb.jphyloio.events.EdgeEvent;
import info.bioinfweb.jphyloio.events.JPhyloIOEvent;
import info.bioinfweb.jphyloio.events.LabeledIDEvent;
import info.bioinfweb.jphyloio.events.NodeEvent;
import info.bioinfweb.jphyloio.events.meta.LiteralMetadataContentEvent;
import info.bioinfweb.jphyloio.events.meta.LiteralMetadataEvent;
import info.bioinfweb.jphyloio.events.meta.ResourceMetadataEvent;
import info.bioinfweb.jphyloio.events.meta.URIOrStringIdentifier;
import info.bioinfweb.jphyloio.events.type.EventContentType;
import info.bioinfweb.jphyloio.events.type.EventTopologyType;
import info.bioinfweb.jphyloio.utils.JPhyloIOReadingUtils;
import info.bioinfweb.treegraph.document.HiddenDataMap;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.TextElementData;
import info.bioinfweb.treegraph.document.Tree;
import info.bioinfweb.treegraph.document.nodebranchdata.BranchLengthAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeNameAdapter;



public class JPhyloIOTreeReader {
	public static class TreeResult {
		private String id;
		private String label;
		private Tree tree;
		
		
		public TreeResult(String id, String label, Tree tree) {
			super();
			this.id = id;
			this.label = label;
			this.tree = tree;
		}


		public String getID() {
			return id;
		}


		public String getLabel() {
			return label;
		}


		public Tree getTree() {
			return tree;
		}
	}
	
	
	private JPhyloIOEventReader reader;
	private ApplicationLogger logger;
	private String currentTreeName;
	private Map<String, Node> idToNodeMap = new HashMap<String, Node>();
	private List<String> possiblePaintStartIDs = new ArrayList<String>();
	private List<String> rootNodeIDs = new ArrayList<String>(); //TODO Mark all root nodes with icon label or something similar
	private String currentColumnID = null;
	private NodeBranchDataAdapter nodeNameAdapter = NodeNameAdapter.getSharedInstance();
	private BranchLengthAdapter branchLengthAdapter = BranchLengthAdapter.getSharedInstance();

	
	public TreeResult readTree(LabeledIDEvent treeEvent, JPhyloIOEventReader reader, ApplicationLogger logger) throws XMLStreamException, IOException {
		this.reader = reader;
		this.logger = logger;
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
    
    return new TreeResult(treeEvent.getID(), treeEvent.getLabel(), tree);
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
    	if (event.getType().getContentType().equals(EventContentType.LITERAL_META) || event.getType().getContentType().equals(EventContentType.RESOURCE_META)) {
    		readMetadata(event, node.getHiddenDataMap());
    	}
    	else if (event.getType().getContentType().equals(EventContentType.LITERAL_META_CONTENT)) {
    		readLiteralContent(event.asLiteralMetadataContentEvent(), node.getHiddenDataMap()); 
    	}
      else {  // Possible additional element, which is not read
      	if (event.getType().getTopologyType().equals(EventTopologyType.START)) {  // SOLE and END events do not have to be processed here, because they have no further content.
      		JPhyloIOReadingUtils.reachElementEnd(reader);
    		}
      }
      event = reader.next();
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
    	if (event.getType().getContentType().equals(EventContentType.LITERAL_META) || event.getType().getContentType().equals(EventContentType.RESOURCE_META)) {
    		readMetadata(event, targetNode.getAfferentBranch().getHiddenDataMap());
    	}
    	else if (event.getType().getContentType().equals(EventContentType.LITERAL_META_CONTENT)) {
    		readLiteralContent(event.asLiteralMetadataContentEvent(), targetNode.getAfferentBranch().getHiddenDataMap()); 
    	}
    	else {  // Possible additional element, which is not read
      	if (event.getType().getTopologyType().equals(EventTopologyType.START)) {  // SOLE and END events do not have to be processed here, because they have no further content.
      		JPhyloIOReadingUtils.reachElementEnd(reader);
    		}
      }
      event = reader.next();
    }
  }
	
	
	private void readMetadata(JPhyloIOEvent metaEvent, HiddenDataMap map) throws IOException {
		if (metaEvent.getType().getTopologyType().equals(EventTopologyType.START)) {
			if (metaEvent.getType().getContentType().equals(EventContentType.RESOURCE_META)) {
				ResourceMetadataEvent resourceMeta = metaEvent.asResourceMetadataEvent();
				if ((resourceMeta.getHRef() != null) && (resourceMeta.getRel().getURI() != null)) {
					storeMetaData(map, extractMetadataKey(resourceMeta.getRel()), new TextElementData(resourceMeta.getHRef().toString()));  // The whole predicate URI is used as a key here
				}
			}
			else if (metaEvent.getType().getContentType().equals(EventContentType.LITERAL_META)) {
				LiteralMetadataEvent literalMeta = metaEvent.asLiteralMetadataEvent();
				if (literalMeta.getPredicate().getURI() != null) {
					currentColumnID = extractMetadataKey(literalMeta.getPredicate());
				}
			}
		}
		
		JPhyloIOEvent event = reader.next();
	    while (!event.getType().getTopologyType().equals(EventTopologyType.END)) {
	    	if (event.getType().getContentType().equals(EventContentType.LITERAL_META) || event.getType().getContentType().equals(EventContentType.RESOURCE_META)) {
	    		readMetadata(event, map);
	    	}
	    	else if (event.getType().getContentType().equals(EventContentType.LITERAL_META_CONTENT)) {
	    		readLiteralContent(event.asLiteralMetadataContentEvent(), map);
	    	}
	    	else {  // Possible additional element, which is not read
	      	if (event.getType().getTopologyType().equals(EventTopologyType.START)) {  // SOLE and END events do not have to be processed here, because they have no further content.
	      		JPhyloIOReadingUtils.reachElementEnd(reader);
	    		}
	      }
	      event = reader.next();
	    }
	}
	
	
	private void readLiteralContent(LiteralMetadataContentEvent literalEvent, HiddenDataMap map) throws IOException {
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
		
    storeMetaData(map, currentColumnID, data);
	}
	
	
	private String extractMetadataKey(URIOrStringIdentifier predicate) {
		return predicate.getURI().getNamespaceURI() + predicate.getURI().getLocalPart();
		//TODO Extend functionality of this method when more formats are supported. (Make use of string representation alternatively.)
		//TODO Will a '/' separating namespace URI and local part always be present/necessary? 
	}
	
	
	private void storeMetaData(HiddenDataMap map, String key, TextElementData data) {
		if (!map.containsKey(key)) {
			map.put(key, data);
		}
		else if (logger != null) {
			logger.addMessage("More than one value with the key \"" + currentColumnID + "\" was encountered for one node or branch."
					+ " Only the first encountered value was imported.");
		}
	}
}
