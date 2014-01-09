/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2014  Ben Stöver, Kai Müller
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

import info.bioinfweb.treegraph.gui.actions.DocumentAction;
import info.bioinfweb.treegraph.gui.mainframe.MainFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeInternalFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeSelection;



/**
 * Abstract precursor of all actions that manipulate the document selection. It sets the 
 * <code>valueIsAdjusting</code> property of the selection to <code>true</code> during the selection
 * operations.
 * 
 * @author Ben St&ouml;ver
 * @since 2.0.44
 */
public abstract class AbstractSelectionAction extends DocumentAction {
	public AbstractSelectionAction(MainFrame mainFrame) {
		super(mainFrame);
	}

	
	protected abstract void performSelection(ActionEvent e, TreeInternalFrame frame, 
			TreeSelection selection);
	
	
	@Override
	protected void onActionPerformed(ActionEvent e, TreeInternalFrame frame) {
		frame.getTreeViewPanel().getSelection().setValueIsAdjusting(true);
		try {
			performSelection(e, frame, frame.getTreeViewPanel().getSelection());
		}
		finally {
			frame.getTreeViewPanel().getSelection().setValueIsAdjusting(false);
		}
	}
}
