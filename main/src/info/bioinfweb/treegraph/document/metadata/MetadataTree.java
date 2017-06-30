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


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import info.bioinfweb.treegraph.document.HiddenDataElement;



public class MetadataTree implements Cloneable {
	private HiddenDataElement parent;
	private List<MetadataNode> children = new ArrayList<MetadataNode>();
	
	
	public MetadataTree(HiddenDataElement parent) {
		this.parent = parent;
	}
	
	
	public HiddenDataElement getParent() {
		return parent;
	}
	
	
	public void setParent(HiddenDataElement parent) {
		this.parent = parent;
	}
	
	
	public List<MetadataNode> getChildren() {
		return children;
	}
	
	
	public MetadataNode searchNodeByPath(MetadataPath path, boolean createNodes) {
		if (!path.getElementList().isEmpty()) {
			Iterator<MetadataPathElement> iterator = path.getElementList().iterator();
			MetadataNode node = searchNodeInList(getChildren(), iterator.next(), iterator.hasNext() ? false : path.isLiteral(), createNodes);
			while (iterator.hasNext() && (node instanceof ResourceMetadataNode)) {
				node = searchNodeInList(((ResourceMetadataNode)node).getChildren(), iterator.next(), iterator.hasNext() ? false : path.isLiteral(), createNodes);
			}
			if (!iterator.hasNext()) {				
				return node;
			}
		}
		return null;
	}
	
	
	private MetadataNode searchNodeInList(List<MetadataNode> list, MetadataPathElement element, boolean searchLiteral, boolean createNodes) {
		int index = 0;
		for (MetadataNode child : list) {
			if ((((child instanceof ResourceMetadataNode) && !searchLiteral) || ((child instanceof LiteralMetadataNode) && searchLiteral)) && 
						(child.getPredicateOrRel().equals(element.getPredicateOrRel()))) {
				
				if (index == element.getIndex()) {
					return child;
				}
				else {
					index++;
				}
			}
		}

		if (createNodes) {  // Add more nodes to this level, if necessary and requested.
			while (index <= element.getIndex()) {
				if (searchLiteral) {
					list.add(new LiteralMetadataNode(element.getPredicateOrRel()));
				}
				else {
					list.add(new ResourceMetadataNode(element.getPredicateOrRel()));
				}
				index++;
			}
			return list.get(list.size() -1);
		}
		else {
			return null;
		}
	}


	@Override
	public MetadataTree clone() {
		try {
			MetadataTree result = (MetadataTree)super.clone();
			result.setParent(null);
			result.children = new ArrayList<MetadataNode>();
			for (MetadataNode child : getChildren()) {
				MetadataNode childClone = child.clone();
				result.getChildren().add(childClone);
			}
			return result;
		}
		catch (CloneNotSupportedException e) {
			throw new InternalError(e);
		}
	}
}
