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
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.change.DocumentChangeType;
import info.bioinfweb.treegraph.document.undo.DocumentEdit;



/**
 * This edit is executed if a branch is selected and a new node should be inserted. In 
 * that case a new parent node is created under the branch which has the previous target
 * node and a new node as children. 
 * @author Ben St&ouml;ver
 */
public class InsertSiblingEdit extends DocumentEdit {
  private Node firstSibling;
  private Node newParent = Node.newInstanceWithBranch();
  
  
	public InsertSiblingEdit(Document document, Node firstSibling, Node newSibling) {
		super(document, DocumentChangeType.TOPOLOGICAL_BY_OBJECT_CHANGE);
		this.firstSibling = firstSibling;
		
		newParent.getFormats().assign(firstSibling.getFormats());
		newParent.getAfferentBranch().getFormats().assign(firstSibling.getAfferentBranch().getFormats());
		
		newParent.getChildren().add(firstSibling);
		newParent.getChildren().add(newSibling);
		newParent.setParent(firstSibling.getParent());
		newSibling.setParent(newParent);
	}


	@Override
	public void redo() throws CannotRedoException {
		if (firstSibling.hasParent()) {
			Node parent = firstSibling.getParent();
			int index = parent.getChildren().indexOf(firstSibling);
			parent.getChildren().remove(index);
			parent.getChildren().add(index, newParent);
		}
		else {
			getDocument().getTree().setPaintStart(newParent);
		}
		firstSibling.setParent(newParent);
		
		super.redo();
	}


	@Override
	public void undo() throws CannotUndoException {
		Node parent = newParent.getParent();
		if (newParent.hasParent()) {
			int index = parent.getChildren().indexOf(newParent);
			parent.getChildren().remove(index);
			parent.getChildren().add(index, firstSibling);
		}
		else {
			getDocument().getTree().setPaintStart(firstSibling);
		}
		firstSibling.setParent(parent);
		
		super.undo();
	}


	public String getPresentationName() {
		return "Insert Sibling";
	}
}