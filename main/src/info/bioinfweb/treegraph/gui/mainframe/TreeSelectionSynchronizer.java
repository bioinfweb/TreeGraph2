package info.bioinfweb.treegraph.gui.mainframe;


import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.TextElementData;
import info.bioinfweb.treegraph.document.change.DocumentChangeEvent;
import info.bioinfweb.treegraph.document.change.DocumentChangeType;
import info.bioinfweb.treegraph.document.change.DocumentListener;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.topologicalcalculation.LeafSet;
import info.bioinfweb.treegraph.document.topologicalcalculation.NodeInfo;
import info.bioinfweb.treegraph.document.topologicalcalculation.TopologicalCalculator;
import info.bioinfweb.treegraph.document.undo.ImportTextElementDataParameters;
import info.bioinfweb.treegraph.gui.treeframe.TreeInternalFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeSelection;
import info.bioinfweb.treegraph.gui.treeframe.TreeViewPanel;
import info.bioinfweb.treegraph.gui.treeframe.TreeViewPanelListener;

import java.util.Iterator;
import java.util.Map;

import javax.swing.Action;
import javax.swing.event.ChangeEvent;



public class TreeSelectionSynchronizer implements TreeViewPanelListener, DocumentListener {
	public static final String KEY_LEAF_REFERENCE = TreeSelectionSynchronizer.class.getName() + ".LeafSet";
	
	private MainFrame owner;
	private boolean isUpdating = false;
	private TopologicalCalculator topologicalCalculator = null;
	private ImportTextElementDataParameters compareParameters = new ImportTextElementDataParameters();
	
	
	public TreeSelectionSynchronizer(MainFrame owner) {
		super();
		this.owner = owner;		
	}


	public MainFrame getOwner() {
		return owner;
	}
	
	
	public ImportTextElementDataParameters getCompareParameters() {
		return compareParameters;
	}


	public void reset() {
		TreeInternalFrame frame = getOwner().getActiveTreeFrame();
		NodeBranchDataAdapter adapter = frame.getDocument().getDefaultLeafAdapter();
		topologicalCalculator = new TopologicalCalculator(frame.getDocument(), adapter, false, KEY_LEAF_REFERENCE, compareParameters);
		Map<TextElementData, Integer> leafValues = topologicalCalculator.getLeafValues();
	
		// Create map of leaves from all documents:
		Iterator<TreeInternalFrame> treeFrameIterator = getOwner().treeFrameIterator();
		while(treeFrameIterator.hasNext()) {
				TreeInternalFrame currentFrame = treeFrameIterator.next();				
				if (currentFrame.getDocument() != frame.getDocument()) {  // First document was already added in the constructor. 
					topologicalCalculator.addLeafMap(leafValues, currentFrame.getDocument().getTree().getPaintStart(), adapter);
				}
		}

		// Create leaf sets:
		treeFrameIterator = getOwner().treeFrameIterator();
		while(treeFrameIterator.hasNext()) {
			topologicalCalculator.addLeafSets(treeFrameIterator.next().getDocument().getTree().getPaintStart(), adapter);
		}
	}
	
	
	private void selectAccordingNodes(TreeViewPanel source, TreeViewPanel target) {
		if (!source.equals(target)) {
			TreeSelection selection = target.getSelection();
			selection.clear();
			for (Node node : source.getSelection().getAllElementsOfType(Node.class, false)) {
//				System.out.println("Source Leafset: " + topologicalCalculator.getLeafSet(node));
//				System.out.println("Target Leafset: " + topologicalCalculator.getLeafSet(target.getDocument().getTree().getPaintStart()));
				LeafSet targetLeafs = topologicalCalculator.getLeafSet(node).addTo(topologicalCalculator.getLeafSet(target.getDocument().getTree().getPaintStart()));
//				LeafSet targetLeafs = topologicalCalculator.getLeafSet(node);
//				System.out.println("Added LS: " + targetLeafs);
				NodeInfo sourceNode = topologicalCalculator.findSourceNodeWithAllLeafs(target.getDocument().getTree().getPaintStart(), targetLeafs);
				if (sourceNode.getAdditionalCount() == 0) {
					System.out.println("Match found");
//					System.out.println("Matching Leafset: " + topologicalCalculator.getLeafSet(sourceNode.getNode()));
					selection.add(sourceNode.getNode());
				}
				else { // if (sourceNode.getAdditionalCount() == -1)
					System.out.println("Conflict found, additional count:" + sourceNode.getAdditionalCount());
//					System.out.println("Target Leafset: " + topologicalCalculator.getLeafSet(sourceNode.getNode()));
//					selection.add(sourceNode.getNode());
//					Node highestConflictingNode = topologicalCalculator.findHighestConflictingNode(target.getDocument().getTree().getPaintStart(), node, bestSourceNode);
//		
//					if (highestConflictingNode != null) {
//						selection.add(highestConflictingNode);
//					}
//					else {
//						System.out.println("Null.");
//					}					
				}
			}
		}					
	}


	@Override
	public void selectionChanged(ChangeEvent e) {
		if (!isUpdating) {
			isUpdating = true;  // Avoid recursive calls
			try {
				TreeViewPanel source = (TreeViewPanel)e.getSource();				
				Iterator<TreeInternalFrame> iterator = getOwner().treeFrameIterator();
				while (iterator.hasNext()) {
					TreeViewPanel target = iterator.next().getTreeViewPanel();
					selectAccordingNodes(source, target);
				}
			}
			finally {
				isUpdating = false;
			}
		}
	}

	
	@Override
	public void zoomChanged(ChangeEvent e) {}

	
	@Override
	public void sizeChanged(ChangeEvent e) {}
		
		
	@Override
	public void changeHappened(DocumentChangeEvent e) {
		if (e.getEdit() != null) {
			DocumentChangeType changeType = e.getEdit().getChangeType();
			if (changeType == DocumentChangeType.ROOT_POSITION || changeType == DocumentChangeType.TOPOLOGICAL_BY_RENAMING || 
					changeType == DocumentChangeType.TOPOLOGICAL_BY_OBJECT_CHANGE) {				
				reset();				
			}
		}		
	}


	public boolean isActive() {
		return (Boolean)getOwner().getActionManagement().get("select.synchronizeTreeSelection").getValue(Action.SELECTED_KEY);
	}
	
	
	public TopologicalCalculator getTopologicalCalculator() {
		return topologicalCalculator;
	}
}