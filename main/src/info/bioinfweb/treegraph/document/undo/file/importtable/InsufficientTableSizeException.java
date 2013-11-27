/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2013  Ben St�ver, Kai M�ller
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
package info.bioinfweb.treegraph.document.undo.file.importtable;



/**
 * This exception is thrown, if a name table file contains too few columns or rows to be processed.
 * 
 * @author Ben St&ouml;ver
 * @since 2.0.50
 */
public class InsufficientTableSizeException extends ImportTableException {
	public InsufficientTableSizeException(int columns, int rows) {
	  super("The imported table only had " + columns + " columns and " + rows + " rows. It must at least have 2 columns and 1 row.");
  }
}