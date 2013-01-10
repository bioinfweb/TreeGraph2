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
package info.bioinfweb.treegraph.gui.dialogs.io.ancestralstate;


import java.awt.Frame;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;

import info.bioinfweb.treegraph.gui.dialogs.EditDialog;
import info.bioinfweb.treegraph.gui.dialogs.nodebranchdatainput.NodeBranchDataList;

import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import javax.swing.JTextField;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.border.TitledBorder;
import javax.swing.UIManager;
import javax.swing.JRadioButton;



public class GenerateBayesMultiStateCommandsDialog extends EditDialog {
	//private List<DefaultListModel<E>>
	
	private JPanel jContentPane = null;
	private JPanel filesPanel = null;
	private JTextField folderTextField;
	private JLabel lblOutputFolder;
	private JTextField filePrefixTextField;
	private JPanel nodesPanel;
	private ButtonGroup nodesButtonGroup = null;
	private JRadioButton allNodesRadioButton;
	private JRadioButton selectedNodesRadioButton;
	private NodeBranchDataList charactersPanel;

	
	public GenerateBayesMultiStateCommandsDialog(Frame owner) {
	  super(owner);
		setHelpCode(60);
		initialize();
		setLocationRelativeTo(owner);
  }

	
	@Override
  protected boolean onExecute() {
	  // TODO Auto-generated method stub
	  return false;
  }

	
	@Override
  protected boolean apply() {
	  // TODO Auto-generated method stub
	  return false;
  }

	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		setTitle("Generate BayesTraits input files");
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
			jContentPane.add(getFilesPanel());
			jContentPane.add(getNodesPanel());
			jContentPane.add(getCharactersPanel());
			jContentPane.add(getButtonsPanel(), null);
			getApplyButton().setVisible(false);
		}
		return jContentPane;
	}
	
	
	protected JPanel getFilesPanel() {
		if (filesPanel == null) {
			filesPanel = new JPanel();
			filesPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Files", TitledBorder.LEADING, TitledBorder.TOP, null, null));
			GridBagLayout gbl_filesPanel = new GridBagLayout();
			gbl_filesPanel.columnWidths = new int[]{0, 0, 0};
			gbl_filesPanel.rowHeights = new int[]{0, 0, 0};
			gbl_filesPanel.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
			gbl_filesPanel.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
			filesPanel.setLayout(gbl_filesPanel);
			GridBagConstraints gbc_lblOutputFolder = new GridBagConstraints();
			gbc_lblOutputFolder.insets = new Insets(0, 0, 5, 5);
			gbc_lblOutputFolder.anchor = GridBagConstraints.WEST;
			gbc_lblOutputFolder.gridx = 0;
			gbc_lblOutputFolder.gridy = 0;
			filesPanel.add(getLblOutputFolder(), gbc_lblOutputFolder);
			
			folderTextField = new JTextField();
			GridBagConstraints gbc_folderTextField = new GridBagConstraints();
			gbc_folderTextField.insets = new Insets(0, 0, 5, 5);
			gbc_folderTextField.fill = GridBagConstraints.HORIZONTAL;
			gbc_folderTextField.gridx = 1;
			gbc_folderTextField.gridy = 0;
			filesPanel.add(folderTextField, gbc_folderTextField);
			folderTextField.setColumns(10);
			
			JButton changeOutputFolderButton = new JButton("...");
			GridBagConstraints gbc_changeOutputFolderButton = new GridBagConstraints();
			gbc_changeOutputFolderButton.insets = new Insets(0, 0, 5, 0);
			gbc_changeOutputFolderButton.gridx = 2;
			gbc_changeOutputFolderButton.gridy = 0;
			filesPanel.add(changeOutputFolderButton, gbc_changeOutputFolderButton);
			
			JLabel lblPrefixForOutput = new JLabel("Prefix for generated files: ");
			GridBagConstraints gbc_lblPrefixForOutput = new GridBagConstraints();
			gbc_lblPrefixForOutput.insets = new Insets(0, 0, 0, 5);
			gbc_lblPrefixForOutput.anchor = GridBagConstraints.WEST;
			gbc_lblPrefixForOutput.gridx = 0;
			gbc_lblPrefixForOutput.gridy = 1;
			filesPanel.add(lblPrefixForOutput, gbc_lblPrefixForOutput);
			GridBagConstraints gbc_filePrefixTextField = new GridBagConstraints();
			gbc_filePrefixTextField.fill = GridBagConstraints.HORIZONTAL;
			gbc_filePrefixTextField.gridx = 1;
			gbc_filePrefixTextField.gridy = 1;
			gbc_filePrefixTextField.gridwidth = 2;
			filesPanel.add(getFilePrefixTextField(), gbc_filePrefixTextField);
		}
		return filesPanel;
	}
	
	
	private JLabel getLblOutputFolder() {
		if (lblOutputFolder == null) {
			lblOutputFolder = new JLabel("Output folder: ");
		}
		return lblOutputFolder;
	}
	
	
	private JTextField getFilePrefixTextField() {
		if (filePrefixTextField == null) {
			filePrefixTextField = new JTextField();
			filePrefixTextField.setColumns(10);
		}
		return filePrefixTextField;
	}
	
	
	private JPanel getNodesPanel() {
		if (nodesPanel == null) {
			getNodesButtonGroup();
			
			nodesPanel = new JPanel();
			nodesPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Nodes to be reconstructed", TitledBorder.LEADING, TitledBorder.TOP, null, null));
			GridBagLayout gbl_nodesPanel = new GridBagLayout();
			gbl_nodesPanel.columnWidths = new int[]{0, 0, 0};
			gbl_nodesPanel.rowHeights = new int[]{0, 0};
			gbl_nodesPanel.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
			gbl_nodesPanel.rowWeights = new double[]{0.0, Double.MIN_VALUE};
			nodesPanel.setLayout(gbl_nodesPanel);
			
			GridBagConstraints gbc_allNodesRadioButton = new GridBagConstraints();
			gbc_allNodesRadioButton.insets = new Insets(0, 0, 0, 5);
			gbc_allNodesRadioButton.gridx = 0;
			gbc_allNodesRadioButton.gridy = 0;
			nodesPanel.add(getAllNodesRadioButton(), gbc_allNodesRadioButton);

			GridBagConstraints gbc_selectedNodesRadioButton = new GridBagConstraints();
			gbc_selectedNodesRadioButton.gridx = 1;
			gbc_selectedNodesRadioButton.gridy = 0;
			nodesPanel.add(getSelectedNodesRadioButton(), gbc_selectedNodesRadioButton);
		}
		return nodesPanel;
	}
	
	
	public ButtonGroup getNodesButtonGroup() {
		if (nodesButtonGroup == null) {
			nodesButtonGroup = new ButtonGroup();
			nodesButtonGroup.add(getAllNodesRadioButton());
			nodesButtonGroup.add(getSelectedNodesRadioButton());
		}
		return nodesButtonGroup;
	}


	private JRadioButton getAllNodesRadioButton() {
		if (allNodesRadioButton == null) {
			allNodesRadioButton = new JRadioButton("All nodes in the document");
			allNodesRadioButton.setSelected(true);
		}
		return allNodesRadioButton;
	}
	
	
	private JRadioButton getSelectedNodesRadioButton() {
		if (selectedNodesRadioButton == null) {
			selectedNodesRadioButton = new JRadioButton("Only selected nodes");
		}
		return selectedNodesRadioButton;
	}
	
	
	private NodeBranchDataList getCharactersPanel() {
		if (charactersPanel == null) {
			charactersPanel = new NodeBranchDataList();
			charactersPanel.setBorder(new TitledBorder(null, "Characters", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		}
		return charactersPanel;
	}
}
