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
package info.bioinfweb.treegraph.document.io.nexus;


import java.util.Map;

import info.bioinfweb.treegraph.document.AbstractPaintableElement;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.TextElementData;
import info.bioinfweb.treegraph.document.nodebranchdata.AbstractNodeBranchDataAdapter;



class TranslTableAdapter extends AbstractNodeBranchDataAdapter {
	private Map<String, Integer> nameToKeyMap = null;
	
	
	public TranslTableAdapter(Map<String, Integer> nameToKeyMap) {
		super();
		this.nameToKeyMap = nameToKeyMap;
	}


	@Override
	public String getName() {
		return NAME_PREFIX + "translation Table";
	}
	

	@Override
	public boolean readOnly() {
		return true;
	}
	

	@Override
	public boolean decimalOnly() {
		return false;
	}
	

	@Override
	public boolean isNewColumn() {
		return false;
	}
	

	@Override
	public boolean isDecimal(Node node) {
		return false;
	}
	

	@Override
	public boolean isString(Node node) {
		return true;
	}
	

	@Override
	public boolean isEmpty(Node node) {
		return false;
	}
	

	@Override
	public String getText(Node node) throws InternalError {
		String uniqueName = node.getUniqueName();
		if (nameToKeyMap.containsKey(uniqueName)) {
			return "" + nameToKeyMap.get(uniqueName);
		}
		else {
			throw new InternalError("The node name " + node.getUniqueName() + " could not be found as a key.");
		}
	}

	
	@Override
	public void setText(Node node, String value) {
		throw new UnsupportedOperationException();
	}
	

	@Override
	public double getDecimal(Node node) {
		return Double.NaN;
	}
	

	@Override
	public void setDecimal(Node node, double value) {
		throw new UnsupportedOperationException();
	}
	

	@Override
	public TextElementData toTextElementData(Node node) {
		return new TextElementData(getText(node));
	}
	

	@Override
	public void delete(Node node) {
		throw new UnsupportedOperationException();
	}
	

	@Override
	public AbstractPaintableElement getDataElement(Node node) {
		return node;
	}
}
