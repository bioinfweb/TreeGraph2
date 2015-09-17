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
package info.bioinfweb.treegraph.document.undo.format;


import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.change.DocumentChangeType;
import info.bioinfweb.treegraph.document.format.adapters.color.ColorAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.AbstractNodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.undo.DocumentEdit;

import java.awt.Color;
import java.util.Vector;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;



public class ColorsByNodeBranchDataEdit extends DocumentEdit {
  private NodeBranchDataAdapter sourceAdapter;
  private Color min;
	double redFactor;
	double greenFactor;
	double blueFactor;
  private boolean changeUndefined;
  private boolean inheritToTerminals;
  private ColorAdapter[] targetAdapters;
  private Vector<Color>[] oldColors;
  
  
	public ColorsByNodeBranchDataEdit(Document document, NodeBranchDataAdapter sourceAdapter, 
			Color min, Color max, boolean changeUndefined, boolean inheritToTerminals, 
			ColorAdapter[] targetAdapters) {
		
		super(document, DocumentChangeType.NEUTRAL);
		this.sourceAdapter = sourceAdapter;
		this.min = min;
		this.changeUndefined = changeUndefined;
		this.inheritToTerminals = inheritToTerminals;
		this.targetAdapters = targetAdapters;
		
		double maxValue = AbstractNodeBranchDataAdapter.calculateMaxNodeData(
				sourceAdapter, document.getTree().getPaintStart());
		redFactor = ((double)(max.getRed() - min.getRed())) / maxValue;
		greenFactor = ((double)(max.getGreen() - min.getGreen())) / maxValue;
		blueFactor = ((double)(max.getBlue() - min.getBlue())) / maxValue;
		
		saveOldColors();  // Baum darf nicht leer sein.
	}
	
	
	private void saveOldColors() {
		oldColors = new Vector[targetAdapters.length];
		for (int i = 0; i < oldColors.length; i++) {
			oldColors[i]  = new Vector<Color>();
		}
		saveOldSubtreeColors(getDocument().getTree().getPaintStart());
	}
	
	
	/**
	 * Saves the old colors. (Ordering is root, 
	 * child 0..n .)
	 * @param root - the root of the subtree to be saved
	 */
	private void saveOldSubtreeColors(Node root) {
		for (int i = 0; i < targetAdapters.length; i++) {
			oldColors[i].add(targetAdapters[i].getColor(root));
		}
		
		for (int i = 0; i < root.getChildren().size(); i++) {
			saveOldSubtreeColors(root.getChildren().get(i));
		}
	}


	/**
	 * Sets the line colors according to the specified border colors and node data.
	 * @param root 
	 */
	private void setNewColors(Node root) {
		double value = sourceAdapter.getDecimal(root);
		if (inheritToTerminals && Double.isNaN(value) && root.isLeaf() && root.hasParent()) {
			for (int i = 0; i < targetAdapters.length; i++) {
				targetAdapters[i].setColor(targetAdapters[i].getColor(root.getParent()), root);
			}
		}
		else {
			Color color = null;
			if (!Double.isNaN(value)) {
				color = new Color(min.getRed() + (int)Math.round(redFactor * value), 
						min.getGreen() + (int)Math.round(greenFactor * value),
						min.getBlue() + (int)Math.round(blueFactor * value));
						
			}
			else if (changeUndefined) {
				color = min;
			}
			if (color != null) {
				for (int i = 0; i < targetAdapters.length; i++) {
					targetAdapters[i].setColor(color, root);
				}
			}
		}
		
		for (int i = 0; i < root.getChildren().size(); i++) {
			setNewColors(root.getChildren().get(i));
		}
	}


	@Override
	public void redo() throws CannotRedoException {
		setNewColors(getDocument().getTree().getPaintStart());
		super.redo();
	}


	/**
	 * Assigns the previously saved colors in the subtree under <code>root</code>.
	 * @param root
	 * @param index - the index of the value for root in the linear lists
	 * @return the next unused index of the linear lists
	 */
	private int setOldColors(Node root, int index) {
		for (int i = 0; i < targetAdapters.length; i++) {
			targetAdapters[i].setColor(oldColors[i].get(index), root);
		}
		index++;
		
		for (int i = 0; i < root.getChildren().size(); i++) {
			index = setOldColors(root.getChildren().get(i), index);
		}
		return index;
	}	


	@Override
	public void undo() throws CannotUndoException {
		setOldColors(getDocument().getTree().getPaintStart(), 0);
		super.undo();
	}
	
	
	public String getPresentationName() {
		return "Set colors by node/branch data";
	}
}