/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2011  Ben St�ver, Kai M�ller
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
import info.bioinfweb.treegraph.document.undo.DocumentEdit;



public class SeparateBranchEdit extends DocumentEdit {
  private Branch branch;
  private Node node = Node.getInstanceWithBranch();
  private int index;
  
  
	public SeparateBranchEdit(Document document, Branch branch) {
		super(document);
		this.branch = branch;
		
		node.getFormats().assign(branch.getTargetNode().getFormats());
		node.getAfferentBranch().getFormats().assign(branch.getFormats());
	}


	@Override
	public void redo() throws CannotRedoException {
		if (branch.getTargetNode().hasParent()) {
			Node parent = branch.getTargetNode().getParent();
			index = parent.getChildren().indexOf(branch.getTargetNode());
			parent.getChildren().remove(index);
			branch.getTargetNode().setParent(node);
			node.getChildren().add(branch.getTargetNode());
			node.setParent(parent);
			parent.getChildren().add(index, node);
		}
		else {
			document.getTree().setPaintStart(node);
			branch.getTargetNode().setParent(node);
			node.getChildren().add(branch.getTargetNode());
		}
		super.redo();
	}


	@Override
	public void undo() throws CannotUndoException {
		node.getChildren().remove(branch.getTargetNode());
		if (node.hasParent()) {
			Node parent = node.getParent();
			parent.getChildren().remove(node);
			branch.getTargetNode().setParent(parent);
			parent.getChildren().add(index, branch.getTargetNode());
		}
		else {
			branch.getTargetNode().setParent(null);
			document.getTree().setPaintStart(branch.getTargetNode());
		}
		super.undo();
	}


	public String getPresentationName() {
		return "Seperate branch";
	}
}