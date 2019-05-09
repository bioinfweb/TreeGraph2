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
package info.bioinfweb.treegraph.gui.actions.file;


import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import info.bioinfweb.treegraph.Main;
import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.undo.file.addsupportvalues.AddSupportValuesEdit;
import info.bioinfweb.treegraph.document.undo.file.addsupportvalues.AddSupportValuesParameters;
import info.bioinfweb.treegraph.gui.actions.DocumentAction;
import info.bioinfweb.treegraph.gui.dialogs.io.addsupportvalues.AddSupportValueColumnsDialog;
import info.bioinfweb.treegraph.gui.dialogs.io.addsupportvalues.AddSupportValuesDialog;
import info.bioinfweb.treegraph.gui.mainframe.MainFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeInternalFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeSelection;
import info.bioinfweb.wikihelp.client.WikiHelpOptionPane;



public class AddSupportValuesAction extends DocumentAction {
	private AddSupportValuesDialog addSupportValuesDialog;
	private AddSupportValueColumnsDialog addSupportValueColumnsDialog;
	
	
	public AddSupportValuesAction(MainFrame mainFrame) {
		super(mainFrame);
		putValue(Action.NAME, "Add support values..."); 
	  putValue(Action.MNEMONIC_KEY, KeyEvent.VK_V);
		putValue(Action.SHORT_DESCRIPTION, "Add support values from other tree"); 
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0));
	}

	
	@Override
	protected void onActionPerformed(ActionEvent e, TreeInternalFrame frame) {
		if (getAddSupportValuesDialog().execute(frame.getDocument(), frame.getTreeViewPanel().getSelection(), frame.getSelectedAdapter())) {
			AddSupportValuesParameters addSupportValuesParameters = new AddSupportValuesParameters();
			if (getAddSupportValuesDialog().assignParameters(addSupportValuesParameters)) {  // Will return false, if user canceled an error occurs e.g. while loading the source document.
				if (getAddSupportValueColumnsDialog().execute(addSupportValuesParameters, addSupportValuesParameters.getSourceDocument().getTree())) {
					AddSupportValuesEdit edit = new AddSupportValuesEdit(frame.getDocument(), addSupportValuesParameters);
					frame.getDocument().executeEdit(edit);
					
					if (edit.hasWarnings()) {
						WikiHelpOptionPane.showMessageDialog(MainFrame.getInstance(), edit.getWarningText(), "Some leaf nodes did not match",	
								JOptionPane.WARNING_MESSAGE, Main.getInstance().getWikiHelp(), 86);
					}
				}
			}
		}
	}


	@Override
	public void setEnabled(Document document, TreeSelection selection, NodeBranchDataAdapter tableAdapter) {
		setEnabled((document != null) && !document.getTree().isEmpty());
	}


	private AddSupportValuesDialog getAddSupportValuesDialog() {
		if (addSupportValuesDialog == null) {
			addSupportValuesDialog = new AddSupportValuesDialog(getMainFrame());
		}
		return addSupportValuesDialog;
	}


	private AddSupportValueColumnsDialog getAddSupportValueColumnsDialog() {
		if (addSupportValueColumnsDialog == null) {
			addSupportValueColumnsDialog = new AddSupportValueColumnsDialog(getMainFrame());
		}
		return addSupportValueColumnsDialog;
	}
}