/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2015  Ben Stöver, Sarah Wiechers, Kai Müller
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
package info.bioinfweb.treegraph.gui.actions.help;


import java.awt.event.KeyEvent;

import javax.swing.Action;



/**
 * Displays the page of the help system that describes all entries of the help system.
 * 
 * @author Ben St&ouml;ver
 */
public class HelpAboutMenuAction extends HelpTopicAction {
	public HelpAboutMenuAction() {
		super(74);
		putValue(Action.NAME, "Help about this main menu"); 
	  putValue(Action.MNEMONIC_KEY, KeyEvent.VK_H);
		putValue(Action.SHORT_DESCRIPTION, "Help about this main menu"); 
	  loadSymbols("Help");
	}
}