/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2011  Ben Stöver, Kai Müller
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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.Vector;

import info.bioinfweb.treegraph.Main;
import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.Tree;
import info.bioinfweb.treegraph.document.io.AbstractDocumentIterator;
import info.bioinfweb.treegraph.document.io.DocumentIterator;
import info.bioinfweb.treegraph.document.io.DocumentReader;
import info.bioinfweb.treegraph.document.io.TextStreamReader;
import info.bioinfweb.treegraph.document.io.TreeSelector;
import info.bioinfweb.treegraph.document.io.log.LoadLogger;
import info.bioinfweb.treegraph.document.io.nexus.NexusParser;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;



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
		private NewickStringReader newickReader = new NewickStringReader(); 
		
		
		public NewickDocumentIterator(InputStreamReader reader, LoadLogger loadLogger,
				NodeBranchDataAdapter internalAdapter,
				NodeBranchDataAdapter branchLengthsAdapter) {
			
			super(loadLogger, internalAdapter, branchLengthsAdapter, false);
			streamReader = reader;
		}


		@Override
		public Document next() throws IOException {
			Document result = new Document();
			result.setTree(newickReader.read(
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
				char c = (char)code;  //TODO Konvertierung nachsehen
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
	  	return terminateNewick(result.toString());  // If end of file was reached without a terminal symbol, the sequence until than is returned.
		}
	}
	
	
	private String[] splitDocument(InputStreamReader reader) throws IOException {
  	LinkedList<String> result = new LinkedList<String>();
		String tree = readNextTree(reader);
		while (tree != null) {
			result.add(tree);
		}
		return result.toArray(new String[result.size()]);
		
//		Vector<Integer> ends = new Vector<Integer>();
//		ends.add(-1);
//		
//		Status status = Status.FREE;
//		for (int i = 0; i < content.length(); i++) {
//			switch (status) {
//			  case FREE:
//					switch (content.charAt(i)) {
//					  case NewickStringChars.NAME_DELIMITER:
//		  				status = Status.NAME;
//		  				break;
//					  case NewickStringChars.COMMENT_START:
//		  				status = Status.COMMENT;
//		  				break;
//					  case NewickStringChars.TERMINAL_SYMBOL:
//					  	ends.add(i);
//					  	break;
//					}
//					break;
//			  case NAME:
//			  	if (content.charAt(i) == NewickStringChars.NAME_DELIMITER) {
//			  		status = Status.FREE;
//			  	}
//			  	break;
//			  case COMMENT:
//			  	if (content.charAt(i) == NewickStringChars.COMMENT_END) {
//			  		status = Status.FREE;
//			  	}
//			  	break;
//			}
//		}
//		
//		String[] result = new String[0];
//		if (ends.size() == 0) {
//			content = content.trim();
//			if (!content.equals("")) {
//				result = new String[1];
//				result[0] = terminateNewick(content);
//			}
//		}
//		else {
//			String lastTree = content.substring(ends.lastElement() + 1).trim();
//			int addend = 0;
//			if (!lastTree.equals("")) {
//				addend = 1;  // Letzten Eintrag als Newick mit fehlendem ";" behandeln
//			}
//			result = new String[ends.size() - 1 + addend];
//			for (int i = 0; i < ends.size() - 1; i++) {
//				result[i] = terminateNewick(content.substring(ends.get(i) + 1, ends.get(i + 1)).trim());
//			}
//			if (addend == 1) {
//				result[result.length - 1] = terminateNewick(lastTree);  // Es könnte bei einem nicht abgeschlossenen Namen oder Kommentar bereits ein TERMINAL_SYMBOL enthalten sein. Damit im Fehlerdialog kein zweites ; angezeigt wird, prüft die Methode dies.
//			}
//		}
//		return result;
	}
	
	
	public Document read(InputStream stream, LoadLogger loadLogger, NodeBranchDataAdapter internalAdapter, 
			NodeBranchDataAdapter branchLengthsAdapter,	TreeSelector selector, 
			boolean translateInternals) throws Exception {
		
		String[] parts = splitDocument(new InputStreamReader(new BufferedInputStream(stream)));

		//
	  String[] names = new String[parts.length];
		for (int i = 0; i < names.length; i++) {
			names[i] = "Tree " + i;
		}
		Tree[] trees = NewickStringReader.read(parts, internalAdapter, branchLengthsAdapter, null, false);
		//
		
		Document result = new Document();
		result.setTree(trees[selector.select(names, trees)]);
		return result;
	}
	
	
	public Document read(File file, LoadLogger loadLogger, NodeBranchDataAdapter internalAdapter, NodeBranchDataAdapter branchLengthsAdapter,
			TreeSelector selector, boolean translateInternals) throws Exception {
		
		Document result = read(new FileInputStream(file), loadLogger, internalAdapter, branchLengthsAdapter, selector, 
				translateInternals);
		result.setDefaultName(result.getDefaultName().replaceAll(
				Main.getInstance().getNameManager().getPrefix(), file.getName()));
		return result;
	}


	@Override
	public DocumentIterator readAll(InputStream stream, LoadLogger loadLogger,
			NodeBranchDataAdapter internalAdapter,
			NodeBranchDataAdapter branchLengthsAdapter, boolean translateInternalNodes)
			throws Exception {

		InputStreamReader reader = new InputStreamReader(new BufferedInputStream(stream));
		return new NewickDocumentIterator(reader, loadLogger, internalAdapter, branchLengthsAdapter);
	}
}