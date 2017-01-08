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
package info.bioinfweb.treegraph.gui.actions.view;


import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;

import info.bioinfweb.treegraph.document.format.DistanceDimension;
import info.bioinfweb.treegraph.gui.mainframe.MainFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeInternalFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeViewPanel;



public class FitZoomToWidthAction extends SetZoomAction {
	public FitZoomToWidthAction(MainFrame mainFrame) {
		super(mainFrame);
		putValue(Action.NAME, "Fit zoom to width"); 
	  putValue(Action.MNEMONIC_KEY, KeyEvent.VK_W);
		putValue(Action.SHORT_DESCRIPTION, "Fit to width"); 
	  loadSymbols("FitToWidth");
	}

	
	@Override
	protected float getZoom(ActionEvent e, TreeInternalFrame frame) {
		DistanceDimension d = frame.getTreeViewPanel().getDocument().getTree().getPaintDimension(frame.getTreeViewPanel().getPainterType());
		Rectangle r = frame.getDocumentRect();
		return ((float)r.width) / d.getWidth().getInPixels(TreeViewPanel.PIXELS_PER_MM_100);
	}
}