/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2015  Ben Stöver, Sarah Wiechers, Kai Müller
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
package info.bioinfweb.treegraph.document.io.phyloxml;


import info.bioinfweb.treegraph.document.Branch;
import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.HiddenDataMap;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.TextElementData;
import info.bioinfweb.treegraph.document.TextLabel;
import info.bioinfweb.treegraph.document.Tree;
import info.bioinfweb.treegraph.document.format.GlobalFormats;
import info.bioinfweb.treegraph.document.io.AbstractDocumentReader;
import info.bioinfweb.treegraph.document.io.DocumentIterator;
import info.bioinfweb.treegraph.document.io.newick.BranchLengthsScaler;
import info.bioinfweb.treegraph.document.io.newick.NewickTreeList;
import info.bioinfweb.treegraph.document.undo.format.AutoPositionLabelsEdit;
import info.bioinfweb.commons.io.XMLUtils;

import java.awt.Color;
import java.io.BufferedInputStream;
import java.util.Vector;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;



/**
 * Reads a <a href="http://www.phyloxml.org/">phyloXML</a> document.
 * @author Ben St&ouml;ver
 * @since 2.0.35
 */
public class PhyloXMLReader extends AbstractDocumentReader implements PhyloXMLConstants {
  private XMLEventReader reader;
  private Vector<String> names = new Vector<String>();
  private Vector<Tree> phylogenies = new Vector<Tree>();
  private BranchLengthsScaler branchLengthsScaler = new BranchLengthsScaler();
  
  
	public PhyloXMLReader() {
		super(false);
	}
	
	
	private int readColorValue() throws XMLStreamException {
  	return Math.max(0, Math.min(255, Short.parseShort(reader.nextEvent().asCharacters().getData())));
	}
	
	
	private Color readColor() throws XMLStreamException {
    int red = 0;
    int green = 0;
    int blue = 0;
    
    XMLEvent event = reader.nextEvent();
    while (event.getEventType() != XMLStreamConstants.END_ELEMENT) {
      if (event.getEventType() == XMLStreamConstants.START_ELEMENT) {
      	StartElement element = event.asStartElement();
        if (element.getName().equals(TAG_COLOR_RED)) {
        	red = readColorValue();  // Anmerkung: F�hrt zu Fehler, falls Element leer ist!
        	reader.nextEvent();
        }
        else if (element.getName().equals(TAG_COLOR_GREEN)) {
        	green = readColorValue();  // Anmerkung: F�hrt zu Fehler, falls Element leer ist!
        	reader.nextEvent();
        }
        else if (element.getName().equals(TAG_COLOR_BLUE)) {
        	blue = readColorValue();  // Anmerkung: F�hrt zu Fehler, falls Element leer ist!
        	reader.nextEvent();
        }
        else {  // evtl. zus�tzlich vorhandenes Element, dass nicht gelesen wird
          XMLUtils.reachElementEnd(reader);  
        }
      }
      event = reader.nextEvent();
    }
    return new Color(red, green, blue);
	}
	
	
	private void storeHiddenData(HiddenDataMap map, String keyPrefix, StartElement element) throws XMLStreamException {
		String value = reader.nextEvent().asCharacters().getData();
		TextElementData data;
		try {
			data = new TextElementData(Double.parseDouble(value));
		}
		catch (NumberFormatException e) {
			data = new TextElementData(value);
		}
		map.put(ID_PREFIX + keyPrefix + element.getName().getLocalPart(), data);
  	reader.nextEvent();
	}
	
	
	private void readTaxonomy(Node node) throws XMLStreamException {
    XMLEvent event = reader.nextEvent();
    while (event.getEventType() != XMLStreamConstants.END_ELEMENT) {
      if (event.getEventType() == XMLStreamConstants.START_ELEMENT) {
      	StartElement element = event.asStartElement();
        if (element.getName().equals(TAG_CODE) || element.getName().equals(TAG_SCIENTIFIC_NAME)	|| 
        		element.getName().equals(TAG_RANK)	|| element.getName().equals(TAG_COMMON_NAME)	|| 
        		element.getName().equals(TAG_AUTHORITY)	|| element.getName().equals(TAG_SYNONYM)) {
        	
        	storeHiddenData(node.getHiddenDataMap(), TAG_TAXONOMY.getLocalPart().toString() + ".", element);
        }
        else {  // evtl. zus�tzlich vorhandenes Element, dass nicht gelesen wird
          XMLUtils.reachElementEnd(reader);  
        }
      }
      event = reader.nextEvent();
    }
	}
	
	
	private void readSequence(StartElement rootElement, Node node) throws XMLStreamException {
		String type = XMLUtils.readStringAttr(rootElement, ATTR_TYPE, null);
		if (type != null) {
			node.getHiddenDataMap().put(ID_PREFIX + TAG_SEQUENCE.getLocalPart().toString() + "." + 
					ATTR_TYPE.toString(), new TextElementData(type));
		}
		
    XMLEvent event = reader.nextEvent();
    while (event.getEventType() != XMLStreamConstants.END_ELEMENT) {
      if (event.getEventType() == XMLStreamConstants.START_ELEMENT) {
      	StartElement element = event.asStartElement();
        if (element.getName().equals(TAG_SYMBOL) || element.getName().equals(TAG_ACCESSION)
        		|| element.getName().equals(TAG_NAME) || element.getName().equals(TAG_LOCATION)) {
        	
        	storeHiddenData(node.getHiddenDataMap(), TAG_SEQUENCE.getLocalPart().toString() + ".", element);
        }
        else {  // evtl. zus�tzlich vorhandenes Element, dass nicht gelesen wird
          XMLUtils.reachElementEnd(reader);  
        }
      }
      event = reader.nextEvent();
    }
	}
	
	
	private void readDistribution(Node node) throws XMLStreamException {
    XMLEvent event = reader.nextEvent();
    while (event.getEventType() != XMLStreamConstants.END_ELEMENT) {
      if (event.getEventType() == XMLStreamConstants.START_ELEMENT) {
      	StartElement element = event.asStartElement();
        if (element.getName().equals(TAG_DESCRIPTION)) {
        	storeHiddenData(node.getHiddenDataMap(), TAG_DISTRIBUTION.getLocalPart().toString() + ".", 
        			element);
        }
        else {  // evtl. zus�tzlich vorhandenes Element, dass nicht gelesen wird
          XMLUtils.reachElementEnd(reader);  
        }
      }
      event = reader.nextEvent();
    }
	}
	
	
	private void readDate(Node node) throws XMLStreamException {
    XMLEvent event = reader.nextEvent();
    while (event.getEventType() != XMLStreamConstants.END_ELEMENT) {
      if (event.getEventType() == XMLStreamConstants.START_ELEMENT) {
      	StartElement element = event.asStartElement();
        if (element.getName().equals(TAG_DESCRIPTION) || element.getName().equals(TAG_VALUE) || 
        		element.getName().equals(TAG_MIN) || element.getName().equals(TAG_MAX)) {
        	
        	storeHiddenData(node.getHiddenDataMap(), TAG_DATE.getLocalPart().toString() + ".", element);
        }
        else {  // evtl. zus�tzlich vorhandenes Element, dass nicht gelesen wird
          XMLUtils.reachElementEnd(reader);  
        }
      }
      event = reader.nextEvent();
    }
	}
	
	
	private Node readSubtree(StartElement rootElement, Node parent) throws XMLStreamException {
		Node result = Node.newInstanceWithBranch();
		Branch b = result.getAfferentBranch();
		b.setLength(XMLUtils.readDoubleAttr(rootElement, ATTR_BRANCH_LENGTH, b.getLength()));  // Format erlaubt Angabe �ber Attribut oder Tag.
		
    XMLEvent event = reader.nextEvent();
    int confidenceCount = 0;
    boolean colorDefined = false;
    while (event.getEventType() != XMLStreamConstants.END_ELEMENT) {
      if (event.getEventType() == XMLStreamConstants.START_ELEMENT) {
      	StartElement element = event.asStartElement();
        if (element.getName().equals(TAG_NAME)) {
        	storeHiddenData(result.getHiddenDataMap(), "", element);
        }
        else if (element.getName().equals(TAG_BRANCH_LENGTH)) {
        	b.setLength(Double.parseDouble(
        			reader.nextEvent().asCharacters().getData()));
          reader.nextEvent();
        }
        else if (element.getName().equals(TAG_CONFIDENCE)) {
        	TextLabel l = new TextLabel(result.getAfferentBranch().getLabels());
        	l.setID(XMLUtils.readStringAttr(element, ATTR_TYPE, DEFAULT_CONFIDENCE_NAME + confidenceCount));
        	try {
        		l.getData().setDecimal(Double.parseDouble(reader.nextEvent().asCharacters().getData()));
        		result.getAfferentBranch().getLabels().add(l);
        	}
        	catch (NumberFormatException e) {}  // Ignore non numeric confidence values (would be invalid XML)
        	
        	confidenceCount++;
          reader.nextEvent();
        }
        else if (element.getName().equals(TAG_LINE_WIDTH)) {
        	double width = Double.parseDouble(reader.nextEvent().asCharacters().getData());
        	b.getHiddenDataMap().put(
        			BRANCH_WIDT_DATA_NAME, new TextElementData(width));  // Astdicke wird f�r evtl. Reskalierungen zus�tzlich als hidden node data gespeichert.
        	b.getFormats().getLineWidth().setInMillimeters((float)width);  //TODO Welche Einheit hat die hier gelesene Astdicke?
          reader.nextEvent();
        }
        else if (element.getName().equals(TAG_LINE_COLOR)) {
        	Color color = readColor();
        	result.getFormats().setLineColor(color);
        	b.getFormats().setLineColor(color);
        	colorDefined = true;
        }
        else if (element.getName().equals(TAG_TAXONOMY)) {
        	readTaxonomy(result);
        }
        else if (element.getName().equals(TAG_SEQUENCE)) {
        	readSequence(element, result);
        }
        else if (element.getName().equals(TAG_DISTRIBUTION)) {
        	readDistribution(result);
        }
        else if (element.getName().equals(TAG_DATE)) {
        	readDate(result);
        }
        else if (element.getName().equals(TAG_CLADE)) {
        	Node subelement = readSubtree(element, result);
        	subelement.setParent(result);
        	result.getChildren().add(subelement);
        }
        else {  // evtl. zus�tzlich vorhandenes Element, dass nicht gelesen wird
          XMLUtils.reachElementEnd(reader);  
        }
      }
      event = reader.nextEvent();
    }
    
    // Node name belegen:
    TextElementData data = result.getHiddenDataMap().get(ID_PREFIX + TAG_NAME.getLocalPart());
    if (data == null) {
    	data = result.getHiddenDataMap().get(ID_PREFIX + TAG_TAXONOMY.getLocalPart() + "." + 
    			TAG_SCIENTIFIC_NAME.getLocalPart());
    	if (data == null) {
      	data = result.getHiddenDataMap().get(ID_PREFIX + TAG_TAXONOMY.getLocalPart() + "." + 
      			TAG_COMMON_NAME.getLocalPart());
      	if (data == null) {
        	data = result.getHiddenDataMap().get(ID_PREFIX + TAG_SEQUENCE.getLocalPart() + "." + 
        			TAG_NAME.getLocalPart());
      	}
    	}
    }
    if (data != null) {
    	result.getData().assign(data);
    }
    
    // Formate des Elternelements �bernehmen:
    if (parent != null) {
    	if (b.getHiddenDataMap().get(BRANCH_WIDT_DATA_NAME) == null) {
    		data = parent.getAfferentBranch().getHiddenDataMap().get(BRANCH_WIDT_DATA_NAME); 
    		if (data != null) {
      		b.getHiddenDataMap().put(BRANCH_WIDT_DATA_NAME, new TextElementData(data.getDecimal()));
      		b.getFormats().getLineWidth().setInMillimeters((float)data.getDecimal());
    		}
    		
    		if (!colorDefined) {
    			Color parentColor = parent.getFormats().getLineColor();
    			result.getFormats().setLineColor(parentColor);
    			b.getFormats().setLineColor(parentColor);
    		}
    	}
    }
    
		return result;
	}
  
  
	public Tree readNextTree(XMLEventReader reader, StartElement rootElement) throws XMLStreamException {
		this.reader = reader;
		Tree result = readPhylogeny(rootElement);
		names.clear();
		return result;
	}
	
	
	private Tree readPhylogeny(StartElement rootElement) throws XMLStreamException {
		names.add(DEFAULT_TREE_NAME + phylogenies.size());

		Tree result = new Tree();
		result.getFormats().setShowRooted(
        XMLUtils.readBooleanAttr(rootElement, ATTR_ROOTED, GlobalFormats.DEFAULT_SHOW_ROOTED));
		result.getScaleBar().getData().setText(
        XMLUtils.readStringAttr(rootElement, ATTR_BRANCH_LENGTH_UNIT, ""));
		
    XMLEvent event = reader.nextEvent();
    while (event.getEventType() != XMLStreamConstants.END_ELEMENT) {
      if (event.getEventType() == XMLStreamConstants.START_ELEMENT) {
      	StartElement element = event.asStartElement();
        if (element.getName().equals(TAG_NAME)) {
        	names.set(names.size() - 1, reader.nextEvent().asCharacters().getData());
          reader.nextEvent();
        }
        else if (element.getName().equals(TAG_CLADE)) {
        	result.setPaintStart(readSubtree(element, null));
        	result.assignUniqueNames();  // If nodes without an unique name were present.
        	result.updateElementSet();
          reader.nextEvent();
        }
        else {  // evtl. zus�tzlich vorhandenes Element, dass nicht gelesen wird
          XMLUtils.reachElementEnd(reader);  
        }
      }
      event = reader.nextEvent();
    }
    
  	branchLengthsScaler.setDefaultAverageScale(phylogenies.lastElement());
  	return result;
  }
	
	
	private void readDocument(StartElement rootElement) throws XMLStreamException {
    XMLEvent event = reader.nextEvent();
    while (event.getEventType() != XMLStreamConstants.END_ELEMENT) {
      if (event.getEventType() == XMLStreamConstants.START_ELEMENT) {
      	StartElement element = event.asStartElement();
        if (element.getName().equals(TAG_PHYLOGENY)) {
      		phylogenies.add(readPhylogeny(element));
        }
        else {  // evtl. zus�tzlich vorhandenes Element, dass nicht gelesen wird
          XMLUtils.reachElementEnd(reader);  
        }
      }
      event = reader.nextEvent();
    }
  }
	
	
	@Override
  public Document readDocument(BufferedInputStream stream) throws Exception {
		reader = XMLInputFactory.newInstance().createXMLEventReader(stream);
		try {
			XMLEvent event;
			while (reader.hasNext()) {
	      event = reader.nextEvent();
	      switch (event.getEventType()) {
	        case XMLStreamConstants.START_DOCUMENT:
	          document = createEmptyDocument();
	          break;
	        case XMLStreamConstants.END_DOCUMENT:
	          reader.close();
	          Tree tree = phylogenies.get(parameterMap.getTreeSelector().select(names.toArray(new String[names.size()]), phylogenies));
	          AutoPositionLabelsEdit.position(tree.getPaintStart());  // Label gleichm��ig anordnen
	          document.setTree(tree);
	          phylogenies.clear();
	          return document;
	        case XMLStreamConstants.START_ELEMENT:
	        	StartElement element = event.asStartElement();
	          if (element.getName().equals(TAG_ROOT)) {
	        	  readDocument(element);
	          }
	          else {
	            XMLUtils.reachElementEnd(reader);  
	          }
	          break;
	      }
	    }
		}
		finally {
	    reader.close();
	    stream.close();
		}
    phylogenies.clear();
		return null;
	}


	@Override
  public DocumentIterator createIterator(BufferedInputStream stream) throws Exception {
	  return null;
  }
}