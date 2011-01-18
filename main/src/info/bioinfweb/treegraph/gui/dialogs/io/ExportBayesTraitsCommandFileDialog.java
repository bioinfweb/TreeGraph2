/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2011  Ben Stöver, Kai Müller
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


import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.graphics.export.GraphicFilter;
import info.bioinfweb.treegraph.graphics.export.GraphicWriterFactory;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import java.awt.Frame;
import java.io.File;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.Font;
import java.awt.Color;
import javax.swing.JRadioButton;



/**
 * Dialog that allows to export BayesTraits node definitions.
 * 
 * @author Ben St&ouml;ver
 * @since 2.0.46
 */
public class ExportBayesTraitsCommandFileDialog extends FileDialog {
	public static final String EXPORT_FILE_EXT = "txt";
	
	private static final long serialVersionUID = 1L;

	
	private JPanel jContentPane = null;
	private JPanel NodeSelectionPanel = null;
	private JPanel fileChooserPanel = null;
	private JRadioButton allNodesRadioButton = null;
	private JRadioButton selectedNodesRadioButton = null;
	private ButtonGroup nodesButtonGroup = null;  //  @jve:decl-index=0:
	private JFileChooser fileChooser = null;

	
	/**
	 * @param owner
	 */
	public ExportBayesTraitsCommandFileDialog(Frame owner) {
		super(owner, Option.ASK_TO_OVERWRITE);
		initialize();
		setLocationRelativeTo(owner);
		//setHelpCode();  //TODO festlegen
	}

	
	@Override
	protected boolean onApply(File file) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	protected boolean onExecute() {
		boolean nodeSelected = getSelection().containsType(Node.class);
		getSelectedNodesRadioButton().setEnabled(nodeSelected);
		getAllNodesRadioButton().setEnabled(nodeSelected);
		if (nodeSelected) {
			getSelectedNodesRadioButton().setSelected(true);
		}
		else {
			getAllNodesRadioButton().setSelected(true);
		}
		return true;
	}


	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
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
			jContentPane.add(getNodeSelectionPanel(), null);
			jContentPane.add(getFileChooserPanel(), null);
			getApplyButton().setVisible(false);
			getOkButton().setText("Export");
			jContentPane.add(getButtonsPanel(), null);
		}
		return jContentPane;
	}


	/**
	 * This method initializes NodeSelectionPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getNodeSelectionPanel() {
		if (NodeSelectionPanel == null) {
			getNodesButtonGroup();
			
			GridBagConstraints selectedGBC = new GridBagConstraints();
			selectedGBC.gridx = 1;
			selectedGBC.weightx = 1.0;
			selectedGBC.anchor = GridBagConstraints.CENTER;
			selectedGBC.fill = GridBagConstraints.HORIZONTAL;
			selectedGBC.gridy = 0;
			GridBagConstraints allGBC = new GridBagConstraints();
			allGBC.gridx = 0;
			allGBC.weightx = 1.0;
			allGBC.anchor = GridBagConstraints.CENTER;
			allGBC.fill = GridBagConstraints.HORIZONTAL;
			allGBC.gridy = 0;
			NodeSelectionPanel = new JPanel();
			NodeSelectionPanel.setLayout(new GridBagLayout());
			NodeSelectionPanel.setBorder(BorderFactory.createTitledBorder(null, "Nodes to export", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			NodeSelectionPanel.add(getAllNodesRadioButton(), allGBC);
			NodeSelectionPanel.add(getSelectedNodesRadioButton(), selectedGBC);
		}
		return NodeSelectionPanel;
	}


	/**
	 * This method initializes fileChooserPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getFileChooserPanel() {
		if (fileChooserPanel == null) {
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			fileChooserPanel = new JPanel();
			fileChooserPanel.setLayout(new GridBagLayout());
			fileChooserPanel.add(getFileChooser(), gridBagConstraints);
		}
		return fileChooserPanel;
	}


	/**
	 * This method initializes allNodesRadioButton	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getAllNodesRadioButton() {
		if (allNodesRadioButton == null) {
			allNodesRadioButton = new JRadioButton();
			allNodesRadioButton.setText("all");
		}
		return allNodesRadioButton;
	}


	/**
	 * This method initializes selectedNodesRadioButton	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getSelectedNodesRadioButton() {
		if (selectedNodesRadioButton == null) {
			selectedNodesRadioButton = new JRadioButton();
			selectedNodesRadioButton.setText("only selected");
		}
		return selectedNodesRadioButton;
	}


  private ButtonGroup getNodesButtonGroup() {
  	if (nodesButtonGroup == null) {
  		nodesButtonGroup = new ButtonGroup();
  		nodesButtonGroup.add(getAllNodesRadioButton());
  		nodesButtonGroup.add(selectedNodesRadioButton);
  	}
 		return nodesButtonGroup;
	}


	/**
	 * This method initializes fileChooser	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	@Override
	protected JFileChooser getFileChooser() {
		if (fileChooser == null) {
			fileChooser = new JFileChooser();
			fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
			fileChooser.setControlButtonsAreShown(false);
			
			if (fileChooser.getFileFilter() != null) {  // "Alle Dateien"-Filter entfernen
				fileChooser.removeChoosableFileFilter(fileChooser.getFileFilter());
			}
 			fileChooser.addChoosableFileFilter(new FileNameExtensionFilter(
 	        "Text files", EXPORT_FILE_EXT));
		}
		return fileChooser;
	}
}
