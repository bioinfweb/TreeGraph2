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
import info.bioinfweb.treegraph.gui.dialogs.nodebranchdatainput.NodeBranchDataInput;
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

import com.l2fprod.common.swing.JDirectoryChooser;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;



public class GenerateBayesMultiStateCommandsDialog extends EditDialog {
	//private List<DefaultListModel<E>>
	
	private JPanel jContentPane = null;
	private JPanel filesPanel = null;
	private JTextField outputFolderTextField;
	private JButton changeOutputFolderButton = null;
	private JDirectoryChooser directoryChooser = null;
	private JLabel outputFolderLabel;
	private JTextField filePrefixTextField;
	private JPanel nodesPanel = null;
	private ButtonGroup nodesButtonGroup = null;
	private JRadioButton allNodesRadioButton;
	private JRadioButton selectedNodesRadioButton;
	private NodeBranchDataList charactersPanel;
	private JPanel nodeTypePanel;
	private JRadioButton addNodeRadioButton;
	private JRadioButton addMRCARadioButton;
	private JRadioButton dependantCommandRadioButton;
	private JLabel addNodeLabel;
	private JLabel addMRCALabel;
	private JLabel dependantCommandLabel;
	private ButtonGroup commandTypeButtonGroup = null;
	private NodeBranchDataInput commandTypeColumnInput = null;

	
	public GenerateBayesMultiStateCommandsDialog(Frame owner) {
	  super(owner);
		setHelpCode(60);
		initialize();
		setLocationRelativeTo(owner);
  }

	
	@Override
  protected boolean onExecute() {
		getCharactersPanel().setIDs(getDocument());
		getCommandTypeColumnInput().setAdapters(getDocument().getTree());
	  return true;
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
			jContentPane.add(getNodeTypePanel());
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
			GridBagConstraints gbc_outputFolderLabel = new GridBagConstraints();
			gbc_outputFolderLabel.insets = new Insets(0, 0, 5, 5);
			gbc_outputFolderLabel.anchor = GridBagConstraints.WEST;
			gbc_outputFolderLabel.gridx = 0;
			gbc_outputFolderLabel.gridy = 0;
			filesPanel.add(getOutputFolderLabel(), gbc_outputFolderLabel);
			
			GridBagConstraints gbc_outputFolderTextField = new GridBagConstraints();
			gbc_outputFolderTextField.insets = new Insets(0, 0, 5, 5);
			gbc_outputFolderTextField.fill = GridBagConstraints.HORIZONTAL;
			gbc_outputFolderTextField.gridx = 1;
			gbc_outputFolderTextField.gridy = 0;
			filesPanel.add(getOutputFolderTextField(), gbc_outputFolderTextField);
			
			GridBagConstraints gbc_changeOutputFolderButton = new GridBagConstraints();
			gbc_changeOutputFolderButton.insets = new Insets(0, 0, 5, 0);
			gbc_changeOutputFolderButton.gridx = 2;
			gbc_changeOutputFolderButton.gridy = 0;
			filesPanel.add(getChangeOutputFolderButton(), gbc_changeOutputFolderButton);
			
			JLabel prefixForOutputLabel = new JLabel("Prefix for generated files: ");
			GridBagConstraints gbc_prefixForOutputLabel = new GridBagConstraints();
			gbc_prefixForOutputLabel.insets = new Insets(0, 0, 0, 5);
			gbc_prefixForOutputLabel.anchor = GridBagConstraints.WEST;
			gbc_prefixForOutputLabel.gridx = 0;
			gbc_prefixForOutputLabel.gridy = 1;
			filesPanel.add(prefixForOutputLabel, gbc_prefixForOutputLabel);
			GridBagConstraints gbc_filePrefixTextField = new GridBagConstraints();
			gbc_filePrefixTextField.fill = GridBagConstraints.HORIZONTAL;
			gbc_filePrefixTextField.gridx = 1;
			gbc_filePrefixTextField.gridy = 1;
			gbc_filePrefixTextField.gridwidth = 2;
			filesPanel.add(getFilePrefixTextField(), gbc_filePrefixTextField);
		}
		return filesPanel;
	}
	
	
	private JTextField getOutputFolderTextField() {
		if (outputFolderTextField == null) {
			outputFolderTextField = new JTextField();
		}
		return outputFolderTextField;
	}


	private JButton getChangeOutputFolderButton() {
		if (changeOutputFolderButton == null) {
			changeOutputFolderButton = new JButton("...");
			final GenerateBayesMultiStateCommandsDialog parent = this;
			changeOutputFolderButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
  						if (getDirectoryChooser().showOpenDialog(parent) == JDirectoryChooser.APPROVE_OPTION) {
								getOutputFolderTextField().setText(getDirectoryChooser().getSelectedFile().getAbsolutePath());
							}
						}
					});
		}
		return changeOutputFolderButton;
	}

	
	private JDirectoryChooser getDirectoryChooser() {
		if (directoryChooser == null) {
			directoryChooser = new JDirectoryChooser();
		}
		return directoryChooser;
	}


	private JLabel getOutputFolderLabel() {
		if (outputFolderLabel == null) {
			outputFolderLabel = new JLabel("Output folder: ");
		}
		return outputFolderLabel;
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
		}
		return nodesButtonGroup;
	}


	private JRadioButton getAllNodesRadioButton() {
		if (allNodesRadioButton == null) {
			allNodesRadioButton = new JRadioButton("All nodes in the document");
			allNodesRadioButton.setSelected(true);
			getNodesButtonGroup().add(allNodesRadioButton);
		}
		return allNodesRadioButton;
	}
	
	
	private JRadioButton getSelectedNodesRadioButton() {
		if (selectedNodesRadioButton == null) {
			selectedNodesRadioButton = new JRadioButton("Only selected nodes");
			getNodesButtonGroup().add(selectedNodesRadioButton);
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
	
	
	private JPanel getNodeTypePanel() {
		if (nodeTypePanel == null) {
			nodeTypePanel = new JPanel();
			nodeTypePanel.setBorder(new TitledBorder(null, "Type of commands to be created", TitledBorder.LEADING, TitledBorder.TOP, null, null));
			GridBagLayout gbl_nodeTypePanel = new GridBagLayout();
			gbl_nodeTypePanel.columnWidths = new int[]{0, 0, 0};
			gbl_nodeTypePanel.rowHeights = new int[]{0, 0, 0, 0, 0};
			gbl_nodeTypePanel.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
			gbl_nodeTypePanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
			nodeTypePanel.setLayout(gbl_nodeTypePanel);
			GridBagConstraints gbc_addNodeRadioButton = new GridBagConstraints();
			gbc_addNodeRadioButton.anchor = GridBagConstraints.WEST;
			gbc_addNodeRadioButton.insets = new Insets(0, 0, 5, 5);
			gbc_addNodeRadioButton.gridx = 0;
			gbc_addNodeRadioButton.gridy = 0;
			nodeTypePanel.add(getAddNodeRadioButton(), gbc_addNodeRadioButton);
			GridBagConstraints gbc_addNodeLabel = new GridBagConstraints();
			gbc_addNodeLabel.anchor = GridBagConstraints.WEST;
			gbc_addNodeLabel.insets = new Insets(0, 0, 5, 0);
			gbc_addNodeLabel.gridx = 1;
			gbc_addNodeLabel.gridy = 0;
			nodeTypePanel.add(getAddNodeLabel(), gbc_addNodeLabel);
			GridBagConstraints gbc_addMRCARadioButton = new GridBagConstraints();
			gbc_addMRCARadioButton.anchor = GridBagConstraints.WEST;
			gbc_addMRCARadioButton.insets = new Insets(0, 0, 5, 5);
			gbc_addMRCARadioButton.gridx = 0;
			gbc_addMRCARadioButton.gridy = 1;
			nodeTypePanel.add(getAddMRCARadioButton(), gbc_addMRCARadioButton);
			GridBagConstraints gbc_addMRCALabel = new GridBagConstraints();
			gbc_addMRCALabel.anchor = GridBagConstraints.WEST;
			gbc_addMRCALabel.insets = new Insets(0, 0, 5, 0);
			gbc_addMRCALabel.gridx = 1;
			gbc_addMRCALabel.gridy = 1;
			nodeTypePanel.add(getAddMRCALabel(), gbc_addMRCALabel);
			GridBagConstraints gbc_dependantCommandRadioButton = new GridBagConstraints();
			gbc_dependantCommandRadioButton.insets = new Insets(0, 0, 5, 5);
			gbc_dependantCommandRadioButton.gridx = 0;
			gbc_dependantCommandRadioButton.gridy = 2;
			nodeTypePanel.add(getDependantCommandRadioButton(), gbc_dependantCommandRadioButton);
			GridBagConstraints gbc_dependantCommandLabel = new GridBagConstraints();
			gbc_dependantCommandLabel.insets = new Insets(0, 0, 5, 0);
			gbc_dependantCommandLabel.anchor = GridBagConstraints.WEST;
			gbc_dependantCommandLabel.gridx = 1;
			gbc_dependantCommandLabel.gridy = 2;
			nodeTypePanel.add(getDependantCommandLabel(), gbc_dependantCommandLabel);
			commandTypeColumnInput = new NodeBranchDataInput(nodeTypePanel, 1, 3);
		}
		return nodeTypePanel;
	}
	
	
	protected NodeBranchDataInput getCommandTypeColumnInput() {
		getNodeTypePanel();
		return commandTypeColumnInput;
	}


	private ButtonGroup getCommandTypeButtonGroup() {
		if (commandTypeButtonGroup == null) {
			commandTypeButtonGroup = new ButtonGroup();
		}
		return commandTypeButtonGroup;
	}


	private JRadioButton getAddNodeRadioButton() {
		if (addNodeRadioButton == null) {
			addNodeRadioButton = new JRadioButton("");
			addNodeRadioButton.setSelected(true);
			getCommandTypeButtonGroup().add(addNodeRadioButton);
		}
		return addNodeRadioButton;
	}
	
	
	private JRadioButton getAddMRCARadioButton() {
		if (addMRCARadioButton == null) {
			addMRCARadioButton = new JRadioButton("");
			getCommandTypeButtonGroup().add(addMRCARadioButton);
		}
		return addMRCARadioButton;
	}
	
	
	private JRadioButton getDependantCommandRadioButton() {
		if (dependantCommandRadioButton == null) {
			dependantCommandRadioButton = new JRadioButton("");
			getCommandTypeButtonGroup().add(dependantCommandRadioButton);
		}
		return dependantCommandRadioButton;
	}
	
	
	private JLabel getAddNodeLabel() {
		if (addNodeLabel == null) {
			addNodeLabel = new JLabel("Create AddNode command for all nodes");
		}
		return addNodeLabel;
	}
	
	
	private JLabel getAddMRCALabel() {
		if (addMRCALabel == null) {
			addMRCALabel = new JLabel("Create AddMRCA command for all nodes");
		}
		return addMRCALabel;
	}
	
	
	private JLabel getDependantCommandLabel() {
		if (dependantCommandLabel == null) {
			dependantCommandLabel = new JLabel("Choose dependant on the following node/branch data column");
		}
		return dependantCommandLabel;
	}
}
