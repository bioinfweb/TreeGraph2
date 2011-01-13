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
package info.bioinfweb.treegraph.document.io.nexus;


import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import info.bioinfweb.treegraph.Main;
import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.Tree;
import info.bioinfweb.treegraph.document.io.TextStreamReader;
import info.bioinfweb.treegraph.document.io.TreeSelector;
import info.bioinfweb.treegraph.document.io.log.LoadLogger;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;



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
	
	
	public Document read(InputStream stream, LoadLogger loadLogger, NodeBranchDataAdapter internalAdapter, 
			NodeBranchDataAdapter branchLengthAdapter, TreeSelector selector, 
			boolean translateInternalNodes) throws Exception {
		
		NexusDocument nex = NexusParser.parse(readStream(stream));
		Tree[] trees = nex.createTrees(internalAdapter, branchLengthAdapter, translateInternalNodes);
		int treePos = selector.select(nex.namesToArray(), trees);
		
		Document result = new Document();
		result.setTree(trees[treePos]);
		
		return result;
	}
	
	
	public Document read(File file, LoadLogger loadLogger, NodeBranchDataAdapter internalAdapter, 
			NodeBranchDataAdapter branchLengthAdapter, TreeSelector selector, 
			boolean translateInternalNodes) throws Exception {
		
		Document result = read(new FileInputStream(file), loadLogger, internalAdapter, branchLengthAdapter, selector, 
				translateInternalNodes);
		result.setDefaultName(result.getDefaultName().replaceAll(
				Main.getInstance().getNameManager().getPrefix(), file.getName()));
		return result;
	}
}