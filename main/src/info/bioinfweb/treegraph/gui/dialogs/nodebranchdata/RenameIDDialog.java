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


import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.IDManager;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.undo.edit.RenameDataIDEdit;
import info.bioinfweb.treegraph.gui.dialogs.EditDialog;
import info.webinsel.util.Math2;

import javax.swing.JPanel;
import java.awt.Frame;
import java.awt.GridBagLayout;
import javax.swing.BoxLayout;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;
import java.awt.Font;
import java.awt.Color;
import javax.swing.JTextField;
import java.awt.GridBagConstraints;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JList;



public class RenameIDDialog extends EditDialog {
	private static final long serialVersionUID = 1L;
	
	
	private String[] oldNames = null;
	private JPanel jContentPane = null;
	private JPanel labelIDPanel = null;
	private JTextField newNameTextField = null;
	private JButton renameButton = null;
	private JList idList = null;


	/**
	 * @param owner
	 */
	public RenameIDDialog(Frame owner) {
		super(owner);
		setHelpCode(47);
		initialize();
		setLocationRelativeTo(owner);
	}


	private void setNames() {
		Node root = getDocument().getTree().getPaintStart();
		if (root != null) {
			oldNames = IDManager.getIDs(root);
			DefaultListModel model = (DefaultListModel)getIdList().getModel();
			model.removeAllElements();
			if (oldNames.length > 0) {
				for (int i = 0; i < oldNames.length; i++) {
					model.addElement(oldNames[i]);
				}
				getIdList().setSelectedIndex(0);
				getIdList().ensureIndexIsVisible(0);
			}
		}
	}
	
	
	@Override
	protected boolean onExecute() {
		setNames();
		return true;
	}


	@Override
	protected boolean apply() {
		Vector<String> oldIDs = new Vector<String>();
		Vector<String> newIDs = new Vector<String>();
		for (int i = 0; i < oldNames.length; i++) {
			String newName = (String)((DefaultListModel)getIdList().getModel()).elementAt(i);
			if (!oldNames[i].equals(newName)) {
				oldIDs.add(oldNames[i]);
				newIDs.add(newName);
			}
		}
		getDocument().executeEdit(new RenameDataIDEdit(getDocument(), 
				newIDs.toArray(new String[newIDs.size()]),
				oldIDs.toArray(new String[oldIDs.size()])));
		return true;
	}
	
	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setTitle("Rename ID");
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
			jContentPane.add(getLabelIDPanel(), null);
			jContentPane.add(getButtonsPanel(), null);
		}
		return jContentPane;
	}


	/**
	 * This method initializes labelIDPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getLabelIDPanel() {
		if (labelIDPanel == null) {
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.fill = GridBagConstraints.BOTH;
			gridBagConstraints2.gridy = 1;
			gridBagConstraints2.weightx = 1.0;
			gridBagConstraints2.weighty = 1.0;
			gridBagConstraints2.gridwidth = 2;
			gridBagConstraints2.anchor = GridBagConstraints.WEST;
			gridBagConstraints2.gridx = 0;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 1;
			gridBagConstraints1.gridy = 0;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints.gridy = 0;
			gridBagConstraints.weightx = 1.0;
			gridBagConstraints.gridx = 0;
			labelIDPanel = new JPanel();
			labelIDPanel.setLayout(new GridBagLayout());
			labelIDPanel.setBorder(BorderFactory.createTitledBorder(null, "", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			labelIDPanel.add(getNewNameTextField(), gridBagConstraints);
			labelIDPanel.add(getRenameButton(), gridBagConstraints1);
			labelIDPanel.add(new JScrollPane(getIdList()), gridBagConstraints2);
		}
		return labelIDPanel;
	}


	/**
	 * This method initializes newNameTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getNewNameTextField() {
		if (newNameTextField == null) {
			newNameTextField = new JTextField();
		}
		return newNameTextField;
	}


	/**
	 * This method initializes renameButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getRenameButton() {
		if (renameButton == null) {
			renameButton = new JButton();
			renameButton.setText("Rename");
			renameButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if (getIdList().getSelectedIndex() != -1) {
						((DefaultListModel)getIdList().getModel()).set(
								getIdList().getSelectedIndex(), 
								getNewNameTextField().getText());
					}
					else {
						JOptionPane.showMessageDialog(null, "There is no ID selected.", 
								"No ID selected", JOptionPane.ERROR_MESSAGE);
					}
				}
			});
		}
		return renameButton;
	}


	/**
	 * This method initializes idList	
	 * 	
	 * @return javax.swing.JList	
	 */
	private JList getIdList() {
		if (idList == null) {
			idList = new JList(new DefaultListModel());
			idList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
				public void valueChanged(javax.swing.event.ListSelectionEvent e) {
					if (Math2.isBetween(e.getFirstIndex(), 0, getIdList().getModel().getSize() - 1)) {
						getNewNameTextField().setText(
								(String)getIdList().getModel().getElementAt(e.getFirstIndex()));
					}
				}
			});
		}
		return idList;
	}

}