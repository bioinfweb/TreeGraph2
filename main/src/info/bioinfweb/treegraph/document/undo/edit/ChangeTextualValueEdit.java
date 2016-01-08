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
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;



/**
 * Changes a textual node/branch data value. Instances of this class are created if a cell in table of
 * the document window is edited by the user.
 * 
 * @author Ben St&ouml;ver
 * @since 2.0.45
 */
public class ChangeTextualValueEdit extends ChangeCellValueEdit {
  private String newValue;
  private String oldValue;
  
  
	public ChangeTextualValueEdit(Document document,
			NodeBranchDataAdapter adapter, Node node, String newValue) {
		
		super(document, adapter, node);
		this.newValue = newValue;
		oldValue = getAdapter().getText(getNode());
	}


	@Override
	public void redo() throws CannotRedoException {
		getAdapter().setText(getNode(), newValue);
		super.redo();
	}


	@Override
	public void undo() throws CannotUndoException {
		getAdapter().setText(getNode(), oldValue);
		super.undo();
	}


	@Override
	public String getPresentationName() {
		return "Edit a textual node/branch data value";
	}
}
