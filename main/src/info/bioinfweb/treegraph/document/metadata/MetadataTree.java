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
import java.util.List;
import java.util.ListIterator;

import info.bioinfweb.treegraph.document.Node;



public class MetadataTree implements Cloneable {
	private Node owner;
	private List<MetadataNode> treeChildren = new ArrayList<MetadataNode>();
	private MetadataNode parent = null;
	
	
	public MetadataTree(Node owner) {
		this.owner = owner;
	}
	
	
	public Node getOwner() {
		return owner;
	}
	
	
	public Node setOwner(Node owner) {
		this.owner = owner;
		return owner;
	}
	
	
	public MetadataNode getParent() {
		return parent;
	}
	
	
	public MetadataNode setParent(MetadataNode parent) {
		this.parent = parent;
		return parent;
	}
	
	
	public List<MetadataNode> getTreeChildren() {
		return treeChildren;
	}


	//TODO Can I change parameter type to Object and then cast to MetadataNode? Would help in RerootEdit.
	public void setTreeChildren(MetadataNode metaNode) {
		clear();
		ListIterator<MetadataNode> iterator = iterator();
		while (iterator.hasNext()) {
			treeChildren.add(metaNode);
			iterator.next();			
		}
	}
	
	
	public ListIterator<MetadataNode> iterator() {
		return treeChildren.listIterator();
	}
	
	
	public void clear() {
		treeChildren.clear();
	}
	
	
	public boolean isEmpty() {
		return treeChildren.isEmpty();
	}


	@Override
	public List<MetadataNode> clone() throws CloneNotSupportedException {
//		MetadataTree result = new MetadataTree();
		List<MetadataNode> nodeList = new ArrayList<MetadataNode>();
		MetadataNode result = new MetadataNode();
		for (int i = 0; i < treeChildren.size(); i++) {
			nodeList.add(i, result.cloneWithSubtree());
		}
		return nodeList;
	}
}
