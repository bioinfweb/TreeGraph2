/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2015  Ben St�ver, Kai M�ller
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
import info.bioinfweb.treegraph.document.undo.WarningMessageEdit;

import javax.swing.undo.*;



/**
 * Removes a subtree with all of its contents from the document. Legends that are anchored only
 * inside this subtree are removed as well. Legends that have only one anchor in the subtree
 * are anchored at a new node.
 * <p>
 * Should the parent node of the deleted subtree root have less than two child nodes after
 * the operation, that node is additionally collapsed using {@link CollapseNodeEdit}.
 * 
 * @author Ben St&ouml;ver
 */
public class RemoveSubtreeEdit extends InsertRemoveSubtreeEdit implements WarningMessageEdit {
	private CollapseNodeEdit collapseNodeEdit = null;
	private boolean collapseDone = false;
	
	
	public RemoveSubtreeEdit(Document document, Node parent, Node root, int index) {		
		super(document, parent, root, index);
		if ((parent != null) && parent.getChildren().size() <= 2) {
			collapseNodeEdit = new CollapseNodeEdit(document, parent);
		}
	}
	

	@Override
	public boolean getLegendsReanchored() {
		boolean result = super.getLegendsReanchored();
		if (collapseNodeEdit != null) {
			result |= collapseNodeEdit.getLegendsReanchored();
		}
		return result;
	}


	@Override
	public boolean getLegendsRemoved() {
		boolean result = super.getLegendsRemoved();
		if (collapseNodeEdit != null) {
			result |= collapseNodeEdit.getLegendsRemoved();
		}
		return result;
	}


	public void redo() throws CannotRedoException {
		saveLegends();
		editSubtreeLegends(root);
		remove();
		if ((parent != null) && parent.getChildren().size() == 1) {
			collapseNodeEdit.redo();
			collapseDone = true;
		}
		super.redo();
	}

	
	public void undo() throws CannotUndoException {
		if (collapseDone) {
			collapseNodeEdit.undo();
		}
		insert();
		restoreLegends();
		
		super.undo();
	}


	public String getPresentationName() {
		return "Remove subtree";
	}
}