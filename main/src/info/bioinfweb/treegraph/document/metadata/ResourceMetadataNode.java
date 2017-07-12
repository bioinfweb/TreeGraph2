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


import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.xml.namespace.QName;

import info.bioinfweb.jphyloio.events.meta.ResourceMetadataEvent;
import info.bioinfweb.jphyloio.events.meta.URIOrStringIdentifier;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.TextElementData;



public class ResourceMetadataNode extends MetadataNode {
	private List<MetadataNode> children = new ArrayList<MetadataNode>();
	private URI href;
	
	
	public ResourceMetadataNode() {  //TODO Which properties must be set?
		super();
	}


	public ResourceMetadataNode(QName predicateOrRel) {
		super(predicateOrRel);
	}


	public URI getURI() {
		return href;
	}
	
	
	public void setURI(URI uri) {
		if (uri != null) {
			this.href = uri;			
		}
	}
	
	
	public List<MetadataNode> getChildren() {
		return children;
	}
	
	
	@Override
	public boolean isLeaf() {
		if (!getChildren().isEmpty()) {
			return false;
		}
		else {
			return true;
		}
	}
	
	
	public void clear() {
		href = null;
		children.clear();
	}
	
	
	@Override
	public ResourceMetadataNode clone() {
		ResourceMetadataNode result = (ResourceMetadataNode)super.clone();
		result.children = new ArrayList<MetadataNode>();
		for (MetadataNode child : getChildren()) {
			MetadataNode childClone = child.clone();
			childClone.setParent(result);
			result.getChildren().add(childClone);
		}
		return result;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((href == null) ? 0 : href.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ResourceMetadataNode other = (ResourceMetadataNode) obj;
		if (href == null) {
			if (other.href != null)
				return false;
		} else if (!href.equals(other.href))
			return false;
		return true;
	}
}
