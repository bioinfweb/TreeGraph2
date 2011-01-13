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
package info.bioinfweb.treegraph.gui.dialogs.editelement;


import info.bioinfweb.treegraph.document.ConcretePaintableElement;
import info.bioinfweb.treegraph.document.Label;
import info.bioinfweb.treegraph.document.undo.edit.ChangeLabelIDEdit;
import info.bioinfweb.treegraph.gui.dialogs.DataIDComboBox;
import info.bioinfweb.treegraph.gui.dialogs.EditDialog;

import javax.swing.JPanel;
import java.awt.Frame;
import javax.swing.BoxLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.util.Iterator;
import java.util.LinkedList;



public class ChangeIDDialog extends EditDialog {
	private static final long serialVersionUID = 1L;
	
	
	private JPanel jContentPane = null;
	private JPanel idPanel = null;
	private DataIDComboBox idComboBox = null;


	/**
	 * @param owner
	 */
	public ChangeIDDialog(Frame owner) {
		super(owner);
		setHelpCode(21);
		initialize();
		setLocationRelativeTo(owner);
	}


	@Override
	protected boolean onExecute() {
		if (getSelection().containsType(Label.class)) {
			getIDComboBox().setIDs(getDocument());
			getIDComboBox().setSelectedItem(getSelection().getFirstElementOfType(Label.class).getID());
			return true;
		}
		else {
			return false;
		}
	}


	@Override
	protected boolean apply() {
		getDocument().executeEdit(new ChangeLabelIDEdit(getDocument(), 
						(String)getIDComboBox().getSelectedItem(),
						getSelection().getAllElementsOfType(Label.class)));
		return true;
	}


	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		//this.setSize(300, 200);
		this.setTitle("Change ID");
		this.setContentPane(getJContentPane());
		this.pack();
	}


	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BoxLayout(getJContentPane(), BoxLayout.Y_AXIS));
			jContentPane.add(getIDPanel(), null);
			jContentPane.add(getButtonsPanel(), null);
		}
		return jContentPane;
	}


	/**
	 * This method initializes idPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getIDPanel() {
		if (idPanel == null) {
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints.gridy = 0;
			gridBagConstraints.weightx = 1.0;
			gridBagConstraints.ipadx = 0;
			gridBagConstraints.ipady = 0;
			gridBagConstraints.gridx = 0;
			idPanel = new JPanel();
			idPanel.setLayout(new GridBagLayout());
			idPanel.add(getIDComboBox(), gridBagConstraints);
		}
		return idPanel;
	}


	/**
	 * This method initializes idComboBox	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private DataIDComboBox getIDComboBox() {
		if (idComboBox == null) {
			idComboBox = new DataIDComboBox(true);
		}
		return idComboBox;
	}

}