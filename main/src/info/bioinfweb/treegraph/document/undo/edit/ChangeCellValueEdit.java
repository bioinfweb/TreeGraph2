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


import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.change.DocumentChangeType;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.undo.DocumentEdit;



/**
 * This is the abstract base class of undo objects that apply changes of node/branch data that the user 
 * made by editing a table cell of the document window.
 * 
 * @author Ben St&ouml;ver
 * @since 2.0.45
 */
public abstract class ChangeCellValueEdit extends DocumentEdit {
	private NodeBranchDataAdapter adapter;
	private Node node;
	

	public ChangeCellValueEdit(Document document, NodeBranchDataAdapter adapter, Node node) {
		super(document, DocumentChangeType.TOPOLOGICAL_BY_RENAMING);
		this.adapter = adapter;
		this.node = node;
	}


	protected NodeBranchDataAdapter getAdapter() {
		return adapter;
	}


	protected Node getNode() {
		return node;
	}
}
