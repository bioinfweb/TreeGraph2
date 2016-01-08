/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2015-2016  Ben Stöver, Sarah Wiechers
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
package info.bioinfweb.treegraph.gui.dialogs;


import info.bioinfweb.treegraph.document.format.Margin;

import java.awt.GridBagConstraints;
import javax.swing.JLabel;
import javax.swing.JPanel;



public class MarginInput {
	private JLabel titleLabel = null;
  private DistanceValueInput left = null;
  private DistanceValueInput right = null;
  private DistanceValueInput top = null;
  private DistanceValueInput bottom = null;
  
  
  private void initLabel(String title, JPanel panel, int y) {
  	titleLabel = new JLabel(title + " margin: ");
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = y;
		gbc.ipadx = 3;
		gbc.anchor = GridBagConstraints.WEST;
    panel.add(titleLabel, gbc);
  }
  
  
  public MarginInput(String title, JPanel panel, int y0) {
  	if ((title != null) && !"".equals(title)) {
  		initLabel(title, panel, y0);
  		y0++;
  	}
    left = new DistanceValueInput("Left: ", panel, y0);
    right = new DistanceValueInput("Right: ", panel, y0 + 2);
    top = new DistanceValueInput("Top: ", panel, y0 + 4);
    bottom = new DistanceValueInput("Bottom: ", panel, y0 + 6);
  }


  public DistanceValueInput getBottom() {
		return bottom;
	}


	public DistanceValueInput getLeft() {
		return left;
	}


	public DistanceValueInput getRight() {
		return right;
	}


	public JLabel getTitleLabel() {
		return titleLabel;
	}


	public DistanceValueInput getTop() {
		return top;
	}
	
	
	/**
	 * Assigns the given value to the text-fields.
	 * @param m
	 */
	public void setValue(Margin m) {
		getLeft().setValue(m.getLeft());
		getRight().setValue(m.getRight());
		getTop().setValue(m.getTop());
		getBottom().setValue(m.getBottom());
	}
	
	
	/**
	 * Assigns the current values of the text-fields to the given <code>Margin</code>.
	 * @param m
	 */
	public void assignValueTo(Margin m) {
		getLeft().assignValueTo(m.getLeft());
		getRight().assignValueTo(m.getRight());
		getTop().assignValueTo(m.getTop());
		getBottom().assignValueTo(m.getBottom());
	}
	
	
	public void resetChangeMonitors() {
		getLeft().getChangeMonitor().reset();
		getTop().getChangeMonitor().reset();
		getRight().getChangeMonitor().reset();
		getBottom().getChangeMonitor().reset();
	}
}