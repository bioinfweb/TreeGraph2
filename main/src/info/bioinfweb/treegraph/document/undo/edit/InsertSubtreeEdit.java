/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2015-2016  Ben Stöver, Sarah Wiechers
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

import javax.swing.undo.*;




public class InsertSubtreeEdit extends InsertRemoveSubtreeEdit {
	public InsertSubtreeEdit(Document document, Node parent, Node root, int index) {
		super(document, parent, root, index);
	}
	
	
	public String getPresentationName() {
		if (root.isLeaf()) {
			return "Insert node";
		}
		else {
			return "Insert subtree";
		}
	}
	
	
	public void redo() throws CannotRedoException {
		insert();
		super.redo();
	}

	
	public void undo() throws CannotUndoException {
		remove();
		super.undo();
	}
}