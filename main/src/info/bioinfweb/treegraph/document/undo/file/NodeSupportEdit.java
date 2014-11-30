package info.bioinfweb.treegraph.document.undo.file;


import java.util.HashMap;

import javax.swing.JOptionPane;

import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.io.DocumentIterator;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeNameAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.TextElementDataAdapter;
import info.bioinfweb.treegraph.document.undo.topologicalcalculation.AbstractTopologicalCalculationEdit;
import info.bioinfweb.treegraph.document.undo.topologicalcalculation.LeafSet;
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
		
		super(document,terminalsAdapter, processRooted); 
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
		 addLeafSets(getDocument().getTree().getPaintStart(), targetLeafsAdapter);	
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
			LeafSet field = getLeafSet(root);
			hashMap.put(field, root);
			for (int i = 0; i < root.getChildren().size(); i++) {
				Node child = root.getChildren().get(i);	
				createHashmap(child);
			}
		}
	}
	
	
	private void counter(Node node){
		// System.out.println("" + supportValuesAdapter.getDecimal(node));
		supportValuesAdapter.setDecimal(node, supportValuesAdapter.getDecimal(node) + 1);
	}
	
	
	private void countSimilarNodes(Node root) {
		System.out.println("Count similar nodes");
		addLeafSets(root, sourceLeafsAdapter);

		if (!root.isLeaf()) {
			Node corresponding = hashMap.get(getLeafSet(root));
			System.out.println("" + corresponding + " " + hashMap.size());
			
			if (corresponding != null) {
				counter(corresponding);
			}
			else {
				corresponding = hashMap.get(getLeafSet(root).complement());
				if (corresponding != null){
					counter(corresponding);
				}
			}
		
			for (int i = 0; i < root.getChildren().size(); i++) {
				countSimilarNodes(root.getChildren().get(i));
		}
	}
		
		
	}
//		LeafField field = getLeafField(root);
//		if (hashMap.get(root).hashCode() == rootToCompare.hashCode()){
//			supportValuesAdapter.setDecimal(root, supportValuesAdapter.getDecimal(root)+1);
//		}
//		else if (getLeafField(root).complement().hashCode() == rootToCompare.hashCode()){
//			supportValuesAdapter.setDecimal(root, supportValuesAdapter.getDecimal(root)+1);
//		}
//		
//		if (!root.isLeaf()) {
//			for (int i = 0; i < rootToCompare.getChildren().size(); i++) {
//				root = root.getChildren().get(i);
//				Node child = rootToCompare.getChildren().get(i);
//				countSimilarNodes(root, child);
//			}
//		}
//			
//	}
}

