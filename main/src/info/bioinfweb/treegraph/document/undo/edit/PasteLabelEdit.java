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

import info.bioinfweb.treegraph.document.Branch;
import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.Label;
import info.bioinfweb.treegraph.document.TextLabel;
import info.bioinfweb.treegraph.document.undo.DocumentEdit;
import info.bioinfweb.treegraph.gui.dialogs.CollidingIDsDialog;



public class PasteLabelEdit extends DocumentEdit {
  private Label label = null;
  private Branch branch = null;
  
  
	public PasteLabelEdit(Document document, Branch branch, Label label) {
		super(document);
		this.label = label;
		this.branch = branch;
	}


	@Override
	public void redo() throws CannotRedoException {
		label.setID(CollidingIDsDialog.getInstance().checkConflicts(new Branch[]{branch}, label.getID()));
		label.setLabels(branch.getLabels());
		branch.getLabels().add(label);
		super.redo();
	}


	@Override
	public void undo() throws CannotUndoException {
		branch.getLabels().remove(label);
		label.setLabels(null);
		super.undo();
	}


	public String getPresentationName() {
		if (label instanceof TextLabel) {
			return "Paste text-label \"" + ((TextLabel)label).getData() + "\"";
		}
		else {
			return "Paste icon-label";
		}
	}
  
  
}