package info.bioinfweb.treegraph.gui.mainframe;


import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.TextElementData;
import info.bioinfweb.treegraph.gui.treeframe.TreeInternalFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeSelection;
import info.bioinfweb.treegraph.gui.treeframe.TreeViewPanel;
import info.bioinfweb.treegraph.gui.treeframe.TreeViewPanelListener;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.event.ChangeEvent;




public class TreeSelectionSynchronizer implements TreeViewPanelListener {
	private MainFrame owner;
	private boolean isUpdating = false;
	
	
	public TreeSelectionSynchronizer(MainFrame owner) {
		super();
		this.owner = owner;
	}


	public MainFrame getOwner() {
		return owner;
	}


	private void selectAccordingNodesInSubtree(Node root, Set<TextElementData> names, TreeSelection selection) {
		if (root.isLeaf()) {
			if (names.contains(root.getData().toString())) {  //TODO Use compare options of TreeGraph in the future.
				selection.add(root);
			}
		}
		else {
			for (Node child : root.getChildren()) {
				selectAccordingNodesInSubtree(child, names, selection);
			}
		}
	}
	
	
	private void selectAccordingNodes(TreeViewPanel source, TreeViewPanel target) {
		if (!source.equals(target)) {
			Set<TextElementData> nameSet = new TreeSet<TextElementData>();
			for (Node node : source.getSelection().getAllElementsOfType(Node.class, true)) {
				nameSet.add(node.getData());
			}
			
			TreeSelection selection = target.getSelection();
			selection.clear();
			
			selectAccordingNodesInSubtree(target.getDocument().getTree().getPaintStart(), nameSet, selection);		
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
	public void zoomChanged(ChangeEvent e) {
//		if (!isUpdating) {
//			isUpdating = true;  // Avoid recursive calls
//			try {
//				float zoom = ((TreeViewPanel)e.getSource()).getZoom();
//				Iterator<ResultTreePanel> iterator = getOwner().getTreePanels().iterator();
//				while (iterator.hasNext()) {
//					TreeViewPanel treePanel = iterator.next().getTreeScrollPane().getTreeViewPanel(); 
//					if (!e.getSource().equals(treePanel)) {
//						treePanel.setZoom(zoom);
//					}
//				}
//			}
//			finally {
//				isUpdating = false;
//			}
//		}
	}

	
	@Override
	public void sizeChanged(ChangeEvent e) {}
}