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


import info.bioinfweb.treegraph.Main;
import info.bioinfweb.treegraph.document.Document;
import info.webinsel.util.collections.ParameterMap;
import info.webinsel.util.io.XMLUtils;
import info.webinsel.util.log.ApplicationLogger;
import info.webinsel.util.log.VoidApplicationLogger;

import java.io.*;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;




/**
 * Implements basic functionality (especially method delegation) for document readers.
 * @author Ben St&ouml;ver
 */
public abstract class AbstractDocumentReader implements DocumentReader {
	private boolean saveFileName = false;
	protected ApplicationLogger loadLogger = null;
	protected ReadWriteParameterMap parameterMap = null;
  protected Document document = null;
  
  
  /**
   * Creates a new instance of this class.
   * 
   * @param saveFileName - Specify <code>true</code> here, if XTG format can be written into the opened file.
   *        If <code>false</code> is specified a unique default name will be generated according to the name of
   *        the opened file.
   */
  public AbstractDocumentReader(boolean saveFileName) {
	  super();
	  this.saveFileName = saveFileName;
  }


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
  

  private ApplicationLogger getLoadLogger(ParameterMap parameterMap) {
  	return (ApplicationLogger)parameterMap.getObject(ReadWriteParameterMap.KEY_LOAD_LOGGER, new VoidApplicationLogger());
  }
  
  
	@Override
  public Document read(InputStream stream) throws Exception {
	  return read(stream, new ReadWriteParameterMap());
  }


	@Override
  public Document read(File file) throws Exception {
	  return read(file, new ReadWriteParameterMap());
  }


	@Override
  public Document read(InputStream stream, ReadWriteParameterMap properties) throws Exception {
		parameterMap = properties;
		loadLogger = getLoadLogger(properties);
		return readDocument(new BufferedInputStream(stream));
  }
	
	
	public abstract Document readDocument(BufferedInputStream stream) throws Exception;


	@Override
  public Document read(File file, ReadWriteParameterMap properties) throws Exception {
		Document result = read(new FileInputStream(file), properties);
		if (saveFileName) {
			result.setFile(file);
		}
		else {
			result.setDefaultName(result.getDefaultName().replaceAll(
    			Main.getInstance().getNameManager().getPrefix(), file.getName()));
		}
		return result;
  }


	public abstract DocumentIterator createIterator(BufferedInputStream stream) throws Exception;
	
	
	@Override
  public DocumentIterator readAll(InputStream stream, ReadWriteParameterMap properties) throws Exception {
		parameterMap = properties;
		loadLogger = getLoadLogger(properties);
		return createIterator(new BufferedInputStream(stream));
  }


	@Override
	public DocumentIterator readAll(File file, ReadWriteParameterMap properties) throws Exception {
		return readAll(new FileInputStream(file), properties);
	}
}