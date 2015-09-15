/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2015  Ben St�ver, Kai M�ller
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


import info.bioinfweb.treegraph.document.Branch;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.TextElementData;
import info.bioinfweb.commons.Math2;



public class BranchLengthAdapter extends AbstractNodeBranchDataAdapter 
    implements NodeBranchDataAdapter {
	
	private static BranchLengthAdapter sharedInstance = null;
	
	
	public static BranchLengthAdapter getSharedInstance() {
		if (sharedInstance == null) {
			sharedInstance = new BranchLengthAdapter();
		}
		return sharedInstance;
	}
	
	
	@Override
	public boolean decimalOnly() {
		return true;
	}
	

	@Override
	public boolean readOnly() {
		return false;
	}


	@Override
	public boolean isNewColumn() {
		return false;
	}


	@Override
	public boolean isDecimal(Node node) {
		return node.getAfferentBranch().hasLength();
	}


	@Override
	public boolean isEmpty(Node node) {
		return !node.getAfferentBranch().hasLength();
	}


	@Override
  public boolean isString(Node node) {
		return false;
	}


	@Override
	public void setDecimal(Node node, double value) {
		node.getAfferentBranch().setLength(value);
	}


	@Override
	public String getText(Node node) {
		Double decimal = getDecimal(node);
		if (Double.isNaN(decimal)) {
			return "";
		}
		else {
			return "" + decimal;
		}
	}

	
	@Override
	public double getDecimal(Node node) throws NumberFormatException {
		if (node.hasAfferentBranch()) {
			if (node.getAfferentBranch().hasLength()) {
				return node.getAfferentBranch().getLength();
			}
		}
		return Double.NaN;
	}

	
	@Override
	public void setText(Node node, String value) throws NumberFormatException, UnsupportedOperationException {
		if (node.hasAfferentBranch()) {
			if ((value != null) && (!value.equals(""))) {
  			node.getAfferentBranch().setLength(Math2.parseDouble(value));
			}
			else {
				node.getAfferentBranch().setLength(Double.NaN);
			}
		}
		else {
			throw new UnsupportedOperationException("This node does not have an afferent branch to store a branch length.");
		}
	}


	@Override
	public void delete(Node node) {
		if (node.hasAfferentBranch()) {
			node.getAfferentBranch().deleteLength();
		}
	}


	@Override
	public Branch getDataElement(Node node) {
		return node.getAfferentBranch();
	}


	@Override
  public TextElementData toTextElementData(Node node) {
	  return new TextElementData(getDecimal(node));
  }


	@Override
	public String toString() {
		return "Branch lengths";
	}
}