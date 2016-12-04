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
package info.bioinfweb.treegraph.gui.dialogs.elementformats;


import info.bioinfweb.treegraph.document.format.DistanceValue;
import info.bioinfweb.treegraph.document.format.IconLabelFormats;
import info.bioinfweb.treegraph.graphics.positionpaint.labelicons.LabelIconMap;
import info.bioinfweb.treegraph.gui.treeframe.TreeViewPanel;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.SystemColor;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;



/**
 * Panel used to display a preview of the formatted icon in the {@link IconPieChartLabelPanel} of the 
 * {@link ElementFormatsDialog}.
 * 
 * @author Ben St&ouml;ver
 * @since 2.0.25
 */
public class IconPreviewPanel extends JPanel {
	public static final Color BG_COLOR = SystemColor.text; 
	public static final Color ICON_COLOR = SystemColor.textText;
	
	
  private IconLabelFormats formats = new IconLabelFormats(null);

  
	public IconPreviewPanel() {
		super();
		setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		formats.setLineColor(ICON_COLOR);
	}


	public DistanceValue getIconHeight() {
		return formats.getHeight();
	}


	public DistanceValue getIconWidth() {
		return formats.getWidth();
	}


	public void setIcon(String icon) {
		formats.setIcon(icon);
	}


	public void setIconFilled(boolean iconFilled) {
		formats.setIconFilled(iconFilled);
	}


	@Override
	public void paint(Graphics g) {
		g.setColor(BG_COLOR);
		g.fillRect(0, 0, getWidth() - 1, getHeight() - 1);
    paintBorder(g);
		
		g.setColor(ICON_COLOR);
		LabelIconMap.getInstance().get(formats.getIcon()).paint(
				(Graphics2D)g, 
				Math.max(0, 0.5f * 
						(getWidth() - formats.getWidth().getInPixels(TreeViewPanel.PIXELS_PER_MM_100))), 
				Math.max(0, 0.5f * 
						(getHeight() - formats.getHeight().getInPixels(TreeViewPanel.PIXELS_PER_MM_100))), 
				formats,	TreeViewPanel.PIXELS_PER_MM_100);
	}
}