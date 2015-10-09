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
package info.bioinfweb.treegraph.document.undo.file;


import java.text.DecimalFormat;

import javax.swing.JOptionPane;

import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.change.DocumentChangeType;
import info.bioinfweb.treegraph.document.nodebranchdata.BranchLengthAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.HiddenBranchDataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.HiddenNodeDataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeNameAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.AbstractTextElementDataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.TextElementDataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.TextLabelAdapter;
import info.bioinfweb.treegraph.document.topologicalcalculation.NodeInfo;
import info.bioinfweb.treegraph.document.topologicalcalculation.TopologicalCalculator;
import info.bioinfweb.treegraph.document.undo.AbstractTopologicalCalculationEdit;
import info.bioinfweb.treegraph.gui.mainframe.MainFrame;



public class AddSupportValuesEdit extends AbstractTopologicalCalculationEdit {
	public static final String SUPPORT_NAME = "Support";
	public static final String CONFLICT_NAME = "Conflict";
	public static final DecimalFormat SUPPORT_DECIMAL_FORMAT = 
		  new DecimalFormat("#.#######");
	public static final DecimalFormat CONFLICT_DECIMAL_FORMAT =
		  new DecimalFormat("[#.#######];[-#.#######]");
	
	
	/** The document from which support values are imported */
	private Document sourceDocument = null;
	
	/** The node/branch data to be imported (node names or branch lengths). */
	private NodeBranchDataAdapter sourceAdapter = null;
	
	/** The node/branch data column to write imported support values to. */
	private AbstractTextElementDataAdapter supportAdapter = null;
	
	/** The node/branch data column to write imported conflict values to. */
	private AbstractTextElementDataAdapter conflictAdapter = null;
	
	public enum TargetType {
		LABEL, HIDDEN_NODE_DATA, HIDDEN_BRANCH_DATA;
	}


	public AddSupportValuesEdit(Document targetDocument, Document sourceDocument, 
			TextElementDataAdapter terminalsAdapter, TargetType targetType, String idPrefix,
			NodeBranchDataAdapter sourceAdapter, boolean processRooted) {
		
		super(targetDocument, DocumentChangeType.TOPOLOGICAL_BY_RENAMING, terminalsAdapter, processRooted);  // Topological relevance only if the default leaves adapter ID is affected. 
		this.sourceDocument = sourceDocument;
		this.sourceAdapter = sourceAdapter;
		
		switch (targetType) {
			case LABEL:
				supportAdapter = new TextLabelAdapter(idPrefix + SUPPORT_NAME, SUPPORT_DECIMAL_FORMAT);
				conflictAdapter = new TextLabelAdapter(idPrefix + CONFLICT_NAME, CONFLICT_DECIMAL_FORMAT);
				break;
			case HIDDEN_BRANCH_DATA:
				supportAdapter = new HiddenBranchDataAdapter(idPrefix + SUPPORT_NAME);
				conflictAdapter = new HiddenBranchDataAdapter(idPrefix + CONFLICT_NAME);
				break;
			case HIDDEN_NODE_DATA:
				supportAdapter = new HiddenNodeDataAdapter(idPrefix + SUPPORT_NAME);
				conflictAdapter = new HiddenNodeDataAdapter(idPrefix + CONFLICT_NAME);
				break;
		}
	}
	
	
	/**
	 * Tests if all internal nodes in the subtree under <code>root</code> contain 
	 * decimal values.
	 * 
	 * @param root - the root of the subtree to be checked
	 * @param adapter - the adapter to obtain the data from the nodes
	 * @return <code>true</code> if only decimal values are found
	 */
	private static boolean internalsAreDecimal(Node root, NodeBranchDataAdapter adapter) {
		if ((!root.isLeaf()) && !adapter.isDecimal(root)) {
			return false;
		}
		
		for (int i = 0; i < root.getChildren().size(); i++) {
			if (!internalsAreDecimal(root.getChildren().get(i), adapter)) {
				return false;
			}
		}
		
		return true;
	}
	
	
	/**
	 * Returns a new instance of <code>AddSupportValuesEdit</code> or <code>null</code>
	 * if the given source document contains internal node names that are not decimal.
	 * 
	 * @param document - the document to add the support values to
	 * @param src - the document to obtain the support values from
	 * @param terminalsAdapter - the adapter to obtain the terminal names from the target
	 *        document
	 * @param targetType - the type of node data to store the new support values
	 * @param idPrefix - the prefix of the IDs that will be given to the new support 
	 *        (and conflict values)
	 * @param importNodeNames - If <code>true</code> the internal node names are imported as
	 *        support values, otherwise the internal branch lengths are used.
	 * @param processRooted - defines whether the two specified trees shall be merged rooted or not 
	 *        (no matter if the specified documents are rooted or not)
	 * @return a new instance of <code>AddSupportValuesEdit</code> or <code>null</code>
	 */
	public static AddSupportValuesEdit createInstance(Document document, Document src, 
			TextElementDataAdapter terminalsAdapter, TargetType targetType, String idPrefix, 
			boolean importNodeNames, boolean processRooted) {
		
    NodeBranchDataAdapter sourceAdapter;
		if (importNodeNames) {		
			sourceAdapter = new NodeNameAdapter();
		}
		else {
			sourceAdapter = new BranchLengthAdapter();
		}
		
		if (internalsAreDecimal(src.getTree().getPaintStart(), sourceAdapter) || true) {  //TODO Diese Bedingung ist nicht wirklich sinnvoll! Was war hier eigentlich geplant?
			return new AddSupportValuesEdit(document, src, terminalsAdapter, targetType, 
					idPrefix, sourceAdapter, processRooted);
		}
		else {
			return null;
		}
	}
	
	
	@Override
	protected void performRedo() {
    String errorMsg = getTopologicalCalculator().compareLeaves(sourceDocument);
    if (errorMsg == null) {  // The terminal nodes of both trees are identical.
  		getTopologicalCalculator().addLeafSets(sourceDocument.getTree().getPaintStart(), TopologicalCalculator.SOURCE_LEAVES_ADAPTER);
  		getTopologicalCalculator().addLeafSets(getDocument().getTree().getPaintStart(), getTargetLeavesAdapter());
  		processSubtree(getDocument().getTree().getPaintStart());
    }
    else {
			JOptionPane.showMessageDialog(MainFrame.getInstance(), errorMsg, 
					"Incompatible trees", JOptionPane.ERROR_MESSAGE);
    }
	}

	
	private boolean checkOtherPaintStartBranch(Node sourceNode) {
		return !getTopologicalCalculator().isProcessRooted() &&  // no root node between the two branches 
				sourceNode.hasParent() && !sourceNode.getParent().hasParent() &&  // the source node is attached to the paint start   
				(sourceNode.getParent().getChildren().size() == 2);  // no polytomy at the paint start
	}
	
	
	/**
	 * Finds the support or conflict values in the source document.
	 * 
	 * @param targetRoot - the root of the subtree to add support values to (a node of the 
	 *        target document)
	 */
	private void processSubtree(Node targetRoot) {
		if (!targetRoot.isLeaf()) {
			if (!(targetRoot.hasParent() && !targetRoot.getParent().hasParent() && !getTopologicalCalculator().isProcessRooted() && 
					(targetRoot.getParent().getChildren().size() == 2) && targetRoot.isLast())) {  // Check if the current node is linked to the paint start which does not represent a root and the other linked branch already carries the same support value.
				
				NodeInfo bestSourceNode = getTopologicalCalculator().findSourceNodeWithAllLeaves(sourceDocument.getTree(), 
								getTopologicalCalculator().getLeafSet(targetRoot));
				if (bestSourceNode.getAdditionalCount() == 0) {  // support found
  				if (sourceAdapter.isDecimal(bestSourceNode.getNode())) {  // if no value exists there
	  				supportAdapter.setDecimal(targetRoot, sourceAdapter.getDecimal(bestSourceNode.getNode()));  // Only decimal values can appear here.
					}
  				else if (checkOtherPaintStartBranch(bestSourceNode.getNode())) {
						int index = 1;
						if (bestSourceNode.getNode().isLast()) {
							index = 0;
						}
						Node other = bestSourceNode.getNode().getParent().getChildren().get(index);
						if (sourceAdapter.isDecimal(other)) {
		  				supportAdapter.setDecimal(targetRoot, sourceAdapter.getDecimal(other));  // Only decimal values can appear here.
						}
  				}
				}
				else if (bestSourceNode.getAdditionalCount() == -1) {
					throw new InternalError("-1 RETURNED");  // Should not happen.
				}
				else {  // conflict found
					double value = getSupportValue(getTopologicalCalculator().findHighestConflict(sourceDocument.getTree(), 
							getDocument().getTree(), bestSourceNode.getNode(), getTopologicalCalculator().getLeafSet(targetRoot), 
							getTopologicalCalculator().getLeafSet(bestSourceNode.getNode()), sourceAdapter));
					if (!Double.isNaN(value) && (value != 0)) {
	  				conflictAdapter.setDecimal(targetRoot, value);
					}
				}
			}
			for (int i = 0; i < targetRoot.getChildren().size(); i++) {
				processSubtree(targetRoot.getChildren().get(i));
			}
		}
	}	
	
	
	public double getSupportValue(Node node) {
		return sourceAdapter.getDecimal(node);
	}
	
	
	public String getPresentationName() {
		return "Add more support values";
	}
}