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
package info.bioinfweb.treegraph.document.io.newick;


import java.io.OutputStream;
import java.io.OutputStreamWriter;

import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.io.AbstractDocumentWriter;
import info.bioinfweb.treegraph.document.io.DocumentWriter;
import info.bioinfweb.treegraph.document.io.ReadWriteParameterMap;



/**
 * This class writes a <code>Document</code> in a nexus file (contains only the newick string). 
 * @author Ben St&ouml;ver
 */
public class NewickWriter extends AbstractDocumentWriter implements DocumentWriter {
	public void write(Document document, OutputStream stream,	ReadWriteParameterMap properties) throws Exception {
		
		OutputStreamWriter writer = new OutputStreamWriter(stream);
		try {
			writer.write(NewickStringWriter.write(document.getTree(), properties));
		}
		finally {
			writer.close();
		}
	}
}