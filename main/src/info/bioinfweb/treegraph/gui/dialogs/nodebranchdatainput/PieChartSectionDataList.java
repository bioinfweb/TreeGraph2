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
package info.bioinfweb.treegraph.gui.dialogs.nodebranchdatainput;


import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.PieChartLabel;
import info.bioinfweb.treegraph.document.PieChartLabel.SectionData;
import info.bioinfweb.treegraph.gui.dialogs.DataIDComboBox;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;



/**
 * Provides an user editable list with node/branch data column IDs for the TreeGraph GUI.
 *  
 * @author Ben St&ouml;ver
 * @since 2.0.48
 */
public class PieChartSectionDataList extends JPanel {
	private DataIDComboBox listIDComboBox = null;
	private JList<PieChartLabel.SectionData> idList = null;
	private JButton addButton = null;
	private JButton replaceButton = null;
	private JButton removeButton = null;
	private JButton clearButton;
	private JButton moveDownButton;
	private JButton moveUpButton;
	private JScrollPane idListScrollPane = null;
	private JTextField captionTextField;
	private JPanel buttonsPanel;
	private JLabel lblDataSource;
	private JLabel lblCaption;

	
	public PieChartSectionDataList() {
	  super();
	  initialize();
	  refreshGUIStatus();
  }
	
	
	public void setIDs(Document document) {
	  listIDComboBox.setIDs(document);
	  refreshGUIStatus();
  }
	
	
	public boolean isEnabled() {
		return getIDList().isEnabled();  // A button can't be used here, because setButtonStatus() would not work anymore then.
  }


	public void setEnabled(boolean flag) {
	  getListIDComboBox().setEnabled(flag);
	  getCaptionTextField().setEditable(flag);
	  getIDList().setEnabled(flag);
	  refreshGUIStatus();
  }
	
	
	public void refreshGUIStatus() {
		// Set inputs:
		PieChartLabel.SectionData data = getIDList().getSelectedValue();
		if (data != null) {
			getListIDComboBox().setSelectedItem(data.getValueColumnID());
			getCaptionTextField().setText(data.getCaption());
		}
		
		// Set button status:
		if (isEnabled()) {
			boolean notEmpty = !getListModel().isEmpty();
			getRemoveButton().setEnabled(notEmpty);
			getClearButton().setEnabled(notEmpty);
			getReplaceButton().setEnabled(notEmpty);
		  getClearButton().setEnabled(notEmpty);
		  getMoveUpButton().setEnabled(getIDList().getSelectedIndex() > 0);
		  getMoveDownButton().setEnabled(getIDList().getSelectedIndex() < getListModel().size() - 1);
		}
		else {
		  getAddButton().setEnabled(false);
		  getRemoveButton().setEnabled(false);
		  getReplaceButton().setEnabled(false);
		  getClearButton().setEnabled(false);
		  getMoveUpButton().setEnabled(false);
		  getMoveDownButton().setEnabled(false);
		}
	}


	private DefaultListModel<PieChartLabel.SectionData> getListModel() {
		return (DefaultListModel<PieChartLabel.SectionData>)getIDList().getModel();
	}
	
	
	public ListSelectionModel getSelectionModel() {
	  return idList.getSelectionModel();
  }
	
	
	/**
	 * Returns <code>true</code> if the combo box of this component contains IDs that can be added to the list.
	 * (Note that you might have to call {@link #setIDs(Document)}) before calling this method.)
	 */
	public boolean containsIDsToAdd() {
		return getListIDComboBox().getModel().getSize() > 0;
	}


	private void initialize() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.rowWeights = new double[]{1.0, 0.0, 0.0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 1.0};
		setLayout(gridBagLayout);
		GridBagConstraints gbc_lblDataSource = new GridBagConstraints();
		gbc_lblDataSource.insets = new Insets(0, 0, 5, 5);
		gbc_lblDataSource.anchor = GridBagConstraints.WEST;
		gbc_lblDataSource.gridx = 0;
		gbc_lblDataSource.gridy = 0;
		add(getLblDataSource(), gbc_lblDataSource);
		GridBagConstraints listIDComboBoxGBC = new GridBagConstraints();
		listIDComboBoxGBC.fill = GridBagConstraints.HORIZONTAL;
		listIDComboBoxGBC.gridy = 0;
		listIDComboBoxGBC.weightx = 1.0;
		listIDComboBoxGBC.insets = new Insets(2, 2, 5, 5);
		listIDComboBoxGBC.gridx = 1;
		add(getListIDComboBox(), listIDComboBoxGBC);
		GridBagConstraints gbc_buttonsPanel = new GridBagConstraints();
		gbc_buttonsPanel.anchor = GridBagConstraints.NORTH;
		gbc_buttonsPanel.gridheight = 3;
		gbc_buttonsPanel.insets = new Insets(0, 0, 5, 5);
		gbc_buttonsPanel.gridx = 2;
		gbc_buttonsPanel.gridy = 0;
		add(getButtonsPanel(), gbc_buttonsPanel);
		GridBagConstraints gbc_lblCaption = new GridBagConstraints();
		gbc_lblCaption.insets = new Insets(0, 0, 5, 5);
		gbc_lblCaption.anchor = GridBagConstraints.WEST;
		gbc_lblCaption.gridx = 0;
		gbc_lblCaption.gridy = 1;
		add(getLblCaption(), gbc_lblCaption);
		GridBagConstraints gbc_captionTextField = new GridBagConstraints();
		gbc_captionTextField.insets = new Insets(0, 0, 5, 5);
		gbc_captionTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_captionTextField.gridx = 1;
		gbc_captionTextField.gridy = 1;
		add(getCaptionTextField(), gbc_captionTextField);
		GridBagConstraints listScrollPaneGBC = new GridBagConstraints();
		listScrollPaneGBC.gridwidth = 2;
		listScrollPaneGBC.fill = GridBagConstraints.BOTH;
		listScrollPaneGBC.gridy = 2;
		listScrollPaneGBC.weightx = 1.0;
		listScrollPaneGBC.weighty = 1.0;
		listScrollPaneGBC.insets = new Insets(2, 2, 0, 5);
		listScrollPaneGBC.gridx = 0;
		add(getIdListScrollPane(), listScrollPaneGBC);
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
	protected JList<PieChartLabel.SectionData> getIDList() {
		if (idList == null) {
			idList = new JList<PieChartLabel.SectionData>(new DefaultListModel<PieChartLabel.SectionData>());
			idList.addListSelectionListener(new ListSelectionListener() {
						public void valueChanged(ListSelectionEvent e) {
						  refreshGUIStatus();
						}
					});
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
	
	
	private PieChartLabel.SectionData getSelectedSectionData() {
		return new PieChartLabel.SectionData(getListIDComboBox().getSelectedItem(), getCaptionTextField().getText());
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
							getListModel().addElement(getSelectedSectionData());
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
							getListModel().set(getIDList().getSelectedIndex(), getSelectedSectionData());
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


	private JButton getMoveDownButton() {
		if (moveDownButton == null) {
			moveDownButton = new JButton("Move down");
			moveDownButton.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							int index = getIDList().getSelectedIndex();
							PieChartLabel.SectionData data = getListModel().remove(index);
							getListModel().add(index + 1, data);
							getIDList().setSelectedIndex(index + 1);
						}
					});
		}
		return moveDownButton;
	}
	
	
	private JButton getMoveUpButton() {
		if (moveUpButton == null) {
			moveUpButton = new JButton("Move up");
			moveUpButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					int index = getIDList().getSelectedIndex();
					PieChartLabel.SectionData data = getListModel().remove(index);
					getListModel().add(index - 1, data);
					getIDList().setSelectedIndex(index - 1);
				}
			});
		}
		return moveUpButton;
	}

	
	/**
	 * This method initializes idListScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getIdListScrollPane() {
		if (idListScrollPane == null) {
			idListScrollPane = new JScrollPane(getIDList());
		}
		return idListScrollPane;
	}
	
	
	public List<PieChartLabel.SectionData> getSectionDataList() {
		List<PieChartLabel.SectionData> result = new ArrayList<PieChartLabel.SectionData>();
		for (int i = 0; i < getListModel().size(); i++) {
			result.add(getListModel().get(i)); 
		}
		return result;
	}
	
	
	public void setSectionDataList(List<PieChartLabel.SectionData> list) {
		getListModel().clear();
		for (SectionData sectionData : list) {
			getListModel().addElement(sectionData);
		}
		
		if (!getListModel().isEmpty()) {
			getSelectionModel().setSelectionInterval(0, 0);
		}
	}
	
	
	private JTextField getCaptionTextField() {
		if (captionTextField == null) {
			captionTextField = new JTextField();
			captionTextField.setColumns(10);
		}
		return captionTextField;
	}
	
	
	private JPanel getButtonsPanel() {
		if (buttonsPanel == null) {
			buttonsPanel = new JPanel();
			buttonsPanel.setLayout(new GridBagLayout());
			GridBagConstraints gbc_addButton = new GridBagConstraints();
			gbc_addButton.fill = GridBagConstraints.HORIZONTAL;
			gbc_addButton.insets = new Insets(0, 0, 3, 0);
			gbc_addButton.gridx = 0;
			gbc_addButton.gridy = 0;
			buttonsPanel.add(getAddButton(), gbc_addButton);
			GridBagConstraints gbc_replaceButton = new GridBagConstraints();
			gbc_replaceButton.fill = GridBagConstraints.HORIZONTAL;
			gbc_replaceButton.insets = new Insets(0, 0, 3, 0);
			gbc_replaceButton.gridx = 0;
			gbc_replaceButton.gridy = 1;
			buttonsPanel.add(getReplaceButton(), gbc_replaceButton);
			GridBagConstraints gbc_removeButton = new GridBagConstraints();
			gbc_removeButton.fill = GridBagConstraints.HORIZONTAL;
			gbc_removeButton.insets = new Insets(0, 0, 3, 0);
			gbc_removeButton.gridx = 0;
			gbc_removeButton.gridy = 2;
			buttonsPanel.add(getRemoveButton(), gbc_removeButton);
			GridBagConstraints gbc_clearButton = new GridBagConstraints();
			gbc_clearButton.fill = GridBagConstraints.HORIZONTAL;
			gbc_clearButton.insets = new Insets(0, 0, 3, 0);
			gbc_clearButton.gridx = 0;
			gbc_clearButton.gridy = 3;
			buttonsPanel.add(getClearButton(), gbc_clearButton);
			GridBagConstraints gbc_moveUpButton = new GridBagConstraints();
			gbc_moveUpButton.fill = GridBagConstraints.HORIZONTAL;
			gbc_moveUpButton.insets = new Insets(0, 0, 3, 0);
			gbc_moveUpButton.gridx = 0;
			gbc_moveUpButton.gridy = 4;
			buttonsPanel.add(getMoveUpButton(), gbc_moveUpButton);
			GridBagConstraints gbc_moveDownButton = new GridBagConstraints();
			gbc_moveDownButton.fill = GridBagConstraints.HORIZONTAL;
			gbc_moveDownButton.gridx = 0;
			gbc_moveDownButton.gridy = 5;
			buttonsPanel.add(getMoveDownButton(), gbc_moveDownButton);
		}
		return buttonsPanel;
	}
	
	
	private JLabel getLblDataSource() {
		if (lblDataSource == null) {
			lblDataSource = new JLabel("Data source: ");
		}
		return lblDataSource;
	}
	
	
	private JLabel getLblCaption() {
		if (lblCaption == null) {
			lblCaption = new JLabel("Caption: ");
		}
		return lblCaption;
	}
}
