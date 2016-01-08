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
package info.bioinfweb.treegraph.gui.dialogs.elementformats;


import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import info.bioinfweb.treegraph.document.format.DistanceValue;
import info.bioinfweb.treegraph.document.format.ScaleValue;
import info.bioinfweb.treegraph.gui.dialogs.DecimalUnitInput;
import info.bioinfweb.treegraph.gui.dialogs.DistanceValueInput;



/**
 * Used to edit distance values in millimeters, points or scale bar units.
 * 
 * @author Ben St&ouml;ver
 * @see ElementFormatsDialog
 * @see ScaleBarPanel
 */
public class ScaleValueInput extends DecimalUnitInput {
	public static final String UNIT_SCALE = "branch length units";
	
	
	private String previousUnit = DistanceValueInput.UNIT_MILLIMETERS;
	private float branchLengthScale = 1;
	
	
	public ScaleValueInput(String labelText, JPanel panel, int y) {
		super(labelText, panel, y);
	}

	
	public float getBranchLengthScale() {
		return branchLengthScale;
	}


	/**
	 * The branch length scale is used to convert millimeters and points to scale values.
	 * It should be refreshed every time the dialog, that uses this input is displayed
	 * (and the apply document or the value itself might have changed).  
	 * @param branchLengthScale - the new value of the branch length scale
	 */
	public void setBranchLengthScale(float branchLengthScale) {
		this.branchLengthScale = branchLengthScale;
	}


	@Override
	protected void customizeComboBox(JComboBox comboBox) {
  	comboBox.addItem(DistanceValueInput.UNIT_MILLIMETERS);
  	comboBox.addItem(DistanceValueInput.UNIT_POINTS);
  	comboBox.addItem(UNIT_SCALE);
  	comboBox.addItemListener(new ItemListener() {
  		   public void itemStateChanged(ItemEvent e) {
  		  	   if (e.getStateChange() == ItemEvent.SELECTED) {
  		  	  	 String currentUnit = (String)e.getItem(); 
  		  	  	 if (currentUnit.equals(DistanceValueInput.UNIT_MILLIMETERS)) {
  		  	  		 if (previousUnit.equals(DistanceValueInput.UNIT_POINTS)) {
  		  	  			 getTextField().setText("" + DistanceValue.pointsToMillimeters(
    		  	  				 parseFloat()));
  		  	  		 }
  		  	  		 else {  // UNIT_SCALE
  		  	  			 getTextField().setText("" + ScaleValue.unitsToMillimeters(
    		  	  				 parseFloat(), getBranchLengthScale()));
  		  	  		 }
		  		  	 }
  		  	  	 else if (currentUnit.equals(DistanceValueInput.UNIT_POINTS)) {
  		  	  		 if (previousUnit.equals(DistanceValueInput.UNIT_MILLIMETERS)) {
    		  	  		 getTextField().setText("" + DistanceValue.millimetersToPoints(
    		  	  				 parseFloat()));
  		  	  		 }
  		  	  		 else {  // UNIT_SCALE
  		  	  			 getTextField().setText("" + ScaleValue.unitsToPoints(
    		  	  				 parseFloat(), getBranchLengthScale()));
  		  	  		 }
  		  	  	 }
  		  	  	 else {  // UNIT_SCALE
  		  	  		 if (previousUnit.equals(DistanceValueInput.UNIT_MILLIMETERS)) {
    		  	  		 getTextField().setText("" + ScaleValue.millimetersToUnits(
    		  	  				 parseFloat(), getBranchLengthScale()));
  		  	  		 }
  		  	  		 else {  // UNIT_POINTS
    		  	  		 getTextField().setText("" + ScaleValue.pointsToUnits(
    		  	  				 parseFloat(), getBranchLengthScale()));
  		  	  		 }
  		  	  	 }
  		  	  	 previousUnit = currentUnit; 
  		  	   }
  		     }  		   
  	     });
	}
	
	
	public void setValue(ScaleValue value) {
		String unit = (String)getComboBox().getSelectedItem();
		if (unit.equals(DistanceValueInput.UNIT_MILLIMETERS)) {
			setValue(value.getInMillimeters(branchLengthScale));
		}
		else if (unit.equals(DistanceValueInput.UNIT_POINTS)){
			setValue(value.getInPoints(branchLengthScale));
		}
		else {  // UNIT_SCALE
			setValue(value.getInUnits(branchLengthScale));
		}
	}
	
	
	public void assignValueTo(ScaleValue target) {
		float value = parseFloat();
		String unit = (String)getComboBox().getSelectedItem();
		if (unit.equals(DistanceValueInput.UNIT_MILLIMETERS)) {
			target.setInMillimeters(value);
		}
		else if (unit.equals(DistanceValueInput.UNIT_POINTS)){
			target.setInPoints(value);
		}
		else {  // UNIT_SCALE
			target.setInUnits(value);
		}
	}
	
	
	public ScaleValue getValue() {
		ScaleValue result = new ScaleValue();
		assignValueTo(result);
		return result;
	}
}