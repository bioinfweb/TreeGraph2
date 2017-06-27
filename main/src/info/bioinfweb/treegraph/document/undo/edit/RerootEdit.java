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
package info.bioinfweb.treegraph.document.undo.edit;


import java.util.List;

import info.bioinfweb.treegraph.document.Branch;
import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.Label;
import info.bioinfweb.treegraph.document.Labels;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.Tree;
import info.bioinfweb.treegraph.document.change.DocumentChangeType;
import info.bioinfweb.treegraph.document.undo.ComplexDocumentEdit;
import info.bioinfweb.treegraph.document.undo.WarningMessageEdit;



/**
 * Document edit that reroots the tree on a specified branch.
 * 
 * @author Ben St&ouml;ver
 */
public class RerootEdit extends ComplexDocumentEdit implements WarningMessageEdit {
	public static final int DIALOG_HELP_TOPIC = 24;
	
	
  private Branch rootingPoint = null;
  private String warningText = null;
  
  
	public RerootEdit(Document document, Branch rootingPoint) {
		super(document, DocumentChangeType.ROOT_POSITION);
		this.rootingPoint = findEquivalent(rootingPoint);
	}
	
	
	/**
	 * Constructor to be called by {@link RerootByLeafSetEdit} which does not specify a rooting point.
	 * Calling classes must make sure that they specify a rooting point using {@link #setRootingPoint(Branch)}
	 * before the first call if {@link #performRedo()}.
	 * 
	 * @param document - the document that contains the tree to be rerooted 
	 */
	protected RerootEdit(Document document) {
		super(document, DocumentChangeType.ROOT_POSITION);
	}
	
	
  public Branch getRootingPoint() {
		return rootingPoint;
	}


	protected void setRootingPoint(Branch rootingPoint) {
		this.rootingPoint = rootingPoint;
	}


	private static void copyLabels(Labels source, Labels target) {
  	Label labels[] = source.toLabelArray();
  	for (int i = 0; i < labels.length; i++) {
			target.add(labels[i].clone());
		}
  }
  
  
	private static void copyBranchData(Branch source, Branch target) {
		// Merge branch data:
		target.getMetadataTree().setTreeChildren(source.getMetadataTree().getParent());;  // Copying is only helpful, if the two branches contain elements with different IDs. All other elements are overwritten. 
		copyLabels(source.getLabels(), target.getLabels());
		
		// Add up branch length:
		if (source.hasLength()) {
			if (target.hasLength()) {
				target.setLength(source.getLength() + target.getLength());
			}
			else {
				target.setLength(source.getLength());
			}
		}
	}
	

	private static String createWarningMessage(Node root, boolean rootNodeDeleted) {
		String msg = "";
		
		// Root node warnings:
		if (rootNodeDeleted) {
			if (!root.getData().isEmpty() && !"".equals(root.getData().getText())) {
				msg += "- The former root node (which has been deleted) contained the node name \"" + 
			      root.getData().toString() + "\".\n";
			}
			if (!root.getMetadataTree().isEmpty()) {
				msg += "- The former root node (which has been deleted) contained hidden node data.\n";
			}
		}
		
		// Root branch warnings (the root branch is always deleted):
		Branch rootBranch = root.getAfferentBranch();
		if (rootBranch.hasLength()) {
			msg += "- The former root branch (which has been deleted) had a specified length of " + 
							rootBranch.getLength() + ".\n";
		}
		if (!rootBranch.getLabels().isEmpty()) {
			msg += "- The former root branch (which has been deleted) was carrying one or more labels.\n";
		}
		if (!rootBranch.getMetadataTree().isEmpty()) {
			msg += "- The former root branch (which has been deleted) contained hidden branch data.\n";
		}
		
		// Child branch warnings:
		if (rootNodeDeleted) {
			Branch b1 = root.getChildren().get(0).getAfferentBranch(); 
			Branch b2 = root.getChildren().get(1).getAfferentBranch(); 
			if (b1.getLabels().containsSameID(b2.getLabels())) {
				msg += "- The two child branches of the root contained one or more labels with the same ID(s). " +
						"One element of each ID has been deleted.\n";
			}
//			if (b1.getMetadataTree().containsSameID(b2.getMetadataTree())) {
//				msg += "- The two child branches of the root contained hidden branch data with the same ID. " +
//						"One element of each ID has been deleted.\n";
//			}
		}
		
		// Output:
		if (!"".equals(msg)) {
			return "Rerooting produced warnings:\n\n" + msg + "\nYou can use the undo-function to restore lost data.";
		}
		else {
			return null;
		}
	}
	
	
	/**
	 * Returns a warning text, if the last call of {@link #redo()} produced warnings.
	 * @return the warning text or {@code null} if no warning occurred
	 */
	public String getWarningText() {
		return warningText;
	}

  
	public boolean hasWarnings() {
		return warningText != null;
	}
	
	
	/**
	 * If the branch that was selected as the rooting point has a branch length, than this value
	 * is equally separated on both branches leading from the new root to the two subtrees.
	 */
	private static void separateRootBranchLength(Tree tree) {
		List<Node> nodes = tree.getPaintStart().getChildren();
		if (nodes.size() != 2) {
			throw new InternalError("Unexpected tree topology after rerooting.");
		}
		else {
			double halfLength = Double.NaN; 
			if (nodes.get(0).getAfferentBranch().hasLength()) {
				halfLength = 0.5 * nodes.get(0).getAfferentBranch().getLength();
			}
			else if (nodes.get(1).getAfferentBranch().hasLength()) {
				halfLength = 0.5 * nodes.get(1).getAfferentBranch().getLength();
			}
			if (!Double.isNaN(halfLength)) {
				nodes.get(0).getAfferentBranch().setLength(halfLength);
				nodes.get(1).getAfferentBranch().setLength(halfLength);
				tree.getPaintStart().getAfferentBranch().setLength(0.0);  // Make sure the new root has a defined branch length if the child branches do.
			}
		}
	}
	
	
	private static Node createNewRoot(Node rootingPoint) {
		Node result = Node.newInstanceWithBranch();
		result.getFormats().assign(rootingPoint.getFormats());
		result.getFormats().assignLineFormats(rootingPoint.getAfferentBranch().getFormats());  // Use line formats from branch and not from (not directly linked) node.
		result.getAfferentBranch().getFormats().assign(rootingPoint.getAfferentBranch().getFormats());
		return result;
	}
	
	
	private static Branch createNewBranchUnderRoot(Node targetNode) {
		Branch result = new Branch(targetNode);
		result.getFormats().assign(targetNode.getAfferentBranch().getFormats());
		return result;
	}
	
	
	public static String reroot(Tree tree, Branch position) {
		String result = null;
		Node parent = position.getTargetNode().getParent();
		if (parent != null) {  // Otherwise rerooting would not change anything.
			// Save node data:
			List<Node> children = tree.getPaintStart().getChildren();
			boolean collapseFormerRoot = (children.size() == 2);
			result = createWarningMessage(tree.getPaintStart(), collapseFormerRoot);
			if (collapseFormerRoot) {
				if (children.get(0).containedInSubtree(position)) {  // Depending on the position of the new root the one or the other child will be deleted in the end.
					copyBranchData(children.get(0).getAfferentBranch(), children.get(1).getAfferentBranch());
				}
				else {
					copyBranchData(children.get(1).getAfferentBranch(), children.get(0).getAfferentBranch());
				}				
			}
			
			// Structural changes:
			Node current = createNewRoot(position.getTargetNode());
			tree.setPaintStart(current);
			parent.getChildren().remove(position.getTargetNode());
			current.getChildren().add(position.getTargetNode());
			position.getTargetNode().setParent(current);
			position = parent.getAfferentBranch();
			parent.setAfferentBranch(createNewBranchUnderRoot(parent));
			current.getChildren().add(0, parent);  // Conserve order of terminal nodes
			Node last = current;
			current = parent;
			parent = parent.getParent();
			current.setParent(last);
			while (parent != null) {
				current.getChildren().add(0, parent);
				last = current;
				current = parent;
				parent = parent.getParent();
				Branch b2 = current.getAfferentBranch();
				current.setAfferentBranch(position);
				position = b2;
				current.getChildren().remove(last);
				current.setParent(last);
			}
			separateRootBranchLength(tree);
			
			// Delete remaining internal node, if necessary:
			if (collapseFormerRoot) {
				parent = current.getParent();
				parent.getChildren().remove(current);
				parent.getChildren().add(current.getChildren().get(0));
				current.getChildren().get(0).setParent(parent);
				// current is now no longer part of the tree
			}
		}
		return result;
	}
	
	
	@Override
	protected void performRedo() {
		warningText = reroot(getDocument().getTree(), rootingPoint);
	}


	public String getPresentationName() {
		return "Reroot tree";
	}
}