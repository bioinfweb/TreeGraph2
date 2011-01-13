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


import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;

import info.bioinfweb.treegraph.Main;
import info.bioinfweb.treegraph.document.Branch;
import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.Label;
import info.bioinfweb.treegraph.document.Legend;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.PaintableElement;
import info.bioinfweb.treegraph.document.ScaleBar;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.gui.actions.DocumentAction;
import info.bioinfweb.treegraph.gui.mainframe.MainFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeInternalFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeSelection;



public class CopyElementAction extends DocumentAction {
	public CopyElementAction(MainFrame mainFrame) {
		super(mainFrame);
		putValue(Action.NAME, "Copy"); 
	  putValue(Action.MNEMONIC_KEY, KeyEvent.VK_C);
		putValue(Action.SHORT_DESCRIPTION, "Copy"); 
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke('C', 
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
	  loadSymbols("CopyElement");
	}


	@Override
	public void setEnabled(Document document, TreeSelection selection, NodeBranchDataAdapter tableAdapter) {
		setEnabled(oneElementSelected(selection) && !selection.containsType(ScaleBar.class));
		// Nodes (also per branch), labels and legends can be copied.
	}

	
	@Override
	protected void onActionPerformed(ActionEvent e, TreeInternalFrame frame) {
		PaintableElement selected = frame.getTreeViewPanel().getSelection().first(); 
		if (selected instanceof Node) {
			Main.getInstance().getClipboard().copySubtree(frame.getDocument().getTree(), (Node)selected);
		}
		else if (selected instanceof Branch) {
			Main.getInstance().getClipboard().copySubtree(frame.getDocument().getTree(), 
					((Branch)selected).getTargetNode());
		}
		else if (selected instanceof Label) {
			Main.getInstance().getClipboard().copyLabel((Label)selected);
		}
		else if (selected instanceof Legend) {
			Main.getInstance().getClipboard().copyLegend((Legend)selected);
		}
		//TODO ScaleBar kopieren (in ein anderes Dokument)
		
		MainFrame.getInstance().getActionManagement().refreshActionStatus();  // Paste könnte gerade möglich geworden sein.
	}
}