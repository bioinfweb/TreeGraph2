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
package info.bioinfweb.treegraph.document.undo.edit;


import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.Legend;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.change.DocumentChangeType;
import info.bioinfweb.treegraph.document.undo.DocumentEdit;



/**
 * @author Ben St&ouml;ver
 */
public class PasteSubtreeEdit extends DocumentEdit {
  private Node parent = null;
  private Node root = null;
  private LegendPaster legendPaster;
  
  
	public PasteSubtreeEdit(Document document, Node parent, Node root, Legend[] legends) {
		super(document, DocumentChangeType.TOPOLOGICAL_BY_OBJECT_CHANGE);
		this.parent = parent;
		this.root = root;
		legendPaster = new LegendPaster(document, legends);
	}
	
	
	@Override
	public void redo() throws CannotRedoException {
		legendPaster.changeUniqueNames(root);
		root.setParent(parent);
		parent.getChildren().add(root);
		legendPaster.pasteLegends();
		super.redo();
	}


	@Override
	public void undo() throws CannotUndoException {
		parent.getChildren().remove(root);
		root.setParent(null);
		legendPaster.removeLegends();
		super.undo();
	}


	public String getPresentationName() {
		return "Paste subtree";
	}
}