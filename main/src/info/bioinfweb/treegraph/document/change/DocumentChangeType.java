/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2014  Ben Stöver, Kai Müller
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
package info.bioinfweb.treegraph.document.change;



/**
 * Enumerates different types of document changes.
 * 
 * @author Ben St&ouml;ver
 * @since 2.5.0
 */
public enum DocumentChangeType {
	/** 
	 * Indicates that a document change does not affect the paint or topological position of any document element.
	 * (A color change would be an example.) 
	 */
	NEUTRAL,
	
	/** 
	 * Indicates that the paint position of one or more document elements was changed, but no topological or node order 
	 * changes occurred.
	 */
	POSITION,
	
	/** 
	 * Indicates the order of the child nodes of one or more parent nodes in the document changed, but tree topology
	 * remains unchanged. 
	 */
	NODE_ORDER,
	
	/** 
	 * Indicates that the position of the root if the document changed. If the tree would be considered unrooted,
	 * its topology would be unchanged. 
	 */
	ROOT_POSITION,
	
	/** 
	 * Indicates that the topology of the tree in this document was changed, even if the tree would be considered 
	 * unrooted.
	 */
	TOPOLOGICAL;
}
