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
package info.bioinfweb.treegraph.document.topologicalcalculation;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import info.bioinfweb.commons.Math2;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.TextElementData;
import info.bioinfweb.treegraph.document.Tree;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.undo.CompareTextElementDataParameters;
import info.bioinfweb.treegraph.document.undo.file.addsupportvalues.AddSupportValuesEdit;



/**
 * Implements basic functionalities for all edits and operations that depend on calculations based on the 
 * tree topology and therefore need the tree nodes to be decorated with information in their subtrees. 
 * 
 * @author Ben St&ouml;ver
 */
public class TopologicalCalculator {
	protected Map<TextElementData, Integer> leafValueToIndexMap;
	protected Map<TextElementData, Integer> leafValueToIndexMapBackup;
	protected boolean processRooted;
	protected String keyLeafReference;
	protected CompareTextElementDataParameters parameters;
	
	
	public TopologicalCalculator(boolean processRooted,	String keyLeafReference, CompareTextElementDataParameters parameters) {
		this.processRooted = processRooted;
		this.keyLeafReference = keyLeafReference;
		this.parameters = parameters;
		createNewIndexMap();
	}

	
	private void createNewIndexMap() {
		leafValueToIndexMap = new TreeMap<TextElementData, Integer>();
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
	 * This map is used to map leaves to indices in a leaf set internally. Therefore all terminals under nodes 
	 * for which leaf sets shall be calculated using this instance in the future must be registered using this
	 * method.
	 * <p>
	 * Note that the leaf set index assigned to each text element data does not need to be related to the index of that leaf in
	 * a concrete tree. Therefore it is possible to add taxa from multiple trees to the leaf map of this instance.
	 * 
	 * @param root the root of the subtree in which all leaves shall be registered
	 * @param adapter the adapter to be used to obtain leaf values from the terminals
	 */
	public void addSubtreeToLeafValueToIndexMap(Node root, NodeBranchDataAdapter adapter) {
		if (root.isLeaf()) {
			TextElementData data = parameters.createEditedValue(adapter.toTextElementData(root).toString());
			if (!leafValueToIndexMap.containsKey(data)) {
				leafValueToIndexMap.put(data, leafValueToIndexMap.size());
			}
		}
		else {
			for (Node child : root.getChildren()) {
				addSubtreeToLeafValueToIndexMap(child, adapter);
			}
		}
	}
	
	
	/**
	 * Removes all entries from the current index map that are not present in the specified subtree. Note that this method may change
	 * the indices associated with leaves. It should therefore be called before creating any leaf sets based in this index map.
	 * <p>
	 * This method can be used to make sure the index map only contains entries for leaves that are present in two separate trees.
	 * 
	 * @param root the root of the subtree
	 * @param adapter the node/branch data adapter that defines the column used to identify a leaf
	 */
	public void filterIndexMapBySubtree(Node root, NodeBranchDataAdapter adapter) {
		leafValueToIndexMapBackup = leafValueToIndexMap;
		createNewIndexMap();
		filterIndexMapBySubtreeRek(root, adapter);
		leafValueToIndexMapBackup = null;  // Allow to free memory of previous map.
	}
	
	
	private void filterIndexMapBySubtreeRek(Node root, NodeBranchDataAdapter adapter) {
		if (root.isLeaf()) {
			TextElementData data = parameters.createEditedValue(adapter.toTextElementData(root).toString());
			if (leafValueToIndexMapBackup.containsKey(data) && !leafValueToIndexMap.containsKey(data)) {  // Only use values that were present in the previous map.
				leafValueToIndexMap.put(data, leafValueToIndexMap.size());
			}
		}
		else {
			for (Node child : root.getChildren()) {
				filterIndexMapBySubtreeRek(child, adapter);
			}
		}
	}

	
	/**
	 * Returns the leaf field attribute of {@code node} if it has one attached. If not an according object
	 * is created first and than returned.
	 * 
	 * @param node the node from which the leaf field attribute shall be returned or created. 
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
	 * Adds a boolean set which indicates the leafs located under {@code root} to its attribute map.
	 * 
	 * @param root the root of the subtree to be processed
	 * @param leafAdapter the node/branch data adapter that provides access to the node/branch data column that defined a leaf node
	 */
	public void addLeafSets(Node root, NodeBranchDataAdapter leafAdapter) {
		root.getAttributeMap().remove(keyLeafReference);  // Necessary to overwrite possible leaf sets from previous edits which might not be valid anymore.
		LeafSet field = getLeafSet(root);
		if (!root.isLeaf()) {
			for (int i = 0; i < root.getChildren().size(); i++) {
				Node child = root.getChildren().get(i);
				addLeafSets(child, leafAdapter);
				if (child.isLeaf()) {
					int index = getLeafIndex(leafAdapter.toTextElementData(child).toString());
					if (index >= 0) {  // Ignore leaves that are not contained in the index list (e.g. leafs not present in both trees when adding support values) 
						field.setChild(index, true);
					}
				}
				else {
					field.addField(getLeafSet(child));
				}
			}
		}
		else {
			int index = getLeafIndex(leafAdapter.toTextElementData(root).toString());
			if (index >= 0) {  // see comment above
				field.setChild(index, true);
			}
		}
	}
	
	
	private LeafSet restrictLeafSet(LeafSet leafSet, LeafSet restrictingLeafSet) {
		if (restrictingLeafSet != null) {
			return leafSet.and(restrictingLeafSet);
		}
		else {
			return leafSet;
		}
	}
	
	
	/**
	 * Searches the MRCA in {@code tree} containing all leaves defined in the specified leaf set.
	 * <p>
	 * This method will return {@code null} if either an empty leaf set is provided or the specified
	 * leaf set contains only terminals, that are not contained in {@code tree}.
	 * 
	 * @param tree the tree to be searched
	 * @param leafSet the leaves that should be contained in the sought-after subtree
	 * @param restrictingLeafSet an optional set of terminals that contains the leaves to be considered for comparison (All other 
	 *        nodes found in compared leaf sets will be ignored. This parameter may be {@code null}.)
	 * @return a node info describing the found subtree root or {@code null} if no according subtree could 
	 *         be found. 
	 */
	public List<NodeInfo> findNodeWithAllLeaves(Tree tree, LeafSet searchedLeafSet, LeafSet restrictingLeafSet) {
		searchedLeafSet = restrictLeafSet(searchedLeafSet, restrictingLeafSet);
		List<NodeInfo> result = new ArrayList<NodeInfo>();
		findNodeWithAllLeavesRecursive(result, tree.getPaintStart(), searchedLeafSet, restrictingLeafSet);
		return result;
	}
	
	
	private void findNodeWithAllLeavesRecursive(List<NodeInfo> result, Node root, LeafSet searchedLeafSet, LeafSet restrictingLeafSet) {
		if (!isLeafSetEmpty(searchedLeafSet)) {
			LeafSet comparedLeafSet = getLeafSet(root);
			comparedLeafSet = restrictLeafSet(comparedLeafSet, restrictingLeafSet);
			
			int additionalCount = searchedLeafSet.compareTo(comparedLeafSet, false);
			boolean downwards = additionalCount != -1;
			if (!downwards) {
				additionalCount = searchedLeafSet.compareTo(comparedLeafSet, true);
			}
			//NodeInfo result = null;
			NodeInfo info = new NodeInfo(root, additionalCount, downwards);
			if (additionalCount != -1) {
				if (result.isEmpty()) {
					result.add(info);
				}
				else {
					int previousAdditionalCount = result.get(0).getAdditionalCount();
					if (additionalCount < previousAdditionalCount) {  // previousAdditionalCount should never be -1. (Was tested here before.)
						result.clear();
						result.add(info);
					}
					else if (additionalCount == previousAdditionalCount) {
						result.add(info);
					}
				}
			}
	  	
			for (Node child : root.getChildren()) {
				findNodeWithAllLeavesRecursive(result, child, searchedLeafSet, restrictingLeafSet);
			}
		}
	}
	

	// New method implementation for addSupportValues.  //TODO Check if it can also be used for TreeSelectionSynchronizer.
	public Node findHighestConflict(Node searchRoot, LeafSet conflictNodeLeafSet, NodeBranchDataAdapter supportAdapter, boolean parseText) {
		Node highestConflictingNode = null;
		// The root is not tested since it 
		for (int i = 0; i < searchRoot.getChildren().size(); i++) {
			highestConflictingNode = findHighestConflictRecursive(searchRoot.getChildren().get(i), conflictNodeLeafSet, highestConflictingNode, 
					supportAdapter, parseText);
		}
		return highestConflictingNode;
	}

	
	// New method implementation for addSupportValues.  //TODO Check if it can also be used for TreeSelectionSynchronizer.
	private Node findHighestConflictRecursive(Node searchRoot, LeafSet conflictNodeLeafSet, Node highestConflictingNode, 
			NodeBranchDataAdapter supportAdapter, boolean parseText) {
		
		LeafSet currentSearchRootLeafSet = getLeafSet(searchRoot);
		if (currentSearchRootLeafSet.containsAnyAndOther(conflictNodeLeafSet, false)
				|| currentSearchRootLeafSet.containsAnyAndOther(conflictNodeLeafSet, true)) {
			
			double currentSupport = supportAdapter.getNumericValue(searchRoot, parseText);
			if (!Double.isNaN(currentSupport)) {
				if (highestConflictingNode == null) {
					highestConflictingNode = searchRoot;
				}
				else {
					double previousSupport = supportAdapter.getNumericValue(highestConflictingNode, parseText);
					if (previousSupport < currentSupport) {
						highestConflictingNode = searchRoot;
					}
				}
			}
		}

		for (int i = 0; i < searchRoot.getChildren().size(); i++) {
			highestConflictingNode = findHighestConflictRecursive(searchRoot.getChildren().get(i), conflictNodeLeafSet, highestConflictingNode, 
					supportAdapter, parseText);
		}
		
		return highestConflictingNode;
	}
	
	
	public Node findHighestConflict(Tree referenceTree, Tree searchedTree, Node searchRoot, LeafSet conflictingReferenceLeafSet, 
			LeafSet completeSearchedLeafSet, NodeBranchDataAdapter searchedSupportAdapter) {
		
		LeafSet referenceRootLeafSet = getLeafSet(referenceTree.getPaintStart());
	
		completeSearchedLeafSet = completeSearchedLeafSet.and(referenceRootLeafSet);
		conflictingReferenceLeafSet = conflictingReferenceLeafSet.and(getLeafSet(searchedTree.getPaintStart()));
	
		return findHighestConflictRecursive(searchRoot, referenceRootLeafSet, null, conflictingReferenceLeafSet, 
				completeSearchedLeafSet, searchedSupportAdapter);
	}
	
	
	/**
	 * This method is the recursive part called by {@code findHighestConflict}. The only difference between 
	 * the two is that this method can return the support value of root itself as {@code findHighestConflict} does not.
	 * 
	 * @param currentSearchRoot the root of the subtree to be searched
	 * @param referenceRootLeafSet the leaf set of the root of the reference tree (containing the node in conflict with the 
	 *        searched tree)
	 * @param highestConflictingNode the conflicting node carrying the highest support value, that was found until now (Can
	 *        be {@code null}.)
	 * @param conflictingReferenceLeafSet a leaf set describing the node from the reference tree that is in conflict with the searched
	 *        tree (For {@link AddSupportValuesEdit}: The node in the target document to attach a support value to)
	 * @param completeSearchedLeafSet a leaf set describing the MRCA (in the searched tree) of all shared leaves contained under the 
	 *        conflicting node in the reference tree (These leaves are described by {@code conflictingReferenceLeafSet}.)
	 * @param info information about the node in the source document which contains all terminals of
	 *        {@code targetNode} in its subtree
	 * @return the node with the highest support value found (in the source document)
	 */
	private Node findHighestConflictRecursive(Node currentSearchRoot, LeafSet referenceRootLeafSet, Node highestConflictingNode, 
			LeafSet conflictingReferenceLeafSet, LeafSet completeSearchedLeafSet, NodeBranchDataAdapter searchedSupportAdapter) {
		
		LeafSet currentSearchRootLeafSet = getLeafSet(currentSearchRoot).and(referenceRootLeafSet);
		if ((currentSearchRootLeafSet.containsAnyAndOther(conflictingReferenceLeafSet, false) &&
				currentSearchRootLeafSet.inSubtreeOf(completeSearchedLeafSet, false))  //TODO This condition could be removed if the loop of the first level would be run in the non-recursive method.
				|| (currentSearchRootLeafSet.containsAnyAndOther(conflictingReferenceLeafSet, true) &&
						currentSearchRootLeafSet.inSubtreeOf(completeSearchedLeafSet, true))) {
			
			double currentSupport = Double.NaN;
			if (searchedSupportAdapter.isDecimal(currentSearchRoot)) {
				currentSupport = searchedSupportAdapter.getDecimal(currentSearchRoot);
			}
			else if (searchedSupportAdapter.isString(currentSearchRoot)) {  //TODO Do that only of parameter for parsing numeric values is set.
				try {
					currentSupport = Math2.parseDouble(searchedSupportAdapter.getText(currentSearchRoot));
				}
				catch (NumberFormatException e) {}  // Nothing to do. 
			}
			
			if (!Double.isNaN(currentSupport)) {
				if (highestConflictingNode == null) {
					highestConflictingNode = currentSearchRoot;
				}
				else {
					double previousSupport;
					if (searchedSupportAdapter.isDecimal(highestConflictingNode)) {
						previousSupport = searchedSupportAdapter.getDecimal(highestConflictingNode);
					}
					else {
						previousSupport = Math2.parseDouble(searchedSupportAdapter.getText(highestConflictingNode));  //TODO Do that only of parameter for parsing numeric values is set.
					}
					
					if (previousSupport < currentSupport) {
						highestConflictingNode = currentSearchRoot;
					}
				}
			}
		}
		
		for (int i = 0; i < currentSearchRoot.getChildren().size(); i++) {
			highestConflictingNode = findHighestConflictRecursive(currentSearchRoot.getChildren().get(i), referenceRootLeafSet, 
					highestConflictingNode,	conflictingReferenceLeafSet, completeSearchedLeafSet, searchedSupportAdapter);
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