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


import javax.xml.namespace.QName;

import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.TextElementData;



public class LiteralMetadataNode extends MetadataNode implements MetadataInterface {
	private Object value;  // Ggf. später durch Object ersetzen
	private QName predicate;  // Evtl. String key wird nicht hier, sondern für die ganze Spalte gespeichert.
	private QName datatype;  // Hier wird ein XML-Datentyp angegeben. (Andere String-Datentypen aus JPhyloIO müssten entsprechend übersetzt oder ignoriert werden.)
	
	
	public LiteralMetadataNode(Node owner, QName predicate) {
		super();
		this.predicate = predicate;
	}
	
	
	public QName getDatatype(QName datatype) {
		datatype.getNamespaceURI();		
		return datatype;
	}
	
	public TextElementData getValue() {
		return (TextElementData) value;
	}
	
	
	public void setValue(Object value) {
		this.value = value;
	}
	
	
//	public TextElementData setValue(Node node, TextElementData data) {
//		data = node.getData();
//		return data;
//	}
	
	
	public QName getPredicate() {
		return predicate;
	}
	
	
	public void setPredicate(QName predicate) {
		this.predicate = predicate;
	}
	
	
	public QName getDatatype() {
		return datatype;
	}


	public void setDatatype(QName datatype) {
		this.datatype = datatype;
	}


	public boolean isEmpty() {
		if (value != null) {
			return false;
		}
		else {
			return true;
		}
	}
}
