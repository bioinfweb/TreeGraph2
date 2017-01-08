/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2011, 2013-2017  Ben Stöver, Sarah Wiechers, Kai Müller
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
package info.bioinfweb.treegraph.gui.dialogs.io;


import info.bioinfweb.treegraph.gui.mainframe.MainFrame;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;



/**
 * Used to enter a column separator to import tables from text files. 
 * @author Ben St&ouml;ver
 * @since 2.0.24
 */
public class TableSeparatorPanel extends JPanel {
	/** The default separeter which is not a tab. */
	public static final char DEFAULT_SEPARATOR = ';';
	
	
	private JRadioButton tabRadioButton = null;
	private JRadioButton stringRadioButton = null;
	private JTextField separatorTextField = null;
	private ButtonGroup separatorGroup = new ButtonGroup();  //  @jve:decl-index=0:

	
	public TableSeparatorPanel() {
		super();
		initialize();
	}
	
	
	/**
	 * Checks if the input is correct and displays an error dialog if not.
	 * @return <code>true</code> if the input was correct
	 */
	public boolean checkInput() {
		if (!(getTabRadioButton().isSelected() || getSeparatorTextField().getText().length() == 1)) {
			JOptionPane.showMessageDialog(MainFrame.getInstance(), "The separator must be " +
					"exactly one character long.", "Invalid separator", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		else {
			return true;
		}
	}
	
	
	public char getSeparator() {
		if (getTabRadioButton().isSelected()) {
			return '\t';
		}
		else {
			return getSeparatorTextField().getText().charAt(0);
		}
	}
	
	
	private void initialize() {
		GridBagConstraints textFieldGBC = new GridBagConstraints();
		textFieldGBC.fill = GridBagConstraints.HORIZONTAL;
		textFieldGBC.gridy = 1;
		textFieldGBC.weightx = 2.0;
		textFieldGBC.gridx = 1;
		GridBagConstraints stringRadioGBC = new GridBagConstraints();
		stringRadioGBC.gridx = 0;
		stringRadioGBC.anchor = GridBagConstraints.WEST;
		stringRadioGBC.gridy = 1;
		GridBagConstraints tabRadioGBC = new GridBagConstraints();
		tabRadioGBC.gridx = 0;
		tabRadioGBC.anchor = GridBagConstraints.WEST;
		tabRadioGBC.gridwidth = 2;
		tabRadioGBC.gridy = 0;
		setLayout(new GridBagLayout());
		setBorder(BorderFactory.createTitledBorder(null, "Values separated by:", 
				TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, 
				new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
		add(getTabRadioButton(), tabRadioGBC);
		add(getStringRadioButton(), stringRadioGBC);
		add(getSeparatorTextField(), textFieldGBC);
	}

	
	/**
	 * This method initializes tabRadioButton	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getTabRadioButton() {
		if (tabRadioButton == null) {
			tabRadioButton = new JRadioButton();
			tabRadioButton.setText("Tab");
			tabRadioButton.setSelected(true);
			separatorGroup.add(tabRadioButton);
		}
		return tabRadioButton;
	}


	/**
	 * This method initializes stringRadioButton	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getStringRadioButton() {
		if (stringRadioButton == null) {
			stringRadioButton = new JRadioButton();
			stringRadioButton.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(java.awt.event.ItemEvent e) {
					getSeparatorTextField().setEnabled(e.getItem().equals(getStringRadioButton()));
				}
			});
			separatorGroup.add(stringRadioButton);
		}
		return stringRadioButton;
	}


	/**
	 * This method initializes separatorTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getSeparatorTextField() {
		if (separatorTextField == null) {
			separatorTextField = new JTextField();
			separatorTextField.setText(Character.toString(DEFAULT_SEPARATOR));
			separatorTextField.setEnabled(false);
		}
		return separatorTextField;
	}	
}