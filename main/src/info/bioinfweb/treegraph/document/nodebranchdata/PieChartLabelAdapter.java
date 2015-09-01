package info.bioinfweb.treegraph.document.nodebranchdata;


import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.TextElementData;

import java.text.DecimalFormat;



public class PieChartLabelAdapter extends AbstractIDElementAdapter {
	String id = null;

	
	public PieChartLabelAdapter(String id) {
		super(id);
		this.id = id;
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public TextElementData getData(Node node) {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	public boolean assignData(Node node, TextElementData data) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public DecimalFormat getDecimalFormat(Node node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void createData(Node node) {
		// TODO Auto-generated method stub		
	}

	@Override
	public String getID() {
		return id;
	}

	@Override
	public String toString() {
		return "New pie chart label with the specified ID";
	}
}
