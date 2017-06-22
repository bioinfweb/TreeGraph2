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

import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.TreeElement;

public class MetadataTree implements Cloneable {
	private MetadataNode metadataRoot = null;
	private List<MetadataNode> treeChildren = new ArrayList<MetadataNode>();
	
	
	public MetadataTree(TreeElement node) {
		this.metadataRoot = (MetadataNode) node;
	}


	public MetadataNode getRoot() {
		return metadataRoot;
	}
	
	
	public void clear() {
		treeChildren.clear();
	}
	
	
	public boolean isEmpty() {
	  return treeChildren.isEmpty();
  }
	
	
	public List<MetadataNode> getChildren(MetadataNode metadataNode) {
		int index = 0;
		while (metadataNode != null) {
			treeChildren.add(index, metadataNode);
			index++;
		}
		return treeChildren;
	}
	
	
	public MetadataTree clone(TreeElement other) {
		MetadataTree metadataTree = new MetadataTree(other);
		metadataTree.clone(other);
		return metadataTree;
	}
}
