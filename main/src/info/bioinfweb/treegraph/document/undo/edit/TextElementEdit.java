/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2011, 2013-2019  Ben Stöver, Sarah Wiechers, Kai Müller
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


import java.util.ArrayList;
import java.util.List;

import info.bioinfweb.treegraph.document.*;
import info.bioinfweb.treegraph.document.change.DocumentChangeType;
import info.bioinfweb.treegraph.document.undo.DocumentEdit;

import javax.swing.undo.*;




/**
 * Edit object that change the contents of a set of {@link TextElement}s.
 * 
 * @author Ben St&ouml;ver
 */
public class TextElementEdit extends DocumentEdit {
	private TextElement[] elements;
	private List<TextElementData> oldData;
	private TextElementData newData;

	
	public TextElementEdit(Document document, TextElement[] elements, TextElementData newData) {
	  super(document, DocumentChangeType.TOPOLOGICAL_BY_RENAMING);
	  this.elements = elements;
	  
	  oldData = new ArrayList<>(elements.length);
	  for (TextElement element : elements) {
			oldData.add(element.getData().clone());
		}
	  this.newData = newData;
	}
	
	
	public String getPresentationName() {
		return "Change Text to \"" + newData + "\"";  //TODO Text ggf. bei einer Maximallänge abschneiden.
	}
	

	public void redo() throws CannotRedoException {
		for (TextElement element : elements) {
			element.getData().assign(newData);
		}
		super.redo();
	}

	
	public void undo() throws CannotUndoException {
		for (int i = 0; i < elements.length; i++) {
			elements[i].getData().assign(oldData.get(i));
		}
		super.undo();
	}
}