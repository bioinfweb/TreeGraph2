/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2011, 2013-2019  Ben Stöver, Sarah Wiechers, Kai Müller
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


import info.bioinfweb.treegraph.document.Branch;
import info.bioinfweb.treegraph.document.GraphicalLabel;
import info.bioinfweb.treegraph.document.format.FormatUtils;
import info.bioinfweb.treegraph.document.format.GraphicalLabelFormats;
import info.bioinfweb.treegraph.document.undo.edit.InsertLabelsEdit;
import info.bioinfweb.treegraph.gui.dialogs.CollidingIDsDialog;
import info.bioinfweb.treegraph.gui.dialogs.DataIDComboBox;
import info.bioinfweb.treegraph.gui.dialogs.EditDialog;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;



/**
 * Implements basic functionalities for dialogs used to created graphical labels.   
 * @author Ben St&ouml;ver
 * @since 2.0.43
 */
public abstract class NewGraphicalLabelsDialog extends EditDialog {
	private static final long serialVersionUID = 1L;

	
	private JPanel idPanel = null;
	private DataIDComboBox idComboBox = null;

	
	/**
	 * @param owner
	 */
	public NewGraphicalLabelsDialog(Frame owner) {
		super(owner);
	}

	
	@Override
	protected boolean onExecute() {
		boolean result = getSelection().containsType(Branch.class);
		if (result) {
			getIDComboBox().setIDs(getDocument());
		}
		return true;
	}

	
	/**
	 * Descendant classes should return a label object here which if formatted according to the user input.
	 * The label ID and line color are set by this class later on.
	 */
	protected abstract GraphicalLabel createLabel();
	

	@Override
	protected boolean apply() {
		GraphicalLabel label = createLabel();
		Branch[] selection = getSelection().getAllElementsOfType(Branch.class);
		if (getIDComboBox().getSelectedItem() == null) {
			JOptionPane.showMessageDialog(this, "You have to specify an ID.", "Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		else {
			label.setID(CollidingIDsDialog.getInstance().checkConflicts(selection, 
					(String)getIDComboBox().getSelectedItem()));			
		}
		GraphicalLabelFormats f = label.getFormats();
		f.setLineColor(FormatUtils.getLineColor(getDocument()));
		
		getDocument().executeEdit(new InsertLabelsEdit(getDocument(), label, 
				selection));
		return true;
	}

	
	/**
	 * This method initializes idPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	protected JPanel getIDPanel() {
		if (idPanel == null) {
			idPanel = new JPanel();
			idPanel.setLayout(new GridBagLayout());
			idPanel.setBorder(BorderFactory.createTitledBorder(null, "Label ID", 
					TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null)); 
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.fill = GridBagConstraints.BOTH;
			gridBagConstraints.weightx = 1.0;
			idPanel.add(getIDComboBox(), gridBagConstraints);
		}
		return idPanel;
	}


	/**
	 * This method initializes idComboBox	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	protected DataIDComboBox getIDComboBox() {
		if (idComboBox == null) {
			idComboBox = new DataIDComboBox(true);
		}
		return idComboBox;
	}


	@Override
	protected JPanel getButtonsPanel() {
		getApplyButton().setVisible(false);
		return super.getButtonsPanel();
	}
}