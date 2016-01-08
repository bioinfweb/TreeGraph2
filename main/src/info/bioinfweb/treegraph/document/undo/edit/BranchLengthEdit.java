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


import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import info.bioinfweb.treegraph.document.Branch;
import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.change.DocumentChangeType;
import info.bioinfweb.treegraph.document.undo.DocumentEdit;



public class BranchLengthEdit extends DocumentEdit {
  private Branch branch;
  private double newLength;
  private double oldLength;

  
	public BranchLengthEdit(Document document, Branch branch, double newLength) {
		super(document, DocumentChangeType.POSITION);
		
		this.branch = branch;
		oldLength = branch.getLength();
		this.newLength = newLength;
	}


	@Override
	public void redo() throws CannotRedoException {
		branch.setLength(newLength);
		super.redo();
	}


	@Override
	public void undo() throws CannotUndoException {
		branch.setLength(oldLength);
		super.undo();
	}


	public String getPresentationName() {
		return "Edit branch length";
	}
}