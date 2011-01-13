/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2011  Ben St�ver, Kai M�ller
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
package info.bioinfweb.treegraph.gui.dialogs.editelement;


import info.bioinfweb.treegraph.document.ConcretePaintableElement;
import info.bioinfweb.treegraph.document.PieChartLabel;
import info.bioinfweb.treegraph.document.undo.edit.PieChartLabelIDsEdit;

import java.awt.Frame;
import java.util.Iterator;
import java.util.LinkedList;



/**
 * @author Ben St&ouml;ver
 * @since 2.0.43
 */
public class EditPieChartLabelsDialog extends NewPieChartLabelsDialog {
	private static final long serialVersionUID = 1L;

	
	/**
	 * @param owner
	 */
	public EditPieChartLabelsDialog(Frame owner) {
		super(owner);
		setHelpCode(56);
		setTitle("Edit pie chart source data IDs");
		getIDPanel().setVisible(false);
	}


	@Override
	protected boolean onExecute() {
		PieChartLabel l = getSelection().getFirstElementOfType(PieChartLabel.class);
		boolean result = (l != null);
		if (result) {
			getListIDComboBox().setIDs(getDocument());
			getIDListModel().clear();
			for (int i = 0; i < l.valueCount(); i++) {
				getIDListModel().addElement(l.getValueID(i));
			}
		}
		return result;
	}


	@Override
	protected boolean apply() {
		String[] ids = new String[getIDListModel().size()];
		for (int i = 0; i < ids.length; i++) {
			ids[i] = getIDListModel().get(i).toString();
		}

		getDocument().executeEdit(new PieChartLabelIDsEdit(getDocument(), 
				getSelection().getAllElementsOfType(PieChartLabel.class), ids));
		return true;
	}
}