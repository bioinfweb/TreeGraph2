/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2015-2016  Ben Stöver, Sarah Wiechers, Kai Müller
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
package info.bioinfweb.treegraph.document;



/**
 * Exception that is thrown if an element with a node/branch data ID (e.g. a label) shall be attached to a node or 
 * branch that is already linked to another element with that ID.
 * 
 * @author Ben St&ouml;ver
 * @since 2.4.0
 */
public class DuplicateIDException extends RuntimeException {
	private String id;
	private Node node;

	
	public DuplicateIDException(String id, Node node) {
	  super("An element with the ID \"" + id + "\" is already present on the node \"" + node.getUniqueName() + "\".");
	  this.id = id;
  }


	public String getId() {
		return id;
	}


	public Node getNode() {
		return node;
	}
}
