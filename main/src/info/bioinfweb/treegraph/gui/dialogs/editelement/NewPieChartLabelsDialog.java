/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2011, 2013-2016  Ben Stöver, Sarah Wiechers, Kai Müller
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


import info.bioinfweb.treegraph.document.GraphicalLabel;
import info.bioinfweb.treegraph.document.PieChartLabel;
import info.bioinfweb.treegraph.gui.dialogs.nodebranchdatainput.PieChartSectionDataList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;



/**
 * Dialog used to create new pie chart labels.
 * 
 * @author Ben St&ouml;ver
 * @since 2.0.43
 * @see PieChartLabel
 */
public class NewPieChartLabelsDialog extends NewGraphicalLabelsDialog {
	private static final long serialVersionUID = 1L;

	
	private JPanel jContentPane = null;
	private PieChartSectionDataList valuesPanel = null;

	
	/**
	 * @param owner
	 */
	public NewPieChartLabelsDialog(Frame owner) {
		super(owner);
		setHelpCode(55);
		initialize();
		setLocationRelativeTo(owner);
	}

	
	@Override
	protected boolean onExecute() {
		boolean result = super.onExecute();
		if (result) {
			getValuesPanel().setIDs(getDocument());
		}
		return result;
	}


	@Override
	protected GraphicalLabel createLabel() {
		PieChartLabel label = new PieChartLabel(null);
		label.getSectionDataList().addAll(getValuesPanel().getSectionDataList());
		return label;
	}


	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		setTitle("New pie chart label(s)");
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
			jContentPane.add(getIDPanel(), null);
			jContentPane.add(getValuesPanel(), null);
			jContentPane.add(getButtonsPanel(), null);
		}
		return jContentPane;
	}
	
	
	protected PieChartSectionDataList getValuesPanel() {
		if (valuesPanel == null) {
			valuesPanel = new PieChartSectionDataList();
			valuesPanel.setBorder(BorderFactory.createTitledBorder(null, "Pie chart value IDs", 
					TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, 
					new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
		}
		return valuesPanel;
	}
}