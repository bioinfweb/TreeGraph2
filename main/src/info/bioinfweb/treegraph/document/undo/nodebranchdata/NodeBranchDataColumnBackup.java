/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2015-2016  Ben Stöver, Sarah Wiechers
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
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;

import java.util.ArrayList;
import java.util.List;



/**
 * Used to backup a node/branch data column before an edit operation.
 * 
 * @author Ben St&ouml;ver
 * @since 2.0.24
 */
public class NodeBranchDataColumnBackup {
  private List<NodeBranchDataElementBackup> list = null;
  
  
	public NodeBranchDataColumnBackup(NodeBranchDataAdapter adapter, Node root) {
		super();
		list = new ArrayList<NodeBranchDataElementBackup>();
		backupSubtree(root, adapter);
	}
  
  
	private void backupSubtree(Node root, NodeBranchDataAdapter adapter) {
		list.add(new NodeBranchDataElementBackup(adapter, root));
		
		for (int i = 0; i < root.getChildren().size(); i++) {
			backupSubtree(root.getChildren().get(i), adapter);
		}
	}
	
	
	private int restoreSubtree(Node root, int index) {
		list.get(index).restoreNode(root);
		index++;
		
		for (int i = 0; i < root.getChildren().size(); i++) {
			index = restoreSubtree(root.getChildren().get(i), index);			
		}
		return index;
	}
	
	
	/**
	 * Restores the previous values of the specified column if it previously existed. Otherwise the 
	 * specified column is deleted.
	 * 
	 * @param root
	 */
	public void restore(Node root) {
		restoreSubtree(root, 0);
	}
}