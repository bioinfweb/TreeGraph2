/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2014  Ben Stöver, Kai Müller
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


import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.undo.DocumentEdit;



public abstract class NodeBranchDataFormatEdit extends DocumentEdit {
	public NodeBranchDataFormatEdit(Document document) {
		super(document);
	}
	

	/**
	 * Searches for the maximal node data specified by <code>adapter</code> in the 
	 * subtree under root.
	 * @param root the root of the subtree
	 * @return the maximal node data (0 if no decimal value was found)
	 */
	public static double calculateMaxNodeData(NodeBranchDataAdapter adapter, Node root) {
		double result = 0;
		if (!Double.isNaN(adapter.getDecimal(root))) {
			result = adapter.getDecimal(root);
		}
		for (int i = 0; i < root.getChildren().size(); i++) {
			result = Math.max(result, calculateMaxNodeData(adapter, root.getChildren().get(i)));
		}
		return result;
	}
}