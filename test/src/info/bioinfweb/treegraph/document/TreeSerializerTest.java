/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2016  Ben Stöver, Sarah Wiechers, Kai Müller
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
		
		Node[] internals = TreeSerializer.getElementsInSubtree(tree.getPaintStart(), NodeType.LEAVES, Node.class);
		assertEquals(3, internals.length);
		assertEquals("A", internals[0].getData().getText());
		assertEquals("B", internals[1].getData().getText());
		assertEquals("C", internals[2].getData().getText());
	}
	
	
	@Test
	public void test_getElementsInSubtree_both() {
		Tree tree = createTree();
		
		Node[] internals = TreeSerializer.getElementsInSubtree(tree.getPaintStart(), NodeType.BOTH, Node.class);
		assertEquals(5, internals.length);
		assertEquals("root", internals[0].getData().getText());
		assertEquals("n1", internals[1].getData().getText());
		assertEquals("A", internals[2].getData().getText());
		assertEquals("B", internals[3].getData().getText());
		assertEquals("C", internals[4].getData().getText());
	}
}
