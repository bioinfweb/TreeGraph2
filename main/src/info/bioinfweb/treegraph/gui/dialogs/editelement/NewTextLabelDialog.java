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


import info.bioinfweb.treegraph.document.Branch;
import info.bioinfweb.treegraph.document.IDManager;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.TextLabel;
import info.bioinfweb.treegraph.document.format.FormatUtils;
import info.bioinfweb.treegraph.document.undo.edit.InsertLabelEdit;
import info.bioinfweb.treegraph.gui.dialogs.DataIDComboBox;
import info.webinsel.util.Math2;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;



/**
 * Dialog used to add a new text label to a document.
 * 
 * @author BenStoever
 */
public class NewTextLabelDialog extends AbstractTextElementDialog {
	private JPanel jContentPane = null;
	private JPanel idPanel = null;
	private DataIDComboBox idComboBox = null;
	
	
	public NewTextLabelDialog(Frame owner) {
		super(owner);
		setHelpCode(15);
		initialize();
		setLocationRelativeTo(owner);
	}

	
	@Override
	protected boolean onExecute() {
		Branch b = getSelection().getFirstElementOfType(Branch.class);
		boolean result = (b != null);
		
		if (result) {
			setTextElement(new TextLabel(b.getLabels()));
			getIDComboBox().setIDs(getDocument());
		}
		return result;
	}


	@Override
	protected boolean apply() {
		TextLabel label = (TextLabel)getTextElement();
		if (getDecimalCheckBox().isSelected()) {
			label.getData().setDecimal(Math2.parseDouble(getValueTextField().getText()));
		}
		else {
			label.getData().setText(getValueTextField().getText());
		}
		if (getIDComboBox().getSelectedItem() != null) {
			label.setID((String)getIDComboBox().getSelectedItem());
		}
		label.getFormats().setTextColor(FormatUtils.getTextColor(getDocument()));
		getDocument().executeEdit(new InsertLabelEdit(getDocument(), label, 
				getSelection().getFirstElementOfType(Branch.class).getLabels()));
		return true;
	}

	
	/**
	 * This method initializes this dialog
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setTitle("New text label");
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
			jContentPane.add(getValuesPanel(), null);
			jContentPane.add(getPreviewPanel(), null);
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
			idPanel.setBorder(BorderFactory.createTitledBorder(null, "ID", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
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