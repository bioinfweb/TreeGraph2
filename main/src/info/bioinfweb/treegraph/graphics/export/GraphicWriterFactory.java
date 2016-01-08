/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2015-2016  Ben Stöver, Sarah Wiechers, Kai Müller
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


import info.bioinfweb.treegraph.graphics.export.emf.EMFFactory;
import info.bioinfweb.treegraph.graphics.export.jpeg.JPEGFactory;
import info.bioinfweb.treegraph.graphics.export.pdf.PDFFactory;
import info.bioinfweb.treegraph.graphics.export.png.PNGFactory;
import info.bioinfweb.treegraph.graphics.export.svg.SVGFactory;
import info.bioinfweb.treegraph.graphics.export.tiff.TIFFFactory;

import java.util.EnumMap;
import java.util.Iterator;
import java.util.Set;



public class GraphicWriterFactory {
	private static GraphicWriterFactory firstInstance = null;
	
  private EnumMap<GraphicFormat, GraphicsFactory> factories = 
  	  new EnumMap<GraphicFormat, GraphicsFactory>(GraphicFormat.class);
  
  
  private GraphicWriterFactory() {
  	super();
  	fillList();
  }
  
  
  public static GraphicWriterFactory getInstance() {
  	if (firstInstance == null) {
  		firstInstance = new GraphicWriterFactory();
  	}
  	return firstInstance;
  }
  
  
  private void fillList() {
  	factories.put(GraphicFormat.PDF, new PDFFactory());
  	factories.put(GraphicFormat.SVG, new SVGFactory());
  	factories.put(GraphicFormat.PNG, new PNGFactory());
  	factories.put(GraphicFormat.TIFF, new TIFFFactory());
  	factories.put(GraphicFormat.JPEG, new JPEGFactory());
  	factories.put(GraphicFormat.EMF, new EMFFactory());
  }
  
  
  public GraphicFormat formatByFileName(String name) {
  	for (GraphicFormat f: GraphicFormat.values()) {
  		if (getFilter(f).validExtension(name)) {
  			return f;
  		}
  	}
  	return null;
  }
  
  
  public GraphicWriter getWriter(GraphicFormat f) {
  	return factories.get(f).getWriter();
  }
  
  
  public GraphicFilter getFilter(GraphicFormat f) {
  	return factories.get(f).getFilter();
  }
  
  
  public GraphicFilter[] getAllFilters() {
  	Set<GraphicFormat> set = factories.keySet();
  	GraphicFilter[] result = new GraphicFilter[set.size()];
  	Iterator<GraphicFormat> iterator = set.iterator();
  	int pos = 0;
  	while (iterator.hasNext()) {
  		result[pos] = getFilter(iterator.next());
  		pos++;
  	}
  	return result;
  }
  
  
  public int size() {
  	return factories.size();
  }
}