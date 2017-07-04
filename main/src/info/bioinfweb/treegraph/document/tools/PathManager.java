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


import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import info.bioinfweb.treegraph.document.HiddenDataElement;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.NodeType;
import info.bioinfweb.treegraph.document.metadata.MetadataNode;
import info.bioinfweb.treegraph.document.metadata.MetadataPath;
import info.bioinfweb.treegraph.document.metadata.MetadataPathElement;
import info.bioinfweb.treegraph.document.metadata.MetadataTree;
import info.bioinfweb.treegraph.document.metadata.ResourceMetadataNode;
import info.bioinfweb.treegraph.document.nodebranchdata.LiteralMetadataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.ResourceMetadataAdapter;



public class PathManager {
	private static void fillPathList(List<MetadataNode> nodeList, List<MetadataPath> pathList, boolean isNode, NodeType nodeType) {
		Map<QName, Integer> resourceMap = null;
		Map<QName, Integer> literalMap = null;		

		for (MetadataNode metadataNode : nodeList) {
			QName predicate = metadataNode.getPredicateOrRel();
			if (metadataNode instanceof ResourceMetadataNode) {
				MetadataPath path = new MetadataPath(isNode, false);
				int index = 0;
				if (!resourceMap.containsKey(predicate)) {
					resourceMap.put(metadataNode.getPredicateOrRel(), index);
				}
				
				MetadataPathElement element = new MetadataPathElement(predicate, resourceMap.get(predicate));
				if (!path.getElementList().contains(element)) {
					path.getElementList().add(element);
				}
			}
		}		
	}
	
	
	private static void createCombinedMetadataTree(Node root, MetadataTree tree, boolean includeHiddenNodeData,	boolean includeHiddenBranchData) {
		Iterator<MetadataNode> iterator = root.getMetadataTree().getChildren().iterator();
		List<MetadataNode> treeChildren = tree.getChildren();
		MetadataPath path = new MetadataPath(includeHiddenNodeData, true);
		
		while(iterator.hasNext()) {
			int index = 0;
			Map<K, V>
			MetadataNode child = iterator.next();
			QName predicate = child.getPredicateOrRel();
			MetadataPathElement pathElement = new MetadataPathElement(predicate, index);
			
			while (!path.getElementList().contains(pathElement)) {
				if (!path.getElementList().contains(pathElement)) {
					path.getElementList().add(pathElement);
				}
				else {
					pathElement = new MetadataPathElement(predicate, index++);
				}
			}
			
			for (int i = 0; i < root.getChildren().size(); i++) {
				createCombinedMetadataTree(root.getChildren().get(i), tree, includeHiddenNodeData, includeHiddenBranchData);
			}
		}
		
		MetadataNode treeNode = tree.searchNodeByPath(path, true);
		if(!treeChildren.contains(treeNode)) {
			treeChildren.add(treeNode);
		}
	//Creates combined metadataTree from nodes from subtree.
	//Fügt Baum von einem Knoten zu tree ggf. hinzu.
	//Index muss beachtet werden, ggf. neues Element erstellen mit selbem Prädikat aber höherem index.
	//Ruft sich selbst rekursiv auf.	
	//searchNodeByPath
	}
	
	
	public static List<NodeBranchDataAdapter> createAdapterList(MetadataTree tree) {
		List<NodeBranchDataAdapter> list = null;
		List<MetadataNode> children = tree.getChildren();
		
		for (MetadataNode metadataNode : children) {
			fillAdapterList(metadataNode, list);
		}
		return list;
		
		//Creates list of all NodeBranchDataAdapters of a given MetadataTree
		//Durchläuft Baum und erstellt Liste aus Adapters (ruft fillAdapterList rekursiv auf)
	}
	
	
	private static void fillAdapterList(MetadataNode root, List<NodeBranchDataAdapter> list) {
		if (root instanceof ResourceMetadataNode) {
			int index = 0;
			MetadataPath resourcePath = new MetadataPath(true, false);
			MetadataPathElement pathElement = new MetadataPathElement(root.getPredicateOrRel(), index);			

			if (!resourcePath.getElementList().contains(pathElement)) {
				resourcePath.getElementList().add(pathElement);
				list.add(new ResourceMetadataAdapter(resourcePath));
			}
			else {
				pathElement = new MetadataPathElement(root.getPredicateOrRel(), index++);
			}			
		}
		else {
			int index = 0;
			MetadataPath literalPath = new MetadataPath(true, true);
			MetadataPathElement pathElement = new MetadataPathElement(root.getPredicateOrRel(), index);
			
			if (!literalPath.getElementList().contains(pathElement)) {
				literalPath.getElementList().add(pathElement);
				list.add(new ResourceMetadataAdapter(literalPath));
			}
			else {
				pathElement = new MetadataPathElement(root.getPredicateOrRel(), index++);
			}
		}
	//Füllt leere List mit NodeBranchDataAdapter vom jeweiligen MetadataNode.
	}
}
