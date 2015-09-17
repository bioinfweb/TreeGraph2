/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2015  Ben Stöver, Sarah Wiechers, Kai Müller
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


import java.awt.Color;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import info.bioinfweb.treegraph.Main;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.gui.dialogs.nodebranchdatainput.NodeDataComboBoxModel;
import info.bioinfweb.treegraph.gui.mainframe.MainFrame;
import info.bioinfweb.wikihelp.client.WikiHelpOptionPane;



/**
 * Superclass of all dialogs which set formats by node/branch data.
 * @author Ben St&ouml;ver
 * @since 2.0.23
 */
public abstract class FormatsByNodeBranchDataDialog extends SpecialFormatsDialog {
	private int errorHelpCode;
	
	private JComboBox<NodeBranchDataAdapter> sourceComboBox = null;
	private JPanel sourcePanel = null;
	
	
	/**
	 * @param owner
	 * @param errorHelpCode - the help code to use in the error message which is displayed if no 
	 *        node/branch data columns with numeric values are available.
	 */
	public FormatsByNodeBranchDataDialog(Dialog owner, int errorHelpCode) {
		super(owner);
		this.errorHelpCode = errorHelpCode;
	}

	
	public FormatsByNodeBranchDataDialog(Frame owner, int errorHelpCode) {
		super(owner);
		this.errorHelpCode = errorHelpCode;
	}


	/**
	 * Subclasses should implement {@link SpecialFormatsDialog#customizeTarget()} instead of
	 * overriding this method. ({@link SpecialFormatsDialog#customizeTarget()} is called from
	 * here if {@link SpecialFormatsDialog#customizeTarget()} returned <code>true</code>.)
	 * @see info.bioinfweb.treegraph.gui.dialogs.EditDialog#onExecute()
	 */
	@Override
	protected boolean onExecute() {
		((NodeDataComboBoxModel)getSourceComboBox().getModel()).setAdapters(
				getDocument().getTree(), false, true, true, true, false, "");
		
		boolean result = getSourceComboBox().getModel().getSize() > 0; 
		if (result) {
			result = super.onExecute();
		}
		else {
			WikiHelpOptionPane.showMessageDialog(MainFrame.getInstance(), "This document " +
					"does not contain any node/branch data column with decimal values.", 
					"No data available", JOptionPane.ERROR_MESSAGE, 
					Main.getInstance().getWikiHelp(), errorHelpCode);
		}
		return result;
	}
	
	
	/**
	 * This method initializes sourcePanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	protected JPanel getSourcePanel() {
		if (sourcePanel == null) {
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints1.gridx = -1;
			gridBagConstraints1.gridy = -1;
			gridBagConstraints1.weightx = 1.0;
			gridBagConstraints1.insets = new Insets(5, 0, 5, 0);
			sourcePanel = new JPanel();
			sourcePanel.setLayout(new GridBagLayout());
			sourcePanel.setBorder(BorderFactory.createTitledBorder(null, "Source data", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			sourcePanel.add(getSourceComboBox(), gridBagConstraints1);
		}
		return sourcePanel;
	}


	/**
	 * This method initializes sourceComboBox	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	protected JComboBox<NodeBranchDataAdapter> getSourceComboBox() {
		if (sourceComboBox == null) {
			sourceComboBox = new JComboBox<NodeBranchDataAdapter>(new NodeDataComboBoxModel());
		}
		return sourceComboBox;
	}


	protected NodeDataComboBoxModel getSourceComboBoxModel() {
		return (NodeDataComboBoxModel)getSourceComboBox().getModel();
	}
}