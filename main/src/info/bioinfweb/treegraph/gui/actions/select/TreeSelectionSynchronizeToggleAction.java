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
		if ((Boolean)getValue(Action.SELECTED_KEY)) { //TODO Action geht aus und lädt alles beim Neustart neu, wenn Änderungen im Dokument sind
			getMainFrame().getTreeSelectionSynchronizer().setTopologicalCalculator(
					new TopologicalCalculator(frame.getDocument(), adapter, false, KEY_LEAF_REFERENCE, new ImportTextElementDataParameters()));
			Map<TextElementData, Integer> leafValues = getMainFrame().getTreeSelectionSynchronizer().getTopologicalCalculator().getLeafValues();
	
			// Create map of leaves from all documents and add listeners:
			Iterator<TreeInternalFrame> treeFrameIterator = getMainFrame().treeFrameIterator();
			while(treeFrameIterator.hasNext()) {
					TreeInternalFrame currentFrame = treeFrameIterator.next();
					currentFrame.getTreeViewPanel().addTreeViewPanelListener(getMainFrame().getTreeSelectionSynchronizer());
					
					if (currentFrame.getDocument() != frame.getDocument()) {  // First document was already added in the constructor. 
						getMainFrame().getTreeSelectionSynchronizer().getTopologicalCalculator().
								addLeafMap(leafValues, currentFrame.getDocument().getTree().getPaintStart(), adapter);
					}
			}

			// Create leaf sets:
			treeFrameIterator = getMainFrame().treeFrameIterator();
			while(treeFrameIterator.hasNext()) {
					getMainFrame().getTreeSelectionSynchronizer().getTopologicalCalculator().
							addLeafSets(treeFrameIterator.next().getDocument().getTree().getPaintStart(), adapter);
			}
		}
		else {  // Unregister listeners:				
			Iterator<TreeInternalFrame> treeFrameIterator = getMainFrame().treeFrameIterator();
			while(treeFrameIterator.hasNext()) {
				treeFrameIterator.next().getTreeViewPanel().removeTreeViewPanelListener(getMainFrame().getTreeSelectionSynchronizer());
			}
		}
	}


	@Override
	public void setEnabled(Document document, TreeSelection selection, NodeBranchDataAdapter tableAdapter) {
		setEnabled((document != null) && !document.getTree().isEmpty());
	}
}