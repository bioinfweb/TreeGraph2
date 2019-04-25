/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2011, 2013-2019  Ben Stöver, Sarah Wiechers, Kai Müller
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


import info.bioinfweb.treegraph.document.AbstractPaintableElement;
import info.bioinfweb.treegraph.document.Label;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.TextElementData;
import info.bioinfweb.treegraph.document.TextLabel;
import info.bioinfweb.treegraph.document.tools.IDManager;

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
	public AbstractPaintableElement getDataElement(Node node) {
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


	@Override
	public String toString() {
		return  "All data with the ID \"" + getID() + "\"";
	}
}
