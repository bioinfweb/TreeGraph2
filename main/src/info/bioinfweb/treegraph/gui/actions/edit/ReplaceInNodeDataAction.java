/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2015-2016  Ben Stöver, Sarah Wiechers, Kai Müller
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
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;

import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.gui.actions.EditDialogAction;
import info.bioinfweb.treegraph.gui.dialogs.EditDialog;
import info.bioinfweb.treegraph.gui.dialogs.ReplaceInNodeDataDialog;
import info.bioinfweb.treegraph.gui.mainframe.MainFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeSelection;



/**
 * Action in the edit menu.
 *  
 * @author Ben St&ouml;ver
 */
public class ReplaceInNodeDataAction extends EditDialogAction {
	public static final String TEXT = "Replace text in node/branch data...";
	
	
	public ReplaceInNodeDataAction(MainFrame mainFrame) {
		super(mainFrame);
		putValue(Action.NAME, TEXT); 
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke('H', 
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
	  putValue(Action.MNEMONIC_KEY, KeyEvent.VK_R);
	  loadSymbols("Replace");
	}

	
	@Override
	public EditDialog createDialog() {
		return new ReplaceInNodeDataDialog(getMainFrame());
	}


	@Override
	public void setEnabled(Document document, TreeSelection selection, NodeBranchDataAdapter tableAdapter) {
		setEnabled((document != null) && !document.getTree().isEmpty());
	}
}