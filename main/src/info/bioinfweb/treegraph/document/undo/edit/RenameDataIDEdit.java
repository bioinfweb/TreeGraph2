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


import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.lsmp.djep.vectorJep.function.GetDiagonal;

import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.IDManager;
import info.bioinfweb.treegraph.document.undo.DocumentEdit;



/**
 * @author Ben St&ouml;ver
 * @since 2.0.24
 */
public class RenameDataIDEdit extends DocumentEdit {
  private String[] newIDs;
  private String[] oldIDs;
  
  
	/**
	 * @param document
	 * @param newIDs - the new IDs
	 * @param oldIDs - the old IDs that shall be renamed
	 */
	public RenameDataIDEdit(Document document, String[] newIDs, String[] oldIDs) {
		super(document);
		this.newIDs = newIDs;
		this.oldIDs = oldIDs;
	}


	@Override
	public void redo() throws CannotRedoException {
		for (int i = 0; i < oldIDs.length; i++) {
			IDManager.renameID(oldIDs[i], newIDs[i], getDocument().getTree());
		}
		super.redo();
	}


	@Override
	public void undo() throws CannotUndoException {
		for (int i = 0; i < oldIDs.length; i++) {
			IDManager.renameID(newIDs[i], oldIDs[i], getDocument().getTree());
		}
		super.undo();
	}


	public String getPresentationName() {
		return "Rename data ID(s)";
	}
}