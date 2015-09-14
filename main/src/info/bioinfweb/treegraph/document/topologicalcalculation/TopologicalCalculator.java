package info.bioinfweb.treegraph.document.topologicalcalculation;


import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.TextElementData;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeNameAdapter;
import info.bioinfweb.treegraph.document.topologicalcalculation.LeafSet;
import info.bioinfweb.treegraph.document.topologicalcalculation.NodeInfo;
import info.bioinfweb.treegraph.document.undo.ImportTextElementDataParameters;



/**
 * Implements basic functionalities for all edits that depend on calculations based on the tree topology
 * and therefore need the tree nodes to be decorated with information in their subtrees. 
 * 
 * @author Ben St&ouml;ver
 */
public class TopologicalCalculator {
	public static final int MAX_TERMINAL_ERROR_COUNT = 10;
	public static final NodeNameAdapter SOURCE_LEAFS_ADAPTER = NodeNameAdapter.getSharedInstance();
	
	
	protected Map<TextElementData, Integer> leafValues = new TreeMap<TextElementData, Integer>();
	protected boolean processRooted;
	protected String keyLeafReference;
	protected ImportTextElementDataParameters parameters;
	
	/** The column that contains the terminal identifiers in the target document (usually nodes names) */
	protected NodeBranchDataAdapter targetLeafsAdapter = null;
	
	
	public TopologicalCalculator(Document document, NodeBranchDataAdapter targetLeafsAdapter, boolean processRooted, 
				String keyLeafReference, ImportTextElementDataParameters parameters) {
		
		this.processRooted = processRooted;
		this.targetLeafsAdapter = targetLeafsAdapter;
		this.keyLeafReference = keyLeafReference;
		this.parameters = parameters;
		
		addLeafMap(leafValues, document.getTree().getPaintStart(), targetLeafsAdapter);
	}


	public NodeBranchDataAdapter getTargetLeafsAdapter() {
		return targetLeafsAdapter;
	}


	public Map<TextElementData, Integer> getLeafValues() {
		return leafValues;
	}


	public boolean isProcessRooted() {
		return processRooted;
	}


	/**
	 * Fills the specified map with the values of all leafs under <code>root</code> and an according index.
	 * 
	 * @param list
	 * @param root
	 * @param adapter
	 */
	public void addLeafMap(Map<TextElementData, Integer> leafMap, Node root, NodeBranchDataAdapter adapter) {
		if (root.isLeaf()) {
			TextElementData data = parameters.createEditedValue(adapter.getText(root));
			if (!leafMap.containsKey(data)) {
				leafMap.put(data, leafMap.size());
			}
		}
		else {
			Iterator<Node> iterator = root.getChildren().iterator();
			while (iterator.hasNext()) {
				addLeafMap(leafMap, iterator.next(), adapter);
			}
		}
	}

	
	/**
	 * Checks if both the loaded and the imported tree contain exactly the same terminals.
	 * 
	 * @return an error message, if the terminal nodes are not identical or <code>null</code> if they are
	 */
	public String compareLeafs(Document src) {
		Map<TextElementData, Integer> sourceLeafValues = new TreeMap<TextElementData, Integer>();
		addLeafMap(sourceLeafValues, src.getTree().getPaintStart(), SOURCE_LEAFS_ADAPTER);
		if (leafValues.size() != sourceLeafValues.size()) {
			return "The selected tree has a different number of terminals than " +
				"the opened document. No support values were added.";
		}
		else {
			String errorMsg = "";
			int errorCount = 0;
			for (TextElementData data : sourceLeafValues.keySet()) {
				if (!getLeafValues().containsKey(parameters.createEditedValue(data.toString()))) {
					if (errorCount < MAX_TERMINAL_ERROR_COUNT) {
						if (!errorMsg.equals("")) {
							errorMsg += ",\n";
						}
						errorMsg += "\"" + data + "\"";
					}
					errorCount++;
				}
			}
			if (errorMsg.equals("")) {
				return null;
			}
			errorMsg = "The selected tree contains the following terminals which are " +
					"not present in the opened document:\n\n" + errorMsg;
			if (errorCount > MAX_TERMINAL_ERROR_COUNT) {
				errorMsg += ", ...\n(" + (errorCount - MAX_TERMINAL_ERROR_COUNT) + " more)";
			}
			return errorMsg + "\n\nNo support values were added.";
		}
	}


	/**
	 * Returns the leaf field attribute of <code>node</code> if it has one attached. If not an according object
	 * is created first and than returned.
	 * 
	 * @param node - the node from which the leaf field attribute shall be returned or created. 
	 */
	public LeafSet getLeafSet(Node node) {
		if (node.getAttributeMap().get(keyLeafReference) == null) {
			int size = leafValues.size();
			if (processRooted) {
				size++;
			}
			LeafSet field = new LeafSet(size);
			node.getAttributeMap().put(keyLeafReference, field);
		}
		return (LeafSet)node.getAttributeMap().get(keyLeafReference);
	}

	
	public int getLeafIndex(String value) {
		TextElementData key = parameters.createEditedValue(value);
		Integer result = leafValues.get(key);
		if (result == null) {
			return -1;
		}
		else {
			return result;
		}
	}
	
	
	public int getLeafCount() {
		return leafValues.size();
	}
	

	/**
	 * Adds a boolean set which indicates the leafs located under <code>root</code> to
	 * the attribute map of <code>root</code>.`
	 * 
	 * @param root - the root of the subtree
	 */
	public void addLeafSets(Node root, NodeBranchDataAdapter adapter) {
		root.getAttributeMap().remove(keyLeafReference);  // Necessary to overwrite possible leaf sets from previous edits which might not be valid anymore.
		LeafSet field = getLeafSet(root);
		if (!root.isLeaf()) {
			for (int i = 0; i < root.getChildren().size(); i++) {
				Node child = root.getChildren().get(i);
				addLeafSets(child, adapter);
				if (child.isLeaf()) {
					field.setChild(getLeafIndex(adapter.toTextElementData(child).toString()), true);
				}
				else {
					field.addField(getLeafSet(child));
				}
			}
		}
		else {
			field.setChild(getLeafIndex(adapter.toTextElementData(root).toString()), true);
		}
	}
	
	
	public NodeInfo findSourceNodeWithAllLeafs(Node sourceRoot, LeafSet targetLeafs) {
//		targetLeafs = targetLeafs.addTo(getLeafSet(sourceRoot)); //TODO passendes leafset erzeugen
		int additionalCount = targetLeafs.compareTo(getLeafSet(sourceRoot), false);
		boolean downwards = additionalCount != -1;
		if (!downwards) {
			additionalCount = targetLeafs.compareTo(getLeafSet(sourceRoot), true);
		}
  	NodeInfo result = new NodeInfo(sourceRoot, additionalCount, downwards);
		for (int i = 0; i < sourceRoot.getChildren().size(); i++) {
			NodeInfo childResult = findSourceNodeWithAllLeafs(sourceRoot.getChildren().get(i), targetLeafs);
			if ((childResult.getAdditionalCount() != -1) && 
					((childResult.getAdditionalCount() < result.getAdditionalCount()) || (result.getAdditionalCount() == -1))) {
				
				result = childResult;
			}
		}
		return result;
	}
	
	
//	public Node findHighestConflictingNode(Node root, Node targetNode, NodeInfo info) {
//		Node highestConflict = null;
//		if ((getLeafSet(root).containsAnyAndOther(getLeafSet(targetNode), false) &&
//				 getLeafSet(root).inSubtreeOf(getLeafSet(info.getNode()), false)))
////				|| (getLeafSet(root).containsAnyAndOther(getLeafSet(targetNode), true) &&
////						getLeafSet(root).inSubtreeOf(getLeafSet(info.getNode()), true))) 
//		{
//			return root;
//		}
//		else {
//			for (int i = 0; i < root.getChildren().size(); i++) {
//				findHighestConflictingNode(root.getChildren().get(i), targetNode, info);
//			}
//		}
//		return highestConflict;
//	}
}