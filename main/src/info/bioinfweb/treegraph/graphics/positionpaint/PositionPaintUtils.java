/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2011, 2013-2019  Ben Stöver, Sarah Wiechers, Kai Müller
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
package info.bioinfweb.treegraph.graphics.positionpaint;


import info.bioinfweb.commons.graphics.FontCalculator;
import info.bioinfweb.treegraph.document.TextElement;
import info.bioinfweb.treegraph.document.format.DistanceDimension;
import info.bioinfweb.treegraph.document.format.TextFormats;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Line2D;



/**
 * Tool class that provides method for positioning and painting tree elements.
 * 
 * @author Ben St&ouml;ver
 * @since 2.13.0
 */
public class PositionPaintUtils {
	public static final float UNDERLINE_DISTANCE_FACTOR = 0.15f;
	public static final float UNDERLINE_LINE_WIDTH_FACTOR = 0.05f;
	
	
	/**
	 * Calculates the Dimensions of this text element. The height is already defined in
	 * by the provided formats and the width will calculated from the the text length scaled
	 * with the provided height.
	 * 
	 * @param textElement the text element for which the dimensions shall be calculated
	 * @return the dimensions of the element not including optional margins
	 */
	public static DistanceDimension calculateTextDimension(TextElement textElement) {
		DistanceDimension result = new DistanceDimension();
		
		TextFormats formats = textElement.getFormats();
		String text = textElement.getData().formatValue(formats.getDecimalFormat());
		float height = formats.getTextHeight().getInMillimeters();
		result.getHeight().setInMillimeters(height);
		result.getWidth().setInMillimeters(
			  FontCalculator.getInstance().getTextWidthToTextHeigth(formats.getFontName(), 
			  		formats.getTextStyle() & ~TextFormats.UNDERLINE, text, height));
		
		return result;
	}
	
	
	public static float paintText(Graphics2D g, float pixelsPerMillimeter, String text, TextFormats f, float x, float y) {
		Font font = f.getFont(pixelsPerMillimeter);
		g.setColor(f.getTextColor());
		g.setFont(font);
		g.drawString(text, x, y);
		
		float width = FontCalculator.getInstance().getWidth(font, text);
		if (f.hasTextStyle(TextFormats.UNDERLINE)) {
			float height = FontCalculator.getInstance().getHeight(font);
			Stroke oldStroke = g.getStroke();
			g.setStroke(new BasicStroke(UNDERLINE_LINE_WIDTH_FACTOR * height));
			y += UNDERLINE_DISTANCE_FACTOR * height;
			g.draw(new Line2D.Float(x, y, x + width, y));
			g.setStroke(oldStroke);
		}
		
		return width;
	}
}
