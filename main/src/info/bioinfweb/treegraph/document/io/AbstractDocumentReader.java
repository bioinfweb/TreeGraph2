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


import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.io.log.LoadLogger;
import info.bioinfweb.treegraph.document.nodebranchdata.BranchLengthAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeNameAdapter;
import info.webinsel.util.io.XMLUtils;

import java.io.*;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;




/**
 * Implements basic functionality (especially method delegation) for document readers.
 * @author Ben St&ouml;ver
 */
public abstract class AbstractDocumentReader implements DocumentReader {
	protected LoadLogger loadLogger = null;
  protected Document document = null;
  
  
  /**
   * Delegates to {@link #reachElementEnd(XMLEventReader, String)} where the name of the specified element is 
   * passed as <code>elementDescription</code>.
   * @param reader
   * @param element
   * @throws XMLStreamException
   * @since 2.0.42
   */
  protected void reachElementEnd(XMLEventReader reader, StartElement element) throws XMLStreamException {
  	reachElementEnd(reader, "The XML element \"" + element.getName().toString() + "\" and possible subelements ");
  }
  
  
  /**
   * Delegates to {@link XMLUtils#reachElementEnd(XMLEventReader)} and adds a warning to the {@link LoadLogger}.
   * @param reader
   * @param elementDescription - a description of the possibly skipped elements (e.g. "<i>Elements under 
   *        &lt;ScaleBar&gt;</i>) 
   * @throws XMLStreamException
   * @since 2.0.42
   */
  protected void reachElementEnd(XMLEventReader reader, String elementDescription) throws XMLStreamException {
  	if (XMLUtils.reachElementEnd(reader) && (loadLogger != null)) {
  		loadLogger.addWarning(elementDescription + " are not recognized at this position and were skipped. If you " +
  				"are loading an XTG file and save it to XTG again later these elements will be lost.");
  	}
  }
  
  
	public Document read(File file, LoadLogger loadLogger) throws Exception {
		return read(file, loadLogger, NodeNameAdapter.getSharedInstance(), BranchLengthAdapter.getSharedInstance());
	}
  
  
	public Document read(File file, LoadLogger loadLogger, NodeBranchDataAdapter internalAdapter, 
			NodeBranchDataAdapter branchLengthsAdapter) throws Exception {
		
		return read(file, loadLogger, internalAdapter, branchLengthsAdapter, new DefaultTreeSelector(), false);
	}


	/**
	 * If you want to implement file specific functionalities you should override this
	 * method. It is called by all other <code>read</code>-methods with a file as parameter.
	 * @see info.bioinfweb.treegraph.document.io.DocumentReader#read(java.io.File, NodeBranchDataAdapter, int)
	 */
	public Document read(File file, LoadLogger loadLogger, NodeBranchDataAdapter internalAdapter, NodeBranchDataAdapter branchLengthsAdapter,
      TreeSelector selector, boolean translateInternalNodes) throws Exception {
		
		return read(new FileInputStream(file), loadLogger, internalAdapter, branchLengthsAdapter, selector, 
				translateInternalNodes);
	}


	public Document read(InputStream stream, LoadLogger loadLogger, NodeBranchDataAdapter internalAdapter,
			NodeBranchDataAdapter branchLengthsAdapter) throws Exception {
				
		return read(stream, loadLogger, internalAdapter, branchLengthsAdapter, new DefaultTreeSelector(), false);
	}


	public Document read(InputStream stream, LoadLogger loadLogger) throws Exception {
		return read(stream, loadLogger, NodeNameAdapter.getSharedInstance(), BranchLengthAdapter.getSharedInstance(),
				new DefaultTreeSelector(), false);
	}


	@Override
	public DocumentIterator readAll(File file, LoadLogger loadLogger,
			NodeBranchDataAdapter internalAdapter,
			NodeBranchDataAdapter branchLengthsAdapter, boolean translateInternalNodes)
			throws Exception {

		return readAll(new FileInputStream(file), loadLogger, internalAdapter, branchLengthsAdapter, translateInternalNodes);
	}
}