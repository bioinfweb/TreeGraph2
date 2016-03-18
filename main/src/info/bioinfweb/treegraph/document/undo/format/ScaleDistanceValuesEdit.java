/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2015-2016  Ben Stöver, Sarah Wiechers, Kai Müller
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


import java.util.List;
import java.util.Vector;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.Legend;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.change.DocumentChangeType;
import info.bioinfweb.treegraph.document.format.DistanceValue;
import info.bioinfweb.treegraph.document.format.adapters.distance.AbstractLegendAdapter;
import info.bioinfweb.treegraph.document.format.adapters.distance.DistanceAdapter;
import info.bioinfweb.treegraph.document.format.adapters.distance.LegendLineWidthAdapter;
import info.bioinfweb.treegraph.document.format.adapters.distance.LegendTextHeightAdapter;
import info.bioinfweb.treegraph.document.tools.TreeSerializer;
import info.bioinfweb.treegraph.document.undo.DocumentEdit;



/**
 * @author Ben St&ouml;ver
 * @since 2.0.24
 */
public class ScaleDistanceValuesEdit extends DocumentEdit {
	private Node root;
  private float factor;
  private DistanceAdapter[] targetAdapters;
  private DistanceValuesBackup oldValues;
  
  
	public ScaleDistanceValuesEdit(Document document, Node root, float factor, 
			DistanceAdapter[] targetAdapters) {
		
		super(document, DocumentChangeType.POSITION);
		this.root = root;
		this.factor = factor;
		this.targetAdapters = targetAdapters;
		
		oldValues = new DistanceValuesBackup(document.getTree(), root, targetAdapters);  // Baum darf nicht leer sein.
	}


	/**
	 * Sets the branch widths according to the specified rescaling factor.
	 * @param root
	 */
	private void setNewSubtreeValues(Node root) {
		for (int i = 0; i < targetAdapters.length; i++) {
			targetAdapters[i].setDistance(targetAdapters[i].getDistance(root) * factor, root);
		}
		
		for (int i = 0; i < root.getChildren().size(); i++) {
			setNewSubtreeValues(root.getChildren().get(i));
		}
	}
	
	
	private void setNewDistanceValue(DistanceValue value) {
		value.setInMillimeters(value.getInMillimeters() * factor);
	}
	
	
	private void setNewLegendValues() {
		Legend[] legends = TreeSerializer.getLegendsInSubtree(getDocument().getTree(), root);
		for (int i = 0; i < targetAdapters.length; i++) {
			if (targetAdapters[i] instanceof AbstractLegendAdapter) {
				for (int j = 0; j < legends.length; j++) {
					setNewDistanceValue(
							((AbstractLegendAdapter)targetAdapters[i]).getDistanceValue(legends[j].getFormats()));
				}
			}
		}
	}


	@Override
	public void redo() throws CannotRedoException {
		setNewSubtreeValues(root);
		setNewLegendValues();
		super.redo();
	}


	@Override
	public void undo() throws CannotUndoException {
		oldValues.restoreValues();
		super.undo();
	}


	public String getPresentationName() {
		return "Scale distance values";
	}
}