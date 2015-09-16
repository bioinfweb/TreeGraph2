/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2015  Ben St�ver, Kai M�ller
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

import java.util.Collection;
import java.util.Vector;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;



/**
 * Model for a combo box displaying node/branch data.
 * 
 * @author Ben St&ouml;ver
 */
public class NodeDataComboBoxModel extends AbstractListModel<NodeBranchDataAdapter> 
    implements ComboBoxModel<NodeBranchDataAdapter> {
	
	private Vector<NodeBranchDataAdapter> adapters = new Vector<NodeBranchDataAdapter>();
	private NodeBranchDataAdapter selected = null;
	

	/**
   * Equivalent to a call of <code>setAdapters(tree, false, true, false, false)</code>.
   * 
   * @param tree
   * @see info.bioinfweb.treegraph.gui.dialogs.nodebranchdatainput.NodeDataComboBoxModel#setAdapters(Tree, boolean, boolean)
   */
  public void setAdapters(Tree tree) {
  	setAdapters(tree, false, true, true, false, false, "");
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
   * Refreshes the selectable node data adapters.
   * 
   * @param tree the tree to obtain the IDs from (Can also be <code>null</code>.)
   * @param uniqueNamesSelectable determines whether the unique node names adapter can be selected
   * @param nodeNamesSelectable determines whether the node names adapter can be selected
   * @param branchLengthSelectable determines whether the branch length adapter can be
   *        selected
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

  	clear();
  	
  	if ((noImportAdapterSelectable != null) && !noImportAdapterSelectable.equals("")) {
			adapters.add(new VoidNodeBranchDataAdapter(noImportAdapterSelectable));
		}
  	
  	if (uniqueNamesSelectable) {
  		adapters.add(UniqueNameAdapter.getSharedInstance());
  	}
  	if (nodeNamesSelectable) {
  		adapters.add(NodeNameAdapter.getSharedInstance());
  	}
		if (branchLengthSelectable) {
			adapters.add(BranchLengthAdapter.getSharedInstance());
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
		
		// Delete all adapters for columns that contain no decimal value
		if (decimalOnly && (tree != null)) {
			for (int i = getSize() - 1; i >= 0; i--) {
				if (!tree.containsDecimal(adapters.get(i)) && !(adapters.get(i) instanceof VoidNodeBranchDataAdapter)) {
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
	 * Inserts the specified node/branch data adapter instance at the specified position. Note that this method
	 * is only for special tasks an in general {@link #setAdapters(Tree, boolean, boolean, boolean, boolean, boolean, String)}
	 * should be used.
	 * 
	 * @param index - the index of the new adapter
	 * @param adapter - the new adapter instance.
	 * 
	 * @see #setAdapters(Tree)
	 * @see #setAdapters(Tree, boolean, boolean, boolean, boolean, boolean, String)
	 * @see #setOnlyNewAdapters()
	 * @see #addAdapter(NodeBranchDataAdapter)
	 */
	public void addAdapter(int index, NodeBranchDataAdapter adapter) {
	  adapters.add(index, adapter);
	  fireIntervalAdded(this, index, index);
  }


	/**
	 * Appends the specified node/branch data adapter instance at the end of the list. Note that this method
	 * is only for special tasks an in general {@link #setAdapters(Tree, boolean, boolean, boolean, boolean, boolean, String)}
	 * should be used.
	 * 
	 * @param index - the index of the new adapter
	 * @param adapter - the new adapter instance.
	 * @return {@code true} (as specified by {@link Collection#add(Object)})
	 * 
	 * @see #setAdapters(Tree)
	 * @see #setAdapters(Tree, boolean, boolean, boolean, boolean, boolean, String)
	 * @see #setOnlyNewAdapters()
	 * @see #addAdapter(int, NodeBranchDataAdapter)
	 */
	public boolean addAdapter(NodeBranchDataAdapter adapter) {
		boolean result = adapters.add(adapter);
		fireIntervalAdded(this, getSize() - 1, getSize() - 1);
		return result;
  }


	public void clear() {
  	adapters.clear();
  	fireIntervalRemoved(this, 0, 0);
  }


	/**
	 * Selects the adapter which is an instance (not instance of a subclass) of the 
	 * given class.
	 * 
	 * @param adapterClass - the class used to identify the adapter to be selected
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