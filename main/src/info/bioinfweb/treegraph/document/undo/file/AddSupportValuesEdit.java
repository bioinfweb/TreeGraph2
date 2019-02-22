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
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.TextElementDataAdapter;
import info.bioinfweb.treegraph.document.tools.TextElementDataAsStringIterator;
import info.bioinfweb.treegraph.document.tools.TreeSerializer;
import info.bioinfweb.treegraph.document.topologicalcalculation.LeafSet;
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
		super(targetDocument, DocumentChangeType.TOPOLOGICAL_BY_RENAMING, parameters.getTerminalsAdapter(), parameters.isRooted());  // This edit only has a topological relevance if the default leaves adapter is used to store values.
		this.sourceDocument = parameters.getSourceDocument();
		this.sourceSupportAdapter = parameters.getSourceSupportColumn();
		this.sourceLeavesAdapter = parameters.getSourceLeavesColumn();
		this.parseNumericValues = parameters.isParseNumericValues();
		
		targetSupportAdapter = parameters.getTargetType().createAdapterInstance(parameters.getIDPrefix() + SUPPORT_NAME, SUPPORT_DECIMAL_FORMAT);
		targetConflictAdapter = parameters.getTargetType().createAdapterInstance(parameters.getIDPrefix() + CONFLICT_NAME, CONFLICT_DECIMAL_FORMAT);
		getTopologicalCalculator().filterIndexMapBySubtree(sourceDocument.getTree().getPaintStart(), sourceLeavesAdapter);  // Remove all leaves from the index map that are not present in the source tree, as well. (Leaves from the target tree have already been added in the super constructor.)
	}
	
	
	@Override
	protected void performRedo() {
		getTopologicalCalculator().addLeafSets(sourceDocument.getTree().getPaintStart(), sourceLeavesAdapter);  // Only leaves present in both trees will be considered, since
		getTopologicalCalculator().addLeafSets(getDocument().getTree().getPaintStart(), getTargetLeavesAdapter());  // filterIndexMapBySubtree() was called in the constructor.
		// (Adding these leave sets must happen after filterIndexMapBySubtree(), since this methods may change indices of terminals.)
		
		processSubtree(sourceDocument.getTree().getPaintStart());
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
	private void processSubtree(Node sourceRoot) {
		double importedValue = getSupportValue(sourceRoot);  //TODO In some cases the other paint start branch might have to be checked in addition or is this already covered by visiting all branches?
		if (!Double.isNaN(importedValue) && hasTwoOrMoreSharedTerminalsOnBothSides(sourceRoot)) {  // Check if a support value is present and its branch separated at least two shared terminals on each side.
			NodeInfo bestTargetNode = getTopologicalCalculator().findNodeWithAllLeaves(getDocument().getTree(), 
					getTopologicalCalculator().getLeafSet(sourceRoot));
			
			if (bestTargetNode != null) {
				if (bestTargetNode.getAdditionalCount() == 0) {  //TODO Is checking for conflicts still necessary here? (It should not be, because only shared terminals were considered.)
					targetSupportAdapter.setDecimal(bestTargetNode.getNode(), importedValue);
				}
				else {
					Node conflict = getTopologicalCalculator().findHighestConflict(sourceDocument.getTree(), 
							getDocument().getTree(), bestTargetNode.getNode(), getTopologicalCalculator().getLeafSet(sourceRoot), 
							getTopologicalCalculator().getLeafSet(bestTargetNode.getNode()), sourceSupportAdapter);  //TODO Check later if this method can also be simplified now that combined leaf sets are used.
					
					if (conflict != null) {
						targetConflictAdapter.setDecimal(bestTargetNode.getNode(), getSupportValue(conflict));  // getSupportValue() should never return NaN here, since the numeric values were already compared before.
					}
					else {
						System.out.println("conflict == null");
					}
				}
			}
			else {  // Should not happen, since if was checked that two shared terminals are present on both sides. 
				throw new InternalError("No target node could be found. This is an unexpected internal error. Please inform support@bioinfweb.info "
						+ "if you see this message or send an error report.");
			}
		}
		
		for (Node child : sourceRoot.getChildren()) {
			processSubtree(child);
		}
		
//		        //TODO Should this be called "combinedSourceLeafSet"?
//		LeafSet sourceLeafSet = getTopologicalCalculator().getLeafSet(sourceRoot).and(
//					getTopologicalCalculator().getLeafSet(getDocument().getTree().getPaintStart()));
//		if (!sourceRoot.isLeaf() && (sourceLeafSet.childCount() > 1)) {  // The second condition (and the one below) test the same as the first for the case of different sets of terminals.  //TODO The same AND operation is repeated in findSourceNodeWithAllLeaves() below. This could be combined in the future.
//			if ((sourceLeafSet.complement().childCount() > 1) &&  // This must not be done in the statement above, since iterating over the children still needs to be done.
//					!(sourceRoot.hasParent() && !sourceRoot.getParent().hasParent() &&  // The current source node is a direct child of the root. 
//								!getTopologicalCalculator().isProcessRooted() && 
//								/*hasTwoOrMoreSharedTerminalsOnBothSides(sourceRoot, )*/ (sourceRoot.getParent().getChildren().size() == 2))) {  // Check if the current node is linked to the paint start which does not represent a root.
//								//TODO Count only children that are in the combined leaf set or are internal nodes using hasTwoOrMoreSharedTerminalsOnBothSides().
//				
//				NodeInfo bestTargetNode = getTopologicalCalculator().findSourceNodeWithAllLeaves(getDocument().getTree(), sourceLeafSet);
////				if ("x7474zcnij".equals(bestTargetNode.getNode().getUniqueName())) {
////					System.out.println("Importing to node " + sourceLeafSet.complement());
////				}
//				if (bestTargetNode != null) {
//					Node conflict = getTopologicalCalculator().findHighestConflict(sourceDocument.getTree(), 
//							getDocument().getTree(), bestTargetNode.getNode(), getTopologicalCalculator().getLeafSet(sourceRoot), 
//							getTopologicalCalculator().getLeafSet(bestTargetNode.getNode()), sourceSupportAdapter);
//					
//					if (conflict == null) {  // Support found
//						if (bestTargetNode.getAdditionalCount() == 0) {
//							if (sourceSupportAdapter.isDecimal(sourceRoot)) {  // If no value exists there
//								targetSupportAdapter.setDecimal(bestTargetNode.getNode(), sourceSupportAdapter.getDecimal(sourceRoot));  // Only decimal values can appear here.
//							}
//							else if (checkOtherPaintStartBranch(bestTargetNode.getNode())) {
//								int index = 1;
//								if (bestTargetNode.getNode().isLast()) {
//									index = 0;
//								}
//								Node other = bestTargetNode.getNode().getParent().getChildren().get(index);
//								if (sourceSupportAdapter.isDecimal(sourceRoot)) {
//									targetSupportAdapter.setDecimal(other, sourceSupportAdapter.getDecimal(sourceRoot));  // Only decimal values can appear here.
//								}
//								else if (parseNumericValues) {
//									try {
//										targetSupportAdapter.setDecimal(other, Double.parseDouble(sourceSupportAdapter.getText(sourceRoot)));
//									}
//									catch (Exception e) {}
//								}
//							}
//							else if (parseNumericValues) {
//								try {
//									targetSupportAdapter.setDecimal(bestTargetNode.getNode(), Double.parseDouble(sourceSupportAdapter.getText(sourceRoot)));
//								}
//								catch (Exception e) {}
//							}
//						}
//						else {
//							System.out.println("value not imported");  //TODO Create info message here that values were not imported due to an unrooted comparison.
//						}
//					}
//					else if (bestTargetNode.getAdditionalCount() == -1) {
//						throw new InternalError("An unexpected error has occurred. (Undefined bestTargetNode.getAdditionalCount().) "
//								+ "Please contact the developers at support@bioinfweb.info.");  // Should not happen.
//					}
//					else {  // conflict found						
//						double value = getSupportValue(sourceRoot);
//						if (!Double.isNaN(value) && (value != 0)) {
//							targetConflictAdapter.setDecimal(conflict, value);
//						}
//					}
//				}
//			}
//			for (int i = 0; i < sourceRoot.getChildren().size(); i++) {
//				processSubtree(sourceRoot.getChildren().get(i));
//			}
//		}
	}
	
	
	private double getSupportValue(Node node) {
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
			return "There were no respective terminal nodes in the target document for the following terminal nodes from the source "
					+ "document found:\n\n" + DocumentAction.createElementList(new TextElementDataAsStringIterator(
							remainingSourceLeaves.iterator()), remainingSourceLeaves.size(), true)
					+ "\n\nConsequently, some support values may not have been imported from the source tree (which may have been desired).\n"
					+ "(Note that matching also may have failed because the specified leaf node/branch data columns for the source or "
					+ "target document were not correct.)";
		}
	}
}