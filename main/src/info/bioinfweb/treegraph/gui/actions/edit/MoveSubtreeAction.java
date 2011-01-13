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


import info.bioinfweb.treegraph.document.Branch;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.gui.actions.DocumentAction;
import info.bioinfweb.treegraph.gui.mainframe.MainFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeSelection;



/**
 * This abstract class contains basic functionalities to extract the node to be
 * moved from the selection and is the ancestor of <code>MoveSubtreeUpAction</code>
 * and <code>MoveSubtreeDownAction</code>.
 * @author Ben St&ouml;ver
 */
public abstract class MoveSubtreeAction extends DocumentAction {
	public MoveSubtreeAction(MainFrame mainFrame) {
		super(mainFrame);
	}

	
	/**
	 * Returns the node that should be moved if the selection contains this node or its
	 * afferent branch as the first element.
	 * @param selection
	 * @return
	 */
	protected Node getSelectedNode(TreeSelection selection) {
		if (selection.containsType(Node.class)) {
			return selection.getFirstElementOfType(Node.class);
		}
		else {
			return selection.getFirstElementOfType(Branch.class).getTargetNode();
		}
	}
}