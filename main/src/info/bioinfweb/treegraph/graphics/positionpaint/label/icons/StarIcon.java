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
package info.bioinfweb.treegraph.graphics.positionpaint.label.icons;


import info.bioinfweb.treegraph.document.format.IconLabelFormats;

import java.awt.Shape;
import java.awt.geom.Path2D;



/**
 * @author Ben St&ouml;ver
 * @since 2.0.25
 */
public class StarIcon extends ShapeLabelIcon implements LabelIcon {
	public static final String ID = "Star";
	
	public static final double RAY_ANGLE = (2d / 5d) * Math.PI;	
	public static final float SIN_RAY_72 = (float)Math.sin(RAY_ANGLE);	
	public static final float COS_RAY_72 = (float)Math.cos(RAY_ANGLE);	
	public static final float SIN_RAY_36 = (float)Math.sin(RAY_ANGLE / 2d);	
	public static final float COS_RAY_36 = (float)Math.cos(RAY_ANGLE / 2d);	
	public static final float LENGTH_BETWEEN_RAYS_FACTOR = (1f / 3f);	
	
	
	@Override
	public Shape getShape(float x, float y, IconLabelFormats formats, float pixelsPerMillimeter) {
		float lineWidth = formats.getLineWidth().getInPixels(pixelsPerMillimeter);
		x += lineWidth;
		y += lineWidth;
		float height = formats.getHeight().getInPixels(pixelsPerMillimeter) - 2 * lineWidth;
		float width = height;  // Height is used as the width during calculation to allow trigonometry.
		float halfWidth = 0.5f * width;
		float widthFactor = (formats.getWidth().getInPixels(pixelsPerMillimeter) - 2 * lineWidth) / height;
		float rayLength = height / (1 + COS_RAY_36);  // a1
		float betweenRayLength = LENGTH_BETWEEN_RAYS_FACTOR * rayLength;  // a2
		float halfUpperRayWidth = SIN_RAY_36 * betweenRayLength;  // c4
		float upperRayHeight = rayLength - COS_RAY_36 * betweenRayLength;  // d3
		float middleRaysApexY = rayLength - COS_RAY_72 * rayLength;  // a1 - d5
		float halfMiddleStarWidth = SIN_RAY_72 * betweenRayLength;  // c8
		float middleStarY = rayLength + COS_RAY_72 * betweenRayLength;  // a1 + d4
		float halfBottomRayDistance = SIN_RAY_36 * (height - rayLength);  // c5
		
		Path2D result = new Path2D.Float();
		result.moveTo(x + widthFactor * halfWidth, y);
		result.lineTo(x + widthFactor * (halfWidth + halfUpperRayWidth), y + upperRayHeight);
		result.lineTo(x + widthFactor * width, y + middleRaysApexY);
		result.lineTo(x + widthFactor * (halfWidth + halfMiddleStarWidth), y + middleStarY);
		result.lineTo(x + widthFactor * (halfWidth + halfBottomRayDistance), y + height);
		result.lineTo(x + widthFactor * halfWidth, y + rayLength + betweenRayLength);
		result.lineTo(x + widthFactor * (halfWidth - halfBottomRayDistance), y + height);
		result.lineTo(x + widthFactor * (halfWidth - halfMiddleStarWidth), y + middleStarY);
		result.lineTo(x, y + middleRaysApexY);
		result.lineTo(x +  widthFactor * (halfWidth - halfUpperRayWidth), y + upperRayHeight);
		result.closePath();
		return result;
	}

	
	public String id() {
		return ID;
	}
}