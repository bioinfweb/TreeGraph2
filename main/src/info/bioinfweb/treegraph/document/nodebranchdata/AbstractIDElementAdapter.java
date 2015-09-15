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
package info.bioinfweb.treegraph.document.nodebranchdata;


import java.text.DecimalFormat;



/**
 * Node data adapters that need to store an ID can be inherited from this abstract class.
 * @author Ben St&ouml;ver
 */
public abstract class AbstractIDElementAdapter extends AbstractTextElementDataAdapter 
    implements IDElementAdapter {
	
	protected String id = "";
	
	
	public AbstractIDElementAdapter(String id) {
		this(id, DEFAULT_DECIMAL_FORMAT);
	}
	
	
	public AbstractIDElementAdapter(String id, DecimalFormat defaultDecimalFormat) {
		super(defaultDecimalFormat);
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


	/**
	 * Returns <code>true</code> if the specified object is an instance of the same class and has the same
	 * ID.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object other) {
		return super.equals(other) && ((AbstractIDElementAdapter)other).getID().equals(getID());
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
}