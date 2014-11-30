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


import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.undo.ComplexDocumentEdit;



public class RescaleNodeDataEdit extends ComplexDocumentEdit {
	private NodeBranchDataAdapter adapter = null;
	private double factor;
	private double addend;
	
	
  public RescaleNodeDataEdit(Document document, NodeBranchDataAdapter adapter, double factor, double addend) {
		super(document);
		this.adapter = adapter;
		this.factor = factor;
		this.addend = addend;
	}


	public void rescale(Node root) {
		double value = adapter.getDecimal(root);
		
		if (!Double.isNaN(value)) {
			adapter.setDecimal(root, value * factor + addend);
		}
  	
  	for (int i = 0; i < root.getChildren().size(); i++) {
 			rescale(root.getChildren().get(i));
		}
  }


	@Override
	protected void performRedo() {
		if (!getDocument().getTree().isEmpty()) {
			rescale(getDocument().getTree().getPaintStart());
		}
	}


	public String getPresentationName() {
		return "Rescale " + adapter.toString() + " by " + factor;
	}
}