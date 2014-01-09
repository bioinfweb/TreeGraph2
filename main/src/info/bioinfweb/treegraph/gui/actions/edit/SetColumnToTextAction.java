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
package info.bioinfweb.treegraph.gui.actions.edit;


import info.bioinfweb.treegraph.gui.mainframe.MainFrame;

import java.awt.event.KeyEvent;

import javax.swing.Action;



public class SetColumnToTextAction extends SetColumnTypeAction {
	public SetColumnToTextAction(MainFrame mainFrame) {
		super(mainFrame, false);
		putValue(Action.NAME, "Set column to text type"); 
	  putValue(Action.MNEMONIC_KEY, KeyEvent.VK_T);
	  putValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY, 14);
		putValue(Action.SHORT_DESCRIPTION, "Set column to text type"); 
	}
}