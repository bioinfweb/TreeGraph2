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
package info.bioinfweb.treegraph.document.undo.nodebranchdata;


import info.bioinfweb.treegraph.document.Label;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.TextElementData;
import info.bioinfweb.treegraph.document.TextLabel;
import info.bioinfweb.treegraph.document.nodebranchdata.IDElementAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.TextLabelAdapter;
import info.bioinfweb.treegraph.document.tools.NodeBranchDataColumnManager;



/**
 * Stores the backup for a line in a node/branch data column. This can either be an instance of {@link TextElementData}
 * or in case of a column referenced by an {@link IDElementAdapter} also a label instance if a label with that ID
 * was present at the linked node. 
 * <p>
 * Labels need to be saved separately because their formats and the concrete reference (possibly stored by subsequent 
 * edits) would not be restored by a {@link TextLabelAdapter}.
 * 
 * @author Ben St&ouml;ver
 * @since 2.4.0
 */
public class NodeBranchDataElementBackup {
	private TextElementData textElementData = null;
	private Label label = null;
	
	
	public NodeBranchDataElementBackup(NodeBranchDataAdapter adapter, Node node) {
	  super();	  
	  backupNode(adapter, node);
  }
	
	
	public boolean hasLabel() {
		return (label != null);
	}
	
	
	public boolean isEmpty() {
		return (textElementData == null) && (label == null);
	}
	
	
	private void backupIDElement(String id, Node node) {
		Object element = NodeBranchDataColumnManager.getElementByID(node, id);
		if (element != null) {
			if (element instanceof Label) {
				label = (Label)element;
				if (label instanceof TextLabel) {
					textElementData = ((TextLabel)label).getData().clone();  // The value of an existing label could be overwritten in the coming edit.
				}
			}
			else {
				textElementData = ((TextElementData)element).clone();
			}
		}
	}
	
	
	private void backupAdapter(NodeBranchDataAdapter adapter, Node node) {
		if (adapter.isDecimal(node)) {
			textElementData = new TextElementData(adapter.getDecimal(node));
		}
		else {
			textElementData = new TextElementData(adapter.getText(node));
		}
	}
	
	
	private void backupNode(NodeBranchDataAdapter adapter, Node node) {
		if (adapter instanceof IDElementAdapter) {
			backupIDElement(((IDElementAdapter)adapter).getID(), node);
		}
		else if (!adapter.isEmpty(node)) {
			backupAdapter(adapter, node);
		}
	}
	
	
	public void restoreNode(NodeBranchDataAdapter adapter, Node node) {
		// Remove possible other elements with the same ID:
		if (adapter instanceof IDElementAdapter) {
			NodeBranchDataColumnManager.removeElementWithID(node, ((IDElementAdapter)adapter).getID());  // adapter.delete() would not delete elements in other columns with the same ID.
		}
		
		// Restore old element:
		if (isEmpty()) {
			adapter.delete(node);  // Node had no value before the backup.
		}
		else { 
			if (hasLabel()) {
				node.getAfferentBranch().getLabels().add(label);
			}
			adapter.setTextElementData(node, textElementData);  // Also restores value of possibly restored label.
		}
	}
}
