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
package info.bioinfweb.treegraph.gui.actions.format;


import java.awt.event.ActionEvent;
import javax.swing.Action;

import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.undo.format.ShowRootedEdit;
import info.bioinfweb.treegraph.gui.actions.DocumentAction;
import info.bioinfweb.treegraph.gui.mainframe.MainFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeInternalFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeSelection;



public class ShowRootedAction extends DocumentAction {
	public ShowRootedAction(MainFrame mainFrame) {
		super(mainFrame);
		putValue(Action.NAME, "Show (un)rooted"); 
		putValue(Action.SHORT_DESCRIPTION, "Show (un)rooted"); 
	  loadSymbols("ShowRooted");
	}

	
	@Override
	protected void onActionPerformed(ActionEvent e, TreeInternalFrame frame) {
		frame.getDocument().executeEdit(ShowRootedEdit.createInstance(frame.getDocument(), 
				!frame.getDocument().getTree().getFormats().getShowRooted()));
	}

	
	@Override
	public void setEnabled(Document document, TreeSelection selection, NodeBranchDataAdapter tableAdapter) {
		setEnabled((document != null) && !document.getTree().isEmpty());
	}
}