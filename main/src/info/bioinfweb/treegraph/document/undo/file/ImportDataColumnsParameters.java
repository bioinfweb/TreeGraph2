package info.bioinfweb.treegraph.document.undo.file;


import java.io.File;

import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.undo.ImportTextElementDataParameters;



public class ImportDataColumnsParameters extends ImportTextElementDataParameters {
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
