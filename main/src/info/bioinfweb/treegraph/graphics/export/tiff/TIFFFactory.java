/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2013  Ben Stöver, Kai Müller
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
import info.bioinfweb.treegraph.graphics.export.GraphicWriter;
import info.bioinfweb.treegraph.graphics.export.SingletonGraphicsFactory;



public class TIFFFactory extends SingletonGraphicsFactory {
	@Override
	protected GraphicFilter createFilter() {
		return new TIFFFilter();
	}

	
	@Override
	protected GraphicWriter createWriter() {
		return new TIFFWriter();
	}
}