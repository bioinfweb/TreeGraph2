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
package info.bioinfweb.treegraph.document.tools;


import info.bioinfweb.treegraph.document.ConcretePaintableElement;
import info.bioinfweb.treegraph.document.Label;
import info.bioinfweb.treegraph.document.Labels;
import info.bioinfweb.treegraph.document.Legend;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.NodeType;
import info.bioinfweb.treegraph.document.PaintableElement;
import info.bioinfweb.treegraph.document.TextElementData;
import info.bioinfweb.treegraph.document.Tree;
import info.bioinfweb.treegraph.document.TreePath;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.TextElementDataAdapter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;



/**
 * Utility class that is able to serialize tree elements in several ways.
 * 
 * @author Ben St&ouml;ver
 */
public class TreeSerializer {
	//TODO Serializing large trees could be done by returning iterators instead of collections, to avoid storing references to each element.
	
	
	private static void addLabelBlock(Collection<PaintableElement> list, Labels labels, boolean above) {
		for (int lineNo = 0; lineNo < labels.lineCount(above); lineNo++) {
			for (int lineIndex = 0; lineIndex < labels.labelCount(above, lineNo); lineIndex++) {
				list.add(labels.get(above, lineNo, lineIndex));
			}
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public static <T extends PaintableElement> void addElementsOnNode(Collection<T> list, Node node, Class<? extends T> c) {
  	if (c.isInstance(node)) {
  		list.add((T)node);  // Conversion is checked.
  	}
  	if (c.isInstance(node.getAfferentBranch())) {
  		list.add((T)node.getAfferentBranch());  // Conversion is checked.
  	}
  	
    Label[] labels = node.getAfferentBranch().getLabels().toLabelArray();
    for (int i = 0; i < labels.length; i++) {
    	if (c.isInstance(labels[i])) {
    		list.add((T)labels[i]);  // Conversion is checked.
    	}
		}
	}
	
	
  /**
   * Returns an array of tree elements linked to the specified node. (No elements from the subtree are included.)
   * 
   * @param <T> the type of the array to be returned
   * @param node the node to which the returned elements are connected
   * @param c the class defining which elements should be returned
   * @param array the array to store the result in (defines the return type and is recreated if it is too small)
   * @return the array of tree elements
   * @since 2.0.43
   */
  public static <T extends PaintableElement> T[] getElementsOnNode(Node node, Class<T> c,	T[] array) {
  	List<T> list = new ArrayList<T>();
  	addElementsOnNode(list, node, c);
    return list.toArray(array);
  }
	
  
  /**
   * Returns an array of tree elements linked to the specified node. (No elements from the subtree are included.)
   * <p>
   * Calling this method is equivalent to <code>getElementsOnNode(node, c, (T[])Array.newInstance(c, 0))</code>.
   * 
   * @param <T> - the type of the array to be returned
   * @param node - the node to which the returned elements are connected
   * @param c - the class defining which elements should be returned
   * @return the array of tree elements
   * @since 2.0.43
   */
  @SuppressWarnings("unchecked")
	public static <T extends PaintableElement> T[] getElementsOnNode(Node node, Class<T> c) {
  	return getElementsOnNode(node, c, (T[])Array.newInstance(c, 0));
  }
  
  
  public static <T extends PaintableElement> void addElementsInSubtree(Collection<T> list, Node root, NodeType nodeType,
  		Class<? extends T> elementClass) {

  	if ((root.isLeaf() && !nodeType.equals(NodeType.INTERNAL_NODES)) || (!root.isLeaf() && !nodeType.equals(NodeType.LEAVES))) {
			addElementsOnNode(list, root, elementClass);
		}  	
		for (int i = 0; i < root.getChildren().size(); i++) {
			addElementsInSubtree(list, root.getChildren().get(i), nodeType, elementClass);
		}
	}
	
	
  /**
   * Returns an array of tree elements in the subtree under {@code root}. The elements are returns in pre-order.
   * 
   * @param <T> - the type of the array to be returned
   * @param root - the node to which the returned elements are connected
   * @param leavesOnly - determines whether only elements attached to leaf nodes (or the leaf nodes themselves) shall
   *        be included
   * @param elementClass - the class defining which elements should be returned
   * @param array - the array to store the result in (defines the return type and is recreated if it is too small)
   * @return the array of tree elements
   * @since 2.0.43
   */
  public static <T extends PaintableElement> T[] getElementsInSubtree(Node root, NodeType nodeType, Class<? extends T> elementClass, T[] array) {
  	return getElementsInSubtreeAsList(root, nodeType, elementClass).toArray(array);
  }
  
  
  /**
   * Returns a list of tree elements in the subtree under {@code root}. The elements are returns in pre-order.
   * 
   * @param <T> the type of the array to be returned
   * @param root the node to which the returned elements are connected
   * @param leavesOnly determines whether only elements attached to leaf nodes (or the leaf nodes themselves) shall
   *        be included
   * @param elementClass the class defining which elements should be returned
   * @param array the array to store the result in (defines the return type and is recreated if it is too small)
   * @return a list of tree elements
   * @since 2.2.0
   */

  public static <T extends PaintableElement> List<T> getElementsInSubtreeAsList(Node root, NodeType nodeType, Class<? extends T> elementClass) {  	
  	List<T> list = new ArrayList<T>();
  	addElementsInSubtree(list, root, nodeType, elementClass);
  	return list;
  }
  
  	  
  /**
   * Returns an array of tree elements in the subtree under {@code root}. The elements are returnes in pre-order.
   * <p>
   * Calling this method is equivalent to 
   * {@code getElementsInSubtree(root, leavesOnly, elementClass, (T[])Array.newInstance(elementClass, 0))}.
   * 
   * @param <T> the type of the array to be returned
   * @param root the node to which the returned elements are connected
   * @param leavesOnly determines whether only elements attached to leaf nodes (or the leaf nodes themselves) shall
   *        be included
   * @param elementClass the class defining which elements should be returned
   * @param array the array to store the result in (defines the return type and is recreated if it is too small)
   * @return the array of tree elements
   * @since 2.0.43
   */
  @SuppressWarnings("unchecked")
	public static <T extends PaintableElement> T[] getElementsInSubtree(Node root, NodeType nodeType, Class<T> elementClass) {
  	return getElementsInSubtree(root, nodeType, elementClass, (T[])Array.newInstance(elementClass, 0));
  }
  
  
  private static void addLeafNodesBetween(Collection<Node> list, Node root, int level, TreePath upperPath, 
  		TreePath lowerPath) {
  	
  	if (root.isLeaf()) {
  		list.add(root);
  	}
  	else {
  		int start = 0;
  		int end = root.getChildren().size() - 1;
  		if (upperPath != null) {
  			start = upperPath.getPosition(level);
  		}
  		if (lowerPath != null) {
  			end = lowerPath.getPosition(level);
  		}
  		
  		for (int i = start; i <= end; i++) {
  			TreePath currentUpperPath = null;
  			if (i == start) {
  				currentUpperPath = upperPath;
  			}
  			TreePath currentLowerPath = null;
  			if (i == end) {
  				currentLowerPath = lowerPath;
  			}
				addLeafNodesBetween(list, root.getChildren().get(i), level + 1, currentUpperPath, currentLowerPath);
			}
  	}
  }
  
  
  public static Node[] getLeafNodesBetween(Node upperLeaf, Node lowerLeaf) {
  	Node ancestor = Tree.mostRecentCommonAncestor(upperLeaf, lowerLeaf);
  	if (ancestor == null) {
  		throw new IllegalArgumentException("The specified nodes are not part of the same tree.");
  	}
  	else {
	  	List<Node> list = new ArrayList<Node>();
	  	TreePath upperPath = new TreePath(upperLeaf, ancestor);
	  	TreePath lowerPath = new TreePath(lowerLeaf, ancestor);
	  	addLeafNodesBetween(list, ancestor, 0, upperPath, lowerPath);
	  	return list.toArray(new Node[list.size()]);
  	}
  }
  
  
  private static void getLabelsWithIDInBlock(Collection<Label> list, Labels labels, 
  		boolean above, String id) {
  	
		for (int lineNo = 0; lineNo < labels.lineCount(above); lineNo++) {
			for (int lineIndex = 0; lineIndex < labels.labelCount(above, lineNo); lineIndex++) {
				Label label = labels.get(above, lineNo, lineIndex);
				if (label.getID().equals(id) ) {
					list.add(label);
				}
			}
		}
  }
  
  
  private static void addLabelsWithID(Collection<Label> list, Node root, String id) {
  	if (root.hasAfferentBranch()) {
			Labels labels = root.getAfferentBranch().getLabels();
			getLabelsWithIDInBlock(list, labels, true, id);
			getLabelsWithIDInBlock(list, labels, false, id);
  	}
  	
		for (int i = 0; i < root.getChildren().size(); i++) {
			addLabelsWithID(list, root.getChildren().get(i), id);
		}
  }
  
  
  public static Label[] getLabelsWithID(Node root, String id) {
  	List<Label> list = new ArrayList<Label>();
  	addLabelsWithID(list, root, id);
  	return list.toArray(new Label[list.size()]);
  }
  
  
  /**
   * Returns an array of all legends which are completely anchored inside the subtree under 
   * <code>root</code>.
   * 
   * @param tree - the tree that contains the legends and <code>root</code>
   * @param root - the root node of the subtree
   * @throws IllegalArgumentException if <code>root</code> is not contained in <code>tree</code>.
   * @return an array of the legends (possibly with the length 0)
   * @see Node#containedInSubtree(ConcretePaintableElement)
   */
  public static Legend[] getLegendsInSubtree(Tree tree, Node root) {
  	if (!tree.contains(root)) {
  		throw new IllegalArgumentException("The specified root is not contained in the specified tree.");
  	}
  	else {
  		List<Legend> result = new ArrayList<Legend>();
  		for (int i = 0; i < tree.getLegends().size(); i++) {
  			Legend l = tree.getLegends().get(i);
    		if (root.containedInSubtree(l)) {
    			result.add(l);
    		}
			}
  		return result.toArray(new Legend[result.size()]);
  	}
  }

  
	/**
	 * Adds all text element data in the subtree under {@code root} returned by the specified adapter to the specified collection.
	 * The collection will be filled with references to the data objects.
	 * 
	 * @param collection the collection to add the elements to
	 * @param root the root node of the subtree to be processed
	 * @param nodeType the type of nodes to be taken into account
	 * @param adapter the adapter provide the text element data references
	 * @param ignoredElements a collection of elements that shall not be added to {@code collection} if they are encountered
	 *        (Maybe {@code null} if no elements shall be ignored.)
	 * @return a reference to {@code collection}
	 * @since 2.9.0
	 * @see #addTextElementDataCopiesFromSubtree(Collection, Node, NodeType, NodeBranchDataAdapter, Collection)
	 */
	public static <C extends Collection<TextElementData>> C addTextElementDataFromSubtree(C collection, Node root, NodeType nodeType, 
			TextElementDataAdapter adapter, Collection<TextElementData> ignoredElements) {
		
		if ((root.isLeaf() && !nodeType.equals(NodeType.INTERNAL_NODES)) || (!root.isLeaf() && !nodeType.equals(NodeType.LEAVES))) {
			TextElementData data = adapter.getData(root);
			if ((ignoredElements == null) || !ignoredElements.contains(data)) {
				collection.add(data);
			}
		}  	
		for (Node child : root.getChildren()) {
			addTextElementDataFromSubtree(collection, child, nodeType, adapter, ignoredElements);
		}
		return collection;
	}

  
	/**
	 * Adds all text element data in the subtree under {@code root} returned by the specified adapter to the specified collection.
	 * The collection will be filled with copies of the data objects.
	 * 
	 * @param collection the collection to add the elements to
	 * @param root the root node of the subtree to be processed
	 * @param nodeType the type of nodes to be taken into account
	 * @param adapter the adapter provide the text element data copies
	 * @param ignoredElements a collection of elements that shall not be added to {@code collection} if they are encountered
	 *        (Maybe {@code null} if no elements shall be ignored.)
	 * @return a reference to {@code collection}
	 * @since 2.9.0
	 * @see #addTextElementDataFromSubtree(Collection, Node, NodeType, TextElementDataAdapter, Collection)
	 */
	public static <C extends Collection<TextElementData>> C addTextElementDataCopiesFromSubtree(C collection, Node root, NodeType nodeType, 
			NodeBranchDataAdapter adapter, Collection<TextElementData> ignoredElements) {
		
		if ((root.isLeaf() && !nodeType.equals(NodeType.INTERNAL_NODES)) || (!root.isLeaf() && !nodeType.equals(NodeType.LEAVES))) {
			TextElementData data = adapter.toTextElementData(root);  // Creates a copy of the data object.
			if ((ignoredElements == null) || !ignoredElements.contains(data)) {
				collection.add(data);
			}
		}  	
		for (Node child : root.getChildren()) {
			addTextElementDataCopiesFromSubtree(collection, child, nodeType, adapter, ignoredElements);
		}
		return collection;
	}
}