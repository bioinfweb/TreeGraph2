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
package info.bioinfweb.treegraph.gui.dialogs.nodebranchdata.defaultadapter;


import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.JCheckBox;
import java.awt.Insets;



public class SelectionSynchronizationMessagePanel extends JPanel {
	public SelectionSynchronizationMessagePanel() {
		setLayout(new GridBagLayout());
		
		JTextArea messageArea = new JTextArea("In one or more of the currently opened documents, default support node/branch data columns are not " + 
				"specified., although columns with numeric values are available.\n\n"
				+ "You should specify a column cotaining support values for each document. Otherwise topological conflicts will not be hightlighted "
				+ "by the selection synchronization feature.");
		messageArea.setWrapStyleWord(true);
		messageArea.setLineWrap(true);
		messageArea.setEditable(false);
		messageArea.setBackground(UIManager.getColor("Label.background"));
		messageArea.setFont(new Font("Dialog", Font.PLAIN, 12));
		messageArea.setBorder(new EmptyBorder(2, 2, 2, 2));
		GridBagConstraints messageAreaGBC = new GridBagConstraints();
		messageAreaGBC.insets = new Insets(0, 0, 5, 0);
		messageAreaGBC.weightx = 1.0;
		messageAreaGBC.fill = GridBagConstraints.BOTH;
		messageAreaGBC.gridx = 0;
		messageAreaGBC.gridy = 0;
		add(messageArea, messageAreaGBC);
		
		JCheckBox showAgainCheckBox = new JCheckBox("Don't show this message again");
		GridBagConstraints showAgainCheckBoxGBC = new GridBagConstraints();
		showAgainCheckBoxGBC.anchor = GridBagConstraints.WEST;
		showAgainCheckBoxGBC.gridx = 0;
		showAgainCheckBoxGBC.gridy = 1;
		add(showAgainCheckBox, showAgainCheckBoxGBC);
	}
	
}
