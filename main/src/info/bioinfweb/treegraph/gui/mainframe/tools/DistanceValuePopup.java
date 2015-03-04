/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2015  Ben Stöver, Kai Müller
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
package info.bioinfweb.treegraph.gui.mainframe.tools;


import info.bioinfweb.treegraph.gui.dialogs.DistanceValueInput;
import info.bioinfweb.commons.swing.RelativeRangeModel;
import info.bioinfweb.commons.swing.ToolBarPopup;

import javax.swing.JPanel;
import java.awt.Frame;
import java.awt.GridBagLayout;
import javax.swing.JSlider;
import java.awt.GridBagConstraints;



/**
 * Component to display a popup menu for a tool bar button. (This class is not in use with the current
 * implementation of TreeGraph 2.)
 * 
 * @author Ben St&ouml;ver
 */
public class DistanceValuePopup extends ToolBarPopup {
	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private DistanceValueInput input;
	private JSlider slider = null;


	/**
	 * @param owner
	 */
	public DistanceValuePopup(Frame owner) {
		super(owner);
		initialize();
	}


	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(300, 200);
		this.setContentPane(getJContentPane());
	}


	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints.gridy = 1;
			gridBagConstraints.weightx = 1.0;
			gridBagConstraints.gridwidth = 2;
			gridBagConstraints.gridx = 0;
			jContentPane = new JPanel();
			jContentPane.setLayout(new GridBagLayout());
			jContentPane.add(getSlider(), gridBagConstraints);
		}
		return jContentPane;
	}


	/**
	 * This method initializes slider	
	 * 	
	 * @return javax.swing.JSlider	
	 */
	private JSlider getSlider() {
		if (slider == null) {
			slider = new JSlider(new RelativeRangeModel());
			slider.setPaintLabels(true);
			slider.setPaintTicks(true);
			slider.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
				public void propertyChange(java.beans.PropertyChangeEvent e) {
					if ((e.getPropertyName().equals("value"))) {
						System.out.println("propertyChange(value)"); // TODO Auto-generated property Event stub "value" 
					}
				}
			});
		}
		return slider;
	}

}