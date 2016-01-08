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
package info.bioinfweb.treegraph.document.undo.file;

import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.TextElementDataAdapter;
import info.bioinfweb.treegraph.document.undo.file.AddSupportValuesEdit.TargetType;

import java.io.File;

public class AddSupportValuesParameters {
	private Document sourceDocument;
	private TextElementDataAdapter terminalsAdapter;
	private TargetType targetType;
	private String idPrefix;
	private NodeBranchDataAdapter supportColumn;
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
	
	
	public TargetType getTargetType() {
		return targetType;
	}
	
	
	public void setTargetType(TargetType targetType) {
		this.targetType = targetType;
	}
	
	
	public String getIdPrefix() {
		return idPrefix;
	}
	
	
	public void setIdPrefix(String idPrefix) {
		this.idPrefix = idPrefix;
	}
	
	
	public NodeBranchDataAdapter getSupportColumn() {
		return supportColumn;
	}
	
	
	public void setSupportColumn(NodeBranchDataAdapter supportcolumn) {
		this.supportColumn = supportcolumn;
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
