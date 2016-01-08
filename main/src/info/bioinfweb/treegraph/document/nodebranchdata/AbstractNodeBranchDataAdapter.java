/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2015-2016  Ben Stöver, Sarah Wiechers, Kai Müller
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
package info.bioinfweb.treegraph.document.nodebranchdata;


import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.TextElementData;



/**
 * Implements a basic <code>equals()</code>-method for all node/branch data adapters.
 * @author Ben St&ouml;ver
 * @since 2.0.24
 */
public abstract class AbstractNodeBranchDataAdapter implements NodeBranchDataAdapter {
	/**
	 * Returns <code>true</code> if the specified object is an instance of the same class.
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object other) {
		return (other != null) && other.getClass().equals(getClass());  //TODO Liefert dieser Vergleich das richtige Ergebnis? 
	}

	
	@Override
	public int hashCode() {
		return getClass().getCanonicalName().hashCode();
	}


	@Override
  public void setTextElementData(Node node, TextElementData data) {
		if (!readOnly()) {
			if (data.isDecimal()) {
				try {
					setDecimal(node, data.getDecimal());
				}
				catch (NumberFormatException e) {
					delete(node);
				}
			}
			else if (data.isString()) {
				setText(node, data.getText());
			}
			else {  // empty
				delete(node);
			}
		}
  }

	
	/**
	 * Searches for the maximal node data specified by <code>adapter</code> in the 
	 * subtree under root.
	 * 
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