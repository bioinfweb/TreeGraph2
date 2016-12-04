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
package info.bioinfweb.treegraph.document.undo.edit.calculatecolumn.noarg;


import java.util.Stack;

import org.nfunk.jep.ParseException;

import info.bioinfweb.treegraph.document.undo.edit.CalculateColumnEdit;



/**
 * Abstract class that implements basic functionality for functions used in {@link CalculateColumnEdit} that
 * have no parameters and return a {@code boolean} value.
 * 
 * @author Ben St&ouml;ver
 * @since 2.4.0
 */
public abstract class NoArgBooleanFunction extends NoArgFunction {
	public NoArgBooleanFunction(CalculateColumnEdit edit) {
	  super(edit);
  }

	
	protected abstract boolean calculateResult();
	
	
	@Override
  public void run(Stack stack) throws ParseException {
		if (getEdit().isEvaluating()) {
			stack.push(codeBoolean(false));
		}
		else {
			stack.push(codeBoolean(calculateResult()));
		}
  }
}
