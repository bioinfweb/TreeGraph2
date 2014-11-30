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
package info.bioinfweb.treegraph.document.undo.edit.calculatecolumn.topology;


import info.bioinfweb.treegraph.document.undo.edit.CalculateColumnEdit;
import info.bioinfweb.treegraph.document.undo.edit.calculatecolumn.noarg.NoArgBooleanFunction;



/**
 * Function used with {@link CalculateColumnEdit} that checks if the current node is a terminal node.
 * 
 * @author Ben St&ouml;ver
 * @since 2.4.0
 */
public class IsLeafFunction extends NoArgBooleanFunction {
	public IsLeafFunction(CalculateColumnEdit edit) {
	  super(edit);
  }


	@Override
  public String getName() {
	  return "isLeaf";
  }


	@Override
  protected boolean calculateResult() {
	  return getEdit().getPosition().isLeaf();
  }
}
