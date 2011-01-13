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
package info.bioinfweb.treegraph.gui.dialogs.editelement;


import info.bioinfweb.treegraph.document.GraphicalLabel;
import info.bioinfweb.treegraph.document.PieChartLabel;
import info.bioinfweb.treegraph.gui.dialogs.DataIDComboBox;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;



/**
 * Dialog used to create new pie chart labels.   
 * @author Ben St&ouml;ver
 * @since 2.0.43
 * @see PieChartLabel
 */
public class NewPieChartLabelsDialog extends NewGraphicalLabelsDialog {
	private static final long serialVersionUID = 1L;

	
	private JPanel jContentPane = null;
	private JPanel valuesPanel = null;
	private DataIDComboBox listIDComboBox = null;
	private JList idList = null;
	private JButton addButton = null;
	private JButton replaceButton = null;
	private JButton removeButton = null;
	private JScrollPane idListScrollPane = null;

	
	/**
	 * @param owner
	 */
	public NewPieChartLabelsDialog(Frame owner) {
		super(owner);
		setHelpCode(55);
		initialize();
		setLocationRelativeTo(owner);
	}

	
	@Override
	protected boolean onExecute() {
		boolean result = super.onExecute();
		if (result) {
			getListIDComboBox().setIDs(getDocument());
		}
		return result;
	}


	@Override
	protected GraphicalLabel createLabel() {
		PieChartLabel label = new PieChartLabel(null);
		for (int i = 0; i < getIDListModel().size(); i++) {
			label.addValueID(getIDListModel().get(i).toString());
		}
		return label;
	}


	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		setTitle("New pie chart label(s)");
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
			jContentPane.add(getIDPanel(), null);
			jContentPane.add(getValuesPanel(), null);
			jContentPane.add(getButtonsPanel(), null);
		}
		return jContentPane;
	}


	/**
	 * This method initializes valuesPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getValuesPanel() {
		if (valuesPanel == null) {
			valuesPanel = new JPanel();
			GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
			gridBagConstraints21.fill = GridBagConstraints.BOTH;
			gridBagConstraints21.gridy = 1;
			gridBagConstraints21.weightx = 1.0;
			gridBagConstraints21.weighty = 1.0;
			gridBagConstraints21.gridheight = 3;
			gridBagConstraints21.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints21.gridx = 1;
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.gridx = 2;
			gridBagConstraints4.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints4.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints4.gridy = 2;
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 2;
			gridBagConstraints3.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints3.gridy = 1;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 2;
			gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints2.gridy = 0;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints.gridy = 0;
			gridBagConstraints.weightx = 1.0;
			gridBagConstraints.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints.gridx = 1;
			valuesPanel.setLayout(new GridBagLayout());
			valuesPanel.setBorder(BorderFactory.createTitledBorder(null, "Pie chart value IDs", 
					TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, 
					new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			valuesPanel.add(getListIDComboBox(), gridBagConstraints);
			valuesPanel.add(getAddButton(), gridBagConstraints2);
			valuesPanel.add(getReplaceButton(), gridBagConstraints3);
			valuesPanel.add(getRemoveButton(), gridBagConstraints4);
			valuesPanel.add(getIdListScrollPane(), gridBagConstraints21);
		}
		return valuesPanel;
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
								getReplaceButton().setEnabled(!getIDListModel().isEmpty());
								getRemoveButton().setEnabled(!getIDListModel().isEmpty());
							}
						}
					});
		}
		return idList;
	}
	
	
	protected DefaultListModel getIDListModel() {
		return (DefaultListModel)getIDList().getModel();
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
					getIDListModel().addElement(getListIDComboBox().getSelectedItem());
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
					getIDListModel().set(getIDList().getSelectedIndex(), getListIDComboBox().getSelectedItem());
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
					getIDListModel().remove(getIDList().getSelectedIndex());
				}
			});
		}
		return removeButton;
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