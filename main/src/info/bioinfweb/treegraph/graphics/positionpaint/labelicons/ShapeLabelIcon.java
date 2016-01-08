/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2015-2016  Ben Stöver, Sarah Wiechers, Kai Müller
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
package info.bioinfweb.treegraph.graphics.positionpaint.labelicons;


import info.bioinfweb.treegraph.document.format.IconLabelFormats;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;



/**
 * @author Ben St&ouml;ver
 * @since 2.0.25
 */
public abstract class ShapeLabelIcon implements LabelIcon {
  public abstract Shape getShape(float x, float y, IconLabelFormats formats, float pixelsPerMillimeter);
  
  
	/**
	 * Paints the shape of the implementing class depending on the specified formats and restores the current
	 * {@link Stroke} after the operation.
	 * @see info.bioinfweb.treegraph.graphics.positionpaint.labelicons.LabelIcon#paint(java.awt.Graphics2D, float, float, info.bioinfweb.treegraph.document.format.LabelFormats, float)
	 */
	public void paint(Graphics2D g, float x, float y, IconLabelFormats formats, float pixelsPerMillimeter) {
		Shape shape = getShape(x, y, formats, pixelsPerMillimeter);
		
		g.setColor(formats.getLineColor());
		Stroke stroke = g.getStroke();
		try {
	    g.setStroke(new BasicStroke(formats.getLineWidth().getInPixels(pixelsPerMillimeter)));
			g.draw(shape);  // g.fill() allein w�rde zu klein zeichnen
			if (formats.getIconFilled()) {
				g.fill(shape);
			}
		}
		finally {
			g.setStroke(stroke);
		}
	}
}