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
	public NodeListDataAdapter(Tree treeModel) {
		super(DEFAULT_NODE_ID_PREFIX, treeModel);
	}
		
	
	@Override
	protected NodeEvent createEvent(String id, Node node) {
		return new NodeEvent(id, node.getData().toString(), null, node.getParent() == null);
	}
	
	
	@Override
	protected void writeContentData(ReadWriteParameterMap parameters, JPhyloIOEventReceiver receiver, String id, Node node)
			throws IOException, IllegalArgumentException {
		
		IntegerIDManager idManager = new IntegerIDManager();
		//Nodes
		receiver.add(new ResourceMetadataEvent(TreeDataAdapter.createMetaID(id, idManager), null, new URIOrStringIdentifier(null, info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_TEXT), null, null));
		TreeDataAdapter.writeTextAttributes(receiver, id, idManager, node);
		
		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(id, idManager), null,
					info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_IS_DECIMAL, W3CXSConstants.DATA_TYPE_BOOLEAN, 
					node.getData().isDecimal(), null);
		TreeDataAdapter.writeLineAtrributes(receiver, id, node, idManager);
		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(id, idManager), null,
				info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_NODE_ATTR_UNIQUE_NAME, W3CXSConstants.DATA_TYPE_STRING, 
				node.getUniqueName(), null);
		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(id, idManager), null,
				info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_NODE_ATTR_EDGE_RADIUS, W3CXSConstants.DATA_TYPE_FLOAT, 
				node.getFormats().getCornerRadius().getInMillimeters(), null);
				
		receiver.add(new ResourceMetadataEvent(TreeDataAdapter.createMetaID(id, idManager), null, new URIOrStringIdentifier(null, info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_LEAF_MARGIN), null, null));
		TreeDataAdapter.writeMargin(receiver, id, idManager, node.getFormats().getLeafMargin());		
		receiver.add(ConcreteJPhyloIOEvent.createEndEvent(EventContentType.RESOURCE_META));
		
		
		//Metadata
		writeMetadata(receiver, id, node, idManager);
	}
}
