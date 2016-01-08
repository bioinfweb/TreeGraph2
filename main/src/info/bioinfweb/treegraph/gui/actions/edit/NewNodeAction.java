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
package info.bioinfweb.treegraph.gui.actions.edit;


import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;

import info.bioinfweb.treegraph.document.Branch;
import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.format.FormatUtils;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.undo.edit.InsertSiblingEdit;
import info.bioinfweb.treegraph.document.undo.edit.InsertSubtreeEdit;
import info.bioinfweb.treegraph.gui.actions.DocumentAction;
import info.bioinfweb.treegraph.gui.mainframe.MainFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeInternalFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeSelection;



/**
 * @author Ben St&ouml;ver
 */
public class NewNodeAction extends DocumentAction {
	public NewNodeAction(MainFrame mainFrame) {
		super(mainFrame);
		putValue(Action.NAME, "New node"); 
	  putValue(Action.MNEMONIC_KEY, KeyEvent.VK_N);
		putValue(Action.ACCELERATOR_KEY, 
				KeyStroke.getKeyStroke('N', 
						Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() + InputEvent.SHIFT_MASK));
	}
	
	
	@Override
	public void setEnabled(Document document, TreeSelection selection, NodeBranchDataAdapter tableAdapter) {
		setEnabled((document != null && document.getTree().isEmpty()) ||
				(oneElementSelected(selection) && 
						(selection.containsType(Node.class) || selection.containsType(Branch.class))));
	}
	
	
	/**
	 * Inserts a new subelement as the last subelement of the given parent.
	 * This method should be used for inserting newly generated elements and 
	 * paste-operations of single taxa, labels or nodes with or without whole subtrees.
	 * If <code>parent</code> is <code>null</code> the given element will set as tree-root.
	 * If the tree-root is already set, an <code>IllegalArgumentException</code> will be
	 * thrown.
	 * @param parent the parent node under which the subelement should be inserted
	 * @param root the element to insert or the root element of the subtree to insert
	 */
	public static boolean insertSubtree(Document document, Node parent, Node root) {
		int index = 0;
		if (parent != null) {
			index = parent.getChildren().size();
		}
		
		return insertSubtree(document, parent, root, index);
	}
	
	
	/**
	 * Inserts a new subelement at the given position.
	 * This method should be used for inserting newly generated elements and 
	 * paste-operations of single elements or whole subtrees at a defined position. The 
	 * position must not be greater then the current number of subelements. Note that 
	 * labels can not be inserted an defined position because their position is 
	 * irrelevant.
	 * @param parent the parent node under which the subelement should be inserted
	 * @param pos the position where the new element or subtree shall be inserted
	 * @param root the element to insert or the root element of the subtree to insert (can't be label)
	 */
	public static boolean insertSubtree(Document document, Node parent, Node root, int index) {
		if ((parent == null) && !document.getTree().isEmpty()) {
			return false;
		}
		else {
			InsertSubtreeEdit edit = new InsertSubtreeEdit(document, parent, root, index);
			document.executeEdit(edit);
			return true;
		}
	}
	
	
	@Override
	protected void onActionPerformed(ActionEvent e, TreeInternalFrame frame) {
		if (frame.getTreeViewPanel().getSelection().containsType(Branch.class)) {
			Node parent = ((Branch)frame.getTreeViewPanel().getSelection().first()).getTargetNode();
			frame.getDocument().executeEdit(new InsertSiblingEdit(frame.getDocument(),
					parent, FormatUtils.createNode(parent)));
		}
		else if (frame.getDocument().getTree().isEmpty()) {
			Node node = FormatUtils.createNode(frame.getDocument());
			if (insertSubtree(frame.getDocument(), null, node)) {  
				frame.getTreeViewPanel().getSelection().set(node);
			}
		}
		else {
			Node parent = (Node)frame.getTreeViewPanel().getSelection().first();
			Node node = FormatUtils.createNode(parent);
			if (insertSubtree(frame.getDocument(), parent, node)) {  
				frame.getTreeViewPanel().getSelection().set(node);
			}
		}
	}
}