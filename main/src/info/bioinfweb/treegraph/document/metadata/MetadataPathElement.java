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



public class MetadataPathElement {
	private QName predicateOrRel;
	private int index;
	
	
	public MetadataPathElement(QName predicateOrRel, int index) {
		super();
		this.predicateOrRel = predicateOrRel;
		this.index = index;
	}
	
	
	public QName getPredicateOrRel() {
		return predicateOrRel;
	}
	
	
	/**
	 * If more than one metadata node of the same type (literal or resource) with the same predicate is present on one level of the metadata tree, 
	 * the value returned here denotes its index in the order of node objects with this predicate. (Note that this index is not necessarily 
	 * identical with the list index of the respective metadata node.)
	 * <p>
	 * <b>Example:</b>
	 * <p>
	 * The following example shows a list of resource and literal metadata nodes with their predicates. The last value of each triple in the index 
	 * that would be used to describe the respective matadata node.
	 * <p>
	 * {@code (type=literal, predicate=p1, index=0), (type=literal, predicate=p2, index=0), (type=literal, predicate=p1, index=1), (type=resource, rel=p1, index=0), (type=literal, predicate=p1, index=2), (type=resource, rel=p1, index=1)}
	 * 
	 * @return the predicate-specific index of the referenced metadata node
	 */
	public int getIndex() {
		return index;
	}


	@Override
	public String toString() {
		String predicateAndIndex = getPredicateOrRel().getLocalPart() + getIndex();
		return predicateAndIndex;		
	}
	
	
}