/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2015  Ben Stöver, Sarah Wiechers, Kai Müller
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
import info.bioinfweb.treegraph.document.IconLabel;
import info.bioinfweb.treegraph.gui.dialogs.elementformats.IconPieChartLabelPanel;

import javax.swing.JPanel;
import java.awt.Frame;
import javax.swing.BoxLayout;



/**
 * Dialog used to add a new icon label to a document.
 * 
 * @author Ben St&ouml;ver
 * @since 2.0.26
 */
public class NewIconLabelsDialog extends NewGraphicalLabelsDialog {
	private static final long serialVersionUID = 1L;

	
	private JPanel jContentPane = null;
	private IconPieChartLabelPanel formatsPanel = null;

	
	/**
	 * @param owner
	 */
	public NewIconLabelsDialog(Frame owner) {
		super(owner);
		setHelpCode(54);
		initialize();
		setLocationRelativeTo(owner);
	}

	
	@Override
	protected GraphicalLabel createLabel() {
		IconLabel label = new IconLabel(null);
		getFormatsPanel().setLabelFormats(label.getFormats());
		return label;
	}


	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		setTitle("New icon label");
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
			jContentPane.add(getFormatsPanel(), null);
			jContentPane.add(getButtonsPanel(), null);
		}
		return jContentPane;
	}
	
	
	protected IconPieChartLabelPanel getFormatsPanel() {
		if (formatsPanel == null) {
			formatsPanel = new IconPieChartLabelPanel();
			formatsPanel.setPieChartElementsVisible(false);
		}
		return formatsPanel;
	}
}