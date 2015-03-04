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
package info.bioinfweb.treegraph.gui.actions.view;


import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.JOptionPane;

import info.bioinfweb.treegraph.gui.mainframe.MainFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeInternalFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeViewPanel;



public class SetUserZoomAction extends SetZoomAction {
	public SetUserZoomAction(MainFrame mainFrame) {
		super(mainFrame);
		putValue(Action.NAME, "Set zoom..."); 
	  putValue(Action.MNEMONIC_KEY, KeyEvent.VK_S);
  }

	
	@Override
	protected float getZoom(ActionEvent e, TreeInternalFrame frame) {
		TreeViewPanel panel = frame.getTreeViewPanel();
		boolean cancel = false;
		float result = panel.getZoom();
		
		while (!cancel) {
			String input = JOptionPane.showInputDialog(frame, 
					"Note that you could alternatively use Ctrl + mouse wheel.\n" +
					"(See the help for details.)\n\nZoom [%]:", "" + (panel.getZoom() * 100f));
			if (input != null) {
				try {
					result = Float.parseFloat(input) / 100f;
					cancel = true;
				}
				catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(frame, "\"" + input + "\" is not a valid floating point value.");
				}
			}
			else {
				cancel = true;
			}
		}
		
		return result; 
	}
}