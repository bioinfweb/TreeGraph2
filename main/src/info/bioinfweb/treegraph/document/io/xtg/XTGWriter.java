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
package info.bioinfweb.treegraph.document.io.xtg;


import info.bioinfweb.treegraph.document.*;
import info.bioinfweb.treegraph.document.format.*;
import info.bioinfweb.treegraph.document.io.AbstractDocumentWriter;
import info.bioinfweb.treegraph.document.io.DocumentWriter;
import info.bioinfweb.treegraph.document.io.ReadWriteParameterMap;
import info.bioinfweb.treegraph.document.nodebranchdata.IDElementAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.commons.io.XMLUtils;

import java.awt.Color;
import java.io.OutputStream;
import java.util.Iterator;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;



public class XTGWriter extends AbstractDocumentWriter implements XTGConstants, DocumentWriter {
	public static final String STREAM_ENCODING = "UTF8";
	public static String XML_VERSION = "1.0";
	public static String XML_ENCODING = "UTF-8";
	
	
	private XMLStreamWriter writer = null;
	
	
	private String formatColorByte(int value) {
		String result = String.format("%x", value).toUpperCase();
		if (result.length() == 1) {
			result = "0" + result;
		}
		return result;
	}
	
	
	private String formatColor(Color color) {
		return "#" + formatColorByte(color.getRed()) + formatColorByte(color.getGreen()) + formatColorByte(color.getBlue());  
	}
	
	
	private String formatTextStyle(TextFormats f) {
		String result = "";
		if (f.hasTextStyle(TextFormats.BOLD)) {
			result += STYLE_BOLD;
		}
		if (f.hasTextStyle(TextFormats.ITALIC)) {
			result += STYLE_ITALIC;
		}
		if (f.hasTextStyle(TextFormats.UNDERLINE)) {
			result += STYLE_UNDERLINE;
		}
		// More TextStyles can be added here.
		return result;
	}
	
	
	private void writeTextElementData(TextElementData data) throws XMLStreamException {
		if (!data.isEmpty()) {
			writer.writeAttribute(ATTR_TEXT.toString(), data.toString());
			writer.writeAttribute(ATTR_TEXT_IS_DECIMAL.toString(), "" + data.isDecimal());
		}
	}
	
	
	private void writeDistanceDimension(String name, DistanceDimension d) throws XMLStreamException {
		writer.writeStartElement(name);
		writer.writeAttribute(ATTR_WIDTH.toString(), "" + d.getWidth().getInMillimeters());
		writer.writeAttribute(ATTR_HEIGHT.toString(), "" + d.getHeight().getInMillimeters());
		writer.writeEndElement();
	}
	
	
	private void writeAdapter(Document document, NodeBranchDataAdapter adapter, String purpose) throws XMLStreamException {
		writer.writeStartElement(TAG_ADAPTER);
		writer.writeAttribute(ATTR_ADAPTER_NAME.toString(), adapter.getName());
		if (adapter instanceof IDElementAdapter) {
			writer.writeAttribute(ATTR_ADAPTER_ID.toString(), ((IDElementAdapter)adapter).getID());		
		}
		writer.writeAttribute(ATTR_ADAPTER_PURPOSE.toString(), purpose);		
		writer.writeEndElement();
	}
	
	
	private void writeNodeBranchDataAdapters(Document document) throws XMLStreamException {
		writer.writeStartElement(TAG_NODE_BRANCH_DATA_ADAPTERS.toString());
		writeAdapter(document, document.getDefaultLeafAdapter(), VALUE_LEAVES_ADAPTER);
		writeAdapter(document, document.getDefaultSupportAdapter(), VALUE_SUPPORT_VALUES_ADAPTER);
		writer.writeEndElement();
	}
	
	
	private void writeMargin(String name, Margin m) throws XMLStreamException {
		writer.writeStartElement(name);
		writer.writeAttribute(ATTR_LEFT.toString(), "" + m.getLeft().getInMillimeters());
		writer.writeAttribute(ATTR_TOP.toString(), "" + m.getTop().getInMillimeters());
		writer.writeAttribute(ATTR_RIGHT.toString(), "" + m.getRight().getInMillimeters());
		writer.writeAttribute(ATTR_BOTTOM.toString(), "" + m.getBottom().getInMillimeters());
		writer.writeEndElement();
	}
	
	
	private void writeGlobalFormats(GlobalFormats f) throws XMLStreamException {
		writer.writeStartElement(TAG_GLOBAL_FORMATS.toString());
			writer.writeAttribute(ATTR_BG_COLOR.toString(), formatColor(f.getBackgroundColor()));
			writer.writeAttribute(ATTR_BRANCH_LENGTH_SCALE.toString(), "" + f.getBranchLengthScale().getInMillimeters());
			writer.writeAttribute(ATTR_SHOW_SCALE_BAR.toString(), "" + f.getShowScaleBar());
			writer.writeAttribute(ATTR_SHOW_ROOTED.toString(), "" + f.getShowRooted());
			writer.writeAttribute(ATTR_ALIGN_TO_SUBTREE.toString(), "" + f.getAlignLegendsToSubtree());
			writer.writeAttribute(ATTR_POSITION_LABELS_TO_LEFT.toString(), "" + f.getPositionLabelsToLeft());
			writeMargin(TAG_DOC_MARGIN.toString(), f.getDocumentMargin());
		writer.writeEndElement();
	}
	
	
	private void writeTextFormatsAttr(TextFormats f) throws XMLStreamException {
		writeTextFormatsAttr(f, "");
	}
	
	
	private void writeTextFormatsAttr(TextFormats f, String prefix) throws XMLStreamException {
  	writer.writeAttribute(prefix + ATTR_TEXT_COLOR.toString(), formatColor(f.getTextColor()));
  	writer.writeAttribute(prefix + ATTR_TEXT_HEIGHT.toString(), "" + f.getTextHeight().getInMillimeters());
  	writer.writeAttribute(prefix + ATTR_TEXT_STYLE.toString(), formatTextStyle(f));
  	writer.writeAttribute(prefix + ATTR_FONT_FAMILY.toString(), f.getFontName());
  	
  	writer.writeAttribute(prefix + ATTR_DECIMAL_FORMAT, f.getDecimalFormat().toPattern());
  	writer.writeAttribute(prefix + ATTR_LOCALE_LANG, f.getLocale().getLanguage());
  	writer.writeAttribute(prefix + ATTR_LOCALE_COUNTRY, f.getLocale().getCountry());
  	writer.writeAttribute(prefix + ATTR_LOCALE_VARIANT, f.getLocale().getVariant());
	}
	
	
	private void writeLineAttr(LineFormats f) throws XMLStreamException {
  	writer.writeAttribute(ATTR_LINE_COLOR.toString(), formatColor(f.getLineColor()));
  	writer.writeAttribute(ATTR_LINE_WIDTH.toString(), "" + f.getLineWidth().getInMillimeters());
	}
	
	
	private void writeLabelDimensions(GraphicalLabelFormats f) throws XMLStreamException {
  	writer.writeAttribute(ATTR_LABEL_WIDTH.toString(), "" + f.getWidth().getInMillimeters());
  	writer.writeAttribute(ATTR_LABEL_HEIGHT.toString(), "" + f.getHeight().getInMillimeters());
	}
	
	
	private void writeLabelBlock(Labels labels, boolean above) throws XMLStreamException {
		for (int lineNo = 0; lineNo < labels.lineCount(above); lineNo++) {
			for (int lineIndex = 0; lineIndex < labels.labelCount(above, lineNo); lineIndex++) {
				Label l = labels.get(above, lineNo, lineIndex);
	    	if (l instanceof TextLabel) {
	    		writer.writeStartElement(TAG_TEXT_LABEL);
			  	writeTextElementData(((TextElement)l).getData());
					writeTextFormatsAttr(((TextElement)l).getFormats());
	    	}
	    	else if (l instanceof IconLabel) {
	    		writer.writeStartElement(TAG_ICON_LABEL);
	    		IconLabelFormats f = ((IconLabel)l).getFormats();
					writeLineAttr(f);
		    	writeLabelDimensions(f);
	    		writer.writeAttribute(ATTR_ICON.toString(), f.getIcon());
		    	writer.writeAttribute(ATTR_ICON_FILLED.toString(), "" + f.getIconFilled());
	    	}
				else if (l instanceof PieChartLabel) {
					writer.writeStartElement(TAG_PIE_CHART_LABEL);
					PieChartLabelFormats f = ((PieChartLabel)l).getFormats();
			  	writeTextElementData(((TextElement)l).getData());
					writeTextFormatsAttr(f);
					writeLineAttr(f);
		    	writeLabelDimensions(f);
					writer.writeAttribute(ATTR_SHOW_INTERNAL_LINES.toString(), "" + f.isShowInternalLines());
					writer.writeAttribute(ATTR_SHOW_NULL_LINES.toString(), "" + f.isShowLinesForZero());
					writer.writeAttribute(ATTR_SHOW_TITLE.toString(), "" + f.isShowTitle());
					writer.writeAttribute(ATTR_CAPTION_TYPE.toString(), f.getCaptionsContentType().name());
					writer.writeAttribute(ATTR_CAPTION_LINK_TYPE.toString(), f.getCaptionsLinkType().name());
				}
				else {
					throw new InternalError("Unsupported label of type " + l.getClass().getCanonicalName() + " found.");
				}
	    	writer.writeAttribute(ATTR_ID.toString(), l.getID());
	    	writer.writeAttribute(ATTR_LABEL_ABOVE.toString(), "" + l.getFormats().isAbove());
	    	writer.writeAttribute(ATTR_LINE_NO.toString(), "" + l.getFormats().getLineNumber());
	    	writer.writeAttribute(ATTR_LINE_POS.toString(), "" + l.getFormats().getLinePosition());
	    	
	    	writeMargin(TAG_LABEL_MARGIN.toString(), l.getFormats().getMargin());
	    	
	    	if (l instanceof PieChartLabel) {
	    		PieChartLabel pieChartLabel = (PieChartLabel)l;
	    		writer.writeStartElement(TAG_PIE_CHART_IDS.toString());
					writeTextFormatsAttr(pieChartLabel.getFormats().getCaptionsTextFormats());
	    		for (int i = 0; i < pieChartLabel.getSectionDataList().size(); i++) {
		    		writer.writeStartElement(TAG_PIE_CHART_ID.toString());
			    	writer.writeAttribute(ATTR_PIE_COLOR.toString(), "" + 
			    			formatColor(pieChartLabel.getFormats().getPieColor(i)));
			    	
			    	PieChartLabel.SectionData data = pieChartLabel.getSectionDataList().get(i);
			    	writer.writeAttribute(ATTR_PIE_CAPTION.toString(), data.getCaption()); 
						writer.writeCharacters(data.getValueColumnID());
		    		writer.writeEndElement();
					}
	    		writer.writeEndElement();
	    	}
	    	
	    	writer.writeEndElement();
			}
		}
	}
	
	
	private void writeHiddenDataMap(HiddenDataMap m) throws XMLStreamException {
		Iterator<String> iterator = m.idIterator();
		while (iterator.hasNext()) {
			writer.writeStartElement(TAG_HIDDEN_DATA.toString());
			String key = iterator.next();
    	writer.writeAttribute(ATTR_ID.toString(), key);
    	writeTextElementData(m.get(key));
    	writer.writeEndElement();
		}
	}
	
	
	private void writeSubtree(Node root) throws XMLStreamException {
		writer.writeStartElement(TAG_NODE.toString());

		// Node data:
		writeTextElementData(root.getData());
  	if (root.hasUniqueName()) {
  		writer.writeAttribute(ATTR_UNIQUE_NAME.toString(), root.getUniqueName());
  	}
		writeTextFormatsAttr(root.getFormats());
		writeLineAttr(root.getFormats());
  	writer.writeAttribute(ATTR_EDGE_RADIUS.toString(), "" + root.getFormats().getCornerRadius().getInMillimeters());
  	
  	writeMargin(TAG_LEAF_MARGIN.toString(), root.getFormats().getLeafMargin());
  	
  	if (root.hasAfferentBranch()) {
    	// Branch:
    	Branch b = root.getAfferentBranch();
  		BranchFormats f = b.getFormats();
  		writer.writeStartElement(TAG_BRANCH.toString());
  		if (b.hasLength()) {
  			writer.writeAttribute(ATTR_BRANCH_LENGTH.toString(), "" + b.getLength());
  		}
  		writeLineAttr(f);
  		writer.writeAttribute(ATTR_CONSTANT_WIDTH.toString(), "" + f.isConstantWidth());
  		writer.writeAttribute(ATTR_MIN_BRANCH_LENGTH.toString(), "" + f.getMinLength().getInMillimeters());
  		writer.writeAttribute(ATTR_MIN_SPACE_ABOVE.toString(), "" + f.getMinSpaceAbove().getInMillimeters());
  		writer.writeAttribute(ATTR_MIN_SPACE_BELOW.toString(), "" + f.getMinSpaceBelow().getInMillimeters());
  		
    	// Labels:
    	writeLabelBlock(b.getLabels(), true);
    	writeLabelBlock(b.getLabels(), false);
    	
    	writeHiddenDataMap(b.getHiddenDataMap());
    	
    	writer.writeEndElement();
    	
    	
    	// Branch data:
  	}
  	writeHiddenDataMap(root.getHiddenDataMap());
  	
  	// Subnodes:
  	for (int i = 0; i < root.getChildren().size(); i++) {
			writeSubtree(root.getChildren().get(i));
		}
	  
  	writer.writeEndElement();
	}
	
	
	private void writeScaleValue(String name, ScaleValue v) throws XMLStreamException {
		String s = "" + v.getStoredValue();
		if (v.isInScaleUnits()) {
			s += BRANCH_LENGTH_UNITS;
		}
		else {
			s += MILLIMETERS;
		}
		writer.writeAttribute(name, s);
	}
	
	
	private void writeScaleBar(ScaleBar sb) throws XMLStreamException {
		ScaleBarFormats f = sb.getFormats();
		writer.writeStartElement(TAG_SCALE_BAR.toString());
  	writeTextElementData(sb.getData());
  	
  	writeLineAttr(f);
  	writeTextFormatsAttr(f);
  	
  	String align;
  	switch (f.getAlignment()) {
  		case LEFT:
  			align = ALIGN_LEFT;
  			break;
  		case RIGHT:
  			align = ALIGN_RIGHT;
  			break;
  		default:  // TREE_WIDTH
  			align = ALIGN_TREE_WIDTH;
  			break;
  	}
  	writer.writeAttribute(ATTR_SCALE_BAR_ALIGN.toString(), align);
  	
		writer.writeAttribute(ATTR_SCALE_BAR_DISTANCE.toString(), "" + f.getTreeDistance().getInMillimeters());
		writeScaleValue(ATTR_SCALE_BAR_WIDTH.toString(), f.getWidth());
		writer.writeAttribute(ATTR_SCALE_BAR_HEIGHT.toString(), "" + f.getHeight().getInMillimeters());
		writer.writeAttribute(ATTR_SMALL_INTERVAL.toString(), "" + f.getSmallInterval());
		writer.writeAttribute(ATTR_LONG_INTERVAL.toString(), "" + f.getLongInterval());
		writer.writeAttribute(ATTR_SCALE_BAR_START.toString(), "" + f.isStartLeft());
		writer.writeAttribute(ATTR_SCALE_BAR_INCREASE.toString(), "" + f.isIncreasing());
		
  	writer.writeEndElement();
	}
	
	
  private void writeAnchor(LegendFormats f, int no) throws XMLStreamException {
  	Node anchor = f.getAnchor(no);
  	if (anchor != null) {
	  	writer.writeAttribute(PRE_LEGEND_ANCHOR + no, anchor.getUniqueName());
  	}
  }
	
	
	private void writeLegends(Legends legends) throws XMLStreamException {
		for (int i = 0; i < legends.size(); i++) {
			Legend l = legends.get(i);
			LegendFormats f = l.getFormats();
			
			writer.writeStartElement(TAG_LEGEND.toString());
	  	writeTextElementData(l.getData());
	  	writeAnchor(f, 0);
	  	writeAnchor(f, 1);
	  	
	  	writer.writeAttribute(ATTR_LEGEND_POS.toString(), "" + f.getPosition());
	  	writer.writeAttribute(ATTR_MIN_TREE_DISTANCE.toString(), "" + f.getMinTreeDistance().getInMillimeters());
	  	writer.writeAttribute(ATTR_LEGEND_SPACING.toString(), "" + f.getSpacing().getInMillimeters());
	  	
			String style = STYLE_BRACE;
	  	if (f.getLegendStyle().equals(LegendStyle.BRACKET)) {
				style = STYLE_BRACKET;
			}
	  	writer.writeAttribute(ATTR_LEGEND_STYLE.toString(), style);
	  	
	  	String orientation;
	  	switch (f.getOrientation()) {
	  		case DOWN:
	  			orientation = ORIENT_DOWN;
	  			break;
	  		case HORIZONTAL:
	  			orientation = ORIENT_HORIZONTAL;
	  			break;
	  		default:
	  			orientation = ORIENT_UP;
	  			break;
	  	}
	  	writer.writeAttribute(ATTR_TEXT_ORIENTATION.toString(), orientation);
	  	
	  	writeLineAttr(f);
	  	writer.writeAttribute(ATTR_EDGE_RADIUS.toString(), "" + f.getCornerRadius().getInMillimeters());
	  	writeTextFormatsAttr(f);
	  	
	  	writeMargin(TAG_LEGEND_MARGIN.toString(), f.getMargin());
	  	
	  	writer.writeEndElement();
		}
	}
	
	
	public void write(Document document, OutputStream stream, ReadWriteParameterMap properties) throws Exception {
		try {
			writer = XMLOutputFactory.newInstance().createXMLStreamWriter(
					stream, STREAM_ENCODING);
			try {
				writer.writeStartDocument(XML_ENCODING, XML_VERSION);
				writer.setDefaultNamespace(NAMESPACE_URI);
				writer.writeStartElement(TAG_ROOT.toString());
				XMLUtils.writeNamespaceXSDAttr(writer, NAMESPACE_URI, NAMESPACE_URI + "/" + 
						VERSION + ".xsd");
				
			  writeGlobalFormats(document.getTree().getFormats());
			  writeNodeBranchDataAdapters(document);
			  //TODO write default adapters
				writer.writeStartElement(TAG_TREE.toString());
			  if (!document.getTree().isEmpty()) {
				  writeSubtree(document.getTree().getPaintStart());
				}
			  writeScaleBar(document.getTree().getScaleBar());
			  writeLegends(document.getTree().getLegends());
		  	writer.writeEndElement();
		  	
				writer.writeEndElement();
				writer.writeEndDocument();
			}
			finally {
				writer.close();
			}
		}
		finally {
			stream.close();
		}
	}
}