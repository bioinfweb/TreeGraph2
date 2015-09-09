package info.bioinfweb.treegraph.gui.actions.select;


import java.util.Map;
import java.awt.event.ActionEvent;
import java.util.Iterator;

import javax.swing.Action;

import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.TextElementData;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeNameAdapter;
import info.bioinfweb.treegraph.document.topologicalcalculation.TopologicalCalculator;
import info.bioinfweb.treegraph.document.undo.ImportTextElementDataParameters;
import info.bioinfweb.treegraph.gui.actions.DocumentAction;
import info.bioinfweb.treegraph.gui.mainframe.MainFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeInternalFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeSelection;
import info.bioinfweb.treegraph.gui.treeframe.TreeViewPanel;



public class TreeSelectionSynchronizeToggleAction extends DocumentAction {
	public static final String KEY_LEAF_REFERENCE = TreeSelectionSynchronizeToggleAction.class.getName() + ".LeafSet";
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
		getMainFrame().getTreeSelectionSynchronizer().setTopologicalCalculator(
				new TopologicalCalculator(getMainFrame().getActiveTreeFrame().getDocument(), adapter, false, KEY_LEAF_REFERENCE, new ImportTextElementDataParameters()));
		System.out.println("start: " + getMainFrame().getTreeSelectionSynchronizer().getTopologicalCalculator().getLeafValues().size());
		Iterator<TreeInternalFrame> treeFrameIterator = getMainFrame().treeFrameIterator();
		while(treeFrameIterator.hasNext()) {
			TreeInternalFrame currentFrame = treeFrameIterator.next();
			currentFrame.getTreeViewPanel().addTreeViewPanelListener(getMainFrame().getTreeSelectionSynchronizer());
			Map<TextElementData, Integer> leafValues = getMainFrame().getTreeSelectionSynchronizer().getTopologicalCalculator().getLeafValues();
			getMainFrame().getTreeSelectionSynchronizer().getTopologicalCalculator().
				addLeafMap(leafValues, currentFrame.getDocument().getTree().getPaintStart(), adapter);
			getMainFrame().getTreeSelectionSynchronizer().getTopologicalCalculator().
					addLeafSets(currentFrame.getDocument().getTree().getPaintStart(), adapter);
		}
		System.out.println("end: " + getMainFrame().getTreeSelectionSynchronizer().getTopologicalCalculator().getLeafValues().size());
	}


	@Override
	public void setEnabled(Document document, TreeSelection selection, NodeBranchDataAdapter tableAdapter) {
		setEnabled((document != null) && !document.getTree().isEmpty());
	}
}