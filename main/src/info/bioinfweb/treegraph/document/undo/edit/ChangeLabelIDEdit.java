/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2011, 2013-2017  Ben Stöver, Sarah Wiechers, Kai Müller
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
import info.bioinfweb.treegraph.document.Label;
import info.bioinfweb.treegraph.document.change.DocumentChangeType;
import info.bioinfweb.treegraph.document.undo.DocumentEdit;



/**
 * Changes the IDs of a specified group of labels.
 * <p>
 * This class should only be used to change the IDs of labels and not to rename an ID in the 
 * document, because it informs document listeners only about general changes in the document and not
 * about a renamed ID. For this case {@link RenameDataIDEdit} should be used.
 *  
 * @author Ben St&ouml;ver
 */
public class ChangeLabelIDEdit extends DocumentEdit {
  private String newID;
  private Label[] labels;
  private String[] oldIDs;
  
  
	public ChangeLabelIDEdit(Document document, String newID, Label[] labels) {
		super(document, DocumentChangeType.TOPOLOGICAL_BY_RENAMING);  // Does anyway not change the paintable element position.
		this.newID = newID;
		this.labels = labels;
		
		oldIDs = new String[labels.length];
		for (int i = 0; i < labels.length; i++) {
			oldIDs[i] = labels[i].getID();
		}
	}


	@Override
	public void redo() throws CannotRedoException {
		for (int i = 0; i < labels.length; i++) {
			labels[i].setID(newID);
		}
		
		super.redo();
	}


	@Override
	public void undo() throws CannotUndoException {
		for (int i = 0; i < labels.length; i++) {
			labels[i].setID(oldIDs[i]);
		}
		
		super.undo();
	}


	public String getPresentationName() {
		return "Change label ID to \"" + newID + "\"";
	}
}