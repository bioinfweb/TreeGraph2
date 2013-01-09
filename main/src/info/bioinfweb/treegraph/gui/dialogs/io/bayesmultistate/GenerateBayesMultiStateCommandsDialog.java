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
package info.bioinfweb.treegraph.gui.dialogs.io.bayesmultistate;


import java.awt.Frame;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JPanel;

import info.bioinfweb.treegraph.gui.dialogs.EditDialog;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import javax.swing.JTextField;
import java.awt.Insets;
import java.util.List;

import javax.swing.JButton;
import javax.swing.border.TitledBorder;
import javax.swing.UIManager;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;



public class GenerateBayesMultiStateCommandsDialog extends EditDialog {
	//private List<DefaultListModel<E>>
	
	private JPanel jContentPane = null;
	private JPanel filesPanel = null;
	private JTextField folderTextField;
	private JLabel lblOutputFolder;
	private JTextField filePrefixTextField;
	private JPanel nodesPanel;
	private JRadioButton rdbtnAllNodesIn;
	private JRadioButton rdbtnOnlySelectedNodes;
	private JPanel charactersPpanel;
	private JScrollPane scrollPane;

	
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
		setTitle("Export node/branch data");
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
			jContentPane.add(getCharactersPpanel());
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
			nodesPanel = new JPanel();
			nodesPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Nodes to be reconstructed", TitledBorder.LEADING, TitledBorder.TOP, null, null));
			GridBagLayout gbl_nodesPanel = new GridBagLayout();
			gbl_nodesPanel.columnWidths = new int[]{0, 0, 0};
			gbl_nodesPanel.rowHeights = new int[]{0, 0};
			gbl_nodesPanel.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
			gbl_nodesPanel.rowWeights = new double[]{0.0, Double.MIN_VALUE};
			nodesPanel.setLayout(gbl_nodesPanel);
			GridBagConstraints gbc_rdbtnAllNodesIn = new GridBagConstraints();
			gbc_rdbtnAllNodesIn.insets = new Insets(0, 0, 0, 5);
			gbc_rdbtnAllNodesIn.gridx = 0;
			gbc_rdbtnAllNodesIn.gridy = 0;
			nodesPanel.add(getRdbtnAllNodesIn(), gbc_rdbtnAllNodesIn);
			GridBagConstraints gbc_rdbtnOnlySelectedNodes = new GridBagConstraints();
			gbc_rdbtnOnlySelectedNodes.gridx = 1;
			gbc_rdbtnOnlySelectedNodes.gridy = 0;
			nodesPanel.add(getRdbtnOnlySelectedNodes(), gbc_rdbtnOnlySelectedNodes);
		}
		return nodesPanel;
	}
	
	
	private JRadioButton getRdbtnAllNodesIn() {
		if (rdbtnAllNodesIn == null) {
			rdbtnAllNodesIn = new JRadioButton("All nodes in the document");
			rdbtnAllNodesIn.setSelected(true);
		}
		return rdbtnAllNodesIn;
	}
	
	
	private JRadioButton getRdbtnOnlySelectedNodes() {
		if (rdbtnOnlySelectedNodes == null) {
			rdbtnOnlySelectedNodes = new JRadioButton("Only selected nodes");
		}
		return rdbtnOnlySelectedNodes;
	}
	
	
	private JPanel getCharactersPpanel() {
		if (charactersPpanel == null) {
			charactersPpanel = new JPanel();
			charactersPpanel.setBorder(new TitledBorder(null, "Characters", TitledBorder.LEADING, TitledBorder.TOP, null, null));
			GridBagLayout gbl_charactersPpanel = new GridBagLayout();
			gbl_charactersPpanel.columnWidths = new int[]{0, 0};
			gbl_charactersPpanel.rowHeights = new int[]{0, 0};
			gbl_charactersPpanel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
			gbl_charactersPpanel.rowWeights = new double[]{1.0, Double.MIN_VALUE};
			charactersPpanel.setLayout(gbl_charactersPpanel);
			GridBagConstraints gbc_scrollPane = new GridBagConstraints();
			gbc_scrollPane.fill = GridBagConstraints.BOTH;
			gbc_scrollPane.gridx = 0;
			gbc_scrollPane.gridy = 0;
			charactersPpanel.add(getScrollPane(), gbc_scrollPane);
		}
		return charactersPpanel;
	}
	private JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
		}
		return scrollPane;
	}
}
