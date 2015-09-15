/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2015  Ben Stöver, Kai Müller
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


import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.PieChartLabel;
import info.bioinfweb.treegraph.document.change.DocumentChangeType;
import info.bioinfweb.treegraph.document.undo.DocumentEdit;



/**
 * Changes the node/branch data column IDs referenced by a pie chart label.
 * 
 * @author Ben St&ouml;ver
 * @since 2.0.43
 */
public class PieChartLabelIDsEdit extends DocumentEdit {
	private PieChartLabel[] labels;
	private String[] newIDs;
	private String[][] oldIDs;
	
	
	public PieChartLabelIDsEdit(Document document, PieChartLabel[] labels, String[] newIDs) {
		super(document, DocumentChangeType.NEUTRAL);
		this.labels = labels;
		this.newIDs = newIDs;
		
		oldIDs = new String[labels.length][];
		for (int i = 0; i < oldIDs.length; i++) {
			oldIDs[i] = new String[labels[i].valueCount()];
			for (int j = 0; j < labels[i].valueCount(); j++) {
			  oldIDs[i][j] = labels[i].getValueID(j);
			}
		}
	}

	
	private void setIDs(String[] ids, int labelIndex) {
		labels[labelIndex].clearValueIDs();
		for (int i = 0; i < ids.length; i++) {
			labels[labelIndex].addValueID(ids[i]);
		}
	}
	

	@Override
	public void redo() throws CannotRedoException {
		for (int i = 0; i < labels.length; i++) {
			setIDs(newIDs, i);
		}
		super.redo();
	}

	
	@Override
	public void undo() throws CannotUndoException {
		for (int i = 0; i < labels.length; i++) {
			setIDs(oldIDs[i], i);
		}
		super.undo();
	}

	
	@Override
	public String getPresentationName() {
		return "Change pie chart label value ID(s)";
	}
}