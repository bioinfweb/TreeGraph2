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


import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;

import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.Tree;
import info.bioinfweb.treegraph.document.io.AbstractDocumentIterator;
import info.bioinfweb.treegraph.document.io.DocumentIterator;
import info.bioinfweb.treegraph.document.io.DocumentReader;
import info.bioinfweb.treegraph.document.io.ReadWriteParameterMap;
import info.bioinfweb.treegraph.document.io.TextStreamReader;
import info.bioinfweb.treegraph.document.io.nexus.NexusParser;
import info.bioinfweb.treegraph.document.nodebranchdata.BranchLengthAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeNameAdapter;
import info.webinsel.util.log.ApplicationLogger;



/**
 * Reads text files consisting only of Newick strings.
 * 
 * @author Ben St&ouml;ver
 */
public class NewickReader extends TextStreamReader implements DocumentReader {
	private enum Status {
		FREE,
		NAME,
		COMMENT;
	}
	
	
	private class NewickDocumentIterator extends AbstractDocumentIterator {
		private InputStreamReader streamReader;
		private NewickStringReader newickStringReader = new NewickStringReader(); 
		
		
		public NewickDocumentIterator(InputStreamReader reader, ApplicationLogger loadLogger,
				NodeBranchDataAdapter internalAdapter,
				NodeBranchDataAdapter branchLengthsAdapter) {
			
			super(loadLogger, internalAdapter, branchLengthsAdapter, false);
			streamReader = reader;
		}


		@Override
		protected Document readNext() throws Exception {
			Document result = createEmptyDocument();
			result.setTree(newickStringReader.read(
					readNextTree(streamReader), getInternalAdapter(), getBranchLengthsAdapter(), null, false));
			return result;
		}
  }
	
	
	/**
	 * Creates a new instance of <code>NewickReader</code>.
	 */
	public NewickReader() {
		super();
	}
	
	
	private String terminateNewick(String newick) {
		if (!newick.endsWith("" + NewickStringChars.TERMINAL_SYMBOL)) {
			newick += NewickStringChars.TERMINAL_SYMBOL;
		}
		return newick;
	}
	
	
	private String readNextTree(InputStreamReader reader) throws IOException {
		int code = reader.read();
		if (code == -1) {  //TODO Wird hier wirklich -1 zurückgegeben, wenn Ende des Streams erreicht ist.
			return null;
		}
		else {
			StringBuffer result = new StringBuffer(NexusParser.MAX_EXPECTED_COMMAND_LENGTH);
			
			Status status = Status.FREE;
			while (code != -1) {
				char c = (char)code;
				result.append(c);
				switch (status) {
				  case FREE:
						switch (result.charAt(result.length() - 1)) {
						  case NewickStringChars.NAME_DELIMITER:
			  				status = Status.NAME;
			  				break;
						  case NewickStringChars.COMMENT_START:
			  				status = Status.COMMENT;
			  				break;
						  case NewickStringChars.TERMINAL_SYMBOL:
						  	return result.toString();
						}
						break;
				  case NAME:
				  	if (c == NewickStringChars.NAME_DELIMITER) {
				  		status = Status.FREE;
				  	}
				  	break;
				  case COMMENT:
				  	if (c == NewickStringChars.COMMENT_END) {
				  		status = Status.FREE;
				  	}
				  	break;
				}
				code = reader.read();
			}
			String newick = result.toString().trim();
			if (newick.equals("")) {
				return null;  // null should be returned already here,  because the file could end with whitespaces
			}
			else {
				return terminateNewick(newick);  // If end of file was reached without a terminal symbol, the sequence until than is returned.
			}
		}
	}
	
	
	private String[] splitDocument(InputStreamReader reader) throws IOException {
  	LinkedList<String> result = new LinkedList<String>();
		String tree = readNextTree(reader);
		while (tree != null) {
			result.add(tree);
			tree = readNextTree(reader);
		}
		return result.toArray(new String[result.size()]);		
	}
	
	
	@Override
  public Document readDocument(BufferedInputStream stream) throws Exception {
		String[] parts = splitDocument(new InputStreamReader(stream));

	  String[] names = new String[parts.length];
		for (int i = 0; i < names.length; i++) {
			names[i] = "Tree " + i;
		}
		Tree[] trees = NewickStringReader.read(
				parts,
				parameterMap.getNodeBranchDataAdapter(ReadWriteParameterMap.KEY_INTERNAL_NODE_NAMES_ADAPTER, 
						NodeNameAdapter.getSharedInstance()),
				parameterMap.getNodeBranchDataAdapter(ReadWriteParameterMap.KEY_BRANCH_LENGTH_ADAPTER, 
						BranchLengthAdapter.getSharedInstance()),
				null, false);  // no translation table in available Newick format 
		
		Document result = createEmptyDocument();
		result.setTree(trees[parameterMap.getTreeSelector().select(names, trees)]);
		return result;
	}
	
	
	@Override
  public DocumentIterator createIterator(BufferedInputStream stream) {
	  InputStreamReader reader = new InputStreamReader(new BufferedInputStream(stream));
  	return new NewickDocumentIterator(reader, loadLogger,  
  			parameterMap.getNodeBranchDataAdapter(ReadWriteParameterMap.KEY_INTERNAL_NODE_NAMES_ADAPTER, 
						NodeNameAdapter.getSharedInstance()),
				parameterMap.getNodeBranchDataAdapter(ReadWriteParameterMap.KEY_INTERNAL_NODE_NAMES_ADAPTER, 
						BranchLengthAdapter.getSharedInstance()));
  }
}