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
package info.bioinfweb.treegraph.document.undo.format;


import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.Legend;
import info.bioinfweb.treegraph.document.undo.DocumentEdit;



public class ReanchorLegendEdit extends DocumentEdit {
	private Legend legend = null;
	private String[] newAnchors = new String[2];
	private String[] oldAnchors = new String[2];
	

	public ReanchorLegendEdit(Document document, Legend legend, String newAnchor1, String newAnchor2) {
		super(document);
		
		this.legend = legend;
		for (int i = 0; i < 2; i++) {
			oldAnchors[i] = legend.getFormats().getAnchorName(i);
		}
		newAnchors[0] = newAnchor1;
		newAnchors[1] = newAnchor2;
	}


	@Override
	public void redo() throws CannotRedoException {
		for (int i = 0; i < 2; i++) {
			legend.getFormats().setAnchorName(i, newAnchors[i]);
		}
		super.redo();
	}


	@Override
	public void undo() throws CannotUndoException {
		for (int i = 0; i < 2; i++) {
			legend.getFormats().setAnchorName(i, oldAnchors[i]);
		}
		super.undo();
	}


	public String getPresentationName() {
		return "Reanchor legend";
	}
}