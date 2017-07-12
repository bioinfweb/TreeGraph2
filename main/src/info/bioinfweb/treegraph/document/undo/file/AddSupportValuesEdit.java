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
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.NodeType;
import info.bioinfweb.treegraph.document.TextElementData;
import info.bioinfweb.treegraph.document.change.DocumentChangeType;
import info.bioinfweb.treegraph.document.nodebranchdata.AbstractTextElementDataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.TextElementDataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.TextLabelAdapter;
import info.bioinfweb.treegraph.document.tools.TextElementDataAsStringIterator;
import info.bioinfweb.treegraph.document.tools.TreeSerializer;
import info.bioinfweb.treegraph.document.topologicalcalculation.NodeInfo;
import info.bioinfweb.treegraph.document.undo.AbstractTopologicalCalculationEdit;
import info.bioinfweb.treegraph.document.undo.WarningMessageEdit;
import info.bioinfweb.treegraph.gui.actions.DocumentAction;

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Set;



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
	private String warningMessage = null;
	
	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param targetDocument the document to add the support values to
	 * @param parameters the parameter object for this edit
	 */
	public AddSupportValuesEdit(Document targetDocument, AddSupportValuesParameters parameters) {
		super(targetDocument, DocumentChangeType.TOPOLOGICAL_BY_RENAMING, parameters.getTerminalsAdapter(), parameters.isRooted());  // Topological relevance only if the default leaves adapter ID is affected.
		this.sourceDocument = parameters.getSourceDocument();
		this.sourceSupportAdapter = parameters.getSourceSupportColumn();
		this.sourceLeavesAdapter = parameters.getSourceLeavesColumn();
		this.parseNumericValues = parameters.isParseNumericValues();
		
		targetSupportAdapter = parameters.getTargetType().createAdapterInstance(
				parameters.getIDPrefix() + SUPPORT_NAME, SUPPORT_DECIMAL_FORMAT);
		targetConflictAdapter = parameters.getTargetType().createAdapterInstance(
				parameters.getIDPrefix() + CONFLICT_NAME, CONFLICT_DECIMAL_FORMAT);
		getTopologicalCalculator().addSubtreeToLeafValueToIndexMap(sourceDocument.getTree().getPaintStart(), sourceLeavesAdapter);  //TODO Must be a different calculator then used in the inherited constructor. Otherwise the indices for the target document are wrong, if the order differs.
	}
	
	
	@Override
	protected void performRedo() {
		//topologicalCalculator.addLeafValueToIndexMap(getDocument().getTree().getPaintStart(), getTargetLeavesAdapter());  // Is already done in the inherited constructor.
		getTopologicalCalculator().addLeafSets(sourceDocument.getTree().getPaintStart(), sourceLeavesAdapter);
		getTopologicalCalculator().addLeafSets(getDocument().getTree().getPaintStart(), getTargetLeavesAdapter());
		processSubtree(getDocument().getTree().getPaintStart());
		
		warningMessage = createWarningMessage();
	}

	
	private boolean checkOtherPaintStartBranch(Node sourceNode) {
		return !getTopologicalCalculator().isProcessRooted() &&  // no root node between the two branches 
				sourceNode.hasParent() && !sourceNode.getParent().hasParent() &&  // the source node is attached to the paint start   
				(sourceNode.getParent().getChildren().size() == 2);  // no polytomy at the paint start
	}
	
	
	/**
	 * Finds the support or conflict values in the source document.
	 * 
	 * @param targetRoot - the root of the subtree to add support values to (a node of the 
	 *        target document)
	 */
	private void processSubtree(Node targetRoot) {
		if (!targetRoot.isLeaf()) {
			if (!(targetRoot.hasParent() && !targetRoot.getParent().hasParent() && !getTopologicalCalculator().isProcessRooted() && 
					(targetRoot.getParent().getChildren().size() == 2) && targetRoot.isLast())) {  // Check if the current node is linked to the paint start which does not represent a root and the other linked branch already carries the same support value.
				
				NodeInfo bestSourceNode = getTopologicalCalculator().findSourceNodeWithAllLeaves(sourceDocument.getTree(), 
								getTopologicalCalculator().getLeafSet(targetRoot));
				if (bestSourceNode != null) {
					Node conflict = getTopologicalCalculator().findHighestConflict(getDocument().getTree(), 
							sourceDocument.getTree(), bestSourceNode.getNode(), getTopologicalCalculator().getLeafSet(targetRoot), 
							getTopologicalCalculator().getLeafSet(bestSourceNode.getNode()), sourceSupportAdapter);
					
					if (conflict == null) {  // support found
	  				if (sourceSupportAdapter.isDecimal(bestSourceNode.getNode())) {  // if no value exists there
		  				targetSupportAdapter.setDecimal(targetRoot, sourceSupportAdapter.getDecimal(bestSourceNode.getNode()));  // Only decimal values can appear here.
						}
	  				else if (checkOtherPaintStartBranch(bestSourceNode.getNode())) {
							int index = 1;
							if (bestSourceNode.getNode().isLast()) {
								index = 0;
							}
							Node other = bestSourceNode.getNode().getParent().getChildren().get(index);
							if (sourceSupportAdapter.isDecimal(other)) {
			  				targetSupportAdapter.setDecimal(targetRoot, sourceSupportAdapter.getDecimal(other));  // Only decimal values can appear here.
							}
							else if (parseNumericValues) {
								try {
									targetSupportAdapter.setDecimal(targetRoot, Double.parseDouble(sourceSupportAdapter.getText(other)));
								}
								catch (Exception e) {}
							}
	  				}
	  				else if (parseNumericValues) {
	  					try {
								targetSupportAdapter.setDecimal(targetRoot, Double.parseDouble(sourceSupportAdapter.getText(bestSourceNode.getNode())));
							}
							catch (Exception e) {}
	  				}
					}
					else if (bestSourceNode.getAdditionalCount() == -1) {
						throw new InternalError("-1 RETURNED");  // Should not happen.
					}
					else {  // conflict found
						double value = getSupportValue(conflict);
						if (!Double.isNaN(value) && (value != 0)) {
		  				targetConflictAdapter.setDecimal(targetRoot, value);
						}
					}
				}
			}
			for (int i = 0; i < targetRoot.getChildren().size(); i++) {
				processSubtree(targetRoot.getChildren().get(i));
			}
		}
	}
	
	
	public double getSupportValue(Node node) {
		if (sourceSupportAdapter.isDecimal(node)) {
			return sourceSupportAdapter.getDecimal(node);
		}
		else if (parseNumericValues) {
			try {
				Double supportValue = Double.parseDouble(sourceSupportAdapter.getText(node));
				return supportValue;
			}
			catch (Exception e) {
				return Double.NaN;
			}
		}
		else {
			return Double.NaN;
		}
	}
	
	
	public String getPresentationName() {
		return "Add more support values";
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
		
		if (remainingSourceLeaves.isEmpty()) {
			return null;
		}
		else {
			return "There were no according terminal nodes in the target document for the following terminal nodes from the source "
					+ "document found:\n\n" + DocumentAction.createElementList(new TextElementDataAsStringIterator(
							remainingSourceLeaves.iterator()), remainingSourceLeaves.size(), true)
					+ "\n\nAccordingly some support values may not have been imported from the source tree (which may have been desired).\n"
					+ "(Note that matching also may have failed because the specified leaf node/branch data columns for the source or "
					+ "target document were not correct.)";
		}
	}
}