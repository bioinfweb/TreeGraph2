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
package info.bioinfweb.treegraph.document.undo.edit.calculatecolumn.vararg;


import java.util.Stack;

import org.nfunk.jep.ParseException;

import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.undo.edit.CalculateColumnEdit;
import info.bioinfweb.treegraph.document.undo.edit.calculatecolumn.IDFunction;



/**
 * Calculates the result of a vararg function from all values of a node/branch data column.
 * 
 * @author Ben St&ouml;ver
 * @since 2.4.0
 */
public class WholeColumnFunction extends IDFunction {
	private VarArgFunction implementingVersion;
	
	
	public WholeColumnFunction(CalculateColumnEdit edit, VarArgFunction implementingVersion) {
	  super(edit);
	  this.implementingVersion = implementingVersion;
  }

	
	@Override
  public String getName() {
	  return implementingVersion.getName() + "OfLines";
  }


	@Override
  public boolean checkNumberOfParameters(int n) {
	  return (n == 1);
  }


	@Override
  public int getNumberOfParameters() {
	  return 1;
  }

	
	private void addSubtreeToStack(Node root, NodeBranchDataAdapter adapter, Stack stack) {
		Object value = getEdit().getValue(root, adapter);
		if (value != null) {
			stack.push(value);
		}
		
		for (Node child : root.getChildren()) {
	    addSubtreeToStack(child, adapter, stack);
    }
	}
	
	
	private Object calculate(NodeBranchDataAdapter adapter) throws ParseException {
		Stack stack = new Stack();
		addSubtreeToStack(getEdit().getDocument().getTree().getPaintStart(), adapter, stack);  // Would lead to NullPointerException if document would be empty, but no node values can be calculated in empty documents.
		implementingVersion.setCurNumberOfParameters(stack.size());
		implementingVersion.run(stack);
		return stack.pop();
	}
	
	
	@Override
  public void run(Stack stack) throws ParseException {
		Object idValue = stack.pop();
		if (idValue instanceof String) {
			NodeBranchDataAdapter adapter = getEdit().getAdapterByID((String)idValue);
			if (adapter != null) {
				stack.push(calculate(adapter));
			}
			else {
				getEdit().throwUndefinedIDException((String)idValue);
			}
		}
		else if (idValue instanceof NodeBranchDataAdapter) {
			NodeBranchDataAdapter adapter = (NodeBranchDataAdapter)idValue;
			if (adapter.equals(getEdit().getCurrentTargetAdapter())) {
				stack.push(calculate(adapter));
			}
			else {
				throw new ParseException("Functions iterating over all lines of a column cannot be called with \"THIS\".");
			}
		}
		else {
			throw new ParseException("Invalid parameter type");
		}
  }
}
