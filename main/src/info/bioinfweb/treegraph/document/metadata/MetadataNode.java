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
package info.bioinfweb.treegraph.document.metadata;


import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.QName;

import info.bioinfweb.treegraph.document.HiddenDataMap;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.TextElement;
import info.bioinfweb.treegraph.document.TextElementData;
import info.bioinfweb.treegraph.document.tools.IDManager;



public class MetadataNode {
	private MetadataNode parent;
	private TextElementData value;  // Ggf. später durch Object ersetzen
	private QName predicate;  // Evtl. String key wird nicht hier, sondern für die ganze Spalte gespeichert.
	private Node owner;
	
	
	public MetadataNode(Node owner) {
		this.owner = owner;
	}
	
	
	public Node getOwner() {
		return owner;
	}

	
//	public MetadataNode getParent() {
//		return parent;
//	}
	
	public TextElementData getValue() {
		return value;
	}
	
	
	public QName getPredicate() {
		return predicate;
	}
	
	
	public boolean isEmpty() {
		return value.isEmpty();
	}
	
	
	public int size() {
		return parent.size();
	}
	
	
	public void clear() {
		value = null;
	}
	
	
	public TextElementData remove(String id) {
		return parent.remove(id);
	}
		
	
	public void assign(MetadataNode other) {
		clear();
		value.setText(other.value.getText());
	}
	
	
	public Object putForID(String id, TextElementData value) {
		Object result = null;
//		if (getParent() != null) {
//			result = IDManager.removeElementWithID(getParent(), id);
//		}
		parent.put(id, value);
		return result;
	}
	
	
	public TextElementData put(String id, TextElementData value) {
		Object result = putForID(id, value);
		if (result instanceof TextElementData) {
			return (TextElementData)result;
		}
		else if (result instanceof TextElement) {  // return text of TextLabel
			return ((TextElement)result).getData();
		}
		else {
			return null;
		}
	}
}
