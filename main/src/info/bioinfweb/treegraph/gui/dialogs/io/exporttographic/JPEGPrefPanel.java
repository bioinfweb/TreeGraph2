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


import info.bioinfweb.treegraph.graphics.export.jpeg.JPEGWriter;
import info.bioinfweb.commons.collections.ParameterMap;

import java.awt.GridBagLayout;
import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import javax.swing.JSlider;
import java.awt.Insets;



public class JPEGPrefPanel extends JPanel implements PreferencesPanel {
	private static final long serialVersionUID = 1L;
	
	
	private JLabel qualityLabel = null;
	private JSlider qualitySlider = null;
	private JLabel highestComprLabel = null;
	private JLabel bestQualityLabel = null;


	/**
	 * This is the default constructor
	 */
	public JPEGPrefPanel() {
		super();
		initialize();
	}


	public void addHints(ParameterMap hints) {
		hints.put(JPEGWriter.KEY_JPEG_QUALITY, 
				new Float((float)getQualitySlider().getValue() / 100f));
	}


	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
		gridBagConstraints4.gridx = 2;
		gridBagConstraints4.anchor = GridBagConstraints.EAST;
		gridBagConstraints4.gridy = 0;
		bestQualityLabel = new JLabel();
		bestQualityLabel.setText("best quality");
		GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
		gridBagConstraints3.gridx = 1;
		gridBagConstraints3.anchor = GridBagConstraints.WEST;
		gridBagConstraints3.insets = new Insets(0, 0, 0, 0);
		gridBagConstraints3.gridy = 0;
		highestComprLabel = new JLabel();
		highestComprLabel.setText("highest compression");
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints1.gridy = 1;
		gridBagConstraints1.weightx = 1.0;
		gridBagConstraints1.gridwidth = 2;
		gridBagConstraints1.gridx = 1;
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.gridy = 1;
		qualityLabel = new JLabel();
		qualityLabel.setText("Quality [%]: ");
		this.setSize(300, 200);
		this.setLayout(new GridBagLayout());
		this.add(qualityLabel, gridBagConstraints);
		this.add(getQualitySlider(), gridBagConstraints1);
		this.add(highestComprLabel, gridBagConstraints3);
		this.add(bestQualityLabel, gridBagConstraints4);
	}


	/**
	 * This method initializes qualitySlider	
	 * 	
	 * @return javax.swing.JSlider	
	 */
	private JSlider getQualitySlider() {
		if (qualitySlider == null) {
			qualitySlider = new JSlider();
			qualitySlider.setMinimum(0);
			qualitySlider.setMaximum(100);
			qualitySlider.setMajorTickSpacing(10);
			qualitySlider.setMinorTickSpacing(1);
			qualitySlider.setValue(80);
			qualitySlider.setPaintLabels(true);
			qualitySlider.setPaintTicks(true);
		}
		return qualitySlider;
	}
}