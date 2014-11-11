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
package info.bioinfweb.treegraph.gui.actions.edit;


import javax.swing.Action;

import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.undo.edit.CollapseNodesBySupportEdit;
import info.bioinfweb.treegraph.gui.actions.EditDialogAction;
import info.bioinfweb.treegraph.gui.dialogs.CollapseNodesBySupportDialog;
import info.bioinfweb.treegraph.gui.dialogs.EditDialog;
import info.bioinfweb.treegraph.gui.mainframe.MainFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeSelection;



/**
 * Action object related to {@link CollapseNodesBySupportDialog} and {@link CollapseNodesBySupportEdit}.
 * 
 * @author Ben St&ouml;ver
 * @since 2.2.0
 */
public class CollapseNodesBySupportAction extends EditDialogAction {
	public CollapseNodesBySupportAction (MainFrame mainFrame) {
		super(mainFrame);
		putValue(Action.NAME, "Collapse nodes in subtree by support value"); 
	  //putValue(Action.MNEMONIC_KEY, KeyEvent.VK_O);  //TODO Possibly define unique key
	}


	@Override
  public EditDialog createDialog() {
	  return new CollapseNodesBySupportDialog(getMainFrame());
  }


	@Override
	public void setEnabled(Document document, TreeSelection selection, NodeBranchDataAdapter tableAdapter) {
		boolean oneNodeSelected = oneElementSelected(selection) && (selection.containsType(Node.class));
		Node node = null;
		if (oneNodeSelected) {
			node = (Node)selection.first();
		}
		setEnabled(oneNodeSelected && (node.hasParent() || (node.getChildren().size() == 1)) 
				&& !node.isLeaf());
	}
}
