/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2011, 2013-2016  Ben Stöver, Sarah Wiechers, Kai Müller
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
package info.bioinfweb.treegraph.document.undo.format;


import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.change.DocumentChangeType;
import info.bioinfweb.treegraph.document.format.GlobalFormats;
import info.bioinfweb.treegraph.document.undo.DocumentEdit;



public class GlobalFormatsEdit extends DocumentEdit {
	GlobalFormats newFormats = null;
	GlobalFormats oldFormats = new GlobalFormats();
	
	
	public GlobalFormatsEdit(Document document, GlobalFormats newFormats) {
		super(document, DocumentChangeType.POSITION);
		this.newFormats = newFormats;
		oldFormats.assign(document.getTree().getFormats());
	}
	
	
	private void setFormats(GlobalFormats formats) {
		getDocument().getTree().getFormats().assign(formats);
	}
	
	
	@Override
	public void redo() throws CannotRedoException {
		setFormats(newFormats);
		super.redo();
	}


	@Override
	public void undo() throws CannotUndoException {
		setFormats(oldFormats);
		super.undo();
	}


	public String getPresentationName() {
		return "Edit global document formats";
	}
}