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


import java.awt.Component;

import javax.swing.AbstractCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.gui.dialogs.nodebranchdatainput.NodeBranchDataComboBoxModel;



public abstract class NodeBranchDataAdapterCellEditor extends AbstractCellEditor implements TableCellEditor {
	public static class LeafAdapterEditor extends NodeBranchDataAdapterCellEditor {
		@Override
		protected NodeBranchDataAdapter getDefaultAdapter(Document document) {
			return document.getDefaultLeafAdapter();
		}

		@Override
		protected void setAdapters(NodeBranchDataComboBoxModel model, Document document) {
			model.setAdapters(document.getTree(), true, true, false, false, false, null);
		}
	}
	
	
	public static class SupportAdapterEditor extends NodeBranchDataAdapterCellEditor {
		@Override
		protected NodeBranchDataAdapter getDefaultAdapter(Document document) {
			return document.getDefaultSupportAdapter();
		}

		@Override
		protected void setAdapters(NodeBranchDataComboBoxModel model, Document document) {
			model.setAdapters(document.getTree(), false, true, true, false, false, "No support values available");  // DecimalOnly is not set because previously set default document adapters may not be displayed then.
		}
	}
	
	
	JComboBox<NodeBranchDataAdapter> comboBox;  //TODO The current implementation of using a shared combo box model might be too slow for large trees, since the whole tree is searched for node/branch data columns each time a cell in the table is selected in getDefaultAdapter().
	
	
	public NodeBranchDataAdapterCellEditor() {
		super();
		
		comboBox = new JComboBox<NodeBranchDataAdapter>(new NodeBranchDataComboBoxModel());
	}


	protected abstract NodeBranchDataAdapter getDefaultAdapter(Document document);
	
	
	protected abstract void setAdapters(NodeBranchDataComboBoxModel model, Document document);
	
	
	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		if (table.getModel() instanceof DefaultDocumentAdaptersTableModel) {
			DefaultDocumentAdaptersTableModel model = (DefaultDocumentAdaptersTableModel)table.getModel();
			Document document = model.getDocument(row);
			setAdapters((NodeBranchDataComboBoxModel)comboBox.getModel(), document);
			
			comboBox.setSelectedItem(model.getValueAt(row, column)); 
			return comboBox;
		}
		else {
			throw new InternalError("Table model instance of type " + table.getModel().getClass().getCanonicalName() + " found but expected " + 
					DefaultDocumentAdaptersTableModel.class.getCanonicalName() + ". Contact support@bioinfweb.info with this error message if you encounter this error.");
		}
	}
	

	@Override
	public Object getCellEditorValue() {
		return comboBox.getSelectedItem();
	}
}
