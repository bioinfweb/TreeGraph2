/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2011  Ben Stöver, Kai Müller
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

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommandI;



/**
 * @author Ben St&ouml;ver
 * @since 2.0.24
 */
public abstract class IDFunction implements PostfixMathCommandI {
	private int curNumberOfParameters = 1;
	
	
  public static Double codeBoolean(boolean value) {
  	if (value) {
  		return new Double(1);
  	}
  	else {
  		return new Double(0);
  	}
  }
  
  
	public boolean checkNumberOfParameters(int n) {
		return (n == 1) || (n == 2);
	}

	
	public int getNumberOfParameters() {
		return -1;
	}

	
	public void setCurNumberOfParameters(int n) {
		curNumberOfParameters = n;
	}

	
	public void run(Stack stack) throws ParseException {
		if (checkNumberOfParameters(curNumberOfParameters)) {
			Object defaultValue = null;
			if (curNumberOfParameters > 1) {
				defaultValue = stack.pop();
			}
			Object id = stack.pop();
			
			Object result = null;
			if (id instanceof String) {
				result = getValue((String)id);
			}
			else if (id instanceof NodeBranchDataAdapter) {
				result = getValue((NodeBranchDataAdapter)id);
			}
			else {
				throw new ParseException("Invalid parameter type");
			}
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
	
	
	public abstract Object getValue(String id) throws ParseException;
	
	
	public abstract Object getValue(NodeBranchDataAdapter id) throws ParseException;
}