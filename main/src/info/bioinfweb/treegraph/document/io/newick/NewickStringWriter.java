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
package info.bioinfweb.treegraph.document.io.newick;


import info.bioinfweb.treegraph.document.*;
import info.bioinfweb.treegraph.document.io.ReadWriteParameterMap;
import info.bioinfweb.treegraph.document.io.nexus.NexusParser;
import info.bioinfweb.treegraph.document.nodebranchdata.BranchLengthAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeNameAdapter;



public class NewickStringWriter extends NewickStringChars {
	private static boolean isFreeName(String name) {
		if (name.length() == 0) {
			return true;
		}
		else if (isFreeNameFirstChar(name.charAt(0))) {
			for (int i = 1; i < name.length(); i++) {
				if (!isFreeNameChar(name.charAt(i))) {
					return false;
				}
			}
			return true;
		}
		else {
			return false;
		}
	}
	
	
	private static String formatName(Node node, NodeBranchDataAdapter adapter) {
		String name = "";
    if (adapter.isDecimal(node)) {
    	name = "" + adapter.getDecimal(node);
    }
    else if (adapter.isString(node)) {
    	name = adapter.getText(node);
    }
		
		if (isFreeName(name)) {
			return name;
		}
		else {
			String result = "" + NAME_DELIMITER;
			for (int i = 0; i < name.length(); i++) {
				if (name.charAt(i) == NAME_DELIMITER) {
					result += NAME_DELIMITER;  // zweites mal 
				}
				result += name.charAt(i);
			}
			return result + NAME_DELIMITER;
		}
	}
	
	
	private static String writeSubtree(Node root, NodeBranchDataAdapter internalAdapter, 
  		NodeBranchDataAdapter leafAdapter, NodeBranchDataAdapter branchLengthAdapter) {
		
		String result = "";
		
		// Write subtree:
		if (!root.isLeaf()) {
			result += SUBTREE_START;
			int size = root.getChildren().size();
			for (int i = 0; i < size - 1; i++) {
				result += writeSubtree(root.getChildren().get(i), internalAdapter, leafAdapter, 
						branchLengthAdapter);
				result += ELEMENT_SEPERATOR + " ";
			}
			result += writeSubtree(root.getChildren().get(size - 1), internalAdapter, leafAdapter, 
					branchLengthAdapter);
			result += SUBTREE_END;
		}
		
		// Write node name:
		if (root.isLeaf()) {
			result += formatName(root, leafAdapter);
		}
		else {
			result += formatName(root, internalAdapter);
		}
		
		// Astlänge schreiben:
		if ((branchLengthAdapter != null) && branchLengthAdapter.isDecimal(root)) {
			result += LENGTH_SEPERATOR + "" + branchLengthAdapter.getDecimal(root);
		}
		
		return result;
	}
	
	
	public static String write(Tree tree, ReadWriteParameterMap properties) {
		return write(tree, 
	  		properties.getNodeBranchDataAdapter(ReadWriteParameterMap.KEY_INTERNAL_NODE_NAMES_ADAPTER, 
	  				NodeNameAdapter.getSharedInstance()),
			  properties.getNodeBranchDataAdapter(ReadWriteParameterMap.KEY_LEAF_NODE_NAMES_ADAPTER, 
			  		NodeNameAdapter.getSharedInstance()),
			  properties.getNodeBranchDataAdapter(ReadWriteParameterMap.KEY_BRANCH_LENGTH_ADAPTER, 
			  		BranchLengthAdapter.getSharedInstance()));
	}
	
	
  public static String write(Tree tree, NodeBranchDataAdapter internalAdapter, 
  		NodeBranchDataAdapter leafAdapter, NodeBranchDataAdapter branchLengthAdapter) {
  	
  	String result;
  	if (tree.getFormats().getShowRooted()) {
  		result = NexusParser.COMMENT_START + NexusParser.ROOTED_COMMAND + 
  		    NexusParser.COMMENT_END + " ";
  	}
  	else {
  		result = NexusParser.COMMENT_START + NexusParser.UNROOTED_COMMAND + 
  		    NexusParser.COMMENT_END + " ";
  	}
  	
  	if (tree.isEmpty()) {
  		result += TERMINAL_SYMBOL;
  	}
  	else {
  		result += writeSubtree((Node)tree.getPaintStart(), internalAdapter, leafAdapter, 
  				branchLengthAdapter) + TERMINAL_SYMBOL;
  	}
  	return result;
  }
}