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
package info.bioinfweb.treegraph.gui.actions.select;


import info.bioinfweb.treegraph.document.ConcretePaintableElement;
import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.Legends;
import info.bioinfweb.treegraph.document.NodeType;
import info.bioinfweb.treegraph.document.Tree;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.tools.TreeSerializer;
import info.bioinfweb.treegraph.gui.mainframe.MainFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeInternalFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeSelection;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Vector;

import javax.swing.Action;



public class InvertSelectionAction extends AbstractSelectionAction {
	public InvertSelectionAction(MainFrame mainFrame) {
		super(mainFrame);
	  putValue(Action.NAME, "Invert selection"); 
	  putValue(Action.MNEMONIC_KEY, KeyEvent.VK_V);
	}


	@Override
	public void setEnabled(Document document, TreeSelection selection, NodeBranchDataAdapter tableAdapter) {
		setEnabled((selection != null) && !selection.isEmpty());
	}

	
	@Override
	protected void performSelection(ActionEvent e, TreeInternalFrame frame,
			TreeSelection selection) {
		
		Tree tree = frame.getDocument().getTree();
		List<ConcretePaintableElement> newSelection = new Vector<ConcretePaintableElement>();
		
		ConcretePaintableElement[] all = 
			  TreeSerializer.getElementsInSubtree(tree.getPaintStart(), NodeType.BOTH, ConcretePaintableElement.class);
		for (int i = 0; i < all.length; i++) {
			if (!selection.contains(all[i])) {
				newSelection.add(all[i]);
			}
		}
		
		Legends legends = tree.getLegends();
		for (int i = 0; i < legends.size(); i++) {
			if (!selection.contains(legends.get(i))) {
				newSelection.add(legends.get(i));
			}
		}
		
		if (tree.getFormats().getShowScaleBar() && !selection.contains(tree.getScaleBar())) {
			newSelection.add(tree.getScaleBar());
		}
		
		selection.clear();
		for (int i = 0; i < newSelection.size(); i++) {
			selection.add(newSelection.get(i));
		}
	}
}