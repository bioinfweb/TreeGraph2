/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2015-2016  Ben Stöver, Sarah Wiechers
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
package info.bioinfweb.treegraph.document.undo.edit.calculatecolumn.values;


import info.bioinfweb.treegraph.document.undo.edit.CalculateColumnEdit;
import info.bioinfweb.treegraph.document.undo.edit.calculatecolumn.DefaultValueIDFunction;



/**
 * Function that returns the value of a certain node/branch data column at the current node in {@link CalculateColumnEdit}.
 * <p>
 * This function can directly use the default implementations of {@link #getValue(String)} and 
 * {@link #getValue(info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter)}.
 * 
 * @author Ben St&ouml;ver
 * @since 2.4.0
 */
public class GetValueFunction extends DefaultValueIDFunction {
	public GetValueFunction(CalculateColumnEdit edit) {
	  super(edit);
  }

	
	@Override
  public String getName() {
	  return "getValue";
  }
}
