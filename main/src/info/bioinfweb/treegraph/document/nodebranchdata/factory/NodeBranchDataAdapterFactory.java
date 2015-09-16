package info.bioinfweb.treegraph.document.nodebranchdata.factory;


import java.util.Map;
import java.util.TreeMap;

import info.bioinfweb.treegraph.document.nodebranchdata.BranchLengthAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.GeneralIDAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.HiddenBranchDataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.HiddenNodeDataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeNameAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.PieChartLabelAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.TextLabelAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.UniqueNameAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.VoidNodeBranchDataAdapter;



public class NodeBranchDataAdapterFactory {
	private static NodeBranchDataAdapterFactory firstInstance = null;
	
	
	private Map<String, SingleNodeBranchDataAdapterFactory<NodeBranchDataAdapter>> factories = 
			new TreeMap<String, SingleNodeBranchDataAdapterFactory<NodeBranchDataAdapter>>();
	
	
	private NodeBranchDataAdapterFactory() {
		super();
		fillFactoryMap();
	}


	private void fillFactoryMap() {
		factories.put(BranchLengthAdapter.getSharedInstance().getName(), new SingleNodeBranchDataAdapterFactory<NodeBranchDataAdapter>() {
			@Override
			public NodeBranchDataAdapter newInstance(String id) {
				return BranchLengthAdapter.getSharedInstance();
			}
		});
		factories.put(new GeneralIDAdapter("").getName(), new SingleNodeBranchDataAdapterFactory<NodeBranchDataAdapter>() {
			@Override
			public NodeBranchDataAdapter newInstance(String id) {
				return new GeneralIDAdapter(id);
			}
		});
		factories.put(new HiddenBranchDataAdapter("").getName(), new SingleNodeBranchDataAdapterFactory<NodeBranchDataAdapter>() {
			@Override
			public NodeBranchDataAdapter newInstance(String id) {
				return new HiddenBranchDataAdapter(id);
			}
		});
		factories.put(new HiddenNodeDataAdapter("").getName(), new SingleNodeBranchDataAdapterFactory<NodeBranchDataAdapter>() {
			@Override
			public NodeBranchDataAdapter newInstance(String id) {
				return new HiddenNodeDataAdapter(id);
			}
		});
		factories.put(NodeNameAdapter.getSharedInstance().getName(), new SingleNodeBranchDataAdapterFactory<NodeBranchDataAdapter>() {
			@Override
			public NodeBranchDataAdapter newInstance(String id) {
				return NodeNameAdapter.getSharedInstance();
			}
		});
		factories.put(new PieChartLabelAdapter("").getName(), new SingleNodeBranchDataAdapterFactory<NodeBranchDataAdapter>() {
			@Override
			public NodeBranchDataAdapter newInstance(String id) {
				return new PieChartLabelAdapter(id);
			}
		});
		factories.put(new TextLabelAdapter("", null).getName(), new SingleNodeBranchDataAdapterFactory<NodeBranchDataAdapter>() {
			@Override
			public NodeBranchDataAdapter newInstance(String id) {
				return new TextLabelAdapter(id);
			}
		});
		factories.put(UniqueNameAdapter.getSharedInstance().getName(), new SingleNodeBranchDataAdapterFactory<NodeBranchDataAdapter>() {
			@Override
			public NodeBranchDataAdapter newInstance(String id) {
				return UniqueNameAdapter.getSharedInstance();
			}
		});
		factories.put(new VoidNodeBranchDataAdapter("").getName(), new SingleNodeBranchDataAdapterFactory<NodeBranchDataAdapter>() {
			@Override
			public NodeBranchDataAdapter newInstance(String id) {
				return new VoidNodeBranchDataAdapter("No support values available"); //TODO find way to customize text (Possibly move text property to NodeBranchDataInput and do not model in adapter.)
			}
		});
	}
	
	
	public static NodeBranchDataAdapterFactory getInstance() {
		if (firstInstance == null) {
			firstInstance = new NodeBranchDataAdapterFactory();
		}
		return firstInstance;
	}
	
	
	public NodeBranchDataAdapter newAdapterInstance(String name, String id) {
		return factories.get(name).newInstance(id);
	}
}
