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
package info.bioinfweb.treegraph.gui.dialogs.specialformats;


import info.bioinfweb.treegraph.document.format.adapters.color.ColorAdapter;
import info.bioinfweb.treegraph.document.undo.format.ColorsByNodeBranchDataEdit;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JColorChooser;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.ListModel;
import javax.swing.border.TitledBorder;


/**
 * @author Ben St&ouml;ver
 * @since 2.0.23
 */
public class ColorsByNodeBranchDataDialog extends FormatsByNodeBranchDataDialog {
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * This field is necessary to access this instance from the action listeners.
	 */
	private ColorsByNodeBranchDataDialog self = this; 
	
	private JPanel jContentPane = null;
	private JPanel colorPanel = null;
	private JPanel minMaxPanel = null;
	private JButton minColorButton = null;
	private ColorPreviewPanel colorPreviewPanel = null;
	private JButton maxColorButton = null;
	private JCheckBox changeUndefinedCheckBox = null;
	private JCheckBox inheritToTerminalsCheckBox = null;


	/**
	 * @param owner
	 */
	public ColorsByNodeBranchDataDialog(Frame owner) {
		super(owner, 38);
		initialize();
		setLocationRelativeTo(owner);
	}


	@Override
	protected boolean customizeTarget() {
		getTargetListModel().setAdapters(getDocument().getTree().getPaintStart());
		if (getTargetListModel().getSize() >= 2) {
			getTargetList().setSelectionInterval(0, 1);
		}
		return true;
	}


	@Override
	protected boolean apply() {
		Vector<ColorAdapter> adapters = new Vector<ColorAdapter>();
		int[] selection = getTargetList().getSelectedIndices();
		for (int i = 0; i < selection.length; i++) {
			adapters.add(getTargetListModel().getElementAt(selection[i]));
		}
		
		getDocument().executeEdit(new ColorsByNodeBranchDataEdit(getDocument(),
				getSourceComboBoxModel().getSelectedItem(), 
				getColorPreviewPanel().getMinColor(), 
				getColorPreviewPanel().getMaxColor(),
				getChangeUndefinedCheckBox().isSelected(),
				getInheritToTerminalsCheckBox().isSelected(),
				adapters.toArray(new ColorAdapter[adapters.size()])));
		
		return true;
	}


	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		setHelpCode(37);
	  setTitle("Set colors by node/branch data");
		setContentPane(getJContentPane());
		pack();
	}


	@Override
	protected ListModel createTargetListModel() {
		return new ColorAdapterListModel();
	}


	@Override
	protected ColorAdapterListModel getTargetListModel() {
		return (ColorAdapterListModel)super.getTargetListModel();
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
			jContentPane.add(getColorPanel(), null);
			jContentPane.add(getTargetPanel(), null);
			jContentPane.add(getButtonsPanel(), null);
		}
		return jContentPane;
	}


	/**
	 * This method initializes colorPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getColorPanel() {
		if (colorPanel == null) {
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.gridx = 1;
			gridBagConstraints4.anchor = GridBagConstraints.WEST;
			gridBagConstraints4.weightx = 1.0;
			gridBagConstraints4.gridy = 3;
			GridBagConstraints gridBagConstraints31 = new GridBagConstraints();
			gridBagConstraints31.gridx = 0;
			gridBagConstraints31.gridwidth = 2;
			gridBagConstraints31.anchor = GridBagConstraints.WEST;
			gridBagConstraints31.gridy = 2;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = -1;
			gridBagConstraints2.gridy = -1;
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.gridx = 0;
			gridBagConstraints11.gridwidth = 2;
			gridBagConstraints11.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints11.weightx = 1.0;
			gridBagConstraints11.gridy = 1;
			colorPanel = new JPanel();
			colorPanel.setLayout(new GridBagLayout());
			colorPanel.setBorder(BorderFactory.createTitledBorder(null, "Color", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			colorPanel.add(getMinMaxPanel(), gridBagConstraints11);
			colorPanel.add(getChangeUndefinedCheckBox(), gridBagConstraints31);
			colorPanel.add(getInheritToTerminalsCheckBox(), gridBagConstraints4);
		}
		return colorPanel;
	}


	/**
	 * This method initializes minMaxPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getMinMaxPanel() {
		if (minMaxPanel == null) {
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.fill = GridBagConstraints.BOTH;
			gridBagConstraints3.insets = new Insets(0, 5, 0, 5);
			gridBagConstraints3.weightx = 1.0;
			minMaxPanel = new JPanel();
			minMaxPanel.setLayout(new GridBagLayout());
			minMaxPanel.add(getMinColorButton(), new GridBagConstraints());
			minMaxPanel.add(getColorPreviewPanel(), gridBagConstraints3);
			minMaxPanel.add(getMaxColorButton(), new GridBagConstraints());
		}
		return minMaxPanel;
	}


	/**
	 * This method initializes minColorButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getMinColorButton() {
		if (minColorButton == null) {
			minColorButton = new JButton();
			minColorButton.setText("Minimum...");
			minColorButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Color color = JColorChooser.showDialog(
							self, "Minimal color", getColorPreviewPanel().getMinColor());
					
					if (color != null) {
						getColorPreviewPanel().setMinColor(color);
					}
				}
			});
		}
		return minColorButton;
	}


	/**
	 * This method initializes colorPreviewPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private ColorPreviewPanel getColorPreviewPanel() {
		if (colorPreviewPanel == null) {
			colorPreviewPanel = new ColorPreviewPanel();
		}
		return colorPreviewPanel;
	}


	/**
	 * This method initializes maxColorButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getMaxColorButton() {
		if (maxColorButton == null) {
			maxColorButton = new JButton();
			maxColorButton.setText("Maximum...");
			maxColorButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Color color = JColorChooser.showDialog(
							self, "Maxmal color", getColorPreviewPanel().getMaxColor());
					
					if (color != null) {
						getColorPreviewPanel().setMaxColor(color);
					}
				}
			});
		}
		return maxColorButton;
	}


	/**
	 * This method initializes hCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getChangeUndefinedCheckBox() {
		if (changeUndefinedCheckBox == null) {
			changeUndefinedCheckBox = new JCheckBox();
			changeUndefinedCheckBox.setText("Set undefined to minmal color");
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
			inheritToTerminalsCheckBox.setText("Set parent color to terminals");
		}
		return inheritToTerminalsCheckBox;
	}
}