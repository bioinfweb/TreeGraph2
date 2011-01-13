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
package info.bioinfweb.treegraph.gui.actions.edit;


import info.bioinfweb.treegraph.document.Branch;
import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.undo.edit.MoveSubtreeEdit;
import info.bioinfweb.treegraph.gui.mainframe.MainFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeInternalFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeSelection;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import javax.swing.Action;
import javax.swing.KeyStroke;



public class MoveSubtreeDownAction extends MoveSubtreeAction{
	public MoveSubtreeDownAction(MainFrame mainFrame) {
		super(mainFrame);
		putValue(Action.NAME, "Move subtree down"); 
	  putValue(Action.MNEMONIC_KEY, KeyEvent.VK_M);
		putValue(Action.SHORT_DESCRIPTION, "Move subtree down"); 
		putValue(Action.ACCELERATOR_KEY, 
				KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 
						Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
	  loadSymbols("MoveDown");
	}


	@Override
	public void setEnabled(Document document, TreeSelection selection, NodeBranchDataAdapter tableAdapter) {
		boolean enabled = oneElementSelected(selection) && 
		    (selection.containsType(Node.class) || selection.containsType(Branch.class));
		if (enabled) {
			enabled = !getSelectedNode(selection).isLast();
		}
		setEnabled(enabled);
	}


	@Override
	protected void onActionPerformed(ActionEvent e, TreeInternalFrame frame) {
		frame.getDocument().executeEdit(new MoveSubtreeEdit(frame.getDocument(), 
				getSelectedNode(frame.getTreeViewPanel().getSelection()), true));
	}
}