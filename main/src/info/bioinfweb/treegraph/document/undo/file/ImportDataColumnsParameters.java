/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2011, 2013-2016  Ben Stöver, Sarah Wiechers, Kai Müller
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


import java.io.File;

import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.undo.CompareTextElementDataParameters;



public class ImportDataColumnsParameters extends CompareTextElementDataParameters {
	private File tableFile = null;
	private NodeBranchDataAdapter keyAdapter = null;
  private NodeBranchDataAdapter[] importAdapters = new NodeBranchDataAdapter[0];   
  

	public File getTableFile() {
		return tableFile;
	}


	public void setTableFile(File tableFile) {
		this.tableFile = tableFile;
	}
	

	public NodeBranchDataAdapter getKeyAdapter() {
		return keyAdapter;
	}


	public void setKeyAdapter(NodeBranchDataAdapter keyAdapter) {
		this.keyAdapter = keyAdapter;
	}


	public NodeBranchDataAdapter[] getImportAdapters() {
		return importAdapters;
	}


	public void setImportAdapters(NodeBranchDataAdapter[] importAdapters) {
		this.importAdapters = importAdapters;
	}
}
