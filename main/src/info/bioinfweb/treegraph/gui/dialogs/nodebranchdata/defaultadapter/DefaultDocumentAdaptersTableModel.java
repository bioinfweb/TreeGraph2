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
package info.bioinfweb.treegraph.gui.dialogs.nodebranchdata.defaultadapter;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.VoidNodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.tools.NodeBranchDataColumnAnalyzer;
import info.bioinfweb.treegraph.gui.dialogs.nodebranchdatainput.NodeBranchDataComboBoxModel;
import info.bioinfweb.treegraph.gui.mainframe.MainFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeInternalFrame;



/**
 * Table model for the {@link DefaultDocumentAdapterDialog} containing the default document adapters of all currently opened documents.
 * 
 * @author Ben St&ouml;ver
 * @since 2.16.0
 */
public class DefaultDocumentAdaptersTableModel extends AbstractTableModel {
	public static final int FILE_NAME_COLUMN = 0;
	public static final int LEAF_ADAPTER_COLUMN = 1;
	public static final int SUPPORT_ADAPTER_COLUMN = 2;
	public static final int PATH_COLUMN = 3;
	
	
	private static class DocumentListElement {
		public Document document;
		public NodeBranchDataAdapter selectedLeafAdapter;  // The selected item of leafAdapters cannot be used since JTable will reset this before displaying a cell editor.
		public NodeBranchDataComboBoxModel leafAdapters;
		public NodeBranchDataAdapter selectedSupportAdapter;  // The selected item of supportAdapters cannot be used since JTable will reset this before displaying a cell editor.
		public NodeBranchDataComboBoxModel supportAdapters;
		
		
		private void sortSupportAdapters() {
			List<NodeBranchDataAdapter> adapters = new ArrayList<NodeBranchDataAdapter>(supportAdapters.getAdapters());
			NodeBranchDataColumnAnalyzer.sortColumnListByStatus(document.getTree(), adapters);
			supportAdapters.replaceAdapterListContents(adapters);
		}

		
		public DocumentListElement(Document document) {
			super();
			this.document = document;
			
			leafAdapters = new NodeBranchDataComboBoxModel();
			leafAdapters.setAdapters(document.getTree(), true, true, false, false, false, null);
			selectedLeafAdapter = document.getDefaultLeafAdapter();
			
			supportAdapters = new NodeBranchDataComboBoxModel();
			supportAdapters.setAdapters(document.getTree(), false, true, true, false, false, "No support values available");  // DecimalOnly is not set because previously set default document adapters may not be displayed then.
			sortSupportAdapters();
			selectedSupportAdapter = document.getDefaultSupportAdapter();
		}
	}
	
	
	private List<DocumentListElement> documentList = new ArrayList<DocumentListElement>();
	
	
	/**
	 * Refreshes the contents of this model to contain all currently opened documents.
	 */
	public void refreshDocuments(boolean allowEmptyDocuments) {  //TODO Keep this parameter?
		documentList.clear();
		Iterator<TreeInternalFrame> iterator = MainFrame.getInstance().treeFrameIterator();
		while (iterator.hasNext()) {
			Document document = iterator.next().getDocument();
			if (allowEmptyDocuments || !document.getTree().isEmpty()) {
				documentList.add(new DocumentListElement(document));
			}
		}
	}
	
	
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
			case LEAF_ADAPTER_COLUMN:
			case SUPPORT_ADAPTER_COLUMN:
				return NodeBranchDataAdapter.class;
			
			default:
				return String.class;
		}
	}



	@Override
	public String getColumnName(int columnIndex) {
		switch (columnIndex) {
			case FILE_NAME_COLUMN:
				return "Name";
			
			case LEAF_ADAPTER_COLUMN:
				return "Default leaf adapter";
			
			case SUPPORT_ADAPTER_COLUMN:
				return "Default support adapter";
			
			case PATH_COLUMN:
				return "Path";
			
			default:
				return null;
		}
	}


	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return (columnIndex == LEAF_ADAPTER_COLUMN) || (columnIndex == SUPPORT_ADAPTER_COLUMN);
	}


	@Override
	public int getColumnCount() {
		return 4;
	}

	
	@Override
	public int getRowCount() {
		return documentList.size();
	}

	
	public Document getDocument(int rowIndex) {
		return documentList.get(rowIndex).document;
	}
	
	
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		DocumentListElement element = documentList.get(rowIndex);
		switch (columnIndex) {
			case FILE_NAME_COLUMN:
					if (element.document.getFile() != null) {
						return element.document.getFile().getName();
					}
					else {
						return element.document.getDefaultNameOrPath();
					}
			
			case LEAF_ADAPTER_COLUMN:
				return element.selectedLeafAdapter;
			
			case SUPPORT_ADAPTER_COLUMN:
				return element.selectedSupportAdapter;
			
			case PATH_COLUMN:
				if (element.document.getFile() != null) {
					return element.document.getFile().getAbsolutePath();
				}
				else {
					return "";
				}
			
			default:
				return null;
		}
	}


	public NodeBranchDataAdapter getSelectedLeafAdapter(int rowIndex) {
		return documentList.get(rowIndex).selectedLeafAdapter;
	}
	

	public NodeBranchDataAdapter getSelectedSupportAdapter(int rowIndex) {
		return documentList.get(rowIndex).selectedSupportAdapter;
	}
	
	
	public NodeBranchDataComboBoxModel getLeafComboBoxModel(int rowIndex) {
		return documentList.get(rowIndex).leafAdapters;
	}
	

	public NodeBranchDataComboBoxModel getSupportComboBoxModel(int rowIndex) {
		return documentList.get(rowIndex).supportAdapters;
	}
	

	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		DocumentListElement element = documentList.get(rowIndex);
		switch (columnIndex) {
			case LEAF_ADAPTER_COLUMN:
				element.selectedLeafAdapter = (NodeBranchDataAdapter)value;
				fireTableCellUpdated(rowIndex, columnIndex);
				break;
			
			case SUPPORT_ADAPTER_COLUMN:
				element.selectedSupportAdapter = (NodeBranchDataAdapter)value;
				fireTableCellUpdated(rowIndex, columnIndex);
				break;
		}
	}
	
	
	public void setLeafAdapterToAll(NodeBranchDataAdapter adapter) {
		for (int rowIndex = 0; rowIndex < documentList.size(); rowIndex++) {
			DocumentListElement element = documentList.get(rowIndex);
			if (element.leafAdapters.getAdapters().contains(adapter)) {
				element.selectedLeafAdapter = adapter;
				fireTableCellUpdated(rowIndex, LEAF_ADAPTER_COLUMN);
			}
		}
	}
	
	
	public void setSupportAdapterToAll(NodeBranchDataAdapter adapter) {
		for (int rowIndex = 0; rowIndex < documentList.size(); rowIndex++) {
			DocumentListElement element = documentList.get(rowIndex);
			if (element.supportAdapters.getAdapters().contains(adapter)) {
				element.selectedSupportAdapter = adapter;
				fireTableCellUpdated(rowIndex, SUPPORT_ADAPTER_COLUMN);
			}
		}
	}
	
	
	/**
	 * Sets all or all undefined default support adapters to the first entry of their combo box model. (These entries are
	 * sorted by suitability.)
	 * 
	 * @param onlyUndefined Specify {@code true} here if only currently undefined default columns should be set or {@code false}
	 *        if all columns should be set.
	 */
	public void autoSelectSupportColumns(boolean onlyUndefined) {
		for (int rowIndex = 0; rowIndex < getRowCount(); rowIndex++) {
			if (!onlyUndefined || getValueAt(rowIndex, SUPPORT_ADAPTER_COLUMN) instanceof VoidNodeBranchDataAdapter) {
				setValueAt(documentList.get(rowIndex).supportAdapters.getAdapters().get(0), rowIndex, SUPPORT_ADAPTER_COLUMN);  // Range check should not be needed, since each list must at least contain a void adapter.
			}
		}
	}
}
