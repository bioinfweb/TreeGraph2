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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.undo.edit.DefaultDocumentAdapterEdit;
import info.bioinfweb.treegraph.gui.dialogs.EditDialog;
import info.bioinfweb.treegraph.gui.mainframe.MainFrame;



/**
 * Dialog that allows to set the default document adapters of all opened documents.
 * 
 * @author Ben St&ouml;ver
 */
public class DefaultDocumentAdapterDialog extends EditDialog {
	public static final int MIN_COLUMN_WIDTH = 30;
	
	
	private JPanel jContentPane = null;
	JPanel headingPanel = null;
	private JTable table = null;
	private JLabel combinedLeavesColumnLabel = null;
	private JComboBox<CombinedAdapterEntry> combinedLeavesColumnComboBox = null;
	private JButton combinedLeavesColumnApplyButton = null;
	private JLabel combinedSupportColumnLabel = null;
	private JComboBox<CombinedAdapterEntry> combinedSupportColumnComboBox = null;
	private JButton combinedSupportColumnApplyButton = null;
	
	
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
		getCombinedLeavesColumnComboBoxModel().refreshEntries(getTableModel());
		getCombinedLeavesColumnComboBox().setSelectedIndex(0);
		getCombinedSupportColumnComboBoxModel().refreshEntries(getTableModel());
		getCombinedSupportColumnComboBox().setSelectedIndex(0);
		
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
			if (!document.getDefaultLeafAdapter().equals(getTableModel().getSelectedLeafAdapter(rowIndex)) || 
					!document.getDefaultSupportAdapter().equals(getTableModel().getSelectedSupportAdapter(rowIndex))) {
				
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
			jContentPane = new JPanel(new GridBagLayout());
			
			GridBagConstraints headingPanelGBC = new GridBagConstraints();
			headingPanelGBC.fill = GridBagConstraints.HORIZONTAL;
			headingPanelGBC.gridx = 0;
			headingPanelGBC.gridy = 0;
			headingPanelGBC.weightx = 1.0;
			headingPanelGBC.weighty = 0.0;
			jContentPane.add(getHeadingPanel(), headingPanelGBC);
			
			GridBagConstraints scrollPaneGBC = new GridBagConstraints();
			scrollPaneGBC.fill = GridBagConstraints.BOTH;
			scrollPaneGBC.gridx = 0;
			scrollPaneGBC.gridy = 1;
			scrollPaneGBC.weightx = 1.0;
			scrollPaneGBC.weighty = 1.0;
			JScrollPane scrollPane = new JScrollPane();
			scrollPane.setViewportView(getTable());
			jContentPane.add(scrollPane, scrollPaneGBC);			
			
			GridBagConstraints buttonsPanelGBC = new GridBagConstraints();
			buttonsPanelGBC.fill = GridBagConstraints.HORIZONTAL;
			buttonsPanelGBC.gridx = 0;
			buttonsPanelGBC.gridy = 2;
			buttonsPanelGBC.weightx = 1.0;
			buttonsPanelGBC.weighty = 0.0;
			getApplyButton().setVisible(false);
			jContentPane.add(getButtonsPanel(), buttonsPanelGBC);
		}
		return jContentPane;
	}
	
	
	private JPanel getHeadingPanel() {
		if (headingPanel == null) {
			headingPanel = new JPanel();
			headingPanel.setBorder(new TitledBorder(null, "Set columns for all documents", TitledBorder.LEADING, TitledBorder.TOP, null, null));
			
			GridBagLayout gbl_headingPanel = new GridBagLayout();
			gbl_headingPanel.columnWidths = new int[] {0, 0, 0};
			gbl_headingPanel.rowHeights = new int[] {0, 0};
			gbl_headingPanel.columnWeights = new double[]{0.0, 1.0, 0.0};
			gbl_headingPanel.rowWeights = new double[]{0.0, 0.0};
			headingPanel.setLayout(gbl_headingPanel);
			GridBagConstraints combinedLeafColumnsLabel = new GridBagConstraints();
			combinedLeafColumnsLabel.insets = new Insets(0, 0, 5, 5);
			combinedLeafColumnsLabel.anchor = GridBagConstraints.WEST;
			combinedLeafColumnsLabel.gridx = 0;
			combinedLeafColumnsLabel.gridy = 0;
			headingPanel.add(getCombinedLeavesColumnLabel(), combinedLeafColumnsLabel);
			GridBagConstraints combinedLeafColumnsComboBox = new GridBagConstraints();
			combinedLeafColumnsComboBox.weightx = 1.0;
			combinedLeafColumnsComboBox.insets = new Insets(0, 0, 5, 5);
			combinedLeafColumnsComboBox.fill = GridBagConstraints.HORIZONTAL;
			combinedLeafColumnsComboBox.gridx = 1;
			combinedLeafColumnsComboBox.gridy = 0;
			headingPanel.add(getCombinedLeavesColumnComboBox(), combinedLeafColumnsComboBox);
			GridBagConstraints combinedLeafColumnsApplyButton = new GridBagConstraints();
			combinedLeafColumnsApplyButton.insets = new Insets(0, 0, 5, 5);
			combinedLeafColumnsApplyButton.gridx = 2;
			combinedLeafColumnsApplyButton.gridy = 0;
			headingPanel.add(getCombinedLeavesColumnApplyButton(), combinedLeafColumnsApplyButton);
			GridBagConstraints combinedSupportColumnsLabel = new GridBagConstraints();
			combinedSupportColumnsLabel.anchor = GridBagConstraints.EAST;
			combinedSupportColumnsLabel.insets = new Insets(0, 0, 5, 5);
			combinedSupportColumnsLabel.gridx = 0;
			combinedSupportColumnsLabel.gridy = 1;
			headingPanel.add(getCombinedSupportColumnLabel(), combinedSupportColumnsLabel);
			GridBagConstraints combinedSupportColumnsComboBox = new GridBagConstraints();
			combinedSupportColumnsComboBox.weightx = 1.0;
			combinedSupportColumnsComboBox.insets = new Insets(0, 0, 5, 5);
			combinedSupportColumnsComboBox.fill = GridBagConstraints.HORIZONTAL;
			combinedSupportColumnsComboBox.gridx = 1;
			combinedSupportColumnsComboBox.gridy = 1;
			headingPanel.add(getCombinedSupportColumnComboBox(), combinedSupportColumnsComboBox);
			GridBagConstraints combinedSupportColumnsApplyButton = new GridBagConstraints();
			combinedSupportColumnsApplyButton.insets = new Insets(0, 0, 5, 5);
			combinedSupportColumnsApplyButton.gridx = 2;
			combinedSupportColumnsApplyButton.gridy = 1;
			headingPanel.add(getCombinedSupportColumnApplyButton(), combinedSupportColumnsApplyButton);
		}
		return headingPanel;
	}
	
	
	private JTable getTable() {
		if (table == null) {
			table = new JTable(new DefaultDocumentAdaptersTableModel()) {
				@Override
				public Dimension getPreferredScrollableViewportSize() {
					return getPreferredSize();  // Make sure that scroll container is enlarged if necessary.
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
	
	
	private JLabel getCombinedLeavesColumnLabel() {
		if (combinedLeavesColumnLabel == null) {
			combinedLeavesColumnLabel = new JLabel("Default leaves columns: ");
		}
		return combinedLeavesColumnLabel;
	}
	
	
	private JComboBox<CombinedAdapterEntry> getCombinedLeavesColumnComboBox() {
		if (combinedLeavesColumnComboBox == null) {
			combinedLeavesColumnComboBox = new JComboBox<CombinedAdapterEntry>(new CombinedAdaptersComboBoxModel.LeafAdapterModel());
		}
		return combinedLeavesColumnComboBox;
	}
	
	
	private CombinedAdaptersComboBoxModel getCombinedLeavesColumnComboBoxModel() {
		return (CombinedAdaptersComboBoxModel)getCombinedLeavesColumnComboBox().getModel();
	}
	
	
	private JButton getCombinedLeavesColumnApplyButton() {
		if (combinedLeavesColumnApplyButton == null) {
			combinedLeavesColumnApplyButton = new JButton("Set where applicable");
			combinedLeavesColumnApplyButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					getTableModel().setLeafAdapterToAll(getCombinedLeavesColumnComboBoxModel().getSelectedAdapter());
				}
			});
		}
		return combinedLeavesColumnApplyButton;
	}
	
	
	private JLabel getCombinedSupportColumnLabel() {
		if (combinedSupportColumnLabel == null) {
			combinedSupportColumnLabel = new JLabel("Default support columns: ");
		}
		return combinedSupportColumnLabel;
	}
	
	
	private JComboBox<CombinedAdapterEntry> getCombinedSupportColumnComboBox() {
		if (combinedSupportColumnComboBox == null) {
			combinedSupportColumnComboBox = new JComboBox<CombinedAdapterEntry>(new CombinedAdaptersComboBoxModel.SupportAdapterModel());
		}
		return combinedSupportColumnComboBox;
	}
	
	
	private CombinedAdaptersComboBoxModel getCombinedSupportColumnComboBoxModel() {
		return (CombinedAdaptersComboBoxModel)getCombinedSupportColumnComboBox().getModel();
	}
	
	
	private JButton getCombinedSupportColumnApplyButton() {
		if (combinedSupportColumnApplyButton == null) {
			combinedSupportColumnApplyButton = new JButton("Set where applicable");
			combinedSupportColumnApplyButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					getTableModel().setSupportAdapterToAll(getCombinedSupportColumnComboBoxModel().getSelectedAdapter());
				}
			});
		}
		return combinedSupportColumnApplyButton;
	}
}
