/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2011, 2013-2016  Ben Stöver, Sarah Wiechers, Kai Müller
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
package info.bioinfweb.treegraph.gui.treeframe;


import info.bioinfweb.commons.SystemUtils;
import info.bioinfweb.commons.swing.scrollpaneselector.ExtendedScrollPaneSelector;
import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.format.GlobalFormats;
import info.bioinfweb.treegraph.gui.treeframe.ruler.RulerOrientation;
import info.bioinfweb.treegraph.gui.treeframe.ruler.TreeViewRuler;
import info.bioinfweb.treegraph.gui.treeframe.ruler.TreeViewRulerUnitField;

import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import javax.swing.JScrollPane;



public class TreeScrollPane extends JScrollPane {
	private TreeViewPanel treeViewPanel = null;
	private TreeEditlInputListener treeViewInputListener = null;	
	
	
	public TreeScrollPane(Document document) {
		super();

		treeViewPanel = new TreeViewPanel(document);
		//treeViewPanel.setLayout(new GridBagLayout());
		
		treeViewInputListener = new TreeEditlInputListener(treeViewPanel); 
		treeViewPanel.addKeyListener(treeViewInputListener);
		treeViewPanel.addMouseListener(treeViewInputListener);
		treeViewPanel.addMouseWheelListener(treeViewInputListener);

		setViewportView(treeViewPanel);
		
		TreeViewRuler horizontalRuler = 
		  	new TreeViewRuler(RulerOrientation.HORIZONTAL, getTreeViewPanel()); 
		TreeViewRuler verticalRuler = 
		  	new TreeViewRuler(RulerOrientation.VERTICAL, getTreeViewPanel());
		setColumnHeaderView(horizontalRuler);
		setRowHeaderView(verticalRuler);
		setCorner(JScrollPane.UPPER_LEFT_CORNER, 
				new TreeViewRulerUnitField(horizontalRuler, verticalRuler));
		
		addMouseListener(new MouseAdapter() {
					@Override
					public void mousePressed(java.awt.event.MouseEvent e) {
						// Weitergeben mit Koordinaten (0|0) zum Deselektieren:
						getTreeViewInputListener().mousePressed(new MouseEvent(
								e.getComponent(), e.getID(), e.getWhen(), e.getModifiers(),
								0, 0, e.getButton(), e.isPopupTrigger()));
					}
				});
		
		addMouseWheelListener(new MouseAdapter() {
					@Override
					public void mouseWheelMoved(MouseWheelEvent e) {
						if ((e.isMetaDown() && SystemUtils.IS_OS_MAC) || (e.isControlDown()&&  !SystemUtils.IS_OS_MAC)) {  // Condition necessary, because events are also forwarded in oppisite direction.
							getTreeViewInputListener().mouseWheelMoved(e);
						}
					}
				});
		
		//treeScrollPane.getViewport().setBackground(GlobalFormats.DEFAULT_BACKGROUNG_COLOR);
	}
	
	
	/**
	 * This method initializes treeViewPanel	
	 * 	
	 * @return info.webinsel.treegraph.gui.TreeViewPanel	
	 */
	public TreeViewPanel getTreeViewPanel() {
		return treeViewPanel;
	}

	
	private TreeEditlInputListener getTreeViewInputListener() {
		return treeViewInputListener;
	}	
}
