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

import info.bioinfweb.jphyloio.formats.xtg.XTGConstants;
import info.bioinfweb.treegraph.document.TextElementData;



public class LiteralMetadataNode extends MetadataNode {
	private TextElementData value;  // Ggf. später durch Object ersetzen
	private QName predicate;  // Evtl. String key wird nicht hier, sondern für die ganze Spalte gespeichert.
	private QName datatype;  // Hier wird ein XML-Datentyp angegeben. (Andere String-Datentypen aus JPhyloIO müssten entsprechend übersetzt oder ignoriert werden.)
	
	
	public LiteralMetadataNode(QName predicate) {  //TODO Which properties must be set?
		super();
		this.predicate = predicate;
	}	
	
	
	public QName getDatatype(QName datatype) {
		datatype.getNamespaceURI();		
		return datatype;
	}
	
	
	public TextElementData getValue() {
		return value;
	}
	
	
	public void setValue(TextElementData value) {
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


	@Override
	public LiteralMetadataNode clone() {
		LiteralMetadataNode result = (LiteralMetadataNode)super.clone();
		result.setValue(getValue().clone());
		return result;
	}


//	public static void main(String[] args) {
//		LiteralMetadataNode parent = new LiteralMetadataNode(XTGConstants.PREDICATE_BRANCH_ATTR_MIN_LENGTH);
//		LiteralMetadataNode node = new LiteralMetadataNode(XTGConstants.PREDICATE_BRANCH_ATTR_CONSTANT_WIDTH);
//		node.setValue(new TextElementData(15));
//		node.setDatatype(XTGConstants.DATA_TYPE_COLOR);
//		node.setParent(parent);
//		
//		LiteralMetadataNode copy = node.clone();
//		
//		System.out.println(copy);
//		System.out.println(copy.getPredicate());
//		System.out.println(copy.getDatatype());
//		System.out.println(copy.getValue());
//		System.out.println(copy.getParent());
//		System.out.println();
//		
//		MetadataNode node2 = node;
//		copy = (LiteralMetadataNode)node2.clone();
//		
//		System.out.println(copy);
//		System.out.println(copy.getPredicate());
//		System.out.println(copy.getDatatype());
//		System.out.println(copy.getValue());
//		System.out.println(copy.getParent());
//		
//	}
}
