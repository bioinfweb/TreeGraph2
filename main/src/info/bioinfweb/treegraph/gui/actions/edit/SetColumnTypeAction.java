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
package info.bioinfweb.treegraph.gui.actions.edit;


import java.awt.event.ActionEvent;

import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.TextElementDataAdapter;
import info.bioinfweb.treegraph.document.undo.edit.SetColumnTypeEdit;
import info.bioinfweb.treegraph.gui.actions.DocumentAction;
import info.bioinfweb.treegraph.gui.mainframe.MainFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeInternalFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeSelection;



/**
 * Implements basic functionality to set the data type for all lines of a node/branch data column.
 * @author Ben St&ouml;ver
 * @since 2.0.24
 */
public abstract class SetColumnTypeAction extends DocumentAction {
	private boolean decimal;
	
	
	public SetColumnTypeAction(MainFrame mainFrame, boolean decimal) {
		super(mainFrame);
		this.decimal = decimal;
	}


	@Override
	protected void onActionPerformed(ActionEvent e, TreeInternalFrame frame) {
		frame.getDocument().executeEdit(new SetColumnTypeEdit(frame.getDocument(), 
				(TextElementDataAdapter)frame.getSelectedAdapter(), decimal));
	}


	@Override
	public void setEnabled(Document document, TreeSelection selection, NodeBranchDataAdapter tableAdapter) {
		setEnabled((tableAdapter != null) && (tableAdapter instanceof TextElementDataAdapter));
	}
}