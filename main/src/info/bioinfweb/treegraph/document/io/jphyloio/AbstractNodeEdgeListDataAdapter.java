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

import info.bioinfweb.jphyloio.ReadWriteParameterMap;
import info.bioinfweb.jphyloio.dataadapters.JPhyloIOEventReceiver;
import info.bioinfweb.jphyloio.dataadapters.ObjectListDataAdapter;
import info.bioinfweb.jphyloio.events.LabeledIDEvent;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.NodeType;
import info.bioinfweb.treegraph.document.Tree;
import info.bioinfweb.treegraph.document.tools.TreeSerializer;



public abstract class AbstractNodeEdgeListDataAdapter<E extends LabeledIDEvent> implements ObjectListDataAdapter<E> {
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
}
