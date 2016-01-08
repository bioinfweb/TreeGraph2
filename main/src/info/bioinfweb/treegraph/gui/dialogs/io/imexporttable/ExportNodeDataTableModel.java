/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2015-2016  Ben Stöver, Sarah Wiechers
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
package info.bioinfweb.treegraph.gui.dialogs.io.imexporttable;


import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.NodeType;
import info.bioinfweb.treegraph.document.io.ancestralstate.BayesTraitsCommandsWriter;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;



public class ExportNodeDataTableModel extends AbstractTableModel {
	private Vector<NodeBranchDataAdapter> adapters = new Vector<NodeBranchDataAdapter>();
	private NodeType nodeType = null;
	private OutputStreamWriter writer = null;

	
	public ExportNodeDataTableModel() {
		super();
	}


	public NodeBranchDataAdapter get(int index) {
		return adapters.get(index);
	}


	public boolean add(NodeBranchDataAdapter adapter) {
		boolean result = adapters.add(adapter);
		if (result) {
			int index = size() - 1;
			fireTableRowsInserted(index, index);
		}
		return result;
	}


	public NodeBranchDataAdapter set(int index, NodeBranchDataAdapter adapter) {
		if ((index >= 0) && (index < size())) {
			NodeBranchDataAdapter result = adapters.set(index, adapter);
			if (result != null) {
				fireTableRowsUpdated(index, index);
			}
			return result;
		}
		else {
			throw new IllegalArgumentException("The index " + index + " is not valid.");
		}
	}


	public NodeBranchDataAdapter remove(int index) {
		if ((index >= 0) && (index < size())) {
			NodeBranchDataAdapter result = adapters.remove(index);
			if (result != null) {
				fireTableRowsDeleted(index, index);
			}
			return result;
		}
		else {
			throw new IllegalArgumentException("The index " + index + " is not valid.");
		}
	}


	public void clear() {
		adapters.clear();
		fireTableStructureChanged();
	}
	
	
	public void moveUp(int index) {
		if ((index >= 1) && (index < size())) {
			NodeBranchDataAdapter save = get(index - 1);
			set(index - 1, get(index));
			set(index, save);
			fireTableRowsUpdated(index - 1, index);
		}
		else {
			throw new IllegalArgumentException("The index " + index + " is not valid.");
		}
	}


	public void moveDown(int index) {
		if ((index >= 0) && (index < size() - 1)) {
			NodeBranchDataAdapter save = get(index + 1);
			set(index + 1, get(index));
			set(index, save);
			fireTableRowsUpdated(index, index + 1);
		}
		else {
			throw new IllegalArgumentException("The index " + index + " is not valid.");
		}
	}


	public int size() {
		return adapters.size();
	}


	public boolean isEmpty() {
		return adapters.isEmpty();
	}


	@Override
	public Class<?> getColumnClass(int col) {
  	return String.class;
	}

	
	@Override
	public String getColumnName(int col) {
		if (col == 0) {
			return "Column";
		}
		else {  // nur noch 1 möglich
			return "Data";
		}
	}
	

	public int getColumnCount() {
		return 2;
	}
	

	public int getRowCount() {
		return adapters.size();
	}
	

	public Object getValueAt(int row, int col) {
		if (col == 0) {
			return "" + row;
		}
		else {  // nur noch 1 möglich
			return adapters.get(row).toString();
		}
	}
	
	
	private String getValue(NodeBranchDataAdapter adapter, Node node, boolean replaceSpaces) {
		if (adapter.isString(node)) {
			String nodeName = adapter.getText(node);
			if (replaceSpaces && nodeName.contains(" ")) {
				return nodeName.replaceAll(" ", "_");
			}
			else {
				return nodeName;
			}
		}
		else if (adapter.isDecimal(node)) {
			return BayesTraitsCommandsWriter.DECIMAL_INTEGER_FORMAT.format(adapter.getDecimal(node));
		}
		else {
			return "";
		}
	}
	
	
	/**
	 * Writes the node data of the subtree under root as a text. Each node has its own
	 * line in which the values are separated by tabs.
	 * @param writer - the writer to write the data with
	 * @param root - the root node of the subtree to be written
	 * @throws IOException
	 */
	private void writeSubtree(Node root, boolean replaceSpaces) throws IOException {
		if ((root.isLeaf() && !nodeType.equals(NodeType.INTERNAL_NODES)) || (!root.isLeaf() && !nodeType.equals(NodeType.LEAVES))) {
			String line = "";
			if (adapters.size() > 0) {
				for (int i = 0; i < adapters.size() - 1; i++) {
					line += getValue(adapters.get(i), root, replaceSpaces) + "\t";
				}
				line += getValue(adapters.get(adapters.size() - 1), root, replaceSpaces) + System.getProperty("line.separator");
			}
  		writer.write(line);
		}
		
		for (int i = 0; i < root.getChildren().size(); i++) {
			writeSubtree(root.getChildren().get(i), replaceSpaces);
		}
	}
	
	
	public void writeData(OutputStream stream, Node root, NodeType nodeType, boolean exportHeadings, boolean replaceSpaces) throws IOException {
		writer = new OutputStreamWriter(stream);
		this.nodeType = nodeType;
		try {
			if (exportHeadings) {
				String headings = "";
				if (adapters.size() > 0) {
					for (int i = 0; i < adapters.size() - 1; i++) {
						headings += adapters.get(i).toString() + "\t";
					}
					headings += adapters.get(adapters.size() - 1).toString() + System.getProperty("line.separator");
				}
				writer.write(headings);
			}
			
			writeSubtree(root, replaceSpaces);
		}
		finally {
			writer.close();
		}
	}
	
	
	/**
	 * @param file
	 * @param root
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void writeData(File file, Node root, NodeType nodeType, boolean exportHeadings, boolean replaceSpaces) throws FileNotFoundException, IOException {
		writeData(new FileOutputStream(file), root, nodeType, exportHeadings, replaceSpaces);
	}
}