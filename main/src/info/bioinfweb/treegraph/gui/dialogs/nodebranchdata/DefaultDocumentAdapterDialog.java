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
package info.bioinfweb.treegraph.gui.dialogs.nodebranchdata;


import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import info.bioinfweb.treegraph.document.undo.DocumentEdit;
import info.bioinfweb.treegraph.document.undo.edit.DefaultDocumentAdapterEdit;
import info.bioinfweb.treegraph.gui.dialogs.EditDialog;
import info.bioinfweb.treegraph.gui.dialogs.nodebranchdatainput.NodeBranchDataInput;
import info.bioinfweb.treegraph.gui.mainframe.MainFrame;



public class DefaultDocumentAdapterDialog extends EditDialog {
	private JPanel jContentPane = null;
	private JPanel defaultDocumentAdaptersPanel = null;
	private JLabel defaultSupportAdapterLabel = null;
	private NodeBranchDataInput defaultLeafAdapterInput;
	private NodeBranchDataInput defaultSupportAdapterInput;
	
	
	public DefaultDocumentAdapterDialog(MainFrame mainFrame) {
		super(mainFrame);
		setHelpCode(79);
		initialize();
		setLocationRelativeTo(mainFrame);
	}
	

	@Override
	protected boolean onExecute() {
		defaultLeafAdapterInput.setAdapters(getDocument().getTree(), true, true, false, false, false, null);
		defaultSupportAdapterInput.setAdapters(getDocument().getTree(), false, true, true, false, false, "No support values available");  // DecimalOnly is not set because previously set default document adapters may not be displayed then.
		defaultLeafAdapterInput.setSelectedAdapter(getDocument().getDefaultLeafAdapter());
		defaultSupportAdapterInput.setSelectedAdapter(getDocument().getDefaultSupportAdapter());
		return true;
	}

	
	@Override
	protected boolean apply() {
		DocumentEdit edit = new DefaultDocumentAdapterEdit(getDocument(), defaultLeafAdapterInput.getSelectedAdapter(), defaultSupportAdapterInput.getSelectedAdapter());
		getDocument().executeEdit(edit);
		return true;
	}
	
	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		setTitle("Set default node/branch data columns");
		setContentPane(getJContentPane());
		setMinimumSize(new Dimension(300, 150));
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
			jContentPane.add(getDefaultDocumentAdaptersPanel(), null);
			jContentPane.add(getButtonsPanel(), null);
			getApplyButton().setVisible(false);
		}
		return jContentPane;
	}
	
	
	private JPanel getDefaultDocumentAdaptersPanel() {
		if (defaultDocumentAdaptersPanel == null) {
			defaultDocumentAdaptersPanel = new JPanel();
			defaultDocumentAdaptersPanel.setLayout(new GridBagLayout());
			
			GridBagConstraints defaultLeafAdapterLabelGBC = new GridBagConstraints();	
			defaultLeafAdapterLabelGBC.gridx = 0;
			defaultLeafAdapterLabelGBC.anchor = GridBagConstraints.WEST;
			defaultLeafAdapterLabelGBC.gridy =  0;
			defaultLeafAdapterLabelGBC.insets = new Insets(4, 6, 4, 0);
			defaultLeafAdapterLabelGBC.gridwidth = GridBagConstraints.RELATIVE;
			defaultSupportAdapterLabel = new JLabel();
			defaultSupportAdapterLabel.setText("Set default leaf column:");
			defaultDocumentAdaptersPanel.add(defaultSupportAdapterLabel, defaultLeafAdapterLabelGBC);
			
			defaultLeafAdapterInput = new NodeBranchDataInput(defaultDocumentAdaptersPanel, 0, 1);		
			
			GridBagConstraints defaultSupportAdapterLabelGBC = new GridBagConstraints();
			defaultSupportAdapterLabelGBC.gridx = 0;
			defaultSupportAdapterLabelGBC.anchor = GridBagConstraints.WEST;
			defaultSupportAdapterLabelGBC.gridy =  2;
			defaultSupportAdapterLabelGBC.insets = new Insets(4, 6, 4, 0);
			defaultSupportAdapterLabelGBC.gridwidth = GridBagConstraints.RELATIVE;
			defaultSupportAdapterLabel = new JLabel();
			defaultSupportAdapterLabel.setText("Set default support column:");
			defaultDocumentAdaptersPanel.add(defaultSupportAdapterLabel, defaultSupportAdapterLabelGBC);
			
			defaultSupportAdapterInput = new NodeBranchDataInput(defaultDocumentAdaptersPanel, 0, 3);			
		}
		return defaultDocumentAdaptersPanel;
	}	
}
