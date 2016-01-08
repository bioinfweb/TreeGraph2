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
package info.bioinfweb.treegraph.gui.actions.file;


import info.bioinfweb.treegraph.document.*;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.gui.actions.DocumentAction;
import info.bioinfweb.treegraph.gui.mainframe.MainFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeInternalFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeSelection;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.*;



public class NewDocumentAction extends DocumentAction implements Action {
	public NewDocumentAction(MainFrame mainFrame) {
    super(mainFrame);
		putValue(Action.NAME, "New"); 
	  putValue(Action.MNEMONIC_KEY, KeyEvent.VK_N);
		putValue(Action.SHORT_DESCRIPTION, "New document"); 
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke('N', 
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
	  loadSymbols("NewDocument");
	}
	
	
	@Override
	public void setEnabled(Document document, TreeSelection selection, NodeBranchDataAdapter tableAdapter) {}
	
	
	@Override
	protected void onActionPerformed(ActionEvent e, TreeInternalFrame frame) {}  // unused


	/**
	 * This class overrides a{@code actionPerformed()} directly because it can also be executed if no
	 * document is opend.
	 * 
	 * @see info.bioinfweb.treegraph.gui.actions.DocumentAction#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		Document d = new Document();
		getMainFrame().addInternalFrame(d);
	}
}