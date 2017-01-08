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
import info.bioinfweb.treegraph.document.Legend;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.change.DocumentChangeType;
import info.bioinfweb.treegraph.document.undo.DocumentEdit;



public class PasteLegendEdit extends DocumentEdit {
  private Legend legend;
  
  
	/**
	 * @param document - the document to paste the legend to
	 * @param anchor1 - the first anchor (Must not be <code>null</code>.)
	 * @param anchor2 - the second anchor or <code>null</code>
	 * @param legend - the legend to be pasted
	 */
	public PasteLegendEdit(Document document, Node anchor1, Node anchor2, Legend legend) {
		super(document, DocumentChangeType.POSITION);
		this.legend = legend;
		legend.getFormats().setAnchor(0, anchor1);
		legend.getFormats().setAnchor(1, anchor2);
	}


	@Override
	public void redo() throws CannotRedoException {
		getDocument().getTree().getLegends().insert(legend);
		super.redo();
	}


	@Override
	public void undo() throws CannotUndoException {
		getDocument().getTree().getLegends().remove(legend);
		super.undo();
	}


	public String getPresentationName() {
		return "Paste legend";
	}
}