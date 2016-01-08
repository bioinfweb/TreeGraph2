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


import info.bioinfweb.treegraph.document.ConcretePaintableElement;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.TextElementData;



/**
 * This node branch data adapter can be used as a dummy instance. Data that is written to it will not be stored
 * anywhere. Instances will always remain empty.
 * 
 * @author Ben St&ouml;ver
 */
public class VoidNodeBranchDataAdapter implements NodeBranchDataAdapter {
	private String text = getClass().getName();
	
	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param text - the string representation of this instance
	 */
	public VoidNodeBranchDataAdapter(String text) {
	  super();
	  this.text = text;
  }


	@Override
	public String getName() {
		return NAME_PREFIX + "voidNodeBranchData";
	}


	@Override
  public boolean readOnly() {
	  return true;
  }
	

	@Override
  public boolean decimalOnly() {
	  return false;
  }
	

	@Override
  public boolean isNewColumn() {
	  return false;
  }
	

	@Override
  public boolean isDecimal(Node node) {
	  return false;
  }
	

	@Override
  public boolean isString(Node node) {
	  return false;
  }

	
	@Override
  public boolean isEmpty(Node node) {
	  return true;
  }

	
	/**
	 * Always returns an empty string.
	 */
	@Override
  public String getText(Node node) {
	  return "";  //TODO Should null be returned instead?
  }

	
	@Override
  public void setText(Node node, String value) {}

	
	/**
	 * Always returns {@link Double.NaN}.
	 */
	@Override
  public double getDecimal(Node node) {
	  return Double.NaN;
  }

	
	@Override
  public void setDecimal(Node node, double value) {}

	
	/**
	 * Returns an empty instance of {@link TextElementData}.
	 * 
	 * @see info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter#toTextElementData(info.bioinfweb.treegraph.document.Node)
	 */
	@Override
  public TextElementData toTextElementData(Node node) {
	  return new TextElementData();
  }

	
	@Override
  public void setTextElementData(Node node, TextElementData data) {}


	@Override
  public void delete(Node node) {}

	
	@Override
  public ConcretePaintableElement getDataElement(Node node) {
	  return null;
  }


	@Override
  public String toString() {
	  return text;
  }
}
