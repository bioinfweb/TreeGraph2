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
package info.bioinfweb.treegraph.gui.dialogs.editelement;


import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;

import info.bioinfweb.treegraph.document.undo.CompareTextElementDataParameters;
import info.bioinfweb.treegraph.gui.dialogs.EditDialog;
import info.bioinfweb.treegraph.gui.dialogs.CompareTextElementDataParametersPanel;
import info.bioinfweb.treegraph.gui.mainframe.MainFrame;
import java.awt.FlowLayout;



public class SynchronizeTreeSelectionDialog extends EditDialog {
	private JPanel jContentPane = null;
	private JPanel processRootedPanel = null;
	private JRadioButton rootedRadioButton = null;
	private JRadioButton unrootedRadioButton = null;
	private ButtonGroup processTreeAsGroup = null;
	private CompareTextElementDataParametersPanel compareTextElementDataParametersPanel = null;
	
	
	public SynchronizeTreeSelectionDialog(MainFrame mainFrame) {
		super(mainFrame);
//		setHelpCode(); //TODO set correct help code
		initialize();
		setLocationRelativeTo(mainFrame);
	}
	

	@Override
	protected boolean onExecute() {
		return true;
	}

	
	@Override
	protected boolean apply() {
		compareTextElementDataParametersPanel.assignToParameters(MainFrame.getInstance().getTreeSelectionSynchronizer().getCompareParameters());
		MainFrame.getInstance().getTreeSelectionSynchronizer().getCompareParameters().setProcessRooted(rootedRadioButton.isSelected());
		MainFrame.getInstance().getTreeSelectionSynchronizer().reset();
		return true;
	}
	
	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		setTitle("Tree selection synchronization compare parameters");
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
			jContentPane.add(getProcessRootedPanel(), null);
			jContentPane.add(getCompareTextElementDataParametersPanel(), null);
			jContentPane.add(getButtonsPanel(), null);
			getApplyButton().setVisible(false);
		}
		return jContentPane;
	}
	
	
	private JPanel getProcessRootedPanel() {
		if (processRootedPanel == null) {
			processRootedPanel = new JPanel();
			FlowLayout flowLayout = (FlowLayout) processRootedPanel.getLayout();
			flowLayout.setVgap(0);
			processRootedPanel.setBorder(BorderFactory.createTitledBorder(null, "Process trees as", 
					TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, 
					new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			processTreeAsGroup = new ButtonGroup();
			
			GridBagConstraints unrootedGBC = new GridBagConstraints();	
			unrootedGBC.gridx = 0;
			unrootedGBC.anchor = GridBagConstraints.WEST;
			unrootedGBC.gridy =  0;
			unrootedGBC.insets = new Insets(4, 6, 4, 0);
			unrootedGBC.gridwidth = GridBagConstraints.RELATIVE;
			unrootedGBC.weighty = 2.0;
			unrootedRadioButton = new JRadioButton("unrooted");
			unrootedRadioButton.setSelected(true);
			processTreeAsGroup.add(unrootedRadioButton);
			processRootedPanel.add(unrootedRadioButton, unrootedGBC);
			
			GridBagConstraints rootedGBC = new GridBagConstraints();	
			rootedGBC.gridx = 1;
			rootedGBC.anchor = GridBagConstraints.WEST;
			rootedGBC.gridy =  0;
			rootedGBC.insets = new Insets(4, 6, 4, 0);
			rootedGBC.gridwidth = GridBagConstraints.RELATIVE;
			rootedGBC.weighty = 2.0;
			rootedRadioButton = new JRadioButton("rooted");
			processTreeAsGroup.add(rootedRadioButton);
			processRootedPanel.add(rootedRadioButton, rootedGBC);
		}
		return processRootedPanel;
	}
	
	
	private CompareTextElementDataParametersPanel getCompareTextElementDataParametersPanel() {
		if (compareTextElementDataParametersPanel == null) {
			compareTextElementDataParametersPanel = new CompareTextElementDataParametersPanel();
			compareTextElementDataParametersPanel.setBorder(BorderFactory.createTitledBorder(null, "Compare parameters", 
					TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, 
					new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
		}
		return compareTextElementDataParametersPanel;
	}
}
