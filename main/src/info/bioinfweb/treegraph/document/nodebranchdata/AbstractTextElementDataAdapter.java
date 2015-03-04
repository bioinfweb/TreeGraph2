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


import java.text.DecimalFormat;

import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.TextElementData;
import info.bioinfweb.commons.Math2;



/**
 * This abstract class implements basic functionalities for adapters that deal with data
 * stored in a <code>TextElementData</code>-object.
 * 
 * @author Ben St&ouml;ver
 */
public abstract class AbstractTextElementDataAdapter extends AbstractNodeBranchDataAdapter 
    implements NodeBranchDataAdapter, TextElementDataAdapter {
	
  @Override
  public abstract TextElementData getData(Node node);

  
  @Override
  public TextElementData toTextElementData(Node node) {
  	TextElementData data = getData(node);
  	if (data != null) {
  		return data.clone();
  	}
  	else {
  		return new TextElementData();  // Return empty instance if e.g. no label is present on this node.
  	}
  }


	@Override
  public abstract boolean assignData(Node node, TextElementData data);
  
  
  public abstract DecimalFormat getDecimalFormat(Node node);

  
  /**
   * This method should create the data object if does not already exist.
   * 
   * @param node - the node which is associated with the data object
   */
  protected abstract void createData(Node node);
  
  
	@Override
  public boolean decimalOnly() {
		return false;
	}
	

	@Override
	public boolean isNewColumn() {
		return false;
	}


	@Override
	public void delete(Node node) {
		getData(node).clear();
	}
	

	@Override
	public double getDecimal(Node node) {
		TextElementData d = getData(node);
		if (d != null) {
			return d.getDecimal();
		}
		else {
			return Double.NaN;
		}
	}
	

	@Override
	public String getText(Node node) {
		TextElementData d = getData(node);
		if (d != null) {
			return d.getText();
		}
		else {
			return "";
		}
	}

	
	@Override
	public boolean isDecimal(Node node) {
		TextElementData d = getData(node);
		if (d != null) {
			return d.isDecimal();
		}
		else {
			return false;
		}
	}
	

	@Override
	public boolean isEmpty(Node node) {
		TextElementData d = getData(node);
		if (d != null) {
			return d.isEmpty();
		}
		else {
			return true;
		}
	}

	
	@Override
	public boolean isString(Node node) {
		TextElementData d = getData(node);
		if (d != null) {
			return d.isString();
		}
		else {
			return false;
		}
	}

	
	@Override
	public void setDecimal(Node node, double value) {
		createData(node);
		getData(node).setDecimal(value);
	}
	

	@Override
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