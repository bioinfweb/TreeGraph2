/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2011, 2013-2016  Ben Stöver, Sarah Wiechers, Kai Müller
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


import info.bioinfweb.treegraph.document.Document;



/**
 * Enumerates different types of document changes.
 * <p>
 * The different change types have a hierarchical order, where a subsequent type automatically includes all 
 * previous types except for {@link #TOPOLOGICAL_BY_RENAMING} and {@link #TOPOLOGICAL_LEAF_INVARIANT} which
 * are alternatives located on the same level. The hierarchical order would be the following:
 * <ol>
 *   <li>{@link #NEUTRAL}</li>
 *   <li>{@link #POSITION}</li>
 *   <li>{@link #NODE_ORDER}</li>
 *   <li>{@link #ROOT_POSITION}</li>
 *   <li>{@link #TOPOLOGICAL_BY_RENAMING} | {@link #TOPOLOGICAL_LEAF_INVARIANT}</li>
 *   <li>{@link #TOPOLOGICAL_BY_OBJECT_CHANGE}</li>
 * </ol>
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
	 * Indicates that the paint position of one or more document elements was changed, but no topological or node 
	 * order changes occurred.
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
	 * Indicates a change of topology triggered by the change of a textual or numeric value which changes the identity
	 * of a node. There was no change of the actual object topology, but the character of a node changed, so it could
	 * be considered as a replacement of this node.
	 * <p>
	 * Edits of a node name, a data ID or the contents of a data ID element attached to a node are considered as 
	 * character changing, i.e. a possible replacement of the according node. Changes of branch lengths are not considered 
	 * as a possible node replacement. (Anyway some edits that can modify any node/branch data column would also declare 
	 * this type even if a concrete of them instance modifies nothing but branch lengths.) Note that the question if such 
	 * an edit really would be equal to a node replacement depends of the default leaf adapter that is set in the according 
	 * {@link Document} at runtime. 
	 */
	TOPOLOGICAL_BY_RENAMING,
	
	/**
	 * Indicates that a topological change in the tree occurred that does not affect the leaf sets (e.g. collapsing of
	 * an internal node).
	 * <p>
	 * Note that this type is only returned by edits that never affect leaf distributions. Edits of the types
	 * {@link #TOPOLOGICAL_BY_RENAMING} or {@link #TOPOLOGICAL_BY_OBJECT_CHANGE} may also be leaf set invariant
	 * in certain situations, but that is not guaranteed for every case.
	 */
	TOPOLOGICAL_LEAF_INVARIANT,
	
	/**
	 * Indicates a topological change that includes the actual movement, insertion or deletion of a node instance.
	 * In contrast to {@link #TOPOLOGICAL_BY_RENAMING} changing the character of a node, which stays at its position
	 * is not sufficient for this type.
	 */
	TOPOLOGICAL_BY_OBJECT_CHANGE;
}
