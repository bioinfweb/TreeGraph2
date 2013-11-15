/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2013  Ben Stöver, Kai Müller
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
import info.bioinfweb.treegraph.document.io.nexus.TranslTable;
import info.bioinfweb.treegraph.document.nodebranchdata.BranchLengthAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeNameAdapter;
import info.webinsel.util.Math2;

import java.util.*;



/**
 * <div>This class converts a tree in the Newick format to a <code>Document</code>. First
 * the string is decomposed into Tokens by a <code>NewickParser</code> and then the
 * developed token list is syntactically analyzed. The following grammar is used:</div>
 * &nbsp;<br />
 * <samp>
 *   <div>Tree --> Branch TERMINAL_SYMBOL</div>
 *   <div>Subtree --> Leaf | Internal</div>
 *   <div>Leaf --> Name</div>
 *   <div>Internal --> SUBTREE_START BranchList SUBTREE_END Name</div>
 *   <div>BranchList --> Branch | Branch ELEMENT_SEPERATOR BranchList</div>
 *   <div>Branch --> Subtree Length</div>
 *   <div>Name --> &epsilon; | NAME</div>
 *   <div>Length --> &epsilon; | LENGTH</div>
 * </samp> 
 * &nbsp;<br />
 * <div>Note that this grammar differs from the usual Newick grammar becuase it allows
 * the root node to have a branch length.</div>
 * &nbsp;<br />
 * 
 * @author Ben St&ouml;ver
 */
public class NewickStringReader extends NewickStringChars {
	public static final NodeNameAdapter LEAF_ADAPTER = NodeNameAdapter.getSharedInstance();
	public static final BranchLengthAdapter BRANCH_LENGTH_ADAPTER = 
		  BranchLengthAdapter.getSharedInstance();
	
	
	private static NewickStringReader sharedInstance = null;
	
	private String newickDescription;
	private List<NewickToken> tokens;
	private NodeBranchDataAdapter internalAdapter;
	private NodeBranchDataAdapter branchLengthsAdapter;
	private TranslTable translTable; 
	private boolean translateInternals;
	private boolean hiddenDataAdded = false;
	private CommentDataReader commentDataReader = new CommentDataReader();
  
	
	public static NewickStringReader getSharedInstance() {
		if (sharedInstance == null) {
			sharedInstance = new NewickStringReader();
		}
		return sharedInstance;
	}	
	
	
  private int searchSubtreeEnd(int start, int end) {
  	for (int pos = start; pos <= end; pos++) {
  		TokenType type = tokens.get(pos).getType(); 
			if (type.equals(TokenType.SUBTREE_END)) {
				return pos;
			}
			else if (type.equals(TokenType.SUBTREE_START)) {
				pos = searchSubtreeEnd(pos + 1, end);
				if (pos == -1) {  // Sonst würde diese Rekursionsebene die Suche wieder von neum beginnen.
					return -1;
				}
			}
		}
  	return -1;
  }
 	
 	
 	private int searchToken(int start, int end, TokenType type) {
  	for (int pos = start; pos <= end; pos++) {
			if (tokens.get(pos).getType().equals(type)) {
				return pos;
			}
			else {
				if (tokens.get(pos).getType().equals(TokenType.SUBTREE_START)) {
					pos = searchSubtreeEnd(pos + 1, end);
					if (pos == -1) {  // Sonst würde diese Rekursionsebene die Suche wieder von neum beginnen.
						return -1;
					}
				}
			}
  	}
  	return -1;  	
  }
 	
 	
  private Node readBranch(int start, int end, Node root) throws NewickException {
  	if (tokens.get(end).getType().equals(TokenType.LENGTH)) {
  		Node result = readSubtree(start, end - 1);
  		branchLengthsAdapter.setDecimal(result, tokens.get(end).getLength());
  		return result;
  	}
  	else {
  		return readSubtree(start, end);
  	}  	
  }
 	 	
 	
 	private void readBranchList(int start, int end,	Node root) throws NewickException {
  	int branchEnd = searchToken(start, end, TokenType.ELEMENT_SEPARATOR);
  	if (branchEnd == -1) {
  		branchEnd = end;
  	}
  	else {
  		branchEnd--;  // Vor ELEMENT_SEPERATOR setzen
  	}
  	
  	Node child = readBranch(start, branchEnd, root);  // erstes Unterelement lesen
  	child.setParent(root);
  	root.getChildren().add(child);
  	if (branchEnd < end) {  // Rekursionsbedingung
  		readBranchList(branchEnd + 2, end, root);
  	}
  }
  
  
  private void readName(int previousEnd, int end,	Node root, NodeBranchDataAdapter adapter, 
  		TranslTable translTable) throws NewickException {

  	try {
  		commentDataReader.read(tokens.get(end).getComment(), root);
  		hiddenDataAdded = !root.getHiddenDataMap().isEmpty();
  	}
  	catch (Exception e) {}  // comment was not of the expected format
  	
  	if (previousEnd == end - 1) {
  		if (tokens.get(end).getType().equals(TokenType.NAME)) {
  			String text = tokens.get(end).getText();
  			if ((translTable != null) && !tokens.get(end).wasDelimited()) {
    			String newText = translTable.get(text);
    			if (newText != null) {
    				text = newText;
    			}
    			else if (Math2.isInt(text)) {  // Ist die Zahl als Bezeichner nicht im TransTable vorhanden, gibt sie laut Maddison et.al. den Index im TransTable an, sofern sie innerhalb dieses Bereichs liegt. 
    				int pos = Integer.parseInt(text);
    				if (Math2.isBetween(pos, 0, translTable.size() - 1)) {
    					text = translTable.get(pos);
    				}
    			}
  			}
  			
  			try {
    			adapter.setDecimal(root, Double.parseDouble(text));
  			}
  			catch (NumberFormatException e) {
    			adapter.setText(root, text);
  			}
  		}
  	}
  	else if (previousEnd != end) {  // kein Name angegeben aber trotzdem Token vorhanden  
  		throw new NewickException(tokens.get(previousEnd + 2), newickDescription);  //TODO vorher stand hier "+ 1". So in Funktion z.B. für direkt aufeinander folgende Namen, aber was war mit Kommentar oben gemeint?
  	}
  }
 	
 	
 	private Node readInternal(int start, int end) throws NewickException {
 		if (tokens.get(start).getType().equals(TokenType.SUBTREE_START)) {
 	 		int subtreeEnd = searchToken(start + 1, end, TokenType.SUBTREE_END);
 			Node root = Node.getInstanceWithBranch();
 			readBranchList(start + 1, subtreeEnd - 1, root);
 			if (translateInternals) {
 				readName(subtreeEnd, end, root, internalAdapter, translTable);
 			}
 			else {
 				readName(subtreeEnd, end, root, internalAdapter, null);
 			}
 			return root;
 		}
 		else {
 			throw new NewickException(tokens.get(start).getTextPos(), newickDescription, 
 					TokenType.SUBTREE_START, tokens.get(start).getType());
 		}
 	}
 	
 	
 	private Node readLeaf(int start, int end) throws NewickException {
 		Node result = Node.getInstanceWithBranch();
 		readName(start - 1, end, result, LEAF_ADAPTER, translTable);
 		return result;
 	}
 	
 	
 	private Node readSubtree(int start, int end) throws NewickException{
 		if (tokens.get(start).getType().equals(TokenType.SUBTREE_START)) {
			return readInternal(start, end);
 		}
 		else {
 			return readLeaf(start, end);
 		}
  }


  private Tree readTree() throws NewickException {
  	Tree result = new Tree();
  	
  	if (tokens.size() == 0) {
  		throw new NewickException(0, newickDescription, "String length is 0.");
  	}
  	else if (tokens.get(0).equals(TokenType.TERMNINAL_SYMBOL)) {
  		return result;  // empty tree
  	}
  	else if (tokens.size() < 3) {
  		throw new NewickException(0, newickDescription, "Incomplete Newick string");  // "();" ist mindestens drei zeichen lang.
  	}
  	else if (!(tokens.get(0).getType().equals(TokenType.SUBTREE_START) || 
  			(tokens.get(0).getType().equals(TokenType.UNROOTED_COMMAND) && 
  					tokens.get(1).getType().equals(TokenType.SUBTREE_START)) ||
  			(tokens.get(0).getType().equals(TokenType.ROOTED_COMMAND) && 
  					tokens.get(1).getType().equals(TokenType.SUBTREE_START)))) {
  		if (tokens.get(0).getType().equals(TokenType.UNROOTED_COMMAND)) {
  			throw new NewickException(tokens.get(1).getTextPos(), newickDescription, TokenType.SUBTREE_START, 
  					tokens.get(1).getType());
  		}
  		else {
  			throw new NewickException(0, newickDescription, TokenType.SUBTREE_START, tokens.get(0).getType());
  		}
  	}
  	else {
  		int start = 0;
  		if (tokens.get(0).getType().equals(TokenType.ROOTED_COMMAND)) {
  			result.getFormats().setShowRooted(true);
  			start = 1;
  		}
  		else if (tokens.get(0).getType().equals(TokenType.UNROOTED_COMMAND)) {
  			result.getFormats().setShowRooted(false);
  			start = 1;
  		}
  		
  		int end = searchToken(start + 1, tokens.size() - 1, TokenType.SUBTREE_END);
  		if (end == -1) {
      	throw new NewickException(0, newickDescription, "Unterminated subtree");
  		}
  		else {
  			if ((tokens.size() >= end + 2) && tokens.get(tokens.size() - 1).getType().equals(TokenType.TERMNINAL_SYMBOL)) {
    			result.setPaintStart(readSubtree(start, tokens.size() - 2));
    			result.assignUniqueNames();
    			result.updateElementSet();
    			return result;
  			}
  			else {
        	throw new NewickException(0, newickDescription, "Tree not completed by \";\"");  // NewickReader stellt eigentlich sicher, dass diese Exception niemals geworfen wird.
  			}
  		}
  	}
	}
  
  
  /**
   * Returns whether hidden data has been read from commetns in the newick string (e.g. from BEAST)
   * during the last call of 
   * {@link NewickStringReader#read(String, NodeBranchDataAdapter, NodeBranchDataAdapter, TranslTable, boolean)}
   * or one of its convenience methods.  
   * @return
   */
  public boolean getHiddenDataAdded() {
		return hiddenDataAdded;
	}


	public Tree read(final String newick) throws NewickException {
  	return read(newick, LEAF_ADAPTER, BRANCH_LENGTH_ADAPTER, null, false);
  }
  
  
  public Tree read(final String newick, NodeBranchDataAdapter internalAdapter, 
  		NodeBranchDataAdapter branchLengthsAdapter) throws NewickException {
  	
  	return read(newick, internalAdapter, branchLengthsAdapter, null, false);
  }
  
  
  public Tree read(final String newick, NodeBranchDataAdapter internalAdapter, 
  		NodeBranchDataAdapter branchLengthsAdapter, TranslTable translTable, 
  		boolean translateInternals) throws NewickException {
  	
  	newickDescription = newick;
  	tokens = NewickScanner.parse(newick);
  	this.internalAdapter = internalAdapter;
  	this.branchLengthsAdapter = branchLengthsAdapter;
  	this.translTable = translTable;
  	this.translateInternals = translateInternals;
  	hiddenDataAdded = false;
  	
  	Tree tree = readTree();
  	BranchLengthsScaler.getSharedInstance().setDefaultAverageScale(tree);
  	return tree;
  }
  
  
  public static Tree[] read(final String[] newick, NodeBranchDataAdapter internalAdapter, 
  		NodeBranchDataAdapter branchLengthsAdapter, TranslTable translTable, 
  		boolean translateInternals) throws NewickException {

		Tree[] result = new Tree[newick.length];
		for (int i = 0; i < newick.length; i++) {
			result[i] = NewickStringReader.getSharedInstance().read(newick[i], internalAdapter, 
					branchLengthsAdapter, translTable, translateInternals);
		}
		return result;	
  	
  }
}