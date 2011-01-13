/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2011  Ben Stöver, Kai Müller
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


import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.undo.DocumentEdit;



public class MoveSubtreeEdit extends DocumentEdit {
	protected Node root;
	protected boolean down;
	
	
	public MoveSubtreeEdit(Document document, Node root, boolean down) {
		super(document);
		this.root = root;
		this.down = down;
	}

	
	/**
	 * Moves the subtree defined by <code>root</code> up or down. Ths method must not be
	 * called if <code>root</code>... 
	 * <ul>
	 *   <li>has no parent node.</li> 
	 *   <li>is the first child of the parent node and <code>down</code> is false.</li> 
	 *   <li>is the last child of the parent node and <code>down</code> is true.</li> 
	 * </ul> 
	 */
	private void move(boolean down) {
		Node parent = root.getParent();
		
		int newPos = parent.getChildren().indexOf(root);
		if (down) {
			newPos++;
		}
		else {
			newPos--;
		}
		
		parent.getChildren().remove(root);
		parent.getChildren().add(newPos, root);
	}

	
	@Override
	public void redo() throws CannotRedoException {
		move(down);
		super.redo();
	}


	@Override
	public void undo() throws CannotUndoException {
		move(!down);
		super.undo();
	}


	public String getPresentationName() {
		return "Move Subtree";
	}
}