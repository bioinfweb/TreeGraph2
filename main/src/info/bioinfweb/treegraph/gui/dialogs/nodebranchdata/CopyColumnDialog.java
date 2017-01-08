/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2011, 2013-2017  Ben Stöver, Sarah Wiechers, Kai Müller
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
import info.bioinfweb.treegraph.document.nodebranchdata.NewTextLabelAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.NewNodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.tools.IDManager;
import info.bioinfweb.treegraph.document.undo.edit.CopyColumnEdit;
import info.bioinfweb.treegraph.gui.dialogs.EditDialog;
import info.bioinfweb.treegraph.gui.dialogs.nodebranchdatainput.NewNodeBranchDataInput;
import info.bioinfweb.treegraph.gui.dialogs.nodebranchdatainput.NodeBranchDataInput;
import info.bioinfweb.wikihelp.client.WikiHelpOptionPane;

import javax.swing.JPanel;

import java.awt.Frame;
import java.awt.GridBagLayout;

import javax.swing.JLabel;

import java.awt.GridBagConstraints;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;



public class CopyColumnDialog extends EditDialog {
	private static final long serialVersionUID = 1L;
	
	
	private JPanel jContentPane = null;
	private JPanel copyPanel = null;
	private JLabel srcLabel = null;
	private NodeBranchDataInput srcInput = null;
	private JLabel destLabel = null;
	private JLabel labelIDLabel = null;
	private NewNodeBranchDataInput destInput = null;
	private JCheckBox includeLeavesCheckBox = null;
	

	/**
	 * @param owner
	 */
	public CopyColumnDialog(Frame owner) {
		super(owner);
		setHelpCode(19);
		initialize();
		setLocationRelativeTo(owner);
	}
	
	
	@Override
	protected boolean onExecute() {
		getSrcInput().setAdapters(getDocument().getTree(), true, true, true, false, false, "");
		if (getSelectedAdapter() != null) {
			getSrcInput().setSelectedAdapter(getSelectedAdapter().getClass());
		}
		getDestInput().setAdapters(getDocument().getTree(), false, true, true, false, true, "");
		getDestInput().setSelectedAdapter(NewTextLabelAdapter.class);
		
		pack();  // An L�nge der Namen anpassen.
		return true;
	}


	@Override
	protected boolean apply() {
		boolean result = !getDestAdapter().isNewColumn() || 
		    !IDManager.idExistsInSubtree(getDocument().getTree().getPaintStart(), 
				    ((NewNodeBranchDataAdapter)getDestAdapter()).getID());
		if (result) {
			getDocument().executeEdit(new CopyColumnEdit(getDocument(), getSrcAdapter(), getDestAdapter(), 
					getIncludeLeaves()));
		}
		else {
			WikiHelpOptionPane.showMessageDialog(this,	"There are already elements " +
					"with the ID \"" + ((NewNodeBranchDataAdapter)getDestAdapter()).getID() + "\" present.\n" +
					"Please choose another ID.",	"Invalid ID",	JOptionPane.ERROR_MESSAGE, 
					Main.getInstance().getWikiHelp(), 30);
		}
		return result;
	}
	
	
	public NodeBranchDataAdapter getSrcAdapter() {
		return getSrcInput().getSelectedAdapter();
	}


	public NodeBranchDataAdapter getDestAdapter() {
		return getDestInput().getSelectedAdapter();
	}


	public boolean getIncludeLeaves() {
		return getIncludeLeavesCheckBox().isSelected();
	}


	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setTitle("Copy node/branch data");
		this.setContentPane(getJContentPane());
		getApplyButton().setVisible(false);
		this.pack();
	}


	public NodeBranchDataInput getSrcInput() {
		getCopyPanel();  // ggf. erstellen
		return srcInput;
	}


	public NewNodeBranchDataInput getDestInput() {
		getCopyPanel();  // ggf. erstellen
		return destInput;
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
			jContentPane.add(getCopyPanel(), null);
			jContentPane.add(getButtonsPanel(), null);
		}
		return jContentPane;
	}


	/**
	 * This method initializes copyPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getCopyPanel() {
		if (copyPanel == null) {
			GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
			gridBagConstraints12.gridx = 0;
			gridBagConstraints12.gridwidth = 2;
			gridBagConstraints12.anchor = GridBagConstraints.WEST;
			gridBagConstraints12.gridy = 3;
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 0;
			gridBagConstraints3.anchor = GridBagConstraints.WEST;
			gridBagConstraints3.gridy = 2;
			labelIDLabel = new JLabel();
			labelIDLabel.setText("New ID: ");
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.anchor = GridBagConstraints.WEST;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.anchor = GridBagConstraints.WEST;
			gridBagConstraints1.gridy = 1;
			destLabel = new JLabel();
			destLabel.setText("Destination: ");
			srcLabel = new JLabel();
			srcLabel.setText("Source: ");
			copyPanel = new JPanel();
			copyPanel.setLayout(new GridBagLayout());
			copyPanel.add(srcLabel, gridBagConstraints11);
			srcInput = new NodeBranchDataInput(copyPanel, 1, 0);
			copyPanel.add(destLabel, gridBagConstraints1);
			copyPanel.add(labelIDLabel, gridBagConstraints3);
			destInput = new NewNodeBranchDataInput(copyPanel, 1, 1, false);
			copyPanel.add(getIncludeLeavesCheckBox(), gridBagConstraints12);
		}
		return copyPanel;
	}


	/**
	 * This method initializes includeLeavesCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getIncludeLeavesCheckBox() {
		if (includeLeavesCheckBox == null) {
			includeLeavesCheckBox = new JCheckBox();
			includeLeavesCheckBox.setText("Include data from leaves");
		}
		return includeLeavesCheckBox;
	}

}