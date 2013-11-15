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
package info.bioinfweb.treegraph.document.undo.format;


import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.Tree;
import info.bioinfweb.treegraph.document.undo.DocumentEdit;



public class ScaleBranchLengthsEdit extends DocumentEdit {
  private float oldScale;
  private float newScale;
  
  
  
	/**
	 * @param document
	 * @param length - the desired length of the longest path throught the tree im millimeters
	 */
	public ScaleBranchLengthsEdit(Document document, float length) {
		super(document);
		this.oldScale = document.getTree().getFormats().getBranchLengthScale().getInMillimeters();
		this.newScale = calculateScaleByLength(document.getTree(), length);
	}
	
	
	/**
	 * Returns the branch length scale necessary for the specified tree so that the longest path to a
	 * leaf of this tree has the specified length.
	 * @param tree
	 * @param pathLength - the desired length of the longest path throught the tree im millimeters
	 * @return
	 */
	public static float calculateScaleByLength(Tree tree, float pathLength) {
		return (float)(pathLength / tree.longestPath());
	}
	
	
	@Override
	public void redo() throws CannotRedoException {
		document.getTree().getFormats().getBranchLengthScale().setInMillimeters(newScale);
		super.redo();
	}



	@Override
	public void undo() throws CannotUndoException {
		document.getTree().getFormats().getBranchLengthScale().setInMillimeters(oldScale);
		super.undo();
	}



	public String getPresentationName() {
		return "Scale branch lengths";
	}
}