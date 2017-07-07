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


import static org.junit.Assert.*;

import java.util.List;

import org.junit.*;

import info.bioinfweb.jphyloio.formats.xtg.XTGConstants;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.NodeType;
import info.bioinfweb.treegraph.document.metadata.MetadataPath;
import info.bioinfweb.treegraph.document.metadata.MetadataPathElement;
import info.bioinfweb.treegraph.document.metadata.MetadataTree;
import info.bioinfweb.treegraph.document.metadata.ResourceMetadataNode;



public class PathManagerTest {
	private static MetadataTree createTree() {
		Node parent = Node.newInstanceWithBranch();
		MetadataPath path = new MetadataPath(true, false);
		MetadataTree tree = new MetadataTree(parent);
		
		path.getElementList().add(new MetadataPathElement(XTGConstants.PREDICATE_PIE_CHART_LABEL, 1));
		path.getElementList().add(new MetadataPathElement(XTGConstants.PREDICATE_DATA_IDS, 0));
		path.getElementList().add(new MetadataPathElement(XTGConstants.PREDICATE_DATA_ID, 3));
		path.getElementList().add(new MetadataPathElement(XTGConstants.PREDICATE_DATA_ID_ATTR_PIE_COLOR, 0));
		
		tree.searchAndCreateNodeByPath(path, true);
		
		return tree;
	}
	
	

	@Test
	public void test_createPathList_node() {
		NodeType nodeType = NodeType.LEAVES;
		MetadataTree tree = createTree();
		
		List<MetadataPath> pathList = PathManager.createPathList(tree, nodeType);
		
		//TODO Continue JUnit test.
		assertTrue(pathList.get(0) != null);
		assertTrue(pathList.get(0) instanceof MetadataPath);
		for (MetadataPath child : pathList) {
		}

	}
}
