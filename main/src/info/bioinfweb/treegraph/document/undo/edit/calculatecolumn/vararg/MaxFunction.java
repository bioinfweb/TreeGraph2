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
package info.bioinfweb.treegraph.document.undo.edit.calculatecolumn.vararg;


import info.bioinfweb.treegraph.document.undo.edit.CalculateColumnEdit;



/**
 * Calculates the maximum of a set of {@link Double} values in {@link CalculateColumnEdit}.
 * 
 * @author Ben St&ouml;ver
 * @since 2.4.0
 */
public class MaxFunction extends DoubleVarArgFunction {
	public MaxFunction(CalculateColumnEdit edit) {
	  super(edit);
  }

	
	@Override
  public String getName() {
	  return "max";
  }


	@Override
	protected double calculate(double value1, double value2) {
		return Math.max(value1, value2);
	}
}
