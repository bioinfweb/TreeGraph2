/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2015  Ben Stöver, Sarah Wiechers, Kai Müller
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


import info.bioinfweb.treegraph.document.*;
import info.bioinfweb.treegraph.document.change.DocumentChangeType;
import info.bioinfweb.treegraph.document.format.LabelFormats;
import info.bioinfweb.treegraph.document.undo.DocumentEdit;



/**
 * Abstract implementation for edits that insert or remove labels.
 * 
 * @author Ben St&ouml;ver
 */
public abstract class InsertRemoveLabelEdit extends DocumentEdit {
	protected Label label;
	protected Labels owner;
	
	
	public InsertRemoveLabelEdit(Document document, Label label, Labels owner) {
		super(document, DocumentChangeType.TOPOLOGICAL_BY_RENAMING);  // The label could be inserted with the ID of the default leaves adapter.
		this.label = label;
		this.owner = owner;
	}
	
	
	protected void insert() {
		if (!IDManager.idExistsOnNode(owner.getHoldingBranch().getTargetNode(), label.getID())) {
			LabelFormats f = label.getFormats();  
			f.setLinePosition(owner.getLastLinePos(f.isAbove(), f.getLineNumber()));
			label.setLabels(owner);
			owner.add(label);
		}
		else {
			throw new DuplicateIDException(label.getID(), owner.getHoldingBranch().getTargetNode());
			//TODO Remove this exception, because CollidingIDsDialog allows overwriting of labels. Before removal overwritten labels must be saved by this edit in order to restore them in undo().
		}
	}
	
	
	protected void remove() {
		owner.remove(label);
		label.setLabels(null);
		//label.getFormats().setOwner(null);  //TODO Ist owner=null �berall auf Zugriffsfehler gesichert?
	}
}