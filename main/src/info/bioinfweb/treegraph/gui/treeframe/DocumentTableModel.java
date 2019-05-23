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
package info.bioinfweb.treegraph.gui.treeframe;


import java.util.List;
import java.util.Vector;

import info.bioinfweb.treegraph.document.AbstractPaintableElement;
import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.TextLabel;
import info.bioinfweb.treegraph.document.change.DocumentChangeEvent;
import info.bioinfweb.treegraph.document.change.DocumentListener;
import info.bioinfweb.treegraph.document.nodebranchdata.BranchLengthAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.GeneralIDAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.HiddenBranchDataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.HiddenNodeDataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.TextLabelAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeNameAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.AbstractTextElementDataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.UniqueNameAdapter;
import info.bioinfweb.treegraph.document.tools.NodeBranchDataColumnManager;
import info.bioinfweb.treegraph.document.undo.edit.ChangeCellTypeEdit;
import info.bioinfweb.treegraph.document.undo.edit.ChangeNumercalValueEdit;
import info.bioinfweb.treegraph.document.undo.edit.ChangeTextualValueEdit;
import info.bioinfweb.commons.Math2;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;



/**
 * Table modes to display the node/branch data of a document in a {@link JTable}.
 * 
 * @author Ben St&ouml;ver
 */
public class DocumentTableModel extends AbstractTableModel implements DocumentListener {
	public static final int COL_UNIQUE_NAME_DATA_TYPE = 1;
	public static final int COL_BRANCH_LENGTH_DATA_TYPE = 5;
	public static final String DECIMAL_COLUMN_HEADING = "Dec";
	
	
	private Vector<NodeBranchDataAdapter> adapters = new Vector<NodeBranchDataAdapter>();
	private Vector<Node> nodes = new Vector<Node>();
	private Document document = null;
	
	
	/**
	 * Creates a new <code>DocumentTableModel</code> which shows the tree contained in document.
	 * This class registers itself as a view at the given document.
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
	
	
	private void fillAdapterList(Node root) {
		adapters.clear();
		adapters.add(new UniqueNameAdapter());
		adapters.add(new NodeNameAdapter());
		adapters.add(new BranchLengthAdapter());

		List<String> ids = NodeBranchDataColumnManager.getLabelIDListFromSubtree(root, TextLabel.class);
		for (int i = 0; i < ids.size(); i++) {
			adapters.add(new TextLabelAdapter(ids.get(i), 
					((TextLabel)NodeBranchDataColumnManager.getFirstLabel(root, TextLabel.class, ids.get(i))).getFormats().getDecimalFormat()));
		}
  	        
		ids = NodeBranchDataColumnManager.getHiddenNodeDataIDListFromSubtree(root);
		for (int i = 0; i < ids.size(); i++) {
			adapters.add(new HiddenNodeDataAdapter(ids.get(i)));
		}
		
		ids = NodeBranchDataColumnManager.getHiddenBranchDataIDListFromSubtree(root);
		for (int i = 0; i < ids.size(); i++) {
			adapters.add(new HiddenBranchDataAdapter(ids.get(i)));
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
		if (col % 2 == 0) {
			if (adapters.get(col / 2).decimalOnly()) {
				return Double.class;
			}
			else {
				return Object.class;
			}
		}
		else {
			return Boolean.class;
		}
	}

	
	@Override
	public String getColumnName(int columnIndex) {
		if (columnIndex % 2 == 0) {
			NodeBranchDataAdapter adapter = adapters.get(columnIndex / 2);
			if (adapter instanceof TextLabelAdapter) {
				return ((TextLabelAdapter)adapter).getID() + " (text labels)";
			}
			if (adapter instanceof HiddenNodeDataAdapter) {
				return ((HiddenNodeDataAdapter)adapter).getID() + " (hidden node data)";
			}
			if (adapter instanceof HiddenBranchDataAdapter) {
				return ((HiddenBranchDataAdapter)adapter).getID() + " (hidden branch data)";
			}
			else {
				return adapter.toString();
			}
		}
		else {
			return DECIMAL_COLUMN_HEADING;
		}
	}

	
	@Override
	public boolean isCellEditable(int row, int col) {
		boolean result = (col != COL_UNIQUE_NAME_DATA_TYPE) && 
		    (col != COL_BRANCH_LENGTH_DATA_TYPE)
		    && !(getAdapter(col) instanceof UniqueNameAdapter);
		if (result && (col % 2 == 1)) {
			result = !getAdapter(col).isEmpty(nodes.get(row));
		}
		return result;
	}

	
	public int getColumnCount() {
		return 2 * adapters.size();  // Includes columns to set the data type
	}

	
	public int getRowCount() {
		return nodes.size();
	}

	
	public Object getValueAt(int row, int col) {
		NodeBranchDataAdapter adapter = adapters.get(col / 2);
		Node n = nodes.get(row);
		
		if (col % 2 == 0) {
			if (adapter.isDecimal(n)) {
				return new Double(adapter.getDecimal(n));
			}
			else if (adapter.isString(n)) {
				return adapter.getText(n);
			}
			else {
				return null;  //TODO So sinnvoll?
			}
		}
		else {
			if (adapter.isDecimal(n)) {
				return new Boolean(true);
			}
			else {
				return new Boolean(false);
			}
		}
	}

	
	@Override
	public void setValueAt(Object value, int row, int col) {
		NodeBranchDataAdapter adapter = adapters.get(col / 2);
		Node n = nodes.get(row);
		
		if (col % 2 == 0) {
			String str = "";
			if (value != null) {
				str = value.toString();
			}
			
			if (adapter.isDecimal(n) && Math2.isDecimal(str)) {
				getDocument().executeEdit(new ChangeNumercalValueEdit(getDocument(), adapter, n, 
						Math2.parseDouble(str)));
			}
			else {
				getDocument().executeEdit(new ChangeTextualValueEdit(getDocument(), adapter, n, str));
			}
		}
		else if ((value instanceof Boolean) && (adapter instanceof AbstractTextElementDataAdapter)) {
			getDocument().executeEdit(new ChangeCellTypeEdit(getDocument(), (AbstractTextElementDataAdapter)adapter, 
					n, (Boolean)value));
		}
	}
	
	
	public NodeBranchDataAdapter getAdapter(int col) {
		return adapters.get(col / 2);
	}
	
	
	public int getRow(Node node) {
		return nodes.indexOf(node);
	}
}