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
package info.bioinfweb.treegraph.gui.mainframe;


import info.bioinfweb.commons.graphics.GraphicsUtils;
import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.Label;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.change.DocumentChangeEvent;
import info.bioinfweb.treegraph.document.change.DocumentChangeType;
import info.bioinfweb.treegraph.document.change.DocumentListener;
import info.bioinfweb.treegraph.document.nodebranchdata.IDElementAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.VoidNodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.topologicalcalculation.LeafSet;
import info.bioinfweb.treegraph.document.topologicalcalculation.NodeInfo;
import info.bioinfweb.treegraph.document.topologicalcalculation.TopologicalCalculator;
import info.bioinfweb.treegraph.document.undo.SelectionSynchronizationCompareParameters;
import info.bioinfweb.treegraph.gui.treeframe.HighlightedGroup;
import info.bioinfweb.treegraph.gui.treeframe.TreeInternalFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeSelection;
import info.bioinfweb.treegraph.gui.treeframe.TreeViewPanel;
import info.bioinfweb.treegraph.gui.treeframe.TreeViewPanelListener;

import java.awt.Color;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.util.Iterator;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;



/**
 * Synchronizes the selection in all opened tree when this functionality is enabled.
 * 
 * @author Sarah Wiechers
 * @author Ben St&ouml;ver
 * @since 2.5.0
 */
public class TreeSelectionSynchronizer implements TreeViewPanelListener, DocumentListener, ContainerListener {
	public static final String KEY_LEAF_REFERENCE = TreeSelectionSynchronizer.class.getName() + ".LeafSet";
	public static final String CONFLICT_HIGHLIGHT_GROUP_NAME = "Topological conflict";
	public static final Color PRIMARY_CONFLICT_COLOR = Color.RED;
	public static final Color ALTERNATIVE_CONFLICT_COLOR = GraphicsUtils.invertColor(PRIMARY_CONFLICT_COLOR);  //TODO Possibly adjust colors.

	private Iterable<TreeViewPanel> treeSource;
	protected boolean isUpdating = false;
	private TopologicalCalculator topologicalCalculator = null;
	private SelectionSynchronizationCompareParameters compareParameters = new SelectionSynchronizationCompareParameters();
	
	
	public TreeSelectionSynchronizer(Iterable<TreeViewPanel> treeSource) {
		super();
		this.treeSource = treeSource;		
	}


	public Iterable<TreeViewPanel> getTreeSource() {
		return treeSource;
	}


	public SelectionSynchronizationCompareParameters getCompareParameters() {
		return compareParameters;
	}

	
	public static HighlightedGroup getConflictHighlightGroup(TreeViewPanel panel) {
		HighlightedGroup result = panel.getHighlighting().get(CONFLICT_HIGHLIGHT_GROUP_NAME);
		if (result != null) {
			result.clear();
		}
		else {
			result = new HighlightedGroup(panel, CONFLICT_HIGHLIGHT_GROUP_NAME, PRIMARY_CONFLICT_COLOR, ALTERNATIVE_CONFLICT_COLOR);
			panel.getHighlighting().put(CONFLICT_HIGHLIGHT_GROUP_NAME, result);
		}
		return result;
	}
	

	public void reset() {
		topologicalCalculator = new TopologicalCalculator(compareParameters.isProcessRooted(), KEY_LEAF_REFERENCE, compareParameters);
		
		// Add leaves from documents to map:
		Iterator<TreeViewPanel> iterator = getTreeSource().iterator();
		while (iterator.hasNext()) {
			Document document = iterator.next().getDocument();
			if (!document.getTree().isEmpty()) {
				topologicalCalculator.addSubtreeToLeafValueToIndexMap(document.getTree().getPaintStart(), document.getDefaultLeafAdapter());
			}
		}
		
		// Create leaf sets on all trees:
		iterator = getTreeSource().iterator();
		while (iterator.hasNext()) {
			TreeViewPanel treeViewPanel = iterator.next();
			Document document = treeViewPanel.getDocument();
			if (!document.getTree().isEmpty()) {
				topologicalCalculator.addLeafSets(document.getTree().getPaintStart(), document.getDefaultLeafAdapter());
			}
			
			getConflictHighlightGroup(treeViewPanel).clear();;
    }
	}
	
	
	private void selectCorrespondingNodes(TreeViewPanel activeTree, TreeViewPanel selectionTargetTree) {
		HighlightedGroup conflictGroup = getConflictHighlightGroup(selectionTargetTree);
		conflictGroup.clear();  // Conflict group also needs to be cleared for activeTree.
		
		if (!activeTree.equals(selectionTargetTree) && !selectionTargetTree.getDocument().getTree().isEmpty()) {
			TreeSelection selection = selectionTargetTree.getSelection();
			selection.clear();

			LeafSet restrictingLeafSet = topologicalCalculator.getLeafSet(selectionTargetTree.getDocument().getTree().getPaintStart()).and(
					topologicalCalculator.getLeafSet(activeTree.getDocument().getTree().getPaintStart()));  // Use shared terminals as the restricting leaf set for comparisons in findNodeWithAllLeaves(). 
			NodeBranchDataAdapter defaultSupportAdapter = selectionTargetTree.getDocument().getDefaultSupportAdapter();
			for (Node activeNode : activeTree.getSelection().getAllElementsOfType(Node.class, false)) {
				List<NodeInfo> selectionTargetNodeInfos = topologicalCalculator.findNodeWithAllLeaves(
						selectionTargetTree.getDocument().getTree(), topologicalCalculator.getLeafSet(activeNode), restrictingLeafSet);
				
				for (NodeInfo selectionTargetNodeInfo : selectionTargetNodeInfos) {
					selection.add(selectionTargetNodeInfo.getNode());
					
					if (!(defaultSupportAdapter instanceof VoidNodeBranchDataAdapter)) {
						Node conflictingNode = topologicalCalculator.findHighestConflict(
								activeTree.getDocument().getTree(), selectionTargetTree.getDocument().getTree(), 
								selectionTargetNodeInfo.getNode(), topologicalCalculator.getLeafSet(activeNode), 
								topologicalCalculator.getLeafSet(selectionTargetNodeInfo.getNode()), defaultSupportAdapter);
						
						//TODO Use findAllConflicts() here instead and highlight all conflicting branches as soon as considering changing shared leaf sets is possible in this method.
						//List<Node> conflicts = topologicalCalculator.findAllConflicts(searchRoot, conflictNodeLeafSet)
						
						if (conflictingNode != null) {
							if (defaultSupportAdapter instanceof IDElementAdapter) {
								Label label = conflictingNode.getAfferentBranch().getLabels().get(((IDElementAdapter)defaultSupportAdapter).getID());
								
								if (label != null) {
									conflictGroup.add(label);
								}
								else {
									conflictGroup.add(conflictingNode.getAfferentBranch());
								}
							}
							else {
								conflictGroup.add(conflictingNode.getAfferentBranch());
							}
						}
					}
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
				for (TreeViewPanel target : treeSource) {
					selectCorrespondingNodes(source, target);
        }
			}
			finally {
				isUpdating = false;
			}
		}
	}

	
	@Override
	public void highlightingChanged(ChangeEvent e) {}


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


	public TopologicalCalculator getTopologicalCalculator() {
		return topologicalCalculator;
	}


	@Override
	public void componentAdded(ContainerEvent e) {
		if (e.getChild() instanceof TreeInternalFrame) {
			TreeInternalFrame addedFrame = ((TreeInternalFrame) e.getChild());
			addedFrame.getTreeViewPanel().addTreeViewPanelListener(this);
			addedFrame.getTreeViewPanel().getDocument().addView(this);
			reset();
		}
	}


	@Override
	public void componentRemoved(ContainerEvent e) {
		((TreeInternalFrame) e.getChild()).getTreeViewPanel().removeTreeViewPanelListener(this);
		((TreeInternalFrame) e.getChild()).getTreeViewPanel().getDocument().removeView(this);
	}
}