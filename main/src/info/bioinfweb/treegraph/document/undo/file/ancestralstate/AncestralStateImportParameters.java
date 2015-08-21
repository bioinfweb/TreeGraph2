package info.bioinfweb.treegraph.document.undo.file.ancestralstate;


import java.util.Map;

import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.undo.file.ImportDataColumnsParameters;



public class AncestralStateImportParameters extends ImportDataColumnsParameters {
	private Map<String, AncestralStateData> data = null;
	private NodeBranchDataAdapter internalNodeNamesAdapter = null;
	

	public AncestralStateImportParameters() {
		super();
	}


	public NodeBranchDataAdapter getInternalNodeNamesAdapter() {
		return internalNodeNamesAdapter;
	}


	public void setInternalNodeNamesAdapter(NodeBranchDataAdapter internalNodeNamesAdapter) {
		this.internalNodeNamesAdapter = internalNodeNamesAdapter;
	}

	
	public Map<String, AncestralStateData> getData() {
		return data;
	}


	public void setData(Map<String, AncestralStateData> data) {
		this.data = data;
	}
}
