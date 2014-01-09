/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2014  Ben Stöver, Kai Müller
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

import javax.swing.JOptionPane;

import info.bioinfweb.treegraph.Main;
import info.bioinfweb.treegraph.document.Branch;
import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.Label;
import info.bioinfweb.treegraph.document.Labels;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.undo.ComplexDocumentEdit;
import info.webinsel.wikihelp.client.WikiHelpOptionPane;



public class RerootEdit extends ComplexDocumentEdit {
	public static final int DIALOG_HELP_TOPIC = 24;
	
	
  private Branch rootingPoint = null;
  
  
	public RerootEdit(Document document, Branch rootingPoint) {
		super(document);
		this.rootingPoint = findEquivilant(rootingPoint);
	}
	
	
  private void copyLabels(Labels source, Labels target) {
  	Label labels[] = source.toLabelArray();
  	for (int i = 0; i < labels.length; i++) {
			target.add(labels[i].clone());
		}
  }
  
  
	private void copyBranchData(Branch source, Branch target) {
		target.getHiddenDataMap().putAll(source.getHiddenDataMap());
		copyLabels(source.getLabels(), target.getLabels());
	}
	

	private void showWarnings() {
		Node root = document.getTree().getPaintStart();
		String msg = "";
		
		if (!root.getData().isEmpty()) {
			msg += "- The former root node (which has been deleted) contained the node name \"" + root.getData().toString() + "\".\n";
		}
		if (!root.getHiddenDataMap().isEmpty()) {
			msg += "- The former root node (which has been deleted) contained hidden node data.\n";
		}
		
		Branch b1 = root.getChildren().get(0).getAfferentBranch(); 
		Branch b2 = root.getChildren().get(1).getAfferentBranch(); 
		if (b1.getLabels().containsSameID(b2.getLabels())) {
			msg += "- The two child branches of the root contained labels with the same ID. The remaining branch now carries multiple labels with the same ID.\n";
		}
		if (b1.getHiddenDataMap().containsSameID(b2.getHiddenDataMap())) {
			msg += "- The two child branches of the root contained hidden branch data with the same ID. One element of each ID has been deleted.\n";
		}
		
		if (!"".equals(msg)) {
			WikiHelpOptionPane.showMessageDialog(null, "Rerooting produced warnings:\n\n" + 
					msg + "\nYou can use the undo-function to restore lost data.", "Reroot",	
					JOptionPane.WARNING_MESSAGE, Main.getInstance().getWikiHelp(), 27);
		}
	}
	
	
	private void reroot(Branch position) {
		Node parent = position.getTargetNode().getParent();
		if (parent != null) {  // Sonst wäre die Neuwurzelung ohne Auswirkung
			// Knotendaten retten:
			List<Node> children = document.getTree().getPaintStart().getChildren();
			boolean collapseFormerRoot = (children.size() == 2);
			if (collapseFormerRoot) {
				showWarnings();
				if (children.get(0).containedInSubtree(position)) {  // Je nach Position der neuen Wurzel wird später der eine oder der andere Knoten gelöscht.
					copyBranchData(children.get(0).getAfferentBranch(), children.get(1).getAfferentBranch());
				}
				else {
					copyBranchData(children.get(1).getAfferentBranch(), children.get(0).getAfferentBranch());
				}
				
			}
			
			// Strukturänderungen:
			Node current = Node.getInstanceWithBranch();
			document.getTree().setPaintStart(current);
			parent.getChildren().remove(position.getTargetNode());
			current.getChildren().add(position.getTargetNode());
			position.getTargetNode().setParent(current);
			position = parent.getAfferentBranch();
			parent.setAfferentBranch(new Branch(parent));
			current.getChildren().add(parent);
			Node last = current;
			current = parent;
			parent = parent.getParent();
			current.setParent(last);
			while (parent != null) {
				current.getChildren().add(parent);
				last = current;
				current = parent;
				parent = parent.getParent();
				Branch b2 = current.getAfferentBranch();
				current.setAfferentBranch(position);
				position = b2;
				current.getChildren().remove(last);
				current.setParent(last);
			}
			
			// Ggf. überschüssegen inneren Knoten löschen und Daten verschieben:
			if (collapseFormerRoot) {
				parent = current.getParent();
				parent.getChildren().remove(current);
				parent.getChildren().add(current.getChildren().get(0));
				current.getChildren().get(0).setParent(parent);
				// current ist nun nicht mehr Teil des Baums
			}
		}
	}
	
	
	@Override
	protected void performRedo() {
		reroot(rootingPoint);
	}


	public String getPresentationName() {
		return "Reroot tree";
	}
}