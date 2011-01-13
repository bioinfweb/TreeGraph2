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
package info.bioinfweb.treegraph.document.undo;


import info.bioinfweb.treegraph.document.*;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;




public abstract class DocumentEdit implements UndoableEdit {
	protected Document document;
	private boolean isSubedit = false;
	
		
	public DocumentEdit(Document document) {
		super();
		this.document = document;
	}


	public boolean getIsSubedit() {
		return isSubedit;
	}


	/**
	 * Indicates whether this edit is independent or part of another edit. The value of this property
	 * determines if the document is informed if this edit was redone or undone. 
	 * @param isSubedit
	 * @since 2.0.44
	 */
	public void setIsSubedit(boolean isSubedit) {
		this.isSubedit = isSubedit;
	}


	public boolean addEdit(UndoableEdit arg0) {
		return false;
	}

	
	public boolean canRedo() {
		return true;
	}

	
	public boolean canUndo() {
		return true;
	}

	
	public void die() {
		// Grundimplementierung leer.
	}

	
	public String getRedoPresentationName() {
		return getPresentationName();
	}

	
	public String getUndoPresentationName() {
		return getPresentationName();
	}

	
	public boolean isSignificant() {
		return true;
	}

	
	public boolean replaceEdit(UndoableEdit arg0) {
		return false;
	}


	public void redo() throws CannotRedoException {
		if (!getIsSubedit()) {
			document.getTree().assignUniqueNames();
			document.registerChange();
		}
	}


	public void undo() throws CannotUndoException {
		if (!getIsSubedit()) {
			document.registerChange();
		}
	}
}