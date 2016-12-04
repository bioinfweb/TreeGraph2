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

import info.bioinfweb.treegraph.document.Branch;
import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.change.DocumentChangeType;
import info.bioinfweb.treegraph.document.undo.DocumentEdit;



/**
 * Splits a branch in the document into a parent and a child branch. If the branch has a defined length, the two new
 * branches get the half of that length each.
 * 
 * @author Ben St&ouml;ver
 */
public class SeparateBranchEdit extends DocumentEdit {
  private Branch branchToSplit;
  private Node insertedNode = Node.newInstanceWithBranch();
  private int index;
  
  
	public SeparateBranchEdit(Document document, Branch branch) {
		super(document, DocumentChangeType.TOPOLOGICAL_LEAF_INVARIANT);
		this.branchToSplit = branch;
		
		insertedNode.getFormats().assign(branch.getTargetNode().getFormats());
		insertedNode.getAfferentBranch().getFormats().assign(branch.getFormats());
		if (branchToSplit.hasLength()) {
			insertedNode.getAfferentBranch().setLength(0.5 * branchToSplit.getLength());
		}
	}


	@Override
	public void redo() throws CannotRedoException {
		if (branchToSplit.getTargetNode().hasParent()) {
			Node parent = branchToSplit.getTargetNode().getParent();
			index = parent.getChildren().indexOf(branchToSplit.getTargetNode());
			parent.getChildren().remove(index);
			branchToSplit.getTargetNode().setParent(insertedNode);
			insertedNode.getChildren().add(branchToSplit.getTargetNode());
			insertedNode.setParent(parent);
			parent.getChildren().add(index, insertedNode);
		}
		else {
			getDocument().getTree().setPaintStart(insertedNode);
			branchToSplit.getTargetNode().setParent(insertedNode);
			insertedNode.getChildren().add(branchToSplit.getTargetNode());
		}
		
		if (branchToSplit.hasLength()) {
			branchToSplit.setLength(0.5 * branchToSplit.getLength());
		}
		super.redo();
	}


	@Override
	public void undo() throws CannotUndoException {
		insertedNode.getChildren().remove(branchToSplit.getTargetNode());
		if (insertedNode.hasParent()) {
			Node parent = insertedNode.getParent();
			parent.getChildren().remove(insertedNode);
			branchToSplit.getTargetNode().setParent(parent);
			parent.getChildren().add(index, branchToSplit.getTargetNode());
		}
		else {
			branchToSplit.getTargetNode().setParent(null);
			getDocument().getTree().setPaintStart(branchToSplit.getTargetNode());
		}

		if (branchToSplit.hasLength()) {
			branchToSplit.setLength(2.0 * branchToSplit.getLength());
		}
		super.undo();
	}


	public String getPresentationName() {
		return "Seperate branch";
	}
}