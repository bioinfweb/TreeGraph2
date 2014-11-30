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
package info.bioinfweb.treegraph.document.undo.edit.calculatecolumn.values;


import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.undo.edit.CalculateColumnEdit;
import info.bioinfweb.treegraph.document.undo.edit.calculatecolumn.IDFunction;

import org.nfunk.jep.ParseException;



/**
 * Function used with {@link CalculateColumnEdit} that tests if a certain node/branch data column has a value 
 * at the parent node of the current node.
 * 
 * @author Ben St&ouml;ver
 * @since 2.4.0
 */
public class HasParentValueFunction extends IDFunction {
	public HasParentValueFunction(CalculateColumnEdit edit) {
	  super(edit);
  }


	@Override
  public String getName() {
	  return "hasParentValue";
  }


	@Override
	public Object getValue(String id) throws ParseException {
		if (getEdit().isEvaluating()) {
			return codeBoolean(getEdit().hasIDValue(null, id));  // Return default value for evaluation.
		}
		else {
			Node parent = getEdit().getPosition().getParent();
			boolean result = false;  // Returns false if this function is called on the root node.
			if (parent != null) {
				result = getEdit().hasIDValue(parent, id);
			}
			return codeBoolean(result);
		}
	}
	

	@Override
	public Object getValue(NodeBranchDataAdapter adapter) throws ParseException {
		if (getEdit().isEvaluating()) {
			return codeBoolean(getEdit().hasValue(null, adapter));  // Return default value for evaluation.
		}
		else {
			Node parent = getEdit().getPosition().getParent();
			boolean result = false;  // Returns false if this function is called on the root node.
			if (parent != null) {
				return codeBoolean(getEdit().hasValue(getEdit().getPosition().getParent(), adapter));
			}
			return codeBoolean(result);
		}
	}

}
