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
package info.bioinfweb.treegraph.document.format;


import java.awt.Color;

import info.bioinfweb.treegraph.document.Label;



/**
 * All format classes for document elements which represent labels with line formats should inherit
 * from this class.
 *   
 * @author Ben St&ouml;ver
 * @since 2.0.43
 */
public abstract class GraphicalLabelFormats extends LabelFormats {
	public static final float DEFAULT_ICON_WIDTH_IN_MM = 3f;
	public static final float DEFAULT_ICON_HEIGHT_IN_MM = 3f;


	private Color lineColor = LineFormats.DEFAULT_LINE_COLOR;
  private DistanceValue lineWidth = new DistanceValue(LineFormats.DEFAULT_LINE_WIDTH_IN_MM);
  private DistanceValue width = new DistanceValue(DEFAULT_ICON_WIDTH_IN_MM);
  private DistanceValue height = new DistanceValue(DEFAULT_ICON_HEIGHT_IN_MM);
  
  
	public GraphicalLabelFormats(Label owner, boolean above, int line, int linePosition) {
		super(owner, above, line, linePosition);
	}

	
	public GraphicalLabelFormats(Label owner) {
		super(owner);
	}

	
	public Color getLineColor() {
		return lineColor;
	}


	public void setLineColor(Color color) {
		lineColor = color;
	}


	public DistanceValue getLineWidth() {
		return lineWidth;
	}


	public DistanceValue getWidth() {
		return width;
	}


	public DistanceValue getHeight() {
		return height;
	}
	
	
	public void assignLineFormats(LineFormats other) {
		setLineColor(other.getLineColor());
		getLineWidth().assign(other.getLineWidth());
	}
	
	
	/**
	 * Assigns the graphical labels formats to this object. (This does not include line formats.)
	 * @param other - the source format object
	 */
	public void assignGraphicalLabelFormats(GraphicalLabelFormats other) {
		getWidth().assign(other.getWidth());
		getHeight().assign(other.getHeight());
	}
}