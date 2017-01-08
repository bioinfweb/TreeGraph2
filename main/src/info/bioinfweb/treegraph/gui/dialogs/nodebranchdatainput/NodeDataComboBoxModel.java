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
package info.bioinfweb.treegraph.gui.dialogs.nodebranchdatainput;


import info.bioinfweb.treegraph.document.TextLabel;
import info.bioinfweb.treegraph.document.Tree;
import info.bioinfweb.treegraph.document.nodebranchdata.*;
import info.bioinfweb.treegraph.document.tools.IDManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;



/**
 * Model for a combo box displaying node/branch data.
 * 
 * @author Ben St&ouml;ver
 */
public class NodeDataComboBoxModel extends AbstractListModel<NodeBranchDataAdapter> 
    implements ComboBoxModel<NodeBranchDataAdapter> {
	
	private List<NodeBranchDataAdapter> adapters = new ArrayList<NodeBranchDataAdapter>();
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
  
  
  private void countIDs(String id, Map<String, Integer> idMap) {
  	if (idMap.get(id) != null) {
  		idMap.put(id, idMap.get(id) + 1);
		}
		else {
			idMap.put(id, 1);
		}
  }
  
  
  /**
   * Refreshes the selectable node data adapters.
   * 
   * @param tree the tree to obtain the IDs from (If <code>null</code> no existing IDAdapters can be selected.)
   * @param uniqueNamesSelectable determines whether the unique node names adapter can be selected
   * @param nodeNamesSelectable determines whether the node names adapter can be selected
   * @param branchLengthSelectable determines whether the branch length adapter can be
   *        selected
   * @param decimalOnly only adapters for node/branch data columns that contain at least one decimal
   *        element are included (This flag overwrites <code>nodeNamesSelectable</code> and
   *        <code>branchLengthSelectable</code>.)
   * @param newIDSelectable If true an adaptor for a new user defined label ID is 
   *        added. Note that the label ID has still to be set. These adapters are also added if 
   *        <code>decimalOnly</code> is <code>true</code>.
   * @param voidAdapterText the name to be displayed for instances of {@link VoidNodeBranchDataAdapter}
   */
  public void setAdapters(Tree tree, boolean uniqueNamesSelectable, boolean nodeNamesSelectable, 
  		boolean branchLengthSelectable,	boolean decimalOnly, boolean newIDSelectable, String voidAdapterText) {

  	clear();
  	
  	if ((voidAdapterText != null) && !voidAdapterText.equals("")) {
			adapters.add(new VoidNodeBranchDataAdapter(voidAdapterText));
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
			String[] labelIDs = IDManager.getLabelIDs(tree.getPaintStart(), TextLabel.class);
			String[] hiddenBranchDataIDs = IDManager.getHiddenBranchDataIDs(tree.getPaintStart());
			String[] hiddenNodeDataIDs = IDManager.getHiddenNodeDataIDs(tree.getPaintStart());
			Map<String, Integer> idDuplication = new TreeMap<String, Integer>();
			
			for (int i = 0; i < labelIDs.length; i++) {
				countIDs(labelIDs[i], idDuplication);
			}			
			for (int i = 0; i < hiddenBranchDataIDs.length; i++) {
				countIDs(hiddenBranchDataIDs[i], idDuplication);
			}			
			for (int i = 0; i < hiddenNodeDataIDs.length; i++) {
				countIDs(hiddenNodeDataIDs[i], idDuplication);
			}
			
			for (String key : idDuplication.keySet()) {
				if (idDuplication.get(key) > 1) {
					adapters.add(new GeneralIDAdapter(key));
				}
			}
			
			for (int i = 0; i < labelIDs.length; i++) {				
				adapters.add(new TextLabelAdapter(labelIDs[i],((TextLabel)IDManager.getFirstLabel(tree.getPaintStart(), TextLabel.class, labelIDs[i])).getFormats().getDecimalFormat()));
			}			
			for (int i = 0; i < hiddenBranchDataIDs.length; i++) {
				adapters.add(new HiddenBranchDataAdapter(hiddenBranchDataIDs[i]));				
			}			
			for (int i = 0; i < hiddenNodeDataIDs.length; i++) {				
				adapters.add(new HiddenNodeDataAdapter(hiddenNodeDataIDs[i]));							
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
	 * is only for special tasks and in general {@link #setAdapters(Tree, boolean, boolean, boolean, boolean, boolean, String)}
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
	 * is only for special tasks and in general {@link #setAdapters(Tree, boolean, boolean, boolean, boolean, boolean, String)}
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


	public List<NodeBranchDataAdapter> getAdapters() {
		return Collections.unmodifiableList(adapters);  //TODO Would it be a problem, if the result would be modifiable?
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
	 * <p>
	 * The selected adapter will be equal to the specified adapter, but it is not necessarily the
	 * same instance.
	 * <p>
	 * This is especially relevant for instances of {@link VoidNodeBranchDataAdapter}.
	 * Each instance of this class will use its own instance of {@link VoidNodeBranchDataAdapter}
	 * internally, therefore the text displayed for such adapters will be equal to the text passed
	 * to the last call of {@link #setSelectedAdapter(NodeBranchDataAdapter)} of this instance and
	 * not to the text stored in the adapter passed to this method. This allows using different
	 * string representations of {@link VoidNodeBranchDataAdapter} in different combo boxes. 
	 * 
	 * @param adapter the adapter prototype for the adapter to be selected
	 * @return {@code true}, if one adapter was selected, {@code false}, if 
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
		fireContentsChanged(this, 0, adapters.size() - 1);  //TODO Intervall verkleinern?
	}

	
	public NodeBranchDataAdapter getElementAt(int pos) {
		return adapters.get(pos);
	}

	
	public int getSize() {
		return adapters.size();
	}
}