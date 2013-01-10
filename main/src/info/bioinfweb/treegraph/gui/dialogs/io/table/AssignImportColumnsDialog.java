/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2011  Ben Stöver, Kai Müller
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
package info.bioinfweb.treegraph.gui.dialogs.io.table;


import info.bioinfweb.treegraph.Main;
import info.bioinfweb.treegraph.document.Tree;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.UniqueNameAdapter;
import info.bioinfweb.treegraph.gui.dialogs.nodebranchdatainput.NewNodeBranchDataInput;
import info.webinsel.wikihelp.client.OkCancelApplyWikiHelpDialog;
import javax.swing.JPanel;
import javax.swing.BoxLayout;
import java.awt.GridBagLayout;
import java.util.Vector;
import javax.swing.JLabel;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.Insets;



public class AssignImportColumnsDialog extends OkCancelApplyWikiHelpDialog {
	private static final long serialVersionUID = 1L;
	private static final int USED_ROWS = 2;
	
	
	private JPanel jContentPane = null;
	private JPanel importPanel = null;
	private JLabel typeLabel = null;
	private JLabel idLabel = null;
	private Vector<NewNodeBranchDataInput> inputs = new Vector<NewNodeBranchDataInput>();  //  @jve:decl-index=0:
	private JLabel firstLineLabel = null;
	private JLabel uniqueNamesLabel = null;


	/**
	 * @param owner
	 */
	public AssignImportColumnsDialog(Frame owner) {
		super(owner, true, Main.getInstance().getWikiHelp());
		initialize();
		setLocationRelativeTo(owner);
	}


	private void createInputs(int count, Tree tree) {
		getImportPanel().removeAll();
		inputs.clear();

		GridBagConstraints uniqueNamesGBC = new GridBagConstraints();
		uniqueNamesGBC.gridx = 1;
		uniqueNamesGBC.anchor = GridBagConstraints.WEST;
		uniqueNamesGBC.gridy = 1;
		uniqueNamesLabel = new JLabel();
		uniqueNamesLabel.setText("Unique node names");
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
		idLabel.setText("ID:                ");  // Breite der Eingabefelder erhöhen.
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
		importPanel.add(uniqueNamesLabel, uniqueNamesGBC);

		for (int i = 1; i < count; i++) {
			GridBagConstraints labelGBC = new GridBagConstraints();
			labelGBC.anchor = GridBagConstraints.EAST;
			labelGBC.gridx = 0;
			labelGBC.gridy = i + USED_ROWS - 1;
			labelGBC.insets = new Insets(0, 8, 0, 8);
			
			getImportPanel().add(new JLabel("" + i + ")"), labelGBC);
			NewNodeBranchDataInput input = new NewNodeBranchDataInput(getImportPanel(), 1, i + USED_ROWS - 1, true);
			input.setAdapters(tree, true, true, false, true);
			inputs.add(input);
		}
		pack();
	}
	
	
	public NodeBranchDataAdapter[] execute(int count, Tree tree) {
		createInputs(count, tree);
		if (execute()) {
			NodeBranchDataAdapter[] result = new NodeBranchDataAdapter[count];
			result[0] = new UniqueNameAdapter();
			for (int i = 1; i < result.length; i++) {
				result[i] = inputs.get(i - 1).getSelectedAdapter();
			}
			return result;
		}
		else {
			return null;
		}			
	}
	
	
	@Override
	protected boolean apply() {
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