/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2013  Ben Stöver, Kai Müller
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
package info.bioinfweb.treegraph.gui.dialogs;


import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.undo.edit.ReplaceInNodeDataEdit;
import info.bioinfweb.treegraph.document.undo.edit.ReplaceInNodeDataEdit.InsertPosition;
import info.bioinfweb.treegraph.gui.dialogs.nodebranchdatainput.NodeDataComboBoxModel;

import javax.swing.JPanel;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridBagLayout;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import java.awt.GridBagConstraints;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;
import java.awt.Font;
import java.awt.Color;
import javax.swing.JRadioButton;
import javax.swing.JCheckBox;
import java.awt.Insets;
import javax.swing.UIManager;



/**
 * Dialog used to find and replace text in a node/branch data column.
 * 
 * @author Ben St&ouml;ver
 */
public class ReplaceInNodeDataDialog extends EditDialog {
	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JPanel replacePanel = null;
	private JLabel nodeDataLabel = null;
	private JComboBox<NodeBranchDataAdapter> adapterComboBox = null;
	private JLabel replaceLabel = null;
	private JTextField replaceTextField = null;
	private JPanel positionPanel = null;
	private JRadioButton beginningRadioButton = null;
	private JRadioButton endRadioButton = null;
	private JRadioButton replaceRadioButton = null;
	private JLabel replaceTextLabel = null;
	private JTextField findTextField = null;
	private ButtonGroup insertGroup = new ButtonGroup();  //  @jve:decl-index=0:
	private JCheckBox caseSensitiveCheckBox = null;
	private JCheckBox wordsOnlyCheckBox = null;
	
	
	/**
	 * @param owner
	 * @wbp.parser.constructor
	 */
	public ReplaceInNodeDataDialog(Frame owner) {
		super(owner);
		setHelpCode(6);
		initialize();
		setLocationRelativeTo(owner);
	}


	/**
	 * @param owner
	 */
	public ReplaceInNodeDataDialog(Dialog owner) {
		super(owner);
		setHelpCode(6);
		initialize();
		setLocationRelativeTo(owner);
	}


	@Override
	protected boolean onExecute() {
		((NodeDataComboBoxModel)getAdapterComboBox().getModel()).setAdapters(
				getDocument().getTree());
		return true;
	}


	@Override
	protected boolean apply() {
		if (getReplaceRadioButton().isSelected() && getFindTextField().getText().equals("")) {
			JOptionPane.showMessageDialog(this, "You have to specify a text to replace or choose " +
					"to insert before or after the current text.", "Invalid input", 
					JOptionPane.ERROR_MESSAGE);
			return false;
		}
		else {
			InsertPosition pos = InsertPosition.REPLACE;
			if (getBeginningRadioButton().isSelected()) {
				pos = InsertPosition.BEFORE;
			}
			else if (getEndRadioButton().isSelected()) {
				pos = InsertPosition.AFTER;
			}
			
			getDocument().executeEdit(new ReplaceInNodeDataEdit(getDocument(), 
					(NodeBranchDataAdapter)getAdapterComboBox().getSelectedItem(), pos, 
					getFindTextField().getText(), getReplaceTextField().getText(),
					getCaseSensitiveCheckBox().isSelected(), 
					getWordsOnlyCheckBox().isSelected()));
			return true;
		}
	}


	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setTitle("Add or replace text in node/branch data");
		this.setContentPane(getJContentPane());
		getApplyButton().setVisible(false);
		this.pack();
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
			jContentPane.add(getReplacePanel(), null);
			jContentPane.add(getPositionPanel(), null);
			jContentPane.add(getButtonsPanel(), null);
		}
		return jContentPane;
	}


	/**
	 * This method initializes replacePanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getReplacePanel() {
		if (replacePanel == null) {
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.insets = new Insets(5, 0, 5, 0);
			gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints1.gridy = 0;
			gridBagConstraints1.weightx = 1.0;
			gridBagConstraints1.gridx = 1;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.insets = new Insets(0, 5, 5, 5);
			gridBagConstraints.gridx = 0;
			gridBagConstraints.anchor = GridBagConstraints.WEST;
			gridBagConstraints.gridy = 0;
			nodeDataLabel = new JLabel();
			nodeDataLabel.setText("Node/branch data:");
			replacePanel = new JPanel();
			replacePanel.setLayout(new GridBagLayout());
			replacePanel.add(nodeDataLabel, gridBagConstraints);
			replacePanel.add(getAdapterComboBox(), gridBagConstraints1);
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.insets = new Insets(0, 5, 5, 5);
			gridBagConstraints3.gridx = 0;
			gridBagConstraints3.anchor = GridBagConstraints.WEST;
			gridBagConstraints3.gridy = 1;
			replaceLabel = new JLabel();
			replaceLabel.setText("Text to insert: ");
			replacePanel.add(replaceLabel, gridBagConstraints3);
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.insets = new Insets(0, 0, 5, 0);
			gridBagConstraints5.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints5.gridy = 1;
			gridBagConstraints5.weightx = 1.0;
			gridBagConstraints5.gridx = 1;
			replacePanel.add(getReplaceTextField(), gridBagConstraints5);
		}
		return replacePanel;
	}


	/**
	 * This method initializes nodeDataComboBox	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox<NodeBranchDataAdapter> getAdapterComboBox() {
		if (adapterComboBox == null) {
			adapterComboBox = new JComboBox<NodeBranchDataAdapter>(new NodeDataComboBoxModel());
		}
		return adapterComboBox;
	}


	/**
	 * This method initializes replaceTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getReplaceTextField() {
		if (replaceTextField == null) {
			replaceTextField = new JTextField();
		}
		return replaceTextField;
	}


	/**
	 * This method initializes positionPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getPositionPanel() {
		if (positionPanel == null) {
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.insets = new Insets(0, 0, 5, 5);
			gridBagConstraints2.gridx = 1;
			gridBagConstraints2.anchor = GridBagConstraints.WEST;
			gridBagConstraints2.gridy = 4;
			GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
			gridBagConstraints10.gridwidth = 2;
			gridBagConstraints10.insets = new Insets(0, 0, 5, 5);
			gridBagConstraints10.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints10.gridy = 3;
			gridBagConstraints10.weightx = 1.0;
			gridBagConstraints10.gridx = 1;
			GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			gridBagConstraints9.insets = new Insets(0, 0, 5, 0);
			gridBagConstraints9.gridx = 1;
			gridBagConstraints9.gridwidth = 2;
			gridBagConstraints9.anchor = GridBagConstraints.WEST;
			gridBagConstraints9.gridy = 2;
			replaceTextLabel = new JLabel();
			replaceTextLabel.setText("Replace the following text");
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.insets = new Insets(0, 0, 5, 0);
			gridBagConstraints8.gridx = 0;
			gridBagConstraints8.anchor = GridBagConstraints.WEST;
			gridBagConstraints8.gridy = 2;
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.insets = new Insets(0, 0, 5, 5);
			gridBagConstraints7.gridx = 0;
			gridBagConstraints7.anchor = GridBagConstraints.WEST;
			gridBagConstraints7.gridwidth = 3;
			gridBagConstraints7.gridy = 1;
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.insets = new Insets(0, 0, 5, 5);
			gridBagConstraints6.gridx = 0;
			gridBagConstraints6.anchor = GridBagConstraints.WEST;
			gridBagConstraints6.gridwidth = 3;
			gridBagConstraints6.gridy = 0;
			positionPanel = new JPanel();
			positionPanel.setLayout(new GridBagLayout());
			positionPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Insert position/ replacement:", TitledBorder.LEFT, TitledBorder.TOP, null, new Color(51, 51, 51)));
			positionPanel.add(getBeginningRadioButton(), gridBagConstraints6);
			positionPanel.add(getEndRadioButton(), gridBagConstraints7);
			positionPanel.add(getReplaceRadioButton(), gridBagConstraints8);
			positionPanel.add(replaceTextLabel, gridBagConstraints9);
			positionPanel.add(getFindTextField(), gridBagConstraints10);
			positionPanel.add(getCaseSensitiveCheckBox(), gridBagConstraints2);
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.insets = new Insets(0, 0, 5, 0);
			gridBagConstraints4.gridx = 2;
			gridBagConstraints4.anchor = GridBagConstraints.WEST;
			gridBagConstraints4.gridy = 4;
			positionPanel.add(getWordsOnlyCheckBox(), gridBagConstraints4);
		}
		return positionPanel;
	}


	/**
	 * This method initializes beginningRadioButton	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getBeginningRadioButton() {
		if (beginningRadioButton == null) {
			beginningRadioButton = new JRadioButton();
			beginningRadioButton.setText("Before the current text");
			insertGroup.add(beginningRadioButton);
		}
		return beginningRadioButton;
	}


	/**
	 * This method initializes endRadioButton	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getEndRadioButton() {
		if (endRadioButton == null) {
			endRadioButton = new JRadioButton();
			endRadioButton.setText("After the current text");
			insertGroup.add(endRadioButton);
    }
		return endRadioButton;
	}


	/**
	 * This method initializes replaceRadioButton	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getReplaceRadioButton() {
		if (replaceRadioButton == null) {
			replaceRadioButton = new JRadioButton();
			replaceRadioButton.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(java.awt.event.ItemEvent e) {
					boolean enabled = e.getItem().equals(getReplaceRadioButton());
					getReplaceTextField().setEnabled(enabled);
					getCaseSensitiveCheckBox().setEnabled(enabled);
					getWordsOnlyCheckBox().setEnabled(enabled);
				}
			});
			insertGroup.add(replaceRadioButton);
			replaceRadioButton.setSelected(true);
		}
		return replaceRadioButton;
	}


	/**
	 * This method initializes findTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getFindTextField() {
		if (findTextField == null) {
			findTextField = new JTextField();
		}
		return findTextField;
	}


	/**
	 * This method initializes caseSensitiveCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getCaseSensitiveCheckBox() {
		if (caseSensitiveCheckBox == null) {
			caseSensitiveCheckBox = new JCheckBox();
			caseSensitiveCheckBox.setText("Case sensitive");
		}
		return caseSensitiveCheckBox;
	}


	/**
	 * This method initializes wordsOnlyCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getWordsOnlyCheckBox() {
		if (wordsOnlyCheckBox == null) {
			wordsOnlyCheckBox = new JCheckBox();
			wordsOnlyCheckBox.setText("Words only");
		}
		return wordsOnlyCheckBox;
	}

}