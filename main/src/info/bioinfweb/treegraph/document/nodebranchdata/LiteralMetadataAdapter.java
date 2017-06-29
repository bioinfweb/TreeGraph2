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


import info.bioinfweb.treegraph.document.AbstractPaintableElement;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.TextElementData;
import info.bioinfweb.treegraph.document.metadata.LiteralMetadataNode;
import info.bioinfweb.treegraph.document.metadata.MetadataNode;
import info.bioinfweb.treegraph.document.metadata.MetadataPath;



public class LiteralMetadataAdapter extends AbstractTextElementDataAdapter {	
	private MetadataPath path = null;
	
	
	public LiteralMetadataAdapter(MetadataPath path) {
		super();
		this.path = path;
		//TODO Throw IllegalArgumentException of path references a resource metadata or remove the respective property from MetadataPath, if it is not used anywhere else.
	}
	

	public MetadataPath getPath() {
		return path;
	}


	@Override
	public String getName() {
		return NAME_PREFIX + "literalMetadataAdapter"; 
	}

	
	@Override
	public boolean readOnly() {
		return false;
	}

	
	@Override
	public AbstractPaintableElement getDataElement(Node node) {
		return node;
	}

	
	@Override
	public TextElementData getData(Node node) {
		LiteralMetadataNode result = (LiteralMetadataNode)node.getMetadataTree().searchNodeByPath(getPath());
		if (result != null) {
			return result.getValue();
		}
		else {
			return new TextElementData();
		}
	}

	
	@Override
	public boolean assignData(Node node, TextElementData data) {
		TextElementData result = node.getData();
		if (result != null) {
			result.assign(data);
			return true;
		}
		else {
			return false;
		}
	}

	
	@Override
	protected void createData(Node node) {
		// TODO Auto-generated method stub		
	}


	@Override
	public String toString() {
		return "Literal Metadata with the predicate path \"" + getPath().getElementList().toString().replace("[", "").replace("]", "") + "\"";
	}
}