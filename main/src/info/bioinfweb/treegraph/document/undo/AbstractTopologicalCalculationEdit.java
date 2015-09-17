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
package info.bioinfweb.treegraph.document.undo;


import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.change.DocumentChangeType;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.topologicalcalculation.LeafSet;
import info.bioinfweb.treegraph.document.topologicalcalculation.NodeInfo;
import info.bioinfweb.treegraph.document.topologicalcalculation.TopologicalCalculator;



/**
 * Implements basic functionalities for all edits that depend on calculations based on the tree topology
 * and therefore need the tree nodes to be decorated with information in their subtrees. 
 * 
 * @author Ben St&ouml;ver
 */
public abstract class AbstractTopologicalCalculationEdit extends ComplexDocumentEdit {
	public static final String KEY_LEAF_REFERENCE = AbstractTopologicalCalculationEdit.class.getName() + ".LeafSet";
	
	protected TopologicalCalculator topologicalCalculator = null;

	
	public AbstractTopologicalCalculationEdit(Document document, DocumentChangeType changeType,
			NodeBranchDataAdapter targetLeafsAdapter, boolean processRooted) {
	
		super(document, changeType);
		topologicalCalculator = new TopologicalCalculator(document, targetLeafsAdapter, processRooted, KEY_LEAF_REFERENCE, new ImportTextElementDataParameters());
	}

	
	public TopologicalCalculator getTopologicalCalculator() {
		return topologicalCalculator;
	}

	
	/**
	 * Checks if both the loaded and the imported tree contain exactly the same terminals.
	 * 
	 * @return an error message, if the terminal nodes are not identical or <code>null</code> if they are
	 */
	protected String compareLeafs(Document src) {
		return topologicalCalculator.compareLeafs(src);
	}


	/**
	 * Returns the leaf field attribute of <code>node</code> if it has one attached. If not an according object
	 * is created first and than returned.
	 * 
	 * @param node - the node from which the leaf field attribute shall be returned or created. 
	 */
	protected LeafSet getLeafSet(Node node) {
		return topologicalCalculator.getLeafSet(node);
	}
	
	
	protected int getLeafIndex(String value) {
		return topologicalCalculator.getLeafIndex(value);
	}
	
	
	protected int getLeafCount() {
		return topologicalCalculator.getLeafCount();
	}
	

	/**
	 * Adds a boolean set which indicates the leafs located under <code>root</code> to
	 * the attribute map of <code>root</code>.`
	 * 
	 * @param root - the root of the subtree
	 */
	protected void addLeafSets(Node root, NodeBranchDataAdapter adapter) {
		topologicalCalculator.addLeafSets(root, adapter);
	}	
	
	
	protected NodeInfo findSourceNodeWithAllLeafs(Node sourceRoot, LeafSet targetLeafs) {
		return topologicalCalculator.findSourceNodeWithAllLeafs(sourceRoot, targetLeafs);
	}
	
	
//	protected Node findHighestConflictingNode(Node root, Node targetNode, NodeInfo info) {
//		return topologicalCalculator.findHighestConflictingNode(root, targetNode, info);
//	}
}