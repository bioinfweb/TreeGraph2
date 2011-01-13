/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2011  Ben Stöver, Kai Müller
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



public interface NodeBranchDataAdapter {
	public boolean readOnly();
	
	public boolean decimalOnly();
	
	public boolean isNewColumn();
	
	public boolean isDecimal(Node node);
	
	public boolean isString(Node node);
	
	public boolean isEmpty(Node node);
	
	public String getText(Node node); 
	
	public void setText(Node node, String value); 
	
	public double getDecimal(Node node); 
	
	public void setDecimal(Node node, double value);
	
	public void delete(Node node);
	
  /**
	 * This method should return the tree element which contains the data which is edited
	 * with the implementation of the respective adapter.
	 * @param node
	 * @return
	 */
	public ConcretePaintableElement getDataElement(Node node);
	
	/**
	 * A description of the adapter that is readable by the user should be returned here.
	 * @return
	 */
	public String toString();
}