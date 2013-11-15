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


import java.text.DecimalFormat;

import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.TextElementData;
import info.webinsel.util.Math2;



/**
 * This abstract class implements basic funktionalities for adapters that deal with data
 * stored in a <code>TextElementData</code>-object.
 * @author Ben St&ouml;ver
 */
public abstract class TextElementDataAdapter extends AbstractNodeBranchDataAdapter 
    implements NodeBranchDataAdapter {
	
  public abstract TextElementData getData(Node node);

  
  public abstract boolean assignData(Node node, TextElementData data);
  
  
  public abstract DecimalFormat getDecimalFormat(Node node);

  
  /**
   * This method should create the data object if does not already exist.
   * @param node - the node wich is assiciated with the data object
   */
  protected abstract void createData(Node node);
  
  
	public boolean decimalOnly() {
		return false;
	}
	

	public boolean isNewColumn() {
		return false;
	}


	public void delete(Node node) {
		getData(node).clear();
	}
	

	public double getDecimal(Node node) {
		TextElementData d = getData(node);
		if (d != null) {
			return d.getDecimal();
		}
		else {
			return Double.NaN;
		}
	}
	

	public String getText(Node node) {
		TextElementData d = getData(node);
		if (d != null) {
			return d.getText();
		}
		else {
			return "";
		}
	}

	
	public boolean isDecimal(Node node) {
		TextElementData d = getData(node);
		if (d != null) {
			return d.isDecimal();
		}
		else {
			return false;
		}
	}
	

	public boolean isEmpty(Node node) {
		TextElementData d = getData(node);
		if (d != null) {
			return d.isEmpty();
		}
		else {
			return true;
		}
	}

	
	public boolean isString(Node node) {
		TextElementData d = getData(node);
		if (d != null) {
			return d.isString();
		}
		else {
			return false;
		}
	}

	
	public void setDecimal(Node node, double value) {
		createData(node);
		getData(node).setDecimal(value);
	}
	

	public void setText(Node node, String value) {
		createData(node);
		getData(node).setText(value);
	}
	
	
	public void setType(Node node, boolean decimal) {
		if (isDecimal(node) && !decimal) {
			setText(node, getData(node).formatValue(getDecimalFormat(node)));
		}
		else if (isString(node) && decimal) {
			try {
				setDecimal(node, Math2.parseDouble(getText(node)));
			}
			catch (NumberFormatException e) {}  // noting to do
		}
	}
}