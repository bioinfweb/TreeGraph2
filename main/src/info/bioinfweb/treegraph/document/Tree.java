/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2013  Ben St�ver, Kai M�ller
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


import info.bioinfweb.treegraph.document.format.DistanceDimension;
import info.bioinfweb.treegraph.document.format.GlobalFormats;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.graphics.positionpaint.PositionPaintType;
import info.webinsel.util.RandomValues;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.lsmp.djep.vectorJep.function.ElementMultiply;



/**
 * Instances of this class provide an environment for the recursive data-structure of 
 * tree-elements without their formats.
 *  
 * @author Ben St&ouml;ver
 */
public class Tree {
	/**
	 * The default length of the longest path from the root to a leaf in millimeters. (Used by 
	 * {@link info.bioinfweb.treegraph.document.io.newick.NewickReader} and 
	 * {@link info.bioinfweb.treegraph.document.io.nexus.NexusReader}).)
	 */
	public static final float DEFAULT_LONGEST_PATH_LENGTH = 150;  // mm
	
	public static final String UNIQUE_NAME_CHARS = "abcdefghijklmnopqrstuvwxyz01234567890";
	public static final int UNIQUE_NAME_LENGTH = 10;  // Entspricht 10^14 M�glichkeiten

	
	private Node paintStart = null;
	private ScaleBar scaleBar = new ScaleBar();
  private Legends legends = new Legends(this);
	private EnumMap<PositionPaintType, DistanceDimension> paintDimensions = new EnumMap<PositionPaintType, DistanceDimension>(PositionPaintType.class);
  private GlobalFormats formats = new GlobalFormats();
  private TreeMap<String, Node> uniqueNameMap = new TreeMap<String, Node>();
  private HashSet<ConcretePaintableElement> elementSet = new HashSet<ConcretePaintableElement>();
	
	
	public Node getPaintStart() {
		return paintStart;
	}


	public void setPaintStart(Node paintStart) {
		this.paintStart = paintStart;
	}


	public ScaleBar getScaleBar() {
		return scaleBar;
	}


	public void setScaleBar(ScaleBar scaleBar) {
		this.scaleBar = scaleBar;
	}
	
	
	public Legends getLegends() {
		return legends;
	}

	
  public GlobalFormats getFormats() {
	  return formats;
  }
  
  
	/**
	 * Tests if there are tree elements present. Note that it is not testet weather there
	 * are legends present even if no tree elements are.
	 * @return true if tree elements (minimal a root node) are present
	 */
	public boolean isEmpty() {
	  return getPaintStart() == null;
  } 
	
	
  /**
   * Removes all tree elements including all legends from this object.
   */
  public void clear() {
  	setPaintStart(null);
  	getLegends().clear();
	}
  
  
  /**
   * Tests if the given element is present in the tree.
   * @param element the element to search for
   * @return true, if the element is found
   */
  public boolean contains(ConcretePaintableElement element) {
  	return elementSet.contains(element);
  }
  
  
	/**
	 * Returns the stored paint dimension (the size of the document) for the given painter 
	 * ID. If none is stored an new empty instance of {@link DistanceDimension} is stored
	 * for this ID and returned.
	 * @param id - the painter ID 
	 * @return the stored paint dimension
	 */
	public DistanceDimension getPaintDimension(PositionPaintType type) {
		DistanceDimension result = paintDimensions.get(type);
		if (result == null) {
			result = new DistanceDimension();
			paintDimensions.put(type, result);
		}
		return result;
	}
	
	
	/**
	 * Generates a random unique name for a node which is currently not present in this 
	 * tree. Currently a unique name is 10 characters long and consists of the following 
	 * characters: <code>abcdefghijklmnopqrstuvwxyz01234567890</code>. This means that 
	 * there are 27<sup>10</sup> &asymp; 2 &bull; 10<sup>14</sup> different possible names.  
	 * @return the new unique name
	 */
	public String newUniqueName() {
		String result;
		do {
			result = RandomValues.randChars(UNIQUE_NAME_CHARS, UNIQUE_NAME_LENGTH);
		}	while (getNodeByUniqueName(result) != null);
		return result;
	}
	
	
	/**
	 * Assigns unique names to all nodes in the subtree under root currently without one.<br>
	 * This method is used by {@link info.webinsel.treegraph.document.Tree.assignUniqueNames()}.
	 * @param root - the root node of the subtree
	 */
	private void assignUniqueNamesToSubtree(Node root) {
		if (!root.hasUniqueName()) {
			root.setUniqueName(newUniqueName());
		}
		uniqueNameMap.put(root.getUniqueName(), root);
		
		for (int i = 0; i < root.getChildren().size(); i++) {
			assignUniqueNamesToSubtree(root.getChildren().get(i));
		}
	}
	
	
	/**
	 * Assigns unique names to all nodes in the tree currently without one.
	 */
	public void assignUniqueNames() {
		uniqueNameMap.clear();
		if (getPaintStart() != null) {
			assignUniqueNamesToSubtree(getPaintStart());
		}
	}
	
	
	public void updateElementSet() {
		elementSet.clear();
		
		ConcretePaintableElement[] elements = 
			  TreeSerializer.getElementsInSubtree(getPaintStart(), false, ConcretePaintableElement.class);
		for (int i = 0; i < elements.length; i++) {
			elementSet.add(elements[i]);
		}
		
		for (int i = 0; i < getLegends().size(); i++) {
			elementSet.add(legends.get(i));
		}
		elementSet.add(getScaleBar());
	}
	
	
	/**
	 * Searches for a node with the given unique name in this tree. 
	 * @param uniqueName - the searched unique name
	 * @return the node with the unique name or null if no node is found
	 */
	public Node getNodeByUniqueName(String uniqueName) {
		if (isEmpty() || (uniqueName == null)) {
			return null;
		}
		else {
			return uniqueNameMap.get(uniqueName);
		}
	}
	
	
	/**
	 * Returns the linked node to the given element. The result differs by the type of
	 * <code>PaintableElement</code> as follows:
	 * <ul>
	 *   <li><code>Node</code>: The node itself</li>
	 *   <li><code>Branch</code>: The target node of this branch</li>
	 *   <li><code>Label</code>: The target node of the holding branch</li>
	 *   <li><code>Legend</code>: <code>null</code></li>
	 * </ul>
	 * @param element - the element which linked node shall be returned
	 * @return the linked node or null
	 */
	public static Node getLinkedNode(PaintableElement element) {
		if (element instanceof Node) {
			return (Node)element;
		}
		else if (element instanceof Branch) {
			return ((Branch)element).getTargetNode();
		}
		else if (element instanceof Label) {
			return ((Label)element).getLabels().getHoldingBranch().getTargetNode();
		}
		else {  // z.B. Legend
			return null;
		}
	}
	
	
	public static Node mostRecentCommonAncestor(Node n1, Node n2) {
		Node ancestor = n1;
		while (ancestor != null) {
			if ((n2 == ancestor) || n2.isChildOf(ancestor)) {
				return ancestor;
			}
			ancestor = ancestor.getParent();
		}
		return null;
	}
	
	
	/**
	 * Tests is there are any branches without a defined length in the subtree under 
	 * <code>root</code>.
	 * @param root - the root node of the tree
	 * @param needsRootLength - specifies whether the root node needs a defined branch length
	 * @return <code>true</code>, if there a no lengths missing or <code>root</code> was 
	 *         <code>null</code>.
	 */
	public static boolean hasAllBranchLengths(Node root, boolean needsRootLength) {
		if (root != null) {
			if (!(root.hasAfferentBranch() && root.getAfferentBranch().hasLength()) && 
					needsRootLength) {
				
				return false;
			}
			for (int i = 0; i < root.getChildren().size(); i++) {
				if (!hasAllBranchLengths(root.getChildren().get(i), true)) {
					return false;
				}
			}
		}
		return true;
	}
	
	
	/**
	 * Tests is there are any branches without a defined length in the tree. The root branch
	 * only needs to have a length if it is displayed. Calling this method is equivalent to
	 * <code>hasAllBranchLengths(getPaintStart(), getFormats().getShowRooted())</code>.
	 * @return <code>true</code>, if there a no necessary lengths missing
	 */
	public boolean hasAllBranchLengths() {
		return hasAllBranchLengths(getPaintStart(), getFormats().getShowRooted());
	}
	
	
	/**
	 * Returns the longest path to a terminal in the subtree of <code>root</code>. All branch lengths of
	 * the subtree under root must be defined except that of the root branch if 
	 * <code>includeRootBranch</code> is <code>false</code>. 
	 * @param root - the root node of the tree
	 * @param includeRootBranch - indicates whether the length of the afferent branch of the root node
	 *        shall be included
	 * @return the lengths in branch length units or <code>Double.NaN</code> if not all branch lengths 
	 *         are defined
	 */
	public static double longestPath(Node root, boolean includeRootBranch) {
		if (hasAllBranchLengths(root, includeRootBranch)) {
			double result = 0;
			for (int i = 0; i < root.getChildren().size(); i++) {
				result = Math.max(result, longestPath(root.getChildren().get(i), true));
			}
			if (includeRootBranch) {
				result += root.getAfferentBranch().getLength();
			}
			return result;
		}
		else {
			return Double.NaN;
		}
	}
	
	
	/**
	 * Returns the longest path to a terminal in this tree. All branch lengths must be defined except 
	 * that of the root branch if <code>getFormats().getShowRooted()</code> is <code>false</code>.
	 * If this tree is empty 0 is returned. 
	 * @return the lengths in branch length units or <code>Double.NaN</code> if not all branch lengths 
	 *         are defined
	 */
	public double longestPath() {
		if (isEmpty()) {
			return 0;
		}
		else {
			return longestPath(getPaintStart(), getFormats().getShowRooted());
		}
	}
	
	
	/**
	 * Tests if the subtree under <code>root</code> contains a node which would be 
	 * able to return a decimal value to the given adapter.
	 * @param adapter - the adapter to obtain the decimal value
	 * @param root - the root of the subtree to be checked
	 * @return <code>true</code> if at least one decimal value could be returned 
	 */
	public static boolean containsDecimal(NodeBranchDataAdapter adapter, Node root) {
		if (adapter.isDecimal(root)) {
			return true;
		}
		for (int i = 0; i < root.getChildren().size(); i++) {
			if (containsDecimal(adapter, root.getChildren().get(i))) {
				return true;
			}
		}
		return false;
	}
	
	
	/**
	 * Tests if the tree contains a node which would be able to return a decimal value 
	 * to the given adapter.
	 * @param adapter - the adapter to obtain the decimal value
	 * @return <code>true</code> if at least one decimal value could be returned 
	 */
	public boolean containsDecimal(NodeBranchDataAdapter adapter) {
		if (isEmpty()) {
			return false;
		}
		else {
			return containsDecimal(adapter, getPaintStart());
		}
	}
}