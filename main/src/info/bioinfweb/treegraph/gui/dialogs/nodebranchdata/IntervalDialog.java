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
package info.bioinfweb.treegraph.gui.dialogs.nodebranchdata;


import info.bioinfweb.treegraph.document.undo.edit.DeleteOutsideIntervalEdit;
import info.bioinfweb.treegraph.gui.dialogs.EditDialog;
import info.bioinfweb.treegraph.gui.dialogs.nodebranchdatainput.NodeBranchDataInput;
import info.bioinfweb.commons.swing.DecimalInput;

import javax.swing.JPanel;
import javax.swing.BoxLayout;
import java.awt.GridBagLayout;
import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Color;
import java.awt.Frame;



public class IntervalDialog extends EditDialog {
	private static final long serialVersionUID = 1L;
	public static final double DEFAULT_UPPER_BORDER = 100;
	public static final double DEFAULT_LOWER_BORDER = 50;

	private JPanel jContentPane = null;
	private JPanel intervalPanel = null;
	private DecimalInput upperInput = null;
	private DecimalInput lowerInput = null;
	private JPanel sourcePanel = null;
	private NodeBranchDataInput sourceInput = null;
	
	
	/**
	 * @param owner
	 */
	public IntervalDialog(Frame owner) {
		super(owner);
		initialize();
	}

	
	private double getUpperBorder() {
		return upperInput.parseDouble();
	}
	
	
	private double getLowerBorder() {
		return lowerInput.parseDouble();
	}
	
	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		setHelpCode(31);
		setTitle("Delte values outside interval");
		setMinimumSize(new Dimension(400, 50));
		setContentPane(getJContentPane());
		pack();
	}

	
	@Override
	protected boolean onExecute() {
		getSourceInput().setAdapters(getDocument().getTree());
		if (getSelectedAdapter() != null) {
			getSourceInput().setSelectedAdapter(getSelectedAdapter());
		}
		return true;
	}


	@Override
	protected boolean apply() {
		getDocument().executeEdit(new DeleteOutsideIntervalEdit(getDocument(), 
				getSourceInput().getSelectedAdapter(), getLowerBorder(), getUpperBorder()));
		return true;
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
			jContentPane.add(getIntervalPanel(), null);
			getApplyButton().setVisible(false);
			jContentPane.add(getButtonsPanel(), null);
		}
		return jContentPane;
	}


	/**
	 * This method initializes intervalPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getIntervalPanel() {
		if (intervalPanel == null) {
			intervalPanel = new JPanel();
			intervalPanel.setLayout(new GridBagLayout());
			intervalPanel.setBorder(BorderFactory.createTitledBorder(null, "Borders", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			upperInput = new DecimalInput("Upper border", intervalPanel, 0, 
					DecimalInput.DOUBLE_FORMAT);
			upperInput.setValue(DEFAULT_UPPER_BORDER);
			lowerInput = new DecimalInput("Lower border", intervalPanel, 2, 
					DecimalInput.DOUBLE_FORMAT);
			lowerInput.setValue(DEFAULT_LOWER_BORDER);
		}
		return intervalPanel;
	}


	/**
	 * This method initializes sourcePanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getSourcePanel() {
		if (sourcePanel == null) {
			sourcePanel = new JPanel();
			sourcePanel.setLayout(new GridBagLayout());
			sourcePanel.setBorder(BorderFactory.createTitledBorder(null, "Source column", 
					TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, 
					new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			sourceInput = new NodeBranchDataInput(sourcePanel, 0, 0);
		}
		return sourcePanel;
	}
	
	
	/**
	 * Creates the source input if necessary. 
	 * @return
	 */
	private NodeBranchDataInput getSourceInput() {
		getSourcePanel();
		return sourceInput;
	}
}