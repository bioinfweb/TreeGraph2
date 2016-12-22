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
package info.bioinfweb.treegraph.gui.dialogs.nodebranchdata;


import info.bioinfweb.treegraph.document.nodebranchdata.TextIDElementType;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;



public class TextIDElementTypeInput extends JPanel {
	private JRadioButton labelRadioButton = null;
	private JRadioButton branchDataRadioButton = null;
	private JRadioButton nodeDataRadioButton = null;
	private ButtonGroup targetDataButtonGroup = null;

	
	public TextIDElementTypeInput() {
		super();
		
		GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
		gridBagConstraints5.gridx = 2;
		gridBagConstraints5.weightx = 1.0;
		gridBagConstraints5.anchor = GridBagConstraints.EAST;
		gridBagConstraints5.gridy = 0;
		GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
		gridBagConstraints4.gridx = 1;
		gridBagConstraints4.weightx = 1.0;
		gridBagConstraints4.gridy = 0;
		GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
		gridBagConstraints3.anchor = GridBagConstraints.WEST;
		gridBagConstraints3.weightx = 1.0;
		
		setLayout(new GridBagLayout());
		add(getLabelRadioButton(), gridBagConstraints3);
		add(getBranchDataRadioButton(), gridBagConstraints4);
		add(getNodeDataRadioButton(), gridBagConstraints5);
		getTargetDataButtonGroup();
	}
	
	
	/**
	 * This method initializes labelRadioButton	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getLabelRadioButton() {
		if (labelRadioButton == null) {
			labelRadioButton = new JRadioButton();
			labelRadioButton.setText("New text labels");
			labelRadioButton.setSelected(true);
		}
		return labelRadioButton;
	}


	/**
	 * This method initializes branchDataRadioButton	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getBranchDataRadioButton() {
		if (branchDataRadioButton == null) {
			branchDataRadioButton = new JRadioButton();
			branchDataRadioButton.setText("New hidden branch data");
		}
		return branchDataRadioButton;
	}


	/**
	 * This method initializes nodeDataRadioButton	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getNodeDataRadioButton() {
		if (nodeDataRadioButton == null) {
			nodeDataRadioButton = new JRadioButton();
			nodeDataRadioButton.setText("New hidden node data");
		}
		return nodeDataRadioButton;
	}


	private ButtonGroup getTargetDataButtonGroup() {
		if (targetDataButtonGroup == null) {
			targetDataButtonGroup = new ButtonGroup();
			targetDataButtonGroup.add(getLabelRadioButton());
			targetDataButtonGroup.add(getBranchDataRadioButton());
			targetDataButtonGroup.add(getNodeDataRadioButton());
		}
		return targetDataButtonGroup;
	}
	
	
	public TextIDElementType getSelectedType() {
		if (getLabelRadioButton().isSelected()) {
			return TextIDElementType.TEXT_LABEL;
		}
		else if (getBranchDataRadioButton().isSelected()) {
			return TextIDElementType.HIDDEN_BRANCH_DATA;
		}
		else {
			return TextIDElementType.HIDDEN_NODE_DATA;
		}
	}
	
	
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		
		getLabelRadioButton().setEnabled(enabled);
		getBranchDataRadioButton().setEnabled(enabled);
		getNodeDataRadioButton().setEnabled(enabled);
	}


	public void setSelectedType(TextIDElementType type) {
		switch (type) {
			case TEXT_LABEL:
				getLabelRadioButton().setSelected(true);
				break;
			case HIDDEN_BRANCH_DATA:
				getBranchDataRadioButton().setSelected(true);
				break;
			case HIDDEN_NODE_DATA:
				getNodeDataRadioButton().setSelected(true);
				break;
			default:
				throw new InternalError("Unsupported target type " + type + " encountered. "
						+ "Please inform the TreeGraph developers on this bug.");
		}
	}
}
