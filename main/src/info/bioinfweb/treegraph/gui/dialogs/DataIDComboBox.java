/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2011  Ben Stöver, Kai Müller
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
package info.bioinfweb.treegraph.gui.dialogs;


import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.IDManager;
import info.bioinfweb.treegraph.document.Node;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;



/**
 * Component used to select data IDs (for labels, hidden node/branch data).
 * @author Ben St&ouml;ver
 * @since 2.0.43
 */
public class DataIDComboBox extends JComboBox {
	public DataIDComboBox(boolean allowNewIDS) {
		super(new DefaultComboBoxModel());
		setEditable(allowNewIDS);
	}
	

	public void setIDs(Document document) {
		Node root = document.getTree().getPaintStart();
		if (root != null) {
			DefaultComboBoxModel model = getModel();
			model.removeAllElements();
			String[] ids = IDManager.getIDs(document.getTree().getPaintStart());
			for (int i = 0; i < ids.length; i++) {
				model.addElement(ids[i]);
			}
		}
	}


	@Override
	public DefaultComboBoxModel getModel() {
		return (DefaultComboBoxModel)super.getModel();
	}
}