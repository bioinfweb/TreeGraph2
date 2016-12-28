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
package info.bioinfweb.treegraph.gui.actions.select;


import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;

import info.bioinfweb.treegraph.document.AbstractPaintableElement;
import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.Legends;
import info.bioinfweb.treegraph.document.NodeType;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.tools.TreeSerializer;
import info.bioinfweb.treegraph.gui.mainframe.MainFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeInternalFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeSelection;



public class SelectAllAction extends AbstractSelectionAction {
	public SelectAllAction(MainFrame mainFrame) {
		super(mainFrame);
	  putValue(Action.NAME, "Select all"); 
	  putValue(Action.MNEMONIC_KEY, KeyEvent.VK_A);
	  putValue(Action.SHORT_DESCRIPTION, "Select all"); 
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke('A', 
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
	}

	
	@Override
	protected void performSelection(ActionEvent e, TreeInternalFrame frame,
			TreeSelection selection) {
		
		AbstractPaintableElement[] elements = TreeSerializer.getElementsInSubtree(
				frame.getDocument().getTree().getPaintStart(), NodeType.BOTH, AbstractPaintableElement.class); 
		
		selection.clear();
		for (int i = 0; i < elements.length; i++) {
			selection.add(elements[i]);
		}
		
		Legends legends = frame.getDocument().getTree().getLegends();
		for (int i = 0; i < legends.size(); i++) {
			selection.add(legends.get(i));
		}
		
		if (frame.getDocument().getTree().getFormats().getShowScaleBar()) {
			selection.add(frame.getDocument().getTree().getScaleBar());
		}
	}

	
	@Override
	public void setEnabled(Document document, TreeSelection selection, NodeBranchDataAdapter tableAdapter) {
		setEnabled((document != null) && !document.getTree().isEmpty());
	}
}