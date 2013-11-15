/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2013  Ben Stöver, Kai Müller
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
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.nodebranchdata.TextElementDataAdapter;
import info.bioinfweb.treegraph.document.undo.DocumentEdit;



/**
 * Change the type (textual or numerical) of a cell of the node/branch data table in the document 
 * window.
 * 
 * @author Ben St&ouml;ver
 * @since 2.0.45
 */
public class ChangeCellTypeEdit extends DocumentEdit {
  private TextElementDataAdapter adapter;
  private Node node;
  private boolean newValue;
  
  
	public ChangeCellTypeEdit(Document document, TextElementDataAdapter adapter,
			Node node, boolean newValue) {
		
		super(document);
		this.adapter = adapter;
		this.node = node;
		this.newValue = newValue;
	}


	@Override
	public void redo() throws CannotRedoException {
		adapter.setType(node, newValue);
		super.redo();
	}


	@Override
	public void undo() throws CannotUndoException {
		adapter.setType(node, !newValue);
		super.undo();
	}


	@Override
	public String getPresentationName() {
		return "Change type of a node/branch data value";
	}
}
