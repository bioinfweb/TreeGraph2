/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2011  Ben Stöver, Kai Müller
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
import java.util.List;
import java.util.Vector;

import javax.swing.JOptionPane;

import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.TextElementData;
import info.bioinfweb.treegraph.document.nodebranchdata.BranchLengthAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.HiddenBranchDataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.HiddenNodeDataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeNameAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.TextElementDataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.TextLabelAdapter;
import info.bioinfweb.treegraph.document.undo.ComplexDocumentEdit;
import info.bioinfweb.treegraph.document.undo.file.addsupportvalues.LeafField;
import info.bioinfweb.treegraph.document.undo.file.addsupportvalues.NodeInfo;
import info.bioinfweb.treegraph.gui.mainframe.MainFrame;



public class AddSupportValuesEdit extends ComplexDocumentEdit {
	public static final String SUPPORT_NAME = "Support";
	public static final String CONFLICT_NAME = "Conflict";
	public static final DecimalFormat SUPPORT_DECIMAL_FORMAT = 
		  new DecimalFormat("#.#######");
	public static final DecimalFormat CONFLICT_DECIMAL_FORMAT =
		  new DecimalFormat("[#.#######];[-#.#######]");
	public static final String KEY_LEAF_REFERENCE = 
		  AddSupportValuesEdit.class.getName() + ".LeafList";
	public static final int MAX_TERMINAL_ERROR_COUNT = 10;
	public static final NodeNameAdapter SOURCE_LEAFS_ADAPTER = NodeNameAdapter.getSharedInstance(); 
	
	
	private Document src = null;
	
	/** The column that contains the terminal identifiers in the target document (usually nodes names) */
	private TextElementDataAdapter targetLeafsAdapter = null;
	
	/** The node/branch data to be imported (node names or branch lengths) */
	private NodeBranchDataAdapter sourceAdapter = null;
	
	/** The node/branch data column to write imported support values to */
	private TextElementDataAdapter supportAdapter = null;
	
	/** The node/branch data column to write imported conflict values to */
	private TextElementDataAdapter conflictAdapter = null;
	
	private Vector<TextElementData> leafValues = new Vector<TextElementData>();
	
	private boolean processRooted;
	

	public enum TargetType {
		LABEL, HIDDEN_NODE_DATA, HIDDEN_BRANCH_DATA;
	}


	public AddSupportValuesEdit(Document document, Document src, 
			TextElementDataAdapter terminalsAdapter, TargetType targetType, String idPrefix,
			NodeBranchDataAdapter sourceAdapter, boolean processRooted) {
		
		super(document);
		this.src = src;
		this.targetLeafsAdapter = terminalsAdapter; 	//TODO Prüfen, ob terminalsAdapter nur Decimals als innere Knoten enthält
		this.sourceAdapter = sourceAdapter;
		this.processRooted = processRooted;
		
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
	 * @param document - the document to add the support values to
	 * @param src - the document to obtain the support values frim
	 * @param terminalsAdapter - the adapter to obtain the terminal names from the target
	 *        document
	 * @param targetType - the type of node data to store the new support values
	 * @param idPrefix - the prefix of the IDs that will be given to the new support 
	 *        (and conflict values)
	 * @param importNodeNames - If <code>true</code> the internal node names are imported as
	 *        support valus, otherwise the internal branch lengths are used.
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
		addLeafList(leafValues, document.getTree().getPaintStart(), targetLeafsAdapter);  // Source und target sollten das selbe Ergebnis liefern.
    String errorMsg = compareLeafs();
    if (errorMsg == null) {
  		addLeafFields(src.getTree().getPaintStart(), SOURCE_LEAFS_ADAPTER);
      addLeafFields(document.getTree().getPaintStart(), targetLeafsAdapter);
  		processSubtree(document.getTree().getPaintStart());
    }
    else {
			JOptionPane.showMessageDialog(MainFrame.getInstance(), errorMsg, 
					"Incompatible trees", JOptionPane.ERROR_MESSAGE);
    }
	}
	
	
	/**
	 * Fills the specified list with the values of all leafs under <code>root</code>. 
	 * @param list
	 * @param root
	 * @param adapter
	 */
	private void addLeafList(List<TextElementData> list, Node root, TextElementDataAdapter adapter) {
		if (root.isLeaf()) {
			list.add(adapter.getData(root));
		}
		else {
			for (int i = 0; i < root.getChildren().size(); i++) {
				addLeafList(list, root.getChildren().get(i), adapter);
			}
		}
	}
	
	
	private String compareLeafs() {
		Vector<TextElementData> sourceLeafValues = new Vector<TextElementData>();
		addLeafList(sourceLeafValues, src.getTree().getPaintStart(), SOURCE_LEAFS_ADAPTER);
		if (leafValues.size() != sourceLeafValues.size()) {
			return "The selected tree has different number of terminals than " +
				"the opened document. No support values were added.";
		}
		else {
			String errorMsg = "";
			int errorCount = 0;
			for (int i = 0; i < sourceLeafValues.size(); i++) {
				if (getLeafIndex(sourceLeafValues.get(i)) == -1) {
					if (errorCount < MAX_TERMINAL_ERROR_COUNT) {
						if (!errorMsg.equals("")) {
							errorMsg += ",\n";
						}
						errorMsg += "\"" + sourceLeafValues.get(i) + "\"";
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
	
	
	private LeafField getLeafField(Node node) {
		if (node.getAttributeMap().get(KEY_LEAF_REFERENCE) == null) {
			int size = leafValues.size();
			if (processRooted) {
				size++;
			}
			LeafField field = new LeafField(size);
			node.getAttributeMap().put(KEY_LEAF_REFERENCE, field);
		}
		return (LeafField)node.getAttributeMap().get(KEY_LEAF_REFERENCE);
	}
	
	
	private int getLeafIndex(TextElementData value) {
		int pos = 0;
		while ((pos < leafValues.size()) && !value.equals(leafValues.get(pos))) {
			pos++;
		}
		if (pos < leafValues.size()) {
			return pos;
		}
		else {
			return -1;
		}
	}
	
	
	/**
	 * Adds a boolean field which indicates the leafs located under <code>root</code> to
	 * the attribute map of <code>root</code>.
	 * @param root - the root of the subtree
	 */
	private void addLeafFields(Node root, TextElementDataAdapter adapter) {
		if (!root.isLeaf()) {
			LeafField field = getLeafField(root);
			for (int i = 0; i < root.getChildren().size(); i++) {
				Node child = root.getChildren().get(i);
				addLeafFields(child, adapter);
				if (child.isLeaf()) {
					field.setChild(getLeafIndex(adapter.getData(child)), true);
				}
				else {
					field.addField(getLeafField(child));
				}
			}
		}
	}
	
	
  private NodeInfo findSourceNodeWithAllLeafs(Node sourceRoot, LeafField targetLeafs) {
		int additionalCount = targetLeafs.compareTo(getLeafField(sourceRoot), false);
		boolean downwards = additionalCount != -1;
		if (!downwards) {
			additionalCount = targetLeafs.compareTo(getLeafField(sourceRoot), true);
		}
  	NodeInfo result = new NodeInfo(sourceRoot, additionalCount, downwards);
		for (int i = 0; i < sourceRoot.getChildren().size(); i++) {
			NodeInfo childResult = findSourceNodeWithAllLeafs(sourceRoot.getChildren().get(i), targetLeafs);
			if ((childResult.getAdditionalCount() != -1) && 
					((childResult.getAdditionalCount() < result.getAdditionalCount()) || 
							(result.getAdditionalCount() == -1))) {
				
				result = childResult;
			}
		}
		return result;
	}
	
	
	private double getSupportValue(Node node) {
		return sourceAdapter.getDecimal(node);
	}
	

	/**
	 * This method is the recursive part called by <code>findHighestConflict</code>.
	 * The only difference between the two is that this method can return the support 
	 * value of root itsself as <code>findHighestConflict</code> does not.
	 * @param root - the root of the subtree to be searched (a node in the source document)
	 * @param highest - the initial support value
	 * @param targetNode - the node in the target document to attach a support value to
	 * @param info - information about the node in the source document which contains all terminals of
	 *        <code>targetNode</code> in its subtree 
	 * @return the node with the highest support value found (in the source document)
	 */
	private double findHighestConflict(Node root, double highest, Node targetNode, NodeInfo info) {
		if ((getLeafField(root).containsAnyAndOther(getLeafField(targetNode), false) &&
				 getLeafField(root).inSubtreeOf(getLeafField(info.getNode()), false))
				|| (getLeafField(root).containsAnyAndOther(getLeafField(targetNode), true) &&
						getLeafField(root).inSubtreeOf(getLeafField(info.getNode()), true))) {
			
			double value = getSupportValue(root);
			if (!Double.isNaN(value)) {
				if (Double.isNaN(highest)) {
					highest = value;
				}
				else {
					highest = Math.max(highest, value);
				}
			}
		}
		
		for (int i = 0; i < root.getChildren().size(); i++) {
			double value = findHighestConflict(root.getChildren().get(i), highest, targetNode, info);
			if (!Double.isNaN(value)) {
				if (Double.isNaN(highest)) {
					highest = value;
				}
				else {
					highest = Math.max(highest, value);
				}
			}
		}
		
		return highest;
	}

	
	/**
	 * Finds the support or conflict values in the source document.
	 * @param targetRoot - the root of the subtree to add support values to (a node of the 
	 *        target document)
	 */
	private void processSubtree(Node targetRoot) {
		if (!targetRoot.isLeaf()) {
			if (!(targetRoot.hasParent() && !targetRoot.getParent().hasParent() && !processRooted && 
					(targetRoot.getParent().getChildren().size() == 2) && targetRoot.isLast())) {  // Verhindern, dass ungewurzelte Bäume mehrmals Werte für semantisch gleichen Ast erhalten.
				
				NodeInfo bestSourceNode = findSourceNodeWithAllLeafs(src.getTree().getPaintStart(), 
						getLeafField(targetRoot));
				if (bestSourceNode.getAdditionalCount() == 0) {  // support found
					if (sourceAdapter.isDecimal(bestSourceNode.getNode())) {  // wenn dort ein Wert existiert
	  				supportAdapter.setDecimal(targetRoot, sourceAdapter.getDecimal(bestSourceNode.getNode()));  // String-Werte können nicht auftreten
					}
				}
				else if (bestSourceNode.getAdditionalCount() == -1) {
					System.out.println("-1 RETURNED");  // Dürfte nicht passieren. -> Testen! 
				}
				else {  // conflict found
					double value = findHighestConflict(src.getTree().getPaintStart(), Double.NaN, targetRoot, bestSourceNode);
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
	
	
	public String getPresentationName() {
		return "Add more support values";
	}
}