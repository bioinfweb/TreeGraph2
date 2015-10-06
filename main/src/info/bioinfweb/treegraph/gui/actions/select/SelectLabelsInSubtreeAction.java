/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2015  Ben Stöver, Sarah Wiechers, Kai Müller
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

import javax.swing.Action;

import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.Label;
import info.bioinfweb.treegraph.document.Legend;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.NodeType;
import info.bioinfweb.treegraph.document.PaintableElement;
import info.bioinfweb.treegraph.document.Tree;
import info.bioinfweb.treegraph.document.TreeSerializer;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.gui.mainframe.MainFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeInternalFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeSelection;



/**
 * @author Ben St&ouml;ver
 */
public class SelectLabelsInSubtreeAction extends AbstractSelectionAction {
	public SelectLabelsInSubtreeAction(MainFrame mainFrame) {
		super(mainFrame);
	  putValue(Action.NAME, "Select labels in subtree(s)"); 
	  putValue(Action.MNEMONIC_KEY, KeyEvent.VK_C);
	}


	@Override
	public void setEnabled(Document document, TreeSelection selection, NodeBranchDataAdapter tableAdapter) {
		setEnabled((selection != null) && (!selection.containsOnlyType(Legend.class)) && !selection.isEmpty());
	}

	
	@Override
	protected void performSelection(ActionEvent e, TreeInternalFrame frame,
			TreeSelection selection) {
		
		Iterator<PaintableElement> iterator = selection.iterator();
		while (iterator.hasNext()) {
			PaintableElement element = iterator.next();
			Node root = Tree.getLinkedNode(element);
			if (root != null) {
				Label[] subelements = 
					  TreeSerializer.getElementsInSubtree(root, NodeType.BOTH, Label.class);  //TODO includeFirstBranch Parameter an TreeSerializer geben um ggf. Selektion der obersten Asts und seiner Label zu verhindern.
				
				for (int j = 0; j < subelements.length; j++) {
					selection.add(subelements[j]);
				}
			}
		}
	}
}