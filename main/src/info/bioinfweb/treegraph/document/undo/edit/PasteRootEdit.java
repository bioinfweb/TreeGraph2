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

import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.Legend;
import info.bioinfweb.treegraph.document.Node;



/**
 * @author Ben St&ouml;ver
 */
public class PasteRootEdit extends InsertSubtreeEdit {
  private LegendPaster legendPaster;
  
  
	public PasteRootEdit(Document document, Node root, Legend[] legends) {
		super(document, null, root, 0);
		legendPaster = new LegendPaster(document, legends);
	}

	
	@Override
	public void redo() throws CannotRedoException {
		legendPaster.pasteLegends();
		super.redo();
	}


	@Override
	public void undo() throws CannotUndoException {
		legendPaster.removeLegends();
		super.undo();
	}


	@Override
	public String getPresentationName() {
		return "Paste subtree as root";
	}
}