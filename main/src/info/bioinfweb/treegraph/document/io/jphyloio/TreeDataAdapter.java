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

import info.bioinfweb.commons.IntegerIDManager;
import info.bioinfweb.commons.io.W3CXSConstants;
import info.bioinfweb.jphyloio.ReadWriteConstants;
import info.bioinfweb.jphyloio.ReadWriteParameterMap;
import info.bioinfweb.jphyloio.dataadapters.JPhyloIOEventReceiver;
import info.bioinfweb.jphyloio.dataadapters.ObjectListDataAdapter;
import info.bioinfweb.jphyloio.dataadapters.TreeNetworkDataAdapter;
import info.bioinfweb.jphyloio.dataadapters.implementations.NoSetsTreeNetworkDataAdapter;
import info.bioinfweb.jphyloio.events.ConcreteJPhyloIOEvent;
import info.bioinfweb.jphyloio.events.EdgeEvent;
import info.bioinfweb.jphyloio.events.LabeledIDEvent;
import info.bioinfweb.jphyloio.events.NodeEvent;
import info.bioinfweb.jphyloio.events.meta.ResourceMetadataEvent;
import info.bioinfweb.jphyloio.events.meta.URIOrStringIdentifier;
import info.bioinfweb.jphyloio.events.type.EventContentType;
import info.bioinfweb.jphyloio.utils.JPhyloIOWritingUtils;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.Tree;
import info.bioinfweb.treegraph.document.io.xtg.XTGConstants;



public class TreeDataAdapter extends NoSetsTreeNetworkDataAdapter implements TreeNetworkDataAdapter, XTGConstants, ReadWriteConstants {	

	private Tree treeModel;
	private ObjectListDataAdapter<NodeEvent> nodeList;
	private ObjectListDataAdapter<EdgeEvent> edgeList;
	
	
	public TreeDataAdapter(Tree treeModel) {		
		nodeList = new NodeListDataAdapter(treeModel);
		edgeList = new EdgeListDataAdapter(treeModel);
		this.treeModel = treeModel;
	}
	
	//creates ID to add to unique name of meta event.
	public static String createMetaID(String id, IntegerIDManager idManager) {		
		return id + "_" + DEFAULT_META_ID_PREFIX + idManager.createNewID();
	}
	
	
	@Override
	public void writeMetadata(ReadWriteParameterMap parameters, JPhyloIOEventReceiver receiver) throws IOException {
		IntegerIDManager idManager = new IntegerIDManager();

		receiver.add(new ResourceMetadataEvent(TreeDataAdapter.createMetaID(DEFAULT_TREE_ID_PREFIX, idManager), null, new URIOrStringIdentifier(null, info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_GLOBAL_FORMATS), null, null));		
		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(DEFAULT_TREE_ID_PREFIX, idManager), null,
				info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_GLOBAL_FORMATS_ATTR_BG_COLOR, info.bioinfweb.jphyloio.formats.xtg.XTGConstants.DATA_TYPE_COLOR, treeModel.getFormats().getBackgroundColor(), null);
		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(DEFAULT_TREE_ID_PREFIX, idManager), null,
				info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_GLOBAL_FORMATS_ATTR_BRANCH_LENGTH_SCALE, W3CXSConstants.DATA_TYPE_DOUBLE, treeModel.getFormats().getBranchLengthScale().getInMillimeters(), null);
		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(DEFAULT_TREE_ID_PREFIX, idManager), null,
				info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_GLOBAL_FORMATS_ATTR_SHOW_SCALE_BAR, W3CXSConstants.DATA_TYPE_BOOLEAN, treeModel.getFormats().getShowScaleBar(), null);
		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(DEFAULT_TREE_ID_PREFIX, idManager), null,
				info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_GLOBAL_FORMATS_ATTR_ALIGN_TO_SUBTREE, W3CXSConstants.DATA_TYPE_BOOLEAN, treeModel.getFormats().getAlignLegendsToSubtree(), null);
		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(DEFAULT_TREE_ID_PREFIX, idManager), null,
				info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_GLOBAL_FORMATS_ATTR_POSITION_LABELS_TO_LEFT, W3CXSConstants.DATA_TYPE_BOOLEAN, treeModel.getFormats().getPositionLabelsToLeft(), null);
		
		receiver.add(new ResourceMetadataEvent(TreeDataAdapter.createMetaID(DEFAULT_TREE_ID_PREFIX, idManager), null, new URIOrStringIdentifier(null, info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_DOCUMENT_MARGIN), null, null));
		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(DEFAULT_TREE_ID_PREFIX, idManager), null,
				info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_DOCUMENT_MARGIN_ATTR_LEFT, W3CXSConstants.DATA_TYPE_FLOAT, treeModel.getFormats().getDocumentMargin().getLeft().getInMillimeters(), null);
		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(DEFAULT_TREE_ID_PREFIX, idManager), null,
				info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_DOCUMENT_MARGIN_ATTR_TOP, W3CXSConstants.DATA_TYPE_FLOAT, treeModel.getFormats().getDocumentMargin().getTop().getInMillimeters(), null);
		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(DEFAULT_TREE_ID_PREFIX, idManager), null,
				info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_DOCUMENT_MARGIN_ATTR_RIGHT, W3CXSConstants.DATA_TYPE_FLOAT, treeModel.getFormats().getDocumentMargin().getRight().getInMillimeters(), null);
		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(DEFAULT_TREE_ID_PREFIX, idManager), null,
				info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_DOCUMENT_MARGIN_ATTR_BOTTOM, W3CXSConstants.DATA_TYPE_FLOAT, treeModel.getFormats().getDocumentMargin().getBottom().getInMillimeters(), null);
		receiver.add(ConcreteJPhyloIOEvent.createEndEvent(EventContentType.RESOURCE_META));
		receiver.add(ConcreteJPhyloIOEvent.createEndEvent(EventContentType.RESOURCE_META));
		
		
//		receiver.add(new ResourceMetadataEvent(TreeDataAdapter.createMetaID(DEFAULT_TREE_ID_PREFIX, idManager), null, new URIOrStringIdentifier(null, info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_NODE_BRANCH_DATA_ADAPTERS), null, null));	
//		
//		receiver.add(new ResourceMetadataEvent(TreeDataAdapter.createMetaID(DEFAULT_TREE_ID_PREFIX, idManager), null, new URIOrStringIdentifier(null, info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_NODE_BRANCH_DATA_ADAPTERS_ADAPTER), null, null));		
//		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(DEFAULT_TREE_ID_PREFIX, idManager), null,
//				info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_NODE_BRANCH_DATA_ADAPTERS_ADAPTER_ATTR_ID, W3CXSConstants.DATA_TYPE_STRING, , null);		
//			JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(DEFAULT_TREE_ID_PREFIX, idManager), null,
//					info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_NODE_BRANCH_DATA_ADAPTERS_ADAPTER_ATTR_NAME, W3CXSConstants.DATA_TYPE_STRING, , null);
//			JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(DEFAULT_TREE_ID_PREFIX, idManager), null,
//					info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_NODE_BRANCH_DATA_ADAPTERS_ADAPTER_ATTR_PURPOSE, W3CXSConstants.DATA_TYPE_STRING, , null);
//		receiver.add(ConcreteJPhyloIOEvent.createEndEvent(EventContentType.RESOURCE_META));
//		
//		receiver.add(ConcreteJPhyloIOEvent.createEndEvent(EventContentType.RESOURCE_META));
	}

	
	@Override
	public LabeledIDEvent getStartEvent(ReadWriteParameterMap parameters) {
		return new LabeledIDEvent(EventContentType.TREE, DEFAULT_TREE_ID_PREFIX, null);
	}

	
	@Override
	public ObjectListDataAdapter<EdgeEvent> getEdges(ReadWriteParameterMap parameters) {
		return edgeList;
	}

	
	@Override
	public ObjectListDataAdapter<NodeEvent> getNodes(ReadWriteParameterMap parameters) {
		return nodeList;
	}

	
	@Override
	public boolean isTree(ReadWriteParameterMap parameters) {
		return true;
	}	
}
