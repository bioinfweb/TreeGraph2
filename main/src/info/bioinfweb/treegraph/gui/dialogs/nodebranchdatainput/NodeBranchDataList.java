/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2013  Ben Stöver, Kai Müller
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
package info.bioinfweb.treegraph.gui.dialogs.nodebranchdatainput;


import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.gui.dialogs.DataIDComboBox;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;



/**
 * Provides an user editable list with node/branch data column IDs for the TreeGraph GUI.
 *  
 * @author Ben St&ouml;ver
 * @since 2.0.48
 */
public class NodeBranchDataList extends JPanel {
	private DataIDComboBox listIDComboBox = null;
	private JList idList = null;
	private JButton addButton = null;
	private JButton replaceButton = null;
	private JButton removeButton = null;
	private JScrollPane idListScrollPane = null;
	private JButton clearButton;

	
	public NodeBranchDataList() {
	  super();
	  initialize();
  }
	
	
	public void setIDs(Document document) {
	  listIDComboBox.setIDs(document);
  }
	
	
	public DefaultListModel getListModel() {
		return (DefaultListModel)getIDList().getModel();
	}
	
	
	public void setListModel(DefaultListModel model) {
		getIDList().setModel(model);
	}
	                                           

	private void initialize() {
		GridBagConstraints listScrollPabeGBC = new GridBagConstraints();
		listScrollPabeGBC.fill = GridBagConstraints.BOTH;
		listScrollPabeGBC.gridy = 1;
		listScrollPabeGBC.weightx = 1.0;
		listScrollPabeGBC.weighty = 1.0;
		listScrollPabeGBC.gridheight = 3;
		listScrollPabeGBC.insets = new Insets(2, 2, 2, 5);
		listScrollPabeGBC.gridx = 1;
		GridBagConstraints removeButtonGBC = new GridBagConstraints();
		removeButtonGBC.gridx = 2;
		removeButtonGBC.insets = new Insets(2, 2, 5, 2);
		removeButtonGBC.fill = GridBagConstraints.HORIZONTAL;
		removeButtonGBC.gridy = 2;
		GridBagConstraints replaceButtonGBC = new GridBagConstraints();
		replaceButtonGBC.gridx = 2;
		replaceButtonGBC.insets = new Insets(2, 2, 5, 2);
		replaceButtonGBC.fill = GridBagConstraints.HORIZONTAL;
		replaceButtonGBC.gridy = 1;
		GridBagConstraints addButtonGBC = new GridBagConstraints();
		addButtonGBC.gridx = 2;
		addButtonGBC.insets = new Insets(2, 2, 5, 2);
		addButtonGBC.fill = GridBagConstraints.HORIZONTAL;
		addButtonGBC.gridy = 0;
		GridBagConstraints listIDComboBoxGBC = new GridBagConstraints();
		listIDComboBoxGBC.fill = GridBagConstraints.HORIZONTAL;
		listIDComboBoxGBC.gridy = 0;
		listIDComboBoxGBC.weightx = 1.0;
		listIDComboBoxGBC.insets = new Insets(2, 2, 5, 5);
		listIDComboBoxGBC.gridx = 1;
		setLayout(new GridBagLayout());
		add(getListIDComboBox(), listIDComboBoxGBC);
		add(getAddButton(), addButtonGBC);
		add(getReplaceButton(), replaceButtonGBC);
		add(getRemoveButton(), removeButtonGBC);
		add(getIdListScrollPane(), listScrollPabeGBC);
		GridBagConstraints gbc_clearButton = new GridBagConstraints();
		gbc_clearButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_clearButton.anchor = GridBagConstraints.NORTH;
		gbc_clearButton.gridx = 2;
		gbc_clearButton.gridy = 3;
		add(getClearButton(), gbc_clearButton);
	}


	/**
	 * This method initializes idComboBox	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	protected DataIDComboBox getListIDComboBox() {
		if (listIDComboBox == null) {
			listIDComboBox = new DataIDComboBox(false);
		}
		return listIDComboBox;
	}

	
	/**
	 * This method initializes idList	
	 * 	
	 * @return javax.swing.JList	
	 */
	protected JList getIDList() {
		if (idList == null) {
			idList = new JList(new DefaultListModel());
			idList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
						@Override
						public void valueChanged(ListSelectionEvent e) {
							if (!e.getValueIsAdjusting()) {
								if (getIDList().getSelectedValue() != null) {
									getListIDComboBox().setSelectedItem(getIDList().getSelectedValue());
								}
								getReplaceButton().setEnabled(!getListModel().isEmpty());
								getRemoveButton().setEnabled(!getListModel().isEmpty());
							}
						}
					});
		}
		return idList;
	}
	
	
	/**
	 * This method initializes addButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getAddButton() {
		if (addButton == null) {
			addButton = new JButton();
			addButton.setText("Add");
			addButton.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							getListModel().addElement(getListIDComboBox().getSelectedItem());
						}
					});
		}
		return addButton;
	}


	/**
	 * This method initializes replaceButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getReplaceButton() {
		if (replaceButton == null) {
			replaceButton = new JButton();
			replaceButton.setText("Replace");
			replaceButton.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							getListModel().set(getIDList().getSelectedIndex(), getListIDComboBox().getSelectedItem());
						}
					});
		}
		return replaceButton;
	}


	/**
	 * This method initializes removeButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getRemoveButton() {
		if (removeButton == null) {
			removeButton = new JButton();
			removeButton.setText("Remove");
			removeButton.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							getListModel().remove(getIDList().getSelectedIndex());
						}
					});
		}
		return removeButton;
	}
	
	
	private JButton getClearButton() {
		if (clearButton == null) {
			clearButton = new JButton("Clear");
			clearButton.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							getListModel().clear();
						}
					});
		}
		return clearButton;
	}


	/**
	 * This method initializes idListScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getIdListScrollPane() {
		if (idListScrollPane == null) {
			idListScrollPane = new JScrollPane();
			idListScrollPane.setViewportView(getIDList());
		}
		return idListScrollPane;
	}
}
