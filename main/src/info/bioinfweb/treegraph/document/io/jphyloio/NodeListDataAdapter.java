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
package info.bioinfweb.treegraph.document.io.jphyloio;


import java.io.IOException;

import info.bioinfweb.commons.io.W3CXSConstants;
import info.bioinfweb.jphyloio.ReadWriteConstants;
import info.bioinfweb.jphyloio.ReadWriteParameterMap;
import info.bioinfweb.jphyloio.ReadWriteParameterNames;
import info.bioinfweb.jphyloio.dataadapters.JPhyloIOEventReceiver;
import info.bioinfweb.jphyloio.events.ConcreteJPhyloIOEvent;
import info.bioinfweb.jphyloio.events.NodeEvent;
import info.bioinfweb.jphyloio.events.meta.ResourceMetadataEvent;
import info.bioinfweb.jphyloio.events.meta.URIOrStringIdentifier;
import info.bioinfweb.jphyloio.events.type.EventContentType;
import info.bioinfweb.jphyloio.utils.JPhyloIOWritingUtils;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.Tree;
import info.bioinfweb.treegraph.document.io.xtg.XTGConstants;



public class NodeListDataAdapter extends AbstractNodeEdgeListDataAdapter<NodeEvent> implements ReadWriteConstants, ReadWriteParameterNames, XTGConstants {
	private int sequentialNumber = 0;
	
	
	public NodeListDataAdapter(Tree treeModel) {
		super(DEFAULT_NODE_ID_PREFIX, treeModel);
	}
	
	
	private int getSequentialNumber() {
		return sequentialNumber++; //Int doesn't start at 1, seems to be the number of metadata to write * the number of nodes present. Possible fix?
	}
	
	
	@Override
	protected NodeEvent createEvent(String id, Node node) {
		return new NodeEvent(id, node.getData().toString(), null, node.getParent() == null);
	}
	
	
	@Override
	protected void writeContentData(ReadWriteParameterMap parameters, JPhyloIOEventReceiver receiver, String id, Node node)
			throws IOException, IllegalArgumentException {
		
		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, id + "_" + DEFAULT_META_ID_PREFIX + getSequentialNumber(), null,
					info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_NODE_ATTR_IS_DECIMAL, W3CXSConstants.DATA_TYPE_BOOLEAN, node.getData().isDecimal(), null);

		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, id + "_" + DEFAULT_META_ID_PREFIX + getSequentialNumber(), null,
				info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_NODE_ATTR_TEXT_COLOR, info.bioinfweb.jphyloio.formats.xtg.XTGConstants.DATA_TYPE_COLOR, node.getFormats().getTextColor(), null);

		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, id + "_" + DEFAULT_META_ID_PREFIX + getSequentialNumber(), null,
				info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_NODE_ATTR_TEXT_HEIGHT, W3CXSConstants.DATA_TYPE_FLOAT, node.getFormats().getTextHeight().getInMillimeters(), null);
		
		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, id + "_" + DEFAULT_META_ID_PREFIX + getSequentialNumber(), null,
				info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_NODE_ATTR_TEXT_STYLE, W3CXSConstants.DATA_TYPE_STRING, node.getFormats().getTextStyle(), null);
		
		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, id + "_" + DEFAULT_META_ID_PREFIX + getSequentialNumber(), null,
				info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_NODE_ATTR_FONT_FAMILY, W3CXSConstants.DATA_TYPE_STRING, node.getFormats().getFontName(), null);
		
		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, id + "_" + DEFAULT_META_ID_PREFIX + getSequentialNumber(), null,
				info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_NODE_ATTR_DECIMAL_FORMAT, W3CXSConstants.DATA_TYPE_STRING, node.getData().getDecimal(), null);
		
		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, id + "_" + DEFAULT_META_ID_PREFIX + getSequentialNumber(), null,
				info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_NODE_ATTR_LOCALE_LANG, W3CXSConstants.DATA_TYPE_STRING, node.getFormats().getLocale().getLanguage(), null);
		
		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, id + "_" + DEFAULT_META_ID_PREFIX + getSequentialNumber(), null,
				info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_NODE_ATTR_LOCALE_COUNTRY, W3CXSConstants.DATA_TYPE_STRING, node.getFormats().getLocale().getCountry(), null);
			
		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, id + "_" + DEFAULT_META_ID_PREFIX + getSequentialNumber(), null,
				info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_NODE_ATTR_LOCALE_VARIANT, W3CXSConstants.DATA_TYPE_STRING, node.getFormats().getLocale().getVariant(), null);
		
		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, id + "_" + DEFAULT_META_ID_PREFIX + getSequentialNumber(), null,
				info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_NODE_ATTR_LINE_COLOR, info.bioinfweb.jphyloio.formats.xtg.XTGConstants.DATA_TYPE_COLOR, node.getFormats().getLineColor(), null);
		
		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, id + "_" + DEFAULT_META_ID_PREFIX + getSequentialNumber(), null,
				info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_NODE_ATTR_LINE_WIDTH, W3CXSConstants.DATA_TYPE_FLOAT, node.getFormats().getLineWidth().getInMillimeters(), null);
		
		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, id + "_" + DEFAULT_META_ID_PREFIX + getSequentialNumber(), null,
				info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_NODE_ATTR_UNIQUE_NAME, W3CXSConstants.DATA_TYPE_STRING, node.getUniqueName(), null);

		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, id + "_" + DEFAULT_META_ID_PREFIX + getSequentialNumber(), null,
				info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_NODE_ATTR_EDGE_RADIUS, W3CXSConstants.DATA_TYPE_FLOAT, node.getFormats().getCornerRadius().getInMillimeters(), null);
				
		
		receiver.add(new ResourceMetadataEvent(id + "_" + DEFAULT_META_ID_PREFIX + getSequentialNumber(), null, new URIOrStringIdentifier(null, info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_LEAF_MARGIN), null, null));
		
		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, id + "_" + DEFAULT_META_ID_PREFIX + getSequentialNumber(), null,
				info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_LEAF_MARGIN_ATTR_LEFT, W3CXSConstants.DATA_TYPE_FLOAT, node.getFormats().getLeafMargin().getLeft().getInMillimeters(), null);
		
		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, id + "_" + DEFAULT_META_ID_PREFIX + getSequentialNumber(), null,
				info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_LEAF_MARGIN_ATTR_TOP, W3CXSConstants.DATA_TYPE_FLOAT, node.getFormats().getLeafMargin().getTop().getInMillimeters(), null);
		
		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, id + "_" + DEFAULT_META_ID_PREFIX + getSequentialNumber(), null,
				info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_LEAF_MARGIN_ATTR_RIGHT, W3CXSConstants.DATA_TYPE_FLOAT, node.getFormats().getLeafMargin().getRight().getInMillimeters(), null);
		
		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, id + "_" + DEFAULT_META_ID_PREFIX + getSequentialNumber(), null,
				info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_LEAF_MARGIN_ATTR_BOTTOM, W3CXSConstants.DATA_TYPE_FLOAT, node.getFormats().getLeafMargin().getBottom().getInMillimeters(), null);
		
		receiver.add(ConcreteJPhyloIOEvent.createEndEvent(EventContentType.RESOURCE_META));
		
		
		receiver.add(new ResourceMetadataEvent(id + "_" + DEFAULT_META_ID_PREFIX + getSequentialNumber(), null, new URIOrStringIdentifier(null, info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_INVISIBLE_DATA), null, null));
		
		//TODO Add proper Object value.
//		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, id + "_" + DEFAULT_META_ID_PREFIX + getSequentialNumber(), null,
//				info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_INVISIBLE_DATA_ATTR_TEXT, W3CXSConstants.DATA_TYPE_DOUBLE, , null);
		
		//TODO Add proper Object value.
//		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, id + "_" + DEFAULT_META_ID_PREFIX + getSequentialNumber(), null,
//				info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_INVISIBLE_DATA_ATTR_ID, W3CXSConstants.DATA_TYPE_STRING, node.getHiddenDataMap()., null);
		
		receiver.add(ConcreteJPhyloIOEvent.createEndEvent(EventContentType.RESOURCE_META));
	}
}
