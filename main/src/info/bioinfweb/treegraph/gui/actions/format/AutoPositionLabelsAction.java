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
package info.bioinfweb.treegraph.gui.actions.format;


import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.Action;
import javax.swing.JOptionPane;

import info.bioinfweb.treegraph.document.Branch;
import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.Label;
import info.bioinfweb.treegraph.document.NodeType;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.tools.IDManager;
import info.bioinfweb.treegraph.document.tools.TreeSerializer;
import info.bioinfweb.treegraph.document.undo.file.AddSupportValuesEdit;
import info.bioinfweb.treegraph.document.undo.format.AutoPositionLabelsEdit;
import info.bioinfweb.treegraph.gui.actions.DocumentAction;
import info.bioinfweb.treegraph.gui.mainframe.MainFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeInternalFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeSelection;



public class AutoPositionLabelsAction extends DocumentAction {
	public AutoPositionLabelsAction(MainFrame mainFrame) {
		super(mainFrame);
		putValue(Action.NAME, "Automatically position labels"); 
	  putValue(Action.MNEMONIC_KEY, KeyEvent.VK_A);
  }
	
	
	@Override
	public void setEnabled(Document document, TreeSelection selection, NodeBranchDataAdapter tableAdapter) {
		setEnabled((document != null) && !document.getTree().isEmpty() && (selection != null) &&
				(selection.isEmpty() || selection.containsOnlyType(Branch.class)));
	}


	private boolean containsSupportConflictIDs(Document document) {
  	List<String> ids = new ArrayList<String>(Arrays.asList(IDManager.getLabelIDs(
  			document.getTree().getPaintStart(), Label.class)));
		for (String id : ids) {
			if (id.endsWith(AddSupportValuesEdit.SUPPORT_NAME) && 
					ids.contains(id.replace(AddSupportValuesEdit.SUPPORT_NAME, AddSupportValuesEdit.CONFLICT_NAME))) {
				
				return true;  // Pair found
			}
		}
		return false;
	}
	
	
	private boolean getEqualSupportConflictPosition(Document document) {
		if (containsSupportConflictIDs(document)) {
			return JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(getMainFrame(), 
					"This document contains pairs of label IDs which seem to be comming from the add support values feature\n" +
					"of TreeGraph (e.g. \"XXX" +	AddSupportValuesEdit.SUPPORT_NAME + "\" and \"XXX" + 
					AddSupportValuesEdit.CONFLICT_NAME + "\").\n\n" +
					"Do you want both label columns to have the same position?\n\n" + 
					"(\"Yes\" is recommended because usually both types of labels won't occur on the same branch. Select \"No\" if\n" + 
					"these labels have a different source and you expect both support and conflict labels to occur on the same branch.)", 
					"Treatment of support and conflict labels", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) ;
		}
		else {
			return false;
		}
		
		
	}
	
	
	@Override
	protected void onActionPerformed(ActionEvent e, TreeInternalFrame frame) {
		TreeSelection selection = frame.getTreeViewPanel().getSelection();
		Branch[] branches;
		if (selection.isEmpty()) {
			branches = TreeSerializer.getElementsInSubtree(frame.getDocument().getTree().getPaintStart(), NodeType.BOTH, Branch.class);
		}
		else {
			branches = selection.toArray(new Branch[selection.size()]); 
		}
		frame.getDocument().executeEdit(new AutoPositionLabelsEdit(frame.getDocument(), branches, 
				getEqualSupportConflictPosition(frame.getDocument())));
	}
}