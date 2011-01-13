/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2011  Ben Stöver, Kai Müller
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

import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.undo.NodeBranchDataEdit;



/**
 * @author Ben St&ouml;ver
 * @since 2.0.24
 */
public class DeleteColumnEdit extends NodeBranchDataEdit {
	public DeleteColumnEdit(Document document, NodeBranchDataAdapter adapter) {
		super(document, adapter);
	}

	
  public static void deleteSubtree(Node root, NodeBranchDataAdapter adapter) {
  	adapter.delete(root);

  	for (int i = 0; i < root.getChildren().size(); i++) {
  		deleteSubtree(root.getChildren().get(i), adapter);
  	}
  }
	
	
	@Override
	public void redo() throws CannotRedoException {
		deleteSubtree(document.getTree().getPaintStart(), adapter);
		super.redo();
	}


	public String getPresentationName() {
		return "Delete the column \"" + adapter.toString() + "\"";
	}
}