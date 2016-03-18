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
package info.bioinfweb.treegraph.gui.dialogs.io;


import java.awt.Frame;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import info.bioinfweb.treegraph.document.Tree;
import info.bioinfweb.treegraph.document.nodebranchdata.IDElementAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.VoidNodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.tools.IDManager;
import info.bioinfweb.treegraph.gui.actions.DocumentAction;
import info.bioinfweb.wikihelp.client.OkCancelApplyWikiHelpDialog;
import info.bioinfweb.wikihelp.client.WikiHelp;



public abstract class AssignImportColumnsDialog extends OkCancelApplyWikiHelpDialog {
	protected JPanel importPanel = null;
	protected JScrollPane scrollPane;
	protected Tree tree = null;

	
	public AssignImportColumnsDialog(Frame owner, boolean modal, WikiHelp wikiHelp) {
		super(owner, modal, wikiHelp);
	}
	
	
	protected boolean checkSelectedAdapters(NodeBranchDataAdapter[] adapterArray) {
		StringBuffer message = new StringBuffer();
		List<String> duplicateColumns = new ArrayList<String>();	//TODO probably use DocumentAction.createElementList() here?
		
		message.append("The following node/branch data columns were selected more than once:\n\n");
		Set<String> adapterTypes = new HashSet<String>();
		
		boolean cancel = false;
		boolean idExists = false;
		
		for (int i = 0; i < adapterArray.length; i++) {
			NodeBranchDataAdapter adapter = adapterArray[i];
			if (!(adapter instanceof VoidNodeBranchDataAdapter)) {	
				if (adapter instanceof IDElementAdapter) {
					String name = "Node/branch data columns with the ID \"" + ((IDElementAdapter)adapter).getID() + "\"";
					if (!adapterTypes.add(name)) {
						idExists = true;
						duplicateColumns.add(name);
					}
				}
				else if (!adapterTypes.add(adapter.toString())) {
					idExists = true;
					duplicateColumns.add(adapter.toString());
				}
			}
			cancel = cancel || idExists;
		}
		message.append(DocumentAction.createElementList(duplicateColumns, false));
		message.append("\nPlease select different node/branch data columns.");		
		if (cancel) {
			JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
		}
		else {
			message.delete(0, message.length());
			duplicateColumns.clear();
			
			message.append("The following node/branch data columns already exist in the tree:\n\n");
			for (int i = 0; i < adapterArray.length; i++) {
				NodeBranchDataAdapter adapter = adapterArray[i];
				boolean columnExists = false;
				if (!(adapter instanceof VoidNodeBranchDataAdapter)) {	
					if (adapter instanceof IDElementAdapter) {
						columnExists = IDManager.idExistsInSubtree(tree.getPaintStart(), ((IDElementAdapter)adapter).getID());
						if (columnExists) {
							duplicateColumns.add("Node/branch data columns with the ID \"" + ((IDElementAdapter)adapter).getID() + "\"");
						}						
					}
					else {
						columnExists = true;
						duplicateColumns.add(adapter.toString());
					}
				}
				cancel = cancel || columnExists;
			}
			message.append(DocumentAction.createElementList(duplicateColumns, false));
			
			message.append("\n\nDo you want to possibly overwrite entries in these columns?\n");
			message.append("(Only data of those nodes that are referenced in the imported file will be affected.)");
			
			if (cancel) {
				cancel = (JOptionPane.showConfirmDialog(this, message, "Warning", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION);
			}
		}
		return !cancel;
	}
	

	/**
	 * This method initializes importPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	protected JPanel getImportPanel() {
		if (importPanel == null) {
			importPanel = new JPanel();
			importPanel.setLayout(new GridBagLayout());
		}
		return importPanel;
	}
	
	
	protected JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			scrollPane.setViewportView(getImportPanel());
		}
		return scrollPane;
	}
}
