/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2015-2016  Ben Stöver, Sarah Wiechers
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


import info.bioinfweb.treegraph.document.HiddenDataMap;
import info.bioinfweb.treegraph.document.Node;



public class HiddenNodeDataAdapter extends HiddenDataAdapter {
	public HiddenNodeDataAdapter(String id) {
		super(id);
	}

	
	@Override
	public String getName() {
		return NAME_PREFIX + "hiddenNodeData";
	}


	@Override
	protected HiddenDataMap getDataMap(Node node) {
		return node.getHiddenDataMap();
	}


	public Node getDataElement(Node node) {
		return node;
	}


	@Override
	public String toString() {
		return "Hidden node data with the ID \"" + getID() + "\"";
	}
}