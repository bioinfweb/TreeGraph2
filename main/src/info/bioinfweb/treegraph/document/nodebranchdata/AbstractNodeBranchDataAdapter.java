/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2015  Ben Stöver, Kai Müller
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
}