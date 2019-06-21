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


import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.NodeType;
import info.bioinfweb.treegraph.document.TextElementData;
import info.bioinfweb.treegraph.document.change.DocumentChangeType;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.TextElementDataAdapter;
import info.bioinfweb.treegraph.document.tools.TextElementDataAsStringIterator;
import info.bioinfweb.treegraph.document.tools.TreeSerializer;
import info.bioinfweb.treegraph.document.topologicalcalculation.LeafSet;
import info.bioinfweb.treegraph.document.topologicalcalculation.NodeInfo;
import info.bioinfweb.treegraph.document.undo.AbstractTopologicalCalculationEdit;
import info.bioinfweb.treegraph.document.undo.WarningMessageEdit;
import info.bioinfweb.treegraph.gui.actions.DocumentAction;



/**
 * Edit that imports support values from another tree into the current document. Supporting and conflicting
 * values are imported.
 * 
 * @author Ben St&ouml;ver
 * @author Sarah Wiechers
 */
public class AddSupportValuesEdit extends AbstractTopologicalCalculationEdit implements WarningMessageEdit {
	public static final String SUPPORT_NAME = "Support";
	public static final String CONFLICT_NAME = "Conflict";
	public static final DecimalFormat SUPPORT_DECIMAL_FORMAT = 
		  new DecimalFormat("#.#######");
	public static final DecimalFormat CONFLICT_DECIMAL_FORMAT =
		  new DecimalFormat("[#.#######];[-#.#######]");
	
	
	/** The document from which support values are imported */
	private Document sourceDocument = null;
	
	/** The node/branch data adapter containing support values to be imported. */
	private NodeBranchDataAdapter sourceSupportAdapter = null;
	
	/** The node/branch data adapter identifying the leaves in the document to be imported. */
	private NodeBranchDataAdapter sourceLeavesAdapter = null;
	
	/** The node/branch data column to write imported support values to. */
	private TextElementDataAdapter targetSupportAdapter = null;
	
	/** The node/branch data column to write imported conflict values to. */
	private TextElementDataAdapter targetConflictAdapter = null;
	
	private boolean parseNumericValues;
	private EquivalentBranchHandler equivalentBranchHandler;
	private String warningMessage = null;
	private boolean multipleValuesMappedToOneNode;
	
	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param targetDocument the document to add the support values to
	 * @param parameters the parameter object for this edit
	 */
	public AddSupportValuesEdit(Document targetDocument, AddSupportValuesParameters parameters) {
		super(targetDocument, DocumentChangeType.TOPOLOGICAL_BY_RENAMING, parameters.getTerminalsAdapter(), parameters.isRooted());  // This edit only has a topological relevance if the default leaves adapter is used to store values.
		this.sourceDocument = parameters.getSourceDocument();
		this.sourceSupportAdapter = parameters.getSourceSupportColumn();
		this.sourceLeavesAdapter = parameters.getSourceLeavesColumn();
		this.parseNumericValues = parameters.isParseNumericValues();
		this.equivalentBranchHandler = parameters.getEquivalentBranchHandler();
		
		targetSupportAdapter = parameters.getTargetType().createAdapterInstance(parameters.getIDPrefix() + SUPPORT_NAME, SUPPORT_DECIMAL_FORMAT);
		targetConflictAdapter = parameters.getTargetType().createAdapterInstance(parameters.getIDPrefix() + CONFLICT_NAME, CONFLICT_DECIMAL_FORMAT);
		getTopologicalCalculator().filterIndexMapBySubtree(sourceDocument.getTree().getPaintStart(), sourceLeavesAdapter);  // Remove all leaves from the index map that are not present in the source tree, as well. (Leaves from the target tree have already been added in the super constructor.)
	}
	
	
	@Override
	protected void performRedo() {
		getTopologicalCalculator().addLeafSets(sourceDocument.getTree().getPaintStart(), sourceLeavesAdapter);  // Only leaves present in both trees will be considered, since
		getTopologicalCalculator().addLeafSets(getDocument().getTree().getPaintStart(), getTargetLeavesAdapter());  // filterIndexMapBySubtree() was called in the constructor.
		// (Adding these leave sets must happen after filterIndexMapBySubtree(), since this methods may change indices of terminals.)
		
		multipleValuesMappedToOneNode = false;
		processSubtree(getDocument().getTree().getPaintStart());
		warningMessage = createWarningMessage();
	}

	
	private boolean hasTwoOrMoreSharedTerminalsOnBothSides(Node node) {
		LeafSet leafSet = getTopologicalCalculator().getLeafSet(node);
		return (leafSet.childCount() >= 2) && (leafSet.complement().childCount() >= 2);
	}
	
	
	/**
	 * Finds the support or conflict values in the source document.
	 * 
	 * @param sourceRoot the root of the subtree to add support values to (a node of the target document)
	 */
	private void processSubtree(Node targetRoot) {
		LeafSet leafSet = getTopologicalCalculator().getLeafSet(targetRoot);
		
		if (hasTwoOrMoreSharedTerminalsOnBothSides(targetRoot)) {
			List<NodeInfo> bestSourceNodes = getTopologicalCalculator().findNodeWithAllLeaves(sourceDocument.getTree(), leafSet, null);  // An empty list should never be returned here, since two shared terminals were ensured to be present.
			
			if (bestSourceNodes.get(0).getAdditionalCount() == 0) {  // Exact match found.
				multipleValuesMappedToOneNode = equivalentBranchHandler.handleBranches(bestSourceNodes, targetRoot, sourceSupportAdapter, targetSupportAdapter, parseNumericValues) 
						|| multipleValuesMappedToOneNode;  // The condition must be specified in this order. Otherwise handleBranches() would not be called anymore as soon as multipleValuesMappedToOneNode is true.
				System.out.println(multipleValuesMappedToOneNode);
			}
			else {  // There must be a conflict, since no direct matching group of shared terminals was found.
				Node conflict = getTopologicalCalculator().findHighestConflict(bestSourceNodes.get(0).getNode(), leafSet, sourceSupportAdapter, parseNumericValues);
						// The first node in the list is the closest to the root. This one should be used as the search starting point if multiple topologically equivalent nodes (regarding the shared set of terminals) are present.
				if ((conflict != null) && hasTwoOrMoreSharedTerminalsOnBothSides(conflict)) {
					targetConflictAdapter.setDecimal(targetRoot, sourceSupportAdapter.getNumericValue(conflict, parseNumericValues));  // getSupportValue() should never return NaN here, since the numeric values were already compared before.
				}
			}
		}
		
		for (Node child : targetRoot.getChildren()) {
			processSubtree(child);
		}
	}
	
	
	public String getPresentationName() {
		return "Add support values from another tree";
	}


	@Override
	public String getWarningText() {
		return warningMessage;
	}


	@Override
	public boolean hasWarnings() {
		return warningMessage != null;
	}
	
	
	private String createWarningMessage() {
		Set<TextElementData> targetLeaves = TreeSerializer.addTextElementDataCopiesFromSubtree(new HashSet<TextElementData>(), 
				getDocument().getTree().getPaintStart(), NodeType.LEAVES, targetLeavesAdapter, null);
		Set<TextElementData> remainingSourceLeaves = TreeSerializer.addTextElementDataCopiesFromSubtree(new HashSet<TextElementData>(), 
				sourceDocument.getTree().getPaintStart(), NodeType.LEAVES, sourceLeavesAdapter, targetLeaves);

		int index = -1;  // Do not use index.
		if (multipleValuesMappedToOneNode && !remainingSourceLeaves.isEmpty()) {
			index = 1;  // Use index if two messages are there.
		}
		StringBuilder result = new StringBuilder();
		
		if (multipleValuesMappedToOneNode) {
			if (index > -1) {
				result.append(index);
				result.append(") ");
				index++;
			}
			
			result.append("For one or more nodes in the target document multiple topologically equivalent nodes with support values were found in the source document.\n");
			result.append("(This can happen if the source document contains terminal nodes with no equivalent in the source document.)\n\n");
			result.append("A list of support values was mapped onto the respective taget node(s). You can edit these imports and highlight their source nodes using the\n");
			result.append("selection synchronization feature.\n\n\n");
		}
		
		if (!remainingSourceLeaves.isEmpty()) {
			if (index > -1) {
				result.append(index);
				result.append(") ");
			}
			result.append("There were no respective terminal nodes in the target document for the following terminal nodes from the source document found:\n\n");
			result.append(DocumentAction.createElementList(new TextElementDataAsStringIterator(remainingSourceLeaves.iterator()), remainingSourceLeaves.size(), true));
			result.append("\n\nConsequently, some support values may not have been imported from the source tree (which may have been desired).\n");
			result.append("(Note that matching also may have failed because the specified leaf node/branch data columns for the source or target document were not correct.)");
		}

		if (result.length() == 0) {
			return null;
		}
		else {
			return result.toString();
		}
	}
}