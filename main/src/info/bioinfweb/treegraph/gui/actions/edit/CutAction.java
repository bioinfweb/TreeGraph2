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
import info.bioinfweb.treegraph.document.AbstractPaintableElement;
import info.bioinfweb.treegraph.document.undo.edit.CutEdit;
import info.bioinfweb.treegraph.gui.mainframe.MainFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeInternalFrame;
import info.bioinfweb.wikihelp.client.WikiHelpOptionPane;



/**
 * Cuts a leaf node, subtree, label or legend from the document to the clipboard. 
 * 
 * @author Ben St&ouml;ver
 */
public class CutAction extends CopyElementAction {
	public CutAction(MainFrame mainFrame) {
		super(mainFrame);
		putValue(Action.NAME, "Cut"); 
	  putValue(Action.MNEMONIC_KEY, KeyEvent.VK_C);
		putValue(Action.SHORT_DESCRIPTION, "Cut"); 
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke('X', 
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
	  loadSymbols("Cut");
	}

	
	@Override
	protected void onActionPerformed(ActionEvent e, TreeInternalFrame frame) {
		super.onActionPerformed(e, frame);
		CutEdit edit = new CutEdit(frame.getDocument(), 
						frame.getTreeViewPanel().getSelection().toArray(new AbstractPaintableElement[0]));
		frame.getDocument().executeEdit(edit);
		if (edit.hasWarnings()) {
			WikiHelpOptionPane.showMessageDialog(MainFrame.getInstance(), edit.getWarningText(), "Legend(s) affected",	
					JOptionPane.WARNING_MESSAGE, Main.getInstance().getWikiHelp(), 69);
		}
	}
}