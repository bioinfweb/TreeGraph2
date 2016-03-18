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
package info.bioinfweb.treegraph.document;


import info.bioinfweb.treegraph.document.format.DistanceDimension;
import info.bioinfweb.treegraph.document.format.GlobalFormats;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.tools.TreeSerializer;
import info.bioinfweb.treegraph.document.undo.CompareTextElementDataParameters;
import info.bioinfweb.treegraph.graphics.positionpaint.PositionPaintType;
import info.bioinfweb.commons.RandomValues;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeMap;



/**
 * Contains a {@link PaintableElement}s of a {@link Document}. These are all elements that are visible in a 
 * tree file with there attached data (including invisible data, like hidden branch data).
 * <p>
 * Parts of the document that are not stored on {@link PaintableElement}s (e.g. default document adapters) 
 * are located directly in {@link Document}. 
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
  private HashSet<PaintableElement> elementSet = new HashSet<PaintableElement>();
	
	
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
	 * Tests if there are tree elements present. Note that it is not tested whether there
	 * are legends present even if no tree elements are.
	 * 
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
   * 
   * @param element the element to search for
   * @return true, if the element is found
   */
  public boolean contains(PaintableElement element) {
  	return elementSet.contains(element);
  }
  
  
	/**
	 * Returns the stored paint dimension (the size of the document) for the given painter 
	 * ID. If none is stored an new empty instance of {@link DistanceDimension} is stored
	 * for this ID and returned.
	 * 
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
	 * 
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
	 * 
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
		
		if (!isEmpty()) {  // Avoid NullPointerException in getElementsInSubtree() if root is null.
			PaintableElement[] elements = 
				  TreeSerializer.getElementsInSubtree(getPaintStart(), NodeType.BOTH, PaintableElement.class);
			for (int i = 0; i < elements.length; i++) {
				elementSet.add(elements[i]);
			}
		}
		
		for (int i = 0; i < getLegends().size(); i++) {
			elementSet.add(legends.get(i));
		}
		elementSet.add(getScaleBar());
	}
	
	
	/**
	 * Searches for a node with the given unique name in this tree.
	 * 
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
	 * Searches this tree for an element that contains the same value as {@code value} in the specified node/branch data column.
	 * 
	 * @param adapter - the node/branch data adapter specifying the column to be compared
	 * @param value - the sought-after value
	 * @param leavesOnly - Specify {@code true} here if only terminal node shall be checked, {@code false} otherwise.
	 * @param parameters - the compare parameter object (If {@code null} is specified here only direct matches are found.)
	 * @return the node containing the sought-after value or {@code null} if no according node has been found.
	 */
	public Node getFirstNodeByData(NodeBranchDataAdapter adapter, double value, boolean leavesOnly, 
			CompareTextElementDataParameters parameters) {
		
		return getFirstNodeByData(adapter, new TextElementData(value), leavesOnly, parameters);
	}
	
	
	/**
	 * Searches this tree for an element that contains the same value as {@code value} in the specified node/branch data column.
	 * 
	 * @param adapter - the node/branch data adapter specifying the column to be compared
	 * @param value - the sought-after value
	 * @param leavesOnly - Specify {@code true} here if only terminal node shall be checked, {@code false} otherwise.
	 * @param parameters - the compare parameter object (If {@code null} is specified here only direct matches are found.)
	 * @return the node containing the sought-after value or {@code null} if no according node has been found.
	 */
	public Node getFirstNodeByData(NodeBranchDataAdapter adapter, String value, boolean leavesOnly,
			CompareTextElementDataParameters parameters) {
				
		return getFirstNodeByData(adapter, new TextElementData(value), leavesOnly, parameters);
	}
	
	
	/**
	 * Searches this tree for an element that contains the same value as {@code data} in the specified node/branch data column.
	 * 
	 * @param adapter - the node/branch data adapter specifying the column to be compared
	 * @param data - the sought-after value
	 * @param leavesOnly - Specify {@code true} here if only terminal node shall be checked, {@code false} otherwise.
	 * @param parameters - the compare parameter object (If {@code null} is specified here only direct matches are found.)
	 * @return the node containing the sought-after value or {@code null} if no according node has been found.
	 */
	public Node getFirstNodeByData(NodeBranchDataAdapter adapter, TextElementData data, boolean leavesOnly,
			CompareTextElementDataParameters parameters) {
			
		return getFirstNodeInSubtreeByData(getPaintStart(), adapter, data, leavesOnly, parameters);
	}
	
	
	/**
	 * Searches an element that contains the same value as {@code data} in the specified node/branch data column
	 * in the subtree under {@code root}.
	 * 
	 * @param root - the root of the subtree to be searched
	 * @param adapter - the node/branch data adapter specifying the column to be compared
	 * @param data - the sought-after value
	 * @param leavesOnly - Specify {@code true} here if only terminal node shall be checked, {@code false} otherwise.
	 * @param parameters - the compare parameter object (If {@code null} is specified here only direct matches are found.)
	 * @return the node containing the sought-after value or {@code null} if no according node has been found.
	 */
	public static Node getFirstNodeInSubtreeByData(Node root, NodeBranchDataAdapter adapter, TextElementData data, 
			boolean leavesOnly, CompareTextElementDataParameters parameters) {
		
		TextElementData rootData = adapter.toTextElementData(root);
		if ((!leavesOnly || root.isLeaf()) && (data.equals(rootData) ||  // First tries to compare without creating new objects
				((parameters != null) && 
						parameters.createEditedValue(data.toString()).equals(parameters.createEditedValue(rootData.toString()))))) {
			
			return root;
		}
		else {
			Iterator<Node> iterator = root.getChildren().iterator();
			while (iterator.hasNext()) {
				Node result = getFirstNodeInSubtreeByData(iterator.next(), adapter, data, leavesOnly, parameters);
				if (result != null) {
					return result;
				}
			}
			return null;
		}
	}
	
	
	/**
	 * Returns the linked node to the given element. The result differs by the type of {@link PaintableElement} as follows:
	 * <ul>
	 *   <li>{@link Node}: The node itself</li>
	 *   <li>{@link Branch}: The target node of this branch</li>
	 *   <li>{@link Label}: The target node of the holding branch</li>
	 *   <li>{@link Legend}: {@code null}</li>
	 * </ul>
	 * 
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
	 * 
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
			Iterator<Node> iterator = root.getChildren().iterator();
			while (iterator.hasNext()) {
				if (!hasAllBranchLengths(iterator.next(), true)) {
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
	 * 
	 * @return <code>true</code>, if there a no necessary lengths missing
	 */
	public boolean hasAllBranchLengths() {
		return hasAllBranchLengths(getPaintStart(), getFormats().getShowRooted());
	}
	
	
	/**
	 * Returns the longest path to a terminal in the subtree of <code>root</code>. All branch lengths of
	 * the subtree under root must be defined except that of the root branch if 
	 * <code>includeRootBranch</code> is <code>false</code>.
	 * 
	 * @param root - the root node of the tree
	 * @param includeRootBranch - indicates whether the length of the afferent branch of the root node
	 *        shall be included
	 * @return the lengths in branch length units or <code>Double.NaN</code> if not all branch lengths 
	 *         are defined
	 */
	public static double longestPath(Node root, boolean includeRootBranch) {
		if (hasAllBranchLengths(root, includeRootBranch)) {
			double result = 0;
			Iterator<Node> iterator = root.getChildren().iterator();
			while (iterator.hasNext()) {
				result = Math.max(result, longestPath(iterator.next(), true));
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
	 * 
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
	 * 
	 * @param adapter - the adapter to obtain the decimal value
	 * @param root - the root of the subtree to be checked
	 * @return <code>true</code> if at least one decimal value could be returned 
	 */
	public static boolean containsDecimal(NodeBranchDataAdapter adapter, Node root) {
		if (adapter.isDecimal(root)) {
			return true;
		}
		Iterator<Node> iterator = root.getChildren().iterator();
		while (iterator.hasNext()) {
			if (containsDecimal(adapter, iterator.next())) {
				return true;
			}
		}
		return false;
	}
	
	
	/**
	 * Tests if the tree contains a node which would be able to return a decimal value
	 * to the given adapter.
	 * 
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