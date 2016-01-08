/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2015-2016  Ben Stöver, Sarah Wiechers
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
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeNameAdapter;



public class NewickStringWriter extends NewickStringChars {
	private static boolean isFreeName(String name, boolean edited) {
		if (name.length() == 0) {
			return true;
		}
		else if (!edited && name.contains("_")) {
			return false;  // Always put in quotation marks to distinguish between space and underscore.
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
	
	
	public static String formatName(Node node, NodeBranchDataAdapter adapter, boolean spacesAsUnderscore) {
		String name = "";
    if (adapter.isDecimal(node)) {
    	name = "" + adapter.getDecimal(node);
    }
    else if (adapter.isString(node)) {
    	name = adapter.getText(node);
    }
		
		if (isFreeName(name, false)) {
			return name;
		}
		else {
			String editedName = name.replaceAll(" ", "_");
			if (spacesAsUnderscore && isFreeName(editedName, true)) { 
				return editedName;
			}
			else {  // Spaces are not replaced if other characters that make quotations necessary are present.
				StringBuffer result = new StringBuffer(name.length() * 2);
				result.append(NAME_DELIMITER);
				for (int i = 0; i < name.length(); i++) {
					if (name.charAt(i) == NAME_DELIMITER) {
						result.append(NAME_DELIMITER);  // Second time 
					}
					result.append(name.charAt(i));
				}
				result.append(NAME_DELIMITER);
				return result.toString();
			}
		}
	}
	
	
	private static String writeSubtree(Node root, NodeBranchDataAdapter internalAdapter, 
  		NodeBranchDataAdapter leafAdapter, NodeBranchDataAdapter branchLengthAdapter, boolean spacesAsUnderscore) {
		
		String result = "";
		
		// Write subtree:
		if (!root.isLeaf()) {
			result += SUBTREE_START;
			int size = root.getChildren().size();
			for (int i = 0; i < size - 1; i++) {
				result += writeSubtree(root.getChildren().get(i), internalAdapter, leafAdapter, 
						branchLengthAdapter, spacesAsUnderscore);
				result += ELEMENT_SEPERATOR;
			}
			result += writeSubtree(root.getChildren().get(size - 1), internalAdapter, leafAdapter, 
					branchLengthAdapter, spacesAsUnderscore);
			result += SUBTREE_END;
		}
		
		// Write node name:
		if (root.isLeaf()) {
			result += formatName(root, leafAdapter, spacesAsUnderscore);
		}
		else {
			result += formatName(root, internalAdapter, spacesAsUnderscore);
		}
		
		// Write branch length:
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
					  null),  // Do not specify BranchLengthAdapter.getSharedInstance() here, because exporting without branch length must be possible.
			  properties.getBoolean(ReadWriteParameterMap.KEY_SPACES_AS_UNDERSCORE, false));
	}
	
	
  public static String write(Tree tree, NodeBranchDataAdapter internalAdapter, 
  		NodeBranchDataAdapter leafAdapter, NodeBranchDataAdapter branchLengthAdapter, boolean spacesAsUnderscore) {
  	
  	String result;
  	if (tree.getFormats().getShowRooted()) {
  		result = NexusParser.COMMENT_START + NexusParser.ROOTED_HOT_COMMENT + 
  		    NexusParser.COMMENT_END;
  	}
  	else {
  		result = NexusParser.COMMENT_START + NexusParser.UNROOTED_HOT_COMMENT + 
  		    NexusParser.COMMENT_END;
  	}
  	
  	if (tree.isEmpty()) {
  		result += TERMINAL_SYMBOL;
  	}
  	else {
  		result += writeSubtree((Node)tree.getPaintStart(), internalAdapter, leafAdapter, 
  				branchLengthAdapter, spacesAsUnderscore) + TERMINAL_SYMBOL;
  	}
  	return result;
  }
}