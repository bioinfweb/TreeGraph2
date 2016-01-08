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
package info.bioinfweb.treegraph.document.undo.edit.calculatecolumn;


import info.bioinfweb.treegraph.document.undo.edit.CalculateColumnEdit;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommandI;



/**
 * Function to be used with {@link CalculateColumnEdit} that returns a value depending on a node/branch data ID
 * (specified as the first parameter) or a default value, specified as the second parameter, if the current node 
 * does not carry any data with the specified ID.
 * 
 * @author Ben St&ouml;ver
 * @since 2.0.24
 */
public abstract class DefaultValueIDFunction extends IDFunction implements PostfixMathCommandI {
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param edit - the edit using this function
	 */
	public DefaultValueIDFunction(CalculateColumnEdit edit) {
	  super(edit);
  }


	public boolean checkNumberOfParameters(int n) {
		return (n == 1) || (n == 2);
	}

	
	public int getNumberOfParameters() {
		return -1;
	}

	
	@Override
	public void run(Stack stack) throws ParseException {
		if (checkNumberOfParameters(getCurNumberOfParameters())) {
			Object defaultValue = null;
			if (getCurNumberOfParameters() > 1) {
				defaultValue = stack.pop();
			}
			Object result = parseIDValue(stack.pop());
			if (result == null) {
				if (defaultValue == null) {
					throw new ParseException("A value for the specified column does not exist in the current line.");
				}
				else {
					result = defaultValue;
				}
			}
			stack.push(result);
		}
		else {
			throw new ParseException("Invalid number of parameters");
		}
	}
}