/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2013  Ben Stöver, Kai Müller
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
package info.bioinfweb.treegraph.document.undo.edit;



/**
 * This exception is throw if a name table file does not have two entries seperated by
 * the specified <code>String</code> in a line.
 * @author Ben St&ouml;ver
 */
public class InvalidFormatException extends Exception {
	public InvalidFormatException() {
		super();
	}

	
	public InvalidFormatException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}
	

	public InvalidFormatException(String arg0) {
		super(arg0);
	}

	
	public InvalidFormatException(Throwable arg0) {
		super(arg0);
	}
}