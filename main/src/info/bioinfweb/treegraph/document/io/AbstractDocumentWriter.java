/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2014  Ben St�ver, Kai M�ller
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


import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeNameAdapter;

import java.io.*;



public abstract class AbstractDocumentWriter implements DocumentWriter {
  protected Document document = null;
  
  
	public void write(Document document, File file) throws Exception {
		write(document, new FileOutputStream(file), new ReadWriteParameterMap());
	}
  
  
	@Override
  public void write(Document document, OutputStream stream) throws Exception {
		write(document, stream, new ReadWriteParameterMap());
  }


	@Override
  public void write(Document document, File file, ReadWriteParameterMap properties) throws Exception {
		write(document, new FileOutputStream(file), properties);
  }
}