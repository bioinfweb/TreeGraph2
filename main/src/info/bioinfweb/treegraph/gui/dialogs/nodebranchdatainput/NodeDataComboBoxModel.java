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
package info.bioinfweb.treegraph.gui.dialogs.nodebranchdatainput;


import info.bioinfweb.treegraph.document.IDManager;
import info.bioinfweb.treegraph.document.TextLabel;
import info.bioinfweb.treegraph.document.Tree;
import info.bioinfweb.treegraph.document.nodebranchdata.*;

import java.util.Vector;
import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;



/**
 * Model for a combo box displaying node/branch data.
 * 
 * @author Ben St&ouml;ver
 */
public class NodeDataComboBoxModel extends AbstractListModel implements ComboBoxModel {
	private Vector<NodeBranchDataAdapter> adapters = new Vector<NodeBranchDataAdapter>();
	private NodeBranchDataAdapter selected = null;
	

	/**
   * Equivalent to a call of <code>setAdapters(tree, true, false, false)</code>.
   * @param tree
   * @see info.bioinfweb.treegraph.gui.dialogs.nodebranchdatainput.NodeDataComboBoxModel#setAdapters(Tree, boolean, boolean)
   */
  public void setAdapters(Tree tree) {
  	setAdapters(tree, true, true, false, false);
  }
  
  
  private void addNewAdapters() {
		adapters.add(new NewTextLabelAdapter());
		adapters.add(new NewHiddenBranchDataAdapter());
		adapters.add(new NewHiddenNodeDataAdapter());
  }
  
  
  public void setOnlyNewAdapters() {
  	adapters.clear();
  	addNewAdapters();
  }
  
  
  /**
   * @param tree - the tree to obtain the IDs from (Can also be <code>null</code>.)
   * @param nodeNamesSelectable - determines whether the node names adapter can be selected
   * @param branchLengthSelectable - determines whether the branch length adapter can be
   *        selected
   * @param decimalOnly - only adapters for node/branch data columns that contain at least one decimal
   *        element are included (This flag overwrites <code>nodeNamesSelectable</code> and
   *        <code>branchLengthSelectable</code>.)
   * @param newIDSelectable - If true an adaptor for a new user defined label ID is 
   *        added. Note that the label ID has still to be set. This adapters are also added if 
   *        <code>decimalOnly</code> is <code>true</code>. 
   */
  public void setAdapters(Tree tree, boolean nodeNamesSelectable, boolean branchLengthSelectable, 
  		boolean decimalOnly, boolean newIDSelectable) {
  	
  	adapters.clear();
  	fireIntervalRemoved(this, 0, 0);
  	
  	if (nodeNamesSelectable) {
  		adapters.add(new NodeNameAdapter());
  	}
		if (branchLengthSelectable) {
			adapters.add(new BranchLengthAdapter());
		}
		// More adapters can be added here.
		
		if (tree != null) {
			String[] ids = IDManager.getLabelIDs(tree.getPaintStart(), TextLabel.class);
			for (int i = 0; i < ids.length; i++) {
				adapters.add(new TextLabelAdapter(ids[i], 
						((TextLabel)IDManager.getFirstLabel(tree.getPaintStart(), TextLabel.class, ids[i])).getFormats().getDecimalFormat()));
			}
			
			ids = IDManager.getHiddenBranchDataIDs(tree.getPaintStart());
			for (int i = 0; i < ids.length; i++) {
				adapters.add(new HiddenBranchDataAdapter(ids[i]));
			}
			
			ids = IDManager.getHiddenNodeDataIDs(tree.getPaintStart());
			for (int i = 0; i < ids.length; i++) {
				adapters.add(new HiddenNodeDataAdapter(ids[i]));
			}
		}
		
		// Delete all adapters that can access not decimal value
		if (decimalOnly && (tree != null)) {
			for (int i = getSize() - 1; i >= 0; i--) {
				if (!tree.containsDecimal(adapters.get(i))) {
					adapters.remove(i);
				}
			}
		}
		
		if (newIDSelectable) {  // New adapters can all be numeric
	  	addNewAdapters();
		}
	
		if (getSize() > 0) {
			fireIntervalAdded(this, 0, adapters.size() - 1);
			setSelectedItem(adapters.get(0));
		}
  }
  
  
	/**
	 * Selects the adapter which is an instance (not instance of a subclass) of the 
	 * given class.
	 * @param adapterClass
	 * @return <code>true</code>, if one adapter was selected, <code>false</code>, if 
	 *         no adapter of the given class was found
	 */
	public boolean setSelectedAdapter(Class<? extends NodeBranchDataAdapter> adapterClass) {
		for (int i = 0; i < adapters.size(); i++) {
			if (adapters.get(i).getClass().getName().equals(adapterClass.getName())) {
				setSelectedItem(adapters.get(i));
				return true;
			}
		}
		return false;
	}
	
	
	/**
	 * Selects the adapter which is an instance (not instance of a subclass) of same class as the 
	 * given adapter and has the same ID (if it is an adapter for ID elements).
	 * @param adapter
	 * @return <code>true</code>, if one adapter was selected, <code>false</code>, if 
	 *         no adapter of the given class (with the given ID) was found
	 */
	public boolean setSelectedAdapter(NodeBranchDataAdapter adapter) {
		for (int i = 0; i < adapters.size(); i++) {
			if (adapters.get(i).equals(adapter)) {
				setSelectedItem(adapters.get(i));
				return true;
			}
		}
		return false;
	}
  
  
  public NodeBranchDataAdapter getSelectedItem() {
		return selected;
	}

	
	public void setSelectedItem(Object item) {
		if (item == null) {
			selected = null;
		}
		else if ((item instanceof NodeBranchDataAdapter) && adapters.contains(item)) {
			selected = (NodeBranchDataAdapter)item;
		}
		else {
			return;  // no contentsChanged event!
		}
		fireContentsChanged(this, 0, adapters.size() - 1);  //TODO Interval verkleinern?
	}

	
	public NodeBranchDataAdapter getElementAt(int pos) {
		return adapters.get(pos);
	}

	
	public int getSize() {
		return adapters.size();
	}
}