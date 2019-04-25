/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2011, 2013-2019  Ben Stöver, Sarah Wiechers, Kai Müller
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


import info.bioinfweb.treegraph.document.*;
import info.bioinfweb.treegraph.document.change.DocumentChangeType;
import info.bioinfweb.treegraph.document.undo.SaveLegendsEdit;



/**
 * Allows the user to inserts or remove a subtree to or from a document.
 * 
 * @author Ben St&ouml;ver
 */
public abstract class InsertRemoveSubtreeEdit extends SaveLegendsEdit {
	protected Node parent;
	protected Node root;
	protected int index;
	
	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param document - the document the specified elements are contained in
	 * @param parent - the parent node of the root node of the subtree to be deleted
	 * @param root - the root node of the subtree to be deleted
	 * @param index - the index of the root node in the child list of its parent node
	 */
	public InsertRemoveSubtreeEdit(Document document, Node parent, Node root, int index) {
		super(document, DocumentChangeType.TOPOLOGICAL_BY_OBJECT_CHANGE);
		this.parent = parent;
		this.root = root;
		this.index = index;
	}
	
	
	protected void insert() {
		root.setParent(parent);
		if (parent == null) {  // Insert as root. Tree needs to be empty.
			getDocument().getTree().setPaintStart(root);
		}
		else {
			parent.getChildren().add(index, root);
		}
	}
	
	
	protected void remove() {
		if (parent == null) {
			getDocument().getTree().setPaintStart(null);
		}
		else {
			parent.getChildren().remove(index);
		}
	}
}