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


import info.bioinfweb.treegraph.document.Branch;
import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.TextElementData;
import info.bioinfweb.treegraph.document.nodebranchdata.HiddenDataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeNameAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.TextLabelAdapter;
import info.bioinfweb.treegraph.document.undo.ImportTextElementDataParameters;
import info.bioinfweb.treegraph.document.undo.edit.SortLeafsEdit;
import info.bioinfweb.treegraph.gui.CurrentDirectoryModel;
import info.bioinfweb.treegraph.gui.actions.edit.SortLeafsAction;
import info.bioinfweb.treegraph.gui.dialogs.io.TextFileFilter;
import info.bioinfweb.treegraph.gui.dialogs.nodebranchdatainput.NodeBranchDataInput;
import info.bioinfweb.treegraph.gui.dialogs.nodebranchdatainput.NodeDataComboBoxModel;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.ButtonGroup;

import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JLabel;
import javax.swing.UIManager;

import java.awt.Color;



/**
 * Dialog used in combination with {@link SortLeafsEdit} and {@link SortLeafsAction}.
 * 
 * @author Ben St&ouml;ver
 * @since 2.2.0
 */
public class SortLeafsDialog extends EditDialog {
	private JPanel jContentPane = null;
	private JPanel leafAdapterPanel = null;
	private JPanel openedDocumentPanel;
	private JRadioButton openedDocumentRadioButton;
	private JRadioButton textFileRadioButton;
	private JTextField textFileField;
	private JComboBox<Document> openedDocumentComboBox;
	private JButton chooseTextFileButton;
	private final ButtonGroup orderSourceButtonGroup = new ButtonGroup();
	private ImportTextElementDataParametersPanel textElementDataParametersPanel;
	private JFileChooser fileChooser = null;
	private JComboBox<NodeBranchDataAdapter> sourceAdapterComboBox;
	private JComboBox<NodeBranchDataAdapter> targetAdapterComboBox;

	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param owner - the parent frame
	 */
	public SortLeafsDialog(Frame owner) {
	  super(owner);
	  initialize();
  }

	
	private void updateSourceAdapters() {
		if (getOpenedDocumentsComboBoxModel().getSelectedItem() != null) {
			getSourceAdapterModel().setAdapters(getOpenedDocumentsComboBoxModel().getSelectedItem().getTree(), 
					true, true, true, false, false);
			getSourceAdapterModel().setSelectedAdapter(NodeNameAdapter.class);
		}
		else {
			getSourceAdapterModel().clear();
		}
	}
	
	
	@Override
  protected boolean onExecute() {
		setLocationRelativeTo(getOwner());

		// Refresh opened documents:
		getOpenedDocumentsComboBoxModel().refreshDocuments();
		getOpenedDocumentsComboBoxModel().removeElement(getDocument());

		// Disable opened document option if no other documents are opened:
		boolean otherDocumentsOpen = getOpenedDocumentsComboBoxModel().getSize() > 0;
		if (getOpenedDocumentRadioButton().isSelected() && !otherDocumentsOpen) {
			getTextFileRadioButton().setSelected(true);
		}
		getOpenedDocumentRadioButton().setEnabled(otherDocumentsOpen);
		getSourceAdapterComboBox().setEnabled(otherDocumentsOpen);
		
		// Refresh adapters:
		getTargetAdapterModel().setAdapters(getDocument().getTree(), true, true, true, false, false);
		getTargetAdapterModel().setSelectedAdapter(NodeNameAdapter.class);
		updateSourceAdapters();
		
	  return true;
  }

	
	@Override
  protected boolean apply() {
		ImportTextElementDataParameters parameters = new ImportTextElementDataParameters();
		getTextElementDataParametersPanel().assignParameters(parameters);
		List<TextElementData> newOrder = null;
		
		if (getTextFileRadioButton().isSelected()) {
			File file = new File(getTextFileField().getText());
			if (file.exists()) {
				try {
					newOrder = SortLeafsEdit.orderFromTextFile(file, parameters);
				}
				catch (IOException e) {
					JOptionPane.showMessageDialog(this, "The error \"" + e.toString() + "\" ocurred when trying to read the file \"" + 
							file.getAbsolutePath() + "\".",	"IO error", JOptionPane.ERROR_MESSAGE);
				}
			}
			else {
				JOptionPane.showMessageDialog(this, "The file \"" + file.getAbsolutePath() + "\" could not be found.", 
						"File not found", JOptionPane.ERROR_MESSAGE);
			}
		}
		else {  // Order from other document
			newOrder = SortLeafsEdit.orderFromDocument(getOpenedDocumentsComboBoxModel().getSelectedItem(), 
					getSourceAdapterModel().getSelectedItem(), parameters);
		}
		
		if (newOrder != null) {
			Node root = getSelection().getFirstElementOfType(Node.class);
			if (root == null) {
				Branch branch = getSelection().getFirstElementOfType(Branch.class);
				if (branch != null) {
					root = branch.getTargetNode();
				}
				else {
					root = getDocument().getTree().getPaintStart();
				}
			}
			
			SortLeafsEdit edit = new SortLeafsEdit(getDocument(), root,	newOrder, getTargetAdapterModel().getSelectedItem(), parameters);
			getDocument().executeEdit(edit);
			
			if (edit.hasWarnings()) {
				JOptionPane.showMessageDialog(this, edit.getWarningText(), "Warning", JOptionPane.WARNING_MESSAGE);
			}
			return true;
		}
		else {
			return false;
		}
  }
	
	
	private void enableDisableOptionalElements() {
		boolean textFileOptionEnabled = getTextFileRadioButton().isSelected();
		
		getTextFileField().setEnabled(textFileOptionEnabled);
		getChooseTextFileButton().setEnabled(textFileOptionEnabled);
		getTextElementDataParametersPanel().getParseNumericValuesCheckBox().setEnabled(textFileOptionEnabled);
		
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
			jContentPane.add(getOpenedDocumentPanel());
			
			jContentPane.add(getLeafAdapterPanel());
			jContentPane.add(getTextElementDataParametersPanel());
			jContentPane.add(getButtonsPanel());
		}
		return jContentPane;
	}
	
	
	private JPanel getOpenedDocumentPanel() {
		if (openedDocumentPanel == null) {
			openedDocumentPanel = new JPanel();
			openedDocumentPanel.setBorder(new TitledBorder(null, "Source for new leaf order", TitledBorder.LEADING, TitledBorder.TOP, null, null));
			GridBagLayout gbl_openedDocumentPanel = new GridBagLayout();
			gbl_openedDocumentPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0};
			gbl_openedDocumentPanel.columnWidths = new int[]{0, 0, 0};
			gbl_openedDocumentPanel.columnWeights = new double[]{0.0, 1.0, 0.0};
			openedDocumentPanel.setLayout(gbl_openedDocumentPanel);
			GridBagConstraints gbc_textFileRadioButton = new GridBagConstraints();
			gbc_textFileRadioButton.anchor = GridBagConstraints.WEST;
			gbc_textFileRadioButton.insets = new Insets(0, 0, 5, 5);
			gbc_textFileRadioButton.gridx = 0;
			gbc_textFileRadioButton.gridy = 0;
			openedDocumentPanel.add(getTextFileRadioButton(), gbc_textFileRadioButton);
			GridBagConstraints gbc_textFileField = new GridBagConstraints();
			gbc_textFileField.fill = GridBagConstraints.HORIZONTAL;
			gbc_textFileField.insets = new Insets(0, 0, 5, 5);
			gbc_textFileField.gridx = 1;
			gbc_textFileField.gridy = 0;
			openedDocumentPanel.add(getTextFileField(), gbc_textFileField);
			GridBagConstraints gbc_chooseTextFileButton = new GridBagConstraints();
			gbc_chooseTextFileButton.insets = new Insets(0, 0, 5, 0);
			gbc_chooseTextFileButton.gridx = 2;
			gbc_chooseTextFileButton.gridy = 0;
			openedDocumentPanel.add(getChooseTextFileButton(), gbc_chooseTextFileButton);
			GridBagConstraints gbc_openedDocumentRadioButton = new GridBagConstraints();
			gbc_openedDocumentRadioButton.insets = new Insets(0, 0, 5, 5);
			gbc_openedDocumentRadioButton.anchor = GridBagConstraints.WEST;
			gbc_openedDocumentRadioButton.gridx = 0;
			gbc_openedDocumentRadioButton.gridy = 1;
			openedDocumentPanel.add(getOpenedDocumentRadioButton(), gbc_openedDocumentRadioButton);
			GridBagConstraints gbc_openedDocumentComboBox = new GridBagConstraints();
			gbc_openedDocumentComboBox.gridwidth = 2;
			gbc_openedDocumentComboBox.weightx = 1.0;
			gbc_openedDocumentComboBox.insets = new Insets(0, 0, 5, 0);
			gbc_openedDocumentComboBox.fill = GridBagConstraints.HORIZONTAL;
			gbc_openedDocumentComboBox.gridx = 1;
			gbc_openedDocumentComboBox.gridy = 1;
			openedDocumentPanel.add(getOpenedDocumentComboBox(), gbc_openedDocumentComboBox);
			GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
			gbc_lblNewLabel.gridwidth = 2;
			gbc_lblNewLabel.anchor = GridBagConstraints.WEST;
			gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
			gbc_lblNewLabel.gridx = 1;
			gbc_lblNewLabel.gridy = 2;
			openedDocumentPanel.add(new JLabel("Node/branch data column to load order values from: "), gbc_lblNewLabel);
			GridBagConstraints gbc_sourceAdapterComboBox = new GridBagConstraints();
			gbc_sourceAdapterComboBox.gridwidth = 2;
			gbc_sourceAdapterComboBox.insets = new Insets(0, 0, 5, 0);
			gbc_sourceAdapterComboBox.fill = GridBagConstraints.HORIZONTAL;
			gbc_sourceAdapterComboBox.gridx = 1;
			gbc_sourceAdapterComboBox.gridy = 3;
			openedDocumentPanel.add(getSourceAdapterComboBox(), gbc_sourceAdapterComboBox);
		}
		return openedDocumentPanel;
	}


	private JPanel getLeafAdapterPanel() {
		if (leafAdapterPanel == null) {
			leafAdapterPanel = new JPanel();
			leafAdapterPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Node/branch data column used to identify leaf nodes to be sorted", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
			GridBagLayout gbl_leafAdapterPanel = new GridBagLayout();
			gbl_leafAdapterPanel.columnWeights = new double[]{1.0};
			leafAdapterPanel.setLayout(gbl_leafAdapterPanel);
			GridBagConstraints gbc_targetAdapterComboBox = new GridBagConstraints();
			gbc_targetAdapterComboBox.fill = GridBagConstraints.HORIZONTAL;
			gbc_targetAdapterComboBox.gridx = 0;
			gbc_targetAdapterComboBox.gridy = 0;
			leafAdapterPanel.add(getTargetAdapterComboBox(), gbc_targetAdapterComboBox);
		}
		return leafAdapterPanel;
	}
	
	
	private JRadioButton getOpenedDocumentRadioButton() {
		if (openedDocumentRadioButton == null) {
			openedDocumentRadioButton = new JRadioButton("Opened document");
			orderSourceButtonGroup.add(openedDocumentRadioButton);
		}
		return openedDocumentRadioButton;
	}
	
	
	private JRadioButton getTextFileRadioButton() {
		if (textFileRadioButton == null) {
			textFileRadioButton = new JRadioButton("Text file");
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
			openedDocumentComboBox.addItemListener(new ItemListener() {
						public void itemStateChanged(ItemEvent e) {
							if (e.getStateChange() == ItemEvent.SELECTED) {
								updateSourceAdapters();
							}
						}
					});
		}
		return openedDocumentComboBox;
	}
	
	
	private OpenedDocumentsComboBoxModel getOpenedDocumentsComboBoxModel() {
		return (OpenedDocumentsComboBoxModel)getOpenedDocumentComboBox().getModel();
	}
	
	
	private JFileChooser getFileChooser() {
		if (fileChooser == null) {
			fileChooser = new JFileChooser();
			fileChooser.setAcceptAllFileFilterUsed(true);
			FileFilter textFiler = new TextFileFilter();
			fileChooser.addChoosableFileFilter(textFiler);
			fileChooser.setFileFilter(textFiler);
			CurrentDirectoryModel.getInstance().addFileChooser(fileChooser);
		}
		return fileChooser;
	}
	
	
	private JButton getChooseTextFileButton() {
		if (chooseTextFileButton == null) {
			chooseTextFileButton = new JButton("...");
			chooseTextFileButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							if (getFileChooser().showOpenDialog(chooseTextFileButton) == JFileChooser.APPROVE_OPTION) {
								getTextFileField().setText(getFileChooser().getSelectedFile().getAbsolutePath());
							}
						}
					});
		}
		return chooseTextFileButton;
	}
	
	
	private ImportTextElementDataParametersPanel getTextElementDataParametersPanel() {
		if (textElementDataParametersPanel == null) {
			textElementDataParametersPanel = new ImportTextElementDataParametersPanel();
			textElementDataParametersPanel.setBorder(new TitledBorder(null, "Compare options", TitledBorder.LEADING, 
							TitledBorder.TOP, null, null));
		}
		return textElementDataParametersPanel;
	}
	
	
	private JComboBox<NodeBranchDataAdapter> getSourceAdapterComboBox() {
		if (sourceAdapterComboBox == null) {
			sourceAdapterComboBox = new JComboBox<NodeBranchDataAdapter>(new NodeDataComboBoxModel());
		}
		return sourceAdapterComboBox;
	}
	
	
	private NodeDataComboBoxModel getSourceAdapterModel() {
		return (NodeDataComboBoxModel)getSourceAdapterComboBox().getModel();
	}
	
	
	private JComboBox<NodeBranchDataAdapter> getTargetAdapterComboBox() {
		if (targetAdapterComboBox == null) {
			targetAdapterComboBox = new JComboBox<NodeBranchDataAdapter>(new NodeDataComboBoxModel());
		}
		return targetAdapterComboBox;
	}

	
	private NodeDataComboBoxModel getTargetAdapterModel() {
		return (NodeDataComboBoxModel)getTargetAdapterComboBox().getModel();
	}
}
