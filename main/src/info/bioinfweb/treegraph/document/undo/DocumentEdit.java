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
package info.bioinfweb.treegraph.document.undo;


import info.bioinfweb.treegraph.document.*;
import info.bioinfweb.commons.swing.AbstractDocumentEdit;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.UndoableEdit;




public abstract class DocumentEdit extends AbstractDocumentEdit implements UndoableEdit {
	private Document document;
	
		
	public DocumentEdit(Document document) {
		super();
		this.document = document;
	}


	public Document getDocument() {
		return document;
	}


	public void redo() throws CannotRedoException {
		super.redo();
	}


	@Override
	public void registerDocumentChange() {
  	getDocument().getTree().assignUniqueNames();  // Must be called to update the uniqueNameMap. There not necessarily nodes without unique names present, but the content of the map might not match the current tree.
		getDocument().registerChange();
	}
}