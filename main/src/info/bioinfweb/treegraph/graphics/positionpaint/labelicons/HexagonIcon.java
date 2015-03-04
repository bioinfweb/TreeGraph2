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
package info.bioinfweb.treegraph.graphics.positionpaint.labelicons;


import info.bioinfweb.treegraph.document.format.IconLabelFormats;

import java.awt.Shape;
import java.awt.geom.Path2D;



/**
 * @author Ben St&ouml;ver
 * @since 2.0.25
 */
public class HexagonIcon extends ShapeLabelIcon implements LabelIcon {
	@Override
	public Shape getShape(float x, float y, IconLabelFormats formats, float pixelsPerMillimeter) {
		float lineWidth = formats.getLineWidth().getInPixels(pixelsPerMillimeter);
		x += 0.5f * lineWidth;
		y += 0.5f * lineWidth;
		float width = formats.getWidth().getInPixels(pixelsPerMillimeter) - lineWidth;
		float cornerLengthX = width / 4f;  // wg. sin(60°) = 0.5 
		float edgeLengthX = width / 2f;
		float height = formats.getHeight().getInPixels(pixelsPerMillimeter) - lineWidth;
		float edgeLengthY = height / 2f;
		
		Path2D result = new Path2D.Float();
		result.moveTo(x + cornerLengthX, y);
		result.lineTo(x + cornerLengthX + edgeLengthX, y);
		result.lineTo(x + width, y + edgeLengthY);
		result.lineTo(x + cornerLengthX + edgeLengthX, y + height);
		result.lineTo(x + cornerLengthX, y + height);
		result.lineTo(x, y + edgeLengthY);
		result.closePath();
		return result;
	}

	
	public String id() {
		return "Hexagon";
	}
}