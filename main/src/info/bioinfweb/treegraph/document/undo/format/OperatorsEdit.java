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
package info.bioinfweb.treegraph.document.undo.format;


import info.bioinfweb.treegraph.document.*;
import info.bioinfweb.treegraph.document.format.ElementFormats;
import info.bioinfweb.treegraph.document.format.LabelFormats;
import info.bioinfweb.treegraph.document.format.operate.FormatOperator;
import info.bioinfweb.treegraph.document.undo.DocumentEdit;

import java.util.Vector;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;



/**
 * This edit changes applies the specified formats of the specified document elements. 
 * 
 * @author Ben St&ouml;ver
 */
public class OperatorsEdit extends DocumentEdit {
	private PaintableElement[] applyElements = null;
	private FormatOperator[] operators = null;
	
	/** 
	 * Used to store the previous format objects before they are edited to restore them in
	 * {@link OperatorsEdit#undo()}. 
	 */
	private Vector<ElementFormats> changedFormats = new Vector<ElementFormats>();

	
	public OperatorsEdit(Document document, PaintableElement[] applyElements, 
			FormatOperator[] operators) {
		
		super(document);
		this.applyElements = applyElements;
		this.operators = operators;
	}


	@Override
	public void redo() throws CannotRedoException {
		changedFormats.clear();
		for (int i = 0; i < applyElements.length; i++) {
			ElementFormats f = applyElements[i].getFormats().clone();
			if (f instanceof LabelFormats) {
				((LabelFormats)f).setOwner((Label)applyElements[i]);
			}
			changedFormats.add(f);
			for (int j = 0; j < operators.length; j++) {
				if (operators[j].validTarget(applyElements[i].getFormats())) {
					operators[j].applyTo(applyElements[i].getFormats());
				}
			}
		}
		
		super.redo();
	}


	@Override
	public void undo() throws CannotUndoException {
		for (int i = 0; i < applyElements.length; i++) {
			applyElements[i].setFormats(changedFormats.get(i));
		}
		
		super.undo();
	}


	public String getPresentationName() {
		return "Format element";
	}
}