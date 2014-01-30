/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2014  Ben Stöver, Kai Müller
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
package info.bioinfweb.treegraph.graphics.export;


import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.graphics.positionpaint.TreePainter;
import info.webinsel.util.collections.ParameterMap;



public interface GraphicWriter {
	public static final String KEY_PIXELS_PER_MILLIMETER = "ppm";
	public static final String KEY_WIDTH = "width";
	public static final String KEY_HEIGHT = "height";
	public static final String KEY_DIMENSIONS_IN_PIXELS = "dimensionsInPixels";
	public static final String KEY_TRANSPARENT = "transparent";
	
	
	public void write(Document document, TreePainter painter, ParameterMap hints, OutputStream stream) throws IOException;

	
  public void write(Document document, TreePainter painter, ParameterMap hints,	File file)  throws IOException;
}