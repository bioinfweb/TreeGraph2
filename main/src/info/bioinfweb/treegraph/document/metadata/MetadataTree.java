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
	private MetadataNodeList children = new MetadataNodeList();
	
	
	public MetadataTree(HiddenDataElement parent) {
		this.parent = parent;
	}
	
	
	public HiddenDataElement getParent() {
		return parent;
	}
	
	
	public void setParent(HiddenDataElement parent) {
		this.parent = parent;
	}
	
	
	public MetadataNodeList getChildren() {
		return children;
	}
	
	
	public boolean isEmpty() {
		return (getChildren().size() == 0);
	}
	
	
	public MetadataNode searchAndCreateNodeByPath(MetadataPath path, boolean createNodes) {
		if (!path.getElementList().isEmpty()) {
			Iterator<MetadataPathElement> iterator = path.getElementList().iterator();
			MetadataNode node = searchAndCreateNodeInList(getChildren(), iterator.next(), iterator.hasNext() ? false : path.isLiteral(), createNodes);
			while (iterator.hasNext() && (node instanceof ResourceMetadataNode)) {
				node = searchAndCreateNodeInList(((ResourceMetadataNode)node).getChildren(), iterator.next(), iterator.hasNext() ? false : path.isLiteral(), createNodes);
			}
			if (!iterator.hasNext()) {				
				return node;
			}
		}
		return null;
	}
	
	
	private MetadataNode searchAndCreateNodeInList(List<MetadataNode> list, MetadataPathElement element, boolean searchLiteral, boolean createNodes) {
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


	public List<Integer> determineSubtreeDepths() {
		List<Integer> result = new ArrayList<Integer>();
		determineSubtreeDepths(getChildren(), result);
		return result;
	}
	
		
	public int determineSubtreeDepths(List<MetadataNode> children, List<Integer> depths) {
		int maxDepth = 0;
		int rootIndex = depths.size();
		depths.add(-1);  // Add placeholder for this level.
		
		for (MetadataNode child : children) {
			if (child instanceof ResourceMetadataNode) {
				maxDepth = Math.max(maxDepth, determineSubtreeDepths(((ResourceMetadataNode) child).getChildren(), depths));
			}
			else {
				depths.add(0);  // No additional levels under a literal metadata node
				maxDepth = Math.max(maxDepth, 1);
			}
		}
		
		depths.set(rootIndex, maxDepth);
		return maxDepth + 1;
	}
	
	
	public int determineMaxDepth() {
		return determineMaxDepth(getChildren());
	}
	
	
	private int determineMaxDepth(List<MetadataNode> children) {
		int additionalDepth = 0;
		for (MetadataNode child : children) {
			if (child instanceof ResourceMetadataNode) {
				additionalDepth = Math.max(additionalDepth, determineMaxDepth(((ResourceMetadataNode)child).getChildren()));
			}
			else {
				additionalDepth = Math.max(additionalDepth, 1);
			}
		}
		return additionalDepth + 1;  // Count the root of this level.
	}
	

	@Override
	public MetadataTree clone() {
		try {
			MetadataTree result = (MetadataTree)super.clone();
			result.setParent(null);
			result.children = new MetadataNodeList();
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
