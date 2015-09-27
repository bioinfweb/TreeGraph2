/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2015  Ben Stöver, Sarah Wiechers, Kai Müller
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
package info.bioinfweb.treegraph.document.undo;


import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.change.DocumentChangeType;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.topologicalcalculation.TopologicalCalculator;



/**
 * Implements basic functionalities for all edits that depend on calculations based on the tree topology
 * and therefore need the tree nodes to be decorated with information in their subtrees. 
 * 
 * @author Ben St&ouml;ver
 */
public abstract class AbstractTopologicalCalculationEdit extends ComplexDocumentEdit {
	public static final String KEY_LEAF_REFERENCE = AbstractTopologicalCalculationEdit.class.getName() + ".LeafSet";
	
	protected TopologicalCalculator topologicalCalculator = null;

	/** The column that contains the terminal identifiers in the target document (usually nodes names) */
	protected NodeBranchDataAdapter targetLeafsAdapter = null;

	
	public AbstractTopologicalCalculationEdit(Document document, DocumentChangeType changeType,
			NodeBranchDataAdapter targetLeafsAdapter, boolean processRooted) {
	
		super(document, changeType);
		this.targetLeafsAdapter = targetLeafsAdapter;
		topologicalCalculator = new TopologicalCalculator(processRooted, KEY_LEAF_REFERENCE, 
						new CompareTextElementDataParameters());
		topologicalCalculator.addToLeafValueToIndexMap(document.getTree().getPaintStart(), targetLeafsAdapter);
	}

	
	public TopologicalCalculator getTopologicalCalculator() {
		return topologicalCalculator;
	}

	
	public NodeBranchDataAdapter getTargetLeafsAdapter() {
		return targetLeafsAdapter;
	}
}