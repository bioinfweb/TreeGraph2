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
package info.bioinfweb.treegraph.document;


import info.bioinfweb.treegraph.document.format.*;



/**
 * The entity for a branch.
 * @author Ben St&ouml;ver
 */
public class Branch extends ConcretePaintableElement 
    implements LineElement, HiddenDataElement, TreeElement, Cloneable {
	
  private double length = Double.NaN;
  private Labels labels = new Labels(this);
  private Node targetNode = null;  // Notwendig um von markiertem Knoten in den umgebenden Baum zu gelangen.
  private BranchFormats formats = new BranchFormats();
  private HiddenDataMap hiddenDataMap = null;
  
  
	public Branch(Node target) {
		super();
  	targetNode = target;
  	hiddenDataMap = new HiddenDataMap(getTargetNode());
  }
  
  
  public LineFormats getLineFormats() {
		return getFormats();
	}


  public double getLength() {
		return length;
	}


	public void setLength(double length) {
		this.length = length;
	}
	
	
	public boolean hasLength() {
		return !Double.isNaN(getLength());
	}
	
	
	public void deleteLength() {
		setLength(Double.NaN);
	}


	public Labels getLabels() {
	  return labels;
  }
	
	
	public Node getTargetNode() {
		return targetNode;
	}


	public void setTargetNode(Node targetNode) {
		this.targetNode = targetNode;
	}


	public BranchFormats getFormats() {
		return formats;
	}


	public void setFormats(ElementFormats formats) {
		this.formats = (BranchFormats)formats;
	}
	
	
	public HiddenDataMap getHiddenDataMap() {
		return hiddenDataMap;
	}
	
	
	public Node getLinkedNode() {
		return getTargetNode();
	}


	/**
	 * Clones this <code>Branch</code> including its <code>Labels</code>-object with the 
	 * included <code>Label</code>s. The connected <code>Node</code>s are the same objects
	 * as in the original <code>Branch</code>.
	 * @return the copy of this object 
	 */
	@Override
	public Branch clone() {
		Branch result = new Branch(getTargetNode());
		result.setLength(getLength());
		result.labels = getLabels().clone();
		result.labels.setHoldingBranch(result);
		result.getHiddenDataMap().assign(getHiddenDataMap());
		result.setFormats(getFormats().clone());
		
		return result;
	}
}