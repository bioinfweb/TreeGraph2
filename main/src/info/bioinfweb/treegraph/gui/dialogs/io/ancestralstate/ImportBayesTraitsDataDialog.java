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


import info.bioinfweb.treegraph.document.undo.file.ancestralstate.AncestralStateImportParameters;
import info.bioinfweb.treegraph.gui.dialogs.CompareTextElementDataParametersPanel;
import info.bioinfweb.treegraph.gui.dialogs.io.FileDialog;
import info.bioinfweb.treegraph.gui.dialogs.io.TextFileFilter;
import info.bioinfweb.treegraph.gui.dialogs.nodebranchdata.NodeBranchDataColumnsDialog;
import info.bioinfweb.treegraph.gui.dialogs.nodebranchdatainput.NodeBranchDataInput;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;



/**
 * Dialog that prompts the user for a table file to be imported to node/branch data columns as well as
 * several import options. This dialog is displayed in a sequence before {@link NodeBranchDataColumnsDialog}.
 * 
 * @author Ben St&ouml;ver
 */
public class ImportBayesTraitsDataDialog extends FileDialog {
	private static final long serialVersionUID = 1L;

	
	private JPanel jContentPane = null;
	private JPanel filePanel = null;
	private JFileChooser fileChooser;  //  This field must not be set to anything (e.g. null) because the initialization performed by the super constructor (FileDialog) would be overwritten than.
	private JLabel nodeIdentifierLabel;
	private NodeBranchDataInput keyColumnInput;
	private JPanel keyColumnPanel;
	private CompareTextElementDataParametersPanel textElementDataParametersPanel;


	/**
	 * @param owner
	 */
	public ImportBayesTraitsDataDialog(Frame owner) {
		super(owner, FileDialog.Option.FILE_MUST_EXIST);
		setHelpCode(80);
		initialize();
		setLocationRelativeTo(owner);
	}
	
	
  public void assignParameters(AncestralStateImportParameters parameters) {
		parameters.setTableFile(getSelectedFile());
		parameters.setKeyAdapter(getKeyColumnInput().getSelectedAdapter());
		getTextElementDataParametersPanel().assignToParameters(parameters);
  }


	@Override
	protected boolean onApply(File file) {
		return true;
	}


	@Override
	protected boolean onExecute() {
		getKeyColumnInput().setAdapters(getDocument().getTree(), true, true, true, false, false, "");
		getKeyColumnInput().setSelectedAdapter(getDocument().getDefaultLeafAdapter());
		return true;
	}


	/**
	 * This method initializes this
	 *
	 * @return void
	 */
	private void initialize() {
		setTitle("Import BayesTraits Log Data");
		setContentPane(getJContentPane());
		pack();
	}

	
	@Override
	protected JFileChooser getFileChooser() {
		if (fileChooser == null) {
			fileChooser = new JFileChooser();
			fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
			fileChooser.setControlButtonsAreShown(false);
			FileFilter textFilter = new TextFileFilter();
			fileChooser.addChoosableFileFilter(textFilter);
			fileChooser.setFileFilter(textFilter);
		}
		return fileChooser;
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
			jContentPane.add(getFilePanel(), null);
			jContentPane.add(getKeyColumnPanel());
			jContentPane.add(getButtonsPanel(), null);
			getApplyButton().setVisible(false);
			getOkButton().setText("Next >");
		}
		return jContentPane;
	}

	
	/**
	 * This method initializes filePanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getFilePanel() {
		if (filePanel == null) {
			filePanel = new JPanel();
			filePanel.setLayout(new BoxLayout(getFilePanel(), BoxLayout.Y_AXIS));
			filePanel.setBorder(BorderFactory.createTitledBorder(null, "File", 
					TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, 
					new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			filePanel.add(getFileChooser(), null);
		}
		return filePanel;
	}
	
	
	private JLabel getNodeIdentifierLabel() {
		if (nodeIdentifierLabel == null) {
			nodeIdentifierLabel = new JLabel("Column in tree to identify nodes: ");
		}
		return nodeIdentifierLabel;
	}
	
	
	private NodeBranchDataInput getKeyColumnInput() {
		if (keyColumnInput == null) {
			getKeyColumnPanel();
		}
		return keyColumnInput;
	}
	
	
	private JPanel getKeyColumnPanel() {
		if (keyColumnPanel == null) {
			keyColumnPanel = new JPanel();
			keyColumnPanel.setBorder(new TitledBorder(null, "Node names", TitledBorder.LEADING, TitledBorder.TOP, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			GridBagLayout gbl_keyColumnPanel = new GridBagLayout();
			gbl_keyColumnPanel.rowWeights = new double[]{0.0, 1.0};
			gbl_keyColumnPanel.columnWeights = new double[]{0.0, 1.0};
			keyColumnPanel.setLayout(gbl_keyColumnPanel);
			keyColumnInput = new NodeBranchDataInput(keyColumnPanel, 1, 0);
			GridBagConstraints gbc_nodeIdentifierLabel = new GridBagConstraints();
			gbc_nodeIdentifierLabel.anchor = GridBagConstraints.WEST;
			gbc_nodeIdentifierLabel.insets = new Insets(0, 0, 5, 0);
			gbc_nodeIdentifierLabel.gridx = 0;
			gbc_nodeIdentifierLabel.gridy = 0;
			keyColumnPanel.add(getNodeIdentifierLabel(), gbc_nodeIdentifierLabel);			
			GridBagConstraints gbc_textElementDataParametersPanel = new GridBagConstraints();
			gbc_textElementDataParametersPanel.gridwidth = 2;
			gbc_textElementDataParametersPanel.fill = GridBagConstraints.BOTH;
			gbc_textElementDataParametersPanel.gridx = 0;
			gbc_textElementDataParametersPanel.gridy = 1;
			keyColumnPanel.add(getTextElementDataParametersPanel(), gbc_textElementDataParametersPanel);
  	}
		return keyColumnPanel;
	}
	
	
	private CompareTextElementDataParametersPanel getTextElementDataParametersPanel() {
		if (textElementDataParametersPanel == null) {
			textElementDataParametersPanel = new CompareTextElementDataParametersPanel();
		}
		return textElementDataParametersPanel;
	}
}