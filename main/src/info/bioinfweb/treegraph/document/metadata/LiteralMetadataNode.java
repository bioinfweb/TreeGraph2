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

import info.bioinfweb.treegraph.document.TextElementData;



public class LiteralMetadataNode extends MetadataNode {
	private TextElementData value;  // Ggf. später durch Object ersetzen
	private QName datatype;  // Hier wird ein XML-Datentyp angegeben. (Andere String-Datentypen aus JPhyloIO müssten entsprechend übersetzt oder ignoriert werden.)
	
	
	public LiteralMetadataNode() {  //TODO Which properties must be set?
		super();
	}
	
		
	public LiteralMetadataNode(QName predicateOrRel) {
		super(predicateOrRel);
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
	
	
	public QName getDatatype() {
		return datatype;
	}


	public void setDatatype(QName datatype) {
		this.datatype = datatype;
	}
	
	
	public void clear() {
		datatype = null;
		value = null;
	}


	@Override
	public LiteralMetadataNode clone() {
		LiteralMetadataNode result = (LiteralMetadataNode)super.clone();
		result.setValue(getValue().clone());
		return result;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((datatype == null) ? 0 : datatype.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		LiteralMetadataNode other = (LiteralMetadataNode) obj;
		if (datatype == null) {
			if (other.datatype != null)
				return false;
		} else if (!datatype.equals(other.datatype))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}


	//TODO Move to LiteralMetadataNodeTest
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
