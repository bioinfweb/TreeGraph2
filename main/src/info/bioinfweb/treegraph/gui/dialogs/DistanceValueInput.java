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
package info.bioinfweb.treegraph.gui.dialogs;


import info.bioinfweb.treegraph.document.format.DistanceValue;

import java.awt.event.*;
import javax.swing.*;



public class DistanceValueInput extends DecimalUnitInput {
	public static final String UNIT_MILLIMETERS = "mm";
	public static final String UNIT_POINTS = "pt";
	
	
	public DistanceValueInput(String labelText, JPanel panel, int y) {
		this(labelText, panel, 0, y);
	}

	
  public DistanceValueInput(String labelText, JPanel panel, int x, int y) {
  	super(labelText, panel, x, y);
  }


	@Override
	protected void customizeComboBox(JComboBox<String> comboBox) {
  	comboBox.addItem(UNIT_MILLIMETERS);
  	comboBox.addItem(UNIT_POINTS);
  	comboBox.addItemListener(new ItemListener() {
  		   public void itemStateChanged(ItemEvent e) {
  		  	   if (e.getStateChange() == ItemEvent.SELECTED) {
  		  	  	 if (((String)e.getItem()).equals(UNIT_MILLIMETERS)) {
  		  	  		 setValue(DistanceValue.pointsToMillimeters(parseFloat()));
		  		  	 }
  		  	  	 else {
  		  	  		 setValue(DistanceValue.millimetersToPoints(parseFloat()));
  		  	  	 }
  		  	   }
  		     }  		   
  	     });
	}


	/**
	 * Assigns the given value to the text-field.
	 * @param distanceValue
	 */
	public void setValue(DistanceValue distanceValue) {
		if (((String)getComboBox().getSelectedItem()).equals(UNIT_MILLIMETERS)) {
			setValue(distanceValue.getInMillimeters());
		}
		else {
			setValue(distanceValue.getInPoints());
		}
	}
	
	
	/**
	 * Assigns the current value of the text-field to the given <code>DistanceValue</code>.
	 * @param distanceValue
	 */
	public void assignValueTo(DistanceValue distanceValue) {
		float value = parseFloat();
		if (((String)getComboBox().getSelectedItem()).equals(UNIT_MILLIMETERS)) {
			distanceValue.setInMillimeters(value);
		}
		else {
			distanceValue.setInPoints(value);
		}
	}
	
	
	public DistanceValue getValue() {
		DistanceValue result = new DistanceValue();
		assignValueTo(result);
		return result;
	}
}