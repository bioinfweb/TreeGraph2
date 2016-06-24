/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2016  Ben Stöver, Sarah Wiechers, Kai Müller
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
import info.bioinfweb.jphyloio.events.type.EventContentType;
import info.bioinfweb.jphyloio.events.type.EventTopologyType;
import info.bioinfweb.jphyloio.formats.nexml.NeXMLEventReader;
import info.bioinfweb.jphyloio.tools.JPhyloIOUtils;
import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.Tree;
import info.bioinfweb.treegraph.document.io.AbstractDocumentReader;
import info.bioinfweb.treegraph.document.io.DocumentIterator;
import info.bioinfweb.treegraph.document.io.SingleDocumentIterator;
import info.bioinfweb.treegraph.document.nodebranchdata.BranchLengthAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeNameAdapter;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamException;



public class JPhyloIOReader extends AbstractDocumentReader {
	private JPhyloIOEventReader reader;
	private List<Tree> trees = new ArrayList<Tree>();
	private List<String> names = new ArrayList<String>();
	private Map<String, Node> idToNodeMap = new HashMap<String, Node>();
	private List<String> possiblePaintStartIDs = new ArrayList<String>();
	private List<String> rootNodeIDs = new ArrayList<String>(); //TODO mark all root nodes with icon label or something similar
	private NodeBranchDataAdapter nodeNameAdapter = NodeNameAdapter.getSharedInstance();
	private BranchLengthAdapter branchLengthAdapter = BranchLengthAdapter.getSharedInstance();
	

	public JPhyloIOReader() {
		super(false);
	}

	
	@Override
	public Document readDocument(BufferedInputStream stream) throws Exception {
		document = null;
		reader = new NeXMLEventReader(stream, new ReadWriteParameterMap()); //TODO are some entries to the parameter map necessary here?
		
//		JPhyloIOReaderWriterFactory factory = new JPhyloIOReaderWriterFactory(); //TODO file a bug, buffer size seems to be not large enough to read larger files (like the treebase example file)
//		reader = factory.guessReader(stream, new ReadWriteParameterMap());		
		
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
		          	parameterMap.getApplicationLogger().addMessage("No tree could be read from the document.");
		          }
		          
		          return document; //TODO do not return an empty document if no tree could be read
	      		}
	      		break;
	      	case TREE_NETWORK_GROUP:
	      		readDocument(event.asLinkedLabeledIDEvent());
	        	break;
	        default: // possible additional element, which is not read
	        	JPhyloIOUtils.reachElementEnd(reader);
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
	
	
	private void readDocument(LinkedLabeledIDEvent treeGroupEvent) throws XMLStreamException, IOException {
    JPhyloIOEvent event = reader.next();   
    while (!event.getType().getTopologyType().equals(EventTopologyType.END)) {
    	if (event.getType().getContentType().equals(EventContentType.TREE) || event.getType().getContentType().equals(EventContentType.NETWORK)) {    		
    		readTree(event.asLabeledIDEvent());
    	}
      else { // possible additional element, which is not read      	
        JPhyloIOUtils.reachElementEnd(reader);
      }
      event = reader.next();
    }
  }
	
	
	private void readTree(LabeledIDEvent treeEvent) throws XMLStreamException, IOException {
		String treeName;
		if ((treeEvent.getLabel() != null) && !treeEvent.getLabel().isEmpty()) {
			treeName = treeEvent.getLabel();
    }
		else {
			treeName = treeEvent.getID();
		}
		
		possiblePaintStartIDs.clear();
		idToNodeMap.clear();
		
    JPhyloIOEvent event = reader.next();
    while (!event.getType().getTopologyType().equals(EventTopologyType.END)) {    	
    	if (event.getType().getContentType().equals(EventContentType.NODE)) {
    		readNode(event.asLinkedLabeledIDEvent());
    	}
    	else if (event.getType().getContentType().equals(EventContentType.EDGE)) {
    		readEdge(event.asEdgeEvent());
    	}
      else { // possible additional element, which is not read
      	JPhyloIOUtils.reachElementEnd(reader);
      }
      event = reader.next();
    }
    
    Tree tree;
    if (possiblePaintStartIDs.size() > 1) {
    	parameterMap.getApplicationLogger().addWarning("More than one tree was constructed from the data in this file. "
    			+ "It is possible that some information was lost during this process.");
    }
    
    for (String nodeID : possiblePaintStartIDs) {
	    tree = new Tree();
	    tree.setPaintStart(idToNodeMap.get(nodeID));
	    tree.assignUniqueNames();
	    trees.add(tree);

	    names.add(treeName);
    }
  }
	
	
	private void readNode(LinkedLabeledIDEvent nodeEvent) throws XMLStreamException, IOException {
		Node node = Node.newInstanceWithBranch();

		nodeNameAdapter.setText(node, nodeEvent.getLabel());
		idToNodeMap.put(nodeEvent.getID(), node);
		possiblePaintStartIDs.add(nodeEvent.getID());
		
		JPhyloIOEvent event = reader.next();
    while (!event.getType().getTopologyType().equals(EventTopologyType.END)) {
    	JPhyloIOUtils.reachElementEnd(reader); // Events nested under node are not read
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
				possiblePaintStartIDs.remove(edgeEvent.getTargetID()); // Nodes that were not referenced as target are possible paint starts
			}
			else {
				rootNodeIDs.add(edgeEvent.getTargetID()); // Nodes without source node are root nodes
			}
		}
		else { // Edge is network edge
			parameterMap.getApplicationLogger().addWarning("Multiple parent nodes were specified for the node \"" + edgeEvent.getTargetID() + "\". "
					+ "This can not be displayed in TreeGraph 2, therefore it is possible that some information was lost. ");
		}
		
		JPhyloIOEvent event = reader.next();
    while (!event.getType().getTopologyType().equals(EventTopologyType.END)) {
    	JPhyloIOUtils.reachElementEnd(reader); // Events nested under edge are not read
      event = reader.next();
    }
  }
	

	@Override
	public DocumentIterator createIterator(BufferedInputStream stream) throws Exception {
		return new SingleDocumentIterator(read(stream));
	}
}
