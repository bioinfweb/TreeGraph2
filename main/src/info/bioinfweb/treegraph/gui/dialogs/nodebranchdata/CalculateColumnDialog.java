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
package info.bioinfweb.treegraph.gui.dialogs.nodebranchdata;


import info.bioinfweb.treegraph.document.nodebranchdata.NewHiddenBranchDataAdapter;
import info.bioinfweb.treegraph.document.undo.edit.CalculateColumnEdit;
import info.bioinfweb.treegraph.gui.dialogs.EditDialog;
import info.bioinfweb.treegraph.gui.dialogs.nodebranchdatainput.NewNodeBranchDataInput;

import javax.swing.JPanel;

import java.awt.Dimension;
import java.awt.Frame;

import javax.swing.BoxLayout;
import javax.swing.JOptionPane;

import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;

import java.awt.Font;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.io.IOException;

import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.ListSelectionModel;



/**
 * Used to calculate a node/branch data column as specified by the user defined 
 * expression.
 * @author Ben St&ouml;ver
 * @since 2.0.24
 */
public class CalculateColumnDialog extends EditDialog {
	public static final int MIN_EXPRESSION_FIELD_WIDTH = 300;
	
	private static final long serialVersionUID = 1L;

	
	private JPanel jContentPane = null;
	private JPanel expressionPanel = null;
	private JTextField expressionTextField = null;
	private JPanel recentlyUsedPanel = null;
	private JScrollPane recentlyUsedScrollPane = null;
	private JList<String> recentlyUsedList = null;
	private JPanel columnPanel = null;
	private NewNodeBranchDataInput columnInput = null;

	
	/**
	 * @param owner
	 */
	public CalculateColumnDialog(Frame owner) {
		super(owner);
		initialize();
	}

	
	@Override
	protected boolean onExecute() {
		getColumnInput().setAdapters(getDocument().getTree(), false, true, true, false, true, "");
		getColumnInput().setSelectedAdapter(NewHiddenBranchDataAdapter.class);
		if (getSelectedAdapter() != null) {
			getColumnInput().setSelectedAdapter(getSelectedAdapter());  // If an readOnly adapter is selected, nothing will change.
		}
		pack();
		return true;
	}


	@Override
	protected boolean apply() {
		CalculateColumnEdit edit = new CalculateColumnEdit(getDocument(), 
				getColumnInput().getSelectedAdapter(),	getExpressionTextField().getText());
		boolean result = edit.evaluate();
		if (result) {
			getDocument().executeEdit(edit);
			getRecentlyUsedListModel().addExpression(getExpressionTextField().getText());
			try {
				getRecentlyUsedListModel().saveList();
			}
			catch (IOException e) {
				JOptionPane.showMessageDialog(this, "The error \"" + e.getLocalizedMessage() + "\" occured while " +
						"trying to write the configuration file.",	"IO Error", JOptionPane.ERROR_MESSAGE);
			}
		}
		else {
			JOptionPane.showMessageDialog(this, edit.getErrors(), "Erroneous expression", 
					JOptionPane.ERROR_MESSAGE);
		}
		return result;
	}


	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		setHelpCode(77);
		setContentPane(getJContentPane());
		setTitle("Calculate node/branch data");
		setMinimumSize(new Dimension(500, 50));
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
			jContentPane.add(getColumnPanel(), null);
			jContentPane.add(getExpressionPanel(), null);
			jContentPane.add(getRecentlyUsedPanel(), null);
			jContentPane.add(getButtonsPanel(), null);
		}
		return jContentPane;
	}


	/**
	 * This method initializes expressionPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getExpressionPanel() {
		if (expressionPanel == null) {
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints3.gridy = 0;
			gridBagConstraints3.weightx = 10.0;
			gridBagConstraints3.gridx = 0;
			expressionPanel = new JPanel();
			expressionPanel.setLayout(new GridBagLayout());
			expressionPanel.setBorder(BorderFactory.createTitledBorder(null, "Expression", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			expressionPanel.add(getExpressionTextField(), gridBagConstraints3);
		}
		return expressionPanel;
	}


	/**
	 * This method initializes expressionTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getExpressionTextField() {
		if (expressionTextField == null) {
			expressionTextField = new JTextField();
			expressionTextField.setMinimumSize(
					new Dimension(MIN_EXPRESSION_FIELD_WIDTH, 20));
		}
		return expressionTextField;
	}


	/**
	 * This method initializes recentlyUsedPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getRecentlyUsedPanel() {
		if (recentlyUsedPanel == null) {
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.fill = GridBagConstraints.BOTH;
			gridBagConstraints5.gridy = 0;
			gridBagConstraints5.weightx = 1.0;
			gridBagConstraints5.weighty = 1.0;
			gridBagConstraints5.gridx = 0;
			recentlyUsedPanel = new JPanel();
			recentlyUsedPanel.setLayout(new GridBagLayout());
			recentlyUsedPanel.setBorder(BorderFactory.createTitledBorder(null, "Recently used expressions", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			recentlyUsedPanel.add(getRecentlyUsedScrollPane(), gridBagConstraints5);
		}
		return recentlyUsedPanel;
	}


	/**
	 * This method initializes recentlyUsedScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getRecentlyUsedScrollPane() {
		if (recentlyUsedScrollPane == null) {
			recentlyUsedScrollPane = new JScrollPane();
			recentlyUsedScrollPane.setViewportView(getRecentlyUsedList());
		}
		return recentlyUsedScrollPane;
	}


	/**
	 * This method initializes recentlyUsedList	
	 * 	
	 * @return javax.swing.JList	
	 */
	private JList<String> getRecentlyUsedList() {
		if (recentlyUsedList == null) {
			RecentlyUsedExpressionsListModel model = new RecentlyUsedExpressionsListModel();
			try {
				model.loadList();
			}
			catch (IOException e) {
				JOptionPane.showMessageDialog(this, "An error occured while trying to read a " +
						"configuration file.",	"IO Error", JOptionPane.ERROR_MESSAGE);
			}
			
			recentlyUsedList = new JList<String>(model);
			recentlyUsedList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			recentlyUsedList.addListSelectionListener(
					new javax.swing.event.ListSelectionListener() {
						public void valueChanged(javax.swing.event.ListSelectionEvent e) {
							if (!e.getValueIsAdjusting() && recentlyUsedList.getSelectedIndex() != -1) {
								getExpressionTextField().setText(
										getRecentlyUsedListModel().getElementAt(recentlyUsedList.getSelectedIndex()));
							}
						}
					});
		}
		return recentlyUsedList;
	}
	
	
	private RecentlyUsedExpressionsListModel getRecentlyUsedListModel() {
		return (RecentlyUsedExpressionsListModel)getRecentlyUsedList().getModel();
	}


	/**
	 * This method initializes columnPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getColumnPanel() {
		if (columnPanel == null) {
			columnPanel = new JPanel();
			columnPanel.setLayout(new GridBagLayout());
			columnInput = new NewNodeBranchDataInput(columnPanel, 0, 0, true);
		}
		return columnPanel;
	}
	
	
	private NewNodeBranchDataInput getColumnInput() {
		getColumnPanel();
		return columnInput;
	}
}