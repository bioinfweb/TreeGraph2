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



public class UniqueNameAdapter extends AbstractNodeBranchDataAdapter implements NodeBranchDataAdapter {
	private static UniqueNameAdapter sharedInstance = null;
	
	
	public static UniqueNameAdapter getSharedInstance() {
		if (sharedInstance == null) {
			sharedInstance = new UniqueNameAdapter();
		}
		return sharedInstance;
	}
	
	
	@Override
	public String getName() {
		return NAME_PREFIX + "uniqueNodeName";
	}


	public boolean readOnly() {
		return true;
	}

	
	public boolean decimalOnly() {
		return false;
	}

	
	public boolean isNewColumn() {
		return false;
	}


	public boolean isDecimal(Node node) {
		return false;
	}


	public boolean isEmpty(Node node) {
		return false;
	}


	public boolean isString(Node node) {
		return true;
	}


	public void delete(Node node) {
		throw new UnsupportedOperationException("Cannot delete the unique name of a node.");
	}
	

	public double getDecimal(Node node) {
		throw new UnsupportedOperationException("Unique node names cannot be parsed as a decimal.");
	}

	
	public String getText(Node node) {
		return node.getUniqueName();
	}
	

	public void setDecimal(Node node, double value) {
		setText(node, "" + value);
	}
	

	public void setText(Node node, String value) {
		node.setUniqueName(value);
	}


	public Node getDataElement(Node node) {
		return node;
	}


	@Override
  public TextElementData toTextElementData(Node node) {
	  return new TextElementData(getText(node));
  }


	@Override
	public String toString() {
		return "Unique node names";
	}
}