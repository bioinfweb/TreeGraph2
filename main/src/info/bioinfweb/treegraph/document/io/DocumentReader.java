/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2011, 2013-2016  Ben Stöver, Sarah Wiechers, Kai Müller
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

import java.io.*;
import java.util.Iterator;



/**
 * Classes that are able to read tree documents should implement this interface.
 * 
 * @author Ben St&ouml;ver
 */
public interface DocumentReader {
  public Document read(InputStream stream) throws Exception;
  
  public Document read(File file) throws Exception;
  
	public Document read(InputStream stream, ReadWriteParameterMap properties) throws Exception;
	
	public Document read(File file, ReadWriteParameterMap properties) throws Exception;
  
	/**
	 * Implementations of this method should return an iterator that reads the next tree in <code>file</code>, if 
	 * {@link Iterator#next()} is called. (The single trees must not be loaded at once, but should be loaded one by
	 * one each time {@link Iterator#next()} is called.)
	 */
	public DocumentIterator readAll(InputStream stream, ReadWriteParameterMap properties) throws Exception;

	/**
	 * Implementations of this method should return an iterator that reads the next tree in <code>file</code>, if 
	 * {@link Iterator#next()} is called. (The single trees must not be loaded at once, but should be loaded one by
	 * one each time {@link Iterator#next()} is called.)
	 */
	public DocumentIterator readAll(File file, ReadWriteParameterMap properties) throws Exception;
}