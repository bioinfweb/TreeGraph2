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
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.undo.edit.DefaultDocumentAdapterEdit;
import info.bioinfweb.treegraph.gui.dialogs.EditDialog;
import info.bioinfweb.treegraph.gui.mainframe.MainFrame;



public class DefaultDocumentAdapterDialog extends EditDialog {
	public static final int MIN_COLUMN_WIDTH = 30;
	
	
	private JPanel jContentPane = null;
	private JTable table;
	
	
	public DefaultDocumentAdapterDialog(MainFrame mainFrame) {
		super(mainFrame);
		setHelpCode(79);  //TODO Set new help code.
		initialize();
		setLocationRelativeTo(mainFrame);
	}

	
	private int activeDocumentRow() {
		for (int rowIndex = 0; rowIndex < getTableModel().getRowCount(); rowIndex++) {
			if (getDocument() == getTableModel().getDocument(rowIndex)) {
				return rowIndex;
			}
		}
		return -1;
	}
	

	private void stopCellEditing() {
		if (getTable().getCellEditor() != null) {
			getTable().getCellEditor().stopCellEditing();
		}
	}
	
	
	private void updateTableColumnWidths() {
		final JTable table = getTable();
    final TableColumnModel columnModel = table.getColumnModel();
    for (int column = 0; column < table.getColumnCount(); column++) {
    	int width = MIN_COLUMN_WIDTH;
    	for (int row = 0; row < table.getRowCount(); row++) {
    		Component comp;
    		if (table.isCellEditable(row, column)) {  // Editor must be used to consider widths of non-selected node/branch adapter names.
      		TableCellEditor editor = table.getCellEditor(row, column);
      		comp = table.prepareEditor(editor, row, column);
    		}
    		else {
    			TableCellRenderer renderer = table.getCellRenderer(row, column);
    			comp = table.prepareRenderer(renderer, row, column);
    		}
    		width = Math.max(comp.getPreferredSize().width + 1 , width);
    	}
    	columnModel.getColumn(column).setPreferredWidth(width);
    }
	}
	
	
	@Override
	protected boolean onExecute() {
		stopCellEditing();  // Avoid possible previous combo box selections still being displayed.
		getTableModel().refreshDocuments(true);
		updateTableColumnWidths();
		getTable().changeSelection(activeDocumentRow(), 0, false, false);  // Select a cell without a combo box. Otherwise the previous selection of the combo box remains, even of these changes were not applied when the dialog was used last time.
				// Using activeDocumentRow() is currently unnecessary, since the current implementation always puts the document of the focused window in the first row.
		pack();
		return true;
	}

	
	@Override
	protected boolean apply() {
		stopCellEditing();  // Apply the value of the currently selected cell.
		
		for (int rowIndex = 0; rowIndex < getTable().getRowCount(); rowIndex++) {
			Document document = getTableModel().getDocument(rowIndex);
			if (document.getDefaultLeafAdapter().equals(getTableModel().getSelectedLeafAdapter(rowIndex)) || 
					document.getDefaultSupportAdapter().equals(getTableModel().getSelectedSupportAdapter(rowIndex))) {  //TODO Is equals properly implemented?
				
				System.out.println("Setting adapters in " + document.getDefaultNameOrPath() + " to " + getTableModel().getSelectedLeafAdapter(rowIndex) + " and " + getTableModel().getSelectedSupportAdapter(rowIndex));
				document.executeEdit(new DefaultDocumentAdapterEdit(document, 
						getTableModel().getSelectedLeafAdapter(rowIndex), getTableModel().getSelectedSupportAdapter(rowIndex)));
			}
		}
		
		return true;
	}
	
	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		setTitle("Set default node/branch data columns");
		setContentPane(getJContentPane());
		setMinimumSize(new Dimension(300, 150));
		pack();
	}
	
	
	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BoxLayout(getJContentPane(), BoxLayout.Y_AXIS));
			
			JPanel headingPanel = new JPanel();
			jContentPane.add(headingPanel);
			
			JScrollPane scrollPane = new JScrollPane();
			scrollPane.setViewportView(getTable());
			jContentPane.add(scrollPane);			
			
			jContentPane.add(getButtonsPanel(), null);
			getApplyButton().setVisible(false);
		}
		return jContentPane;
	}
	
	
	private JTable getTable() {
		if (table == null) {
			table = new JTable(new DefaultDocumentAdaptersTableModel()) {
				@Override
				public Dimension getPreferredScrollableViewportSize() {
					return super.getPreferredSize();  // Make sure that scroll container is enlarged if necessary.
				}
			};
			
			table.getColumnModel().getColumn(DefaultDocumentAdaptersTableModel.LEAF_ADAPTER_COLUMN).setCellEditor(
					new NodeBranchDataAdapterCellEditor.LeafAdapterEditor());
			table.getColumnModel().getColumn(DefaultDocumentAdaptersTableModel.SUPPORT_ADAPTER_COLUMN).setCellEditor(
					new NodeBranchDataAdapterCellEditor.SupportAdapterEditor());
			
			table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			//table.setSurrendersFocusOnKeystroke(true);
			//table.setColumnSelectionAllowed(true);
			//table.setRowSelectionAllowed(true);
			//table.getTableHeader().setReorderingAllowed(false);
			table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		}
		return table;
	}
	
	
	private DefaultDocumentAdaptersTableModel getTableModel() {
		return (DefaultDocumentAdaptersTableModel)getTable().getModel();
	}
}
