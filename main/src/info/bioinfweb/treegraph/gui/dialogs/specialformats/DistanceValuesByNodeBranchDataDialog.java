/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2013  Ben Stöver, Kai Müller
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
package info.bioinfweb.treegraph.gui.dialogs.specialformats;


import info.bioinfweb.treegraph.document.format.adapters.distance.DistanceAdapter;
import info.bioinfweb.treegraph.document.undo.format.DistanceValuesByNodeBranchDataEdit;
import info.bioinfweb.treegraph.gui.dialogs.DistanceValueInput;

import javax.swing.JPanel;
import java.awt.Frame;
import javax.swing.BoxLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.JCheckBox;
import javax.swing.BorderFactory;
import javax.swing.ListModel;
import javax.swing.border.TitledBorder;
import java.awt.Font;
import java.awt.Color;
import java.util.Vector;



/**
 * @author Ben St&ouml;ver
 * @since 2.0.23
 */
public class DistanceValuesByNodeBranchDataDialog extends FormatsByNodeBranchDataDialog {
	private static final long serialVersionUID = 1L;
	
	public static final double DEFAULT_MIN_WIDTH_IN_MM = 0.3;
	public static final double DEFAULT_MAX_WIDTH_IN_MM = 10;
	
	
	private JPanel jContentPane = null;
	private JPanel distancesPanel = null;
	private DistanceValueInput minWidthInput; 
	private DistanceValueInput maxWidthInput;
	private JCheckBox changeUndefinedCheckBox = null;
	private JCheckBox inheritToTerminalsCheckBox = null;
	

	/**
	 * @param owner
	 */
	public DistanceValuesByNodeBranchDataDialog(Frame owner) {
		super(owner, 40);
		setHelpCode(39);
		initialize();
		setLocationRelativeTo(owner);
	}


	@Override
	protected boolean customizeTarget() {
		getTargetListModel().setAdapters(getDocument().getTree(), false);
		if (getTargetListModel().getSize() >= 1) {
			getTargetList().setSelectionInterval(0, 0);
		}
		return true;
	}


	@Override
	protected boolean apply() {
		Vector<DistanceAdapter> adapters = new Vector<DistanceAdapter>();
		int[] selection = getTargetList().getSelectedIndices();
		for (int i = 0; i < selection.length; i++) {
			adapters.add(getTargetListModel().getElementAt(selection[i]));
		}
		
    getDocument().executeEdit(new DistanceValuesByNodeBranchDataEdit(getDocument(), 
				getSourceComboBoxModel().getSelectedItem(), 
				getMinWidthInput().parseFloat(), getMaxWidthInput().parseFloat(),
				getChangeUndefinedCheckBox().isSelected(),
				getInheritToTerminalsCheckBox().isSelected(),
				adapters.toArray(new DistanceAdapter[adapters.size()])));
    
		return true;
	}


	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		setTitle("Set distance values by node/branch data");
		setContentPane(getJContentPane());
		pack();
	}


	@Override
	protected ListModel createTargetListModel() {
		return new DistanceAdapterListModel();
	}


	@Override
	protected DistanceAdapterListModel getTargetListModel() {
		return (DistanceAdapterListModel)super.getTargetListModel();
	}


	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BoxLayout(getJContentPane(), BoxLayout.Y_AXIS));
			jContentPane.add(getSourcePanel(), null);
			jContentPane.add(getDistancesPanel(), null);
			jContentPane.add(getTargetPanel(), null);
			jContentPane.add(getButtonsPanel(), null);
		}
		return jContentPane;
	}


	/**
	 * This method initializes distancesPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getDistancesPanel() {
		if (distancesPanel == null) {
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridwidth = 3;
			gridBagConstraints.fill = GridBagConstraints.NONE;
			gridBagConstraints.anchor = GridBagConstraints.WEST;
			gridBagConstraints.weightx = 1.0;
			gridBagConstraints.gridy = 6;
			GridBagConstraints undefinedGBC = new GridBagConstraints();
			undefinedGBC.gridx = 0;
			undefinedGBC.anchor = GridBagConstraints.WEST;
			undefinedGBC.gridwidth = 3;
			undefinedGBC.gridy = 5;
			distancesPanel = new JPanel();
			distancesPanel.setLayout(new GridBagLayout());
			distancesPanel.setBorder(BorderFactory.createTitledBorder(null, 
					"Distance values", TitledBorder.DEFAULT_JUSTIFICATION, 
					TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), 
					new Color(51, 51, 51)));
			distancesPanel.add(getChangeUndefinedCheckBox(), undefinedGBC);
			distancesPanel.add(getInheritToTerminalsCheckBox(), gridBagConstraints);
			minWidthInput = new DistanceValueInput("Minimal value", distancesPanel, 1);
			minWidthInput.setValue(DEFAULT_MIN_WIDTH_IN_MM);
			maxWidthInput = new DistanceValueInput("Maximal value", distancesPanel, 3);
			maxWidthInput.setValue(DEFAULT_MAX_WIDTH_IN_MM);
		}
		return distancesPanel;
	}


	private DistanceValueInput getMaxWidthInput() {
		getDistancesPanel();
		return maxWidthInput;
	}


	private DistanceValueInput getMinWidthInput() {
		getDistancesPanel();
		return minWidthInput;
	}


	/**
	 * This method initializes changeUndefinedCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getChangeUndefinedCheckBox() {
		if (changeUndefinedCheckBox == null) {
			changeUndefinedCheckBox = new JCheckBox();
			changeUndefinedCheckBox.setText("Set undefined nodes to minimal width");
			changeUndefinedCheckBox.setSelected(true);
		}
		return changeUndefinedCheckBox;
	}


	/**
	 * This method initializes inheritToTerminalsCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getInheritToTerminalsCheckBox() {
		if (inheritToTerminalsCheckBox == null) {
			inheritToTerminalsCheckBox = new JCheckBox();
			inheritToTerminalsCheckBox.setText("Set parent value to terminals");
		}
		return inheritToTerminalsCheckBox;
	}

}