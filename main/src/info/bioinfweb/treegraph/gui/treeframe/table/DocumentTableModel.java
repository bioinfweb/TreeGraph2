/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2011, 2013-2017  Ben Stöver, Sarah Wiechers, Kai Müller
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
package info.bioinfweb.treegraph.gui.treeframe.table;


import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import info.bioinfweb.commons.Math2;
import info.bioinfweb.treegraph.document.AbstractPaintableElement;
import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.NodeType;
import info.bioinfweb.treegraph.document.TextLabel;
import info.bioinfweb.treegraph.document.change.DocumentChangeEvent;
import info.bioinfweb.treegraph.document.change.DocumentListener;
import info.bioinfweb.treegraph.document.metadata.MetadataTree;
import info.bioinfweb.treegraph.document.nodebranchdata.BranchLengthAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeNameAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.TextLabelAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.UniqueNameAdapter;
import info.bioinfweb.treegraph.document.tools.IDManager;
import info.bioinfweb.treegraph.document.tools.PathManager;
import info.bioinfweb.treegraph.document.undo.edit.ChangeNumercalValueEdit;
import info.bioinfweb.treegraph.document.undo.edit.ChangeTextualValueEdit;



/**
 * Table modes to display the node/branch data of a document in a {@link JTable}.
 * 
 * @author Ben St&ouml;ver
 */
public class DocumentTableModel extends AbstractTableModel implements DocumentListener {
	public static final int ROW_DATA_TYPE = 0;
	public static final int COLUMN_UNIQUE_NAMES = 0;
	
	
	private Document document = null;
	private MetadataTree nodeTree;
	private MetadataTree branchTree;
	private List<NodeBranchDataAdapter> adapters = new ArrayList<NodeBranchDataAdapter>();
	private List<Node> nodes = new ArrayList<Node>();
	private int[] subtreeDepths;
	private int maxTreeDepth = 0;
	private int nodeTreeDepth = 0;
	private int branchTreeDepth = 0;
	
	
	/**
	 * Creates a new <code>DocumentTableModel</code> which shows the tree contained in document.
	 * This class registers itself as a view at the given document.
	 * 
	 * @param document the document to show
	 */
	public DocumentTableModel(Document document) {
		super();
		setDocument(document);
	}
	

	public Document getDocument() {
		return document;
	}


	/**
	 * Sets this view to a new model and repaints the element using the data of the 
	 * new document. This view is automatically unregistered at the old and registered at 
	 * the new document.
	 * 
	 * @param document the model to associate with
	 */
	public void setDocument(Document document) {
		if (this.document != null) {  // Beim Aufruf im Konstruktor.
			this.document.removeView(this);
		}
		
		this.document = document;
		if (document != null) {
			document.addView(this);
		  changeHappened(new DocumentChangeEvent(document, null));
		}
	}


  public MetadataTree getNodeTree() {
		return nodeTree;
	}


	public MetadataTree getBranchTree() {
		return branchTree;
	}


	/**
	 * Returns the length of the longest path from the root any leaf in both the node and the branch metadata tree.
	 * <p>
	 * The returned value is updated each time a {@link DocumentChangeEvent} is received. Before the first event is received,
	 * the returned length will always be 0.
	 * 
	 * @return the length of longest path in the metadata trees
	 */
	public int getMaxTreeDepth() {
		return maxTreeDepth;
	}
	
	
	public int getNodeTreeDepth() {
		return nodeTreeDepth;
	}


	public int getBranchTreeDepth() {
		return branchTreeDepth;
	}


	public int getSubtreeDepth(int column) {
		return subtreeDepths[column];
	}


	public AbstractPaintableElement getTreeElement(int row, int col) {
  	return getAdapter(col).getDataElement(nodes.get(row));
  }
  
  
	public void changeHappened(DocumentChangeEvent e) {
		fillAdapterList(getDocument().getTree().getPaintStart());
		nodes.clear();
		if (!getDocument().getTree().isEmpty()) {
			fillNodeList(getDocument().getTree().getPaintStart());
		}
		fireTableStructureChanged();
	}
	
	
	private void updateSubtreeDepths() {
		List<Integer> nodeTreeDepths = nodeTree.determineSubtreeDepths();
		List<Integer> branchTreeDepths = branchTree.determineSubtreeDepths();
		
		subtreeDepths = new int[1 + nodeTreeDepths.size() + branchTreeDepths.size()];
		subtreeDepths[0] = 0;  // Unique node names have no levels displayed underneath them.
		
		int offset = 1;
		for (int i = 0; i < nodeTreeDepths.size(); i++) {
			subtreeDepths[offset + i] = nodeTreeDepths.get(i);
		}
		
		offset = 1 + nodeTreeDepths.size();
		for (int i = 0; i < branchTreeDepths.size(); i++) {
			subtreeDepths[offset + i] = branchTreeDepths.get(i);
		}
		
		nodeTreeDepth = nodeTreeDepths.get(0) + 1;  // + 1 to count the level for the "node" and "branch" roots.
		branchTreeDepth = branchTreeDepths.get(0) + 1;
		maxTreeDepth = Math.max(nodeTreeDepth, branchTreeDepth);
	}
	
	
	private void fillAdapterList(Node root) {
		nodeTree = PathManager.createCombinedMetadataTreeFromNodes(root, NodeType.BOTH);  //TODO Does this take too long for large phylogenetic trees?
		branchTree = PathManager.createCombinedMetadataTreeFromBranches(root, NodeType.BOTH);
		updateSubtreeDepths();
		
		adapters.clear();
		adapters.add(new UniqueNameAdapter());
		
		adapters.add(new NodeNameAdapter());
		adapters.addAll(PathManager.createAdapterList(NodeType.BOTH, nodeTree));
		
		adapters.add(new BranchLengthAdapter());
		adapters.addAll(PathManager.createAdapterList(NodeType.BOTH, branchTree));

		// TODO The following can be removed as soon as text label do not store data anymore but reference a metadata path.
		List<String> ids = IDManager.getLabelIDListFromSubtree(root, TextLabel.class);
		for (int i = 0; i < ids.size(); i++) {
			adapters.add(new TextLabelAdapter(ids.get(i), 
					((TextLabel)IDManager.getFirstLabel(root, TextLabel.class, ids.get(i))).getFormats().getDecimalFormat()));
		}
	}
	
	
	private void fillNodeList(Node root) {
		nodes.add(root);
		for (int i = 0; i < root.getChildren().size(); i++) {
			fillNodeList(root.getChildren().get(i));
		}
	}
	
	
  @Override
	public Class<?> getColumnClass(int col) {
		if (adapters.get(col).decimalOnly()) {
			return Double.class;
		}
		else {
			return Object.class;
		}
	}

	
	@Override
	public String getColumnName(int columnIndex) {
		NodeBranchDataAdapter adapter = adapters.get(columnIndex);
		if (adapter instanceof TextLabelAdapter) {
			return ((TextLabelAdapter)adapter).getID() + " (text labels)";
		}
		else {
			return adapter.toString();
		}
	}

	
	@Override
	public boolean isCellEditable(int row, int col) {
		return !(getAdapter(col) instanceof UniqueNameAdapter);
		//TODO Some lines of a column may not be editable, because parent or child values (in other columns of that line) are editable.
		//     - May resource metadata elements have URLs and child nodes at the same time?
		//     - Should using the same predicated for literal and parent resource metadata on different node be discouraged anyway?
	}

	
	@Override
	public int getColumnCount() {
		return adapters.size();
	}

	
	@Override
	public int getRowCount() {
		return nodes.size() + 1;  // First line contains the data type list.
	}

	
	@Override
	public Object getValueAt(int row, int col) {
		if (row == ROW_DATA_TYPE) {
			//TODO Return data type
			return null;
		}
		else {
			NodeBranchDataAdapter adapter = adapters.get(col);
			Node n = nodes.get(row - 1);
			
			if (adapter.isDecimal(n)) {
				return new Double(adapter.getDecimal(n));
			}
			else if (adapter.isString(n)) {  //TODO Adjust this when more data types are supported/available.
				return adapter.getText(n);
			}
			else {
				return null;  //TODO So sinnvoll?
			}
		}
	}

	
	@Override
	public void setValueAt(Object value, int row, int col) {
		if (row == ROW_DATA_TYPE) {
			//TODO Set data type similar to the (previous) code below.
//			getDocument().executeEdit(new ChangeCellTypeEdit(getDocument(), (AbstractTextElementDataAdapter)adapter, 
//					n, (Boolean)value));
		}
		else {
			NodeBranchDataAdapter adapter = adapters.get(col);
			Node n = nodes.get(row - 1);
			
			String str = "";
			if (value != null) {
				str = value.toString();
			}
			
			if (adapter.isDecimal(n) && Math2.isDecimal(str)) {
				getDocument().executeEdit(new ChangeNumercalValueEdit(getDocument(), adapter, n, 
						Math2.parseDouble(str)));
			}
			else {  //TODO Adjust when more data types  are supported/available.
				getDocument().executeEdit(new ChangeTextualValueEdit(getDocument(), adapter, n, str));
			}
		}
	}
	
	
	public NodeBranchDataAdapter getAdapter(int col) {
		return adapters.get(col);
	}
	
	
	public int getRow(Node node) {
		return nodes.indexOf(node) + 1;
	}
}