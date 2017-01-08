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
package info.bioinfweb.treegraph.document.io.newick;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import info.bioinfweb.treegraph.document.Tree;



/**
 * Contains the result of reading multiple Newick strings.
 * 
 * @author Ben St&ouml;ver
 * @since 2.3.0
 */
public class NewickTreeList {
  private Tree[] trees;
  private boolean[] hiddenDataAdded;
  private boolean[] internalNamesAdded;
  
  
	public NewickTreeList(Tree[] trees, boolean[] hiddenDataAdded, boolean[] internalNamesAdded) {
	  super();
	  if (trees.length != hiddenDataAdded.length) {
	  	throw new IllegalArgumentException("The two specified fields contain a different number of elements (" + trees.length + 
	  			", " + hiddenDataAdded.length + ")");
	  }
	  else {
	  	this.trees = trees;
		  this.hiddenDataAdded = hiddenDataAdded;
		  this.internalNamesAdded = internalNamesAdded;
	  }
  }
	
	
	public Tree getTree(int index) {
		return trees[index];
	}
	
	
	public List<Tree> treesAsList() {
		return Collections.unmodifiableList(Arrays.asList(trees));
	}
	
	
	public boolean getHiddenDataAdded(int index) {
		return hiddenDataAdded[index];
	}
	
	
	public int size() {
		return trees.length;
	}


	public boolean getInternalNamesAdded(int index) {
		return internalNamesAdded[index];
	}
}
