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
package info.bioinfweb.treegraph.document.undo.edit.calculatecolumn.string;


import info.bioinfweb.treegraph.document.undo.edit.CalculateColumnEdit;
import info.bioinfweb.treegraph.document.undo.edit.calculatecolumn.AbstractFunction;

import java.util.Stack;

import org.nfunk.jep.ParseException;



public abstract class AbstractStringFunction extends AbstractFunction {
	public AbstractStringFunction(CalculateColumnEdit edit) {
		super(edit);
	}


	@Override
	public boolean checkNumberOfParameters(int count) {
		return (count == getNumberOfParameters());
	}

	
	protected abstract Object calculate(String text, Object[] additionalParameters) throws ParseException;
	
	
	@Override
	public void run(Stack stack) throws ParseException {
		int parameterCount = getNumberOfParameters();
		if (parameterCount == -1) {  // curNumberOfParameters will only be set by the parser, if getNumberOfParameters() returns -1.
			parameterCount = getCurNumberOfParameters();
		}
		
		if (checkNumberOfParameters(parameterCount)) {
			Object[] additionalParameters = new Object[parameterCount - 1];
			for (int i = additionalParameters.length - 1; i >= 0; i--) {
				additionalParameters[i] = stack.pop();
			}
			stack.push(calculate(checkString(stack.pop()), additionalParameters));
		}
		else {
			throw new ParseException("Invalid number of parameters");
		}
	}
}
