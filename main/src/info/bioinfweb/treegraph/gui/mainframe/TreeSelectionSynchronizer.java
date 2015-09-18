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
package info.bioinfweb.treegraph.gui.mainframe;


import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.TextElementData;
import info.bioinfweb.treegraph.document.change.DocumentChangeEvent;
import info.bioinfweb.treegraph.document.change.DocumentChangeType;
import info.bioinfweb.treegraph.document.change.DocumentListener;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.topologicalcalculation.NodeInfo;
import info.bioinfweb.treegraph.document.topologicalcalculation.TopologicalCalculator;
import info.bioinfweb.treegraph.document.undo.CompareTextElementDataParameters;
import info.bioinfweb.treegraph.document.undo.SelectionSynchronizationCompareParamters;
import info.bioinfweb.treegraph.gui.treeframe.TreeInternalFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeSelection;
import info.bioinfweb.treegraph.gui.treeframe.TreeViewPanel;
import info.bioinfweb.treegraph.gui.treeframe.TreeViewPanelListener;

import java.util.Iterator;
import java.util.Map;

import javax.swing.Action;
import javax.swing.event.ChangeEvent;

import org.lsmp.djep.vectorJep.function.GetDiagonal;



public class TreeSelectionSynchronizer implements TreeViewPanelListener, DocumentListener {
	public static final String KEY_LEAF_REFERENCE = TreeSelectionSynchronizer.class.getName() + ".LeafSet";
	
	private MainFrame owner;
	private boolean isUpdating = false;
	private TopologicalCalculator topologicalCalculator = null;
	private SelectionSynchronizationCompareParamters compareParameters = new SelectionSynchronizationCompareParamters();
	
	
	public TreeSelectionSynchronizer(MainFrame owner) {
		super();
		this.owner = owner;		
	}


	public MainFrame getOwner() {
		return owner;
	}
	
	
	public SelectionSynchronizationCompareParamters getCompareParameters() {
		return compareParameters;
	}


	public void reset() {
		TreeInternalFrame frame = getOwner().getActiveTreeFrame();
		NodeBranchDataAdapter adapter = frame.getDocument().getDefaultLeafAdapter();
		topologicalCalculator = new TopologicalCalculator(frame.getDocument(), adapter, compareParameters.isProcessRooted(), KEY_LEAF_REFERENCE, compareParameters);
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
				NodeInfo sourceNodeInfo = topologicalCalculator.findSourceNodeWithAllLeafs(target.getDocument().getTree(), target.getDocument().getTree().getPaintStart(), topologicalCalculator.getLeafSet(node));
				selection.add(sourceNodeInfo.getNode());
//				if (sourceNode.getAdditionalCount() == 0) {
//					selection.add(sourceNode.getNode());
//				}
//				else {
//					System.out.println("Additional count:" + sourceNode.getAdditionalCount());
//				}
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