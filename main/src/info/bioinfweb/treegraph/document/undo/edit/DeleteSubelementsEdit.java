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

import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.undo.SaveLegendsEdit;



public class DeleteSubelementsEdit extends SaveLegendsEdit {
	private Node root;
  private Node[] subnodes;
	
	
	public DeleteSubelementsEdit(Document document, Node root, boolean showWarnings) {
		super(document);
		this.root = root;
		setShowWarnings(showWarnings);
		setHelpTopic(28);

		subnodes = new Node[root.getChildren().size()];
		for (int i = 0; i < root.getChildren().size(); i++) {
			subnodes[i] = root.getChildren().get(i); 
		}
	}


	@Override
	public void redo() throws CannotRedoException {
		saveLegends();
		editSubtreeLegends(root);
		root.getChildren().clear();
		showWarningDialog();
		super.redo();
	}

	
	@Override
	public void undo() throws CannotUndoException {
		for (int i = 0; i < subnodes.length; i++) {
			root.getChildren().add(subnodes[i]);
		}
		restoreLegends();
		super.undo();
	}

	
	public String getPresentationName() {
		return "Delete subelements";
	}
}