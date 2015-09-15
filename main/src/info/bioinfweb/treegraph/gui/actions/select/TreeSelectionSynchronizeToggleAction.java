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
		setEnabled((document != null) && !document.getTree().isEmpty());
	}
}