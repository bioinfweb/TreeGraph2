/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2013  Ben Stöver, Kai Müller
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


import info.bioinfweb.treegraph.Main;
import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.undo.edit.RerootByLeafSetEdit;
import info.bioinfweb.treegraph.gui.actions.DocumentAction;
import info.bioinfweb.treegraph.gui.mainframe.MainFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeInternalFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeSelection;
import info.webinsel.wikihelp.client.WikiHelpOptionPane;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;



/**
 * Action object that reroots the current tree so that a set of leaf nodes selected by the user is located
 * in a subtree branching from the new root with minimal size.
 * 
 * @author Ben St&ouml;ver
 */
public class RerootByLeafSetAction extends DocumentAction {
	public RerootByLeafSetAction(MainFrame mainFrame) {
		super(mainFrame);
		putValue(Action.NAME, "Root by leafs"); 
	  putValue(Action.MNEMONIC_KEY, KeyEvent.VK_R);
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke('R', 
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));  // The same key as for RerootAction, but both actions should never be enabled at the same time.
	}


	@Override
	protected void onActionPerformed(ActionEvent e, TreeInternalFrame frame) {
		TreeSelection selection = frame.getTreeViewPanel().getSelection();
		RerootByLeafSetEdit edit = new RerootByLeafSetEdit(frame.getDocument(), selection.toArray(new Node[selection.size()]));
		frame.getDocument().executeEdit(edit);

		if (edit.hasWarnings()) {
			WikiHelpOptionPane.showMessageDialog(MainFrame.getInstance(), edit.getWarningText(), "Reroot",	
					JOptionPane.WARNING_MESSAGE, Main.getInstance().getWikiHelp(), 70);
		}
		if (!edit.getAlternativeRootingPoints().isEmpty()) {
			selection.clear();
			selection.addAll(edit.getAlternativeRootingPoints());
			WikiHelpOptionPane.showMessageDialog(MainFrame.getInstance(), 
					"More than one smallest subtree containing all the specified terminal nodes was found.\n" +
					"The alternative rooting positions have been selected in the document. Use the \"Root here\" function\n" +
					"to root at another possible rooting point.", "Multiple optimal rooting points",	
					JOptionPane.WARNING_MESSAGE, Main.getInstance().getWikiHelp(), 68);
		}
	}

	
	@Override
	public void setEnabled(Document document, TreeSelection selection, NodeBranchDataAdapter tableAdapter) {
		setEnabled((selection != null) && selection.containsOnlyLeafNodes());
	}
}
