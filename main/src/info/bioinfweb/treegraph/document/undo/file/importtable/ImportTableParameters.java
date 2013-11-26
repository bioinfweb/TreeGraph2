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
package info.bioinfweb.treegraph.document.undo.file.importtable;


import java.io.File;

import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.gui.dialogs.io.TableSeparatorPanel;



/**
 * Stores all user parameters for importing tables into node/branch data columns.
 * 
 * @author Ben St&ouml;ver
 * @since 2.0.50
 */
public class ImportTableParameters {
	private File tableFile = null;
	private char columnSeparator = '\t';
  private int linesToSkip = 0;
  private boolean headingContained = false;
  
  private NodeBranchDataAdapter keyAdapter = null;
  private boolean ignoreWhitespace = true;
  private boolean distinguishSpaceUnderscore = false;
  private boolean caseSensitive = false;
  private boolean parseNumbericValues = true;
  
  private NodeBranchDataAdapter[] importAdapters = new NodeBranchDataAdapter[0];

  
	public File getTableFile() {
		return tableFile;
	}


	public void setTableFile(File tableFile) {
		this.tableFile = tableFile;
	}


	public char getColumnSeparator() {
		return columnSeparator;
	}

	
	public void setColumnSeparator(char columnSeparator) {
		this.columnSeparator = columnSeparator;
	}


	public int getLinesToSkip() {
		return linesToSkip;
	}


	public void setLinesToSkip(int linesToSkip) {
		this.linesToSkip = linesToSkip;
	}


	public boolean isHeadingContained() {
		return headingContained;
	}


	public void setHeadingContained(boolean headingContained) {
		this.headingContained = headingContained;
	}


	public NodeBranchDataAdapter getKeyAdapter() {
		return keyAdapter;
	}


	public void setKeyAdapter(NodeBranchDataAdapter keyAdapter) {
		this.keyAdapter = keyAdapter;
	}


	public boolean isIgnoreWhitespace() {
		return ignoreWhitespace;
	}


	public void setIgnoreWhitespace(boolean ignoreWhitespace) {
		this.ignoreWhitespace = ignoreWhitespace;
	}


	public boolean isDistinguishSpaceUnderscore() {
		return distinguishSpaceUnderscore;
	}


	public void setDistinguishSpaceUnderscore(boolean distinguishSpaceUnderscore) {
		this.distinguishSpaceUnderscore = distinguishSpaceUnderscore;
	}


	public boolean isCaseSensitive() {
		return caseSensitive;
	}


	public void setCaseSensitive(boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
	}


	public boolean isParseNumbericValues() {
		return parseNumbericValues;
	}


	public void setParseNumbericValues(boolean parseNumbericValues) {
		this.parseNumbericValues = parseNumbericValues;
	}


	public NodeBranchDataAdapter[] getImportAdapters() {
		return importAdapters;
	}


	public void setImportAdapters(NodeBranchDataAdapter[] importAdapters) {
		this.importAdapters = importAdapters;
	}
}
