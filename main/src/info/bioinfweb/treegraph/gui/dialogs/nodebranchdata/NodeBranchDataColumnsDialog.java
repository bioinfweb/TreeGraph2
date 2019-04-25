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
package info.bioinfweb.treegraph.gui.dialogs.nodebranchdata;


import info.bioinfweb.treegraph.Main;
import info.bioinfweb.treegraph.document.Tree;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.VoidNodeBranchDataAdapter;
import info.bioinfweb.treegraph.gui.dialogs.io.imexporttable.SelectImportTableDialog;
import info.bioinfweb.treegraph.gui.dialogs.nodebranchdatainput.NewNodeBranchDataInput;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;



/**
 * Dialog that prompts the user for the node/branch data columns that shall contain the contents of
 * an imported table file. This dialog is displayed after {@link SelectImportTableDialog}.
 * 
 * @author Ben St&ouml;ver
 */
public class NodeBranchDataColumnsDialog extends AbstractNodeBranchDataColumnsDialog {
	private static final long serialVersionUID = 1L;
	private static final int USED_ROWS = 1;
	
	
	private JPanel jContentPane = null;
	private JLabel typeLabel = null;
	private JLabel idLabel = null;
	private List<NewNodeBranchDataInput> inputs = new ArrayList<NewNodeBranchDataInput>();  //  @jve:decl-index=0:
	private NodeBranchDataAdapter[] adapters = null;
	private boolean[] allowEdit;


	/**
	 * Creates a new instance of this dialog.
	 * 
	 * @param owner the parent window
	 */
	public NodeBranchDataColumnsDialog(Frame owner, String title, int helpCode) {
		super(owner, true, Main.getInstance().getWikiHelp());
		setHelpCode(helpCode);
		setTitle(title);
		initialize();
		setLocationRelativeTo(owner);
	}


	private void createInputs(String voidAdapterText) {
		getImportPanel().removeAll();
		inputs.clear();

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

		for (int i = 0; i < allowEdit.length; i++) {
			int line = USED_ROWS + i;
			
			GridBagConstraints labelGBC = new GridBagConstraints();
			labelGBC.anchor = GridBagConstraints.EAST;
			labelGBC.gridx = 0;
			labelGBC.gridy = line;
			labelGBC.insets = new Insets(0, 8, 0, 8);
			getImportPanel().add(new JLabel("" + line + ")"), labelGBC);
			
			NewNodeBranchDataInput input = new NewNodeBranchDataInput(getImportPanel(), 1, line, true);
			input.setAdapters(tree, false, true, true, false, true, voidAdapterText);
			input.setSelectedAdapter(adapters[i]);
			input.setEnabled(allowEdit[i]);
			inputs.add(input);
		}
		pack();
	}
	
	
	private void processPreselectedAdapters(NodeBranchDataAdapter defaultAdapter, String voidAdapterText) {
		if (defaultAdapter == null) {
			defaultAdapter = new VoidNodeBranchDataAdapter(voidAdapterText);
		}
		for (int i = 0; i < adapters.length; i++) {
			if (adapters[i] == null) {
				adapters[i] = defaultAdapter;
			}
		}
	}
	
	
	/**
	 * Opens a modal dialog that allows to edit the specified array of {@link NodeBranchDataAdapter}s.
	 * 
	 * @param tree the tree to work in
	 * @param adapters an array of preselected adapters (Element that are {@code null} will be set to {@code defaultAdapter}
	 * @param defaultAdapter the adapter to use as the default selection for elements in {@code adapters} that are {@code null}
	 *        (If {@code null} is specified, {@link VoidNodeBranchDataAdapter} will be assumed as the default.)
	 * @param allowEdit an array specifying for each selectable adapter whether it may be modified by the user or not
   * @param voidAdapterText Specify the text here, that shall be be displayed if no adapter is selected.
   *        Specify {@code null} if the user shall be forced to decide for one concrete adapter. If selecting no
   *        adapter is allowed, an instance of {@link VoidNodeBranchDataAdapter} will be returned in such cases.
   * @return {@code true} if the user did not cancel, {@code false} otherwise
   * @throws IllegalArgumentException if {@code adapters} and {@code allowEdit} have different lengths
	 */
	public boolean execute(Tree tree, NodeBranchDataAdapter[] adapters, NodeBranchDataAdapter defaultAdapter, boolean[] allowEdit, 
			String voidAdapterText) {
		
		if (adapters.length != allowEdit.length) {
			throw new IllegalArgumentException("Both specified array must have the same lengths.");
		}
		else {
			this.adapters = adapters;
			processPreselectedAdapters(defaultAdapter, voidAdapterText);
			this.allowEdit = allowEdit;
			this.tree = tree;
			createInputs(voidAdapterText);
			
			return execute();
		}
	}
	
	
	/**
	 * Tests if selected columns already exist as prompts the user to proceed.
	 */
	@Override
	protected boolean apply() {
		NodeBranchDataAdapter[] resultingAdapters = new NodeBranchDataAdapter[inputs.size()];
		Iterator<NewNodeBranchDataInput> iterator = inputs.iterator();
		for (int i = 0; i < resultingAdapters.length; i++) {
			resultingAdapters[i] = iterator.next().getSelectedAdapter();
		}
		
		boolean result = checkSelectedAdapters(resultingAdapters, allowEdit);
		if (result) {  // adapters array (that is visible outside of this class) shall only be modified if the inputs are correct.
			for (int i = 0; i < adapters.length; i++) {
				adapters[i] = resultingAdapters[i];  // The adapters array will be used by the calling class to determine the results.
			}
		}
		return result;
	}


	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
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
			jContentPane.add(getScrollPane(), null);
			jContentPane.add(getButtonsPanel(), null);
		}
		return jContentPane;
	}
}