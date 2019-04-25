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
package info.bioinfweb.treegraph.gui.dialogs.nodebranchdata.calculatecolumn;


import info.bioinfweb.treegraph.document.nodebranchdata.NewHiddenBranchDataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.undo.edit.CalculateColumnEdit;
import info.bioinfweb.treegraph.gui.dialogs.EditDialog;
import info.bioinfweb.treegraph.gui.dialogs.editelement.TextElementDataInput;
import info.bioinfweb.treegraph.gui.dialogs.nodebranchdata.RecentlyUsedExpressionsListModel;
import info.bioinfweb.treegraph.gui.dialogs.nodebranchdata.TextIDElementTypeInput;
import info.bioinfweb.treegraph.gui.dialogs.nodebranchdatainput.NewNodeBranchDataInput;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;



/**
 * Used to calculate a node/branch data column as specified by the user defined 
 * expression.
 * @author Ben St&ouml;ver
 * @since 2.0.24
 */
public class CalculateColumnDialog extends EditDialog {
	public static final int MIN_EXPRESSION_FIELD_WIDTH = 300;
	
	private static final long serialVersionUID = 1L;


	private CalculateColumnErrorsDialog errorDialog;
	
	private JPanel jContentPane = null;
	private JPanel expressionPanel = null;
	private JComboBox<String> valueExpressionComboBox = null;
	private JPanel columnPanel = null;
	private NewNodeBranchDataInput columnInput = null;
	private JRadioButton singleTargetRB;
	private JRadioButton calculatedTargetRB;
	private JComboBox<String> columnIDExpressionComboBox;
	private JLabel spacerLabel;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private JLabel expressionStartLabel;
	private TextIDElementTypeInput columnIDTypeInput;
	private JLabel columnTypeLabel;
	private JPanel columnIDExpressionPanel;
	private JLabel columnIDExpressionLabel;
	private JCheckBox defaultValueCheckBox;
	private TextElementDataInput defaultValueInput;
	private JCheckBox clearTargetColumnsCheckBox;
	private JLabel columnTypeNoteLabel;

	
	/**
	 * @param owner
	 */
	public CalculateColumnDialog(Frame owner) {
		super(owner);
		setHelpCode(90);
		initialize();
		setLocationRelativeTo(owner);
		errorDialog = new CalculateColumnErrorsDialog(owner);
	}

	
	@Override
	protected boolean onExecute() {
		getColumnInput().setAdapters(getDocument().getTree(), false, true, true, false, true, "");
		getColumnInput().setSelectedAdapter(NewHiddenBranchDataAdapter.class);
		if (getSelectedAdapter() != null) {
			getColumnInput().setSelectedAdapter(getSelectedAdapter());  // If an readOnly adapter is selected, nothing will change.
		}
		getColumnIDExpressionComboBox().setSelectedItem("");
		getValueExpressionComboBox().setSelectedItem("");

		pack();
		return true;
	}


	@Override
	protected boolean apply() {
		NodeBranchDataAdapter singleSelectedAdapter = null;
		if (getSingleTargetRB().isSelected()) {
			singleSelectedAdapter = getColumnInput().getSelectedAdapter();
		}
		final CalculateColumnEdit edit = new CalculateColumnEdit(getDocument(), singleSelectedAdapter, 
				(String)getColumnIDExpressionComboBox().getSelectedItem(), getColumnIDTypeInput().getSelectedType(), 
				(String)getValueExpressionComboBox().getSelectedItem(), getClearTargetColumnsCheckBox().isSelected(), 
				getDefaultValueCheckBox().isSelected() ? getDefaultValueInput().getValue() : null);
		
		boolean result = edit.evaluate();
		if (result) {
			getDocument().executeEdit(edit);
			if (edit.hasErrors()) {
				EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {  // Display that dialog after this has been closed, so that the tree is visible. 
						errorDialog.execute(edit.getErrors());
					}
				});
			}
			
			// Save expression lists:
			getValueExpressionsModel().addElement((String)getValueExpressionComboBox().getSelectedItem());
			getColumnIDExpressionsModel().addElement((String)getColumnIDExpressionComboBox().getSelectedItem());
			try {
				getValueExpressionsModel().saveList();
				getColumnIDExpressionsModel().saveList();
			}
			catch (IOException e) {
				JOptionPane.showMessageDialog(this, "The error \"" + e.getLocalizedMessage() + "\" occured while " +
						"trying to write a configuration file.",	"I/O Error", JOptionPane.ERROR_MESSAGE);
			}
		}
		else {
			JOptionPane.showMessageDialog(this, edit.getErrors(), "Erroneous expression", 
					JOptionPane.ERROR_MESSAGE);
		}
		return result;
	}


	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		setContentPane(getJContentPane());
		setTitle("Calculate node/branch data");
		setMinimumSize(new Dimension(500, 50));
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
			jContentPane.add(getColumnPanel(), null);
			jContentPane.add(getExpressionPanel(), null);
			jContentPane.add(getButtonsPanel(), null);
		}
		return jContentPane;
	}


	/**
	 * This method initializes expressionPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getExpressionPanel() {
		if (expressionPanel == null) {
			GridBagConstraints gbc_expressionComboBox = new GridBagConstraints();
			gbc_expressionComboBox.insets = new Insets(0, 0, 5, 0);
			gbc_expressionComboBox.weighty = 1.0;
			gbc_expressionComboBox.fill = GridBagConstraints.HORIZONTAL;
			gbc_expressionComboBox.gridy = 0;
			gbc_expressionComboBox.weightx = 10.0;
			gbc_expressionComboBox.gridx = 1;
			expressionPanel = new JPanel();
			GridBagLayout gbl_expressionPanel = new GridBagLayout();
			gbl_expressionPanel.columnWeights = new double[]{0.0, 1.0};
			expressionPanel.setLayout(gbl_expressionPanel);
			expressionPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Calculate value", TitledBorder.LEADING, TitledBorder.TOP, null, null));
			expressionPanel.add(getValueExpressionComboBox(), gbc_expressionComboBox);
			GridBagConstraints gbc_expressionStartLabel = new GridBagConstraints();
			gbc_expressionStartLabel.insets = new Insets(0, 2, 5, 5);
			gbc_expressionStartLabel.anchor = GridBagConstraints.WEST;
			gbc_expressionStartLabel.gridx = 0;
			gbc_expressionStartLabel.gridy = 0;
			expressionPanel.add(getExpressionStartLabel(), gbc_expressionStartLabel);
			GridBagConstraints gbc_chckbxClearTargetColumns = new GridBagConstraints();
			gbc_chckbxClearTargetColumns.anchor = GridBagConstraints.WEST;
			gbc_chckbxClearTargetColumns.gridwidth = 2;
			gbc_chckbxClearTargetColumns.insets = new Insets(0, 0, 2, 2);
			gbc_chckbxClearTargetColumns.gridx = 0;
			gbc_chckbxClearTargetColumns.gridy = 1;
			expressionPanel.add(getClearTargetColumnsCheckBox(), gbc_chckbxClearTargetColumns);
			GridBagConstraints gbc_defaultValueCheckBox = new GridBagConstraints();
			gbc_defaultValueCheckBox.anchor = GridBagConstraints.WEST;
			gbc_defaultValueCheckBox.gridwidth = 2;
			gbc_defaultValueCheckBox.insets = new Insets(0, 0, 2, 2);
			gbc_defaultValueCheckBox.gridx = 0;
			gbc_defaultValueCheckBox.gridy = 2;
			expressionPanel.add(getDefaultValueCheckBox(), gbc_defaultValueCheckBox);
			GridBagConstraints gbc_defaultValueInput = new GridBagConstraints();
			gbc_defaultValueInput.gridwidth = 2;
			gbc_defaultValueInput.insets = new Insets(0, 24, 0, 2);
			gbc_defaultValueInput.fill = GridBagConstraints.BOTH;
			gbc_defaultValueInput.gridx = 0;
			gbc_defaultValueInput.gridy = 3;
			expressionPanel.add(getDefaultValueInput(), gbc_defaultValueInput);
		}
		return expressionPanel;
	}

	
	private JComboBox<String> createExpressionInput(String fileSuffix) {
		RecentlyUsedExpressionsListModel model = new RecentlyUsedExpressionsListModel(fileSuffix);
		try {
			model.loadList();
		}
		catch (IOException e) {
			JOptionPane.showMessageDialog(this, "An error occured while trying to read the configuration file.", 
							"I/O Error", JOptionPane.ERROR_MESSAGE);
		}
		JComboBox<String> result = new JComboBox<String>(model);
		result.setEditable(true);
		result.setMinimumSize(new Dimension(MIN_EXPRESSION_FIELD_WIDTH, 20));
		return result;
	}
	

	private JComboBox<String> getValueExpressionComboBox() {
		if (valueExpressionComboBox == null) {
			valueExpressionComboBox = createExpressionInput("Value");
		}
		return valueExpressionComboBox;
	}
	
	
	private RecentlyUsedExpressionsListModel getValueExpressionsModel() {
		return (RecentlyUsedExpressionsListModel)getValueExpressionComboBox().getModel();
	}


	private RecentlyUsedExpressionsListModel getColumnIDExpressionsModel() {
		return (RecentlyUsedExpressionsListModel)getColumnIDExpressionComboBox().getModel();
	}


	private JPanel getColumnPanel() {
		if (columnPanel == null) {
			columnPanel = new JPanel();
			columnPanel.setBorder(new TitledBorder(null, "Target column", TitledBorder.LEADING, TitledBorder.TOP, null, null));
			GridBagLayout gbl_columnPanel = new GridBagLayout();
			gbl_columnPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0};
			gbl_columnPanel.columnWeights = new double[]{0.0, 1.0};
			columnPanel.setLayout(gbl_columnPanel);
			columnInput = new NewNodeBranchDataInput(columnPanel, 1, 1, true);
			GridBagConstraints gbc_singleTargetRB = new GridBagConstraints();
			gbc_singleTargetRB.gridwidth = 2;
			gbc_singleTargetRB.insets = new Insets(0, 0, 5, 5);
			gbc_singleTargetRB.anchor = GridBagConstraints.WEST;
			gbc_singleTargetRB.gridx = 0;
			gbc_singleTargetRB.gridy = 0;
			columnPanel.add(getSingleTargetRB(), gbc_singleTargetRB);
			GridBagConstraints gbc_spacerLabel = new GridBagConstraints();
			gbc_spacerLabel.insets = new Insets(0, 0, 5, 5);
			gbc_spacerLabel.gridx = 0;
			gbc_spacerLabel.gridy = 1;
			columnPanel.add(getSpacerLabel(), gbc_spacerLabel);
			GridBagConstraints gbc_calculatedTargetRB = new GridBagConstraints();
			gbc_calculatedTargetRB.gridwidth = 2;
			gbc_calculatedTargetRB.insets = new Insets(5, 0, 5, 5);
			gbc_calculatedTargetRB.anchor = GridBagConstraints.WEST;
			gbc_calculatedTargetRB.gridx = 0;
			gbc_calculatedTargetRB.gridy = 2;
			columnPanel.add(getCalculatedTargetRB(), gbc_calculatedTargetRB);
			GridBagConstraints gbc_columnIDExpressionPanel = new GridBagConstraints();
			gbc_columnIDExpressionPanel.gridwidth = 2;
			gbc_columnIDExpressionPanel.fill = GridBagConstraints.BOTH;
			gbc_columnIDExpressionPanel.insets = new Insets(0, 0, 5, 0);
			gbc_columnIDExpressionPanel.gridx = 1;
			gbc_columnIDExpressionPanel.gridy = 3;
			columnPanel.add(getColumnIDExpressionPanel(), gbc_columnIDExpressionPanel);
			GridBagConstraints gbc_columnTypeLabel = new GridBagConstraints();
			gbc_columnTypeLabel.anchor = GridBagConstraints.WEST;
			gbc_columnTypeLabel.gridwidth = 2;
			gbc_columnTypeLabel.insets = new Insets(7, 0, 2, 0);
			gbc_columnTypeLabel.gridx = 1;
			gbc_columnTypeLabel.gridy = 4;
			columnPanel.add(getColumnTypeLabel(), gbc_columnTypeLabel);
			GridBagConstraints gbc_columnIDTypeInput = new GridBagConstraints();
			gbc_columnIDTypeInput.insets = new Insets(0, 0, 5, 0);
			gbc_columnIDTypeInput.gridwidth = 2;
			gbc_columnIDTypeInput.fill = GridBagConstraints.BOTH;
			gbc_columnIDTypeInput.gridx = 1;
			gbc_columnIDTypeInput.gridy = 5;
			columnPanel.add(getColumnIDTypeInput(), gbc_columnIDTypeInput);
			GridBagConstraints gbc_lblnoteThatThe = new GridBagConstraints();
			gbc_lblnoteThatThe.anchor = GridBagConstraints.WEST;
			gbc_lblnoteThatThe.gridx = 1;
			gbc_lblnoteThatThe.gridy = 6;
			columnPanel.add(getColumnTypeNoteLabel(), gbc_lblnoteThatThe);
		}
		return columnPanel;
	}
	
	
	private NewNodeBranchDataInput getColumnInput() {
		getColumnPanel();
		return columnInput;
	}
	
	
	private JRadioButton getSingleTargetRB() {
		if (singleTargetRB == null) {
			singleTargetRB = new JRadioButton("Define a single target column");
			singleTargetRB.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					getColumnInput().setEnabled(singleTargetRB.isSelected());
				}
			});
			buttonGroup.add(singleTargetRB);
			singleTargetRB.setSelected(true);
		}
		return singleTargetRB;
	}
	
	
	private JRadioButton getCalculatedTargetRB() {
		if (calculatedTargetRB == null) {
			calculatedTargetRB = new JRadioButton("Calculate (possibly different) target column ID for each line");
			calculatedTargetRB.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					getColumnIDExpressionLabel().setEnabled(calculatedTargetRB.isSelected());
					getColumnIDExpressionComboBox().setEnabled(calculatedTargetRB.isSelected());
					getColumnIDTypeInput().setEnabled(calculatedTargetRB.isSelected());
					getColumnTypeLabel().setEnabled(calculatedTargetRB.isSelected());
					getColumnTypeNoteLabel().setEnabled(calculatedTargetRB.isSelected());
				}
			});
			buttonGroup.add(calculatedTargetRB);
		}
		return calculatedTargetRB;
	}
	
	
	private JComboBox<String> getColumnIDExpressionComboBox() {
		if (columnIDExpressionComboBox == null) {
			columnIDExpressionComboBox = createExpressionInput("TargetColumn");
			columnIDExpressionComboBox.setEnabled(false);
		}
		return columnIDExpressionComboBox;
	}
	
	
	private JLabel getSpacerLabel() {
		if (spacerLabel == null) {
			spacerLabel = new JLabel("     ");
		}
		return spacerLabel;
	}
	
	
	private JLabel getExpressionStartLabel() {
		if (expressionStartLabel == null) {
			expressionStartLabel = new JLabel("value = ");
		}
		return expressionStartLabel;
	}
	
	
	private TextIDElementTypeInput getColumnIDTypeInput() {
		if (columnIDTypeInput == null) {
			columnIDTypeInput = new TextIDElementTypeInput();
			columnIDTypeInput.setEnabled(false);
		}
		return columnIDTypeInput;
	}
	
	
	private JLabel getColumnTypeLabel() {
		if (columnTypeLabel == null) {
			columnTypeLabel = new JLabel("Specify the type of element that shall be created if the expression results in a new column ID:");
			columnTypeLabel.setEnabled(false);
		}
		return columnTypeLabel;
	}
	
	
	private JPanel getColumnIDExpressionPanel() {
		if (columnIDExpressionPanel == null) {
			columnIDExpressionPanel = new JPanel();
			GridBagLayout gbl_columnIDExpressionPanel = new GridBagLayout();
			gbl_columnIDExpressionPanel.columnWeights = new double[]{0.0, 1.0};
			columnIDExpressionPanel.setLayout(gbl_columnIDExpressionPanel);
			GridBagConstraints gbc_columnIDExpressionLabel = new GridBagConstraints();
			gbc_columnIDExpressionLabel.insets = new Insets(0, 0, 0, 5);
			gbc_columnIDExpressionLabel.anchor = GridBagConstraints.WEST;
			gbc_columnIDExpressionLabel.gridx = 0;
			gbc_columnIDExpressionLabel.gridy = 0;
			columnIDExpressionPanel.add(getColumnIDExpressionLabel(), gbc_columnIDExpressionLabel);
			GridBagConstraints gbc_columnIDExpressionTextField = new GridBagConstraints();
			gbc_columnIDExpressionTextField.fill = GridBagConstraints.HORIZONTAL;
			gbc_columnIDExpressionTextField.weighty = 1.0;
			gbc_columnIDExpressionTextField.anchor = GridBagConstraints.NORTH;
			gbc_columnIDExpressionTextField.gridx = 1;
			gbc_columnIDExpressionTextField.gridy = 0;
			columnIDExpressionPanel.add(getColumnIDExpressionComboBox(), gbc_columnIDExpressionTextField);
		}
		return columnIDExpressionPanel;
	}
	
	
	private JLabel getColumnIDExpressionLabel() {
		if (columnIDExpressionLabel == null) {
			columnIDExpressionLabel = new JLabel("target column = ");
			columnIDExpressionLabel.setEnabled(false);
		}
		return columnIDExpressionLabel;
	}
	
	
	private JCheckBox getDefaultValueCheckBox() {
		if (defaultValueCheckBox == null) {
			defaultValueCheckBox = new JCheckBox("Set the following value to all cells of the affected columns that contain no value after the calculation:");
			defaultValueCheckBox.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					getDefaultValueInput().setEnabled(defaultValueCheckBox.isSelected());
				}
			});
		}
		return defaultValueCheckBox;
	}
	
	
	private TextElementDataInput getDefaultValueInput() {
		if (defaultValueInput == null) {
			defaultValueInput = new TextElementDataInput(true);
			defaultValueInput.setEnabled(false);
		}
		return defaultValueInput;
	}
	
	
	private JCheckBox getClearTargetColumnsCheckBox() {
		if (clearTargetColumnsCheckBox == null) {
			clearTargetColumnsCheckBox = new JCheckBox("Clear target column(s) before calculation");
		}
		return clearTargetColumnsCheckBox;
	}
	
	
	private JLabel getColumnTypeNoteLabel() {
		if (columnTypeNoteLabel == null) {
			columnTypeNoteLabel = new JLabel("(Note that the selection made here will be ignored for columns that already exist.)");
			columnTypeNoteLabel.setEnabled(false);
		}
		return columnTypeNoteLabel;
	}
}