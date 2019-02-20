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


import java.util.ArrayList;
import java.util.Collection;



public class MetadataNodeList extends ArrayList<MetadataNode> {
	public MetadataNodeList() {
		super();
	}


	public MetadataNodeList(Collection<? extends MetadataNode> c) {
		super(c);
	}


	public MetadataNodeList(int initialCapacity) {
		super(initialCapacity);
	}


	/**
	 * Determines the index of the element in this list matching the information defined by {@code pathElement}.
	 * 
	 * @param pathElement 
	 * @return the index or -1 if not respective element is contained in this list
	 */
	public int indexOf(MetadataPathElement pathElement) {
		//TODO Currently, literal and resource nodes with the same index and predicate are distinguished by MetadataTree.searchAndCreateNodeInList(). This should either be changed or be done here as well.
		int index = 0;
		for (int i = 0; i < size(); i++) {
			if (pathElement.getPredicateOrRel().equals(get(i).getPredicateOrRel())) {
				if (index == pathElement.getIndex()) {
					return i;
				}
				else {
					index++;
				}
			}
		}
		return -1;
	}


	public MetadataNode get(MetadataPathElement pathElement) {
		int index = indexOf(pathElement);
		if (index >= 0) {
			return get(index);
		}
		else {
			return null;
		}
	}
}
