/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2015-2016  Ben Stöver, Sarah Wiechers, Kai Müller
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
package info.bioinfweb.treegraph.document.io.nexus;


import java.io.BufferedInputStream;

import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.io.DocumentIterator;
import info.bioinfweb.treegraph.document.io.ReadWriteParameterMap;
import info.bioinfweb.treegraph.document.io.TextStreamReader;
import info.bioinfweb.treegraph.document.io.newick.NewickReader;
import info.bioinfweb.treegraph.document.io.newick.NewickTreeList;
import info.bioinfweb.treegraph.document.nodebranchdata.BranchLengthAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeNameAdapter;



/**
 * This class reads a tree loads a tree out of a Nexus file. It uses a class that implements
 * {@link info.bioinfweb.treegraph.document.io.nexus.NexusTreeSelector} to choose which tree to load, 
 * if there are several stored in the Nexus file.
 * 
 * @author Ben St&ouml;ver
 */
public class NexusReader extends TextStreamReader {
	/**
	 * Constructs an instance of <code>NexusReader</code>.
	 */
	public NexusReader() {
		super();
	}
	
	
	@Override
	public Document readDocument(BufferedInputStream stream) throws Exception {
		NexusDocument nex = NexusParser.parse(readStream(stream));
		NewickTreeList trees = nex.createTrees(
				parameterMap.getNodeBranchDataAdapter(ReadWriteParameterMap.KEY_INTERNAL_NODE_NAMES_ADAPTER, 
						NodeNameAdapter.getSharedInstance()),
				parameterMap.getNodeBranchDataAdapter(ReadWriteParameterMap.KEY_BRANCH_LENGTH_ADAPTER, 
						BranchLengthAdapter.getSharedInstance()),
				parameterMap.getBoolean(ReadWriteParameterMap.KEY_TRANSLATE_INTERNAL_NODE_NAMES, true));
		int treePos = parameterMap.getTreeSelector().select(nex.namesToArray(), trees.treesAsList());
		
		Document result = createEmptyDocument();
		result.setTree(trees.getTree(treePos));
		
		NodeBranchDataAdapter supportAdapter = parameterMap.getNodeBranchDataAdapter(ReadWriteParameterMap.KEY_INTERNAL_NODE_NAMES_ADAPTER, 
				NodeNameAdapter.getSharedInstance());
		NewickReader.setDefaultSupportAdapter(result, supportAdapter, treePos, trees);
			
		if (trees.getHiddenDataAdded(treePos)) {
			NewickReader.displayHiddenDataMessage(parameterMap.getApplicationLogger(), 76);
		}
		return result;
	}


	@Override
	public DocumentIterator createIterator(BufferedInputStream stream) throws Exception {
		//TODO implement (e.g. create local NexusDocument iterator)
		return null;
	}
}