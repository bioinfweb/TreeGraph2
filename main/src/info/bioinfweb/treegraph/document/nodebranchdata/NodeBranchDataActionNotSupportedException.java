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
package info.bioinfweb.treegraph.document.nodebranchdata;



/**
 * This exception is thrown by node data adapters if an unsupported operation is perfomed (e.g.
 * storing a text value as a branch length). 
 * @author Ben St&ouol;ver
 */
public class NodeBranchDataActionNotSupportedException extends RuntimeException {
	public NodeBranchDataActionNotSupportedException() {
		super();
	}

	
	public NodeBranchDataActionNotSupportedException(String message, Throwable cause) {
		super(message, cause);
	}

	
	public NodeBranchDataActionNotSupportedException(String message) {
		super(message);
	}

	
	public NodeBranchDataActionNotSupportedException(Throwable cause) {
		super(cause);
	}
}