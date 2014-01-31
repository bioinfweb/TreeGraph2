/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2014  Ben Stöver, Kai Müller
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
package info.bioinfweb.treegraph.document.undo.edit;


import info.bioinfweb.treegraph.document.Branch;
import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.Node;

import java.util.Iterator;
import java.util.List;



/**
 * Document edit that reroots the tree at the root branch of the smallest subtree containing all
 * specified leaf nodes.
 * 
 * @author Ben St&ouml;ver
 */
public class RerootByLeafSetEdit extends RerootEdit {
	private List<Node> leafs;
	
	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param document - the document that contains the tree to be rerooted 
	 * @param leafs - a list of leaf nodes contained in {@code document} that shall all be contained in one 
	 *        of the subtrees of the future root
	 * @throws IllegalArgumentException - if one or more of the specified nodes is not a leaf 
	 */
	public RerootByLeafSetEdit(Document document, List<Node> leafs) {
		super(document);
		this.leafs = leafs;
		setRootingPoint(calculateRootingPoint());
	}
	
	
	private Branch calculateRootingPoint() {
		// Mark paths:
		Iterator<Node> iterator = leafs.iterator();
		while (iterator.hasNext()) {
			Node leaf = iterator.next();
			if (leaf.isLeaf()) {
				markPath(leaf);
			}
			else {
				throw new IllegalArgumentException("The specified node with the unique name " + leaf.getUniqueName() + 
						" is not a leaf.");
			}
		}
		
		//TODO find largest subtree that branches from the marked paths
		
		return null;
	}
	
	
	private void markPath(Node node) {
//		while (node.hasParent() && ) {
//			node = node.getParent();
//		}
	}


	@Override
	public String getPresentationName() {
		return "Reroot tree by leaf (taxon) set";
	}
}
