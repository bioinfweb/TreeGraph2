/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2011, 2013-2016  Ben Stöver, Sarah Wiechers, Kai Müller
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
import info.bioinfweb.treegraph.document.ConcretePaintableElement;
import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.ScaleBar;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.undo.edit.DeleteEdit;
import info.bioinfweb.treegraph.gui.actions.DocumentAction;
import info.bioinfweb.treegraph.gui.mainframe.MainFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeInternalFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeSelection;
import info.bioinfweb.wikihelp.client.WikiHelpOptionPane;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;



/**
 * Allows the user to remove a leaf node, a subtree, any type of label or a legend from the document.
 * 
 * @author Ben St&ouml;ver
 */
public class DeleteElementAction extends DocumentAction {
	public DeleteElementAction(MainFrame mainFrame) {
		super(mainFrame);
		putValue(Action.NAME, "Delete"); 
	  putValue(Action.MNEMONIC_KEY, KeyEvent.VK_D);
		putValue(Action.SHORT_DESCRIPTION, "Delete"); 
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
	  loadSymbols("Delete");
	}


	@Override
	public void setEnabled(Document document, TreeSelection selection, NodeBranchDataAdapter tableAdapter) {
		setEnabled((selection != null) && (!selection.isEmpty()) && 
				!selection.containsOnlyType(ScaleBar.class));
	}


	@Override
	protected void onActionPerformed(ActionEvent e, TreeInternalFrame frame) {
		DeleteEdit edit = new DeleteEdit(frame.getDocument(), 
						frame.getTreeViewPanel().getSelection().toArray(new ConcretePaintableElement[0]));
		frame.getDocument().executeEdit(edit);
		if (edit.hasWarnings()) {
			WikiHelpOptionPane.showMessageDialog(MainFrame.getInstance(), edit.getWarningText(), "Legend(s) affected",	
							JOptionPane.WARNING_MESSAGE, Main.getInstance().getWikiHelp(), 26);
		}
	}
}