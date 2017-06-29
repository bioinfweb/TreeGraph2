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


import java.net.URI;
import java.net.URISyntaxException;

import info.bioinfweb.treegraph.document.AbstractPaintableElement;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.TextElementData;
import info.bioinfweb.treegraph.document.metadata.MetadataPath;
import info.bioinfweb.treegraph.document.metadata.ResourceMetadataNode;



public class ResourceMetadataAdapter extends AbstractNodeBranchDataAdapter {
	private MetadataPath path = null;
	
	
	public ResourceMetadataAdapter (MetadataPath path) {
		super();
		this.path = path;
	}


	public MetadataPath getPath() {
		return path;
	}


	@Override
	public String getName() {
		return NAME_PREFIX + "resourceMetadataAdapter";
	}


	@Override
	public boolean readOnly() {
		return false;
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
		ResourceMetadataNode result = (ResourceMetadataNode)node.getMetadataTree().searchNodeByPath(getPath());
		if (result != null) {
			return false;
		} return true;
	}


	@Override
	public String getText(Node node) {
		ResourceMetadataNode result = (ResourceMetadataNode)node.getMetadataTree().searchNodeByPath(getPath());
		if (result != null) {
			return result.getURI().toString();
		}
		else {
			return "";
		}
	}


	@Override
	public void setText(Node node, String value) {		
		ResourceMetadataNode result = (ResourceMetadataNode)node.getMetadataTree().searchNodeByPath(getPath());
		try {
			URI uri = new URI(value);
			result.setURI(uri);
		} 
		catch (URISyntaxException e) {
			throw new InternalError(e);
		}
	}


	@Override
	public double getDecimal(Node node) {
		return 0;
	}


	@Override //Decimal can't be set as value of Resource metadata.
	public void setDecimal(Node node, double value) {
		throw new InternalError();		
	}


	@Override
	public TextElementData toTextElementData(Node node) {		
		ResourceMetadataNode result = (ResourceMetadataNode)node.getMetadataTree().searchNodeByPath(getPath());		
		TextElementData textElement = new TextElementData(result.getURI().toString());
		return textElement;
	}


	@Override
	public void delete(Node node) {
		ResourceMetadataNode result = (ResourceMetadataNode)node.getMetadataTree().searchNodeByPath(getPath());	
		result.clear();
		}


	@Override
	public AbstractPaintableElement getDataElement(Node node) {
		return node;
	}


	@Override
	public String toString() {
		return "Resource Metadata with the predicate path \"" + getPath().getElementList().toString().replace("[", "").replace("]", "") + "\"";
	}
	
	
//	public static void main(String[] args) {
//		MetadataPath path = new MetadataPath(true, false);		
//		MetadataPathElement a = new MetadataPathElement(XTGConstants.ATTR_FONT_FAMILY, 0);
//		MetadataPathElement b = new MetadataPathElement(XTGConstants.ATTR_LEGEND_SPACING, 0);
//		MetadataPathElement c = new MetadataPathElement(XTGConstants.ATTR_SCALE_BAR_INCREASE, 0);
//		MetadataPathElement d = new MetadataPathElement(XTGConstants.ATTR_FONT_FAMILY, 1);		
//		ResourceMetadataAdapter adapter = new ResourceMetadataAdapter(path);
//		
//		path.getElementList().add(a);
//		path.getElementList().add(b);
//		path.getElementList().add(c);
//		path.getElementList().add(d);
//		System.out.println(adapter.toString());
//	}
}