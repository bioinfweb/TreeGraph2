/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2015-2016  Ben Stöver, Sarah Wiechers, Kai Müller
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


import info.bioinfweb.commons.Math2;
import info.bioinfweb.commons.text.StringUtils;
import info.bioinfweb.treegraph.Main;
import info.bioinfweb.treegraph.document.Branch;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.nodebranchdata.HiddenDataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.TextLabelAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.VoidNodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.undo.edit.CollapseNodesBySupportEdit;
import info.bioinfweb.treegraph.gui.actions.edit.CollapseNodesBySupportAction;
import info.bioinfweb.treegraph.gui.dialogs.nodebranchdatainput.NodeBranchDataInput;
import info.bioinfweb.treegraph.gui.mainframe.MainFrame;
import info.bioinfweb.wikihelp.client.WikiHelpOptionPane;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JFormattedTextField;



/**
 * Dialog used together with {@link CollapseNodesBySupportAction}.
 * 
 * @author Ben St&ouml;ver
 * @since 2.2.0
 *
 */
public class CollapseNodesBySupportDialog extends EditDialog {
	private JPanel jContentPane = null;
	private JPanel collapseNodesPanel = null;
	private NodeBranchDataInput adapterInput = null;
	private JFormattedTextField thresholdTextField;

	
	public CollapseNodesBySupportDialog(Frame owner) {
	  super(owner);
	  initialize();
  }

	
	/**
	 * Initializes this dialog.
	 * 
	 * @return void
	 */
	private void initialize() {
		setHelpCode(71);  //TODO Link this ID to concrete Wiki page (ID is already registered)
		setMinimumSize(new Dimension(400, 150));
		setTitle("Collapse nodes by support value");
		setContentPane(getJContentPane());
		pack();
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
			
			jContentPane.add(getCollapseNodesPanel());
			jContentPane.add(getButtonsPanel());
		}
		return jContentPane;
	}


	private JPanel getCollapseNodesPanel() {
		if (collapseNodesPanel == null) {
			collapseNodesPanel = new JPanel();
			GridBagLayout gbl_collapseNodesPanel = new GridBagLayout();
			gbl_collapseNodesPanel.columnWidths = new int[]{0, 0, 0};
			gbl_collapseNodesPanel.rowHeights = new int[]{0, 0, 0};
			gbl_collapseNodesPanel.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
			gbl_collapseNodesPanel.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
			collapseNodesPanel.setLayout(gbl_collapseNodesPanel);
			
			JLabel thresholdLabel = new JLabel("Threshold:");
			GridBagConstraints gbc_thresholdLabel = new GridBagConstraints();
			gbc_thresholdLabel.insets = new Insets(3, 3, 5, 5);
			gbc_thresholdLabel.anchor = GridBagConstraints.WEST;
			gbc_thresholdLabel.gridx = 0;
			gbc_thresholdLabel.gridy = 0;
			collapseNodesPanel.add(thresholdLabel, gbc_thresholdLabel);

			JLabel supportValuesLabel = new JLabel("Support values column:");
			GridBagConstraints gbc_lblSupportValuesColumn = new GridBagConstraints();
			gbc_lblSupportValuesColumn.anchor = GridBagConstraints.WEST;
			gbc_lblSupportValuesColumn.insets = new Insets(3, 3, 0, 5);
			gbc_lblSupportValuesColumn.gridx = 0;
			gbc_lblSupportValuesColumn.gridy = 1;
			collapseNodesPanel.add(supportValuesLabel, gbc_lblSupportValuesColumn);
			
			thresholdTextField = new JFormattedTextField(StringUtils.DOUBLE_FORMAT);
			GridBagConstraints gbc_thresholdTextField = new GridBagConstraints();
			gbc_thresholdTextField.insets = new Insets(2, 0, 2, 0);
			gbc_thresholdTextField.fill = GridBagConstraints.HORIZONTAL;
			gbc_thresholdTextField.gridx = 1;
			gbc_thresholdTextField.gridy = 0;
			collapseNodesPanel.add(thresholdTextField, gbc_thresholdTextField);
			
			adapterInput = new NodeBranchDataInput(collapseNodesPanel, 1, 1);			
		}
		return collapseNodesPanel;
	}
	
	
	private JFormattedTextField getThresholdTextField() {
		getCollapseNodesPanel();
		return thresholdTextField;
	}
	
	
	private double getThreshold() {
		double result;
		try {
			result = Math2.parseDouble(getThresholdTextField().getText());
		}
		catch (NumberFormatException e) {
			result = Double.NaN;
		}
		return result;
	}
	
	
	@Override
  protected boolean onExecute() {
		adapterInput.setAdapters(getDocument().getTree(), false, true, true, true, false, "");
		if (adapterInput.getModel().getSize() > 0) {
			if (!(getDocument().getDefaultSupportAdapter() instanceof VoidNodeBranchDataAdapter)) {
				adapterInput.setSelectedAdapter(getDocument().getDefaultSupportAdapter());
			}
			else if (!adapterInput.setSelectedAdapter(TextLabelAdapter.class)) {
				adapterInput.setSelectedAdapter(HiddenDataAdapter.class);
			}
		  return true;
		}
		else {
			JOptionPane.showMessageDialog(getOwner(), 
					"There are no node/branch data columns containing numerical values (e.g. text labels, hidden branch data, ...)\n" +
					"in this tree. Therefore this feature is currently not available.", "Feature currently not available", 
					JOptionPane.ERROR_MESSAGE);
			return false;
		}
  }

	
	@Override
  protected boolean apply() {
		double threshold = getThreshold();
		if (!Double.isNaN(threshold)) {
			CollapseNodesBySupportEdit edit = new CollapseNodesBySupportEdit(getDocument(),	
					getSelection().getFirstNodeBranchOrRoot(), adapterInput.getSelectedAdapter(), threshold); 
			getDocument().executeEdit(edit);
			
			if (edit.hasWarnings()) {
				WikiHelpOptionPane.showMessageDialog(MainFrame.getInstance(), edit.getWarningText(), "Collapse nodes by support",	
						JOptionPane.WARNING_MESSAGE, Main.getInstance().getWikiHelp(), 72);
			}
			return true;
		}
		else {
			JOptionPane.showMessageDialog(this, "The threshold value \"" + getThresholdTextField().getText() +
							"\" is invalid. Please specify a valid numerical value.", "Invalid threshold", JOptionPane.ERROR_MESSAGE);
			return false;
		}
  }
}
