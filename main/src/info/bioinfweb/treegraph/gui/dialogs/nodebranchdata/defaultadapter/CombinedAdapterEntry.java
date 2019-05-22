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


import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;



/**
 * Entry class for a combo box model used in {@link DefaultDocumentAdapterDialog} to select from the combined set of 
 * node/branch data adapters of all documents. It will display the number of documents containing each adapter next
 * to its string representation.
 *  
 * @author Ben St&ouml;ver
 * @since 2.16.0
 */
public class CombinedAdapterEntry implements Comparable<CombinedAdapterEntry> {
	private NodeBranchDataAdapter adapter;
	private int count;
	
	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param adapter the node/branch data adapter contained in one or more documents
	 * @param count the number of documents containing this adapter
	 */
	public CombinedAdapterEntry(NodeBranchDataAdapter adapter, int count) {
		super();
		if (adapter == null) {
			throw new IllegalArgumentException("adapter must not be null.");
		}
		else if (count < 0) {
			throw new IllegalArgumentException("count must not be ´below zero.");
		}
		else {
			this.adapter = adapter;
			this.count = count;
		}
	}
	
	
	/**
	 * Returns the node/branch data adapter represented by this entry.
	 * 
	 * @return in implementation of {@link NodeBranchDataAdapter}, never {@code null}
	 */
	public NodeBranchDataAdapter getAdapter() {
		return adapter;
	}

	
	/**
	 * Returns the number of documents containing this adapter.
	 * 
	 * @return an integer equal or below zero
	 */
	public int getCount() {
		return count;
	}


	@Override
	public int compareTo(CombinedAdapterEntry other) {
		return other.getCount() - getCount();  // Higher numbers should be in front of a list.  
		//TODO Also sort by adapter type for equal counts?
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((adapter == null) ? 0 : adapter.hashCode());
		result = prime * result + count;
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CombinedAdapterEntry other = (CombinedAdapterEntry) obj;
		if (adapter == null) {
			if (other.adapter != null)
				return false;
		} else if (!adapter.equals(other.adapter))
			return false;
		if (count != other.count)
			return false;
		return true;
	}


	@Override
	public String toString() {
		return adapter.toString() + " (Appears in " + count + " documents.)";
	}
}
