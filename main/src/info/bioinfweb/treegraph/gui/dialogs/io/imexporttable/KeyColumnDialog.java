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
package info.bioinfweb.treegraph.gui.dialogs.io.imexporttable;


import info.bioinfweb.treegraph.Main;
import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.undo.file.importtable.ImportTableData;
import info.bioinfweb.treegraph.document.undo.file.importtable.ImportTableParameters;
import info.bioinfweb.treegraph.gui.dialogs.CompareTextElementDataParametersPanel;
import info.bioinfweb.treegraph.gui.dialogs.nodebranchdatainput.NodeBranchDataInput;
import info.bioinfweb.wikihelp.client.OkCancelApplyWikiHelpDialog;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;



public class KeyColumnDialog extends OkCancelApplyWikiHelpDialog {
	private JPanel jContentPane = null;
	private JPanel treeColumnPanel;
	private NodeBranchDataInput keyColumnInput;
	private JLabel nodeIdentifierLabel;
	private CompareTextElementDataParametersPanel textElementDataParametersPanel;
	private JPanel tableColumnPanel;
	private JLabel lblSelectTheColumns;
	private JComboBox<String> tableColumnComboBox;
	
	
	public KeyColumnDialog(Frame owner) {
		super(owner, true, Main.getInstance().getWikiHelp());
		
		initialize();
		setLocationRelativeTo(owner);
	}
	
	
  public void assignParameters(ImportTableParameters parameters) {
		parameters.setKeyAdapter(getKeyColumnInput().getSelectedAdapter());
		getTextElementDataParametersPanel().assignToParameters(parameters);
  }
  
  
  public int getSelectedTableKeyColumn() {
  	return getTableColumnComboBox().getSelectedIndex();
  }
  
  
  private void setTableColumns(ImportTableData data) {
		getTableColumnComboBoxModel().removeAllElements();
  	for (int column = 0; column < data.columnCount(); column++) {
  		getTableColumnComboBoxModel().addElement(column + ") " + data.getHeading(column));
		}
  }

  
	public boolean execute(Document document, ImportTableData data) {
		getKeyColumnInput().setAdapters(document.getTree(), true, true, true, false, false, "");
		getKeyColumnInput().setSelectedAdapter(document.getDefaultLeafAdapter());
		setTableColumns(data);
		
		return execute();
	}
	
	
	@Override
	protected boolean apply() {
		return true;
	}


	/**
	 * This method initializes this
	 *
	 * @return void
	 */
	private void initialize() {
		setHelpCode(87);
		setTitle("Select matching key columns in the table and the tree");
		setContentPane(getJContentPane());
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
			jContentPane.add(getTableColumnPanel());
			
			jContentPane.add(getTreeColumnPanel());
			jContentPane.add(getTextElementDataParametersPanel());
			
			jContentPane.add(getButtonsPanel(), null);
			getApplyButton().setVisible(false);
			getOkButton().setText("Next >");
		}
		return jContentPane;
	}

	
	private JLabel getNodeIdentifierLabel() {
		if (nodeIdentifierLabel == null) {
			nodeIdentifierLabel = new JLabel("Select a node/branch data column of the tree that contains the same values as a column of the table:");
		}
		return nodeIdentifierLabel;
	}
	
	
	private NodeBranchDataInput getKeyColumnInput() {
		if (keyColumnInput == null) {
			getTreeColumnPanel();
		}
		return keyColumnInput;
	}
	
	
	private CompareTextElementDataParametersPanel getTextElementDataParametersPanel() {
		if (textElementDataParametersPanel == null) {
			textElementDataParametersPanel = new CompareTextElementDataParametersPanel();
			textElementDataParametersPanel.setBorder(new TitledBorder(null, "Compare options", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		}
		return textElementDataParametersPanel;
	}
	
	
	private JPanel getTreeColumnPanel() {
		if (treeColumnPanel == null) {
			treeColumnPanel = new JPanel();
			treeColumnPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Key column in tree", TitledBorder.LEADING, TitledBorder.TOP, null, null));
			GridBagLayout gbl_treeColumnPanel = new GridBagLayout();
			treeColumnPanel.setLayout(gbl_treeColumnPanel);
			keyColumnInput = new NodeBranchDataInput(treeColumnPanel, 0, 1);
			GridBagConstraints gbc_nodeIdentifierLabel = new GridBagConstraints();
			gbc_nodeIdentifierLabel.weighty = 1.0;
			gbc_nodeIdentifierLabel.anchor = GridBagConstraints.WEST;
			gbc_nodeIdentifierLabel.insets = new Insets(0, 0, 5, 0);
			gbc_nodeIdentifierLabel.gridx = 0;
			gbc_nodeIdentifierLabel.gridy = 0;
			treeColumnPanel.add(getNodeIdentifierLabel(), gbc_nodeIdentifierLabel);
  	}
		return treeColumnPanel;
	}
	
	
	private JPanel getTableColumnPanel() {
		if (tableColumnPanel == null) {
			tableColumnPanel = new JPanel();
			tableColumnPanel.setBorder(new TitledBorder(null, "Key column in table", TitledBorder.LEADING, TitledBorder.TOP, null, null));
			GridBagLayout gbl_tableColumnPanel = new GridBagLayout();
			tableColumnPanel.setLayout(gbl_tableColumnPanel);
			GridBagConstraints gbc_lblSelectTheColumns = new GridBagConstraints();
			gbc_lblSelectTheColumns.insets = new Insets(0, 0, 5, 0);
			gbc_lblSelectTheColumns.gridx = 0;
			gbc_lblSelectTheColumns.gridy = 0;
			tableColumnPanel.add(getLblSelectTheColumns(), gbc_lblSelectTheColumns);
			GridBagConstraints gbc_tableColumnComboBox = new GridBagConstraints();
			gbc_tableColumnComboBox.fill = GridBagConstraints.HORIZONTAL;
			gbc_tableColumnComboBox.gridx = 0;
			gbc_tableColumnComboBox.gridy = 1;
			tableColumnPanel.add(getTableColumnComboBox(), gbc_tableColumnComboBox);
		}
		return tableColumnPanel;
	}
	
	
	private JLabel getLblSelectTheColumns() {
		if (lblSelectTheColumns == null) {
			lblSelectTheColumns = new JLabel("Select the column of the table that contains the same values as a node/branch data column of the tree:");
		}
		return lblSelectTheColumns;
	}
	
	
	private JComboBox<String> getTableColumnComboBox() {
		if (tableColumnComboBox == null) {
			tableColumnComboBox = new JComboBox<>(new DefaultComboBoxModel<String>());
		}
		return tableColumnComboBox;
	}
	
	
	private DefaultComboBoxModel<String> getTableColumnComboBoxModel() {
		return (DefaultComboBoxModel<String>)getTableColumnComboBox().getModel();
	}
}
