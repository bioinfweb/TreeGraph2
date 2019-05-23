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
package info.bioinfweb.treegraph.gui.actions.select;


import java.awt.event.ActionEvent;
import java.util.Iterator;

import javax.swing.Action;

import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.tools.NodeBranchDataColumnAnalyzer;
import info.bioinfweb.treegraph.document.tools.NodeBranchDataColumnManager;
import info.bioinfweb.treegraph.gui.actions.DocumentAction;
import info.bioinfweb.treegraph.gui.actions.edit.DefaultDocumentAdapterAction;
import info.bioinfweb.treegraph.gui.dialogs.nodebranchdata.defaultadapter.SelectionSynchronizationMessagePanel;
import info.bioinfweb.treegraph.gui.mainframe.MainFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeInternalFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeSelection;



public class TreeSelectionSynchronizeToggleAction extends DocumentAction {
	private SelectionSynchronizationMessagePanel messagePanel = new SelectionSynchronizationMessagePanel();
	
	
	public TreeSelectionSynchronizeToggleAction(MainFrame mainFrame) {
		super(mainFrame);
		putValue(Action.NAME, "Synchronize tree selection");
		putValue(Action.SHORT_DESCRIPTION, "Synchronize node selection in all currently opened trees");
		putValue(Action.SELECTED_KEY, false);
		loadSymbols("SynchronizeTreeSelection");
	}
	
	
	public boolean isActive() {
		return (Boolean)getValue(Action.SELECTED_KEY);
	}
	
	
	private void checkDefaultColumns(ActionEvent e) {
		boolean betterColumnFound = false;
		Iterator<TreeInternalFrame> iterator = MainFrame.getInstance().treeFrameIterator();
		while (iterator.hasNext() && !betterColumnFound) {
			Document document = iterator.next().getDocument();
			if (NodeBranchDataColumnAnalyzer.ColumnStatus.NO_NUMERIC_OR_PARSABLE.equals(
					NodeBranchDataColumnAnalyzer.analyzeColumnStatus(document.getTree(), document.getDefaultSupportAdapter()))) {
				
				betterColumnFound = betterColumnFound || 
						!NodeBranchDataColumnManager.listAdapters(document.getTree(), false, true, true, true, false, null).isEmpty();  // That means that at least one other column contains at least one decimal value.
						//TODO This does not find columns with parsable values. Should these also be considered and analyzeColumnStatus() be used instead here?
			}
		}
		
		if (betterColumnFound) {
			DefaultDocumentAdapterAction action = (DefaultDocumentAdapterAction)MainFrame.getInstance().getActionManagement().get("edit.defaultDocumentAdapters");
			action.getDialog().setAdditionalHeadComponent(messagePanel);
			try {
				action.actionPerformed(e);
			}
			finally {
				action.getDialog().setAdditionalHeadComponent(null);
			}
			//TODO An inherited class with the message panel could alternatively be used. setAdditionalHeadComponent() would then be obsolete. The dialog would be in memory twice, though.
		}
	}
	
	
	@Override
	protected void onActionPerformed(ActionEvent e, TreeInternalFrame frame) {
		Iterator<TreeInternalFrame> treeFrameIterator = getMainFrame().treeFrameIterator();
		if (isActive()) {  // Reset TreeSelectionSynchronizer and add listeners:
			checkDefaultColumns(e);
			
			getMainFrame().getTreeSelectionSynchronizer().reset();
			getMainFrame().addChildWindowListener(getMainFrame().getTreeSelectionSynchronizer());
			while (treeFrameIterator.hasNext()) {
				TreeInternalFrame currentFrame = treeFrameIterator.next();
				currentFrame.getTreeViewPanel().addTreeViewPanelListener(getMainFrame().getTreeSelectionSynchronizer());
				currentFrame.getTreeViewPanel().getDocument().addView(getMainFrame().getTreeSelectionSynchronizer());
			}
		}
		else {  // Unregister listeners:
			getMainFrame().removeChildWindowListener(getMainFrame().getTreeSelectionSynchronizer());
			while (treeFrameIterator.hasNext()) {
				TreeInternalFrame currentFrame = treeFrameIterator.next();		
				currentFrame.getTreeViewPanel().removeTreeViewPanelListener(getMainFrame().getTreeSelectionSynchronizer());
				currentFrame.getTreeViewPanel().getDocument().removeView(getMainFrame().getTreeSelectionSynchronizer());
			}
		}
	}
	
	
	@Override
	public void setEnabled(Document document, TreeSelection selection, NodeBranchDataAdapter tableAdapter) {
		Iterator<TreeInternalFrame> iterator = getMainFrame().treeFrameIterator();
		int treeCount = 0;
		while (iterator.hasNext()) {
			if (!iterator.next().getDocument().getTree().isEmpty()) {
				treeCount++;
				if (treeCount >= 2) {
					setEnabled(true);
					return;
				}
			}
		}
		setEnabled(false);
	}
}