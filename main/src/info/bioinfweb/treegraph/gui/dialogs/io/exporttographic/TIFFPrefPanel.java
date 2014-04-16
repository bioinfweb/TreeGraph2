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
package info.bioinfweb.treegraph.gui.dialogs.io.exporttographic;


import java.awt.GridBagConstraints;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import info.bioinfweb.treegraph.graphics.export.tiff.TIFFWriter;
import info.bioinfweb.commons.collections.ParameterMap;



public class TIFFPrefPanel extends TransparentBgPrefPanel {
	private JLabel compressionMethodLabel = null;
	private JComboBox compressionMethodComboBox = null;
	
	
	@Override
	public void addHints(ParameterMap hints) {
		super.addHints(hints);
		hints.put(TIFFWriter.KEY_TIFF_COMPRESSION_METHOD, 
				getCompressionMethodComboBox().getSelectedItem().toString());
	}

	
	@Override
	protected void initialize() {
		super.initialize();
		
		GridBagConstraints labelGBC = new GridBagConstraints();
		labelGBC.gridx = 0;
		labelGBC.anchor = GridBagConstraints.WEST;
		labelGBC.gridy = 1;
		compressionMethodLabel = new JLabel();
		compressionMethodLabel.setText("Compression method: ");
		add(compressionMethodLabel, labelGBC);
		
		GridBagConstraints comboBoxGBC = new GridBagConstraints();
		comboBoxGBC.fill = GridBagConstraints.HORIZONTAL;
		comboBoxGBC.gridy = 1;
		comboBoxGBC.weightx = 1.0;
		comboBoxGBC.gridx = 1;
		add(getCompressionMethodComboBox(), comboBoxGBC);
	}
	
	
	/**
	 * This method initializes compressionMethodComboBox	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getCompressionMethodComboBox() {
		if (compressionMethodComboBox == null) {
			compressionMethodComboBox = new JComboBox();
			compressionMethodComboBox.addItem("none");
			compressionMethodComboBox.addItem("packbits");
			compressionMethodComboBox.addItem("packbits");
		}
		return compressionMethodComboBox;
	}
}