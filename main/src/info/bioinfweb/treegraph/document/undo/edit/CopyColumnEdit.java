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

import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.undo.nodebranchdata.NodeBranchDataEdit;



/**
 * Copies a node/branch data column. The inherited field <code>adapter</code> is used as the source 
 * adapter here.
 * 
 * @author Ben St&ouml;ver
 * @since 2.0.24
 */
public class CopyColumnEdit extends NodeBranchDataEdit {
	private NodeBranchDataAdapter source;
	private boolean includeLeaves;
	
	
	public CopyColumnEdit(Document document, NodeBranchDataAdapter source, NodeBranchDataAdapter dest, 
			boolean includeLeaves) {
		
		super(document, dest);
		this.source = source;
		this.includeLeaves = includeLeaves;
	}


	private void copySubtree(Node root) {
		if (includeLeaves || !root.isLeaf()) {
			if (source.isDecimal(root)) {
				getAdapter().setDecimal(root, source.getDecimal(root));
			}
			else if (source.isString(root)) {
				try {
					getAdapter().setText(root, source.getText(root));
				}
				catch (NumberFormatException e) {
					getAdapter().setDecimal(root, Double.NaN);  // hasLength() will return false if dest is a branch lengths adapter
				}
			}
	  	else {
	  		getAdapter().delete(root);  // Ggf. vorhandenen Wert l�schen
	  	}
	
	  	for (int i = 0; i < root.getChildren().size(); i++) {
	  		copySubtree(root.getChildren().get(i));
			}
		}
  }
	
	
	@Override
	public void redo() throws CannotRedoException {
		copySubtree(getDocument().getTree().getPaintStart());
		super.redo();
	}


	public String getPresentationName() {
		return "Copy column \"" + source.toString() + "\" to \"" + getAdapter().toString() + "\"";
	}
}