/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2015  Ben Stöver, Sarah Wiechers, Kai Müller
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
package info.bioinfweb.treegraph.gui.dialogs.nodebranchdatainput;


import java.awt.GridBagConstraints;
import java.awt.Insets;

import info.bioinfweb.treegraph.document.Tree;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;

import javax.swing.JComboBox;
import javax.swing.JPanel;



/**
 * A GUI class used to let the user specify a node/branch data column. 
 * 
 * @author Ben St&ouml;ver
 */
public class NodeBranchDataInput {
	protected JComboBox<NodeBranchDataAdapter> comboBox = null;

	
	public NodeBranchDataInput(JPanel panel, int x, int y) {
		super();

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.weightx = 1.0;
    panel.add(getComboBox(), gbc);
	}
	
	
	public NodeDataComboBoxModel getModel() {
		return (NodeDataComboBoxModel)getComboBox().getModel();
	}


	protected JComboBox<NodeBranchDataAdapter> getComboBox() {
		if (comboBox == null) {
			comboBox = new JComboBox<NodeBranchDataAdapter>(new NodeDataComboBoxModel());
		}
		return comboBox;
	}
	
 
	public void setEnabled(boolean enabled) {
		getComboBox().setEnabled(enabled);
	}
	
	
  /**
   * After the call of this method only adapters which create new data will be 
   * available.
   */
  public void setOnlyNewAdapters() {
  	((NodeDataComboBoxModel)getComboBox().getModel()).setOnlyNewAdapters();
  }
  
  
  /**
   * Refreshes the selectable node data adapters.
   * 
   * @param tree - the tree which contains the data
   */
  public void setAdapters(Tree tree) {
  	((NodeDataComboBoxModel)getComboBox().getModel()).setAdapters(tree);
  }
  
  
  /**
   * Refreshes the selectable node data adapters.
   * 
   * @param tree - the tree to obtain the IDs from (can also be <code>null</code>)
   * @param uniqueNamesSelectable Determines whether unique node names can be selected. 
   * @param nodeNamesSelectable Determines whether the node names adapter can be selected.
   * @param branchLengthSelectable Determines whether the branch length adapter can be
   *        selected.
   * @param decimalOnly only adapters for node/branch data columns that contain at least one decimal
   *        element are included (This flag overwrites <code>nodeNamesSelectable</code> and
   *        <code>branchLengthSelectable</code>.)
   * @param newIDSelectable If true an adaptor for a new user defined label ID is 
   *        added. Note that the label ID has still to be set. This adapters are also added if 
   *        <code>decimalOnly</code> is <code>true</code>. 
   * @param noImportAdapterSelectable TODO
   */
  public void setAdapters(Tree tree, boolean uniqueNamesSelectable, boolean nodeNamesSelectable, 
  		boolean branchLengthSelectable,	boolean decimalOnly, boolean newIDSelectable, String noImportAdapterSelectable) {
  	
  	((NodeDataComboBoxModel)getComboBox().getModel()).setAdapters(tree, uniqueNamesSelectable,
  			nodeNamesSelectable, branchLengthSelectable, decimalOnly, newIDSelectable, noImportAdapterSelectable);
  }
  
  
	/**
	 * Selects the adapter which is an instance (not instance of a subclass) of the 
	 * given class.
	 * 
	 * @param adapterClass
	 * @return <code>true</code>, if one adapter was selected, <code>false</code>, if 
	 *         no adapter of the given class was found
	 */
	public boolean setSelectedAdapter(Class<? extends NodeBranchDataAdapter> adapterClass) {
		return ((NodeDataComboBoxModel)getComboBox().getModel()).setSelectedAdapter(
				adapterClass);
	}
	
	
	/**
	 * Selects the adapter which is an instance (not instance of a subclass) of same class as the 
	 * given adapter and has the same ID (if it is an adapter for ID elements).
	 * 
	 * @param adapter
	 * @return <code>true</code>, if one adapter was selected, <code>false</code>, if 
	 *         no adapter of the given class (with the given ID) was found
	 */
	public boolean setSelectedAdapter(NodeBranchDataAdapter adapter) {
		return ((NodeDataComboBoxModel)getComboBox().getModel()).setSelectedAdapter(adapter);
	}
	
	
  /**
   * Returns the currently selected adapter.
   * 
   * @return the selected adapter (not a copy of it)
   */
  public NodeBranchDataAdapter getSelectedAdapter() {
  	return ((NodeDataComboBoxModel)getComboBox().getModel()).getSelectedItem();
  }
}