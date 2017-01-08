/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2011, 2013-2017  Ben Stöver, Sarah Wiechers, Kai Müller
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
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import info.bioinfweb.treegraph.Main;
import info.bioinfweb.treegraph.document.Branch;
import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.undo.edit.RerootEdit;
import info.bioinfweb.treegraph.gui.actions.DocumentAction;
import info.bioinfweb.treegraph.gui.mainframe.MainFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeInternalFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeSelection;
import info.bioinfweb.wikihelp.client.WikiHelpOptionPane;



/**
 * Action object that reroots the current tree at that branch the user has selected.
 * 
 * @author Ben St&ouml;ver
 */
public class RerootAction extends DocumentAction {
	public RerootAction(MainFrame mainFrame) {
		super(mainFrame);
		putValue(Action.NAME, "Root here"); 
	  putValue(Action.MNEMONIC_KEY, KeyEvent.VK_H);
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke('R', 
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));  // The same key as for RerootByLeafSetAction, but both actions should never be enabled at the same time.
	}


	@Override
	protected void onActionPerformed(ActionEvent e, TreeInternalFrame frame) {
		RerootEdit edit = new RerootEdit(frame.getDocument(), (Branch)frame.getTreeViewPanel().getSelection().first());
		frame.getDocument().executeEdit(edit);
		if (edit.hasWarnings()) {
			WikiHelpOptionPane.showMessageDialog(MainFrame.getInstance(), edit.getWarningText(), "Reroot",	
					JOptionPane.WARNING_MESSAGE, Main.getInstance().getWikiHelp(), 27);
		}
	}

	
	@Override
	public void setEnabled(Document document, TreeSelection selection, NodeBranchDataAdapter tableAdapter) {
		boolean enabled = oneElementSelected(selection) && selection.containsType(Branch.class);
		if (enabled) {
			Branch b = ((Branch)selection.first());
			enabled = b.getTargetNode().hasParent();  // rerooting at the root does not make sense
			if (enabled) {  // rerooting directly under the root only makes sense if there are more than two subnodes under the root
				enabled = (b.getTargetNode().getParent().getChildren().size() > 2) || b.getTargetNode().getParent().hasParent();
			}
		}
		setEnabled(enabled);
	}
}