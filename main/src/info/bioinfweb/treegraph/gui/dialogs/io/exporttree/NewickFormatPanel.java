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
package info.bioinfweb.treegraph.gui.dialogs.io.exporttree;


import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import info.bioinfweb.commons.collections.ParameterMap;
import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.io.ReadWriteParameterMap;
import info.bioinfweb.treegraph.document.nodebranchdata.BranchLengthAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.HiddenDataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.TextLabelAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.VoidNodeBranchDataAdapter;
import info.bioinfweb.treegraph.gui.dialogs.nodebranchdatainput.NodeBranchDataInput;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;



public class NewickFormatPanel extends JPanel implements TreeFormatPanel {
	private JPanel sourceDataPanel = null;
	private NodeBranchDataInput internalInput = null;
	private NodeBranchDataInput leafInput = null;
	private NodeBranchDataInput branchLengthInput = null;
	private JLabel internalLabel = null;
	private JLabel leafLabel = null;
	private JLabel branchLengthLabel = null;
	
	private JPanel nodeNameFormatPanel;
	private JRadioButton singleQuotationRadioButton;
	private JRadioButton spacesAsUnderscoresRadioButton;
	private final ButtonGroup nodeNameFormatButtonGroup = new ButtonGroup();
	
	
	public NewickFormatPanel() {
		super();
		initialize();
	}

	
	@Override
	public void addProperties(ParameterMap properties) {
		properties.put(ReadWriteParameterMap.KEY_SPACES_AS_UNDERSCORE, getSpacesAsUnderscoresRadioButton().isSelected());
		properties.put(ReadWriteParameterMap.KEY_INTERNAL_NODE_NAMES_ADAPTER, getInternalInput().getSelectedAdapter());
		properties.put(ReadWriteParameterMap.KEY_LEAF_NODE_NAMES_ADAPTER, getLeafInput().getSelectedAdapter());
		properties.put(ReadWriteParameterMap.KEY_BRANCH_LENGTH_ADAPTER, getBranchLengthInput().getSelectedAdapter());
	}


	private void initialize() {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		this.add(getNodeNameFormatPanel());
		this.add(getSourceDataPanel());
	}
	

	@Override
	public void initializeContents(Document document) {
		if (document != null) {
			getInternalInput().setAdapters(document.getTree(), true, true, true, false, false, "Do not export internal node names");
			if (!getInternalInput().setSelectedAdapter(TextLabelAdapter.class)) {
				if (!getInternalInput().setSelectedAdapter(HiddenDataAdapter.class)) {
					if (document.getDefaultSupportAdapter() instanceof VoidNodeBranchDataAdapter) {  //TODO Remove when VoidAdapter naming was refactored.
						getInternalInput().setSelectedAdapter(VoidNodeBranchDataAdapter.class);
					}
					else {
						getInternalInput().setSelectedAdapter(document.getDefaultSupportAdapter());
					}
				}
			}
			getLeafInput().setAdapters(document.getTree(), true, true, true, false, false, "");
			getLeafInput().setSelectedAdapter(document.getDefaultLeafAdapter());
			getBranchLengthInput().setAdapters(document.getTree(), false, false, true, true, false, "Do not export branch lengths");
			getBranchLengthInput().setSelectedAdapter(BranchLengthAdapter.class);
		}
	}


	/**
	 * This method initializes nodeDataPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	protected JPanel getSourceDataPanel() {
		if (sourceDataPanel == null) {			
			sourceDataPanel = new JPanel();
			sourceDataPanel.setLayout(new GridBagLayout());
			sourceDataPanel.setBorder(BorderFactory.createTitledBorder(null, "Source data", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, 
					new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			
			GridBagConstraints gbc_internalLabel = new GridBagConstraints();
			gbc_internalLabel.gridx = 0;
			gbc_internalLabel.anchor = GridBagConstraints.WEST;
			gbc_internalLabel.insets = new Insets(0, 2, 0, 0);
			gbc_internalLabel.gridy = 0;
			internalLabel = new JLabel();
			internalLabel.setText("Internal node names: ");
			sourceDataPanel.add(internalLabel, gbc_internalLabel);		
			internalInput = new NodeBranchDataInput(sourceDataPanel, 1, 0);
			
			GridBagConstraints gbc_leafLabel = new GridBagConstraints();
			gbc_leafLabel.gridx = 0;
			gbc_leafLabel.anchor = GridBagConstraints.WEST;
			gbc_leafLabel.insets = new Insets(0, 2, 0, 0);
			gbc_leafLabel.gridy = 1;
			leafLabel = new JLabel();
			leafLabel.setText("Leaf node names: ");
			sourceDataPanel.add(leafLabel, gbc_leafLabel);
			leafInput = new NodeBranchDataInput(sourceDataPanel, 1, 1);
			
			GridBagConstraints gbc_branchLengthLabel = new GridBagConstraints();
			gbc_branchLengthLabel.anchor = GridBagConstraints.WEST;
			gbc_branchLengthLabel.gridx = 0;
			gbc_branchLengthLabel.gridy = 2;
			gbc_branchLengthLabel.insets = new Insets(0, 2, 0, 0);
			branchLengthLabel = new JLabel();
			branchLengthLabel.setText("Branch lengths: ");			
			sourceDataPanel.add(branchLengthLabel, gbc_branchLengthLabel);			
			branchLengthInput = new NodeBranchDataInput(sourceDataPanel, 1, 2);
		}
		return sourceDataPanel;
	}


	public NodeBranchDataInput getInternalInput() {
		getSourceDataPanel();
		return internalInput;
	}


	public NodeBranchDataInput getLeafInput() {
		getSourceDataPanel();
		return leafInput;
	}


	public NodeBranchDataInput getBranchLengthInput() {
		getSourceDataPanel();
		return branchLengthInput;
	}


	protected JPanel getNodeNameFormatPanel() {
		if (nodeNameFormatPanel == null) {
			nodeNameFormatPanel = new JPanel();
			nodeNameFormatPanel.setBorder(new TitledBorder(null, "Node name format", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, 
					new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			nodeNameFormatPanel.setLayout(new GridBagLayout());
			
			GridBagConstraints gbc_singleQuotationRadioButton = new GridBagConstraints();
			gbc_singleQuotationRadioButton.weightx = 1.0;
			gbc_singleQuotationRadioButton.fill = GridBagConstraints.HORIZONTAL;
			gbc_singleQuotationRadioButton.insets = new Insets(0, 0, 0, 5);
			gbc_singleQuotationRadioButton.gridx = 0;
			gbc_singleQuotationRadioButton.gridy = 0;
			nodeNameFormatPanel.add(getSingleQuotationRadioButton(), gbc_singleQuotationRadioButton);
			
			GridBagConstraints gbc_spacesAsUnderscoresRadioButton = new GridBagConstraints();
			gbc_spacesAsUnderscoresRadioButton.insets = new Insets(0, 0, 0, 5);
			gbc_spacesAsUnderscoresRadioButton.anchor = GridBagConstraints.EAST;
			gbc_spacesAsUnderscoresRadioButton.weightx = 1.0;
			gbc_spacesAsUnderscoresRadioButton.gridx = 1;
			gbc_spacesAsUnderscoresRadioButton.gridy = 0;
			nodeNameFormatPanel.add(getSpacesAsUnderscoresRadioButton(), gbc_spacesAsUnderscoresRadioButton);
		}
		return nodeNameFormatPanel;
	}
	
	
	private JRadioButton getSingleQuotationRadioButton() {
		if (singleQuotationRadioButton == null) {
			singleQuotationRadioButton = new JRadioButton("Quotation marks if necessary");
			nodeNameFormatButtonGroup.add(singleQuotationRadioButton);
			singleQuotationRadioButton.setSelected(true);
		}
		return singleQuotationRadioButton;
	}
	
	
	private JRadioButton getSpacesAsUnderscoresRadioButton() {
		if (spacesAsUnderscoresRadioButton == null) {
			spacesAsUnderscoresRadioButton = new JRadioButton("Spaces as underscores (No underscores in names possible)");
			nodeNameFormatButtonGroup.add(spacesAsUnderscoresRadioButton);
		}
		return spacesAsUnderscoresRadioButton;
	}
}
