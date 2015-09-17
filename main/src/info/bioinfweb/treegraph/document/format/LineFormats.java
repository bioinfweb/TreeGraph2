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
package info.bioinfweb.treegraph.document.format;


import java.awt.Color;



public interface LineFormats extends ElementFormats {
	public static final float DEFAULT_LINE_WIDTH_IN_MM = 0.3f;
	public static final Color DEFAULT_LINE_COLOR = Color.BLACK;
	
	
	public Color getLineColor();
	
	public void setLineColor(Color color);
	
	public DistanceValue getLineWidth();
	
	public void assignLineFormats(LineFormats other);
	
	public LineFormats clone();
}