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


import info.bioinfweb.treegraph.document.Document;

import java.util.ArrayList;
import java.util.List;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;



/**
 * Abstract base class for all edits that consist of a sequence of other edits. 
 * 
 * @author Ben St&ouml;ver
 * @since 2.2.0
 */
public abstract class ComposedDocumentEdit extends DocumentEdit {
  private List<DocumentEdit> edits = new ArrayList<DocumentEdit>();

  
	public ComposedDocumentEdit(Document document) {
	  super(document);
  }


	protected List<DocumentEdit> getEdits() {
		return edits;
	}


	/**
	 * Executes the subedits in the order specified by {@link #getEdits()}.
	 * 
	 * @see info.bioinfweb.treegraph.document.undo.DocumentEdit#redo()
	 */
	@Override
	public void redo() throws CannotRedoException {
		for (int i = 0; i < getEdits().size(); i++) {
			getEdits().get(i).redo();
		}
		super.redo();
	}


	/**
	 * Undoes the subedits in the reverse order as specified by {@link #getEdits()}.
	 * 
	 * @see info.bioinfweb.commons.swing.AbstractDocumentEdit#undo()
	 */
	@Override
	public void undo() throws CannotUndoException {
		for (int i = getEdits().size() - 1; i >= 0; i--) {
			getEdits().get(i).undo();
		}
		super.undo();
	}
}
