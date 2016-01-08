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


import info.bioinfweb.treegraph.document.Branch;
import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.gui.actions.EditDialogAction;
import info.bioinfweb.treegraph.gui.dialogs.EditDialog;
import info.bioinfweb.treegraph.gui.dialogs.editelement.NewIconLabelsDialog;
import info.bioinfweb.treegraph.gui.mainframe.MainFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeSelection;

import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;



/**
 * @author Ben St&ouml;ver
 */
public class NewIconLabelsAction extends EditDialogAction {
	public NewIconLabelsAction(MainFrame mainFrame) {
		super(mainFrame);
		putValue(Action.NAME, "New icon label(s)..."); 
	  putValue(Action.MNEMONIC_KEY, KeyEvent.VK_I);
		putValue(Action.ACCELERATOR_KEY, 
				KeyStroke.getKeyStroke('I', 
						Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() + InputEvent.SHIFT_MASK));
	}
	
	
	@Override
	public EditDialog createDialog() {
		return new NewIconLabelsDialog(getMainFrame());
	}


	@Override
	public void setEnabled(Document document, TreeSelection selection, NodeBranchDataAdapter tableAdapter) {
		setEnabled((selection != null) && selection.containsType(Branch.class));
	}
}