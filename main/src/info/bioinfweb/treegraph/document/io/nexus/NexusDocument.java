/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2014  Ben Stöver, Kai Müller
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


import info.bioinfweb.treegraph.document.Tree;
import info.bioinfweb.treegraph.document.io.newick.NewickStringReader;
import info.bioinfweb.treegraph.document.io.newick.NewickTreeList;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;

import java.util.List;
import java.util.Vector;



public class NexusDocument {
  private TranslTable translTable = new TranslTable();
  private List<String> names = new Vector<String>();
  private List<String> trees = new Vector<String>();
  
  
	public TranslTable getTranslTable() {
		return translTable;
	}


	public boolean add(String name, String tree) {
		names.add(name);
		return trees.add(tree);
	}


	public String getName(int pos) {
		return names.get(pos);
	}


	public String getTree(int pos) {
		return trees.get(pos);
	}


	public String[] namesToArray() {
		return names.toArray(new String[names.size()]);
	}


	public String[] treesToArray() {
		return trees.toArray(new String[trees.size()]);
	}


	public int size() {
		return names.size();
	}
	
	
	/**
	 * Creates a {@link Tree} object from each stored Newick string.
	 * 
	 * @param internalAdapter
	 * @param branchLengthsAdapter
	 * @param translateInternals
	 * @return an array containing all {@link Tree}-objects
	 */
	public NewickTreeList createTrees(NodeBranchDataAdapter internalAdapter, 
  		NodeBranchDataAdapter branchLengthsAdapter, boolean translateInternals) {
		
		return NewickStringReader.read(treesToArray(), internalAdapter, 
				branchLengthsAdapter, getTranslTable(),	translateInternals);
	}
}