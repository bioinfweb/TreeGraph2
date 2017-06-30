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
package info.bioinfweb.treegraph.document.metadata;


import javax.xml.namespace.QName;



public abstract class MetadataNode implements Cloneable {
	private MetadataNode parent = null;
	private QName predicateOrRel;	
	
	
	public MetadataNode() {
		super();
	}
			

	public MetadataNode(QName predicateOrRel) {
		super();
		this.predicateOrRel = predicateOrRel;
	}




	public MetadataNode getParent() {
		return parent;
	}
	
	
	public void setParent(MetadataNode parent) {
		this.parent = parent;
	}

	
	public boolean hasParent() {
		if (parent != null) {
			return true;
		} return false;
	}
	

	public QName getPredicateOrRel() {
		return predicateOrRel;
	}


	public void setPredicateOrRel(QName predicateOrRel) {
		this.predicateOrRel = predicateOrRel;
	}


	@Override
	public MetadataNode clone() {
		try {
			MetadataNode result = (MetadataNode) super.clone();
			result.setParent(null);
			return result;
		}
		catch (CloneNotSupportedException e) {
			throw new InternalError(e);
		}
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((predicateOrRel == null) ? 0 : predicateOrRel.hashCode());
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
		MetadataNode other = (MetadataNode) obj;
		if (predicateOrRel == null) {
			if (other.predicateOrRel != null)
				return false;
		} else if (!predicateOrRel.equals(other.predicateOrRel))
			return false;
		return true;
	}
}
