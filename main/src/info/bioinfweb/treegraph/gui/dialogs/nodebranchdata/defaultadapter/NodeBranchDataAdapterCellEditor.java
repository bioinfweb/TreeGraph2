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

import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;



public abstract class NodeBranchDataAdapterCellEditor extends AbstractCellEditor implements TableCellEditor {
	public static class LeafAdapterEditor extends NodeBranchDataAdapterCellEditor {
		@Override
		protected void setModel(DefaultDocumentAdaptersTableModel tableModel, int rowIndex) {
			comboBox.setModel(tableModel.getLeafComboBoxModel(rowIndex));
			comboBox.setSelectedItem(tableModel.getSelectedLeafAdapter(rowIndex));
		}
	}
	
	
	public static class SupportAdapterEditor extends NodeBranchDataAdapterCellEditor {
		@Override
		protected void setModel(DefaultDocumentAdaptersTableModel tableModel, int rowIndex) {
			comboBox.setModel(tableModel.getSupportComboBoxModel(rowIndex));
			comboBox.setSelectedItem(tableModel.getSelectedSupportAdapter(rowIndex));
		}
	}
	
	
	protected JComboBox<NodeBranchDataAdapter> comboBox;
	
	
	public NodeBranchDataAdapterCellEditor() {
		super();
		comboBox = new JComboBox<NodeBranchDataAdapter>();  // The model will be set by getTableCellEditorComponent().
	}


	protected abstract void setModel(DefaultDocumentAdaptersTableModel tableModel, int rowIndex);
	
	
	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		if (table.getModel() instanceof DefaultDocumentAdaptersTableModel) {
			setModel((DefaultDocumentAdaptersTableModel)table.getModel(), row);
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
