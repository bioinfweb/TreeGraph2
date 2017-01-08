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
package info.bioinfweb.treegraph.gui.dialogs.nodebranchdata.calculatecolumn;


import info.bioinfweb.treegraph.Main;
import info.bioinfweb.treegraph.document.undo.edit.calculatecolumn.ErrorInfo;
import info.bioinfweb.treegraph.gui.mainframe.MainFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeViewPanel;
import info.bioinfweb.wikihelp.client.OkCancelApplyWikiHelpDialog;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.JLabel;



public class CalculateColumnErrorsDialog extends OkCancelApplyWikiHelpDialog {
	private JPanel jContentPane = null;
	private JPanel messagesPanel = null;
	private JTable messagesTable;
	private JLabel headingLabel;
	
	
	/**
	 * Create the dialog.
	 */
	public CalculateColumnErrorsDialog(Frame owner) {
		super(owner, true, Main.getInstance().getWikiHelp());
		setHelpCode(95);
		initialize();
	}

	
	public void execute(List<ErrorInfo> errors) {
		getTableModel().setErrors(errors);
		pack();
		setLocationRelativeTo(getOwner());
		execute();
	}
	
	
	@Override
  protected boolean apply() {
	  return true;
  }

	
	private void selectNode(String uniqueName) {
		TreeViewPanel panel = MainFrame.getInstance().getActiveTreeFrame().getTreeViewPanel();
		panel.getSelection().set(panel.getDocument().getTree().getNodeByUniqueName(uniqueName));
	}
	

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		setTitle("Calculation errors");
		setContentPane(getJContentPane());
		getCancelButton().setVisible(false);
		getApplyButton().setVisible(false);
		getOkButton().setText("Close");
		setMinimumSize(new Dimension(500, 50));
		pack();
	}
	
	
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BoxLayout(getJContentPane(), BoxLayout.Y_AXIS));
			jContentPane.add(getMessagesPanel(), null);
			jContentPane.add(getButtonsPanel(), null);
		}
		return jContentPane;
	}
	
	
	private JPanel getMessagesPanel() {
		if (messagesPanel == null) {
			messagesPanel = new JPanel();
			messagesPanel.setLayout(new BorderLayout(0, 0));
			messagesPanel.add(new JScrollPane(getMessagesTable()), BorderLayout.CENTER);
			messagesPanel.add(getHeadingLabel(), BorderLayout.NORTH);
		}
		return messagesPanel;
	}
	

	private void fixColumnWidth(TableColumn column) {
		column.setMaxWidth(column.getPreferredWidth());
	}
	
	
	private JTable getMessagesTable() {
		if (messagesTable == null) {
			messagesTable = new JTable(new ErrorsTableModel());
			messagesTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
			messagesTable.setFillsViewportHeight(true);
			fixColumnWidth(messagesTable.getColumnModel().getColumn(0));
			fixColumnWidth(messagesTable.getColumnModel().getColumn(1));
			messagesTable.addMouseListener(new MouseAdapter() {
		    public void mousePressed(MouseEvent e) {
	        int row = getMessagesTable().rowAtPoint(e.getPoint());
	        if (row > -1) {
	        	selectNode(getTableModel().getErrors().get(row).getUniqueNodeName());
	        }
	    }
	});
		}
		return messagesTable;
	}
	
	
	private ErrorsTableModel getTableModel() {
		return (ErrorsTableModel)getMessagesTable().getModel();
	}
	
	
	private JLabel getHeadingLabel() {
		if (headingLabel == null) {
			headingLabel = new JLabel("<html> The following errors occurred during the calculation:<br>"
							+ " (Select a line to see the repective node in the tree.)</html>");
		}
		return headingLabel;
	}
}
