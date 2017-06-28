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
import java.util.List;



public class MetadataPath {
	private boolean isNode;  //TODO Should this be boolean?
	private List<MetadataPathElement> path = new ArrayList<MetadataPathElement>();
	private boolean isLiteral;  //TODO Should this be boolean?


	public MetadataPath(boolean isNode, boolean isLiteral) {
		super();
		this.isNode = isNode;
		this.isLiteral = isLiteral;
	}


	public boolean isNode() {
		return isNode;
	}
	
	
	public List<MetadataPathElement> getElementList() {
		return path;
	}
	
	
	public boolean isLiteral() {
		return isLiteral;
	}
}
