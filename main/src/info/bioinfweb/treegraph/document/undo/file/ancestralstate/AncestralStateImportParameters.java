/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2011, 2013-2019  Ben Stöver, Sarah Wiechers, Kai Müller
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
package info.bioinfweb.treegraph.document.undo.file.ancestralstate;


import java.util.Map;

import info.bioinfweb.treegraph.document.io.ancestralstate.AncestralStateData;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.undo.file.ImportDataColumnsParameters;



public class AncestralStateImportParameters extends ImportDataColumnsParameters {
	private Map<String, AncestralStateData> data = null;
	private NodeBranchDataAdapter internalNodeNamesAdapter = null;
	private String[] pieChartLabelIDs= null;	


	public AncestralStateImportParameters() {
		super();
	}
	
	
	public String[] getPieChartLabelIDs() {
		return pieChartLabelIDs;
	}


	public void setPieChartLabelIDs(String[] pieChartLabelIDs) {
		this.pieChartLabelIDs = pieChartLabelIDs;
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
