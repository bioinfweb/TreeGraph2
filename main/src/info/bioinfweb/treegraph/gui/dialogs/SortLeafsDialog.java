/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2014  Ben Stöver, Kai Müller
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


import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.nodebranchdata.HiddenDataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.TextLabelAdapter;
import info.bioinfweb.treegraph.document.undo.ImportTextElementDataParameters;
import info.bioinfweb.treegraph.document.undo.edit.SortLeafsEdit;
import info.bioinfweb.treegraph.gui.actions.edit.SortLeafsAction;
import info.bioinfweb.treegraph.gui.dialogs.nodebranchdatainput.NodeBranchDataInput;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;

import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.io.File;



/**
 * Dialog used in combination with {@link SortLeafsEdit} and {@link SortLeafsAction}.
 * 
 * @author Ben St&ouml;ver
 * @since 2.2.0
 */
public class SortLeafsDialog extends EditDialog {
	private JPanel jContentPane = null;
	private JPanel leafAdapterPanel = null;
	private NodeBranchDataInput adapterInput = null;
	private JPanel openedDocumentPanel;
	private JRadioButton openedDocumentRadioButton;
	private JRadioButton textFileRadioButton;
	private JTextField textFileField;
	private JComboBox<Document> openedDocumentComboBox;
	private JButton chooseTextFileButton;
	private final ButtonGroup orderSourceButtonGroup = new ButtonGroup();
	private ImportTextElementDataParametersPanel textElementDataParametersPanel;
	private JPanel orderSourcePanel;
	private JPanel textFilePanel;

	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param owner - the parent frame
	 */
	public SortLeafsDialog(Frame owner) {
	  super(owner);
	  initialize();
  }

	
	@Override
  protected boolean onExecute() {
		// Refresh opened documents:
		getOpenedDocumentsComboBoxModel().refreshDocuments();
		getOpenedDocumentsComboBoxModel().removeElement(getDocument());

		// Disable opened document option if no other documents are opened:
		boolean otherDocumentsOpen = getOpenedDocumentsComboBoxModel().getSize() > 0;
		if (getOpenedDocumentRadioButton().isSelected() && !otherDocumentsOpen) {
			getTextFileRadioButton().setSelected(true);
		}
		getOpenedDocumentRadioButton().setEnabled(otherDocumentsOpen);
		
		// Refresh adapters:
		adapterInput.setAdapters(getDocument().getTree(), true, true, true, false, false);
		if (!adapterInput.setSelectedAdapter(TextLabelAdapter.class)) {
			adapterInput.setSelectedAdapter(HiddenDataAdapter.class);
		}
	  return true;
  }

	
	@Override
  protected boolean apply() {
		if (getTextFileRadioButton().isSelected()) {
			File file = new File(getTextFileField().getText());
			if (file.exists()) {
				ImportTextElementDataParameters parameters = new ImportTextElementDataParameters();
				getTextElementDataParametersPanel().assignParameters(parameters);
			}
			else {
				JOptionPane.showMessageDialog(this, "The file \"" + file.getAbsolutePath() + "\" could not be found.", 
						"File not found", JOptionPane.ERROR_MESSAGE);
			}
		}
		else {  // Order from other document
			
		}
		
		
		
//		if (!Double.isNaN(threshold)) {
//			return true;
//		}
//		else {
//			JOptionPane.showMessageDialog(this, "The threshold value \"" + getThresholdTextField().getText() +
//							"\" is invalid. Please specify a valid numerical value.", "Invalid threshold", JOptionPane.ERROR_MESSAGE);
//			return false;
//		}
		return false;
  }
	
	
	private void enableDisableOptionalElements() {
		boolean textFileOptionEnabled = getTextFileRadioButton().isSelected();
		
		getTextFileField().setEnabled(textFileOptionEnabled);
		getChooseTextFileButton().setEnabled(textFileOptionEnabled);
		getTextElementDataParametersPanel().setEnabled(textFileOptionEnabled);
		
		getOpenedDocumentComboBox().setEnabled(!textFileOptionEnabled);
	}
	
	
	/**
	 * Initializes this dialog.
	 * 
	 * @return void
	 */
	private void initialize() {
		setHelpCode(73);  //TODO Link this ID to concrete Wiki page (ID is already registered)
		setMinimumSize(new Dimension(400, 150));
		setTitle("Sort leaf nodes");
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
			jContentPane.add(getOrderSourcePanel());
			
			jContentPane.add(getLeafAdapterPanel());
			jContentPane.add(getButtonsPanel());
		}
		return jContentPane;
	}
	
	
	private JPanel getOpenedDocumentPanel() {
		if (openedDocumentPanel == null) {
			openedDocumentPanel = new JPanel();
			openedDocumentPanel.setBorder(null);
			GridBagLayout gbl_openedDocumentPanel = new GridBagLayout();
			gbl_openedDocumentPanel.columnWidths = new int[]{0, 0, 0, 0};
			gbl_openedDocumentPanel.columnWeights = new double[]{0.0, 0.0, 1.0, Double.MIN_VALUE};
			openedDocumentPanel.setLayout(gbl_openedDocumentPanel);
			GridBagConstraints gbc_openedDocumentRadioButton = new GridBagConstraints();
			gbc_openedDocumentRadioButton.insets = new Insets(0, 0, 5, 5);
			gbc_openedDocumentRadioButton.anchor = GridBagConstraints.WEST;
			gbc_openedDocumentRadioButton.gridx = 0;
			gbc_openedDocumentRadioButton.gridy = 0;
			openedDocumentPanel.add(getOpenedDocumentRadioButton(), gbc_openedDocumentRadioButton);
			GridBagConstraints gbc_lblOpenedDocument = new GridBagConstraints();
			gbc_lblOpenedDocument.insets = new Insets(0, 0, 5, 5);
			gbc_lblOpenedDocument.anchor = GridBagConstraints.WEST;
			gbc_lblOpenedDocument.gridx = 1;
			gbc_lblOpenedDocument.gridy = 0;
			openedDocumentPanel.add(new JLabel("Opened document"), gbc_lblOpenedDocument);
			GridBagConstraints gbc_openedDocumentComboBox = new GridBagConstraints();
			gbc_openedDocumentComboBox.weightx = 1.0;
			gbc_openedDocumentComboBox.insets = new Insets(0, 0, 5, 3);
			gbc_openedDocumentComboBox.fill = GridBagConstraints.HORIZONTAL;
			gbc_openedDocumentComboBox.gridx = 2;
			gbc_openedDocumentComboBox.gridy = 0;
			openedDocumentPanel.add(getOpenedDocumentComboBox(), gbc_openedDocumentComboBox);
		}
		return openedDocumentPanel;
	}


	private JPanel getLeafAdapterPanel() {
		if (leafAdapterPanel == null) {
			leafAdapterPanel = new JPanel();
			leafAdapterPanel.setBorder(new TitledBorder(null, "Node/branch data column used to identify leaf nodes", 
							TitledBorder.LEADING, TitledBorder.TOP, null, null));
			GridBagLayout gbl_leafAdapterPanel = new GridBagLayout();
			leafAdapterPanel.setLayout(gbl_leafAdapterPanel);
			
			adapterInput = new NodeBranchDataInput(leafAdapterPanel, 0, 0);
		}
		return leafAdapterPanel;
	}
	
	
	private JRadioButton getOpenedDocumentRadioButton() {
		if (openedDocumentRadioButton == null) {
			openedDocumentRadioButton = new JRadioButton("");
			orderSourceButtonGroup.add(openedDocumentRadioButton);
		}
		return openedDocumentRadioButton;
	}
	
	
	private JRadioButton getTextFileRadioButton() {
		if (textFileRadioButton == null) {
			textFileRadioButton = new JRadioButton("");
			textFileRadioButton.addItemListener(new ItemListener() {
						public void itemStateChanged(ItemEvent e) {
							enableDisableOptionalElements();  
							// Will be called on selection and deselection. Therefore the other radio button does not need a listener. 
						}
					});
			orderSourceButtonGroup.add(textFileRadioButton);
			textFileRadioButton.setSelected(true);
		}
		return textFileRadioButton;
	}
	
	
	private JTextField getTextFileField() {
		if (textFileField == null) {
			textFileField = new JTextField();
			textFileField.setColumns(10);
		}
		return textFileField;
	}
	
	
	private JComboBox<Document> getOpenedDocumentComboBox() {
		if (openedDocumentComboBox == null) {
			openedDocumentComboBox = new JComboBox<Document>(new OpenedDocumentsComboBoxModel());
		}
		return openedDocumentComboBox;
	}
	
	
	private OpenedDocumentsComboBoxModel getOpenedDocumentsComboBoxModel() {
		return (OpenedDocumentsComboBoxModel)getOpenedDocumentComboBox().getModel();
	}
	
	
	private JButton getChooseTextFileButton() {
		if (chooseTextFileButton == null) {
			chooseTextFileButton = new JButton("...");
		}
		return chooseTextFileButton;
	}
	
	
	private ImportTextElementDataParametersPanel getTextElementDataParametersPanel() {
		if (textElementDataParametersPanel == null) {
			textElementDataParametersPanel = new ImportTextElementDataParametersPanel();
		}
		return textElementDataParametersPanel;
	}
	
	
	private JPanel getOrderSourcePanel() {
		if (orderSourcePanel == null) {
			orderSourcePanel = new JPanel();
			orderSourcePanel.setBorder(new TitledBorder(null, "Source for new leaf order", TitledBorder.LEADING, 
							TitledBorder.TOP, null, null));
			orderSourcePanel.setLayout(new BoxLayout(orderSourcePanel, BoxLayout.Y_AXIS));
			orderSourcePanel.add(getTextFilePanel());
			orderSourcePanel.add(getOpenedDocumentPanel());
		}
		return orderSourcePanel;
	}
	
	
	private JPanel getTextFilePanel() {
		if (textFilePanel == null) {
			textFilePanel = new JPanel();
			GridBagLayout gbl_textFilePanel = new GridBagLayout();
			gbl_textFilePanel.columnWidths = new int[]{0, 0, 0, 0, 0};
			gbl_textFilePanel.rowHeights = new int[]{0, 0};
			gbl_textFilePanel.columnWeights = new double[]{0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
			gbl_textFilePanel.rowWeights = new double[]{0.0, Double.MIN_VALUE};
			textFilePanel.setLayout(gbl_textFilePanel);
			GridBagConstraints gbc_textFileRadioButton = new GridBagConstraints();
			gbc_textFileRadioButton.insets = new Insets(0, 0, 0, 5);
			gbc_textFileRadioButton.gridx = 0;
			gbc_textFileRadioButton.gridy = 0;
			textFilePanel.add(getTextFileRadioButton(), gbc_textFileRadioButton);
			GridBagConstraints gbc_lblTextFile = new GridBagConstraints();
			gbc_lblTextFile.insets = new Insets(0, 0, 0, 5);
			gbc_lblTextFile.gridx = 1;
			gbc_lblTextFile.gridy = 0;
			textFilePanel.add(new JLabel("Text file"), gbc_lblTextFile);
			GridBagConstraints gbc_textFileField = new GridBagConstraints();
			gbc_textFileField.fill = GridBagConstraints.HORIZONTAL;
			gbc_textFileField.insets = new Insets(0, 0, 0, 5);
			gbc_textFileField.gridx = 2;
			gbc_textFileField.gridy = 0;
			textFilePanel.add(getTextFileField(), gbc_textFileField);
			GridBagConstraints gbc_chooseTextFileButton = new GridBagConstraints();
			gbc_chooseTextFileButton.insets = new Insets(0, 0, 0, 5);
			gbc_chooseTextFileButton.gridx = 3;
			gbc_chooseTextFileButton.gridy = 0;
			textFilePanel.add(getChooseTextFileButton(), gbc_chooseTextFileButton);
			GridBagConstraints gbc_textElementDataParametersPanel = new GridBagConstraints();
			gbc_textElementDataParametersPanel.gridwidth = 3;
			gbc_textElementDataParametersPanel.fill = GridBagConstraints.HORIZONTAL;
			gbc_textElementDataParametersPanel.gridx = 1;
			gbc_textElementDataParametersPanel.gridy = 1;
			textFilePanel.add(getTextElementDataParametersPanel(), gbc_textElementDataParametersPanel);
		}
		return textFilePanel;
	}
}
