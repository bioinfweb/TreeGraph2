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
import java.util.Iterator;

import javax.swing.Action;

import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.gui.actions.DocumentAction;
import info.bioinfweb.treegraph.gui.mainframe.MainFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeInternalFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeSelection;



public class TreeSelectionSynchronizeToggleAction extends DocumentAction {
	public TreeSelectionSynchronizeToggleAction(MainFrame mainFrame) {
		super(mainFrame);
		putValue(Action.NAME, "Synchronize tree selection");
		putValue(Action.SHORT_DESCRIPTION, "Synchronize node selection in all currently opened trees");
		putValue(Action.SELECTED_KEY, false);
		loadSymbols("SynchronizeTreeSelection");
	}
	
	
	@Override
	protected void onActionPerformed(ActionEvent e, TreeInternalFrame frame) {
		Iterator<TreeInternalFrame> treeFrameIterator = getMainFrame().treeFrameIterator();
		if (getMainFrame().getTreeSelectionSynchronizer().isActive()) {		// Reset TreeSelectionSynchronizer and add listeners:
			getMainFrame().getTreeSelectionSynchronizer().reset();
			while(treeFrameIterator.hasNext()) {
				TreeInternalFrame currentFrame = treeFrameIterator.next();
				currentFrame.getTreeViewPanel().addTreeViewPanelListener(getMainFrame().getTreeSelectionSynchronizer());
				currentFrame.getTreeViewPanel().getDocument().addView(getMainFrame().getTreeSelectionSynchronizer());
				}
			}
		else {		// Unregister listeners:
			while(treeFrameIterator.hasNext()) {
				TreeInternalFrame currentFrame = treeFrameIterator.next();		
				currentFrame.getTreeViewPanel().removeTreeViewPanelListener(getMainFrame().getTreeSelectionSynchronizer());
				currentFrame.getTreeViewPanel().getDocument().removeView(getMainFrame().getTreeSelectionSynchronizer());
				}
		}
	}
	
	
	@Override
	public void setEnabled(Document document, TreeSelection selection, NodeBranchDataAdapter tableAdapter) {
		setEnabled((document != null) && !document.getTree().isEmpty() && (getMainFrame().getTreeFrameCount() > 1));
	}
}