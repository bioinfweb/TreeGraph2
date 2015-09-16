package info.bioinfweb.treegraph.document.nodebranchdata.factory;


import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;



public interface SingleNodeBranchDataAdapterFactory<A extends NodeBranchDataAdapter> {
	public A newInstance(String id);
}
