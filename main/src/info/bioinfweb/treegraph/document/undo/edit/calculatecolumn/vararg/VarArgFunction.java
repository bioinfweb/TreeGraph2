/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2011, 2013-2016  Ben Stöver, Sarah Wiechers, Kai Müller
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
import info.bioinfweb.treegraph.document.undo.edit.calculatecolumn.AbstractFunction;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommandI;



/**
 * Implements basic functionalities for all vararg functions (e.g. <i>min</i>).
 * 
 * @author Ben St&ouml;ver
 * @since 2.0.46
 */
public abstract class VarArgFunction extends AbstractFunction implements PostfixMathCommandI {
	private Class<? extends Object> paramClass;
	
	
	public VarArgFunction(CalculateColumnEdit edit, Class<? extends Object> paramClass) {
	  super(edit);
	  this.paramClass = paramClass;
  }
	
	
	public IDVarArgFunction createColumnsVersion() {
		return new IDVarArgFunction(getEdit(), this);
	}


	public WholeColumnFunction createLinesVersion() {
		return new WholeColumnFunction(getEdit(), this);
	}


	private boolean checkParamTypes(Stack stack) {
		for (Object value : stack) {
			if (!paramClass.isInstance(value)) {
				return false;
			}
		}
		return true;
	}


	@Override
	public boolean checkNumberOfParameters(int n) {
		return n >= 1;
	}
	

	@Override
	public int getNumberOfParameters() {
		return -1;
	}
	
	
	protected abstract Object calculate(Object value1, Object value2);

	
	@Override
	public void run(Stack stack) throws ParseException {
		if (checkNumberOfParameters(getCurNumberOfParameters())) {
			if (checkParamTypes(stack)) {
				Object result = stack.pop();
				for (int i = 0; i < getCurNumberOfParameters() - 1; i++) {
					if (result instanceof Double) {
						result = calculate(result, stack.pop());
					}
					else {
						throw new ParseException("Invalid parameter type. (Only numeric values are allowed.)");
					}
				}
				stack.push(result);
			}
			else {
				throw new ParseException("Invalid parameter type");
			}
		}
		else {
			throw new ParseException("Invalid number of parameters");
		}
	}
}
