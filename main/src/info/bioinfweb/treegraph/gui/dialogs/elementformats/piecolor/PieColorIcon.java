/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2015-2016  Ben Stöver, Sarah Wiechers
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
package info.bioinfweb.treegraph.gui.dialogs.elementformats.piecolor;


import info.bioinfweb.treegraph.gui.dialogs.elementformats.IconPieChartLabelPanel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;



/**
 * Icon used in the pie color list of the {@link IconPieChartLabelPanel}.
 * @author Ben St&ouml;ver
 * @since 2.0.43
 */
public class PieColorIcon implements Icon{
	public static final int ICON_DOMENSION = 16;
	
	
  private Color color = Color.RED;
	
	
	@Override
	public int getIconHeight() {
		return ICON_DOMENSION;
	}

	
	@Override
	public int getIconWidth() {
		return ICON_DOMENSION;
	}

	
	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
	  g.setColor(color);
	  g.fillRect(x, y, ICON_DOMENSION, ICON_DOMENSION);
		g.setColor(c.getForeground());
		g.drawRect(x, y, ICON_DOMENSION, ICON_DOMENSION);
	}


	public void setColor(Color color) {
		this.color = color;
	}
}