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


import info.bioinfweb.treegraph.document.undo.ImportTextElementDataParameters;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;



/**
 * GUI components that allows the user to specify {@link ImportTextElementDataParameters}.
 * 
 * @author Ben St&ouml;ver
 * @since 2.2.0
 */
public class ImportTextElementDataParametersPanel extends JPanel {
	private JCheckBox ignoreWhitespaceCheckBox;
	private JCheckBox caseCheckBox;
	private JCheckBox distinguishSpaceUnderscoreCheckBox;
	private JCheckBox parseNumericValuesCheckBox;

	
	public ImportTextElementDataParametersPanel() {
		super();
		initialize();
	}
	
	
  @Override
  public void setEnabled(boolean flag) {
	  super.setEnabled(flag);
	  getIgnoreWhitespaceCheckBox().setEnabled(flag);
	  getCaseCheckBox().setEnabled(flag);
	  getDistinguishSpaceUnderscoreCheckBox().setEnabled(flag);
	  getParseNumericValuesCheckBox().setEnabled(flag);
  }


	public void assignParameters(ImportTextElementDataParameters parameters) {
		parameters.setIgnoreWhitespace(getIgnoreWhitespaceCheckBox().isSelected());
		parameters.setDistinguishSpaceUnderscore(getDistinguishSpaceUnderscoreCheckBox().isSelected());
		parameters.setCaseSensitive(getCaseCheckBox().isSelected());
		parameters.setParseNumericValues(getParseNumericValuesCheckBox().isSelected());
  }
	
	
	private void initialize() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		GridBagConstraints gbc_chckbxNewCheckBox = new GridBagConstraints();
		gbc_chckbxNewCheckBox.anchor = GridBagConstraints.WEST;
		gbc_chckbxNewCheckBox.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxNewCheckBox.gridx = 0;
		gbc_chckbxNewCheckBox.gridy = 0;
		add(getIgnoreWhitespaceCheckBox(), gbc_chckbxNewCheckBox);
		
		GridBagConstraints gbc_chckbxNewCheckBox_1 = new GridBagConstraints();
		gbc_chckbxNewCheckBox_1.anchor = GridBagConstraints.WEST;
		gbc_chckbxNewCheckBox_1.insets = new Insets(0, 0, 5, 0);
		gbc_chckbxNewCheckBox_1.gridx = 1;
		gbc_chckbxNewCheckBox_1.gridy = 0;
		add(getDistinguishSpaceUnderscoreCheckBox(), gbc_chckbxNewCheckBox_1);
		
		GridBagConstraints gbc_checkBox = new GridBagConstraints();
		gbc_checkBox.anchor = GridBagConstraints.WEST;
		gbc_checkBox.insets = new Insets(0, 0, 0, 5);
		gbc_checkBox.gridx = 0;
		gbc_checkBox.gridy = 1;
		add(getCaseCheckBox(), gbc_checkBox);
		
		GridBagConstraints gbc_chckbxNewCheckBox_2 = new GridBagConstraints();
		gbc_chckbxNewCheckBox_2.anchor = GridBagConstraints.WEST;
		gbc_chckbxNewCheckBox_2.gridx = 1;
		gbc_chckbxNewCheckBox_2.gridy = 1;
		add(getParseNumericValuesCheckBox(), gbc_chckbxNewCheckBox_2);
	}

	
	private JCheckBox getIgnoreWhitespaceCheckBox() {
		if (ignoreWhitespaceCheckBox == null) {
			ignoreWhitespaceCheckBox = new JCheckBox("Ignore leading and trailing white space");
			ignoreWhitespaceCheckBox.setSelected(false);
		}
		return ignoreWhitespaceCheckBox;
	}
	
	
	private JCheckBox getCaseCheckBox() {
		if (caseCheckBox == null) {
			caseCheckBox = new JCheckBox("Case sensitive");
		}
		return caseCheckBox;
	}
	
	
	private JCheckBox getDistinguishSpaceUnderscoreCheckBox() {
		if (distinguishSpaceUnderscoreCheckBox == null) {
			distinguishSpaceUnderscoreCheckBox = new JCheckBox("Distinguish between space (\" \") and underscore (\"_\")");
		}
		return distinguishSpaceUnderscoreCheckBox;
	}
	
	
	private JCheckBox getParseNumericValuesCheckBox() {
		if (parseNumericValuesCheckBox == null) {
			parseNumericValuesCheckBox = new JCheckBox("Parse numeric values if possible");
			parseNumericValuesCheckBox.setSelected(true);
		}
		return parseNumericValuesCheckBox;
	}
}
