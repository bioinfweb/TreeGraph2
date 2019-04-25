/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2011, 2013-2019  Ben Stöver, Sarah Wiechers, Kai Müller
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


import java.util.Vector;



/**
 * This class stores the path to an node from the root of the tree or a specified ancestor. It can be 
 * used to find equivalent nodes in different trees.
 * 
 * @author Ben St&ouml;ver
 */
public class TreePath {
  private Vector<Integer> path = new Vector<Integer>();
  
  
  /**
   * Creates a path from the specified leaf to the root of the tree.
   * @param leaf
   */
  public TreePath(Node leaf) {
  	super();
  	setPath(leaf, null);
  }


  /**
   * Creates a path from the specified leaf to the specified ancestor (not including the ancestor itself).
   * @param leaf
   * @param ancestor
   * @since 2.0.25
   */
  public TreePath(Node leaf, Node ancestor) {
  	super();
  	setPath(leaf, ancestor);
  }


  private void setPath(Node end, Node ancestor) {
  	path.clear();
  	while ((end.getParent() != null) && (end != ancestor)) {
  		path.add(new Integer(end.getParent().getChildren().indexOf(end)));
  		end = end.getParent();
  	}
  }
  
  
  /**
   * Finds the node specidied by the stored path relative to the given root node.
   * @param root - the start of the path
   * @return the end of the path
   */
  public Node findNode(Node root) {
  	for (int i = path.size() - 1; i >= 0; i--) {  // Pfad wurde in umgekehrter Reihenfolge gespeichert um unn�tige Feldinitialisierungen zu vermeiden.
			if (root.getChildren().size() > path.get(i)) {
				root = root.getChildren().get(path.get(i));
			}
			else {
				return null;
			}
		}
  	return root;
  }
  
  
  /**
   * Calculates which of the two target nodes is further upwards in the tree.
   * @param other - the path that leads to the node that should be compared with the 
   *        target of this path (The given path has to allude to the same tree as the path
   *        contained in this object.)
   * @return -1 if the target of the comapared path is further upwards, 1 if the target of 
   *         this path is further upwards or 0 if both paths lead to the same node
   */
  public int comparePosition(TreePath other) {
  	int pos = 0;
  	while ((pos < path.size()) && (pos < other.path.size())) {
  		if (path.get(pos) < other.path.get(pos)) {
  			return 1;
  		}
  		else if (path.get(pos) > other.path.get(pos)) {
  			return -1;
  		}
  		pos++;
  	}
  	return 0;
  }
 
  
  /**
   * Returns the position in this path on the specified level.
   * @param level - the level in the path where the leaf is on the deepest level (with the highest index)
   * @return
   * @since 2.0.25
   */
  public int getPosition(int level) {
  	return path.get(-(level - length() + 1)); 
  }


  /**
   * Returns the length of the path.
   * @since 2.0.25
   */
	public int length() {
		return path.size();
	}
}