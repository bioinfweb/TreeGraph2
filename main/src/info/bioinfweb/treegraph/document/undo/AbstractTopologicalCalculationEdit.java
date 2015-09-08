package info.bioinfweb.treegraph.document.undo;



import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.TextElementData;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.topologicalcalculation.LeafSet;
import info.bioinfweb.treegraph.document.topologicalcalculation.NodeInfo;
import info.bioinfweb.treegraph.document.topologicalcalculation.TopologicalCalculator;
import info.bioinfweb.treegraph.document.undo.file.ImportDataColumnsParameters;



/**
 * Implements basic functionalities for all edits that depend on calculations based on the tree topology
 * and therefore need the tree nodes to be decorated with information in their subtrees. 
 * 
 * @author Ben St&ouml;ver
 */
public abstract class AbstractTopologicalCalculationEdit extends ComplexDocumentEdit {
	protected TopologicalCalculator topologicalCalculator = null;
	
	
	public AbstractTopologicalCalculationEdit(Document document,
			NodeBranchDataAdapter targetLeafsAdapter, boolean processRooted) {
	
		super(document);
		topologicalCalculator = new TopologicalCalculator(document, targetLeafsAdapter, processRooted);
	}

	
	public TopologicalCalculator getTopologicalCalculator() {
		return topologicalCalculator;
	}

	
	/**
	 * Checks if both the loaded and the imported tree contain exactly the same terminals.
	 * 
	 * @return an error message, if the terminal nodes are not identical or <code>null</code> if they are
	 */
	protected String compareLeafs(Document src) {
		return topologicalCalculator.compareLeafs(src);
	}


	/**
	 * Returns the leaf field attribute of <code>node</code> if it has one attached. If not an according object
	 * is created first and than returned.
	 * 
	 * @param node - the node from which the leaf field attribute shall be returned or created. 
	 */
	protected LeafSet getLeafSet(Node node) {
		return topologicalCalculator.getLeafSet(node);
	}
	
	
	protected int getLeafIndex(TextElementData value, ImportDataColumnsParameters parameters) {
		return topologicalCalculator.getLeafIndex(value, parameters);
	}
	
	
	protected int getLeafCount() {
		return topologicalCalculator.getLeafCount();
	}
	

	/**
	 * Adds a boolean set which indicates the leafs located under <code>root</code> to
	 * the attribute map of <code>root</code>.`
	 * 
	 * @param root - the root of the subtree
	 */
	protected void addLeafSets(Node root, NodeBranchDataAdapter adapter) {
		topologicalCalculator.addLeafSets(root, adapter);
	}	
	
	
	protected NodeInfo findSourceNodeWithAllLeafs(Node sourceRoot, LeafSet targetLeafs) {
		return topologicalCalculator.findSourceNodeWithAllLeafs(sourceRoot, targetLeafs);
	}
}