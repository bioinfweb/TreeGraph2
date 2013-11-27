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
package info.bioinfweb.treegraph.gui.dialogs.io.imexporttable;


import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import info.bioinfweb.treegraph.Main;
import info.bioinfweb.treegraph.document.Tree;
import info.bioinfweb.treegraph.document.nodebranchdata.NewHiddenNodeDataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.undo.file.importtable.ImportTableData;
import info.bioinfweb.treegraph.document.undo.file.importtable.ImportTableParameters;
import info.bioinfweb.treegraph.gui.dialogs.nodebranchdatainput.NewNodeBranchDataInput;
import info.webinsel.wikihelp.client.OkCancelApplyWikiHelpDialog;



/**
 * Dialog that prompts the user for the node/branch data columns that shall hold the contents of
 * an imported table file. This dialog is displayed in a sequence after {@link SelectImportTableDialog}.
 * 
 * @author Ben St&ouml;ver
 */
public class AssignImportColumnsDialog extends OkCancelApplyWikiHelpDialog {
	private static final long serialVersionUID = 1L;
	private static final int USED_ROWS = 2;
	
	
	private JPanel jContentPane = null;
	private JPanel importPanel = null;
	private JLabel typeLabel = null;
	private JLabel idLabel = null;
	private Vector<NewNodeBranchDataInput> inputs = new Vector<NewNodeBranchDataInput>();  //  @jve:decl-index=0:
	private JLabel firstLineLabel = null;
	private JLabel keyColumnLabel = null;


	/**
	 * @param owner
	 */
	public AssignImportColumnsDialog(Frame owner) {
		super(owner, true, Main.getInstance().getWikiHelp());
		initialize();
		setLocationRelativeTo(owner);
	}


	private void createInputs(ImportTableParameters parameters, ImportTableData data, Tree tree) {
		getImportPanel().removeAll();
		inputs.clear();

		GridBagConstraints keyColumnGBC = new GridBagConstraints();
		keyColumnGBC.gridx = 1;
		keyColumnGBC.anchor = GridBagConstraints.WEST;
		keyColumnGBC.gridy = 1;
		keyColumnLabel = new JLabel();
		keyColumnLabel.setText(parameters.getKeyAdapter().toString());
		GridBagConstraints firstLineGBC = new GridBagConstraints();
		firstLineGBC.gridx = 0;
		firstLineGBC.anchor = GridBagConstraints.EAST;
		firstLineGBC.insets = new Insets(0, 8, 0, 8);
		firstLineGBC.gridy = 1;
		firstLineLabel = new JLabel();
		firstLineLabel.setText("0)");
		GridBagConstraints idGBC = new GridBagConstraints();
		idGBC.gridx = 2;
		idGBC.gridy = 0;
		idGBC.anchor = GridBagConstraints.WEST;
		idGBC.insets = new Insets(0, 0, 8, 0);
		idLabel = new JLabel();
		idLabel.setText("ID:                ");  // increase widths of text fields
		GridBagConstraints typeGBC = new GridBagConstraints();
		typeGBC.gridx = 1;
		typeGBC.anchor = GridBagConstraints.WEST;
		typeGBC.gridy = 0;
		typeGBC.insets = new Insets(0, 0, 8, 0);
		typeLabel = new JLabel();
		typeLabel.setText("Node data type:");
		importPanel.add(typeLabel, typeGBC);
		importPanel.add(idLabel, idGBC);
		importPanel.add(firstLineLabel, firstLineGBC);
		importPanel.add(keyColumnLabel, keyColumnGBC);

		for (int i = 1; i <= data.columnCount(); i++) {
			GridBagConstraints labelGBC = new GridBagConstraints();
			labelGBC.anchor = GridBagConstraints.EAST;
			labelGBC.gridx = 0;
			labelGBC.gridy = i + USED_ROWS - 1;
			labelGBC.insets = new Insets(0, 8, 0, 8);
			
			getImportPanel().add(new JLabel("" + i + ")"), labelGBC);
			NewNodeBranchDataInput input = new NewNodeBranchDataInput(getImportPanel(), 1, i + USED_ROWS - 1, true);
			input.setAdapters(tree, false, true, true, false, true);
			input.setSelectedAdapter(NewHiddenNodeDataAdapter.class);
			if (data.containsHeadings()) {
				input.setID(data.getHeading(i - 1));
			}
			inputs.add(input);
		}
		pack();
	}
	
	
	public void execute(ImportTableParameters parameters, ImportTableData data, Tree tree) {
		createInputs(parameters, data, tree);
		if (execute()) {
			NodeBranchDataAdapter[] adapters = new NodeBranchDataAdapter[data.columnCount()];
			for (int i = 0; i < adapters.length; i++) {
				adapters[i] = inputs.get(i).getSelectedAdapter();
			}
			parameters.setImportAdapters(adapters);
		}
	}
	
	
	@Override
	protected boolean apply() {
		//TODO Auf überscheiben prüfen, wenn angegebene IDs bereits existieren und nachfragen
		return true;
	}


	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		setHelpCode(24);
		setTitle("Import table as node/branch data");
		setSize(300, 200);
		getApplyButton().setVisible(false);
		setContentPane(getJContentPane());
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
			jContentPane.add(getImportPanel(), null);
			jContentPane.add(getButtonsPanel(), null);
		}
		return jContentPane;
	}


	/**
	 * This method initializes importPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getImportPanel() {
		if (importPanel == null) {
			importPanel = new JPanel();
			importPanel.setLayout(new GridBagLayout());
		}
		return importPanel;
	}

}