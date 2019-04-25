/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2011, 2013-2019  Ben Stöver, Sarah Wiechers, Kai Müller
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
package info.bioinfweb.treegraph.gui.dialogs.io.ancestralstate;


import info.bioinfweb.treegraph.Main;
import info.bioinfweb.treegraph.document.Tree;
import info.bioinfweb.treegraph.document.io.ancestralstate.AncestralStateData;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeNameAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.PieChartLabelAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.VoidNodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.undo.file.ancestralstate.AncestralStateImportParameters;
import info.bioinfweb.treegraph.gui.dialogs.nodebranchdata.AbstractNodeBranchDataColumnsDialog;
import info.bioinfweb.treegraph.gui.dialogs.nodebranchdatainput.NewNodeBranchDataInput;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;



public class AssignBayesTraitsImportColumnsDialog extends AbstractNodeBranchDataColumnsDialog {	
	private JPanel jContentPane = null;
	
	private JPanel importInternalNodeNamesPanel = null;
	private AncestralStateImportParameters parameters = null;
	private AncestralStateData data = null;
	private List<CharacterInput> characterInputs = new ArrayList<CharacterInput>();
	private NewNodeBranchDataInput internalNodeNames = null;
	
	private JPanel characterListHeadingPanel = null;
	private JLabel importCharacterDataLabel = null;
	private JLabel importInternalNodeNamesLabel = null;
	private JButton invertSelectionButton;
	private JButton selectAllButton;
	private JButton selectNoneButton;


	/**
	 * Creates a new instance of this dialog.
	 * 
	 * @param owner the parent window
	 */
	public AssignBayesTraitsImportColumnsDialog(Frame owner) {
		super(owner, true, Main.getInstance().getWikiHelp());
		setHelpCode(81);
		initialize();
		setLocationRelativeTo(owner);
	}


	private void createInputs(AncestralStateData data) {
		getImportPanel().removeAll();
		characterInputs.clear();
		
		Iterator<String> keySetIterator = data.getSiteIterator();
		int bottomY = 0;
		while (keySetIterator.hasNext()) {
			CharacterInput input = new CharacterInput(getImportPanel(), bottomY, data, tree, keySetIterator.next());
			bottomY = input.getBottomY();
			characterInputs.add(input);
		}

		pack();
		setLocationRelativeTo(getOwner());
		
		// Make sure the task bar does not overlap with the dialog:
		Rectangle bounds = getBounds();
		setBounds(bounds.x, bounds.y, bounds.width, Math.min(bounds.height, 
				Math.round((float)Toolkit.getDefaultToolkit().getScreenSize().getHeight() * 0.9f)));
	}
	
	
	public boolean execute(AncestralStateImportParameters parameters, AncestralStateData data, Tree tree) {
		this.tree = tree;
		this.parameters = parameters;
		this.data = data;

		createInputs(data);
		internalNodeNames.setAdapters(tree, false, true, false, false, true, "Do not import internal node names");
		internalNodeNames.setSelectedAdapter(NodeNameAdapter.class);
		internalNodeNames.setID("");

		return execute();
	}
	
	
	/**
	 * Tests if selected columns already exist as prompts the user to proceed.
	 * 
	 * @see info.bioinfweb.commons.swing.OkCancelApplyDialog#apply()
	 */
	@Override
	protected boolean apply() {
		NodeBranchDataAdapter[] importAdapters = new NodeBranchDataAdapter[data.getCharacterStateCount()];
		String[] labelIDs = new String[data.getSiteCount()];
		parameters.setImportAdapters(importAdapters);
		parameters.setPieChartLabelIDs(labelIDs);
		Iterator<String> keySetIterator = data.getSiteIterator();
		int importAdapterIndexStart = 0;
		int labelIDsIndexStart = 0;
		while (keySetIterator.hasNext()) {
			characterInputs.get(labelIDsIndexStart).assignParameters(parameters, importAdapterIndexStart, labelIDsIndexStart);
			importAdapterIndexStart += data.getStateCountPerSite(keySetIterator.next());
			labelIDsIndexStart += 1;
		}
		parameters.setInternalNodeNamesAdapter(internalNodeNames.getSelectedAdapter());		
		
		int importAdapterCount = parameters.getImportAdapters().length;
		int pieChartLabelCount = parameters.getPieChartLabelIDs().length;
		NodeBranchDataAdapter[] allAdapters = new NodeBranchDataAdapter[1 + importAdapterCount + pieChartLabelCount];
		allAdapters[0] = parameters.getInternalNodeNamesAdapter();
		for (int i = 0; i < importAdapterCount; i++) {
			allAdapters[i + 1] = parameters.getImportAdapters()[i];
		}
		for (int i = 0; i < pieChartLabelCount; i++) {
			String pieChartLabelID = parameters.getPieChartLabelIDs()[i];
			if (pieChartLabelID != null) {
				allAdapters[i + importAdapterCount + 1] = new PieChartLabelAdapter(pieChartLabelID);
			}
			else {
				allAdapters[i + importAdapterCount + 1] = new VoidNodeBranchDataAdapter("");
			}
		}
		return checkSelectedAdapters(allAdapters, null);
	}


	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		setTitle("Import BayesTraits data");
		setSize(380, 400);
		getApplyButton().setVisible(false);
		setContentPane(getJContentPane());
	}


	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BoxLayout(jContentPane, BoxLayout.Y_AXIS));
			jContentPane.add(getCharacterListHeadingPanel());
			jContentPane.add(getScrollPane());
			jContentPane.add(getImportInternalNodeNamesPanel(), null);
			jContentPane.add(getButtonsPanel(), null);
		}
		return jContentPane;
	}
	
	
	private JPanel getCharacterListHeadingPanel() {
		if (characterListHeadingPanel == null) {
			characterListHeadingPanel = new JPanel();
			GridBagLayout gbl_characterListHeadingPanel = new GridBagLayout();
			characterListHeadingPanel.setLayout(gbl_characterListHeadingPanel);
			GridBagConstraints importCharacterDataLabelGBC = new GridBagConstraints();	
			importCharacterDataLabelGBC.weightx = 1.0;
			importCharacterDataLabelGBC.gridx = 0;
			importCharacterDataLabelGBC.anchor = GridBagConstraints.WEST;
			importCharacterDataLabelGBC.gridy =  0;
			importCharacterDataLabelGBC.insets = new Insets(4, 6, 5, 5);
			importCharacterDataLabelGBC.gridwidth = 3;
			importCharacterDataLabelGBC.weighty = 2.0;
			importCharacterDataLabel = new JLabel();
			importCharacterDataLabel.setText("Import ancestral state data");
			characterListHeadingPanel.add(importCharacterDataLabel, importCharacterDataLabelGBC);			
			GridBagConstraints gbc_btnSelectAll = new GridBagConstraints();
			gbc_btnSelectAll.insets = new Insets(0, 4, 2, 5);
			gbc_btnSelectAll.gridx = 0;
			gbc_btnSelectAll.gridy = 1;
			characterListHeadingPanel.add(getSelectAllButton(), gbc_btnSelectAll);
			GridBagConstraints gbc_btnSelectNone = new GridBagConstraints();
			gbc_btnSelectNone.insets = new Insets(0, 0, 2, 5);
			gbc_btnSelectNone.gridx = 1;
			gbc_btnSelectNone.gridy = 1;
			characterListHeadingPanel.add(getSelectNoneButton(), gbc_btnSelectNone);
			GridBagConstraints gbc_btnInvertSelection = new GridBagConstraints();
			gbc_btnInvertSelection.insets = new Insets(0, 0, 2, 4);
			gbc_btnInvertSelection.anchor = GridBagConstraints.WEST;
			gbc_btnInvertSelection.gridx = 2;
			gbc_btnInvertSelection.gridy = 1;
			characterListHeadingPanel.add(getInvertSelectionButton(), gbc_btnInvertSelection);
		}
		return characterListHeadingPanel;
	}


	private JPanel getImportInternalNodeNamesPanel() {
		if (importInternalNodeNamesPanel == null) {
			importInternalNodeNamesPanel = new JPanel();
			importInternalNodeNamesPanel.setLayout(new GridBagLayout());
			GridBagConstraints importInternalNodeNamesLabelGBC = new GridBagConstraints();	
			importInternalNodeNamesLabelGBC.gridx = 0;
			importInternalNodeNamesLabelGBC.anchor = GridBagConstraints.WEST;
			importInternalNodeNamesLabelGBC.gridy = 0;
			importInternalNodeNamesLabelGBC.insets = new Insets(4, 6, 4, 0);
			importInternalNodeNamesLabelGBC.gridwidth = GridBagConstraints.RELATIVE;
			importInternalNodeNamesLabel = new JLabel();
			importInternalNodeNamesLabel.setText("Import internal node names");
			importInternalNodeNamesPanel.add(importInternalNodeNamesLabel, importInternalNodeNamesLabelGBC);
			
			internalNodeNames = new NewNodeBranchDataInput(importInternalNodeNamesPanel, 1, 1, true);
		}
		return importInternalNodeNamesPanel;
	}
	
	
	private JButton getInvertSelectionButton() {
		if (invertSelectionButton == null) {
			invertSelectionButton = new JButton("Invert selection");
			invertSelectionButton.setMnemonic('i');
			invertSelectionButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					for (CharacterInput characterInput : characterInputs) {
						characterInput.setSelected(!characterInput.isSelected());
					}
				}
			});
		}
		return invertSelectionButton;
	}
	
	
	private JButton getSelectAllButton() {
		if (selectAllButton == null) {
			selectAllButton = new JButton("Select all");
			selectAllButton.setMnemonic('a');
			selectAllButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					for (CharacterInput characterInput : characterInputs) {
						characterInput.setSelected(true);
					}
				}
			});
		}
		return selectAllButton;
	}
	
	
	private JButton getSelectNoneButton() {
		if (selectNoneButton == null) {
			selectNoneButton = new JButton("Select none");
			selectNoneButton.setMnemonic('n');
			selectNoneButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					for (CharacterInput characterInput : characterInputs) {
						characterInput.setSelected(false);
					}
				}
			});
		}
		return selectNoneButton;
	}
}
