/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2011, 2013-2017  Ben Stöver, Sarah Wiechers, Kai Müller
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
package info.bioinfweb.treegraph.graphics.export.tiff;


import info.bioinfweb.treegraph.graphics.export.GraphicFilter;
import info.bioinfweb.treegraph.graphics.export.GraphicFormat;



public class TIFFFilter extends GraphicFilter {
	public static final String EXTENSION = ".tiff";
	public static final String ALTERNATIVE_EXTENSION = ".tif";
	
	
	public boolean validExtension(String name) {
		name = name.toLowerCase();
		return name.endsWith(EXTENSION) || name.endsWith(ALTERNATIVE_EXTENSION); 
	}

	
	@Override
	public String getDescription() {
		return "Tagged Image File Format (*" + EXTENSION + ")";
	}


	public String getDefaultExtension() {
		return EXTENSION;
	}


	public GraphicFormat getFormat() {
		return GraphicFormat.TIFF;
	}
}