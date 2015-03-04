/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2015  Ben Stöver, Kai Müller
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
package info.bioinfweb.treegraph.gui.actions.window;


import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyVetoException;

import javax.swing.Action;
import javax.swing.JInternalFrame;

import info.bioinfweb.commons.swing.ExtendedAbstractAction;



public class SelectFrameAction extends ExtendedAbstractAction {
  private JInternalFrame frame = null;
  
  
	public SelectFrameAction(JInternalFrame frame, int index) {
		super();
		this.frame = frame;
		putValue(Action.NAME, index + " \"" + frame.getTitle() + "\""); 
	  putValue(Action.MNEMONIC_KEY, KeyEvent.VK_0 + index);
	  putValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY, 0);
	}


	public void actionPerformed(ActionEvent e) {
		try {
			frame.setSelected(true);
		}
		catch (PropertyVetoException ex) {
			// do nothing
		}
	}
}