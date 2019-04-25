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
package info.bioinfweb.treegraph.gui.dialogs.elementformats;


import info.bioinfweb.treegraph.document.format.ScaleBarFormats;
import info.bioinfweb.treegraph.document.format.ScaleValue;
import info.bioinfweb.commons.swing.DecimalInput;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;

import javax.swing.JLabel;



/**
 * Used in {@link ScaleBarPanel} to display the length of the small and long interval in millimeters
 * and points while the user enters a value in branch length units. (See the 
 * <a href="http://treegraph.bioinfweb.info/Help/wiki/Scale_bar#Intervals">help system</a> for details.)
 *  
 * @author Ben St&ouml;ver
 * @since 2.0.41
 * @see ScaleBarPanel
 * @see ScaleBarFormats
 */
public class ScaleBarIntervalKeyListener extends KeyAdapter {
	public static final DecimalFormat LENGTH_FORMAT = new DecimalFormat("#.###");
	
	
	private ElementFormatsDialog owner = null;
	private JLabel label = null;
	private DecimalInput smallIntervalInput = null;
	private DecimalInput longIntervalInput = null;
	
	
	/**
	 * Creates a <code>ScaleBarIntervalKeyListener</code>.
	 * @param owner - the owner of the {@link ScaleBarPanel} used to determine the branch length scale from 
	 *        the currently associated document 
	 * @param label - the output label
	 * @param smallIntervalInput - the small interval input of the panel
	 * @param longIntervalInput - the small interval input of the panel or <code>null</code> of the length of
	 *        the small interval shall be specified 
	 */
	public ScaleBarIntervalKeyListener(ElementFormatsDialog owner, JLabel label,
			DecimalInput smallIntervalInput, DecimalInput longIntervalInput) {
		
		super();
		this.owner = owner;
		this.label = label;
		this.smallIntervalInput = smallIntervalInput;
		this.longIntervalInput = longIntervalInput;
	}


	private String getIntervalLabelText(float brLenUnits) {
		if (owner.getDocument() != null) {
			float brLenScale = 
				  owner.getDocument().getTree().getFormats().getBranchLengthScale().getInMillimeters();
			return " = " + 
	  	  	LENGTH_FORMAT.format(ScaleValue.unitsToMillimeters(brLenUnits, brLenScale)) + " mm or " + 
	  	  	LENGTH_FORMAT.format(ScaleValue.unitsToPoints(brLenUnits, brLenScale)) + " pt";
		}
		else {
			return "";
		}
	}
	
	
	@Override
	public void keyReleased(KeyEvent e) {
		if (Character.isDigit(e.getKeyChar()) || 
				e.getKeyCode() == KeyEvent.VK_DELETE || 
				e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
			
			refreshText();
		}
	}
	
	
	/**
	 * Refreshes the label text depending of the value of the small and long interval decimal input.
	 */
	public void refreshText() {
		try {
			float value = smallIntervalInput.parseFloat();
			if (longIntervalInput != null) {
				value *= longIntervalInput.parseFloat();
			}
			label.setText(
					getIntervalLabelText(value));
		}
		catch (NumberFormatException ex) {
			label.setText("");
		}				
	}
}