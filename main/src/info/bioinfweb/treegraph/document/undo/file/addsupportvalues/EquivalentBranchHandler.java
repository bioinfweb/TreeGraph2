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
 * Implementations of this interface handle multiple support values from topologically equivalent nodes during the execution of
 * {@link AddSupportValuesEdit}. 
 * 
 * @author Ben St&ouml;ver
 * @since 2.16.0
 */
public interface EquivalentBranchHandler {
	/**
	 * Maps the support values from the specified node list onto the specified target node. If more than one node from the specified 
	 * list carries a support value, the implementing class will decide how to handle multiple values. 
	 * 
	 * @param sourceNodes
	 * @param targetRoot
	 * @param sourceSupportAdapter
	 * @param targetSupportAdapter
	 * @param parseNumericValues
	 * @return {@code true} if more than one support value was mapped or {@code false} if one or no value was mapped
	 */
	public boolean handleBranches(List<NodeInfo> sourceNodes, Node targetRoot, NodeBranchDataAdapter sourceSupportAdapter, 
			NodeBranchDataAdapter targetSupportAdapter,	boolean parseNumericValues);
}
