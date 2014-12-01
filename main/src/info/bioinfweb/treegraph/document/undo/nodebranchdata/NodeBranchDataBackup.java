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
package info.bioinfweb.treegraph.document.undo.nodebranchdata;


import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.TextElementData;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.undo.edit.DeleteColumnEdit;

import java.util.List;
import java.util.Vector;



/**
 * Used to backup a node/branch data column before an edit operation.
 * 
 * @author Ben St&ouml;ver
 * @since 2.0.24
 */
public class NodeBranchDataBackup {
	private NodeBranchDataAdapter adapter;
  private List<TextElementData> list = null;
  
  
	public NodeBranchDataBackup(NodeBranchDataAdapter adapter, Node root) {
		super();
		this.adapter = adapter;
		if (!adapter.isNewColumn()) {
			list = new Vector<TextElementData>();
			backupSubtree(root);
		}
	}
  
  
	private void backupSubtree(Node root) {
		TextElementData data = null;
		if (!adapter.isEmpty(root)) {
			if (adapter.isDecimal(root)) {
				data = new TextElementData(adapter.getDecimal(root));
			}
			else {
				data = new TextElementData(adapter.getText(root));
			}
		}
		list.add(data);
		
		for (int i = 0; i < root.getChildren().size(); i++) {
			backupSubtree(root.getChildren().get(i));
		}
	}
	
	
	private int restoreSubtree(Node root, int index) {
		TextElementData data = list.get(index);
		if (data != null) {
			if (data.isDecimal()) {
				adapter.setDecimal(root, data.getDecimal());
			}
			else {
				adapter.setText(root, data.getText());
			}
		}
		else {
			adapter.delete(root);
		}
		index++;
		
		for (int i = 0; i < root.getChildren().size(); i++) {
			index = restoreSubtree(root.getChildren().get(i), index);			
		}
		return index;
	}
	
	
	/**
	 * Restores the previous values of the specified column if it previously exsited. Otherwise the 
	 * specified column is deleted. 
	 * @param root
	 */
	public void restore(Node root) {
		if (adapter.isNewColumn()) {
			DeleteColumnEdit.deleteSubtree(root, adapter);
		}
		else {
			restoreSubtree(root, 0);
		}
	}
}