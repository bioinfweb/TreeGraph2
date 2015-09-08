package info.bioinfweb.treegraph.gui.actions.select;


import java.awt.event.ActionEvent;
import java.util.Iterator;

import javax.swing.Action;

import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeNameAdapter;
import info.bioinfweb.treegraph.document.topologicalcalculation.TopologicalCalculator;
import info.bioinfweb.treegraph.gui.actions.DocumentAction;
import info.bioinfweb.treegraph.gui.mainframe.MainFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeInternalFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeSelection;



public class TreeSelectionSynchronizeToggleAction extends DocumentAction {
	private NodeBranchDataAdapter adapter = NodeNameAdapter.getSharedInstance();	
	
	
	public TreeSelectionSynchronizeToggleAction(MainFrame mainFrame) {
		super(mainFrame);
		putValue(Action.NAME, "Synchronize tree selection");
		putValue(Action.SHORT_DESCRIPTION, "Synchronize node selection in all currently opened trees");
		putValue(Action.SELECTED_KEY, false);
		loadSymbols("SynchronizeTreeSelection");
	}
	
	
	@Override
	protected void onActionPerformed(ActionEvent e, TreeInternalFrame frame) {
		getMainFrame().getTreeSelectionSynchronizer().setTopologicalCalculator(new TopologicalCalculator(getMainFrame().getActiveTreeFrame().getDocument(), adapter, false));
		Iterator<TreeInternalFrame> treeFrameIterator = getMainFrame().treeFrameIterator();
		while(treeFrameIterator.hasNext()) {
			treeFrameIterator.next().getTreeViewPanel().addTreeViewPanelListener(getMainFrame().getTreeSelectionSynchronizer());
			getMainFrame().getTreeSelectionSynchronizer().getTopologicalCalculator().
					addLeafSets(getMainFrame().getActiveTreeFrame().getDocument().getTree().getPaintStart(), adapter);
		}
	}


	@Override
	public void setEnabled(Document document, TreeSelection selection, NodeBranchDataAdapter tableAdapter) {
		setEnabled((document != null) && !document.getTree().isEmpty());
	}
}