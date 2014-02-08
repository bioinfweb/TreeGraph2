/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2014  Ben Stöver, Kai Müller
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


import info.bioinfweb.treegraph.document.Branch;
import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.nodebranchdata.UniqueNameAdapter;
import info.bioinfweb.treegraph.document.undo.topologicalcalculation.AbstractTopologicalCalculationEdit;
import info.bioinfweb.treegraph.document.undo.topologicalcalculation.LeafSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;



/**
 * Document edit that reroots the tree at the root branch of the smallest subtree containing all
 * specified leaf nodes. If there is more than one smallest subtree one of the is selected and 
 * the root branches of the others can be accessed by {@link #getAlternativeRootingPoints()}.
 * <p>
 * Note that is depends on the ordering of elements in {@link HashSet}) which if the alternatives
 * is used. Since that ordering is non-deterministic it might change which subtree is selected
 * in several calls of this edit. Additionally is might depend on the order of the specified leaf
 * nodes.
 * 
 * @author Ben St&ouml;ver
 */
public class RerootByLeafSetEdit extends AbstractTopologicalCalculationEdit {
	private static class BranchingSubtreesResult {
		public Branch first; 
		public List<Branch> alternatives = new ArrayList<Branch>(4);
		public int leafCount = -1;  // Must be lower than 0. (see calculateRootingPoint() for details.)
	}
	
	
	private Branch rootingPoint;
	private List<Node> leafs;
	private LeafSet selectedLeafs;
	private HashSet<Node> nodesOnPath = new HashSet<Node>();
	private String warningText = null;
	private Collection<Branch> alternativeRootingPoints = new ArrayList<Branch>(8);  // Initialized with 8, since there will never be many.
	
	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param document - the document that contains the tree to be rerooted 
	 * @param leafs - a list of leaf nodes contained in {@code document} that shall all be contained in one 
	 *        of the subtrees of the future root
	 * @throws IllegalArgumentException - if one or more of the specified nodes is not a leaf 
	 */
	public RerootByLeafSetEdit(Document document, List<Node> leafs) {
		super(document, UniqueNameAdapter.getSharedInstance(), false);
		this.leafs = leafs;  // Since leafs are only compared by their unique names, it is not necessary to call findEquivalent() here.
		rootingPoint = calculateRootingPoint();
		nodesOnPath = null;  // Save memory, since this set will not be needed anymore after calculateRootingPoint() is finished.
	}
	
	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param document - the document that contains the tree to be rerooted 
	 * @param leafs - an array of leaf nodes contained in {@code document} that shall all be contained in one 
	 *        of the subtrees of the future root
	 * @throws IllegalArgumentException - if one or more of the specified nodes is not a leaf 
	 */
	public RerootByLeafSetEdit(Document document, Node[] leafs) {
		super(document, UniqueNameAdapter.getSharedInstance(), false);
		this.leafs = Arrays.asList(leafs);  // Since leafs are only compared by their unique names, it is not necessary to call findEquivalent() here.
		rootingPoint = calculateRootingPoint();
		nodesOnPath = null;  // Save memory, since this set will not be needed anymore after calculateRootingPoint() is finished.
	}
	
	
	private void createSelectedLeafsSet() {
		selectedLeafs = new LeafSet(getLeafCount());
		Iterator<Node> iterator = leafs.iterator();
		while (iterator.hasNext()) {
			selectedLeafs.setChild(getLeafIndex(UniqueNameAdapter.getSharedInstance().toTextElementData(iterator.next())), true);
		}
	}
	
	
	/**
	 * Marks the upward part of the path from the specified leaf to the other selected leafs.
	 * 
	 * @param node - one of the leaf node defining the rooting point (outgroup)
	 */
	private void markPath(Node node) {
		node = node.getParent();  // The leaf node should not be added to the path
		while (node != null && !nodesOnPath.contains(node) && !getLeafSet(node).containsAll(selectedLeafs)) {
			nodesOnPath.add(node);
			node = node.getParent();
		}
		if ((node != null) && !nodesOnPath.contains(node)) {  // Mark highest node of the path (getLeafSet(node).containsAll(selectedLeafs) == true)
			nodesOnPath.add(node);
		}
	}
	
	
	/**
	 * Searches for the subtree branching upwards or downwards from this node which contains the maximal number
	 * of leaf nodes and contains no marked path node.
	 * 
	 * @param root - the node from where the subtrees branch
	 * @return an instance of {@link BranchingSubtreesResult}. If {@code root} is contained in the result list,
	 *         than the upward subtree is one of the largest branching subtrees.
	 */
	private BranchingSubtreesResult largestBranchingSubtrees(Node root) {
		BranchingSubtreesResult result = new BranchingSubtreesResult();
		if (root.hasParent() && !nodesOnPath.contains(root.getParent())) {
			result.alternatives.add(root.getAfferentBranch());
			result.leafCount = getLeafSet(root).complement().childCount();
		}
		
		Iterator<Node> iterator = root.getChildren().iterator();
		while (iterator.hasNext()) {
			Node child = iterator.next();
			if (!nodesOnPath.contains(child)) {
				int childLeafCount = getLeafSet(child).childCount();
				if (childLeafCount > result.leafCount) {  // Replace current result(s).
					result.alternatives.clear();
					result.first = child.getAfferentBranch();
					result.leafCount = childLeafCount;
				}
				else if (childLeafCount == result.leafCount) {  // More than one result was found.
					result.alternatives.add(child.getAfferentBranch());
				}
			}
		}
		return result;
	}


	/**
	 * Searches for a branch in the tree where the new root shall be located.
	 */
	private Branch calculateRootingPoint() {
		addLeafSets(document.getTree().getPaintStart(), UniqueNameAdapter.getSharedInstance());
		createSelectedLeafsSet();
		
		// Mark paths:
		Iterator<Node> iterator = leafs.iterator();
		while (iterator.hasNext()) {
			Node leaf = iterator.next();
			if (leaf.isLeaf()) {
				markPath(leaf);
			}
			else {
				throw new IllegalArgumentException("The specified node with the unique name " + 
			      leaf.getUniqueName() + " is not a leaf.");
			}
		}
		
		// Find largest subtree that branches from the marked paths:
		alternativeRootingPoints.clear();  // Currently unnecessary since this method is only called once.
		Branch result = null;
		int maxLeafCount = 0;  // Should be higher than the initial value of subtrees.leafCount.
		iterator = nodesOnPath.iterator();
		while (iterator.hasNext()) {
			BranchingSubtreesResult subtrees = largestBranchingSubtrees(iterator.next());
			if (subtrees.leafCount > maxLeafCount) {
				alternativeRootingPoints.clear();
				result = subtrees.first;
				alternativeRootingPoints.addAll(subtrees.alternatives);
				maxLeafCount = subtrees.leafCount;
			}
			else if (subtrees.leafCount == maxLeafCount) {
				alternativeRootingPoints.add(subtrees.first);
				alternativeRootingPoints.addAll(subtrees.alternatives);
			}
		}
		
		return result;  // The resulting branch is located in the old tree of ComplexDocumentEdit
	}
	
	
	public Collection<Branch> getAlternativeRootingPoints() {
		return Collections.unmodifiableCollection(alternativeRootingPoints);
	}


	/**
	 * Returns a warning text, if the last call of {@link #redo()} produced warnings.
	 * @return the warning text or {@code null} if no warning occurred
	 */
	public String getWarningText() {
		return warningText;
	}

  
	public boolean hasWarnings() {
		return warningText != null;
	}
	
	
	@Override
	public String getPresentationName() {
		return "Reroot tree by leaf (taxon) set";
	}


	@Override
  protected void performRedo() {
		warningText = RerootEdit.reroot(document.getTree(), rootingPoint);
  }
}
