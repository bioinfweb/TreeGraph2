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
package info.bioinfweb.treegraph.document.undo.file.addsupportvalues;


import java.util.List;

import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.topologicalcalculation.NodeInfo;



/**
 * Implementation of {@link EquivalentBranchHandler} that combines the support values from all topologically equivalent branches
 * into a test annotation that contains a list of all values.
 * 
 * @author Ben St&ouml;ver
 * @since 2.16.0
 */
public class ValueListHandler implements EquivalentBranchHandler {
	private String separator;

	
	public ValueListHandler(String separator) {
		super();
		this.separator = separator;
	}


	/**
	 * Creates a new instance with the default separator {@code '/'}.
	 */
	public ValueListHandler() {
		this("/");
	}
	
	
	@Override
	public void handleBranches(List<NodeInfo> sourceNodes, Node targetRoot, NodeBranchDataAdapter sourceSupportAdapter, 
			NodeBranchDataAdapter targetSupportAdapter, boolean parseNumericValues) {

		double firstValue = Double.NaN;
		int valueCount = 0;
		StringBuilder result = new StringBuilder();
		for (NodeInfo nodeInfo : sourceNodes) {
			double value = sourceSupportAdapter.getNumericValue(nodeInfo.getNode(), parseNumericValues);
			if (!Double.isNaN(value)) {
				if (Double.isNaN(firstValue)) {
					firstValue = value;
				}
				
				result.append(value);  //TODO Apply any number format?
				result.append(separator);
				
				valueCount++;
			}
		}
		
		if (valueCount == 1) {
			targetSupportAdapter.setDecimal(targetRoot, firstValue);  // Set decimal value if only one value was found.
		}
		else if (valueCount > 1) {
			targetSupportAdapter.setText(targetRoot, result.substring(0, result.length() - separator.length()));  // Set list if multiple values were found. Remove trailing separator.
		}
	}
}
