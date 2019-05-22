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
package info.bioinfweb.treegraph.gui.dialogs.nodebranchdata.defaultadapter;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;



/**
 * Combo box model containing the combined set of node/branch data adapters of all opened documents.
 * 
 * @author Ben St&ouml;ver
 */
public abstract class CombinedAdaptersComboBoxModel extends AbstractListModel<CombinedAdapterEntry> implements ComboBoxModel<CombinedAdapterEntry> {
	private List<CombinedAdapterEntry> entries = new ArrayList<CombinedAdapterEntry>();
	private CombinedAdapterEntry selected = null;
	
	
	public static class LeafAdapterModel extends CombinedAdaptersComboBoxModel {
		@Override
		protected List<NodeBranchDataAdapter> getAdapterList(DefaultDocumentAdaptersTableModel tableModel, int rowIndex) {
			return tableModel.getLeafComboBoxModel(rowIndex).getAdapters();
		}
	}
	
	
	public static class SupportAdapterModel extends CombinedAdaptersComboBoxModel {
		@Override
		protected List<NodeBranchDataAdapter> getAdapterList(DefaultDocumentAdaptersTableModel tableModel, int rowIndex) {
			return tableModel.getSupportComboBoxModel(rowIndex).getAdapters();
		}
	}
	
	
	protected abstract List<NodeBranchDataAdapter> getAdapterList(DefaultDocumentAdaptersTableModel tableModel, int rowIndex);
	
	
	public void refreshEntries(DefaultDocumentAdaptersTableModel tableModel) {
		// Count adapters:
		Map<NodeBranchDataAdapter, Integer> countMap = new HashMap<NodeBranchDataAdapter, Integer>();
		for (int rowIndex = 0; rowIndex < tableModel.getRowCount(); rowIndex++) {
			for (NodeBranchDataAdapter adapter : getAdapterList(tableModel, rowIndex)) {
				Integer count = countMap.get(adapter);
				if (count == null) {
					count = 1;
				}
				else {
					count++;
				}
				countMap.put(adapter, count);
			}
		}
		
		// Clear list:
		int size = entries.size();
		entries.clear();
		if (size > 0) {
			fireIntervalRemoved(this, 0, size - 1);
		}
		
		// Fill list:
		for (NodeBranchDataAdapter adapter : countMap.keySet()) {
			entries.add(new CombinedAdapterEntry(adapter, countMap.get(adapter)));
		}
		Collections.sort(entries);  // Sorts the list by count.
		fireIntervalAdded(this, 0, entries.size() - 1);
	}


	@Override
	public CombinedAdapterEntry getElementAt(int index) {
		return entries.get(index);
	}


	@Override
	public int getSize() {
		return entries.size();
	}


	@Override
	public Object getSelectedItem() {
		return selected;
	}
	
	
	public NodeBranchDataAdapter getSelectedAdapter() {
		return selected.getAdapter();
	}


	@Override
	public void setSelectedItem(Object selected) {
		if (selected instanceof CombinedAdapterEntry) {
			this.selected = (CombinedAdapterEntry)selected;
			fireContentsChanged(this, 0, getSize() - 1);  //TODO Does it really have to be done this way? Is there no selection change event?
		}
		else {
			throw new IllegalArgumentException("Only instances of CombinedAdapterEntry can be selected.");
		}
	}
}
