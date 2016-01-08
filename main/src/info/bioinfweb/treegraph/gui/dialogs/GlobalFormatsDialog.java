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


import info.bioinfweb.treegraph.document.format.GlobalFormats;
import info.bioinfweb.treegraph.document.undo.format.GlobalFormatsEdit;

import javax.swing.*;
import java.awt.*;
import javax.swing.JCheckBox;
import java.awt.GridBagConstraints;



public class GlobalFormatsDialog extends EditDialog {
  private static final long serialVersionUID = 1L;
  
  
  private JPanel jContentPane = null;
	private JTabbedPane formatsTabbedPane = null;
	private JPanel documentPanel = null;
	private MarginInput documentMarginInput = null;
	private DistanceValueInput branchLengthScaleInput = null;
	private JColorChooser colorChooser = null;
	private JCheckBox showRootedCheckBox = null;
	private JCheckBox showScaleBarCheckBox = null;
	private JCheckBox alignToSubtreeCheckBox = null;
	private JCheckBox labelsToLeftCheckBox = null;


	/**
	 * @param owner
	 */
	public GlobalFormatsDialog(Frame owner) {
		super(owner);
		setHelpCode(48);
		initialize();
		this.pack();
		setLocationRelativeTo(owner);
	}


	@Override
	protected boolean onExecute() {
		setValues(getDocument().getTree().getFormats());
		return true;  // F�r globale formate muss kein Element markiert sein.
	}


	@Override
	protected boolean apply() {
		getDocument().executeEdit(new GlobalFormatsEdit(getDocument(), getValues()));
		return true;
	}
	
	
	private void setValues(GlobalFormats f) {
		documentMarginInput.setValue(f.getDocumentMargin());
		branchLengthScaleInput.setValue(f.getBranchLengthScale());
		getShowScaleBarCheckBox().setSelected(f.getShowScaleBar());
		getShowRootedCheckBox().setSelected(f.getShowRooted());
		getAlignToSubtreeCheckBox().setSelected(f.getAlignLegendsToSubtree());
		getLabelsToLeftCheckBox().setSelected(f.getPositionLabelsToLeft());
		colorChooser.setColor(f.getBackgroundColor());
	}

	
	private GlobalFormats getValues() {
		GlobalFormats result = new GlobalFormats();
		
		documentMarginInput.assignValueTo(result.getDocumentMargin());
		branchLengthScaleInput.assignValueTo(result.getBranchLengthScale());
		result.setShowScaleBar(getShowScaleBarCheckBox().isSelected());
		result.setShowRooted(getShowRootedCheckBox().isSelected());
		result.setAlignLegendsToSubtree(getAlignToSubtreeCheckBox().isSelected());
		result.setPositionLabelsToLeft(getLabelsToLeftCheckBox().isSelected());
		result.setBackgroundColor(colorChooser.getColor());
		
		return result;
	}


	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
  private void addSpacer(JPanel panel, int y) {
  	JLabel spacer = new JLabel(" ");
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = y;
		gbc.ipadx = 3;
		gbc.anchor = GridBagConstraints.WEST;
		panel.add(spacer, gbc);
  }
	
	
	private void initialize() {
		//this.setSize(360, 380);
		this.setTitle("Global document formats");
		this.setContentPane(getJContentPane());
		this.pack();
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
			jContentPane.add(getFormatsTabbedPane(), null);
			jContentPane.add(getButtonsPanel(), null);
		}
		return jContentPane;
	}
	
	
	private JPanel getDocumentPanel() {
		if (documentPanel == null) {
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.anchor = GridBagConstraints.WEST;
			gridBagConstraints.gridwidth = 3;
			gridBagConstraints.gridy = 1;
			GridBagConstraints alignTosubtreeGBC = new GridBagConstraints();
			alignTosubtreeGBC.anchor = GridBagConstraints.WEST;
			alignTosubtreeGBC.gridx = 0;
			alignTosubtreeGBC.gridy = 2;
			alignTosubtreeGBC.gridwidth = 3;
			GridBagConstraints showScaleBarGBC = new GridBagConstraints();
			showScaleBarGBC.anchor = GridBagConstraints.WEST;
			showScaleBarGBC.gridx = 1;
			showScaleBarGBC.gridy = 0;
			showScaleBarGBC.gridwidth = 2;
			GridBagConstraints showRootedGBC = new GridBagConstraints();
			showRootedGBC.anchor = GridBagConstraints.WEST;
			showRootedGBC.gridx = 0;
			showRootedGBC.gridy = 0;
			showRootedGBC.gridwidth = 1;
			documentPanel = new JPanel();
			documentPanel.setLayout(new GridBagLayout());
			documentPanel.add(getShowRootedCheckBox(), showRootedGBC);
			documentPanel.add(getShowScaleBarCheckBox(), showScaleBarGBC);
			documentPanel.add(getAlignToSubtreeCheckBox(), alignTosubtreeGBC);
			documentPanel.add(getLabelsToLeftCheckBox(), gridBagConstraints);
			addSpacer(documentPanel, 3);
			branchLengthScaleInput = new DistanceValueInput("Distance per branch length unit: ", documentPanel, 4);
			addSpacer(documentPanel, 6);
			documentMarginInput = new MarginInput("Document", documentPanel, 7);
		}
		return documentPanel;
	}
	
	
	private JColorChooser getColorChooser() {
		if (colorChooser == null) {
			colorChooser = new JColorChooser(Color.BLACK);
		}
		return colorChooser;
	}

	
	/**
	 * This method initializes formatsTabbedPane	
	 * 	
	 * @return javax.swing.JTabbedPane	
	 */
	private JTabbedPane getFormatsTabbedPane() {
		if (formatsTabbedPane == null) {
			formatsTabbedPane = new JTabbedPane();
			formatsTabbedPane.addTab("Document", getDocumentPanel());
			formatsTabbedPane.addTab("Background color", getColorChooser());
		}
		return formatsTabbedPane;
	}


	/**
	 * This method initializes showRootedCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getShowRootedCheckBox() {
		if (showRootedCheckBox == null) {
			showRootedCheckBox = new JCheckBox();
			showRootedCheckBox.setText("Show rooted tree");
		}
		return showRootedCheckBox;
	}


	/**
	 * This method initializes showScaleBarCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getShowScaleBarCheckBox() {
		if (showScaleBarCheckBox == null) {
			showScaleBarCheckBox = new JCheckBox();
			showScaleBarCheckBox.setText("Show scale bar");
		}
		return showScaleBarCheckBox;
	}


	/**
	 * This method initializes alignLegendsToSubtreeCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getAlignToSubtreeCheckBox() {
		if (alignToSubtreeCheckBox == null) {
			alignToSubtreeCheckBox = new JCheckBox();
			alignToSubtreeCheckBox.setText("Horizontal alignment of legends to subtree (Uncheck to align to the whole tree.)");
		}
		return alignToSubtreeCheckBox;
	}


	/**
	 * This method initializes labelsToLeftCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getLabelsToLeftCheckBox() {
		if (labelsToLeftCheckBox == null) {
			labelsToLeftCheckBox = new JCheckBox();
			labelsToLeftCheckBox.setText("Label block overhang only on left side (only relevant in the phylogram/chronogram view)");
		}
		return labelsToLeftCheckBox;
	}
}