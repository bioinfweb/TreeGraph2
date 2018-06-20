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
package info.bioinfweb.treegraph.document.undo.file;


import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.TextElementDataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.TextIDElementType;



public class AddSupportValuesParameters {
	private Document sourceDocument;
	private TextElementDataAdapter terminalsAdapter;
	private TextIDElementType targetType;
	private String idPrefix;
	private NodeBranchDataAdapter sourceSupportColumn;
	private NodeBranchDataAdapter sourceLeavesColumn;
	private boolean rooted;
	private boolean parseNumericValues;
	
	
	public Document getSourceDocument() {
		return sourceDocument;
	}
	
	
	public void setSourceDocument(Document sourceDocument) {
		this.sourceDocument = sourceDocument;
	}
	
	
	public TextElementDataAdapter getTerminalsAdapter() {
		return terminalsAdapter;
	}
	
	
	public void setTerminalsAdapter(TextElementDataAdapter terminalsAdapter) {
		this.terminalsAdapter = terminalsAdapter;
	}
	
	
	public TextIDElementType getTargetType() {
		return targetType;
	}
	
	
	/**
	 * Sets the column to type to import support values into.
	 * 
	 * @param targetType the type of the new support column to be created
	 */
	public void setTargetType(TextIDElementType targetType) {
		this.targetType = targetType;
	}
	
	
	public String getIDPrefix() {
		return idPrefix;
	}
	
	
	public void setIdPrefix(String idPrefix) {
		this.idPrefix = idPrefix;
	}
	
	
	public NodeBranchDataAdapter getSourceSupportColumn() {
		return sourceSupportColumn;
	}
	
	
	public void setSourceSupportColumn(NodeBranchDataAdapter supportcolumn) {
		this.sourceSupportColumn = supportcolumn;
	}
	
	
	public NodeBranchDataAdapter getSourceLeavesColumn() {
		return sourceLeavesColumn;
	}


	public void setSourceLeavesColumn(NodeBranchDataAdapter sourceLeavesColumn) {
		this.sourceLeavesColumn = sourceLeavesColumn;
	}


	public boolean isRooted() {
		return rooted;
	}
	
	
	public void setRooted(boolean rooted) {
		this.rooted = rooted;
	}


	public boolean isParseNumericValues() {
		return parseNumericValues;
	}


	public void setParseNumericValues(boolean parseNumericValues) {
		this.parseNumericValues = parseNumericValues;
	}
}
