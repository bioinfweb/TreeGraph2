/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2013  Ben St�ver, Kai M�ller
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


import info.bioinfweb.treegraph.Main;

import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;



/**
 * The help button in the upper right corner of a {@link TreeInternalFrame}.
 * 
 * @author Ben St&ouml;ver
 * @since 2.0.23
 */
public class HelpButton extends JButton {
  public HelpButton(final int helpIndex) {
  	super(new ImageIcon(Object.class.getResource("/resources/symbols/Help16.png")));
  	setBorder(BorderFactory.createEmptyBorder());
  	setMargin(new Insets(0, 0, 0, 0));
  	
  	addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent e) {
						Main.getInstance().getWikiHelp().displayTopic(helpIndex);
					}
				});
  }
}