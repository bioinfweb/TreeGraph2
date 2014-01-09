/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2014  Ben Stöver, Kai Müller
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
package info.bioinfweb.treegraph.gui.actions;


import java.awt.event.ActionEvent;

import info.bioinfweb.treegraph.gui.dialogs.EditDialog;
import info.bioinfweb.treegraph.gui.mainframe.MainFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeInternalFrame;



/**
 * Superclass for all actions that display a dialog.
 * @author Ben St&ouml;ver
 */
public abstract class EditDialogAction extends DocumentAction {
  private EditDialog dialog = null;
  
  
  public EditDialogAction(MainFrame mainFrame) {
  	super(mainFrame);
  }


	@Override
	protected void onActionPerformed(ActionEvent e, TreeInternalFrame frame) {
		if (frame != null) {
			getDialog().execute(frame.getDocument(), frame.getTreeViewPanel().getSelection(), 
					frame.getSelectedAdapter());
		}
		else {
			getDialog().execute(null, null, null);
		}
	}
	
	
	public abstract EditDialog createDialog();
	

	public EditDialog getDialog() {
		if (dialog == null) {
			dialog = createDialog();
		}
		return dialog;
	}
}