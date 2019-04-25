/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2011, 2013-2019  Ben Stöver, Sarah Wiechers, Kai Müller
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
package info.bioinfweb.treegraph.document.undo.edit.calculatecolumn;


import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.undo.edit.CalculateColumnEdit;

import org.nfunk.jep.ParseException;



/**
 * Implements basic functionality for functions to be used with {@link CalculateColumnEdit} that accept
 * node/branch data IDs as parameters.
 * 
 * @author Ben St&ouml;ver
 * @since 2.4.0
 */
public abstract class IDFunction extends AbstractFunction {
	public IDFunction(CalculateColumnEdit edit) {
	  super(edit);
  }


	public Object parseIDValue(Object value) throws ParseException {
		if (value instanceof String) {
			return getValue((String)value);
		}
		else if (value instanceof NodeBranchDataAdapter) {
			return getValue((NodeBranchDataAdapter)value);
		}
		else {
			throw new ParseException("Invalid parameter type");
		}
	}
	
	
	/**
	 * Default implementation that returns the value in the specified node/branch data column at the 
	 * current node.
	 * 
	 * @param id - the ID of the column to be used
	 * @return the value found in the node/branch data column or some default value depending on the current
	 *         evaluation status of the associated {@link CalculateColumnEdit}
	 * @throws ParseException if the associated {@link CalculateColumnEdit} is not in evaluation mode and the 
	 *         specified node/branch data column does not contain any value at the current node
	 */
	public Object getValue(String id) throws ParseException {
		return getEdit().getIDValue(getEdit().getPosition(), id);
	}


	/**
	 * Default implementation that returns the value in the specified node/branch data column at the 
	 * current node.
	 * 
	 * @param adapter - the node/branch data adapter of the column to be used
	 * @return the value found in the node/branch data column or some default value depending on the current
	 *         evaluation status of the associated {@link CalculateColumnEdit}
	 * @throws ParseException if the associated {@link CalculateColumnEdit} is not in evaluation mode and the 
	 *         specified node/branch data column does not contain any value at the current node
	 */
	public Object getValue(NodeBranchDataAdapter adapter) throws ParseException {
		return getEdit().getValue(getEdit().getPosition(), adapter);
	}
}
