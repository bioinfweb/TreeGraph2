/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2015-2016  Ben Stöver, Sarah Wiechers
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


import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.undo.edit.LadderizeEdit;
import info.bioinfweb.treegraph.gui.actions.DocumentAction;
import info.bioinfweb.treegraph.gui.mainframe.MainFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeInternalFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeSelection;

import java.awt.event.ActionEvent;



/**
 * @author Ben St&ouml;ver
 */
public class LadderizeAction extends DocumentAction {
	private boolean down = true;
	
	
	public LadderizeAction(MainFrame mainFrame, boolean down) {
		super(mainFrame);
		this.down = down;
	}

	
	@Override
	public void setEnabled(Document document, TreeSelection selection, NodeBranchDataAdapter tableAdapter) {
		setEnabled(oneElementSelected(selection) && selection.containsType(Node.class) && 
				!((Node)selection.first()).isLeaf());
	}

	
	@Override
	protected void onActionPerformed(ActionEvent e, TreeInternalFrame frame) {
		frame.getDocument().executeEdit(new LadderizeEdit(frame.getDocument(), 
				(Node)frame.getTreeViewPanel().getSelection().first(), down));
	}
}