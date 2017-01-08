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
package info.bioinfweb.treegraph.document.undo.format;


import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.change.DocumentChangeType;
import info.bioinfweb.treegraph.document.format.adapters.distance.DistanceAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.AbstractNodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.undo.DocumentEdit;



/**
 * @author Ben St&ouml;ver
 * @since 2.0.23
 */
public class DistanceValuesByNodeBranchDataEdit extends DocumentEdit {
  private NodeBranchDataAdapter sourceAdapter;
  private float min;
  private float factor;
  private boolean changeUndefined;
  private boolean inheritToTerminals;
  private DistanceAdapter[] targetAdapters;
  private DistanceValuesBackup oldValues;
  
  
	public DistanceValuesByNodeBranchDataEdit(Document document, NodeBranchDataAdapter sourceAdapter, 
			float min, float max, boolean changeUndefined, boolean inheritToTerminals, 
			DistanceAdapter[] targetAdapters) {
		
		super(document, DocumentChangeType.POSITION);
		this.sourceAdapter = sourceAdapter;
		this.min = min;
		this.factor = (max - min) / 
		    (float)AbstractNodeBranchDataAdapter.calculateMaxNodeData(sourceAdapter, document.getTree().getPaintStart());
		this.changeUndefined = changeUndefined;
		this.inheritToTerminals = inheritToTerminals;
		this.targetAdapters = targetAdapters;
		
		oldValues = new DistanceValuesBackup(document.getTree(), document.getTree().getPaintStart(), 
				targetAdapters);  // Baum darf nicht leer sein.
	}
	
	
	/**
	 * Sets the branch widths according to the specified range and node data.
	 * @param root
	 */
	private void setNewValues(Node root) {
		float value = (float)sourceAdapter.getDecimal(root);
		if (inheritToTerminals && Double.isNaN(value) && root.isLeaf() && root.hasParent()) {
			for (int i = 0; i < targetAdapters.length; i++) {
				targetAdapters[i].setDistance(targetAdapters[i].getDistance(root.getParent()), root);
			}
		}
		else {
			float distance = Float.NaN;
			if (!Double.isNaN(value)) {
				distance = min + factor * value;
						
			}
			else if (changeUndefined) {
				distance = min;
			}
			if (!Float.isNaN(distance)) {
				for (int i = 0; i < targetAdapters.length; i++) {
					targetAdapters[i].setDistance(distance, root);
				}
			}
		}
		
		for (int i = 0; i < root.getChildren().size(); i++) {
			setNewValues(root.getChildren().get(i));
		}
	}


	@Override
	public void redo() throws CannotRedoException {
		setNewValues(getDocument().getTree().getPaintStart());
		super.redo();
	}
	
	
	@Override
	public void undo() throws CannotUndoException {
		oldValues.restoreValues();
		super.undo();
	}


	public String getPresentationName() {
		return "Set distance values by node/branch data";
	}
}