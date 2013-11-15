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
package info.bioinfweb.treegraph.document.nodebranchdata;

import info.bioinfweb.treegraph.document.ConcretePaintableElement;
import info.bioinfweb.treegraph.document.Node;



/**
 * Node data adapters that need to store an ID can be inherited from this abstract class.
 * @author Ben St&ouml;ver
 */
public abstract class AbstractIDElementAdapter extends TextElementDataAdapter 
    implements IDElementAdapter {
	
	protected String id = "";
	
	
	public AbstractIDElementAdapter(String id) {
		super();
		this.id = id;
	}


	public boolean readOnly() {
		return false;
	}


	public boolean isNewColumn() {
		return false;
	}


	/* (non-Javadoc)
	 * @see info.bioinfweb.treegraph.document.nodedata.IDElementAdapter#getID()
	 */
	public String getID() {
		return id;
	}


	@Override
	public void setID(String id) {
		this.id = id;
	}


	@Override
	public ConcretePaintableElement getDataElement(Node node) {
		// TODO Auto-generated method stub
		return null;
	}


	/**
	 * Returns <code>true</code> if the specified object is an instance of the same class and has the same
	 * ID.
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object other) {
		return super.equals(other) && ((AbstractIDElementAdapter)other).getID().equals(getID());
	}
}