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
import java.awt.geom.RoundRectangle2D;



/**
 * @author Ben St&ouml;ver
 * @since 2.0.25
 */
public class RoundRectangleIcon extends ShapeLabelIcon implements LabelIcon {
	public static final float EDGE_RADIUS_FACTOR = 0.15f;
	
	
	public Shape getShape(float x, float y, IconLabelFormats formats, float pixelsPerMillimeter) {
		float lineWidth = formats.getLineWidth().getInPixels(pixelsPerMillimeter);
		float width = formats.getWidth().getInPixels(pixelsPerMillimeter) - lineWidth;
		float height = formats.getHeight().getInPixels(pixelsPerMillimeter) - lineWidth; 
		return new RoundRectangle2D.Float(x + 0.5f * lineWidth, y + 0.5f * lineWidth, width, height, 
				EDGE_RADIUS_FACTOR * width, EDGE_RADIUS_FACTOR * height);
	}

	
	public String id() {
		return "Rounded rectangle";
	}
}