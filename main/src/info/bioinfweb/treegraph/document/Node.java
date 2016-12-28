/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2011, 2013-2016  Ben Stöver, Sarah Wiechers, Kai Müller
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
import info.bioinfweb.treegraph.document.position.NodePositionData;
import info.bioinfweb.treegraph.document.tools.TreeSerializer;
import info.bioinfweb.treegraph.graphics.positionpaint.PositionPaintType;

import java.util.*;



/**
 * This class represents a node of a tree in the TreeGraph data structure. 
 * @author Ben St&ouml;ver
 */
public class Node extends AbstractTextElement 
    implements LineElement, CornerRadiusElement, HiddenDataElement, TreeElement, Cloneable {
	
	private Node parent = null;
  private ArrayList<Node> children = new ArrayList<Node>();
  private Branch afferentBranch = null;
  private NodeFormats formats = new NodeFormats();
  private String uniqueName = null;
  private HiddenDataMap hiddenDataMap = new HiddenDataMap(this);
  private HashMap<String, Object> attributeMap = new HashMap<String, Object>(); 

  
	public static Node newInstanceWithBranch() {
		Node result = new Node();
		result.setAfferentBranch(new Branch(result));
		return result;
	}
  
  
  @Override
	public NodePositionData getPosition(PositionPaintType type) {
		NodePositionData result = (NodePositionData)positions.get(type);
		if (result == null) {
			result = new NodePositionData();
			positions.put(type, result);
		}
		
		return result;
	}


	public Node getParent() {
		return parent;
	}


	public void setParent(Node node) {
		parent = node;
	}


	public List<Node> getChildren() {
		return children;
	}
	 

  public Branch getAfferentBranch() {
		return afferentBranch;
	}


	/**
	 * Sets the specified branch as the afferent branch of this node and additionally 
	 * sets this node as the target node of the specified branch.
	 * @param afferentBranch - the new afferent branch
	 */
	public void setAfferentBranch(Branch afferentBranch) {
		this.afferentBranch = afferentBranch;
		afferentBranch.setTargetNode(this);
	}

	
	/**
	 * The attribute map can store data that is specific to special tasks like ladderizing
	 * or tree merging. Several special routines can store custom objects under their own
	 * key here.
	 * @return the attribute map
	 */
	public HashMap<String, Object> getAttributeMap() {
		return attributeMap;
	}


	/**
	 * Tests if this <code>Node</code> is at the deepest level of the tree (has no subnodes).
   * @return true if there are no subnodes or branches.
   */
  public boolean isLeaf() {
  	return getChildren().size() == 0;
  }
  
  
	public boolean hasParent() {
  	return getParent() != null;
  }
	
	
	public boolean hasAfferentBranch() {
		return getAfferentBranch() != null;
	}
	
	
	/**
	 * Tests if this element is the only <code>Node</code> under the parent node.
	 * @return true, if the given element is the only element in the parent node
	 */
	public boolean isOnlySubelement() {
		if (!hasParent()) {
			return true;  // Element ist Baumwurzel und deshalb zwangl�ufig allein.
		}
		else {
			return getParent().getChildren().size() == 1;
		}
	}
	
	
	/** 
	 * @return true, if this element is the first
	 */
	public boolean isFirst() {
		if (!hasParent()) {
			return true;  // Element ist allein und somit auch ganz vorne.
		}
		else {  // Es gibt einen Elternknoten
			return getParent().getChildren().indexOf(this) == 0;
		}
	}


	/**  
	 * @return true, if this element is the last in its parent node
	 */
	public boolean isLast() {
		if (!hasParent()) {
			return true;  // Element ist allein und somit auch ganz vorne.
		}
		else {  // Es gibt einen Elternknoten
			return getParent().getChildren().indexOf(this) == getParent().getChildren().size() - 1;
		}
	}


	public Node getPrevious() {
  	if (hasParent() && !isFirst()) {
  		return getParent().getChildren().get(getParent().getChildren().indexOf(this) - 1);
  	}
  	else {
  		return null;
  	}
	}
	
	
	public Node getNext() {
  	if (hasParent() && !isLast()) {
  		return getParent().getChildren().get(getParent().getChildren().indexOf(this) + 1);
  	}
  	else {
  		return null;
  	}
	}
	
	
	public Node getPreviousLeaf() {
		Node next = this;
		while ((next != null) && (next.isFirst())) {
			next = next.getParent();
		}
		if (next != null) {
			return next.getPrevious().getLowestChild();
		}
		else {
			return null;
		}
	}
	
	
	public Node getNextLeaf() {
		Node previous = this;
		while ((previous != null) && (previous.isLast())) {
			previous = previous.getParent();
		}
		if (previous != null) {
			return previous.getNext().getHighestChild();
		}
		else {
			return null;
		}
	}
	
	
	/**
	 * Tests if the specified node is an ancestor of this node. (The result of <code>n.isAncestor(n)</code> 
	 * is <code>false</code>.)
	 * @param ancestor
	 * @return
	 */
	public boolean isChildOf(Node ancestor) {
		Node pos = this.getParent();
		while (pos != null) {
			if (pos == ancestor) {
				return true;
			}
			pos = pos.getParent();
		}
		return false;
	}
	

	/**
	 * Tests whether an element is contained in the subtree under this node. This also includes
	 * legends which are completely anchored inside this subtree.
	 * @param element - the element to search for (either a <code>Node</code>, <code>Branch</code>,
	 *        <code>Label</code> or <code>Legend</code>) 
	 * @return <code>true</code>, if the element is found
	 * @see TreeSerializer
	 */
	public boolean containedInSubtree(AbstractPaintableElement element) {
		if (element == null) {
			return false;
		}
		if (element == this) {
			return true;
		}
		if (hasAfferentBranch()) {
			if (element == getAfferentBranch()) {
				return true;
			}
			if ((element instanceof Label) && getAfferentBranch().getLabels().contains((Label)element)) {
				return true;
			}
		}
		if (element instanceof Legend) {
			LegendFormats f = ((Legend)element).getFormats();
			return containedInSubtree(f.getAnchor(0)) && 
			    (f.hasOneAnchor() || containedInSubtree(f.getAnchor(1)));
		}
		
		for (int i = 0; i < getChildren().size(); i++) {
			if (getChildren().get(i).containedInSubtree(element)) {
				return true;
			}
		}
		return false;
  }
  
	
	public NodeFormats getFormats() {
		return formats;
	}


	public void setFormats(ElementFormats formats) {
		this.formats = (NodeFormats)formats;
	}


	/**
	 * The unique name is used to identify nodes in the XML-file which are associated with 
	 * legends.
	 * @return the unique name or null if none is assigned
	 */
	public String getUniqueName() {
		return uniqueName;
	}


	/**
	 * The unique name is used to identify nodes in the XML-file which are associated with 
	 * legends.
	 * @param uniqueName - the new unique name or null if it shall be deleted
	 */
	public void setUniqueName(String uniqueName) {
		if (uniqueName != null) {
			uniqueName = uniqueName.toLowerCase();
		}
		this.uniqueName = uniqueName;
	}
	
	
	/**
	 * Test if this node has a unique name specified
	 * @return true if a unique name has been specified, false if the unique name is 
	 * <code>null</code>
	 */
	public boolean hasUniqueName() {
		return uniqueName != null;
	}
	
	
	public HiddenDataMap getHiddenDataMap() {
		return hiddenDataMap;
	}


	/**
	 * Returns the upper most subnode of this node (e.g. needed for legend positioning). 
	 * @return the upper most subnode
	 */
	public Node getHighestChild() {
		if (isLeaf()) {
			return this;
		}
		else {
			return getChildren().get(0).getHighestChild();
		}
	}


	/**
	 * Returns the lowest subnode of this node (e.g. needed for legend positioning). 
	 * @return the lowest subnode
	 */
	public Node getLowestChild() {
		if (isLeaf()) {
			return this;
		}
		else {
			return getChildren().get(getChildren().size()  - 1).getLowestChild();
		}
	}
	
	
	private void searchLeafNames(Node root, Vector<String> names) {
		if (root.isLeaf()) {
			names.add(TextElementData.formatTextElement(root));
		}
		else {
			for (int i = 0; i < root.getChildren().size(); i++) {
				searchLeafNames(root.getChildren().get(i), names);
			}
		}
	}
	
	
	public Vector<String> leafNames() {
		Vector<String> result = new Vector<String>();
		searchLeafNames(this, result);
		return result;
	}
		
	
	public Node getLinkedNode() {
		return this;
	}
	
	
	/**
	 * Returns a deep copy of this node not including the subtrees. 
	 * The attribute map and the unique names are not copied. Calling this method is
	 * equivalent to <code>clone(false)</code>.
	 * */
	@Override
	public Node clone() {
		Node result = new Node();
		result.assignTextElementData(this);
		result.setAfferentBranch(getAfferentBranch().clone());
		result.getHiddenDataMap().assign(getHiddenDataMap());
		result.setFormats(getFormats().clone());
		result.attributeMap = new HashMap<String, Object>();
		return result;
	}

	
	/**
	 * Returns a deep copy of this node not including the subtrees. 
	 * The attribute map is not copied.
	 * @param keepUniqheNames - defines whether the copy should have the same unique
	 * name as the original.
	 * @return the copy
	 * */
	public Node clone(boolean keepUniqheNames) {
		Node result = clone();
		if (keepUniqheNames) {
			result.setUniqueName(getUniqueName());
		}
		return result;
	}
	
	
	/**
	 * Clones this object and all objects in this subtree and its afferent branch. The 
	 * parent node will be set to <code>null</code>.
	 * @param keepUniqueNames - defines whether the copied nodes should have the same 
	 * unique names as the originals.
	 * @return the root of the cloned tree
	 */
	public Node cloneWithSubtree(boolean keepUniqueNames) {
		Node result = clone(keepUniqueNames);
		for (int i = 0; i < getChildren().size(); i++) {
			Node child = getChildren().get(i).cloneWithSubtree(keepUniqueNames);
			child.setParent(result);
			result.getChildren().add(child);
		}
		return result;
	}


	@Override
  public String toString() {
		String result = super.toString();
	  return result.substring(0, result.length() - 1) + ", uniqueName=" + getUniqueName() + "]";
  }
}