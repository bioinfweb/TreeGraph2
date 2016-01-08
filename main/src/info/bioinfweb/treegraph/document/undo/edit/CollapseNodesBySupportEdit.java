/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2015-2016  Ben Stöver, Sarah Wiechers
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


import java.util.Iterator;

import javax.swing.undo.CannotRedoException;

import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.change.DocumentChangeType;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.undo.ComposedDocumentEdit;
import info.bioinfweb.treegraph.document.undo.DocumentEdit;
import info.bioinfweb.treegraph.document.undo.WarningMessageEdit;



/**
 * Collapses all nodes in the specified subtree that have a support value in their afferent branch under the 
 * specified threshold.
 *
 * @author Ben St&ouml;ver
 * @since 2.2.0
 * @see DeleteOutsideIntervalEdit
 */
public class CollapseNodesBySupportEdit extends ComposedDocumentEdit implements WarningMessageEdit {
  private NodeBranchDataAdapter supportAdapter;
  private double threshold = -1.0;
  private boolean legendsReanchored = false;
  private boolean nodeBranchDataMissing = false;
  private boolean nodeBranchDataInvalid = false;
  
  
	public CollapseNodesBySupportEdit(Document document, Node root, NodeBranchDataAdapter supportAdapter, double threshold) {
	  super(document, DocumentChangeType.TOPOLOGICAL_LEAF_INVARIANT);
	  this.supportAdapter = supportAdapter;
	  this.threshold = threshold;
	  
	  createSubedits(root);
  }


	public boolean getLegendsReanchored() {
		return legendsReanchored;
	}


	public boolean getNodeBranchDataMissing() {
		return nodeBranchDataMissing;
	}


	public boolean getNodeBranchDataInvalid() {
		return nodeBranchDataInvalid;
	}


	private void createSubedits(Node root) {
		if (!root.isLeaf()) {
			if (supportAdapter.isEmpty(root)) {
				nodeBranchDataMissing = true;
			}
			else if (!supportAdapter.isDecimal(root)) {
				nodeBranchDataInvalid = true;
			}
			else if (supportAdapter.getDecimal(root) < threshold) {
				CollapseNodeEdit edit = new CollapseNodeEdit(getDocument(), root);
				edit.setIsSubedit(true);
				getEdits().add(edit);
			}
			
			for (Node child : root.getChildren()) {
		    createSubedits(child);
	    }
		}
	}

	
	private void checkEditsForWarnings() {
		Iterator<DocumentEdit> iterator = getEdits().iterator();
		while (iterator.hasNext() && !legendsReanchored) {  // Legends are never removed by a CollapseNodeEdit.
			legendsReanchored = legendsReanchored || ((CollapseNodeEdit)iterator.next()).getLegendsReanchored();
		}
	}
	
	
	@Override
  public void redo() throws CannotRedoException {
	  super.redo();
	  checkEditsForWarnings();
  }


	@Override
  public String getPresentationName() {
	  return "Collapse nodes in subtree with support in " + supportAdapter + " below " + threshold;
  }


	@Override
  public String getWarningText() {
		String msg = "";
		if (getNodeBranchDataInvalid()) {
			msg += "- One or more node(s) in the affected subtree did contain non-numerical values in the specified " + 
					"node/branch data column. (Make sure that all relevant values are marked as decimal values and not as strings.)\n";
		}
		if (getNodeBranchDataMissing()) {
			msg += "- One or more node(s) in the affected subtree did not carry any value in the specified node/branch data column.\n";
		}
		if (getLegendsReanchored()) {
			msg += "- One or more legend(s) that were anchored inside the affected subtree were reanchored.\n";
		}
		if ("".equals(msg)) {
			return null;
		}
		else {
			return "This process produced warnings:\n\n" + msg + "\nYou can use the undo-function to restore lost or changed data.";
		}
  }


	@Override
  public boolean hasWarnings() {
	  return getNodeBranchDataMissing() || getNodeBranchDataInvalid() || getLegendsReanchored();
  }
}
