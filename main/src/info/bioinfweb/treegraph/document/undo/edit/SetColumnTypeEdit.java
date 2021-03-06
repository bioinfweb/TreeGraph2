/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2011, 2013-2019  Ben Stöver, Sarah Wiechers, Kai Müller
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
import info.bioinfweb.treegraph.document.nodebranchdata.AbstractTextElementDataAdapter;
import info.bioinfweb.treegraph.document.undo.nodebranchdata.NodeBranchDataEdit;



/**
 * Switches the type of elements in a node/branch data column between numerical and textual if possible.
 * 
 * @author Ben St&ouml;ver
 */
public class SetColumnTypeEdit extends NodeBranchDataEdit {
	private boolean decimal;
	
	
  public SetColumnTypeEdit(Document document, AbstractTextElementDataAdapter adapter, boolean decimal) {
		super(document, adapter);
		this.decimal = decimal;
	}

  
  @Override
  public AbstractTextElementDataAdapter getAdapter() {
  	return (AbstractTextElementDataAdapter)super.getAdapter();
  }
  
  
	protected void setDataTypeInSubtree(Node root, AbstractTextElementDataAdapter adapter, 
  		boolean decimal) {
  	
  	adapter.setType(root, decimal);
  	for (int i = 0; i < root.getChildren().size(); i++) {
			setDataTypeInSubtree(root.getChildren().get(i), adapter, decimal);
		}
  }


	@Override
	public void redo() throws CannotRedoException {
		setDataTypeInSubtree(getDocument().getTree().getPaintStart(), getAdapter(), decimal);
		super.redo();
	}


	public String getPresentationName() {
		String type = "text";
		if (decimal) {
			type = "decimal";
		}
		return "Set column to " + type + " type";
	}
}