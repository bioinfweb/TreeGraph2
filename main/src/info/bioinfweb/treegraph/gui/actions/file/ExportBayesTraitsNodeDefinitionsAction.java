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
package info.bioinfweb.treegraph.gui.actions.file;


import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.gui.actions.EditDialogAction;
import info.bioinfweb.treegraph.gui.dialogs.EditDialog;
import info.bioinfweb.treegraph.gui.dialogs.io.ExportBayesTraitsNodeDefinitionsDialog;
import info.bioinfweb.treegraph.gui.mainframe.MainFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeSelection;

import java.awt.event.KeyEvent;

import javax.swing.Action;



/**
 * Allows to export BayesTraits definitions for internal nodes.
 * 
 * @author Ben St&ouml;ver
 * @since 2.0.46
 */
public class ExportBayesTraitsNodeDefinitionsAction extends EditDialogAction {
	public ExportBayesTraitsNodeDefinitionsAction(MainFrame mainFrame) {
		super(mainFrame);
		putValue(Action.NAME, "Export BayesTraits node definitions..."); 
	  putValue(Action.MNEMONIC_KEY, KeyEvent.VK_B);
		putValue(Action.SHORT_DESCRIPTION, "Export BayesTraits node definitions"); 
	}

	
	@Override
	public EditDialog createDialog() {
		return new ExportBayesTraitsNodeDefinitionsDialog(getMainFrame());
	}


	/**
	 * Tests if at least one internal node in present in the document.
	 * @see info.bioinfweb.treegraph.gui.actions.DocumentAction#setEnabled(info.bioinfweb.treegraph.document.Document, info.bioinfweb.treegraph.gui.treeframe.TreeSelection, info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter)
	 */
	@Override
	public void setEnabled(Document document, TreeSelection selection, NodeBranchDataAdapter tableAdapter) {
		setEnabled((document != null) && !document.getTree().isEmpty() && 
				(document.getTree().getPaintStart().getChildren().size() >= 2));
	}
}
