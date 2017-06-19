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
package info.bioinfweb.treegraph.document.nodebranchdata;


import java.text.DecimalFormat;

import info.bioinfweb.treegraph.document.HiddenDataMap;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.TextElementData;
import info.bioinfweb.treegraph.document.metadata.MetadataNode;



public abstract class HiddenDataAdapter extends AbstractIDElementAdapter {
	public static final DecimalFormat DEFAULT_DECIMAL_FORMAT = new DecimalFormat();
	
	
	private DecimalFormat defaultDecimalFormat;
	
	
  public HiddenDataAdapter(String id) {
		super(id);
	}


	protected abstract MetadataNode getMetadataNode(Node node);

  
	@Override
	public TextElementData getData(Node node) {
		return getMetadataNode(node).getValue();
	}

	
	@Override
	public boolean assignData(Node node, TextElementData data) {
		TextElementData target = getMetadataNode(node).getValue();
		boolean result = (target != null);
		if (result) {
			target.assign(data);
		}
		return result;
	}


	@Override
	public DecimalFormat getDecimalFormat(Node node) {
		return DEFAULT_DECIMAL_FORMAT;
	}
	
	
	@Override
	protected void createData(Node node) {
		getMetadataNode(node).put(node.getUniqueName(), new TextElementData());
	}


//	@Override
//	public void delete(Node node) {
//		getMetadataNode(node).remove(node.getMetadataRoot().getPredicate());
//	}
	
	
	@Override
	public void delete(Node node) {
		getMetadataNode(node).clear();
	}
}