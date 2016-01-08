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
package info.bioinfweb.treegraph.document.undo.nodebranchdata;


import javax.swing.undo.CannotUndoException;

import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.change.DocumentChangeType;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.undo.DocumentEdit;



/**
 * Can be used as a superclass for edits which change a node/branch data column. Implements 
 * restoring the previous values of that column.
 * 
 * @author Ben St&ouml;ver
 * @since 2.0.24
 */
public abstract class NodeBranchDataEdit extends DocumentEdit {
	private NodeBranchDataAdapter adapter;
	protected NodeBranchDataColumnBackup backup;


	public NodeBranchDataEdit(Document document, NodeBranchDataAdapter adapter) {
		super(document, DocumentChangeType.TOPOLOGICAL_BY_RENAMING);
		this.adapter = adapter;
		backup = new NodeBranchDataColumnBackup(adapter, document.getTree().getPaintStart());
	}


	public NodeBranchDataAdapter getAdapter() {
		return adapter;
	}


	@Override
	public void undo() throws CannotUndoException {
		backup.restore(getDocument().getTree().getPaintStart());
		super.undo();
	}
}