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
package info.bioinfweb.treegraph.document.undo.edit.calculatecolumn.vararg;


import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.nfunk.jep.ParseException;

import info.bioinfweb.treegraph.document.undo.edit.CalculateColumnEdit;
import info.bioinfweb.treegraph.document.undo.edit.calculatecolumn.IDFunction;



/**
 * Function to be used with {@link CalculateColumnEdit} that allows to specify a set of node/branch data column IDs
 * instead of concrete value for every {@link VarArgFunction}.
 * 
 * @author Ben St&ouml;ver
 * @since 2.4.0
 */
public class IDVarArgFunction extends IDFunction {
	private VarArgFunction implementingVersion;
	
	
	public IDVarArgFunction(CalculateColumnEdit edit, VarArgFunction implementingVersion) {
	  super(edit);
	  this.implementingVersion = implementingVersion;
  }

	
	@Override
  public String getName() {
	  return implementingVersion.getName() + "OfColumns";
  }


	@Override
  public boolean checkNumberOfParameters(int n) {
		return implementingVersion.checkNumberOfParameters(n);
  }
	

	@Override
  public int getNumberOfParameters() {
	  return implementingVersion.getNumberOfParameters();
  }

	
	@Override
  public void run(Stack stack) throws ParseException {
		if (checkNumberOfParameters(getCurNumberOfParameters())) {
			// Replace ID and adapter values in stack by concrete values, if possible:
			List<Object> processedValues = new ArrayList<Object>(stack.size());
			for (int i = 0; i < getCurNumberOfParameters(); i++) {
				Object processedValue = parseIDValue(stack.pop());
				if (processedValue != null) {
					processedValues.add(0, processedValue);
				}
			}
			stack.addAll(processedValues);
			
			// Run concrete function implementation:
			implementingVersion.setCurNumberOfParameters(processedValues.size());
			implementingVersion.run(stack);  // Also checks if size is still valid.
		}
		else {
			throw new ParseException("Invalid number of parameters");
		}
  }
}
