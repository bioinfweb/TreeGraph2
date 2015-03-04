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
package info.bioinfweb.treegraph.gui.treeframe.ruler;


import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.SystemColor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;



/**
 * Component used to display and change the unit used by a {@link TreeViewRuler}.
 *  
 * @author Ben St&ouml;ver
 */
public class TreeViewRulerUnitField extends JComponent implements TreeViewRulerConstants {
	public static final String TEXT_CENTIMETER = "cm";
	public static final String TEXT_INCH = "in";
	
	
  private TreeViewRuler horizontalRuler;
  private TreeViewRuler verticalRuler;
  
  
	public TreeViewRulerUnitField(TreeViewRuler horizontalRuler, TreeViewRuler vertivalRuler) {
		super();
		this.horizontalRuler = horizontalRuler;
		this.verticalRuler = vertivalRuler;
		
		this.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						if (getUnit().equals(RulerUnit.CENTIMETER)) {
							setUnit(RulerUnit.INCH);
						}
						else {
							setUnit(RulerUnit.CENTIMETER);
						}
					}
    		});
		
		Dimension d = new Dimension(SMALL_LENGTH, SMALL_LENGTH);
		setPreferredSize(d);
		setSize(d);
	}
	
	
	private void setUnit(RulerUnit unit) {
		horizontalRuler.setUnit(unit);
		verticalRuler.setUnit(unit);
		repaint();
	}
	
	
	private RulerUnit getUnit() {
		return horizontalRuler.getUnit();
	}


	@Override
	protected void paintComponent(Graphics g1) {
		Graphics2D g = (Graphics2D)g1;
  	g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
  			RenderingHints.VALUE_ANTIALIAS_ON);
  	
  	g.setColor(SystemColor.menu);
  	g.fillRect(0, 0, SMALL_LENGTH, SMALL_LENGTH);
  	
  	String text = TEXT_CENTIMETER;
  	if (getUnit().equals(RulerUnit.INCH)) {
  		text = TEXT_INCH;
  	}
  	g.setColor(SystemColor.menuText);
  	g.setFont(FONT);
  	FontMetrics fm = g.getFontMetrics();
  	g.drawString(text, (SMALL_LENGTH - fm.stringWidth(text)) / 2,	
  			(SMALL_LENGTH - fm.getHeight()) / 2 + fm.getAscent());
	}
}