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
package info.bioinfweb.treegraph.gui.dialogs.io;


import info.bioinfweb.treegraph.document.io.DocumentFilter;
import info.bioinfweb.treegraph.document.io.DocumentWriter;
import info.bioinfweb.treegraph.document.io.ReadWriteFactory;
import info.bioinfweb.treegraph.document.io.ReadWriteFormat;
import info.bioinfweb.treegraph.document.io.ReadWriteParameterMap;
import info.bioinfweb.treegraph.document.io.nexus.NexusFactory;
import info.bioinfweb.treegraph.document.io.nexus.NexusFilter;
import info.bioinfweb.treegraph.document.io.xtg.XTGFilter;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.gui.dialogs.nodebranchdatainput.NodeBranchDataInput;
import info.bioinfweb.treegraph.gui.mainframe.MainFrame;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.Frame;
import java.awt.GridBagLayout;

import javax.swing.BoxLayout;
import java.io.File;
import javax.swing.JLabel;
import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;
import java.awt.Font;
import java.awt.Color;
import java.awt.GridBagConstraints;
import javax.swing.JCheckBox;
import java.awt.Insets;



public class ExportToNewickStringDialog extends FileDialog {
	private static final long serialVersionUID = 1L;
	
	
	private JPanel jContentPane = null;
	private JFileChooser fileChooser;  //  This field must not be set to anything (e.g. null) because the initialization performed by the super constructor (FileDialog) would be overwritten than.
	private NexusFilter nexusFilter = null;
	private JPanel nodeDataPanel = null;
	private NodeBranchDataInput internalInput = null;
	private NodeBranchDataInput leafInput = null;
	private NodeBranchDataInput branchLengthInput = null;
	private JLabel internalLabel = null;
	private JLabel leafLabel = null;
	private JCheckBox branchLengthCheckBox = null;


	/**
	 * @param owner
	 */
	public ExportToNewickStringDialog(Frame owner) {
		super(owner, FileDialog.Option.ASK_TO_OVERWRITE);
		initialize();
		setHelpCode(10);
		setLocationRelativeTo(owner);
	}


	@Override
	protected boolean onExecute() {
		String name = getDocument().getDefaultNameOrPath();
		if (name.endsWith(XTGFilter.EXTENSION)) {
			name = name.substring(0, name.length() - XTGFilter.EXTENSION.length());
		}
		if (name.length() > 0) {
			getFileChooser().setSelectedFile(new File(name));
		}
		
		getInternalInput().setAdapters(getDocument().getTree(), true, true, true, false, false);
		getLeafInput().setAdapters(getDocument().getTree(), true, true, true, false, false);
		getBranchLengthInput().setAdapters(getDocument().getTree(), false, false, true, true, false);
		
		boolean branchLength = getBranchLengthInput().getModel().getSize() > 0;
		getBranchLengthCheckBox().setEnabled(branchLength);
		if (!branchLength) {
			getBranchLengthCheckBox().setSelected(branchLength);
		}
		
		return true;
	}


	@Override
	protected File getSelectedFile() {
		File result = super.getSelectedFile();
		if (result != null) {
			DocumentFilter filter = (DocumentFilter)getFileChooser().getFileFilter(); 
			if (!filter.validExtension(result.getAbsolutePath())) {
				result = new File(result.getAbsolutePath() + filter.getDefaultExtension());
			}
		}
		return result;
	}


	@Override
	protected boolean onApply(File file) {
		DocumentWriter writer;
		if (((DocumentFilter)getFileChooser().getFileFilter()).equals(nexusFilter)) {
			writer = ReadWriteFactory.getInstance().getWriter(ReadWriteFormat.NEXUS);
		}
		else {
			writer = ReadWriteFactory.getInstance().getWriter(ReadWriteFormat.NEWICK);
		}
		
		try {
			NodeBranchDataAdapter branchLengthAdapter = null;
			if (getBranchLengthCheckBox().isSelected()) {
				branchLengthAdapter = getBranchLengthInput().getSelectedAdapter();
			}
			ReadWriteParameterMap properties = new ReadWriteParameterMap();
			properties.put(ReadWriteParameterMap.KEY_INTERNAL_NODE_NAMES_ADAPTER, getInternalInput().getSelectedAdapter());
			properties.put(ReadWriteParameterMap.KEY_LEAF_NODE_NAMES_ADAPTER, getLeafInput().getSelectedAdapter());
			properties.put(ReadWriteParameterMap.KEY_BRANCH_LENGTH_ADAPTER, branchLengthAdapter);
			writer.write(getDocument(), file, properties);
			return true;
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(MainFrame.getInstance(), 
					"An error occured when trying to write to the file \"" + 
					file.getAbsolutePath() + "\".\nError message: \"" + e.getMessage() + "\"", 
					"Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}


	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setTitle("Export to Newick / Nexus");
		this.setContentPane(getJContentPane());
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
			jContentPane.add(getFileChooser(), null);
			jContentPane.add(getNodeDataPanel(), null);
			getApplyButton().setVisible(false);
			getOkButton().setText("Export");
			jContentPane.add(getButtonsPanel(), null);
		}
		return jContentPane;
	}


	/**
	 * This method initializes fileChooser	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	@Override
	protected JFileChooser getFileChooser() {
		if (fileChooser == null) {
			fileChooser = new JFileChooser();
			fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
			fileChooser.setControlButtonsAreShown(false);
			
			if (fileChooser.getFileFilter() != null) {  // "Alle Datein"-Filter entfernen
				fileChooser.removeChoosableFileFilter(fileChooser.getFileFilter());
			}
			nexusFilter = new NexusFilter();
			fileChooser.addChoosableFileFilter(nexusFilter);
			fileChooser.addChoosableFileFilter(ReadWriteFactory.getInstance().getFilter(ReadWriteFormat.NEWICK));
			fileChooser.setFileFilter(nexusFilter);
		}
		return fileChooser;
	}


	/**
	 * This method initializes nodeDataPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getNodeDataPanel() {
		if (nodeDataPanel == null) {
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.gridy = 2;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.anchor = GridBagConstraints.WEST;
			gridBagConstraints1.insets = new Insets(0, 2, 0, 0);
			gridBagConstraints1.gridy = 1;
			leafLabel = new JLabel();
			leafLabel.setText("Leaf node names: ");
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.anchor = GridBagConstraints.WEST;
			gridBagConstraints.insets = new Insets(0, 2, 0, 0);
			gridBagConstraints.gridy = 0;
			internalLabel = new JLabel();
			internalLabel.setText("Internal node names: ");
			nodeDataPanel = new JPanel();
			nodeDataPanel.setLayout(new GridBagLayout());
			nodeDataPanel.setBorder(BorderFactory.createTitledBorder(null, "Source data", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			nodeDataPanel.add(internalLabel, gridBagConstraints);
			nodeDataPanel.add(leafLabel, gridBagConstraints1);
			nodeDataPanel.add(getBranchLengthCheckBox(), gridBagConstraints2);
			internalInput = new NodeBranchDataInput(nodeDataPanel, 1, 0);
			leafInput = new NodeBranchDataInput(nodeDataPanel, 1, 1);
			branchLengthInput = new NodeBranchDataInput(nodeDataPanel, 1, 2);
		}
		return nodeDataPanel;
	}


	public NodeBranchDataInput getInternalInput() {
		getNodeDataPanel();
		return internalInput;
	}


	public NodeBranchDataInput getLeafInput() {
		getNodeDataPanel();
		return leafInput;
	}


	public NodeBranchDataInput getBranchLengthInput() {
		getNodeDataPanel();
		return branchLengthInput;
	}


	/**
	 * This method initializes branchLengthCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getBranchLengthCheckBox() {
		if (branchLengthCheckBox == null) {
			branchLengthCheckBox = new JCheckBox();
			branchLengthCheckBox.setText("Branch lengths: ");
			branchLengthCheckBox.setSelected(true);
			branchLengthCheckBox.addItemListener(new java.awt.event.ItemListener() {
						public void itemStateChanged(java.awt.event.ItemEvent e) {
							getBranchLengthInput().setEnabled(getBranchLengthCheckBox().isSelected());
						}
					});
		}
		return branchLengthCheckBox;
	}
}