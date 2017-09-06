/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2011, 2013-2017  Ben Stöver, Sarah Wiechers, Kai Müller
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



public class TwitterAction extends WebsiteAction {
	public TwitterAction() {
		super("http://r.bioinfweb.info/Twitter");
		
		putValue(Action.NAME, "bioinfweb news on Twitter"); 
	  putValue(Action.MNEMONIC_KEY, KeyEvent.VK_T);
	  loadSymbols("Twitter");
	}
}