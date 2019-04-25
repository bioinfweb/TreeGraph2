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
package info.bioinfweb.treegraph.gui.dialogs.io.ancestralstate;


import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.UIManager;
import javax.swing.JRadioButton;

import info.bioinfweb.treegraph.Main;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.NodeType;
import info.bioinfweb.treegraph.document.io.ancestralstate.BayesTraitsCommandsWriter;
import info.bioinfweb.treegraph.document.tools.TreeSerializer;
import info.bioinfweb.treegraph.gui.CurrentDirectoryModel;
import info.bioinfweb.treegraph.gui.dialogs.EditDialog;
import info.bioinfweb.treegraph.gui.dialogs.nodebranchdatainput.NodeBranchDataInput;
import info.bioinfweb.treegraph.gui.mainframe.MainFrame;
import info.bioinfweb.wikihelp.client.JHTMLLabel;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;



public class GenerateBayesTraitsInputDialog extends EditDialog {
	public static final String EXPORT_FILE_EXT = "txt";	
	
	private BayesTraitsCommandsWriter commandWriter = new BayesTraitsCommandsWriter();
	
	private JPanel jContentPane = null;
	
	private JPanel filesPanel = null;
	private JTextField outputFileTextField = null;
	private JButton changeOutputFileButton = null;
	private JFileChooser fileChooser = null;
	
	private JPanel nodesPanel = null;
	private ButtonGroup nodesButtonGroup = null;
	private JRadioButton allNodesRadioButton = null;
	private JRadioButton selectedNodesRadioButton = null;
	
	private JPanel commandTypePanel = null;
	private JRadioButton addNodeRadioButton = null;
	private JRadioButton addMRCARadioButton = null;
	private ButtonGroup commandTypeButtonGroup = null;
	
	private JPanel nodeNamesPanel = null;
	private JLabel terminalNodeNamesInputLabel = null;
	private NodeBranchDataInput terminalNodeNamesColumnInput = null;
	private JLabel internalNodeNamesInputLabel = null;
	private NodeBranchDataInput internalNodeNamesColumnInput = null;
	
	private JPanel helpTextPanel = null;
	private JHTMLLabel helpTextLabel;
	
	private JPanel copyButtonPanel = null;
	private JButton copyButton = null;

	
	public GenerateBayesTraitsInputDialog(MainFrame owner) {
	  super(owner);
		setHelpCode(60);
		initialize();
		setLocationRelativeTo(owner);
  }

	
	@Override
  protected boolean onExecute() {
		getTerminalNodeNamesColumnInput().setAdapters(getDocument().getTree(), true, true, false, false, false, null);
		getTerminalNodeNamesColumnInput().setSelectedAdapter(getDocument().getDefaultLeafAdapter());
	  return true;
  }

	
	@Override
  protected boolean apply() {
		String outputFilePath = getOutputFileTextField().getText();
		File file = new File(outputFilePath);
		boolean write = true;
		
		if (file.exists()) {
			write = (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(this, "The file \"" + 
					file.getName() + "\" already exists.\n Do you want to overwrite it?", "Warning", 
	      JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE));
		}
		if (write) {
			try {
				writeBayesTraitsCommands(new BufferedWriter(new FileWriter(file)));
			}
			catch (IOException e) {
				JOptionPane.showMessageDialog(this, "The error \"" + e.getMessage() + "\" occured when writing to the file \"" + file.getName() + 
						"\".", "Error", JOptionPane.ERROR_MESSAGE);
				return false;
			}
		}
		return write;
  }
	
	
	private void writeBayesTraitsCommands(Writer writer) throws IOException {
		Node[] nodes;
		
		if (getSelectedNodesRadioButton().isSelected()) {
			nodes = getSelection().getAllElementsOfType(Node.class, false);
		}
		else {
			nodes = TreeSerializer.getElementsInSubtree(getDocument().getTree().getPaintStart(), NodeType.INTERNAL_NODES, Node.class);
		}
		
		if (getAddNodeRadioButton().isSelected()) {
			commandWriter.write(writer, "AddNode ", nodes, getTerminalNodeNamesColumnInput().getSelectedAdapter(), getInternalNodeNamesColumnInput().getSelectedAdapter());
		}
		else if (getAddMRCARadioButton().isSelected()) {
			commandWriter.write(writer, "AddMRCA ", nodes, getTerminalNodeNamesColumnInput().getSelectedAdapter(), getInternalNodeNamesColumnInput().getSelectedAdapter());
		}
	}

	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		setTitle("Generate BayesTraits commands");
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
			jContentPane.setLayout(new BoxLayout(jContentPane, BoxLayout.Y_AXIS));
			jContentPane.add(getFilesPanel());
			jContentPane.add(getNodesPanel());
			jContentPane.add(getCommandTypePanel());
			jContentPane.add(getNodeNamesPanel());
			jContentPane.add(getHelpTextPanel());
			jContentPane.add(getCopyButtonPanel());
			jContentPane.add(getButtonsPanel(), null);
			getApplyButton().setVisible(false);
		}
		return jContentPane;
	}
	
	
	protected JPanel getFilesPanel() {
		if (filesPanel == null) {
			filesPanel = new JPanel();
			filesPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Output file", TitledBorder.LEADING, TitledBorder.TOP, null, null));
			filesPanel.setLayout(new GridBagLayout());
			
			GridBagConstraints gbc_outputFolderTextField = new GridBagConstraints();
			gbc_outputFolderTextField.insets = new Insets(2, 2, 2, 1);
			gbc_outputFolderTextField.fill = GridBagConstraints.HORIZONTAL;
			gbc_outputFolderTextField.gridx = 0;
			gbc_outputFolderTextField.gridy = 0;
			gbc_outputFolderTextField.weightx = 1.0;
			filesPanel.add(getOutputFileTextField(), gbc_outputFolderTextField);
			
			GridBagConstraints gbc_changeOutputFolderButton = new GridBagConstraints();
			gbc_changeOutputFolderButton.insets = new Insets(2, 1, 2, 2);
			gbc_changeOutputFolderButton.gridx = 1;
			gbc_changeOutputFolderButton.gridy = 0;
			filesPanel.add(getChangeOutputFileButton(), gbc_changeOutputFolderButton);
		}
		return filesPanel;
	}
	
	
	private JTextField getOutputFileTextField() {
		if (outputFileTextField == null) {
			outputFileTextField = new JTextField();
		}
		return outputFileTextField;
	}


	private JButton getChangeOutputFileButton() {
		if (changeOutputFileButton == null) {
			changeOutputFileButton = new JButton();
			changeOutputFileButton.setText("...");
			changeOutputFileButton.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							getFileChooser().setSelectedFile(new File(getOutputFileTextField().getText()));
							if (JFileChooser.APPROVE_OPTION == getFileChooser().showSaveDialog(MainFrame.getInstance())) {
								String path = getFileChooser().getSelectedFile().getAbsolutePath();
								if (!path.endsWith("." + EXPORT_FILE_EXT)) {
									path += "." + EXPORT_FILE_EXT;
								}
								getOutputFileTextField().setText(path);
							}
						}
					});
		}
		return changeOutputFileButton;
	}
	
	
	private JFileChooser getFileChooser() {
		if (fileChooser == null) {
			fileChooser = new JFileChooser();
			fileChooser.setFileFilter(new FileNameExtensionFilter(
	        "Text files", EXPORT_FILE_EXT));
			CurrentDirectoryModel.getInstance().addFileChooser(fileChooser);  // Necessary because this dialog does not inherit from FileDialog
		}
		return fileChooser;
	}
	
	
	private JPanel getNodesPanel() {
		if (nodesPanel == null) {
			nodesPanel = new JPanel();
			nodesPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Nodes to be reconstructed", TitledBorder.LEADING, TitledBorder.TOP, null, null));
			nodesPanel.setLayout(new GridBagLayout());
			
			GridBagConstraints gbc_allNodesRadioButton = new GridBagConstraints();
			gbc_allNodesRadioButton.insets = new Insets(2, 0, 2, 0);
			gbc_allNodesRadioButton.gridx = 0;
			gbc_allNodesRadioButton.gridy = 0;
			nodesPanel.add(getAllNodesRadioButton(), gbc_allNodesRadioButton);

			GridBagConstraints gbc_selectedNodesRadioButton = new GridBagConstraints();
			gbc_selectedNodesRadioButton.insets = new Insets(2, 0, 2, 0);
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
	
	
	private JPanel getCommandTypePanel() {
		if (commandTypePanel == null) {
			commandTypePanel = new JPanel();
			commandTypePanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Type of BayesTraits command", TitledBorder.LEADING, TitledBorder.TOP, null, null));
			commandTypePanel.setLayout(new GridBagLayout());
			
			GridBagConstraints gbc_addNodeRadioButton = new GridBagConstraints();
			gbc_addNodeRadioButton.insets = new Insets(2, 0, 2, 0);
			gbc_addNodeRadioButton.gridx = 0;
			gbc_addNodeRadioButton.gridy = 0;
			commandTypePanel.add(getAddNodeRadioButton(), gbc_addNodeRadioButton);
			
			GridBagConstraints gbc_addMRCARadioButton = new GridBagConstraints();
			gbc_addMRCARadioButton.insets = new Insets(2, 0, 2, 0);
			gbc_addMRCARadioButton.gridx = 1;
			gbc_addMRCARadioButton.gridy = 0;
			commandTypePanel.add(getAddMRCARadioButton(), gbc_addMRCARadioButton);
		}
		return commandTypePanel;
	}


	private ButtonGroup getCommandTypeButtonGroup() {
		if (commandTypeButtonGroup == null) {
			commandTypeButtonGroup = new ButtonGroup();
		}
		return commandTypeButtonGroup;
	}


	private JRadioButton getAddNodeRadioButton() {
		if (addNodeRadioButton == null) {
			addNodeRadioButton = new JRadioButton("AddNode command");
			addNodeRadioButton.setSelected(true);
			getCommandTypeButtonGroup().add(addNodeRadioButton);
		}
		return addNodeRadioButton;
	}
	
	
	private JRadioButton getAddMRCARadioButton() {
		if (addMRCARadioButton == null) {
			addMRCARadioButton = new JRadioButton("AddMRCA command");
			getCommandTypeButtonGroup().add(addMRCARadioButton);
		}
		return addMRCARadioButton;
	}
	
	
	private JPanel getNodeNamesPanel() {
		if (nodeNamesPanel == null) {
			nodeNamesPanel = new JPanel();
			nodeNamesPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Node name column", TitledBorder.LEADING, TitledBorder.TOP, null, null));
			nodeNamesPanel.setLayout(new GridBagLayout());
		
			GridBagConstraints gbc_internalNodeNamesInputlabel = new GridBagConstraints();
			gbc_internalNodeNamesInputlabel.anchor = GridBagConstraints.WEST;
			gbc_internalNodeNamesInputlabel.insets = new Insets(2, 0, 2, 0);
			gbc_internalNodeNamesInputlabel.gridx = 0;
			gbc_internalNodeNamesInputlabel.gridy = 0;
			nodeNamesPanel.add(getInternalNodeNamesInputLabel(), gbc_internalNodeNamesInputlabel);
			
			internalNodeNamesColumnInput = new NodeBranchDataInput(nodeNamesPanel, 0, 1, 2);		
			internalNodeNamesColumnInput.setAdapters(MainFrame.getInstance().getActiveTreeFrame().getDocument().getTree(), true, true, false, false, false, "Generate internal node names");
			
			GridBagConstraints gbc_terminalNodeNamesInputlabel = new GridBagConstraints();
			gbc_terminalNodeNamesInputlabel.anchor = GridBagConstraints.WEST;
			gbc_terminalNodeNamesInputlabel.insets = new Insets(2, 0, 2, 0);
			gbc_terminalNodeNamesInputlabel.gridx = 0;
			gbc_terminalNodeNamesInputlabel.gridy = 2;
			nodeNamesPanel.add(getTerminalNodeNamesInputLabel(), gbc_terminalNodeNamesInputlabel);
			
			terminalNodeNamesColumnInput = new NodeBranchDataInput(nodeNamesPanel, 0, 3, 2);
			terminalNodeNamesColumnInput.setAdapters(MainFrame.getInstance().getActiveTreeFrame().getDocument().getTree(), true, true, false, false, false, "");
		}
		return nodeNamesPanel;
	}
	
	
	private JLabel getInternalNodeNamesInputLabel() {
		if (internalNodeNamesInputLabel == null) {
			internalNodeNamesInputLabel = new JLabel();
			internalNodeNamesInputLabel.setText("Internal node/MRCA names:");
		}
		return internalNodeNamesInputLabel;
	}
	
	
	private NodeBranchDataInput getInternalNodeNamesColumnInput() {
		getNodeNamesPanel();
		return internalNodeNamesColumnInput;
	}
	
	
	private JLabel getTerminalNodeNamesInputLabel() {
		if (terminalNodeNamesInputLabel == null) {
			terminalNodeNamesInputLabel = new JLabel();
			terminalNodeNamesInputLabel.setText("Terminal node names:");
		}
		return terminalNodeNamesInputLabel;
	}
	
	
	private NodeBranchDataInput getTerminalNodeNamesColumnInput() {
		getNodeNamesPanel();
		return terminalNodeNamesColumnInput;
	}
	
	
	private JPanel getCopyButtonPanel() {
		if (copyButtonPanel == null) {
			copyButtonPanel = new JPanel();
			copyButtonPanel.setLayout(new GridBagLayout());

			GridBagConstraints gbc_copyButton = new GridBagConstraints();
			gbc_copyButton.insets = new Insets(2, 0, 2, 0);
			gbc_copyButton.gridx = 0;
			gbc_copyButton.gridy = 0;
			copyButtonPanel.add(getCopyButton(),gbc_copyButton);			
		}
		return copyButtonPanel;
	}
	
	
	private JButton getCopyButton() {
		if (copyButton == null) {
			copyButton = new JButton("Copy to clipboard");
			copyButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {					
					try {
						CharArrayWriter writer = new CharArrayWriter();
						writeBayesTraitsCommands(writer);
						Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(writer.toString()), null);
					}
					catch (IOException ex) {
						throw new InternalError(ex);  //CharArrayWriter does not throw IOException
					}
				}
			});
		}
		return copyButton;
	}


	private JPanel getHelpTextPanel() {
		if (helpTextPanel == null) {
			helpTextPanel = new JPanel();
			GridBagLayout gbl_helpTextPanel = new GridBagLayout();
			gbl_helpTextPanel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
			gbl_helpTextPanel.rowWeights = new double[]{1.0, Double.MIN_VALUE};
			helpTextPanel.setLayout(gbl_helpTextPanel);
			
			GridBagConstraints gbc_helpTextLabel = new GridBagConstraints();
			gbc_helpTextLabel.fill = GridBagConstraints.BOTH;
			gbc_helpTextLabel.insets = new Insets(2, 5, 2, 5);
			gbc_helpTextLabel.gridx = 0;
			gbc_helpTextLabel.gridy = 0;
			helpTextPanel.add(getHelpTextLabel(), gbc_helpTextLabel);
		}
		return helpTextPanel;
	}
	
	
	private JHTMLLabel getHelpTextLabel() {
		if (helpTextLabel == null) {
			helpTextLabel = new JHTMLLabel(Main.getInstance().getWikiHelp());
			helpTextLabel.setHTMLContent(
					"If you also want to create a tree file or a character table see <a href='wikihelp://82'>Exporting trees as Newick/Nexus files</a> <br>"
					+ "and <a href='wikihelp://67'>Exporting node/branch data</a> or click the help button.");
		}
		return helpTextLabel;
	}
}
