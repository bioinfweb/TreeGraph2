/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2011, 2013-2017  Ben Stöver, Sarah Wiechers, Kai Müller
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


import info.bioinfweb.treegraph.document.PieChartLabel;
import info.bioinfweb.treegraph.document.TextElement;
import info.bioinfweb.treegraph.document.TextElementData;
import info.bioinfweb.treegraph.document.undo.edit.PieChartLabelIDsEdit;
import info.bioinfweb.treegraph.document.undo.edit.TextElementEdit;
import info.bioinfweb.commons.Math2;

import javax.swing.JPanel;

import java.awt.Frame;

import javax.swing.BoxLayout;



/**
 * Dialog used to edit a text element.
 * 
 * @author BenStoever
 */
public class EditTextElementDialog extends AbstractTextElementDialog {
	private static final long serialVersionUID = 1L;
	
	
	private JPanel jContentPane = null;



	/**
	 * @param owner
	 */
	public EditTextElementDialog(Frame owner) {
		super(owner);
		initialize();
		setLocationRelativeTo(owner);
	}


	@Override
	protected boolean onExecute() {
		boolean result = getSelection().containsType(TextElement.class);
		
		if (result) {
			setTextElement(getSelection().getFirstElementOfType(TextElement.class));
		}
    return result;
	}


	@Override
	protected boolean apply() {
		TextElementData data = new TextElementData();
		if (getDecimalCheckBox().isSelected()) {
			data.setDecimal(Math2.parseDouble(getValueTextField().getText()));
		}
		else {
			data.setText(getValueTextField().getText());
		}
		
		getDocument().executeEdit(new TextElementEdit(getDocument(), getSelection().getAllElementsOfType(TextElement.class), data));
		return true;
	}
	
	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		setHelpCode(5);
		setTitle("Edit text");
		setContentPane(getJContentPane());
		pack();
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
			jContentPane.add(getButtonsPanel(), null);
		}
		return jContentPane;
	}
}