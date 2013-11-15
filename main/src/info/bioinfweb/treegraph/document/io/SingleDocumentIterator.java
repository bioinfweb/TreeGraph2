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
package info.bioinfweb.treegraph.document.io;


import info.bioinfweb.treegraph.document.Document;



/**
 * Document iterator for XTG documents (or other documents that can store only one tree per file). 
 * Since XTG documents in the current version of TreeGraph can only contain a single tree, the 
 * instance of this document is stored and returned when {@link #next()} is called the first time. 
 * Afterwards {@link #next()} will always return <code>null</code>.
 * 
 * @author Ben St&ouml;ver
 * @since 2.0.48
 */
public class SingleDocumentIterator implements DocumentIterator {
	private Document document;
	

	public SingleDocumentIterator(Document document) {
		super();
		this.document = document;
	}


	@Override
	public boolean hasNext() throws Exception {
		return document != null;
	}


	@Override
	public Document peek() throws Exception {
		return document;
	}


	@Override
	public Document next() throws Exception {
		Document result = document;
		document = null;
		return result;
	}
}
