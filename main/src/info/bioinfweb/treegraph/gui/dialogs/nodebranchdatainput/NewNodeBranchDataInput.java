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
package info.bioinfweb.treegraph.gui.dialogs.nodebranchdatainput;


import info.bioinfweb.treegraph.document.nodebranchdata.NewNodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;



/**
 * Allows the user to specify a node/branch data column including new label or hidden node/branch data columns.
 * 
 * @author Ben St&ouml;ver
 */ 
public class NewNodeBranchDataInput extends NodeBranchDataInput {
	private JTextField textField = null;
	private List<NodeBranchDataInputListener> inputListeners = new ArrayList<NodeBranchDataInputListener>();
	
	
	public NewNodeBranchDataInput(JPanel panel, int x, int y, boolean horizontal) {
		super(panel, x, y);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		if (horizontal) {
			gbc.gridx = x + 1;
			gbc.gridy = y;
		}
		else {
			gbc.gridx = x;
			gbc.gridy = y + 1;
		}
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.weightx = 2.0;
    panel.add(getTextField(), gbc);
	}
	
	
	@Override
	public NodeBranchDataAdapter getSelectedAdapter() {
		NodeBranchDataAdapter result = super.getSelectedAdapter();
		if (result instanceof NewNodeBranchDataAdapter) {
			((NewNodeBranchDataAdapter)result).setID(getTextField().getText());
		}
		return result;
	}


	@Override
	public boolean setSelectedAdapter(NodeBranchDataAdapter adapter) {
		boolean result = super.setSelectedAdapter(adapter);
		if (!result && (adapter instanceof NewNodeBranchDataAdapter)) {  // In this case the IDs do not have to match.
			result = setSelectedAdapter(adapter.getClass());
			if (result) {
				setID(((NewNodeBranchDataAdapter)adapter).getID());
			}
		}
		return result;
	}


	@Override
	protected JComboBox<NodeBranchDataAdapter> getComboBox() {
		if (comboBox == null) {
			super.getComboBox().addItemListener(new ItemListener() {
		 		  public void itemStateChanged(ItemEvent e) {
			      if (e.getStateChange() == ItemEvent.SELECTED) {
			    	  NodeBranchDataAdapter adapter = (NodeBranchDataAdapter)e.getItem();
			  	  	boolean selected =	(adapter instanceof NewNodeBranchDataAdapter);
			  	  	getTextField().setEnabled(selected);
			  	  	fireNewIDSelected(selected);
			  	  }
			    }  		   
  	    });
		}
		return comboBox;
	}


	protected JTextField getTextField() {
		if (textField == null) {
			textField = new JTextField();
		}
		return textField;
	}


	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		getTextField().setEnabled(enabled);
	}
	
	
	/**
	 * Sets the ID displayed in the contained text field.
	 * @param id - the new ID
	 */
	public void setID(String id) {
		getTextField().setText(id);
	}


	public boolean addInputListener(NodeBranchDataInputListener listener) {
		return inputListeners.add(listener);
	}


	public boolean removeInputListener(NodeBranchDataInputListener listener) {
		return inputListeners.remove(listener);
	}
	
	
	private void fireNewIDSelected(boolean selected) {
		for (int i = 0; i < inputListeners.size(); i++) {
			inputListeners.get(i).newIDSelected(selected);
		}
	}
}