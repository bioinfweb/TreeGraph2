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
package info.bioinfweb.treegraph.gui.dialogs.editelement;


import info.bioinfweb.treegraph.document.Branch;
import info.bioinfweb.treegraph.document.undo.edit.BranchLengthEdit;
import info.bioinfweb.treegraph.gui.dialogs.EditDialog;
import info.webinsel.util.swing.DecimalInput;

import javax.swing.JPanel;
import java.awt.Frame;
import javax.swing.BoxLayout;
import java.awt.GridBagLayout;
import javax.swing.JCheckBox;
import java.awt.GridBagConstraints;
import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;
import java.awt.Font;
import java.awt.Color;



/**
 * Dialog used to edit a branch length in a TreeGraph 2 document.
 * @author Ben St&ouml;ver
 */
public class EditBranchLengthDialog extends EditDialog {
	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JPanel inputPanel = null;	
	private DecimalInput decimalInput = null;
	private JCheckBox hasLengthCheckBox = null;


	/**
	 * @param owner
	 */
	public EditBranchLengthDialog(Frame owner) {
		super(owner);
		setHelpCode(14);
		initialize();
		setLocationRelativeTo(owner);
	}


	@Override
	protected boolean onExecute() {
		Branch b = getSelection().getFirstElementOfType(Branch.class);
		boolean result = (b != null);
		
		if (result) {
			getHasLengthCheckBox().setSelected(b.hasLength());
			if (b.hasLength()) {
				getDecimalInput().setValue(b.getLength());
			}
			else {
				getDecimalInput().setValue(0);
			}
		}
		
		return result;
	}


	@Override
	protected boolean apply() {
		double newValue = Double.NaN;
		if (getHasLengthCheckBox().isSelected()) {
			newValue = getDecimalInput().parseDouble();
		}
		getDocument().executeEdit(new BranchLengthEdit(getDocument(), 
				getSelection().getFirstElementOfType(Branch.class), newValue));
		return true;
	}


	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setTitle("Edit branch length");
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
			jContentPane.add(getInputPanel(), null);
			jContentPane.add(getButtonsPanel(), null);
		}
		return jContentPane;
	}


	/**
	 * This method initializes InputPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getInputPanel() {
		if (inputPanel == null) {
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.anchor = GridBagConstraints.WEST;
			gridBagConstraints.gridwidth = 2;
			gridBagConstraints.gridy = 0;
			inputPanel = new JPanel();
			inputPanel.setLayout(new GridBagLayout());
			inputPanel.setBorder(BorderFactory.createTitledBorder(null, "", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			inputPanel.add(getHasLengthCheckBox(), gridBagConstraints);
			decimalInput = new DecimalInput(
					"Branch length: ", inputPanel, 1, DecimalInput.DOUBLE_FORMAT);
		}
		return inputPanel;
	}
	
	
	public DecimalInput getDecimalInput() {
		getInputPanel();
		return decimalInput;
	}


	/**
	 * This method initializes hasLengthCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getHasLengthCheckBox() {
		if (hasLengthCheckBox == null) {
			hasLengthCheckBox = new JCheckBox();
			hasLengthCheckBox.setText("Save branch length");
			hasLengthCheckBox.setSelected(true);
			hasLengthCheckBox.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(java.awt.event.ItemEvent e) {
					getDecimalInput().setEnabled(getHasLengthCheckBox().isSelected());
				}
			});
		}
		return hasLengthCheckBox;
	}

}