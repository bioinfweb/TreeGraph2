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
package info.bioinfweb.treegraph.gui.dialogs.editelement;


import info.bioinfweb.commons.Math2;
import info.bioinfweb.treegraph.document.TextElementData;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;



public class TextElementDataInput extends JPanel {
	private JTextField textField;
	private JCheckBox decimalCheckBox;
	private List<ChangeListener> changeListeners = new ArrayList<ChangeListener>();
	
	
	public TextElementDataInput() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.insets = new Insets(0, 0, 5, 0);
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.gridx = 0;
		gbc_textField.gridy = 0;
		add(getTextField(), gbc_textField);
		
		GridBagConstraints gbc_decimalCheckBox = new GridBagConstraints();
		gbc_decimalCheckBox.anchor = GridBagConstraints.WEST;
		gbc_decimalCheckBox.gridx = 0;
		gbc_decimalCheckBox.gridy = 1;
		add(getDecimalCheckBox(), gbc_decimalCheckBox);
	}

	
	private JCheckBox getDecimalCheckBox() {
		if (decimalCheckBox == null) {
			decimalCheckBox = new JCheckBox("Decimal value");
			decimalCheckBox.addChangeListener(new javax.swing.event.ChangeListener() {
				public void stateChanged(javax.swing.event.ChangeEvent e) {
					//TODO Is the following still necessary?
//					String text = getTextField().getText();
//					if (getDecimalCheckBox().isSelected()) {
//						getValueTextField().setFormatterFactory(decimalFormatterFactory);
//					}
//					else {
//						getValueTextField().setFormatterFactory(stringFormatterFactory);
//					}
//					getValueTextField().setText(text);  // Wert wurde durch Setzen neuer Factory gel�scht.
					
					//updatePreview();  //TODO Call change listener here instead
				}
			});
		}
		return decimalCheckBox;
	}
	
	
	private JTextField getTextField() {
		if (textField == null) {
			textField = new JTextField();
			textField.setColumns(10);
			textField.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyReleased(java.awt.event.KeyEvent e) {
					setDecimalCheckBoxStatus();
					//updatePreview();  //TODO Call change listener here instead
				}
			});
		}
		return textField;
	}
	
	
  private void setDecimalCheckBoxStatus() {
  	boolean isDecimal = Math2.isDecimal(getTextField().getText());
  	if (!isDecimal) {
  		getDecimalCheckBox().setSelected(false);
  	}
		getDecimalCheckBox().setEnabled(isDecimal);
  }
  
  
	public TextElementData getValue() {
		//TODO Implement
		return null;
	}
	
	
	public void setValue(TextElementData data) {
		getTextField().setText(data.toString());
		getDecimalCheckBox().setSelected(data.isDecimal());
	}
	
	
	protected void fireValueChanged() {
		ChangeEvent event = new ChangeEvent(getValue());
		for (ChangeListener listener : changeListeners) {
	    listener.stateChanged(event);
    }
	}
	
	
	public void addValueChangeListener(ChangeListener listener) {
		changeListeners.add(listener);
	}
	
	
	public boolean removeValueChangeListener(ChangeListener listener) {
		return changeListeners.remove(listener);
	}
}
