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


import info.bioinfweb.treegraph.Main;
import info.bioinfweb.treegraph.document.Branch;
import info.bioinfweb.treegraph.document.tools.IDManager;
import info.bioinfweb.treegraph.gui.mainframe.MainFrame;
import info.bioinfweb.wikihelp.client.OkCancelApplyWikiHelpDialog;

import java.awt.Frame;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.Collections;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JRadioButton;
import javax.swing.JPanel;

import org.apache.batik.ext.swing.JAffineTransformChooser.Dialog;



/**
 * Dialog used to solve name conflicts of ID elements.
 * 
 * @author Lukas Aaldering
 * @author Ben St&ouml;ver
 */
public class CollidingIDsDialog extends OkCancelApplyWikiHelpDialog {
	private static final long serialVersionUID = 1L;
	
	
	private static CollidingIDsDialog firstInstance = null;
	
	private String initialNewID = null;
	private Branch[] selection = null;
	private List<String> additionalReservedIDs = null;
	
	private JPanel jContentPane = null;
	private JPanel inputPanel = null;
	private JLabel messageLabel = null;
	private JLabel changeID2Label = null;
	private JLabel changeID1Label = null;
	private JTextField newIDTextField = null;
	private JTextField presentIDTextField = null;
	private JRadioButton overwriteRadioButton = null;
	private JRadioButton changeIDRadioButton = null;
	private JLabel overwriteLabel = null;
	private JLabel changeIDLabel = null;
	private ButtonGroup radioGroup = null; 
	
	
	/**
	 * @param owner
	 */
	private CollidingIDsDialog(Frame owner) {
		super(owner, true, Main.getInstance().getWikiHelp());
		initialize();
		setHelpCode(58);
	}
	
	
	public static CollidingIDsDialog getInstance(){
		if (firstInstance == null){
			firstInstance = new CollidingIDsDialog(MainFrame.getInstance());
		}
		return firstInstance;
	}

	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		setTitle("Conflicting IDs");
		setContentPane(getJContentPane());
		setSize(450, 280);
		getRadioGroup();
		getCancelButton().setVisible(false);
		getApplyButton().setVisible(false);
		setDefaultCloseOperation(Dialog.DO_NOTHING_ON_CLOSE);
	}

	
	@Override
	protected boolean apply() {
		boolean result = idsEqual();
		if (getOverwriteRadioButton().isSelected()) {
			return true;
		}
		else if (result){
			JOptionPane.showMessageDialog(this, "The IDs are still equal. Please type in two different IDs.", 
					"Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		else {
			String idText = "";
			if ((IDManager.idConflict(getPresentLabelID(), selection) || additionalReservedIDs.contains(getPresentLabelID())) 
					&& !getPresentLabelID().equals(initialNewID)) {
				
				idText = "\"" + getPresentLabelID() + "\"";
			}
			if ((IDManager.idConflict(getNewLabelID(), selection) || additionalReservedIDs.contains(getNewLabelID())) 
					&& !(getNewLabelID().equals(initialNewID) && !getPresentLabelID().equals(initialNewID))) {
				
				if (idText.equals("")) {
					idText = "\"" + getNewLabelID() + "\"";
				}
				else {
					idText = idText + " and \"" + getNewLabelID() + "\"";
				}
			}
			if (!idText.equals("")) {
				JOptionPane.showMessageDialog(this, "Elements with the ID(s) " + idText + 
						" are already present on the selected branch(es). Please specify " +
						"a different name.", "Error", JOptionPane.ERROR_MESSAGE);
			}
			return idText.equals("");
		}
	}

	
	public String checkConflicts(Branch[] selection, String id) {
		return checkConflicts(selection, id, Collections.<String>emptyList());
	}
	
	
	public String checkConflicts(Branch[] selection, String id, List<String> additionalReservedIDs) {
		if (IDManager.idConflict(id, selection)) {
			CollidingIDsDialog dialog = CollidingIDsDialog.getInstance();
			if (!dialog.promt(id, selection, additionalReservedIDs)){
				for (int i = 0; i < selection.length; i++) {
					if (IDManager.idExistsOnNode(selection[i].getTargetNode(), id)) {
						IDManager.renameID(id, dialog.getPresentLabelID(), selection[i].getTargetNode());
					}
				}
				id = dialog.getNewLabelID();
			}
		}
		return id;
	}
	
	
	public boolean promt(String id, Branch[] selection) {
		return promt(id, selection, Collections.<String>emptyList());
	}
	
	
	public boolean promt(String id, Branch[] selection, List<String> additionalReservedIDs) {
		this.selection = selection;
		this.additionalReservedIDs = additionalReservedIDs;
		this.initialNewID = id;
		getNewIDTextField().setText(id);
		getPresentIDTextField().setText(id);
		setLocationRelativeTo(getOwner());
		execute();
		return overwriteRadioButton.isSelected();
	}
	
	
	/**
	 * Returns the new ID for the previously present element, which was specified by the user. 
	 * @return
	 */
	public String getPresentLabelID() {
		return getPresentIDTextField().getText();
	}

	
	/**
	 * Returns the new ID for the new element, which was specified by the user. 
	 * @return
	 */
	public String getNewLabelID() {
		return getNewIDTextField().getText();
	}
	
	
	private boolean idsEqual() {
		return (getPresentLabelID().equals(getNewLabelID())); 
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
			jContentPane.add(getInputPanel(), null);
			jContentPane.add(getButtonsPanel());
		}
		return jContentPane;
	}

	
	/**
	 * This method initializes inputPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getInputPanel() {
		if (inputPanel == null) {
			GridBagConstraints gridBagConstraints18 = new GridBagConstraints();
			gridBagConstraints18.gridx = 1;
			gridBagConstraints18.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints18.gridwidth = 2;
			gridBagConstraints18.insets = new Insets(0, 0, 5, 0);
			gridBagConstraints18.gridy = 2;
			changeIDLabel = new JLabel();
			changeIDLabel.setText("<html><body>Change IDs.</body></html>");
			GridBagConstraints gridBagConstraints17 = new GridBagConstraints();
			gridBagConstraints17.gridx = 1;
			gridBagConstraints17.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints17.gridwidth = 2;
			gridBagConstraints17.insets = new Insets(0, 0, 6, 0);
			gridBagConstraints17.gridy = 1;
			overwriteLabel = new JLabel();
			overwriteLabel.setText("<html><body>Overwrite</html></body>");
			GridBagConstraints gridBagConstraints15 = new GridBagConstraints();
			gridBagConstraints15.insets = new Insets(0, 3, 0, 0);
			gridBagConstraints15.gridx = 0;
			gridBagConstraints15.gridy = 2;
			GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
			gridBagConstraints14.gridx = 0;
			gridBagConstraints14.insets = new Insets(0, 3, 6, 0);
			gridBagConstraints14.gridy = 1;
			GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
			gridBagConstraints13.insets = new Insets(0, 0, 0, 3);
			gridBagConstraints13.fill = GridBagConstraints.BOTH;
			gridBagConstraints13.gridy = 4;
			gridBagConstraints13.weightx = 1.0;
			gridBagConstraints13.gridx = 2;
			GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			gridBagConstraints9.insets = new Insets(0, 0, 0, 3);
			gridBagConstraints9.fill = GridBagConstraints.BOTH;
			gridBagConstraints9.gridy = 3;
			gridBagConstraints9.weightx = 1.0;
			gridBagConstraints9.gridx = 2;
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.gridx = 1;
			gridBagConstraints8.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints8.gridy = 3;
			changeID1Label = new JLabel();
			changeID1Label.setText("New ID for new Label: ");
			changeID1Label.setEnabled(true);
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.gridx = 1;
			gridBagConstraints6.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints6.gridy = 4;
			changeID2Label = new JLabel();
			changeID2Label.setText("New ID for old Label: ");
			changeID2Label.setEnabled(true);
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.anchor = GridBagConstraints.NORTH;
			gridBagConstraints.gridx = 0;
			gridBagConstraints.weightx = 1.0;
			gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints.gridwidth = 0;
			gridBagConstraints.ipady = 0;
			gridBagConstraints.weighty = 0.0;
			gridBagConstraints.insets = new Insets(3, 3, 16, 3);
			gridBagConstraints.gridy = 0;
			messageLabel = new JLabel();
			messageLabel.setText("<html><body>An element (label, hidden node/branch data) with the IDs you selected already exists on this branch. Select \"Overwrite\" if you want to delete the element currently present or select \"Change IDs\" to rename one or both of the conflicting IDs.</body></html>");
			messageLabel.setEnabled(true);
			inputPanel = new JPanel();
			inputPanel.setLayout(new GridBagLayout());
			inputPanel.add(messageLabel, gridBagConstraints);
			inputPanel.add(changeID2Label, gridBagConstraints6);
			inputPanel.add(changeID1Label, gridBagConstraints8);
			inputPanel.add(getNewIDTextField(), gridBagConstraints9);
			inputPanel.add(getPresentIDTextField(), gridBagConstraints13);
			inputPanel.add(getOverwriteRadioButton(), gridBagConstraints14);
			inputPanel.add(getChangeIDRadioButton(), gridBagConstraints15);
			inputPanel.add(overwriteLabel, gridBagConstraints17);
			inputPanel.add(changeIDLabel, gridBagConstraints18);
		}
		return inputPanel;
	}

	
	/**
	 * This method initializes newIDTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getNewIDTextField() {
		if (newIDTextField == null) {
			newIDTextField = new JTextField();
			newIDTextField.setEnabled(true);
		}
		return newIDTextField;
	}

	
	/**
	 * This method initializes presentIDTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getPresentIDTextField() {
		if (presentIDTextField == null) {
			presentIDTextField = new JTextField();
			presentIDTextField.setEnabled(true);
		}
		return presentIDTextField;
	}

	
	private ButtonGroup getRadioGroup() {
		if (radioGroup == null){
			radioGroup = new ButtonGroup();
			radioGroup.add(getChangeIDRadioButton());
			radioGroup.add(getOverwriteRadioButton());
		}
		return radioGroup;
	}

	
	/**
	 * This method initializes overwriteRadioButton	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getOverwriteRadioButton() {
		if (overwriteRadioButton == null) {
			overwriteRadioButton = new JRadioButton();
		}
		return overwriteRadioButton;
	}

	
	/**
	 * This method initializes changeIDRadioButton	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getChangeIDRadioButton() {
		if (changeIDRadioButton == null) {
			changeIDRadioButton = new JRadioButton();
			changeIDRadioButton.setSelected(true);
		
			changeIDRadioButton.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(java.awt.event.ItemEvent e) {
					
					getNewIDTextField().setEnabled(getChangeIDRadioButton().isSelected());
					getPresentIDTextField().setEnabled(getChangeIDRadioButton().isSelected());
					changeID1Label.setEnabled(getChangeIDRadioButton().isSelected());
					changeID2Label.setEnabled(getChangeIDRadioButton().isSelected());
				}
			});
		}
		return changeIDRadioButton;
	}
}  //  @jve:decl-index=0:visual-constraint="146,6"
