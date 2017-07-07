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
package info.bioinfweb.treegraph.document.metadata;


import org.junit.*;

import info.bioinfweb.jphyloio.formats.xtg.XTGConstants;

import static org.junit.Assert.* ;

import javax.xml.namespace.QName;



public class MetadataTreeTest {
	private static void createPath(MetadataPath path) {
		path.getElementList().add(new MetadataPathElement(XTGConstants.PREDICATE_PIE_CHART_LABEL, 1));
		path.getElementList().add(new MetadataPathElement(XTGConstants.PREDICATE_DATA_IDS, 0));
		path.getElementList().add(new MetadataPathElement(XTGConstants.PREDICATE_DATA_ID, 3));
		path.getElementList().add(new MetadataPathElement(XTGConstants.PREDICATE_DATA_ID_ATTR_PIE_COLOR, 0));
	}
	
	
	private static ResourceMetadataNode assertResourceMetadataNode(QName expectedRel, int expectedChildCount, MetadataNode node) {
		assertTrue(node instanceof ResourceMetadataNode);
		assertEquals(expectedRel, node.getPredicateOrRel());
		ResourceMetadataNode resourceNode = (ResourceMetadataNode)node;
		assertEquals(expectedChildCount, resourceNode.getChildren().size());
		return resourceNode;
	}
	
	
	private static void assertEqualsSearchNodeByPath(MetadataTree tree, MetadataNode result) {
		assertTrue(tree.getChildren().get(0) instanceof ResourceMetadataNode);
		assertResourceMetadataNode(XTGConstants.PREDICATE_PIE_CHART_LABEL, 0, tree.getChildren().get(0));
		ResourceMetadataNode child = assertResourceMetadataNode(XTGConstants.PREDICATE_PIE_CHART_LABEL, 1, tree.getChildren().get(1));
		child = assertResourceMetadataNode(XTGConstants.PREDICATE_DATA_IDS, 4, child.getChildren().get(0));
		assertResourceMetadataNode(XTGConstants.PREDICATE_DATA_ID, 0, child.getChildren().get(0));
		assertResourceMetadataNode(XTGConstants.PREDICATE_DATA_ID, 0, child.getChildren().get(1));
		assertResourceMetadataNode(XTGConstants.PREDICATE_DATA_ID, 0, child.getChildren().get(2));
		child = assertResourceMetadataNode(XTGConstants.PREDICATE_DATA_ID, 1, child.getChildren().get(3));
		
		assertTrue(child.getChildren().get(0) instanceof LiteralMetadataNode);
		assertEquals(XTGConstants.PREDICATE_DATA_ID_ATTR_PIE_COLOR, child.getChildren().get(0).getPredicateOrRel());
		
		assertEquals(result, child.getChildren().get(0));
	}
	
	
	@Test
	public void test_searchNodeByPath_search() {
		MetadataPath path = new MetadataPath(true, true);
		createPath(path);
		
		ResourceMetadataNode resourceMeta = new ResourceMetadataNode(XTGConstants.PREDICATE_PIE_CHART_LABEL);
		ResourceMetadataNode resourceMeta2 = new ResourceMetadataNode(XTGConstants.PREDICATE_PIE_CHART_LABEL);		
		ResourceMetadataNode resourceMeta3 = new ResourceMetadataNode(XTGConstants.PREDICATE_DATA_IDS);
		ResourceMetadataNode resourceMeta4 = new ResourceMetadataNode(XTGConstants.PREDICATE_DATA_ID);
		ResourceMetadataNode resourceMeta5 = new ResourceMetadataNode(XTGConstants.PREDICATE_DATA_ID);
		ResourceMetadataNode resourceMeta6 = new ResourceMetadataNode(XTGConstants.PREDICATE_DATA_ID);
		ResourceMetadataNode resourceMeta7 = new ResourceMetadataNode(XTGConstants.PREDICATE_DATA_ID);
		LiteralMetadataNode literalMeta = new LiteralMetadataNode(XTGConstants.PREDICATE_DATA_ID_ATTR_PIE_COLOR);
		
		MetadataTree tree = new MetadataTree(null);	
		
		resourceMeta7.getChildren().add(literalMeta);
		resourceMeta3.getChildren().add(resourceMeta4);
		resourceMeta3.getChildren().add(resourceMeta5);
		resourceMeta3.getChildren().add(resourceMeta6);
		resourceMeta3.getChildren().add(resourceMeta7);
		resourceMeta2.getChildren().add(resourceMeta3);	
		
		tree.getChildren().add(resourceMeta);
		tree.getChildren().add(resourceMeta2);		
		
		MetadataNode result = tree.searchAndCreateNodeByPath(path, false);
		
		
		assertEquals(2, tree.getChildren().size());		
		assertEqualsSearchNodeByPath(tree, result);
	}
	
		
	@Test
	public void test_searchNodeByPath_create() {
		MetadataPath path = new MetadataPath(true, true);
		createPath(path);
		
		MetadataTree tree = new MetadataTree(null);		
		MetadataNode result = tree.searchAndCreateNodeByPath(path, true);
		

		assertEquals(2, tree.getChildren().size());		
		assertEqualsSearchNodeByPath(tree, result);
	}
	
	
	@Test
	public void test_searchNodeByPath_searchNonEmpty() {
		MetadataPath path = new MetadataPath(true, true);
		createPath(path);
		
		ResourceMetadataNode resourceMeta = new ResourceMetadataNode(XTGConstants.PREDICATE_PIE_CHART_LABEL);
		ResourceMetadataNode resourceMeta1 = new ResourceMetadataNode(XTGConstants.PREDICATE_PIE_CHART_LABEL);	
		ResourceMetadataNode resourceMeta2 = new ResourceMetadataNode(XTGConstants.PREDICATE_DATA_IDS);
		ResourceMetadataNode resourceMeta3 = new ResourceMetadataNode(XTGConstants.PREDICATE_DATA_ID);
		ResourceMetadataNode resourceMeta4 = new ResourceMetadataNode(XTGConstants.PREDICATE_DATA_ID);
		
		MetadataTree tree = new MetadataTree(null);		

		resourceMeta2.getChildren().add(resourceMeta3);
		resourceMeta2.getChildren().add(resourceMeta4);
		resourceMeta1.getChildren().add(resourceMeta2);
		tree.getChildren().add(resourceMeta);
		tree.getChildren().add(resourceMeta1);
		
		MetadataNode result = tree.searchAndCreateNodeByPath(path, true);
		
		
		assertEquals(2, tree.getChildren().size());		
		assertEqualsSearchNodeByPath(tree, result);
	}
}
