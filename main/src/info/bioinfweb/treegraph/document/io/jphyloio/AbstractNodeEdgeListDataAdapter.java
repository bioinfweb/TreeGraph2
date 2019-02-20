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
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import info.bioinfweb.commons.IntegerIDManager;
import java.net.URI;
import java.net.URISyntaxException;

import info.bioinfweb.commons.io.W3CXSConstants;
import info.bioinfweb.jphyloio.ReadWriteConstants;
import info.bioinfweb.jphyloio.ReadWriteParameterMap;
import info.bioinfweb.jphyloio.dataadapters.JPhyloIOEventReceiver;
import info.bioinfweb.jphyloio.dataadapters.ObjectListDataAdapter;
import info.bioinfweb.jphyloio.events.ConcreteJPhyloIOEvent;
import info.bioinfweb.jphyloio.events.LabeledIDEvent;
import info.bioinfweb.jphyloio.events.meta.ResourceMetadataEvent;
import info.bioinfweb.jphyloio.events.meta.URIOrStringIdentifier;
import info.bioinfweb.jphyloio.events.type.EventContentType;
import info.bioinfweb.jphyloio.formats.xtg.XTGConstants;
import info.bioinfweb.jphyloio.utils.JPhyloIOWritingUtils;
import info.bioinfweb.treegraph.document.HiddenDataElement;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.NodeType;
import info.bioinfweb.treegraph.document.TextElementData;
import info.bioinfweb.treegraph.document.Tree;
import info.bioinfweb.treegraph.document.metadata.LiteralMetadataNode;
import info.bioinfweb.treegraph.document.metadata.MetadataNode;
import info.bioinfweb.treegraph.document.metadata.MetadataPath;
import info.bioinfweb.treegraph.document.metadata.ResourceMetadataNode;
import info.bioinfweb.treegraph.document.tools.MetadataTreeTools;
import info.bioinfweb.treegraph.document.tools.TreeSerializer;



public abstract class AbstractNodeEdgeListDataAdapter<E extends LabeledIDEvent> implements ObjectListDataAdapter<E>, ReadWriteConstants {
	private Tree treeModel;
	private String idPrefix;
	private List<Node> nodeList;
	
	
	public AbstractNodeEdgeListDataAdapter(String idPrefix, Tree treeModel) {
		this.idPrefix = idPrefix;
		this.treeModel = treeModel;
		nodeList = TreeSerializer.getElementsInSubtreeAsList(treeModel.getPaintStart(), NodeType.BOTH, Node.class);
	}
	
	
	protected String getIDPrefix() {
		return idPrefix;
	}
	
	
	protected abstract E createEvent(String id, Node node);


	protected String getIDByUniqeuName(String uniqueName) {
		return idPrefix + uniqueName;
	}
	
	@Override
	public long getCount(ReadWriteParameterMap parameters) {
		return treeModel.getNodeCount();
	}


	@Override
	public Iterator<String> getIDIterator(ReadWriteParameterMap parameters) {
		final Iterator<Node> iterator = nodeList.iterator();
		
		return new Iterator<String>() {
			@Override
			public boolean hasNext() {
				return iterator.hasNext();
			}

			
			@Override
			public String next() {
				return getIDByUniqeuName(iterator.next().getUniqueName());
			}
		};
	}

	
	private Node getNodeByID(String id) {
		return treeModel.getNodeByUniqueName(id.substring(idPrefix.length()));
	}
	

	@Override
	public E getObjectStartEvent(ReadWriteParameterMap parameters, String id) throws IllegalArgumentException {
		return createEvent(id, getNodeByID(id));
	}


	@Override
	public void writeContentData(ReadWriteParameterMap parameters, JPhyloIOEventReceiver receiver, String id)
			throws IOException, IllegalArgumentException {
		
		writeContentData(parameters, receiver, id, getNodeByID(id));
	}
	
	
	protected abstract void writeContentData(ReadWriteParameterMap parameters, JPhyloIOEventReceiver receiver, String id, Node node)
			throws IOException, IllegalArgumentException;
	
	
	protected void writeMetadata(JPhyloIOEventReceiver receiver, String id, HiddenDataElement node, IntegerIDManager idManager) throws IOException {		
		if (!node.getMetadataTree().getChildren().isEmpty()) {
			List<MetadataNode> list = node.getMetadataTree().getChildren();
			
			writeMetadataContent(receiver, id, list, idManager);
		}
	}


	private void writeMetadataContent(JPhyloIOEventReceiver receiver, String id, List<MetadataNode> list, IntegerIDManager idManager)
			throws IOException {
		
		for (MetadataNode child : list) {
			QName predicate = child.getPredicateOrRel();
			
			if (child instanceof ResourceMetadataNode) {
				URI hRef = ((ResourceMetadataNode)child).getURI();
//				if (hRef == null) {
//					try {
//						hRef = new URI("");
//					} catch (URISyntaxException e) {
//						throw new InternalError(e);
//					}
//				}
				
				if(predicate == null) {
					predicate = PREDICATE_HAS_RESOURCE_METADATA;
				}
				
				List<MetadataNode> childList = ((ResourceMetadataNode)child).getChildren();
				
				receiver.add(new ResourceMetadataEvent(TreeDataAdapter.createMetaID(id, idManager), null, new URIOrStringIdentifier(null, predicate), hRef, null));				
				
				if(!childList.isEmpty()) {
					writeMetadataContent(receiver, id, childList, idManager);					
				}
				receiver.add(ConcreteJPhyloIOEvent.createEndEvent(EventContentType.RESOURCE_META));
			}
			else {					
				TextElementData data = ((LiteralMetadataNode)child).getValue();
				Object value;
				if (data.isDecimal()) {
					value = data.getDecimal();
				}
				else {
					value = data.getText();
				}
				
				QName dataType = ((LiteralMetadataNode)child).getDatatype();
				if (dataType == null) {
					if ((data != null) && (data.isDecimal())) {
						dataType = W3CXSConstants.DATA_TYPE_DOUBLE;
					}
					else {
						dataType = W3CXSConstants.DATA_TYPE_STRING;
					}
				}
				
				if (predicate == null) {
					predicate = PREDICATE_HAS_LITERAL_METADATA;
				}
				
				JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(id, idManager), null, predicate, dataType, value, null);
			}			
		}
	}
}
