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
package info.bioinfweb.treegraph.document.undo.file;


import java.util.HashMap;

import javax.swing.JOptionPane;

import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.change.DocumentChangeType;
import info.bioinfweb.treegraph.document.io.DocumentIterator;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeNameAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.TextElementDataAdapter;
import info.bioinfweb.treegraph.document.topologicalcalculation.LeafSet;
import info.bioinfweb.treegraph.document.undo.AbstractTopologicalCalculationEdit;
import info.bioinfweb.treegraph.gui.mainframe.MainFrame;



/**
 * Implements calculating node frequencies from a set of tree topologies. 
 * 
 * @author Ben St&ouml;ver
 */
public class NodeSupportEdit extends AbstractTopologicalCalculationEdit {
	private double treeCounter = 0; 
	private double normalisationBorder = 0;
	private DocumentIterator documentIterator = null; 
	private NodeBranchDataAdapter supportValuesAdapter = null;
	private TextElementDataAdapter sourceLeafsAdapter = NodeNameAdapter.getSharedInstance();
	private HashMap<LeafSet, Node> hashMap = new HashMap<LeafSet, Node>();
	
	
	public NodeSupportEdit(Document document, TextElementDataAdapter terminalsAdapter, 
			NodeBranchDataAdapter supportValuesAdapter, TextElementDataAdapter sourceLeafsAdapter, 
			boolean processRooted, DocumentIterator documentIterator, double normalisationBorder) {
		
		super(document, DocumentChangeType.TOPOLOGICAL_BY_RENAMING, terminalsAdapter, processRooted); 
		this.supportValuesAdapter = supportValuesAdapter;
		this.documentIterator = documentIterator; 
		this.normalisationBorder = normalisationBorder;
	}
	
	
	@Override
	public String getPresentationName() {
		return "Determine node frequency";
	}

	
	@Override
	protected void performRedo() {
		 getTopologicalCalculator().addLeafSets(getDocument().getTree().getPaintStart(), getTargetLeavesAdapter());	
		 createHashmap(getDocument().getTree().getPaintStart());
		 initialiseSupportValues(getDocument().getTree().getPaintStart());
		 try {
			 Document result = documentIterator.next();
			 while (result != null) {
				 treeCounter++; 
				 countSimilarNodes(result.getTree().getPaintStart());
				 result = documentIterator.next();
			}
			 if(normalisationBorder != -1){
				 normalisationValues(getDocument().getTree().getPaintStart());
			 }
		 }
		catch (Exception e) {
			JOptionPane.showMessageDialog(MainFrame.getInstance(), "The following error occured: " +
					e.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			undo();
		}
	}
	
	
	public void initialiseSupportValues(Node node){
		if(!node.isLeaf()){
			supportValuesAdapter.setDecimal(node, 0);
			for (int i = 0; i < node.getChildren().size(); i++) {
				initialiseSupportValues(node.getChildren().get(i));
			}
		}
	}
	
	
	public void normalisationValues(Node node){
		if(!node.isLeaf()){
			supportValuesAdapter.setDecimal(node,(supportValuesAdapter.getDecimal(node) / treeCounter)*normalisationBorder);
			for (int i = 0; i < node.getChildren().size(); i++) {
				normalisationValues(node.getChildren().get(i));
			}
		}
	}
	
	
	public void createHashmap (Node root){
		if (!root.isLeaf()) {
			LeafSet field = getTopologicalCalculator().getLeafSet(root);
			hashMap.put(field, root);
			for (int i = 0; i < root.getChildren().size(); i++) {
				Node child = root.getChildren().get(i);	
				createHashmap(child);
			}
		}
	}
	
	
	private void counter(Node node){
		supportValuesAdapter.setDecimal(node, supportValuesAdapter.getDecimal(node) + 1);
	}
	
	
	private void countSimilarNodes(Node root) {
		getTopologicalCalculator().addLeafSets(root, sourceLeafsAdapter);

		if (!root.isLeaf()) {
			Node corresponding = hashMap.get(getTopologicalCalculator().getLeafSet(root));
			if (corresponding != null) {
				counter(corresponding);
			}
			else {
				corresponding = hashMap.get(getTopologicalCalculator().getLeafSet(root).complement());
				if (corresponding != null){
					counter(corresponding);
				}
			}
		
			for (int i = 0; i < root.getChildren().size(); i++) {
				countSimilarNodes(root.getChildren().get(i));
			}
		}
	}
}

