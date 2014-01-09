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
package info.bioinfweb.treegraph.gui.dialogs;


import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JComboBox;
import javax.swing.JPanel;



public class ResolutionInput extends DecimalUnitInput {
	public static final String UNIT_PPI = "pixel per inch (ppi)";
	public static final String UNIT_PPMM = "pixel per mm";
	
	public static final float MM_PER_INCH = 25.4f;

  
  public ResolutionInput(String labelText, JPanel panel, int y) {
    super(labelText, panel, y);
  }


	public float getPixelsPerMillimeter() {
		float result = parseFloat();
		if (getComboBox().getSelectedItem().equals(UNIT_PPI)) {
			result = ppiToPPM(result);
		}
		return result;
	}


	public void setResolution(float value) {
		if (getComboBox().getSelectedItem().equals(UNIT_PPI)) {
			value = ppiToPPM(value);
		}
		setValue(value);
	}


	@Override
	protected void customizeComboBox(JComboBox comboBox) {
  	comboBox.addItem(UNIT_PPMM);
  	comboBox.addItem(UNIT_PPI);
  	comboBox.addItemListener(new ItemListener() {
		   public void itemStateChanged(ItemEvent e) {
		  	   if (e.getStateChange() == ItemEvent.SELECTED) {
		  	  	 if (((String)e.getItem()).equals(UNIT_PPMM)) {
		  	  		 setValue(ppiToPPM(parseFloat()));
	  		  	 }
		  	  	 else {
		  	  		 setValue(ppmmToPPI(parseFloat()));
		  	  	 }
		  	   }
		     }  		   
	     });
	}
	
	
	public static float ppiToPPM(float ppi) {
	  return ppi / MM_PER_INCH; 
	}
	
	
	public static float ppmmToPPI(float ppmm) {
	  return ppmm * MM_PER_INCH; 
	}
	
	
	public float millimetersToPixels(float mm) {
		return mm * getPixelsPerMillimeter();
	}
	
	
	public float pixelsToMillimeters(float px) {
		return px / getPixelsPerMillimeter();
	}
}