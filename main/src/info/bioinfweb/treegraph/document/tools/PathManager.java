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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import info.bioinfweb.treegraph.document.HiddenDataElement;
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
	private static void createTree(MetadataTree tree, boolean includeHiddenNodeData, boolean isLiteral, List<MetadataNode> treeChildren,
			Map<QName, Integer> map, MetadataNode metadataNode) {
		QName predicate = metadataNode.getPredicateOrRel();
		
		MetadataPath path = new MetadataPath(includeHiddenNodeData, isLiteral);
		int index = 0;
		
		if (!map.containsKey(predicate)) {
			map.put(metadataNode.getPredicateOrRel(), 0);
		}
		else {
			if (map.get(predicate) == index) {
				index++;
				map.replace(predicate, map.get(predicate), index);
			} else {
				index = map.get(predicate);
			}
		}
		
		MetadataPathElement element = new MetadataPathElement(predicate, map.get(predicate));
		if (!path.getElementList().get(path.getElementList().size() - 1).equals(element)) {
			path.getElementList().add(element);
		}
		
		MetadataNode treeNode = tree.searchNodeByPath(path, true);
		if(!treeChildren.contains(treeNode)) {
			treeChildren.add(treeNode);
		}
	}
		
	
	private static void createCombinedMetadataTree(Node root, MetadataTree tree, boolean includeHiddenNodeData,	boolean includeHiddenBranchData) {		
		List<MetadataNode> treeChildren = tree.getChildren();	
		Map<QName, Integer> resourceMap = null;
		Map<QName, Integer> literalMap = null;		
			
		if (includeHiddenNodeData) {
			List<MetadataNode> rootChildren = root.getMetadataTree().getChildren();
						
			for (MetadataNode metadataNode : rootChildren) {			
				if (metadataNode instanceof ResourceMetadataNode) {				
					createTree(tree, includeHiddenNodeData, false, treeChildren, resourceMap, metadataNode);
					
					List<MetadataPath> pathList = new ArrayList<MetadataPath>();
					NodeType nodeType = NodeType.BOTH;
					fillPathList(((ResourceMetadataNode) metadataNode).getChildren(), pathList, includeHiddenNodeData, nodeType);
					
					for (int i = 0; i < pathList.size(); i++) {
						MetadataNode childNode = tree.searchNodeByPath(pathList.get(i), true);
						if(!treeChildren.contains(childNode)) {
							treeChildren.add(childNode);
						}						
					}
				}
				else {
					createTree(tree, includeHiddenNodeData, true, treeChildren, literalMap, metadataNode);
				}
			}
		}
		
		if(includeHiddenBranchData) {
			List<MetadataNode> rootBranchChildren = root.getAfferentBranch().getMetadataTree().getChildren();
			
			for (MetadataNode metadataNode : rootBranchChildren) {
				if (metadataNode instanceof ResourceMetadataNode) {
					createTree(tree, includeHiddenNodeData, false, treeChildren, resourceMap, metadataNode);
					
					List<MetadataPath> pathList = new ArrayList<MetadataPath>();
					NodeType nodeType = NodeType.BOTH;
					fillPathList(((ResourceMetadataNode) metadataNode).getChildren(), pathList, includeHiddenNodeData, nodeType);
					
					for (int i = 0; i < pathList.size(); i++) {
						MetadataNode childNode = tree.searchNodeByPath(pathList.get(i), true);
						if(!treeChildren.contains(childNode)) {
							treeChildren.add(childNode);
						}						
					}
				}
				else {
					createTree(tree, includeHiddenNodeData, true, treeChildren, literalMap, metadataNode);
				}
			}
		}
		
	//Creates combined metadataTree from nodes from subtree.
	//Fügt Baum von einem Knoten zu tree ggf. hinzu.
	//Index muss beachtet werden, ggf. neues Element erstellen mit selbem Prädikat aber höherem index.
	//Ruft sich selbst rekursiv auf.	
	//searchNodeByPath
	}
		
	
	public static List<NodeBranchDataAdapter> createAdapterList(MetadataTree tree, List<NodeBranchDataAdapter> list, boolean isNode) {
		List<MetadataNode> children = tree.getChildren();
		
		for (MetadataNode metadataNode : children) {
			fillAdapterList(metadataNode, list, isNode);
			if (metadataNode instanceof ResourceMetadataNode) {
				for (int i = 0; i < ((ResourceMetadataNode)metadataNode).getChildren().size(); i++) {
					fillAdapterList(((ResourceMetadataNode) metadataNode).getChildren().get(i), list, isNode);
				}				
			}
		}
		return list;
		
		//Creates list of all NodeBranchDataAdapters of a given MetadataTree
		//Durchläuft Baum und erstellt Liste aus Adapters (ruft fillAdapterList rekursiv auf)
	}
		
		
	private static void fillAdapterList(MetadataNode root, List<NodeBranchDataAdapter> list, boolean isNode) {
		Map<QName, Integer> resourceMap = null;
		Map<QName, Integer> literalMap = null;
		QName predicate = root.getPredicateOrRel();
		
		if (root instanceof ResourceMetadataNode) {
			int index = 0;
			MetadataPath resourcePath = new MetadataPath(isNode, false);			
			
			if (!resourceMap.containsKey(predicate)) {
				resourceMap.put(predicate, 0);
			}
			else {
				if (resourceMap.get(predicate) == index) {
					index++;
					resourceMap.replace(predicate, resourceMap.get(predicate), index);
				} else {
					index = resourceMap.get(predicate);
				}
			}
			
			MetadataPathElement pathElement = new MetadataPathElement(root.getPredicateOrRel(), index);	

			if (!resourcePath.getElementList().get(resourcePath.getElementList().size() - 1).equals(pathElement)) {
				resourcePath.getElementList().add(pathElement);
				list.add(new ResourceMetadataAdapter(resourcePath));
			}
			else {
				pathElement = new MetadataPathElement(root.getPredicateOrRel(), index++);
			}			
		}
		else {
			MetadataPath path = new MetadataPath(isNode, true);
			int index = 0;
			
			if (!literalMap.containsKey(predicate)) {
				literalMap.put(predicate, 0);
			}
			else {
				if (literalMap.get(predicate) == index) {
					index++;
					literalMap.replace(predicate, literalMap.get(predicate), index);
				} else {
					index = literalMap.get(predicate);
				}
			}
			
			MetadataPathElement element = new MetadataPathElement(predicate, literalMap.get(predicate));
			if (!path.getElementList().get(path.getElementList().size() - 1).equals(element)) {
				path.getElementList().add(element);
			}
		}
	//Füllt leere List mit NodeBranchDataAdapter vom jeweiligen MetadataNode.
	}
	
	
	private static void fillPathList(List<MetadataNode> nodeList, List<MetadataPath> pathList, boolean isNode, NodeType nodeType) {
		Map<QName, Integer> resourceMap = null;
		Map<QName, Integer> literalMap = null;		

		for (MetadataNode metadataNode : nodeList) {
			QName predicate = metadataNode.getPredicateOrRel();
			
			if (metadataNode instanceof ResourceMetadataNode && !nodeType.equals(NodeType.LEAVES)) {
				MetadataPath path = new MetadataPath(isNode, false);
				int index = 0;
				
				if (!resourceMap.containsKey(predicate)) {
					resourceMap.put(metadataNode.getPredicateOrRel(), 0);
				}
				else {
					if (resourceMap.get(predicate) == index) {
						index++;
						resourceMap.replace(predicate, resourceMap.get(predicate), index);
					} else {
						index = resourceMap.get(predicate);
					}
				}
				
				MetadataPathElement element = new MetadataPathElement(predicate, resourceMap.get(predicate));
				if (!path.getElementList().get(path.getElementList().size() - 1).equals(element)) {
					path.getElementList().add(element);
				}
				
				fillPathList(((ResourceMetadataNode) metadataNode).getChildren(), pathList, isNode, nodeType);
			}
			else if (metadataNode instanceof LiteralMetadataNode && !nodeType.equals(NodeType.INTERNAL_NODES)) {
				MetadataPath path = new MetadataPath(isNode, true);
				int index = 0;
				
				if (!literalMap.containsKey(predicate)) {
					literalMap.put(metadataNode.getPredicateOrRel(), 0);
				}
				else {
					if (literalMap.get(predicate) == index) {
						index++;
						literalMap.replace(predicate, literalMap.get(predicate), index);
					} else {
						index = literalMap.get(predicate);
					}
				}
				
				MetadataPathElement element = new MetadataPathElement(predicate, literalMap.get(predicate));
				if (!path.getElementList().get(path.getElementList().size() - 1).equals(element)) {
					path.getElementList().add(element);
				}
			}
		}		
	}
}
