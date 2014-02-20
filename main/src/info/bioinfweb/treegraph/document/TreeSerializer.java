/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2014  Ben Stöver, Kai Müller
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


import java.lang.reflect.Array;
import java.util.List;
import java.util.Vector;



/**
 * Utility class that is able to serialize tree elements in several ways.
 * @author Ben St&ouml;ver
 */
public class TreeSerializer {
	private static void addLabelBlock(Vector<PaintableElement> list, Labels labels, boolean above) {
		
		for (int lineNo = 0; lineNo < labels.lineCount(above); lineNo++) {
			for (int lineIndex = 0; lineIndex < labels.labelCount(above, lineNo); lineIndex++) {
				list.add(labels.get(above, lineNo, lineIndex));
			}
		}
	}
	
	
	private static <T extends PaintableElement> void addElementsOnNode(List<PaintableElement> list, Node node, 
			Class<T> c) {
  	
  	if (c.isInstance(node)) {
  		list.add(node);
  	}
  	if (c.isInstance(node.getAfferentBranch())) {
  		list.add(node.getAfferentBranch());
  	}
  	
    Label[] labels = node.getAfferentBranch().getLabels().toLabelArray();
    for (int i = 0; i < labels.length; i++) {
    	if (c.isInstance(labels[i])) {
    		list.add(labels[i]);
    	}
		}
	}
	
	
  /**
   * Returns an array of tree elements linked to the specified node. (No elements from the subtree are included.)
   * @param <T> - the type of the array to be returned
   * @param node - the node to which the returned elements are connected
   * @param c - the class defining which elements should be returned
   * @param array - the array to store the result in (defines the return type and is recreated if it is too small)
   * @return the array of tree elements
   * @since 2.0.43
   */
  public static <T extends PaintableElement> T[] getElementsOnNode(Node node, Class<? extends PaintableElement> c,
  		T[] array) {
  	
  	Vector<PaintableElement> list = new Vector<PaintableElement>();
  	addElementsOnNode(list, node, c);
    return list.toArray(array);
  }
	
  
  /**
   * Returns an array of tree elements linked to the specified node. (No elements from the subtree are included.)<br />
   * Calling this method is equivalent to <code>getElementsOnNode(node, c, (T[])Array.newInstance(c, 0))</code>. 
   * @param <T> - the type of the array to be returned
   * @param node - the node to which the returned elements are connected
   * @param c - the class defining which elements should be returned
   * @return the array of tree elements
   * @since 2.0.43
   */
  public static <T extends PaintableElement> T[] getElementsOnNode(Node node, Class<T> c) {
  	return getElementsOnNode(node, c, (T[])Array.newInstance(c, 0));
  }
  
  
  private static void addSubtree(Vector<PaintableElement> list, Node root, boolean leafsOnly,
  		Class<? extends PaintableElement> elementClass) {
  	
		if (root.isLeaf() || !leafsOnly) {
			addElementsOnNode(list, root, elementClass);
		}
  	
		for (int i = 0; i < root.getChildren().size(); i++) {
			addSubtree(list, root.getChildren().get(i), leafsOnly, elementClass);
		}
	}
	
	
  /**
   * Returns an array of tree elements in the subtree under <code>root</code>
   * @param <T> - the type of the array to be returned
   * @param node - the node to which the returned elements are connected
   * @param c - the class defining which elements should be returned
   * @param array - the array to store the result in (defines the return type and is recreated if it is too small)
   * @return the array of tree elements
   * @since 2.0.43
   */
  public static <T extends PaintableElement> T[] getElementsInSubtree(Node root, boolean leafsOnly, 
  		Class<? extends PaintableElement> elementClass, T[] array) {
  	
  	Vector<PaintableElement> list = new Vector<PaintableElement>();
  	addSubtree(list, root, leafsOnly, elementClass);
  	return list.toArray(array);
  }
  
  
  /**
   * Returns an array of tree elements in the subtree under <code>root</code>
   * Calling this method is equivalent to 
   * <code>getElementsInSubtree(root, leafsOnly, elementClass, (T[])Array.newInstance(elementClass, 0))</code>.
   * 
   * @param <T> - the type of the array to be returned
   * @param node - the node to which the returned elements are connected
   * @param c - the class defining which elements should be returned
   * @param array - the array to store the result in (defines the return type and is recreated if it is too small)
   * @return the array of tree elements
   * @since 2.0.43
   */
  public static <T extends PaintableElement> T[] getElementsInSubtree(Node root, boolean leafsOnly, 
  		Class<T> elementClass) {
  	
  	return getElementsInSubtree(root, leafsOnly, elementClass, (T[])Array.newInstance(elementClass, 0));
  }
  
  
  private static void addLeafNodesBetween(Vector<Node> list, Node root, int level, TreePath upperPath, 
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
	  	Vector<Node> list = new Vector<Node>();
	  	TreePath upperPath = new TreePath(upperLeaf, ancestor);
	  	TreePath lowerPath = new TreePath(lowerLeaf, ancestor);
	  	addLeafNodesBetween(list, ancestor, 0, upperPath, lowerPath);
	  	return list.toArray(new Node[list.size()]);
  	}
  }
  
  
  private static void getLabelsWithIDInBlock(Vector<Label> list, Labels labels, 
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
  
  
  private static void addLabelsWithID(Vector<Label> list, Node root, String id) {
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
  	Vector<Label> list = new Vector<Label>();
  	addLabelsWithID(list, root, id);
  	return list.toArray(new Label[list.size()]);
  }
  
  
  /**
   * Returns an array of all legends which are completely anchored inside the subtree under 
   * <code>root</code>.
   * @param tree - the tree that contains the legends and <code>root</code>
   * @param root - the root node of the subtree
   * @throws IllegalArgumentException if <code>root</code> is not contained in <code>tree</code>.
   * @return an array of the legends (possibly with the length 0)
   * @see Node#containedInSubtree(ConcretePaintableElement)
   */
  public static Legend[] getLegendsInSubtree(Tree tree, Node root) {
  	if (!tree.contains(root)) {
  		throw new IllegalArgumentException("The specified root is not contained in the " +
  				"specified tree.");
  	}
  	else {
  		List<Legend> result = new Vector<Legend>();
  		for (int i = 0; i < tree.getLegends().size(); i++) {
  			Legend l = tree.getLegends().get(i);
    		if (root.containedInSubtree(l)) {
    			result.add(l);
    		}
			}
  		return result.toArray(new Legend[result.size()]);
  	}
  }
}