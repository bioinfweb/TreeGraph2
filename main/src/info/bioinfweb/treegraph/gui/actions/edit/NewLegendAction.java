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
package info.bioinfweb.treegraph.gui.actions.edit;


import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Iterator;

import javax.swing.Action;
import javax.swing.KeyStroke;

import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.Legend;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.PaintableElement;
import info.bioinfweb.treegraph.document.format.FormatUtils;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.undo.edit.InsertLegendEdit;
import info.bioinfweb.treegraph.gui.actions.DocumentAction;
import info.bioinfweb.treegraph.gui.mainframe.MainFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeInternalFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeSelection;



public class NewLegendAction extends DocumentAction {
	public NewLegendAction(MainFrame mainFrame) {
		super(mainFrame);
		putValue(Action.NAME, "New legend"); 
	  putValue(Action.MNEMONIC_KEY, KeyEvent.VK_N);
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke('L', 
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
	}
	

	@Override
	public void setEnabled(Document document, TreeSelection selection, NodeBranchDataAdapter tableAdapter) {
		setEnabled((selection != null) && ((selection.size() == 1) || (selection.size() == 2)) && 
				selection.containsOnlyType(Node.class));
	}

	
	@Override
	protected void onActionPerformed(ActionEvent e, TreeInternalFrame frame) {
		TreeSelection selection = frame.getTreeViewPanel().getSelection();
		Iterator<PaintableElement> iterator = selection.iterator();
		Node upperAnchor = (Node)iterator.next();
		Node lowerAnchor = null;
		if (selection.size() == 2) {
			lowerAnchor = (Node)iterator.next();
		}
		
		Legend legend = FormatUtils.createLegend(upperAnchor, 
				frame.getDocument().getTree().getLegends());
		legend.getData().assign(upperAnchor.getData());
		
		frame.getDocument().executeEdit(
				new InsertLegendEdit(frame.getDocument(),	legend, upperAnchor, lowerAnchor));
	}
}