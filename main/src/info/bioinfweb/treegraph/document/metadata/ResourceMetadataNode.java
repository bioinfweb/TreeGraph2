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
	private URI uri;
	
	
	public ResourceMetadataNode(URI uri) {  //TODO Which properties must be set?
		super();
		this.uri = uri;
	}


	public URI getURI() {
		return uri;
	}
	
	
	public void setURI(URI uri) {
		this.uri = uri;
	}
	
	
	public List<MetadataNode> getChildren() {
		return children;
	}
	
	
	public void clear() {
		uri = null;
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
	

//	public static void main(String[] args) {
//		ResourceMetadataNode node = new ResourceMetadataNode(null);
//		node.getChildren().add(e);
//	}
	
	
	//TODO Also model resource metadata
	//     - Wenn value schon vom Typ Object wäre, könnte dort die URI gespeichert werden.
	//     - rel könnte in predicate gespeichert werden
	//     - Ein Datentyp ist bei Resource Metadata nicht vorhanden.
}
