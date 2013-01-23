package info.bioinfweb.treegraph.document.io;

import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.webinsel.util.collections.ParameterMap;



public class ReadWriteParameterMap extends ParameterMap {
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
	
	

}
