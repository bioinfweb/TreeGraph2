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
package info.bioinfweb.treegraph.graphics.export.emf;


import java.awt.Dimension;
import java.io.OutputStream;
import org.freehep.graphicsio.AbstractVectorGraphicsIO;
import org.freehep.graphicsio.emf.EMFGraphics2D;

import info.bioinfweb.treegraph.graphics.export.FreeHEPWriter;
import info.bioinfweb.treegraph.graphics.export.GraphicWriter;



public class EMFWriter extends FreeHEPWriter implements GraphicWriter {
	@Override
	protected AbstractVectorGraphicsIO createGraphics(OutputStream stream, Dimension dim) {
		return new EMFGraphics2D(stream, dim);
	}
}