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
package info.bioinfweb.treegraph.document.io;


import info.webinsel.util.io.XMLUtils;

import java.io.File;
import java.io.IOException;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;



/**
 * @author Ben St&ouml;ver
 * @since 2.0.35
 */
public abstract class AbstractXMLFilter extends AbstractFilter{
	public static final String XML_EXTENSION = ".xml";
	
	
	private String rootName;
	
	
	public AbstractXMLFilter(String rootName) {
		super();
		this.rootName = rootName;
	}


	@Override
	public boolean accept(File f) {
		boolean result = super.accept(f);
		if (result && f.getAbsolutePath().endsWith(XML_EXTENSION)) {
			try {
				result = XMLUtils.readRootElement(f).getName().getLocalPart().equals(rootName);
			}
			catch (IOException e) {
				result = false;
			}
			catch (XMLStreamException e) {
				result = false;
			}
		}
		return result;
	}
}