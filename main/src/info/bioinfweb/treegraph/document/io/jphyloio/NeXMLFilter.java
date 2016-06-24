/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2016  Ben Stöver, Sarah Wiechers, Kai Müller
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
package info.bioinfweb.treegraph.document.io.jphyloio;

import info.bioinfweb.treegraph.document.io.AbstractXMLFilter;
import info.bioinfweb.treegraph.document.io.ReadWriteFormat;
import info.bioinfweb.treegraph.document.io.TreeFilter;

public class NeXMLFilter extends AbstractXMLFilter implements TreeFilter {
	public static final String NEXML_EXTENSION = ".nexml";
	
	
	@Override
	public ReadWriteFormat getFormat() {
		return ReadWriteFormat.NEXML;
	}


	public NeXMLFilter() {
		super("nexml");
	}
	

	public boolean validExtension(String name) {
		name = name.toLowerCase();
		return name.endsWith(NEXML_EXTENSION) || name.endsWith(XML_EXTENSION);
	}
	
	
	@Override
	public String getDescription() {
		return "neXML (*.nexml; *.xml)";
	}


	public String getDefaultExtension() {
		return NEXML_EXTENSION;
	}
}
