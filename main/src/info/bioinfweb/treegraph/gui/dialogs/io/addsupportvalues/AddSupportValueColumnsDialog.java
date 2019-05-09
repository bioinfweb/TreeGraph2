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
package info.bioinfweb.treegraph.gui.dialogs.io.addsupportvalues;


import info.bioinfweb.treegraph.Main;
import info.bioinfweb.treegraph.document.Tree;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeNameAdapter;
import info.bioinfweb.treegraph.document.undo.file.addsupportvalues.AddSupportValuesParameters;
import info.bioinfweb.treegraph.gui.dialogs.nodebranchdatainput.NodeBranchDataInput;
import info.bioinfweb.wikihelp.client.OkCancelApplyWikiHelpDialog;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;



public class AddSupportValueColumnsDialog extends OkCancelApplyWikiHelpDialog {	
	private JPanel jContentPane;
	
	private JPanel importSupportValuesPanel;
	private JLabel supportColumnLabel;
	private NodeBranchDataInput supportColumnInput;
	private JLabel leavesColumnLabel;
	private NodeBranchDataInput leavesColumnInput;
	
	private AddSupportValuesParameters addSupportValuesParameters;
	
	
	public AddSupportValueColumnsDialog(Frame owner) {
		super(owner, true, Main.getInstance().getWikiHelp());
		setHelpCode(84);
		initialize();
		setLocationRelativeTo(owner);
	}
	
	
	public boolean execute(AddSupportValuesParameters addSupportValuesParameters, Tree tree) {
		this.addSupportValuesParameters = addSupportValuesParameters;
		
		supportColumnInput.setAdapters(tree, false, true, true, false, false, "");
		supportColumnInput.setSelectedAdapter(addSupportValuesParameters.getSourceDocument().getDefaultSupportAdapter());
		leavesColumnInput.setAdapters(tree, true, true, false, false, false, "");  //TODO Ideally unique node names would only be selectable, if the XTG is the imported format. In all other cases the unique node names have just been generated and were not present in the source file.
		leavesColumnInput.setSelectedAdapter(NodeNameAdapter.class);
		
		pack();	
		return execute();
	}


	@Override
	protected boolean apply() {
		addSupportValuesParameters.setSourceSupportColumn(supportColumnInput.getSelectedAdapter());
		addSupportValuesParameters.setSourceLeavesColumn(leavesColumnInput.getSelectedAdapter());
		return true;
	}
	
	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		setTitle("Select columns for import");
		getApplyButton().setVisible(false);
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
			jContentPane.add(getImportSupportValuesPanel(), null);
			jContentPane.add(getButtonsPanel(), null);
		}
		return jContentPane;
	}


	private JPanel getImportSupportValuesPanel() {
		if (importSupportValuesPanel == null) {
			importSupportValuesPanel = new JPanel();
			importSupportValuesPanel.setLayout(new GridBagLayout());
			
			GridBagConstraints gbc_supportColumnLabel = new GridBagConstraints();	
			gbc_supportColumnLabel.gridx = 0;
			gbc_supportColumnLabel.anchor = GridBagConstraints.WEST;
			gbc_supportColumnLabel.gridy =  0;
			gbc_supportColumnLabel.insets = new Insets(5, 3, 5, 0);
			importSupportValuesPanel.add(getSupportColumnLabel(), gbc_supportColumnLabel);
			supportColumnInput = new NodeBranchDataInput(importSupportValuesPanel, 1, 0);
			
			GridBagConstraints gbc_lblNodebranchDataColumn = new GridBagConstraints();
			gbc_lblNodebranchDataColumn.anchor = GridBagConstraints.WEST;
			gbc_lblNodebranchDataColumn.gridx = 0;
			gbc_lblNodebranchDataColumn.gridy = 1;
			gbc_lblNodebranchDataColumn.insets = new Insets(5, 3, 5, 0);
			importSupportValuesPanel.add(getLeavesColumnLabel(), gbc_lblNodebranchDataColumn);
			leavesColumnInput = new NodeBranchDataInput(importSupportValuesPanel, 1, 1);
		}
		return importSupportValuesPanel;
	}


	private JLabel getSupportColumnLabel() {
		if (supportColumnLabel == null) {
			supportColumnLabel = new JLabel();
			supportColumnLabel.setText("Node/branch data column in imported document containing support values: ");
		}
		return supportColumnLabel;
	}
	
	
	private JLabel getLeavesColumnLabel() {
		if (leavesColumnLabel == null) {
			leavesColumnLabel = new JLabel("Node/branch data column in imported document identifying the leaves: ");
		}
		return leavesColumnLabel;
	}
}
