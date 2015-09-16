package info.bioinfweb.treegraph.document.nodebranchdata;


import info.bioinfweb.treegraph.document.ConcretePaintableElement;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.TextElementData;

import java.text.DecimalFormat;



public class PieChartLabelAdapter extends AbstractIDElementAdapter {	
	public PieChartLabelAdapter(String id) {
		super(id);
	}

	
	@Override
	public String getName() {
		return NAME_PREFIX + "pieChartLabel";
	}


	@Override
	public TextElementData getData(Node node) {
		// unused
		return null;
	}

	
	@Override
	public boolean assignData(Node node, TextElementData data) {
		// unused
		return false;
	}
	

	@Override
	public DecimalFormat getDecimalFormat(Node node) {
		// unused
		return null;
	}
	

	@Override
	protected void createData(Node node) {
		// unused	
	}

	
	@Override
	public String toString() {
		return "New pie chart label with the specified ID";
	}
	
	
	@Override
	public ConcretePaintableElement getDataElement(Node node) {
		// unused
		return null;
	}
}
