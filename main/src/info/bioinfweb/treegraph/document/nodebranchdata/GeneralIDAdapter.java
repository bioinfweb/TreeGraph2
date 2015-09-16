package info.bioinfweb.treegraph.document.nodebranchdata;


import info.bioinfweb.treegraph.document.ConcretePaintableElement;
import info.bioinfweb.treegraph.document.IDManager;
import info.bioinfweb.treegraph.document.Label;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.TextElementData;
import info.bioinfweb.treegraph.document.TextLabel;

import java.text.DecimalFormat;



public class GeneralIDAdapter extends AbstractIDElementAdapter {
	public GeneralIDAdapter(String id) {
		super(id);
	}
	
	
	@Override
	public String getName() {
		return NAME_PREFIX + "generalID";
	}


	@Override
	public boolean readOnly() {
		return true; //TODO evtl. an anderen Stellen ungünstig?
	}
	

	@Override
	public ConcretePaintableElement getDataElement(Node node) {
  	Label l = node.getAfferentBranch().getLabels().get(id);
  	if ((l != null) && (l instanceof TextLabel)) {
  		return (TextLabel)l;
  	}
  	else {
    	TextElementData data = null;
  		data = node.getHiddenDataMap().get(id);
  		if (data != null) {
  			return node;
  		}
  		else {
    		data = node.getAfferentBranch().getHiddenDataMap().get(id);
    		if (data != null) {
    			return node.getAfferentBranch();
    		}
  		}
  	}
  	return null;
	}
	

	@Override
	public TextElementData getData(Node node) {
		return IDManager.getDataByID(node, id);
	}
	

	@Override
	public boolean assignData(Node node, TextElementData data) {
		throw new UnsupportedOperationException("");
//		getData(node).assign(data);  //TODO Test if null
//		return true;
	}
	

	@Override
	public DecimalFormat getDecimalFormat(Node node) {
		Label l = node.getAfferentBranch().getLabels().get(getID());
		if ((l != null) && (l instanceof TextLabel)) {
			return ((TextLabel)l).getFormats().getDecimalFormat();
		}
		else {
			return super.getDecimalFormat(node);
		}
	}
	

	@Override
	protected void createData(Node node) {
		throw new UnsupportedOperationException("Creating new values is not supported by this adapter.");
	}
}
