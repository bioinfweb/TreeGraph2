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


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.NodeType;
import info.bioinfweb.treegraph.document.metadata.LiteralMetadataNode;
import info.bioinfweb.treegraph.document.metadata.MetadataNode;
import info.bioinfweb.treegraph.document.metadata.MetadataPath;
import info.bioinfweb.treegraph.document.metadata.MetadataPathElement;
import info.bioinfweb.treegraph.document.metadata.MetadataTree;
import info.bioinfweb.treegraph.document.metadata.ResourceMetadataNode;
import info.bioinfweb.treegraph.document.nodebranchdata.LiteralMetadataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.ResourceMetadataAdapter;



public class PathManager {
	/**
	 * Creates the combined metadata tree from all node and branch metadata trees on the specified phylogenetic node and its children.
	 * 
	 * @param root
	 * @param nodeType
	 */
	public static MetadataTree createCombinedMetadataTreeFromNodes(Node root, NodeType nodeType) {
		Node node = Node.newInstanceWithBranch();  //TODO Mention this in documentation.
		MetadataTree result = node.getMetadataTree();
		addToCombinedMetadataTree(root, nodeType, result, true);
		return result;
	}
	
	
	public static MetadataTree createCombinedMetadataTreeFromBranches(Node root, NodeType nodeType) {
		Node node = Node.newInstanceWithBranch();  //TODO Mention this in documentation.
		MetadataTree result = node.getAfferentBranch().getMetadataTree();
		addToCombinedMetadataTree(root, nodeType, result, false);
		return result;
	}
	
	
	private static void addToCombinedMetadataTree(Node root, NodeType nodeType, MetadataTree tree, boolean useNodeData) {		
		List<MetadataPath> pathList = createPathList(root, nodeType, useNodeData, !useNodeData);
		
		for (MetadataPath child : pathList) {			
			tree.searchAndCreateNodeByPath(child, true); //Create paths in tree.
		}
		if (root != null) {
			for (Node child : root.getChildren()) {
				addToCombinedMetadataTree(child, nodeType, tree, useNodeData);
			}
		}
	}
		
	
	//Create combined branch and node trees
	//Create path list from both trees
	//Create adapter list from path list (Concrete instance depends on path.)
	public static List<NodeBranchDataAdapter> createAdapterList(Node root, NodeType nodeType) {
		List<NodeBranchDataAdapter> result = new ArrayList<NodeBranchDataAdapter>();
		
		MetadataTree nodeTree = createCombinedMetadataTreeFromNodes(root, nodeType);
		MetadataTree branchTree = createCombinedMetadataTreeFromBranches(root, nodeType);
		
		List<MetadataPath> pathList = createPathList(nodeTree, nodeType);
		pathList.addAll(createPathList(branchTree, nodeType));
		
		for (MetadataPath path : pathList) {			
			boolean isLiteral = path.isLiteral();
			if(isLiteral) {
				result.add(new LiteralMetadataAdapter(path));
			}
			else {
				result.add(new ResourceMetadataAdapter(path));
			}
		}
		return result;
	}
		
		
	public static List<MetadataPath> createPathList(Node root, NodeType nodeType, boolean includeNodeData, boolean includeBranchData) {
		List<MetadataPath> result = new ArrayList<MetadataPath>();
		if (root != null) {
			if (includeNodeData) {
				fillPathList(root.getMetadataTree().getChildren(), true, nodeType, Collections.<MetadataPathElement>emptyList(), result);
			}
			if (includeBranchData) {
//				fillPathList(root.getAfferentBranch().getMetadataTree().getChildren(), false, nodeType, Collections.<MetadataPathElement>emptyList(), result);
				fillPathList(root.getAfferentBranch().getMetadataTree().getChildren(), false, nodeType, new ArrayList<MetadataPathElement>(), result);
			}
		}
		return result;
	}
	
	
	public static List<MetadataPath> createPathList(MetadataTree tree, NodeType nodeType) {
		List<MetadataPath> result = new ArrayList<MetadataPath>();
//		fillPathList(tree.getChildren(), tree.getParent() instanceof Node, nodeType, Collections.<MetadataPathElement>emptyList(), result);
		fillPathList(tree.getChildren(), tree.getParent() instanceof Node, nodeType, new ArrayList<MetadataPathElement>(), result);
		return result;
	}
	
	
	private static void addToMap(Map<QName, Integer> map, QName predicate) {
		if (!map.containsKey(predicate)) {
			map.put(predicate, 0);
		}
		else {
			map.put(predicate, map.get(predicate) + 1);
		}
	}
	
	
	private static void createNewPathList(List<MetadataPathElement> parentPathElements, List<MetadataPath> resultList,
			Map<QName, Integer> map, QName predicate, MetadataPath path) {
		
		//TODO Does parentPathElements need to be copied to a new List? Can't it just add element itself?
		MetadataPathElement element = new MetadataPathElement(predicate, map.get(predicate));
		List<MetadataPathElement> copyList = new ArrayList<MetadataPathElement>();
		copyList.addAll(parentPathElements);
		
		copyList.add(element);					
		path.getElementList().addAll(copyList);
		resultList.add(path);
	}	
	
	
	private static void fillPathList(List<MetadataNode> currentChildList, boolean isNode, NodeType nodeType, List<MetadataPathElement> parentPathElements, 
			List<MetadataPath> resultList) {
		
		Map<QName, Integer> resourceMap = new HashMap<QName, Integer>();
		Map<QName, Integer> literalMap = new HashMap<QName, Integer>();		

		for (MetadataNode metadataNode : currentChildList) {
			QName predicate = metadataNode.getPredicateOrRel();
			boolean isLeaf = metadataNode.isLeaf();			

			if (metadataNode instanceof ResourceMetadataNode) { //TODO Debug condition: metadataNode may currently be an internal or a leave node and should only be added if the respective nodeType is set.
								MetadataPath path = new MetadataPath(isNode, false);

				addToMap(resourceMap, predicate);
				
				if((isLeaf && !nodeType.equals(NodeType.INTERNAL_NODES)) || (!isLeaf && !nodeType.equals(NodeType.LEAVES))) {
					createNewPathList(parentPathElements, resultList, resourceMap, predicate, path);
				}

				fillPathList(((ResourceMetadataNode) metadataNode).getChildren(), isNode, nodeType, parentPathElements, resultList);
			}
			else if (metadataNode instanceof LiteralMetadataNode) {
				MetadataPath path = new MetadataPath(isNode, true);
				
				addToMap(literalMap, predicate);
				
				if((isLeaf && !nodeType.equals(NodeType.INTERNAL_NODES))) {
					createNewPathList(parentPathElements, resultList, literalMap, predicate, path);
				}
			}
		}
	}
}