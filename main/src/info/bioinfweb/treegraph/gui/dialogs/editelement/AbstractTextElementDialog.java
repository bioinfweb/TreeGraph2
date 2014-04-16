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
package info.bioinfweb.treegraph.gui.dialogs.editelement;


import java.awt.Color;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JFormattedTextField.AbstractFormatterFactory;
import javax.swing.border.TitledBorder;

import info.bioinfweb.treegraph.document.TextElement;
import info.bioinfweb.treegraph.gui.dialogs.EditDialog;
import info.bioinfweb.commons.Math2;



/**
 * Implements basic functionalities for dialogs used to edit a textual document element.   
 * @author Ben St&ouml;ver
 * @see TextElement
 */
public abstract class AbstractTextElementDialog extends EditDialog {
  private TextElement textElement = null;  //  @jve:decl-index=0:
	private JPanel valuesPanel = null;
	private JLabel textLabel = null;
	private JFormattedTextField valueTextField = null;
	private AbstractFormatterFactory stringFormatterFactory = null;
	private AbstractFormatterFactory decimalFormatterFactory = null;
	private JCheckBox decimalCheckBox = null;
	private JPanel previewPanel = null;
	private JLabel previewLabel = null;	
	
	
	public AbstractTextElementDialog(Dialog owner) {
		super(owner);
	}

	
	public AbstractTextElementDialog(Frame owner) {
		super(owner);
	}

	
  @Override
	public boolean execute() {
  	getValueTextField().requestFocus();
		getValueTextField().selectAll();
		return super.execute();
	}


	protected TextElement getTextElement() {
		return textElement;
	}


	protected void setTextElement(TextElement textElement) {
		this.textElement = textElement;
		decimalFormatterFactory = new JFormattedTextField(
				textElement.getFormats().getDecimalFormat()).getFormatterFactory();
		setValue(textElement);
		setDecimalCheckBoxStatus();
		updatePreview();
	}


	protected AbstractFormatterFactory getDecimalFormatterFactory() {
		return decimalFormatterFactory;
	}


	protected AbstractFormatterFactory getStringFormatterFactory() {
		return stringFormatterFactory;
	}


	protected void updatePreview() {
		String text = getValueTextField().getText();
		if (getDecimalCheckBox().isSelected()) {
			try {
				text = textElement.getFormats().getDecimalFormat().format(
  					Math2.parseDouble(getValueTextField().getText()));
			}
			catch (NumberFormatException e) {  // Unclear how this exception can ever happen, but it was send in the error report 20140023_180142_7740879257708879519.xml.
				text = "ERROR: " + e.getMessage();
			}
		}
		if (text.equals("")) {
			text = " ";  // Vermeiden, dass Panel an Höhe verliert.
		}
		getPreviewLabel().setText(text);
  }
  
  
	public void setValue(TextElement textElement) {
		if (textElement.getData().isDecimal()) {
			getValueTextField().setText("" + textElement.getData().getDecimal());
		}
		else {
			getValueTextField().setText(textElement.getData().getText());
		}
		getValueTextField().selectAll();
		getDecimalCheckBox().setSelected(textElement.getData().isDecimal());
	}


  private void setDecimalCheckBoxStatus() {
  	boolean isDecimal = Math2.isDecimal(getValueTextField().getText());
  	if (!isDecimal) {
  		getDecimalCheckBox().setSelected(false);
  	}
		getDecimalCheckBox().setEnabled(isDecimal);
  }
	
	
	/**
	 * This method initializes valuesPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	protected JPanel getValuesPanel() {
		if (valuesPanel == null) {
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.gridx = 0;
			gridBagConstraints11.anchor = GridBagConstraints.WEST;
			gridBagConstraints11.gridwidth = 2;
			gridBagConstraints11.gridy = 1;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints1.gridy = 0;
			gridBagConstraints1.weightx = 2.0;
			gridBagConstraints1.gridx = 1;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			textLabel = new JLabel();
			textLabel.setText("Text:  ");
			valuesPanel = new JPanel();
			valuesPanel.setLayout(new GridBagLayout());
			valuesPanel.setBorder(BorderFactory.createTitledBorder(null, "Value", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			valuesPanel.add(textLabel, gridBagConstraints);
			valuesPanel.add(getValueTextField(), gridBagConstraints1);
			valuesPanel.add(getDecimalCheckBox(), gridBagConstraints11);
		}
		return valuesPanel;
	}


	/**
	 * This method initializes textTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	protected JFormattedTextField getValueTextField() {
		if (valueTextField == null) {
			valueTextField = new JFormattedTextField();
			valueTextField.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyReleased(java.awt.event.KeyEvent e) {
					setDecimalCheckBoxStatus();
					updatePreview();
				}
			});
			
			stringFormatterFactory = valueTextField.getFormatterFactory();
		}
		return valueTextField;
	}


	/**
	 * This method initializes decimalCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	protected JCheckBox getDecimalCheckBox() {
		if (decimalCheckBox == null) {
			decimalCheckBox = new JCheckBox();
			decimalCheckBox.setText("Save as decimal value");
			decimalCheckBox.addChangeListener(new javax.swing.event.ChangeListener() {
				public void stateChanged(javax.swing.event.ChangeEvent e) {
					String text = getValueTextField().getText();
					if (getDecimalCheckBox().isSelected()) {
						getValueTextField().setFormatterFactory(decimalFormatterFactory);
					}
					else {
						getValueTextField().setFormatterFactory(stringFormatterFactory);
					}
					getValueTextField().setText(text);  // Wert wurde durch Setzen neuer Factory gelöscht.
					
					updatePreview();
				}
			});
		}
		return decimalCheckBox;
	}


	/**
	 * This method initializes previewPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	protected JPanel getPreviewPanel() {
		if (previewPanel == null) {
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.gridy = 0;
			previewLabel = new JLabel();
			previewLabel.setText("Text");
			previewPanel = new JPanel();
			previewPanel.setLayout(new GridBagLayout());
			previewPanel.setBorder(BorderFactory.createTitledBorder(null, "Preview", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			previewPanel.add(previewLabel, gridBagConstraints2);
		}
		return previewPanel;
	}
	
	
	private JLabel getPreviewLabel() {
		getPreviewPanel();
		return previewLabel;
	}	
}