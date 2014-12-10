package info.bioinfweb.treegraph.document.undo.topologicalcalculation;


import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.TextElementData;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeNameAdapter;
import info.bioinfweb.treegraph.document.undo.ComplexDocumentEdit;



/**
 * Implements basic functionalities for all edits that depend on calculations based on the tree topology
 * and therefore need the tree nodes to be decorated with information in their subtrees. 
 * 
 * @author Ben St&ouml;ver
 */
public abstract class AbstractTopologicalCalculationEdit extends ComplexDocumentEdit {
	public static final String KEY_LEAF_REFERENCE = AbstractTopologicalCalculationEdit.class.getName() + ".LeafSet";
	public static final int MAX_TERMINAL_ERROR_COUNT = 10;
	public static final NodeNameAdapter SOURCE_LEAFS_ADAPTER = NodeNameAdapter.getSharedInstance();
	
	
	protected List<TextElementData> leafValues = new Vector<TextElementData>();
	protected boolean processRooted;
	
	/** The column that contains the terminal identifiers in the target document (usually nodes names) */
	protected NodeBranchDataAdapter targetLeafsAdapter = null;
	
	
	public AbstractTopologicalCalculationEdit(Document document,
			NodeBranchDataAdapter targetLeafsAdapter, boolean processRooted) {
	
		super(document);
		this.processRooted = processRooted;
		this.targetLeafsAdapter = targetLeafsAdapter;
		addLeafList(leafValues, document.getTree().getPaintStart(), targetLeafsAdapter);  // Source und target sollten das selbe Ergebnis liefern.
	}

	
	/**
	 * Fills the specified list with the values of all leafs under <code>root</code>.
	 * 
	 * @param list
	 * @param root
	 * @param adapter
	 */
	protected void addLeafList(List<TextElementData> list, Node root, NodeBranchDataAdapter adapter) {
		if (root.isLeaf()) {
			list.add(adapter.toTextElementData(root));
		}
		else {
			Iterator<Node> iterator = root.getChildren().iterator();
			while (iterator.hasNext()) {
				addLeafList(list, iterator.next(), adapter);
			}
		}
	}

	
	/**
	 * Checks if both the loaded and the imported tree contain exactly the same terminals.
	 * 
	 * @return an error message, if the terminal nodes are not identical or <code>null</code> if they are
	 */
	protected String compareLeafs(Document src) {
		Vector<TextElementData> sourceLeafValues = new Vector<TextElementData>();
		addLeafList(sourceLeafValues, src.getTree().getPaintStart(), SOURCE_LEAFS_ADAPTER);
		if (leafValues.size() != sourceLeafValues.size()) {
			return "The selected tree has different number of terminals than " +
				"the opened document. No support values were added.";
		}
		else {
			String errorMsg = "";
			int errorCount = 0;
			for (int i = 0; i < sourceLeafValues.size(); i++) {
				if (getLeafIndex(sourceLeafValues.get(i)) == -1) {
					if (errorCount < MAX_TERMINAL_ERROR_COUNT) {
						if (!errorMsg.equals("")) {
							errorMsg += ",\n";
						}
						errorMsg += "\"" + sourceLeafValues.get(i) + "\"";
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
	protected LeafSet getLeafSet(Node node) {
		if (node.getAttributeMap().get(KEY_LEAF_REFERENCE) == null) {
			int size = leafValues.size();
			if (processRooted) {
				size++;
			}
			LeafSet field = new LeafSet(size);
			node.getAttributeMap().put(KEY_LEAF_REFERENCE, field);
		}
		return (LeafSet)node.getAttributeMap().get(KEY_LEAF_REFERENCE);
	}

	
	protected int getLeafIndex(TextElementData value) {
		return leafValues.indexOf(value);
	}
	
	
	protected int getLeafCount() {
		return leafValues.size();
	}
	

	/**
	 * Adds a boolean set which indicates the leafs located under <code>root</code> to
	 * the attribute map of <code>root</code>.`
	 * 
	 * @param root - the root of the subtree
	 */
	protected void addLeafSets(Node root, NodeBranchDataAdapter adapter) {
		if (!root.isLeaf()) {
			root.getAttributeMap().remove(KEY_LEAF_REFERENCE);  // Necessary to overwrite possible leaf sets from previous edits which might not be valid anymore.
			LeafSet field = getLeafSet(root);
			
			for (int i = 0; i < root.getChildren().size(); i++) {
				Node child = root.getChildren().get(i);
				addLeafSets(child, adapter);
				if (child.isLeaf()) {
					field.setChild(getLeafIndex(adapter.toTextElementData(child)), true);
				}
				else {
					field.addField(getLeafSet(child));
				}
			}
		}
	}
}