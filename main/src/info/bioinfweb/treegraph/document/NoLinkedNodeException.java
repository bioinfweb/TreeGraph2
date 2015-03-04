/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2015  Ben Stöver, Kai Müller
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
 * Thrown when a method of a tree element is invoked which need to access the linked node, but the element does not 
 * have a linked node.
 * 
 * @see TreeElement#getLinkedNode()
 * 
 * @author Ben St&ouml;ver
 * @since 2.0.43
 */
public class NoLinkedNodeException extends IllegalStateException {
	public NoLinkedNodeException() {
		super();
	}

	
	public NoLinkedNodeException(String msg, Throwable thr) {
		super(msg, thr);
	}

	
	public NoLinkedNodeException(String msg) {
		super(msg);
	}

	
	public NoLinkedNodeException(Throwable thr) {
		super(thr);
	}
}