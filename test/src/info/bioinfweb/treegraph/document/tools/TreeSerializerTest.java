/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2011, 2013-2017  Ben Stöver, Sarah Wiechers, Kai Müller
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


import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.NodeType;
import info.bioinfweb.treegraph.document.TextElementData;
import info.bioinfweb.treegraph.document.Tree;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeNameAdapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.* ;

import static org.junit.Assert.* ;



public class TreeSerializerTest {
	private Node createNode(Node parent, String name) {
		Node result = Node.newInstanceWithBranch();
		result.getData().setText(name);
		result.setParent(parent);
		if (parent != null) {
			parent.getChildren().add(result);
		}
		return result;
	}
	
	
	private Tree createTree() {
		Tree result = new Tree();
		result.setPaintStart(createNode(null, "root"));
		Node n1 = createNode(result.getPaintStart(), "n1");
		createNode(n1, "A");
		createNode(n1, "B");
		createNode(result.getPaintStart(), "C");
		return result;
	}
	
	
	@Test
	public void test_getElementsInSubtree_internals() {
		Tree tree = createTree();
		
		Node[] internals = TreeSerializer.getElementsInSubtree(tree.getPaintStart(), NodeType.INTERNAL_NODES, Node.class);
		assertEquals(2, internals.length);
		assertEquals("root", internals[0].getData().getText());
		assertEquals("n1", internals[1].getData().getText());
	}
	
	
	@Test
	public void test_getElementsInSubtree_leaves() {
		Tree tree = createTree();
		
		Node[] leaves = TreeSerializer.getElementsInSubtree(tree.getPaintStart(), NodeType.LEAVES, Node.class);
		assertEquals(3, leaves.length);
		assertEquals("A", leaves[0].getData().getText());
		assertEquals("B", leaves[1].getData().getText());
		assertEquals("C", leaves[2].getData().getText());
	}
	
	
	@Test
	public void test_getElementsInSubtree_both() {
		Tree tree = createTree();
		
		Node[] nodes = TreeSerializer.getElementsInSubtree(tree.getPaintStart(), NodeType.BOTH, Node.class);
		assertEquals(5, nodes.length);
		assertEquals("root", nodes[0].getData().getText());
		assertEquals("n1", nodes[1].getData().getText());
		assertEquals("A", nodes[2].getData().getText());
		assertEquals("B", nodes[3].getData().getText());
		assertEquals("C", nodes[4].getData().getText());
	}
	
	
	@Test
	public void test_addTextElementDataFromSubtree_both() {
		Tree tree = createTree();
		
		Set<TextElementData> ignoredElements = new TreeSet<TextElementData>();
		ignoredElements.add(new TextElementData("n1"));
		ignoredElements.add(new TextElementData("B"));
		
		List<TextElementData> list = TreeSerializer.addTextElementDataFromSubtree(new ArrayList<TextElementData>(), tree.getPaintStart(), 
				NodeType.BOTH, new NodeNameAdapter(), ignoredElements);
		
		assertEquals(3, list.size());
		Iterator<TextElementData> iterator = list.iterator();
		assertEquals("root", iterator.next().getText());
		assertEquals("A", iterator.next().getText());
		assertEquals("C", iterator.next().getText());
	}
	
	
	@Test
	public void test_addTextElementDataFromSubtree_internals() {
		Tree tree = createTree();
		
		Set<TextElementData> ignoredElements = new TreeSet<TextElementData>();
		ignoredElements.add(new TextElementData("n1"));
		ignoredElements.add(new TextElementData("B"));
		
		List<TextElementData> list = TreeSerializer.addTextElementDataFromSubtree(new ArrayList<TextElementData>(), tree.getPaintStart(), 
				NodeType.INTERNAL_NODES, new NodeNameAdapter(), ignoredElements);
		
		assertEquals(1, list.size());
		Iterator<TextElementData> iterator = list.iterator();
		assertEquals("root", iterator.next().getText());
	}
	
	
	@Test
	public void test_addTextElementDataFromSubtree_leaves() {
		Tree tree = createTree();
		
		Set<TextElementData> ignoredElements = new TreeSet<TextElementData>();
		ignoredElements.add(new TextElementData("n1"));
		ignoredElements.add(new TextElementData("B"));
		
		List<TextElementData> list = TreeSerializer.addTextElementDataFromSubtree(new ArrayList<TextElementData>(), tree.getPaintStart(), 
				NodeType.LEAVES, new NodeNameAdapter(), ignoredElements);
		
		assertEquals(2, list.size());
		Iterator<TextElementData> iterator = list.iterator();
		assertEquals("A", iterator.next().getText());
		assertEquals("C", iterator.next().getText());
	}
	
	
	@Test
	public void test_addTextElementDataFromSubtree_noIgnored() {
		Tree tree = createTree();
		
		List<TextElementData> list = TreeSerializer.addTextElementDataFromSubtree(new ArrayList<TextElementData>(), tree.getPaintStart(), 
				NodeType.BOTH, new NodeNameAdapter(), null);
		
		assertEquals(5, list.size());
		Iterator<TextElementData> iterator = list.iterator();
		assertEquals("root", iterator.next().getText());
		assertEquals("n1", iterator.next().getText());
		assertEquals("A", iterator.next().getText());
		assertEquals("B", iterator.next().getText());
		assertEquals("C", iterator.next().getText());
	}
}
