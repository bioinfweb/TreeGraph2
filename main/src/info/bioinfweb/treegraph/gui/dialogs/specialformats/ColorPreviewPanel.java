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
package info.bioinfweb.treegraph.gui.dialogs.specialformats;


import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JPanel;



public class ColorPreviewPanel extends JPanel {
	public static final Color DEFAULT_MIN_COLOR = Color.GREEN;
	public static final Color DEFAULT_MAX_COLOR = Color.RED;

	
	private Color minColor = DEFAULT_MIN_COLOR; 
	private Color maxColor = DEFAULT_MAX_COLOR; 
	

	public Color getMaxColor() {
		return maxColor;
	}


	public void setMaxColor(Color maxColor) {
		this.maxColor = maxColor;
		repaint();
	}


	public Color getMinColor() {
		return minColor;
	}


	public void setMinColor(Color minColor) {
		this.minColor = minColor;
		repaint();
	}


	@Override
	public void paint(Graphics g) {
		int redRange = getMaxColor().getRed() - getMinColor().getRed();
		int greenRange = getMaxColor().getGreen() - getMinColor().getGreen();
		int blueRange = getMaxColor().getBlue() - getMinColor().getBlue();
		
		for (int x = 0; x < getWidth(); x++) {
			double percentage = ((double)x) / (double)getWidth();
			g.setColor(new Color(getMinColor().getRed() + (int)Math.round(redRange * percentage),
					getMinColor().getGreen() + (int)Math.round(greenRange * percentage),
					getMinColor().getBlue() + (int)Math.round(blueRange * percentage)));
			g.drawLine(x, 0, x, getHeight() - 1);
		}
	}
}