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
package info.bioinfweb.treegraph.document.undo.edit;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.NodeType;
import info.bioinfweb.treegraph.document.TextElementData;
import info.bioinfweb.treegraph.document.change.DocumentChangeType;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.TextLabelAdapter;
import info.bioinfweb.treegraph.document.tools.TreeSerializer;
import info.bioinfweb.treegraph.document.undo.DocumentEdit;
import info.bioinfweb.treegraph.document.undo.CompareTextElementDataParameters;
import info.bioinfweb.treegraph.document.undo.WarningMessageEdit;



/**
 * Sorts the leaves in the specified subtree as close as possible to the specified order. Leaf nodes that are not 
 * found in the order list are positioned at the end.
 * 
 * @author Ben St&ouml;ver
 * @since 2.2.0
 */
public class SortLeavesEdit extends DocumentEdit implements WarningMessageEdit {
	private static class SortInfo {
		public int indexSum = 0;
		public int nodeCount = 0;
		
		public void add(SortInfo other) {
			indexSum += other.indexSum;
			nodeCount += other.nodeCount;
		}
		
		public double averageIndex() {
			return (double)indexSum / (double)nodeCount;
		}
	}
	
	
	private static class NodeComparator implements Comparator<Node> {
		@Override
    public int compare(Node n1, Node n2) {
	    return (int)Math.signum((Double)n1.getAttributeMap().get(ATTRIBUTE_ID) - (Double)n2.getAttributeMap().get(ATTRIBUTE_ID));
    }
	}
	
	
	public static final int MAX_WARNING_ELEMENTS = 8;
	public static final String ATTRIBUTE_ID = SortLeavesEdit.class.getCanonicalName() + "_averageIndex";
	
	private static final NodeComparator NODE_COMPARATOR = new NodeComparator();
	
	
	private Node root;
	private List<Node> newOrder;
	private List<Node> oldOrder;
	private NodeBranchDataAdapter leafAdapter;
	private CompareTextElementDataParameters parameters;
	private List<TextElementData> unlinkedOrderValues = new ArrayList<TextElementData>();
	private List<TextElementData> unlinkedTreeValues = new ArrayList<TextElementData>();

	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param document the document where the leafs shall be sorted
	 * @param newOrder the new order of the leaf nodes (Should contain most of the values the document contains on 
	 *        its leaves in the specified node/branch data column.)
	 * @param leafAdapter the node/branch data column used to combine the document leaf nodes with the specified 
	 *        order elements
	 */
	public SortLeavesEdit(Document document, Node root, List<TextElementData> newOrder, NodeBranchDataAdapter leafAdapter,
				CompareTextElementDataParameters parameters) {
		
	  super(document, DocumentChangeType.NODE_ORDER);
	  this.root = root;
	  this.leafAdapter = leafAdapter;
	  this.parameters = parameters;
	  
	  saveNewOrder(newOrder);
		oldOrder = TreeSerializer.getElementsInSubtreeAsList(root, NodeType.LEAVES, Node.class);
  }

	
	private void saveNewOrder(List<TextElementData> order) {
		newOrder = new ArrayList<Node>();
		for (TextElementData data : order) {
			Node node = getDocument().getTree().getFirstNodeByData(leafAdapter, data, true, parameters);
			if (node != null) {
				newOrder.add(node);
			}
			else {
				unlinkedOrderValues.add(data);
			}
    }
	}
	
	
	private SortInfo sortLeafs(Node root, List<Node> order) {
		SortInfo result = new SortInfo();
		if (root.isLeaf()) {
			if (order.contains(root)) {
				result.nodeCount = 1;
				result.indexSum = order.indexOf(root);
			}
			else {
				unlinkedTreeValues.add(root.getData());
				return null;
			}
		}
		else {
			for (Node child : root.getChildren()) {
		    SortInfo childInfo = sortLeafs(child, order);
		    if (childInfo != null) {
			    child.getAttributeMap().put(ATTRIBUTE_ID, childInfo.averageIndex());
			    result.add(childInfo);
		    }
		    else {
			    child.getAttributeMap().put(ATTRIBUTE_ID, Double.MAX_VALUE);  // Position nodes with undefined order at the end.
		    }
	    }
			
			Collections.sort(root.getChildren(), NODE_COMPARATOR);
		}
		return result;
	}
	
	
//	private void showIndices(Node root) {
//		Double index = (Double)root.getAttributeMap().get(ATTRIBUTE_ID);
//		if (index != null) {
//			new TextLabelAdapter("averageIndex").setDecimal(root, index);
//		}
//		for (Node child : root.getChildren()) {
//			showIndices(child);
//		}
//	}
	

	@Override
  public void redo() throws CannotRedoException {
		unlinkedTreeValues.clear();
	  sortLeafs(root, newOrder);
	  //showIndices(root);
	  super.redo();
  }

	
	@Override
  public void undo() throws CannotUndoException {
		unlinkedTreeValues.clear();
	  sortLeafs(root, oldOrder);
	  super.undo();
  }

	
	@Override
  public String getPresentationName() {
	  return "Sort leaves";
  }


	private void addListToWarningText(List<TextElementData> list, StringBuffer buffer) {
		int length = Math.min(MAX_WARNING_ELEMENTS, list.size());
		for (int i = 0; i < list.size(); i++) {
      buffer.append("- \"" + list.get(i).toString() + "\"\n");
    }
		if (list.size() > MAX_WARNING_ELEMENTS) {
			buffer.append((list.size() - length) + " more...");
		}
	}
	
	
	@Override
  public String getWarningText() {
		if (hasWarnings()) {
			StringBuffer buffer = new StringBuffer();
			buffer.append("The leaf elements of the tree and the elements in the order list were not identical:\n\n");
			
			if (!unlinkedOrderValues.isEmpty()) {
				buffer.append("The following elements from the order list (loaded list or other tree document) have not been ");
				buffer.append("found in the tree:\n\n");
				addListToWarningText(unlinkedOrderValues, buffer);
				buffer.append("\n\n");
			}
			
			if (!unlinkedTreeValues.isEmpty()) {
				buffer.append("The following elements from the tree have not been found in the order list:\n\n");
				addListToWarningText(unlinkedTreeValues, buffer);
				buffer.append("\nThese elements have been positioned as the last elements in their subtree.");
			}

			return buffer.toString();
		}
		else {
			return null;
		}
  }


	@Override
  public boolean hasWarnings() {
	  return !unlinkedOrderValues.isEmpty() || !unlinkedTreeValues.isEmpty();
  }
	
	
	/**
	 * Reads a list of values to order the leaves of a tree from a text file where each value is contained in one line.
	 * 
	 * @param file - the file to be loaded
	 * @param parameters - the parameter object specifying how to parse the single values for comparison 
	 * @return a list containing the loaded values in the order they were stored in the file
	 * @throws IOException if an IO exception occurs while trying to read from the specified file
	 */
	public static List<TextElementData> orderFromTextFile(File file, CompareTextElementDataParameters parameters) 
			throws IOException {
		
		List<TextElementData> result = new ArrayList<TextElementData>();
		BufferedReader reader = new BufferedReader(new FileReader(file));
		try {
			String line = reader.readLine();
			while (line != null) {
				result.add(parameters.createEditedValue(line));
				line = reader.readLine();
			}
		}
		finally {
			reader.close();
		}
		return result;
	}
	
	
	/**
	 * Creates a list of values to order the leaves of a tree from another tree document.
	 * 
	 * @param document - the document to read the order from
	 * @param adapter - the node/branch data adapter to be used to obtain a value from a leaf node
	 * @param parameters - the parameter object specifying how to parse the single values for comparison 
	 * @return a list containing the values of the leaves nodes of the specified document from top to bottom
	 */
	public static List<TextElementData> orderFromDocument(Document document, NodeBranchDataAdapter adapter,
			CompareTextElementDataParameters parameters) {
		
		List<Node> leaves = TreeSerializer.getElementsInSubtreeAsList(document.getTree().getPaintStart(), NodeType.LEAVES, Node.class);
		List<TextElementData> result = new ArrayList<TextElementData>(leaves.size());
		for (Node node : leaves) {
	    result.add(parameters.createEditedValue(adapter.toTextElementData(node).toString()));
    }
		return result;
	}
}
