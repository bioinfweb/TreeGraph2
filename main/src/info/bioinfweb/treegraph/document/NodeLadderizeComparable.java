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


import java.util.Vector;



/**
 * This class is a wrapper class for <code>Node</code>-objects that allows them to be
 * compared by their number of terminal subnodes. It is used to ladderize a tree.
 * 
 * @author Ben St&ouml;ver
 */
public class NodeLadderizeComparable implements Comparable<NodeLadderizeComparable>{
	public static final String CHILD_COUNT_KEY = "ladderizeChildCount";
	
	
  private Node node = null;
  private boolean ladderizeDown = true;
  
  
	public NodeLadderizeComparable(Node node, boolean ladderizeDown) {
		super();
		this.node = node;
		this.ladderizeDown = ladderizeDown;
	}


	public boolean isLadderizeDown() {
		return ladderizeDown;
	}


	public Node getNode() {
		return node;
	}


	/**
	 * Compares which node has more (or less) nodes in its subtree. Note that
	 * {@link countNodes()} has to be called first.
	 * 
	 * @param other - an other NodeLadderizeComparable that should be compared
	 * @see info.bioinfweb.treegraph.document.Node#getChildCount()
	 * @see info.bioinfweb.treegraph.document.NodeLadderizeComparable#countNodes()
	 */
	public int compareTo(NodeLadderizeComparable other) {
		int result = (Integer)node.getAttributeMap().get(CHILD_COUNT_KEY) - (Integer)other.getNode().getAttributeMap().get(CHILD_COUNT_KEY);
		if (!ladderizeDown) {
			result *= -1;
		}
		return result;
	}
	
	
	/**
	 * Incloses all direct subelements of {@code root} with a {@link NodeLadderizeComparable}
	 * object and adds this objects to an array that is returned.
	 * 
	 * @param root - the parent element of the subelements to enclose
	 * @param ladderizeDown - whether the elements will be ladderized down
	 * @return the array of <code>NodeLadderizeComparable</code>-objects
	 */
	public static NodeLadderizeComparable[] incloseSubnodes(Node root, boolean ladderizeDown) {
		Vector<NodeLadderizeComparable> list = new Vector<NodeLadderizeComparable>();
		for (int i = 0; i < root.getChildren().size(); i++) {
			list.add(new NodeLadderizeComparable(root.getChildren().get(i), ladderizeDown));
		}
		return list.toArray(new NodeLadderizeComparable[list.size()]);
	}
	
	
	/**
	 * This method is used to reinsert sorted elements in the a subtree.
	 * 
	 * @param elements - the elements to insert
	 * @param root - the future parent element of the elements to insert
	 */
	public static void addToNode(NodeLadderizeComparable[] elements, Node root) {
		for (int i = 0; i < elements.length; i++) {
			root.getChildren().add(elements[i].getNode());
		}
	}

	
	/**
	 * Sets all the <code>childCount</code> values of the contained <code>Node</code>-objects.
	 * This method is used to perform ladderization.
	 * 
	 * @return the count of nodes in the subtree under <code>root</code>
	 * @see info.bioinfweb.treegraph.document.Node#getChildCount()
	 * @see info.bioinfweb.treegraph.document.undo.edit.LadderizeEdit
	 * @see info.bioinfweb.treegraph.gui.actions.edit.LadderizeAction
	 */
	public static int countNodes(Node root) {
		int result = 0;
		for (int i = 0; i < root.getChildren().size(); i++) {
			result += countNodes(root.getChildren().get(i));
		}
		
		result += root.getChildren().size();
		root.getAttributeMap().put(CHILD_COUNT_KEY, new Integer(result));
		
		return result;
	}
}