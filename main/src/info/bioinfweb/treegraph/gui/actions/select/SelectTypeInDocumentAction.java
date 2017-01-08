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
package info.bioinfweb.treegraph.gui.actions.select;


import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.Action;

import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.Legend;
import info.bioinfweb.treegraph.document.Legends;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.NodeType;
import info.bioinfweb.treegraph.document.PaintableElement;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.tools.TreeSerializer;
import info.bioinfweb.treegraph.gui.mainframe.MainFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeInternalFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeSelection;



public class SelectTypeInDocumentAction extends AbstractSelectionAction {
	public SelectTypeInDocumentAction(MainFrame mainFrame) {
		super(mainFrame);
	  putValue(Action.NAME, "Select this type(s) in document"); 
	  putValue(Action.MNEMONIC_KEY, KeyEvent.VK_D);
	}
	

	@Override
	protected void performSelection(ActionEvent e, TreeInternalFrame frame,
			TreeSelection selection) {

		Node root = frame.getDocument().getTree().getPaintStart();
		LinkedList<PaintableElement> list = new LinkedList<PaintableElement>();
		Iterator<PaintableElement> iterator = selection.iterator();
		while (iterator.hasNext()) {
			PaintableElement element = iterator.next();
			if (root != null) {
				PaintableElement[] subelements = TreeSerializer.getElementsInSubtree(
						root, NodeType.BOTH, element.getClass(), new PaintableElement[0]);
				
				for (int j = 0; j < subelements.length; j++) {
					list.add(subelements[j]);
				}
			}
		}
		selection.addAll(list);
		
		if (selection.containsType(Legend.class)) {
			Legends legends = frame.getDocument().getTree().getLegends();
			for (int i = 0; i < legends.size(); i++) {
				selection.add(legends.get(i));
			}
		}
	}

	
	@Override
	public void setEnabled(Document document, TreeSelection selection, NodeBranchDataAdapter tableAdapter) {
		setEnabled((selection != null) && (!selection.isEmpty()));
	}
}