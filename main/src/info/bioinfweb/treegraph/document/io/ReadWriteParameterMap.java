/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2013  Ben Stöver, Kai Müller
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
package info.bioinfweb.treegraph.document.io;


import info.bioinfweb.treegraph.document.io.log.LoadLogger;
import info.bioinfweb.treegraph.document.io.log.VoidLoadLogger;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.webinsel.util.collections.ParameterMap;



public class ReadWriteParameterMap extends ParameterMap {
	public static final String KEY_TREE_SELECTOR = "treeSelector";
	public static final String KEY_LOAD_LOGGER = "loadLogger";
	public static final String KEY_INTERNAL_NODE_NAMES_ADAPTER = "internalNodeNamesAdapter";
	public static final String KEY_LEAF_NODE_NAMES_ADAPTER = "leafNodeNamesAdapter";
	public static final String KEY_BRANCH_LENGTH_ADAPTER = "branchLengthAdapter";
	public static final String KEY_TRANSLATE_INTERNAL_NODE_NAMES = "translateInternalNodeNames";
	
	
	/**
	 * Checks if a {@link NodeBranchDataAdapter} object is stored under the specified key. If the stored object has 
	 * another type or no object is found, <code>defaultValue</code> is returned.
	 * 
	 * @param key - the key under which the result is stored
	 * @param defaultValue - the value to be returned, if no appropriate object is found
	 * @return an appropriate object or <code>defaultValue</code>
	 */
	public NodeBranchDataAdapter getNodeBranchDataAdapter(String key, NodeBranchDataAdapter defaultValue) {
		Object result = get(key);
		if (result instanceof NodeBranchDataAdapter) {
			return (NodeBranchDataAdapter)result;
		}
		else {
			return defaultValue;
		}
	}
	
	
	/**
	 * Returns a stored {@link LoadLogger} object or a new instance of {@link VoidLoadLogger} if no appropriate
	 * object is stored.
	 * 
	 * @param key - the key under which the result is stored
	 * @return an appropriate object or a new instance of {@link VoidLoadLogger}
	 */
	public LoadLogger getLoadLogger() {
		Object result = get(KEY_LOAD_LOGGER);
		if (result instanceof LoadLogger) {
			return (LoadLogger)result;
		}
		else {
			return new VoidLoadLogger();
		}
	}
	
	
	public void putLoadLogger(LoadLogger logger) {
		put(KEY_LOAD_LOGGER, logger);
	}
	
	
	public void removeLoadLogger() {
		Object value = get(KEY_LOAD_LOGGER);
		if (value != null) {
			remove(value);
		}
	}
}
