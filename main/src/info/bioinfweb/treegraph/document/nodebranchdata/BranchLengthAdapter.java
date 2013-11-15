/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2013  Ben Stöver, Kai Müller
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
import info.webinsel.util.Math2;



public class BranchLengthAdapter extends AbstractNodeBranchDataAdapter 
    implements NodeBranchDataAdapter {
	
	private static BranchLengthAdapter sharedInstance = null;
	
	
	public static BranchLengthAdapter getSharedInstance() {
		if (sharedInstance == null) {
			sharedInstance = new BranchLengthAdapter();
		}
		return sharedInstance;
	}
	
	
	public boolean decimalOnly() {
		return true;
	}
	

	public boolean readOnly() {
		return false;
	}


	public boolean isNewColumn() {
		return false;
	}


	public boolean isDecimal(Node node) {
		return node.getAfferentBranch().hasLength();
	}


	public boolean isEmpty(Node node) {
		return !node.getAfferentBranch().hasLength();
	}


	public boolean isString(Node node) {
		return false;
	}


	public void setDecimal(Node node, double value) {
		node.getAfferentBranch().setLength(value);
	}


	public String getText(Node node) {
		Double decimal = getDecimal(node);
		if (Double.isNaN(decimal)) {
			return "";
		}
		else {
			return "" + decimal;
		}
	}

	
	public double getDecimal(Node node) throws NumberFormatException {
		if (node.hasAfferentBranch()) {
			if (node.getAfferentBranch().hasLength()) {
				return node.getAfferentBranch().getLength();
			}
		}
		return Double.NaN;
	}

	
	public void setDecimal(Node node, double value, int decimalPlaceCount) {
		if (node.hasAfferentBranch()) {
			node.getAfferentBranch().setLength(value);
		}
		throw new NodeBranchDataActionNotSupportedException();
	}

	
	public void setText(Node node, String value) throws NumberFormatException, NodeBranchDataActionNotSupportedException {
		if (node.hasAfferentBranch()) {
			if ((value != null) && (!value.equals(""))) {
  			node.getAfferentBranch().setLength(Math2.parseDouble(value));
			}
			else {
				node.getAfferentBranch().setLength(Double.NaN);
			}
		}
		else {
			throw new NodeBranchDataActionNotSupportedException("This node does not have an afferent " +
					"branch to store a branch length.");
		}
	}


	public void delete(Node node) {
		if (node.hasAfferentBranch()) {
			node.getAfferentBranch().deleteLength();
		}
	}


	public Branch getDataElement(Node node) {
		return node.getAfferentBranch();
	}


	public String toString() {
		return "Branch lengths";
	}
}