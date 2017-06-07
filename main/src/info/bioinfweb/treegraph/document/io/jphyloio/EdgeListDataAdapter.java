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

import javax.xml.stream.XMLStreamException;

import info.bioinfweb.commons.IntegerIDManager;
import info.bioinfweb.commons.io.W3CXSConstants;
import info.bioinfweb.jphyloio.ReadWriteConstants;
import info.bioinfweb.jphyloio.ReadWriteParameterMap;
import info.bioinfweb.jphyloio.ReadWriteParameterNames;
import info.bioinfweb.jphyloio.dataadapters.JPhyloIOEventReceiver;
import info.bioinfweb.jphyloio.events.ConcreteJPhyloIOEvent;
import info.bioinfweb.jphyloio.events.EdgeEvent;
import info.bioinfweb.jphyloio.events.meta.ResourceMetadataEvent;
import info.bioinfweb.jphyloio.events.meta.URIOrStringIdentifier;
import info.bioinfweb.jphyloio.events.type.EventContentType;
import info.bioinfweb.jphyloio.utils.JPhyloIOWritingUtils;
import info.bioinfweb.treegraph.document.Label;
import info.bioinfweb.treegraph.document.Labels;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.Tree;
import info.bioinfweb.treegraph.document.io.xtg.XTGConstants;



public class EdgeListDataAdapter extends AbstractNodeEdgeListDataAdapter<EdgeEvent> implements ReadWriteConstants, ReadWriteParameterNames, XTGConstants  {
	public EdgeListDataAdapter(Tree treeModel) {
		super(DEFAULT_EDGE_ID_PREFIX, treeModel);
	}


	@Override
	protected EdgeEvent createEvent(String id, Node node) {
		String sourceID = null;
		if (node.getParent() != null) {
			sourceID = DEFAULT_NODE_ID_PREFIX + node.getParent().getUniqueName();
		}
		return new EdgeEvent(id, null, sourceID, DEFAULT_NODE_ID_PREFIX + node.getUniqueName(), Double.NaN);
	}	
		
	
	@Override
	protected void writeContentData(ReadWriteParameterMap parameters, JPhyloIOEventReceiver receiver, String id, Node node)
			throws IOException, IllegalArgumentException {		
		
		IntegerIDManager idManager = new IntegerIDManager();
	
		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(id, idManager), null,
				info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_BRANCH_ATTR_LINE_COLOR, info.bioinfweb.jphyloio.formats.xtg.XTGConstants.DATA_TYPE_COLOR, node.getAfferentBranch().getFormats().getLineColor(), null);
			JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(id, idManager), null,
				info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_BRANCH_ATTR_LINE_WIDTH, W3CXSConstants.DATA_TYPE_FLOAT, node.getAfferentBranch().getFormats().getLineWidth().getInMillimeters(), null);
		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(id, idManager), null,
				info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_BRANCH_ATTR_CONSTANT_WIDTH, W3CXSConstants.DATA_TYPE_BOOLEAN, node.getAfferentBranch().getFormats().isConstantWidth(), null);	
		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(id, idManager), null,
				info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_BRANCH_ATTR_MIN_LENGTH, W3CXSConstants.DATA_TYPE_FLOAT, node.getAfferentBranch().getFormats().getMinLength().getInMillimeters(), null);
		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(id, idManager), null,
				info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_BRANCH_ATTR_MIN_SPACE_ABOVE, W3CXSConstants.DATA_TYPE_FLOAT, node.getAfferentBranch().getFormats().getMinSpaceAbove().getInMillimeters(), null);
		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(id, idManager), null,
				info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_BRANCH_ATTR_MIN_SPACE_BELOW, W3CXSConstants.DATA_TYPE_FLOAT, node.getAfferentBranch().getFormats().getMinSpaceBelow().getInMillimeters(), null);
	
			
		if (!node.getAfferentBranch().getLabels().isEmpty()) {
			receiver.add(new ResourceMetadataEvent(TreeDataAdapter.createMetaID(id, idManager), null, new URIOrStringIdentifier(null, info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_TEXT_LABEL), null, null));
			
//		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(id, idManager), null,
//				info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_TEXT_LABEL_ATTR_TEXT, W3CXSConstants.DATA_TYPE_STRING, , null);
//		
//		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(id, idManager), null,
//				info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_TEXT_LABEL_ATTR_TEXT_COLOR, info.bioinfweb.jphyloio.formats.xtg.XTGConstants.DATA_TYPE_COLOR, node.getAfferentBranch().getLabels(), null);
//		
//		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(id, idManager), null,
//				info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_TEXT_LABEL_ATTR_TEXT_HEIGHT, W3CXSConstants.DATA_TYPE_FLOAT, node.getAfferentBranch().getLabels()., null);
//		
//		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(id, idManager), null,
//				info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_TEXT_LABEL_ATTR_TEXT_STYLE, W3CXSConstants.DATA_TYPE_STRING, node.getAfferentBranch().getLabels()., null);
//		
//		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(id, idManager), null,
//				info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_TEXT_LABEL_ATTR_FONT_FAMILY, W3CXSConstants.DATA_TYPE_STRING, node.getAfferentBranch().getLabels()., null);
//		
//		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(id, idManager), null,
//				info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_TEXT_LABEL_ATTR_DECIMAL_FORMAT, W3CXSConstants.DATA_TYPE_STRING, node.getAfferentBranch().getLabels()., null);
//		
//		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(id, idManager), null,
//				info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_TEXT_LABEL_ATTR_LOCALE_LANG, W3CXSConstants.DATA_TYPE_STRING, node.getAfferentBranch().getLabels()., null);
//		
//		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(id, idManager), null,
//				info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_TEXT_LABEL_ATTR_LOCALE_COUNTRY, W3CXSConstants.DATA_TYPE_STRING, node.getAfferentBranch().getLabels()., null);
//		
//		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(id, idManager), null,
//				info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_TEXT_LABEL_ATTR_LOCALE_VARIANT, W3CXSConstants.DATA_TYPE_STRING, node.getAfferentBranch().getLabels()., null);
//		
//		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(id, idManager), null,
//				info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_TEXT_LABEL_ATTR_ID, W3CXSConstants.DATA_TYPE_STRING, node.getAfferentBranch().getLabels()., null);
//		
//		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(id, idManager), null,
//				info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_TEXT_LABEL_ATTR_ABOVE, W3CXSConstants.DATA_TYPE_BOOLEAN, , null);
//		
//		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(id, idManager), null,
//				info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_TEXT_LABEL_ATTR_LINE_NO, W3CXSConstants.DATA_TYPE_INT, node.getAfferentBranch().getLabels().getLineNo(above, label), null);
//		
//		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(id, idManager), null,
//				info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_TEXT_LABEL_ATTR_LINE_POS, W3CXSConstants.DATA_TYPE_DOUBLE, node.getAfferentBranch().getLabels().getLastLinePos(above, lineNumber), null);
		
		receiver.add(ConcreteJPhyloIOEvent.createEndEvent(EventContentType.RESOURCE_META));
		
		receiver.add(new ResourceMetadataEvent(TreeDataAdapter.createMetaID(id, idManager), null, new URIOrStringIdentifier(null, info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_LABEL_MARGIN), null, null));

		//TODO Add proper object values.		
//		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(id, idManager), null,
//				info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_LABEL_MARGIN_ATTR_LEFT, W3CXSConstants.DATA_TYPE_FLOAT, node.getAfferentBranch().getLabels(), null);
//
//		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(id, idManager), null,
//				info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_LABEL_MARGIN_ATTR_TOP, W3CXSConstants.DATA_TYPE_FLOAT, node.getAfferentBranch().getLabels(), null);
//		
//		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(id, idManager), null,
//				info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_LABEL_MARGIN_ATTR_RIGHT, W3CXSConstants.DATA_TYPE_FLOAT, node.getAfferentBranch().getLabels(), null);
//
//		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(id, idManager), null,
//				info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_LABEL_MARGIN_ATTR_BOTTOM, W3CXSConstants.DATA_TYPE_FLOAT, node.getAfferentBranch().getLabels(), null);

		receiver.add(ConcreteJPhyloIOEvent.createEndEvent(EventContentType.RESOURCE_META));
		}
		
		writeHiddenDataMap(receiver, id, node, idManager);		
	}
}
