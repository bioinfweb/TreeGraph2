/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2011, 2013-2017  Ben Stöver, Sarah Wiechers, Kai Müller
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


import java.io.File;

import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.commons.log.ApplicationLogger;


/**
 * Iterates over all trees contained in multiple documents. The single documents may have different
 * supported formats.
 * 
 * @author Ben St&ouml;ver
 */
public class MultipleDocumentIterator extends AbstractDocumentIterator implements DocumentIterator {
	private File[] files;
	private int position = -1;
	private DocumentIterator documentIterator = null;
	
	
	public MultipleDocumentIterator(File[] files, ApplicationLogger loadLogger,
			NodeBranchDataAdapter internalAdapter,
			NodeBranchDataAdapter branchLengthsAdapter,
			boolean translateInternalNodes) {
		
		super(loadLogger, internalAdapter, branchLengthsAdapter, translateInternalNodes);
		this.files = files;
	}

	
	@Override
  protected Document readNext() throws Exception {
		Document result = null; 
		if (documentIterator != null){
			result = documentIterator.next();
		}
		
		if(result == null){
			position++;
			if (position < files.length){
				documentIterator = ReadWriteFactory.getInstance().getReader(files[position]).readAll(
						files[position], getParameterMap());
				return documentIterator.next();
			}
		}
		else {
			return result;
		}
		return null;
  }
}
