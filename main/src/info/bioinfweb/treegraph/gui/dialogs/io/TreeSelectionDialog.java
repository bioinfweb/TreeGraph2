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
package info.bioinfweb.treegraph.gui.dialogs.io;


import info.bioinfweb.treegraph.Main;
import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.Tree;
import info.bioinfweb.treegraph.document.io.TreeSelector;
import info.bioinfweb.treegraph.gui.mainframe.MainFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeViewPanel;
import info.webinsel.util.StringUtils;
import info.webinsel.wikihelp.client.OkCancelApplyWikiHelpDialog;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;



public class TreeSelectionDialog extends OkCancelApplyWikiHelpDialog 
    implements TreeSelector {
	
	private static final long serialVersionUID = 1L;
	
	
	private static TreeSelectionDialog firstInstance = null;
	private Tree[] trees = null;
	private int currentTree = -1;
	
	private JPanel jContentPane = null;
	private JPanel selectionPanel = null;
	private JComboBox treeComboBox = null;
	private JPanel previewPanel = null;
	private JScrollPane previewScrollPane = null;
	private TreeViewPanel treeViewPanel = null;


	/**
	 * @param owner
	 */
	private TreeSelectionDialog(Frame owner) {
		super(owner, true, Main.getInstance().getWikiHelp());
		initialize();
		setLocationRelativeTo(owner);
		setHelpCode(51);
	}
	
	
	public static TreeSelectionDialog getInstance() {
		if (firstInstance == null) {
			firstInstance = new TreeSelectionDialog(MainFrame.getInstance());
		}
		return firstInstance;
	}


	private int getSelctedIndex() {
		return getTreeComboBox().getSelectedIndex();
	}
	
	
	public int select(String[] names, Tree[] trees) {
		if (trees.length > 1) {
			currentTree = -1;
			this.trees = trees;
			
			StringUtils.renameRepeatedEntries(names);
			DefaultComboBoxModel model = (DefaultComboBoxModel)getTreeComboBox().getModel();
			model.removeAllElements();
			for (int i = 0; i < names.length; i++) {
				model.addElement(names[i]);
			}
			
			execute();
			return getSelctedIndex();  //Always return index, even if canceled
		}
		else {
			return 0;  // Nur ein Baum vorhanden.
		}
	}


	@Override
	protected boolean apply() {
		return true;
	}


	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setTitle("Select tree");
		this.setSize(400, 400);
		this.setContentPane(getJContentPane());
		getCancelButton().setVisible(false);
		getApplyButton().setVisible(false);
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
			jContentPane.add(getSelectionPanel(), null);
			jContentPane.add(getPreviewPanel(), null);
			jContentPane.add(getButtonsPanel(), null);
		}
		return jContentPane;
	}


	/**
	 * This method initializes selectionPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getSelectionPanel() {
		if (selectionPanel == null) {
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints.gridy = 0;
			gridBagConstraints.weightx = 2.0;
			gridBagConstraints.gridx = 0;
			selectionPanel = new JPanel();
			selectionPanel.setLayout(new GridBagLayout());
			selectionPanel.setBorder(BorderFactory.createTitledBorder(null, "Trees in the document:", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			selectionPanel.add(getTreeComboBox(), gridBagConstraints);
		}
		return selectionPanel;
	}


	/**
	 * This method initializes treeComboBox	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getTreeComboBox() {
		if (treeComboBox == null) {
			treeComboBox = new JComboBox();
			treeComboBox.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(java.awt.event.ItemEvent e) {
					if (currentTree != getSelctedIndex()) {
						currentTree = getSelctedIndex();
						getTreeViewPanel().getDocument().setTree(trees[getSelctedIndex()]);
					}
				}
			});
		}
		return treeComboBox;
	}


	/**
	 * This method initializes previewPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getPreviewPanel() {
		if (previewPanel == null) {
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.fill = GridBagConstraints.BOTH;
			gridBagConstraints1.weighty = 1.0;
			gridBagConstraints1.weightx = 1.0;
			previewPanel = new JPanel();
			previewPanel.setLayout(new GridBagLayout());
			previewPanel.setBorder(BorderFactory.createTitledBorder(null, "Preview:", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			previewPanel.add(getPreviewScrollPane(), gridBagConstraints1);
		}
		return previewPanel;
	}


	/**
	 * This method initializes previewScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getPreviewScrollPane() {
		if (previewScrollPane == null) {
			previewScrollPane = new JScrollPane();
			previewScrollPane.setViewportView(getTreeViewPanel());
		}
		return previewScrollPane;
	}


	/**
	 * This method initializes treeViewPanel	
	 * 	
	 * @return info.webinsel.treegraph.gui.TreeViewPanel	
	 */
	private TreeViewPanel getTreeViewPanel() {
		if (treeViewPanel == null) {
			treeViewPanel = new TreeViewPanel(new Document());
		}
		return treeViewPanel;
	}

}