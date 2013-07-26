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
package info.bioinfweb.treegraph.document.io.xtg;


import info.bioinfweb.treegraph.document.*;
import info.bioinfweb.treegraph.document.format.*;
import info.bioinfweb.treegraph.document.io.AbstractDocumentReader;
import info.bioinfweb.treegraph.document.io.DocumentIterator;
import info.bioinfweb.treegraph.document.io.SingleDocumentIterator;
import info.bioinfweb.treegraph.gui.mainframe.MainFrame;
import info.webinsel.util.io.FormatVersion;
import info.webinsel.util.io.InvalidXSDPathException;
import info.webinsel.util.io.XMLUtils;

import java.io.BufferedInputStream;
import java.text.DecimalFormat;
import java.util.Locale;

import javax.swing.JOptionPane;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.stream.*;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;



/**
 * Reads the TreeGraph 2 specific XML format (*.xtg).<br>
 * If the document contains additional (not defined tags) they are ignored. Note that they 
 * can not transfered to the internal data structure and therefor a not written back to a 
 * document generated with TreeGraph 2.<br>
 * If defined attributes are missing in the document, standard values are used.
 * 
 * @author Ben St&ouml;ver
 */
public class XTGReader extends AbstractDocumentReader implements XTGConstants {
  private XMLEventReader reader;
  
  
	public XTGReader() {
		super(true);
	}
	
	
	private void readDistanceValueAttr(DistanceValue value, StartElement element, QName name) {
		Attribute attr = element.getAttributeByName(name);
		if (attr != null) {
			try {
				value.setInMillimeters(Float.parseFloat(attr.getValue()));
			}
			catch (NumberFormatException e) {
				loadLogger.addWarning("The distance value attribute \"" + attr.getValue() + "\" is malformed. A default " +
						"value was used instead.");
			}
		}
	}
	
	
	private void readTextElementDataAttr(TextElementData data, StartElement element) {
		String text = XMLUtils.readStringAttr(element, ATTR_TEXT, null);
		if (text == null) {
			data.clear();
		}
		else {
			if (XMLUtils.readBooleanAttr(element, ATTR_TEXT_IS_DECIMAL, false)) {  // Ist das Attribut nicht angegeben, wird von einem String ausgegangen.
				try {
					data.setDecimal(Double.parseDouble(text));
				}
				catch (NumberFormatException e) {
					loadLogger.addWarning("The decimal value attribute \"" + text + "\" is malformed. It was imported as a " +
							"textual value instead.");
					data.setText(text);
				}
			}
			else {
				data.setText(text);
			}
		}
	}
	
	
  private void readDistanceDimensionAttr(DistanceDimension d, StartElement element) {
  	readDistanceValueAttr(d.getWidth(), element, ATTR_WIDTH);
  	readDistanceValueAttr(d.getHeight(), element, ATTR_HEIGHT);
  }
  
  
  /**
   * @param m
   * @param element
   * @throws XMLStreamException
   * @since 2.0.41
   */
  private void readMargin(Margin m, StartElement element) throws XMLStreamException {
  	readDistanceValueAttr(m.getLeft(), element, ATTR_LEFT);
  	readDistanceValueAttr(m.getTop(), element, ATTR_TOP);
  	readDistanceValueAttr(m.getRight(), element, ATTR_RIGHT);
  	readDistanceValueAttr(m.getBottom(), element, ATTR_BOTTOM);
  	
  	reachElementEnd(reader, "XML elements under <" + TAG_LABEL_MARGIN + ">");
  }
	
	
  private void readGlobalFormats(StartElement rootElement) throws XMLStreamException {
  	GlobalFormats f = document.getTree().getFormats(); 
  	f.setBackgroundColor(XMLUtils.readColorAttr(rootElement, ATTR_BG_COLOR, f.getBackgroundColor()));
		readDistanceValueAttr(f.getBranchLengthScale(), rootElement, ATTR_BRANCH_LENGTH_SCALE);
		f.setShowScaleBar(XMLUtils.readBooleanAttr(rootElement, ATTR_SHOW_SCALE_BAR, f.getShowScaleBar()));
		f.setShowRooted(XMLUtils.readBooleanAttr(rootElement, ATTR_SHOW_ROOTED, f.getShowRooted()));
		f.setAlignLegendsToSubtree(XMLUtils.readBooleanAttr(rootElement, ATTR_ALIGN_TO_SUBTREE,	false));  // Default value is false because this feature did not exists in previous versions.
		f.setAlignLegendsToSubtree(XMLUtils.readBooleanAttr(rootElement, ATTR_POSITION_LABELS_TO_LEFT,	
				f.getPositionLabelsToLeft()));
		
		XMLEvent event = reader.nextEvent();
    while (event.getEventType() != XMLStreamConstants.END_ELEMENT) {
      if (event.getEventType() == XMLStreamConstants.START_ELEMENT) {
      	StartElement element = event.asStartElement();
        if (element.getName().getLocalPart().equals(TAG_DOC_MARGIN)) {
        	readMargin(f.getDocumentMargin(), element);
        }
        else {  // evtl. zusätzlich vorhandenes Element, dass nicht gelesen wird
          reachElementEnd(reader, element);  
        }
      }
      event = reader.nextEvent();
    }
  }
	
	
	private void readTextStyleAttr(TextFormats f, StartElement element) {
		Attribute attr = element.getAttributeByName(ATTR_TEXT_STYLE);
		if (attr != null) {
			String str = attr.getValue().toLowerCase();
			f.setTextStyle(TextFormats.PLAIN);
			if (str.contains(STYLE_BOLD)) {
				f.addTextStyle(TextFormats.BOLD);
			}
			if (str.contains(STYLE_ITALIC)) {
				f.addTextStyle(TextFormats.ITALIC);
			}
			if (str.contains(STYLE_UNDERLINE)) {
				f.addTextStyle(TextFormats.UNDERLINE);
			}
			// Add more TextStyles here
		}
	}
	
	
	private void readDecimalFormat(TextFormats f, StartElement element, String prefix) {
		DecimalFormat format = new DecimalFormat(
				XMLUtils.readStringAttr(element, new QName(prefix + ATTR_DECIMAL_FORMAT.toString()), 
    				TextFormats.DEFAULT_DECIMAL_FORMAT_EXPR));
		
		Locale locale;
		String lang = XMLUtils.readStringAttr(element, new QName(prefix + ATTR_LOCALE_LANG), null); 
		if (lang == null) {
			locale = TextFormats.DEFAULT_LOCALE;
		}
		else {
			String country = XMLUtils.readStringAttr(element, new QName(prefix + ATTR_LOCALE_COUNTRY), null);
			if (country == null) {
				locale = new Locale(lang);
			}
			else {
				String variant = XMLUtils.readStringAttr(element, new QName(prefix + ATTR_LOCALE_VARIANT), null);
				if (variant == null) {
					locale = new Locale(lang, country);
				}
				else {
					locale = new Locale(lang, country, variant);
				}
			}
		}
		f.setDecimalFormat(format, locale);
	}
	
	
	private void readTextFormatsAttr(TextFormats f, StartElement element, String prefix) {
		f.setTextColor(XMLUtils.readColorAttr(element, new QName(prefix + ATTR_TEXT_COLOR.toString()), f.getTextColor()));
		readDistanceValueAttr(f.getTextHeight(), element, new QName(prefix + ATTR_TEXT_HEIGHT.toString()));
		readTextStyleAttr(f, element);
		f.setFontName(XMLUtils.readStringAttr(element, new QName(prefix + ATTR_FONT_FAMILY.toString()), f.getFontName()));
		readDecimalFormat(f, element, prefix);
	}
	
	
	private void readLineAttr(LineFormats f, StartElement element) {
		readDistanceValueAttr(f.getLineWidth(), element, ATTR_LINE_WIDTH);
		f.setLineColor(XMLUtils.readColorAttr(element, ATTR_LINE_COLOR, f.getLineColor()));
	}
	
	
	private void readBranch(StartElement rootElement, Branch b) throws XMLStreamException {
		BranchFormats f = b.getFormats();
		b.setLength(XMLUtils.readDoubleAttr(rootElement, ATTR_BRANCH_LENGTH, b.getLength()));
		readLineAttr(f, rootElement);
		readDistanceValueAttr(f.getMinLength(), rootElement, ATTR_MIN_BRANCH_LENGTH);		
		readDistanceValueAttr(f.getMinSpaceAbove(), rootElement, ATTR_MIN_SPACE_ABOVE);		
		readDistanceValueAttr(f.getMinSpaceBelow(), rootElement, ATTR_MIN_SPACE_BELOW);		
		f.setConstantWidth(XMLUtils.readBooleanAttr(rootElement, ATTR_CONSTANT_WIDTH, f.isConstantWidth()));

		XMLEvent event = reader.nextEvent();
    while (event.getEventType() != XMLStreamConstants.END_ELEMENT) {
      if (event.getEventType() == XMLStreamConstants.START_ELEMENT) {
      	StartElement element = event.asStartElement();
        if (element.getName().getLocalPart().equals(TAG_TEXT_LABEL)) {
        	readTextLabel(element, b.getLabels());
        }
        else if (element.getName().getLocalPart().equals(TAG_ICON_LABEL)) {
        	readIconLabel(element, b.getLabels());
        }
        else if (element.getName().getLocalPart().equals(TAG_PIE_CHART_LABEL)) {
        	readPieChartLabel(element, b.getLabels());
        }
        else if (element.getName().getLocalPart().equals(TAG_HIDDEN_DATA)) {
        	readHiddenData(element, b.getHiddenDataMap());
        }
        else {  // evtl. zusätzlich vorhandenes Element, dass nicht gelesen wird
          reachElementEnd(reader, element);  
        }
      }
      event = reader.nextEvent();
    }
	}
	
	
	private void readPieChartIDs(PieChartLabel l) throws XMLStreamException {
    XMLEvent event = reader.nextEvent();
    int index = 0;
    while (event.getEventType() != XMLStreamConstants.END_ELEMENT) {
      if (event.getEventType() == XMLStreamConstants.START_ELEMENT) {
      	StartElement element = event.asStartElement();
        if (element.getName().getLocalPart().equals(TAG_PIE_CHART_ID)) {
        	l.getFormats().setPieColor(index, XMLUtils.readColorAttr(element, ATTR_PIE_COLOR, 
              l.getFormats().getPieColor(index)));
        	l.addValueID(reader.nextEvent().asCharacters().getData());
          reachElementEnd(reader, element);
          index++;
        }
        else {  // evtl. zusätzlich vorhandenes Element, dass nicht gelesen wird
          reachElementEnd(reader, element);
        }
      }
      event = reader.nextEvent();
    }
	}
	
	
	/**
	 * This method also reads the subelements of the root element. Therefor no more attributes can be read 
	 * after the call of this method.
	 * @param rootElement
	 * @param label
	 */
	private void readLabelData(StartElement rootElement, Label l) throws XMLStreamException {
		l.setID(XMLUtils.readStringAttr(rootElement, ATTR_ID, l.getID()));
		
		LabelFormats f = l.getFormats();
    f.setAbove(XMLUtils.readBooleanAttr(rootElement, ATTR_LABEL_ABOVE, f.isAbove()));
    f.setLineNumber(XMLUtils.readIntAttr(rootElement, ATTR_LINE_NO, f.getLineNumber()));
    f.setLinePosition(XMLUtils.readDoubleAttr(rootElement, ATTR_LINE_POS, f.getLinePosition()));
    
    XMLEvent event = reader.nextEvent();
    while (event.getEventType() != XMLStreamConstants.END_ELEMENT) {
      if (event.getEventType() == XMLStreamConstants.START_ELEMENT) {
      	StartElement element = event.asStartElement();
        if (element.getName().getLocalPart().equals(TAG_LABEL_MARGIN)) {
        	readMargin(f.getMargin(), element);
        }
        else if (element.getName().getLocalPart().equals(TAG_PIE_CHART_IDS) && 
        		(l instanceof PieChartLabel)) {
        	
        	readPieChartIDs((PieChartLabel)l);
        }
        else {  // evtl. zusätzlich vorhandenes Element, dass nicht gelesen wird
          reachElementEnd(reader, element);  
        }
      }
      event = reader.nextEvent();
    }
	}
	
	
	private void readTextLabel(StartElement rootElement, Labels labels) throws XMLStreamException {
		TextLabel l = new TextLabel(null);
		
    readTextElementDataAttr(l.getData(), rootElement);
    readTextFormatsAttr(l.getFormats(), rootElement, "");
    
    readLabelData(rootElement, l);
    labels.add(l);  // label.labels wird hier automatisch gesetzt
	}
	
	
	private void readGraphicalLabelDimensions(GraphicalLabelFormats f, StartElement element) {
  	readDistanceValueAttr(f.getWidth(), element, ATTR_LABEL_WIDTH);
    readDistanceValueAttr(f.getHeight(), element, ATTR_LABEL_HEIGHT);
	}
	
	
  private void readIconLabel(StartElement rootElement, Labels labels) throws XMLStreamException { 
		IconLabel l = new IconLabel(null);
		IconLabelFormats f = l.getFormats();
		
    readLineAttr(f, rootElement);
    f.setIcon(XMLUtils.readStringAttr(rootElement, ATTR_ICON, f.getIcon()));
    if ((document.getVersion() == null) || VERSION.geraterThan(document.getVersion())) {
    	readDistanceValueAttr(f.getWidth(), rootElement, ATTR_ICON_WIDTH);
      readDistanceValueAttr(f.getHeight(), rootElement, ATTR_ICON_HEIGHT);
    }
    else {
    	readGraphicalLabelDimensions(f, rootElement);
    }
    f.setIconFilled(XMLUtils.readBooleanAttr(rootElement, ATTR_ICON_FILLED, f.getIconFilled()));
    
    readLabelData(rootElement, l);
    labels.add(l);  // label.labels wird hier automatisch gesetzt
  }
  
  
  private void readPieChartLabel(StartElement rootElement, Labels labels) throws XMLStreamException { 
		PieChartLabel l = new PieChartLabel(null);
		PieChartLabelFormats f = l.getFormats();
		
    readLineAttr(f, rootElement);
  	readGraphicalLabelDimensions(f, rootElement);
    f.setShowInternalLines(XMLUtils.readBooleanAttr(rootElement, ATTR_SHOW_INTERNAL_LINES, 
    		f.getShowInternalLines()));
    f.setShowNullLines(XMLUtils.readBooleanAttr(rootElement, ATTR_SHOW_NULL_LINES, 
    		f.getShowNullLines()));
    
    readLabelData(rootElement, l);
    labels.add(l);  // label.labels wird hier automatisch gesetzt
  }
  
  
	private void readHiddenData(StartElement element, HiddenDataMap list) throws XMLStreamException {
		String id = XMLUtils.readStringAttr(element, ATTR_ID, null);
		if (id != null) {
			TextElementData data = new TextElementData();
			readTextElementDataAttr(data, element);
			list.put(id, data);
		}
		
  	reachElementEnd(reader, "XML elements under <" + TAG_HIDDEN_DATA + ">");
	}
	
	
	private Node readSubtree(StartElement rootElement) throws XMLStreamException {
		Node result = Node.getInstanceWithBranch();
		NodeFormats f = result.getFormats();
		
    readTextElementDataAttr(result.getData(), rootElement);
		result.setUniqueName(XMLUtils.readStringAttr(rootElement, ATTR_UNIQUE_NAME, result.getUniqueName()));
		readTextFormatsAttr(result.getFormats(), rootElement, "");
		readLineAttr(f, rootElement);
		readDistanceValueAttr(f.getCornerRadius(), rootElement, ATTR_EDGE_RADIUS);
		
    XMLEvent event = reader.nextEvent();
    while (event.getEventType() != XMLStreamConstants.END_ELEMENT) {
      if (event.getEventType() == XMLStreamConstants.START_ELEMENT) {
      	StartElement element = event.asStartElement();
        if (element.getName().getLocalPart().equals(TAG_BRANCH)) {
         	readBranch(element, result.getAfferentBranch());
        }
        else if (element.getName().getLocalPart().equals(TAG_TEXT_LABEL)) {  // Wird nur zur Abwärtskompatibilität auch hier noch gelesen. Sollte jetzt nur noch unterhalb von Branch stehen.
        	readTextLabel(element, result.getAfferentBranch().getLabels());
        }
        else if (element.getName().getLocalPart().equals(TAG_HIDDEN_DATA)) {
        	readHiddenData(element, result.getHiddenDataMap());
        }
        else if (element.getName().getLocalPart().equals(TAG_LEAF_MARGIN)) {
        	readMargin(f.getLeafMargin(), element);
        }
        else if (element.getName().getLocalPart().equals(TAG_NODE)) {
        	Node subelement = readSubtree(element);
        	subelement.setParent(result);
        	result.getChildren().add(subelement);
        }
        else {  // evtl. zusätzlich vorhandenes Element, dass nicht gelesen wird
          reachElementEnd(reader, element);  
        }
      }
      event = reader.nextEvent();
    }
		return result;
	}
  
  
	private void readScaleValueAttr(ScaleValue value, StartElement element, QName name) {
		Attribute attr = element.getAttributeByName(name);
		if (attr != null) {
			String text = attr.getValue();
			if (text.endsWith(BRANCH_LENGTH_UNITS)) {
				value.setInMillimeters(Float.parseFloat(
						text.substring(0, text.length() - BRANCH_LENGTH_UNITS.length())));
			}
			else {
				value.setInMillimeters(Float.parseFloat(
						text.substring(0, text.length() - MILLIMETERS.length())));
			}
		}
	}
	
	
	private void readScaleBar(StartElement element, ScaleBar s) throws XMLStreamException {
		ScaleBarFormats f = s.getFormats();
		
		s.getData().setText(XMLUtils.readStringAttr(element, ATTR_TEXT, s.getData().getText()));
		
		readLineAttr(f, element);
		readTextFormatsAttr(f, element, "");
		
		String align = XMLUtils.readStringAttr(element, ATTR_SCALE_BAR_ALIGN, ALIGN_LEFT);
		if (align.equals(ALIGN_LEFT)) {
			f.setAlignment(ScaleAlignment.LEFT);
		}
		else if (align.equals(ALIGN_RIGHT)) {
			f.setAlignment(ScaleAlignment.RIGHT);
		}
		else {  // ALIGN_TREE_WIDTH
			f.setAlignment(ScaleAlignment.TREE_WIDTH);
		}
		
		readDistanceValueAttr(f.getTreeDistance(), element, ATTR_SCALE_BAR_DISTANCE);
		readScaleValueAttr(f.getWidth(), element, ATTR_SCALE_BAR_WIDTH);
		readDistanceValueAttr(f.getTreeDistance(), element, ATTR_SCALE_BAR_HEIGHT);
		f.setSmallInterval(XMLUtils.readFloatAttr(element, ATTR_SMALL_INTERVAL, f.getSmallInterval()));
		f.setLongInterval(XMLUtils.readIntAttr(element, ATTR_LONG_INTERVAL, f.getLongInterval()));
		f.setStartLeft(XMLUtils.readBooleanAttr(element, ATTR_SCALE_BAR_START, f.isStartLeft()));
		f.setIncreasing(XMLUtils.readBooleanAttr(element, ATTR_SCALE_BAR_INCREASE, f.isIncreasing()));
		
    reachElementEnd(reader, "XML elements under <" + TAG_SCALE_BAR + ">");  // Necessary for future subelements of <ScaleBar>
	}
	
	
  private void readAnchor(StartElement rootElement, Legend l, int no) {
  	String uniqueName = XMLUtils.readStringAttr(rootElement, new QName(PRE_LEGEND_ANCHOR + no), null);
  	if (uniqueName != null) {
  		Node anchor = document.getTree().getNodeByUniqueName(uniqueName.toLowerCase());
  		if (anchor == null) {
  			anchor = document.getTree().getPaintStart();  // Willkürlich gewählter Anker, falls Knoten mit dem entsprechenden Namen fehlt.
  			JOptionPane.showMessageDialog(MainFrame.getInstance(), "The legend \"" + l.getData() + "\" refers to a node with the unique name \"" + uniqueName + "\" as anchor which does not exist in this document. The root node was set as the new achor.\n(This problem is probably the result of an incorrect manual edit of the document.)", "Missing anchor", JOptionPane.WARNING_MESSAGE);
  		}
  		l.getFormats().setAnchor(no, anchor);
  	}
  }
	
	
	private void readLegend(StartElement rootElement, Legends legends) throws XMLStreamException {
  	Legend legend = new Legend(legends);
  	LegendFormats f = legend.getFormats();
  	
    readTextElementDataAttr(legend.getData(), rootElement);
		readAnchor(rootElement, legend, 0);
		readAnchor(rootElement, legend, 1);
		
		String style = XMLUtils.readStringAttr(rootElement, ATTR_LEGEND_STYLE, STYLE_BRACKET);
		if (style.equals(STYLE_BRACE)) {
			f.setLegendStyle(LegendStyle.BRACE);  // LegendStyle.BRACE ist Startwert.  
		}
		
		String orientation = XMLUtils.readStringAttr(rootElement, ATTR_TEXT_ORIENTATION, ORIENT_UP);
		if (orientation.equals(ORIENT_DOWN)) {
			f.setOrientation(TextOrientation.DOWN);
		}
		else if (orientation.equals(ORIENT_HORIZONTAL)) {
			f.setOrientation(TextOrientation.HORIZONTAL);
		}
		else {
			f.setOrientation(TextOrientation.UP);
		}

		f.setPosition(XMLUtils.readIntAttr(rootElement, ATTR_LEGEND_POS, f.getPosition()));
		readDistanceValueAttr(f.getMinTreeDistance(), rootElement, ATTR_MIN_TREE_DISTANCE);
		readDistanceValueAttr(f.getSpacing(), rootElement, ATTR_LEGEND_SPACING);
		readLineAttr(f, rootElement);
		readTextFormatsAttr(f, rootElement, "");
		readDistanceValueAttr(f.getCornerRadius(), rootElement, ATTR_EDGE_RADIUS);
		
    XMLEvent event = reader.nextEvent();
    while (event.getEventType() != XMLStreamConstants.END_ELEMENT) {
      if (event.getEventType() == XMLStreamConstants.START_ELEMENT) {
      	StartElement element = event.asStartElement();
        if (element.getName().getLocalPart().equals(TAG_LEGEND_MARGIN)) {
        	readMargin(f.getMargin(), element);
        }
        else {  // evtl. zusätzlich vorhandenes Element, dass nicht gelesen wird
          reachElementEnd(reader, element);  
        }
      }
      event = reader.nextEvent();
    }
		
  	legends.insert(legend);
  }
	
	
	private void readTree(StartElement rootElement) throws XMLStreamException {
    XMLEvent event = reader.nextEvent();
    while (event.getEventType() != XMLStreamConstants.END_ELEMENT) {
      if (event.getEventType() == XMLStreamConstants.START_ELEMENT) {
      	StartElement element = event.asStartElement();
        if (element.getName().getLocalPart().equals(TAG_NODE)) {
        	document.getTree().setPaintStart(readSubtree(element));
        	document.getTree().assignUniqueNames();  // If nodes without an unique name were present.
        	document.getTree().updateElementSet();
        }
        else if (element.getName().getLocalPart().equals(TAG_SCALE_BAR)) {
        	readScaleBar(element, document.getTree().getScaleBar());
        }
        else if (element.getName().getLocalPart().equals(TAG_LEGEND)) {
          readLegend(element, document.getTree().getLegends());
        }
        else {  // evtl. zusätzlich vorhandenes Element, dass nicht gelesen wird
          reachElementEnd(reader, element);  
        }
      }
      event = reader.nextEvent();
    }
  }
	
	
	private void readDocument(StartElement rootElement) throws XMLStreamException {
    XMLEvent event = reader.nextEvent();
    while (event.getEventType() != XMLStreamConstants.END_ELEMENT) {
      if (event.getEventType() == XMLStreamConstants.START_ELEMENT) {
      	StartElement element = event.asStartElement();
        if (element.getName().getLocalPart().equals(TAG_GLOBAL_FORMATS)) {
         	readGlobalFormats(element);
          //event = reader.nextEvent();
        }
        else if (element.getName().getLocalPart().equals(TAG_TREE)) {
        	readTree(element);
        }
        else {  // evtl. zusätzlich vorhandenes Element, dass nicht gelesen wird
          reachElementEnd(reader, element);  
        }
      }
      event = reader.nextEvent();
    }
  }
	
	
	/**
	 * Outputs warnings about the XML namespace and sets the version of <code>document</code>.
	 * @param element
	 */
	private void checkNameSpace(StartElement element) {
		String namespace = element.getNamespaceContext().getNamespaceURI(XMLConstants.DEFAULT_NS_PREFIX);
		if (namespace == null) {
			loadLogger.addWarning("No XML namespace declared. Assuming " + NAMESPACE_URI + ".");
		}
		else if (!namespace.equals(NAMESPACE_URI)) {
			loadLogger.addWarning("Declared namespace is not " + NAMESPACE_URI + ". Trying to read as " + 
					NAMESPACE_URI + " anyway.");
		}
		
		try {
			FormatVersion version = XMLUtils.extractFormatVersion(element);
			if (version == null) {
				loadLogger.addWarning("No XML schema location found. Assuming \"" + FULL_SCHEMA_LOCATION + "\".");
			}
			else {
				document.setVersion(version);
				
				if (VERSION.compareTo(version) < 0) {
					loadLogger.addWarning("The source file is version " + version + ", whereas this application only " +
							"supports XTG version " + VERSION + " or below. Some elements might not be imported correctly. You " +
							"should consider to upgrade to the latest version of TreeGraph 2.");
				}
			}
		}
		catch (InvalidXSDPathException e) {
			loadLogger.addWarning("The XML schema location is malformed. Assuming \"" + FULL_SCHEMA_LOCATION + "\".");
		}
	}
  
  
	@Override
	public Document readDocument(BufferedInputStream stream) throws Exception {
		document = null;
		reader = XMLInputFactory.newInstance().createXMLEventReader(stream);
		
		try {
			XMLEvent event;
			while (reader.hasNext())
	    {
	      event = reader.nextEvent();
	      switch (event.getEventType()) {
	        case XMLStreamConstants.START_DOCUMENT:
	          document = new Document();
	          break;
	        case XMLStreamConstants.END_DOCUMENT:
	          reader.close();
	          return document;
	        case XMLStreamConstants.START_ELEMENT:
	        	StartElement element = event.asStartElement();
	          if (element.getName().getLocalPart().equals(TAG_ROOT)) {
	          	checkNameSpace(element);
	        	  readDocument(element);
	          }
	          else {
	            reachElementEnd(reader, element);  
	          }
	          break;
	      }
	    }
		}
		finally {
	    reader.close();
	    stream.close();
		}
		return null;
	}


	@Override
  public DocumentIterator createIterator(BufferedInputStream stream) throws Exception {
		return new SingleDocumentIterator(read(stream));
  }
}