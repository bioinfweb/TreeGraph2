/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2011, 2013-2016  Ben Stöver, Sarah Wiechers, Kai Müller
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
