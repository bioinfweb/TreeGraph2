/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2011  Ben Stöver, Kai Müller
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
package info.bioinfweb.treegraph.document.io.xtg;


import info.bioinfweb.treegraph.document.io.AbstractXMLFilter;
import info.bioinfweb.treegraph.document.io.DocumentFilter;



/**
 * @author Ben St&ouml;ver
 */
public class XTGFilter extends AbstractXMLFilter implements DocumentFilter, XTGConstants {
  public static final String EXTENSION = ".xtg";

  
	public XTGFilter() {
		super(TAG_ROOT);
	}


	public boolean validExtension(String name) {
		name = name.toLowerCase();
		return name.endsWith(EXTENSION) || name.endsWith(XML_EXTENSION);
	}
  
  
	@Override
	public String getDescription() {
		return "TreeGraph 2 XML format (*.xtg; *.xml)";
	}


	public String getDefaultExtension() {
		return EXTENSION;
	}
}