/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2015  Ben Stöver, Sarah Wiechers, Kai Müller
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
package info.bioinfweb.treegraph.document.topologicalcalculation;


import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import info.bioinfweb.commons.Math2;
import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.TextElementData;
import info.bioinfweb.treegraph.document.Tree;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeNameAdapter;
import info.bioinfweb.treegraph.document.topologicalcalculation.LeafSet;
import info.bioinfweb.treegraph.document.topologicalcalculation.NodeInfo;
import info.bioinfweb.treegraph.document.undo.CompareTextElementDataParameters;



/**
 * Implements basic functionalities for all edits and operations that depend on calculations based on the 
 * tree topology and therefore need the tree nodes to be decorated with information in their subtrees. 
 * 
 * @author Ben St&ouml;ver
 */
public class TopologicalCalculator {
	public static final int MAX_TERMINAL_ERROR_COUNT = 10;
	public static final NodeNameAdapter SOURCE_LEAFS_ADAPTER = NodeNameAdapter.getSharedInstance();
	
	
	protected Map<TextElementData, Integer> leafValueToIndexMap = new TreeMap<TextElementData, Integer>();
	protected boolean processRooted;
	protected String keyLeafReference;
	protected CompareTextElementDataParameters parameters;
	
	
	public TopologicalCalculator(boolean processRooted,	String keyLeafReference, CompareTextElementDataParameters parameters) {
		this.processRooted = processRooted;
		this.keyLeafReference = keyLeafReference;
		this.parameters = parameters;
	}


	private Map<TextElementData, Integer> getLeafValueToIndexMap() {
		return leafValueToIndexMap;
	}


	/**
	 * Indicates whether the root of the trees used with this instance shall be considered as an additional
	 * terminal node or be ignored to assume trees to be unrooted.
	 * <p>
	 * Note that the value of this property determines how trees are compared by this instance. It is not
	 * taken into account whether the compared documents are displayed rooted or not as determined by their
	 * document format property.
	 * 
	 * @return {@code true} is the root is considered as an additional terminal node or {@code false} if it
	 *         shall be ignored. 
	 */
	public boolean isProcessRooted() {
		return processRooted;
	}


	/**
	 * Registers the leaf values of the subtree under {@code root} to the leaf value map of this instance.
	 * This map is used to map leaves to indices in a leaf set internally. Therefore all terminal under nodes 
	 * for which leaf sets shall be calculated using this instance in the future must be registered using this
	 * method. 
	 * 
	 * @param root the root of the subtree in which all leaves shall be registered
	 * @param adapter the adapter to be used to obtain leaf values from the terminals
	 */
	public void addToLeafValueToIndexMap(Node root, NodeBranchDataAdapter adapter) {
		addToLeafValueToIndexMap(leafValueToIndexMap, root, adapter);
	}
	
	
	/**
	 * Fills the specified map with the values of all leafs under {@code root} and an according index.
	 * 
	 * @param list
	 * @param root
	 * @param adapter
	 */
	private void addToLeafValueToIndexMap(Map<TextElementData, Integer> leafMap, Node root, NodeBranchDataAdapter adapter) {
		if (root.isLeaf()) {
			TextElementData data = parameters.createEditedValue(adapter.toTextElementData(root).toString());
			if (!leafMap.containsKey(data)) {
				leafMap.put(data, leafMap.size());
			}
		}
		else {
			Iterator<Node> iterator = root.getChildren().iterator();
			while (iterator.hasNext()) {
				addToLeafValueToIndexMap(leafMap, iterator.next(), adapter);
			}
		}
	}

	
	/**
	 * Checks if both the loaded and the imported tree contain exactly the same terminals.
	 * 
	 * @return an error message, if the terminal nodes are not identical or <code>null</code> if they are
	 */
	public String compareLeafs(Document src) {
		Map<TextElementData, Integer> sourceLeafValues = new TreeMap<TextElementData, Integer>();
		addToLeafValueToIndexMap(sourceLeafValues, src.getTree().getPaintStart(), SOURCE_LEAFS_ADAPTER);
		if (leafValueToIndexMap.size() != sourceLeafValues.size()) {
			return "The selected tree has a different number of terminals than " +
				"the opened document. No support values were added.";
		}
		else {
			String errorMsg = "";
			int errorCount = 0;
			for (TextElementData data : sourceLeafValues.keySet()) {
				if (!getLeafValueToIndexMap().containsKey(parameters.createEditedValue(data.toString()))) {
					if (errorCount < MAX_TERMINAL_ERROR_COUNT) {
						if (!errorMsg.equals("")) {
							errorMsg += ",\n";
						}
						errorMsg += "\"" + data + "\"";
					}
					errorCount++;
				}
			}
			if (errorMsg.equals("")) {
				return null;
			}
			errorMsg = "The selected tree contains the following terminals which are " +
					"not present in the opened document:\n\n" + errorMsg;
			if (errorCount > MAX_TERMINAL_ERROR_COUNT) {
				errorMsg += ", ...\n(" + (errorCount - MAX_TERMINAL_ERROR_COUNT) + " more)";
			}
			return errorMsg + "\n\nNo support values were added.";
		}
	}


	/**
	 * Returns the leaf field attribute of {@code node} if it has one attached. If not an according object
	 * is created first and than returned.
	 * 
	 * @param node - the node from which the leaf field attribute shall be returned or created. 
	 */
	public LeafSet getLeafSet(Node node) {
		if (node.getAttributeMap().get(keyLeafReference) == null) {
			int size = leafValueToIndexMap.size();
			if (processRooted) {
				size++;
			}
			LeafSet field = new LeafSet(size);
			node.getAttributeMap().put(keyLeafReference, field);
		}
		return (LeafSet)node.getAttributeMap().get(keyLeafReference);
	}

	
	public int getLeafIndex(String value) {
		TextElementData key = parameters.createEditedValue(value);
		Integer result = leafValueToIndexMap.get(key);
		if (result == null) {
			return -1;
		}
		else {
			return result;
		}
	}
	
	
	public int getLeafCount() {
		return leafValueToIndexMap.size();
	}
	

	/**
	 * Adds a boolean set which indicates the leafs located under <code>root</code> to
	 * the attribute map of <code>root</code>.`
	 * 
	 * @param root - the root of the subtree
	 */
	public void addLeafSets(Node root, NodeBranchDataAdapter adapter) {
		root.getAttributeMap().remove(keyLeafReference);  // Necessary to overwrite possible leaf sets from previous edits which might not be valid anymore.
		LeafSet field = getLeafSet(root);
		if (!root.isLeaf()) {
			for (int i = 0; i < root.getChildren().size(); i++) {
				Node child = root.getChildren().get(i);
				addLeafSets(child, adapter);
				if (child.isLeaf()) {
					field.setChild(getLeafIndex(adapter.toTextElementData(child).toString()), true);
				}
				else {
					field.addField(getLeafSet(child));
				}
			}
		}
		else {
			field.setChild(getLeafIndex(adapter.toTextElementData(root).toString()), true);
		}
	}
	
	
	public NodeInfo findSourceNodeWithAllLeafs(Tree tree, LeafSet targetLeafs) {  //necessary because LeafSet can not be created in the recursive method
		targetLeafs = targetLeafs.and(getLeafSet(tree.getPaintStart()));
		return findSourceNodeWithAllLeafsRecursive(tree.getPaintStart(), targetLeafs);
	}
	
	
	private NodeInfo findSourceNodeWithAllLeafsRecursive(Node sourceRoot, LeafSet targetLeafs) {
		if (isLeafSetEmpty(targetLeafs)) {
			return null;
		}
		int additionalCount = targetLeafs.compareTo(getLeafSet(sourceRoot), false);
		boolean downwards = additionalCount != -1;
		if (!downwards) {
			additionalCount = targetLeafs.compareTo(getLeafSet(sourceRoot), true);
		}
  	NodeInfo result = null;
  	if (additionalCount != -1) {
  		result = new NodeInfo(sourceRoot, additionalCount, downwards);
  	}
  	
		for (int i = 0; i < sourceRoot.getChildren().size(); i++) {
			NodeInfo childResult = findSourceNodeWithAllLeafsRecursive(sourceRoot.getChildren().get(i), targetLeafs);
			if (childResult != null) {
				if (result == null) {
					result = childResult;
				}
				else if ((childResult.getAdditionalCount() != -1) && 
						((childResult.getAdditionalCount() < result.getAdditionalCount()) || (result.getAdditionalCount() == -1))) {
					
					result = childResult;
				}
			}
		}
		return result;
	}
	
	
	public Node findHighestConflict(Tree activeTree, Tree selectionTargetTree, Node highestConflictingNode, LeafSet activeLeafSet, LeafSet selectionTargetLeafSet, NodeBranchDataAdapter adapter) {
		// TODO eindeutige Namen finden!
		Node selectionTargetRoot = selectionTargetTree.getPaintStart();
		Node activeTreeRoot = activeTree.getPaintStart();
	
		selectionTargetLeafSet = selectionTargetLeafSet.and(getLeafSet(activeTreeRoot));
		activeLeafSet = activeLeafSet.and(getLeafSet(selectionTargetRoot));
	
		return findHighestConflictRecursive(selectionTargetRoot, activeTreeRoot, highestConflictingNode, activeLeafSet, selectionTargetLeafSet, adapter);
	}
	
	
	/**
	 * This method is the recursive part called by {@code findHighestConflict}. The only difference between 
	 * the two is that this method can return the support value of root itself as {@code findHighestConflict} does not.
	 * 
	 * @param selectionTargetRoot - the root of the subtree to be searched (a node in the source document)
	 * @param highest - the initial support value
	 * @param targetNode - the node in the target document to attach a support value to
	 * @param info - information about the node in the source document which contains all terminals of
	 *        {@code targetNode} in its subtree 
	 * @return the node with the highest support value found (in the source document)
	 */
	private Node findHighestConflictRecursive(Node selectionTargetRoot, Node activeTreeRoot, Node highestConflictingNode, LeafSet activeNode, LeafSet selectionTargetNode, NodeBranchDataAdapter adapter) {
		LeafSet selectionTargetRootLeafSet = getLeafSet(selectionTargetRoot).and(getLeafSet(activeTreeRoot));
		
		if ((selectionTargetRootLeafSet.containsAnyAndOther(activeNode, false) &&
				selectionTargetRootLeafSet.inSubtreeOf(selectionTargetNode, false))
				|| (selectionTargetRootLeafSet.containsAnyAndOther(activeNode, true) &&
						selectionTargetRootLeafSet.inSubtreeOf(selectionTargetNode, true))) {
			
			double currentSupport = Double.NaN;
			if (adapter.isDecimal(selectionTargetRoot)) {
				currentSupport = adapter.getDecimal(selectionTargetRoot);
			}
			else if (adapter.isString(selectionTargetRoot)) {
				try {
					currentSupport = Math2.parseDouble(adapter.getText(selectionTargetRoot));
				}
				catch (NumberFormatException e) {}  // Nothing to do. 
			}
			
			if (!Double.isNaN(currentSupport)) {
				if (highestConflictingNode == null) {
					highestConflictingNode = selectionTargetRoot;
				}
				else {
					double previousSupport;
					if (adapter.isDecimal(highestConflictingNode)) {
						previousSupport = adapter.getDecimal(highestConflictingNode);
					}
					else {
						previousSupport = Math2.parseDouble(adapter.getText(highestConflictingNode));
					}
					
					if (previousSupport < currentSupport) {
						highestConflictingNode = selectionTargetRoot;
					}
				}
			}
		}
		
		for (int i = 0; i < selectionTargetRoot.getChildren().size(); i++) {
			highestConflictingNode = findHighestConflictRecursive(selectionTargetRoot.getChildren().get(i), activeTreeRoot, highestConflictingNode, 
					activeNode, selectionTargetNode, adapter);
		}
		
		return highestConflictingNode;
	}
	
	
	private boolean isLeafSetEmpty(LeafSet leafSet) {
		for (int i = 0; i < leafSet.size(); i++) {
			if(leafSet.isChild(i)) {
				return false;
			}
		}
		return true;
	}
}