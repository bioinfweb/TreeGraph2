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


import info.bioinfweb.commons.swing.DecimalInput;
import java.awt.GridBagConstraints;
import javax.swing.JComboBox;
import javax.swing.JPanel;



/**
 * Specialization of {@link DecimalInput} to manage distance values in different units. 
 * 
 * @author Ben St&ouml;ver
 */
public abstract class DecimalUnitInput extends DecimalInput {
  private JComboBox comboBox = null;

  
	public DecimalUnitInput(String labelText, JPanel panel, int y) {
		this(labelText, panel, 0, y);
	}

	
	public DecimalUnitInput(String labelText, JPanel panel, int x, int y) {
		super(labelText, panel, x, y, DecimalInput.FLOAT_FORMAT);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = x + 2;
		gbc.gridy = y;
		gbc.weightx = 1.0;
    panel.add(getComboBox(), gbc);
	}

	
	public JComboBox getComboBox() {
		if (comboBox == null) {
	    comboBox = new JComboBox();
	  	comboBox.setEditable(false);
	  	customizeComboBox(comboBox);
		}
		return comboBox;
	}
	
	
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		getComboBox().setEnabled(enabled);
	}


	protected abstract void customizeComboBox(JComboBox comboBox);
}