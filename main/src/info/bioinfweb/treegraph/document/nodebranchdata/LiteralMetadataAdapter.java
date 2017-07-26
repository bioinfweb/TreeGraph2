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
import info.bioinfweb.treegraph.document.HiddenDataElement;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.TextElementData;
import info.bioinfweb.treegraph.document.metadata.LiteralMetadataNode;
import info.bioinfweb.treegraph.document.metadata.MetadataNode;
import info.bioinfweb.treegraph.document.metadata.MetadataPath;



public class LiteralMetadataAdapter extends AbstractTextElementDataAdapter implements MetadataAdapter {	
	protected MetadataPath path;
	
	
	public LiteralMetadataAdapter(MetadataPath path) {
		super();
		this.path = path;
		if (!path.isLiteral()) {
			throw new IllegalArgumentException();
		}
	}
	
	
	@Override
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
		if (getPath().isNode()) {
			return node;
		}
		else {
			return node.getAfferentBranch();
		}
	}
	
	
	private MetadataNode metadataNodeByPath(Node node, boolean createNodes) {
		return ((HiddenDataElement)getDataElement(node)).getMetadataTree().searchAndCreateNodeByPath(getPath(), createNodes);
	}

	
	@Override
	public TextElementData getData(Node node) {
		LiteralMetadataNode result = (LiteralMetadataNode)metadataNodeByPath(node, false);	
		if (result != null) {
			return result.getValue();
		}
		else {
			return null;
		}
	}

	
	@Override
	public boolean assignData(Node node, TextElementData data) {
		LiteralMetadataNode metadataNode = (LiteralMetadataNode)metadataNodeByPath(node, false);
		if (metadataNode != null) {
			metadataNode.setValue(data);
			return true;
		}
		else {
			return false;
		}
	}		


	
	@Override
	protected void createData(Node node) {
		metadataNodeByPath(node, true);
	}


	@Override
	public String toString() {
//		MetadataPathElement element = getPath().getElementList().get(getPath().getElementList().size() - 1);
//		return element.toString() + " (Literal Metadata)";
		return getPath().toString() + " (Literal Metadata)";
	}
}