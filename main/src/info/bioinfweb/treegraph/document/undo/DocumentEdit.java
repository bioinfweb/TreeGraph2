/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2015  Ben Stöver, Kai Müller
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
import info.bioinfweb.treegraph.document.change.DocumentChangeType;
import info.bioinfweb.commons.swing.AbstractDocumentEdit;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.UndoableEdit;



/**
 * All edits modifying an instance of {@link Document} should be inherited from this class.
 * 
 * @author Ben St&ouml;ver
 */
public abstract class DocumentEdit extends AbstractDocumentEdit implements UndoableEdit {
	private Document document;
	private DocumentChangeType changeType;
	
		
	public DocumentEdit(Document document, DocumentChangeType changeType) {
		super();
		this.document = document;
		this.changeType = changeType;
	}


	public Document getDocument() {
		return document;
	}


	public DocumentChangeType getChangeType() {
		return changeType;
	}


	public void redo() throws CannotRedoException {
		super.redo();
	}


	@Override
	public void registerDocumentChange() {
  	getDocument().getTree().assignUniqueNames();  // Must be called to update the uniqueNameMap. There not necessarily nodes without unique names present, but the content of the map might not match the current tree.
		getDocument().registerChange(this);
	}
}