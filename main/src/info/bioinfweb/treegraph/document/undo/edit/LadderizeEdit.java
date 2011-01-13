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


import java.util.Arrays;

import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.NodeLadderizeComparable;
import info.bioinfweb.treegraph.document.undo.ComplexDocumentEdit;



/**
 * This class ladderized a subtree. This operation cannot be undone.
 * @author Ben St&ouml;ver
 */
public class LadderizeEdit extends ComplexDocumentEdit {
	private Node root = null;
  private boolean down = true;

  
	public LadderizeEdit(Document document, Node root, boolean down) {
		super(document);
		this.root = findEquivilant(root);
		this.down = down;
	}


	private void ladderize(Node root, boolean down) {
  	if (!root.isLeaf()) {
  		NodeLadderizeComparable[] subtrees = NodeLadderizeComparable.encloseSubnodes(root, down);
  		root.getChildren().clear();
  		Arrays.sort(subtrees);
  		NodeLadderizeComparable.addToNode(subtrees, root);
  		for (int i = 0; i < root.getChildren().size(); i++) {
				ladderize(root.getChildren().get(i), down);
			}
  	}
	}
	
	
	@Override
	protected void performRedo() {
		NodeLadderizeComparable.countNodes(root);
		ladderize(root, down);
	}


	public String getPresentationName() {
		return "Ladderize subtree";
	} 
}