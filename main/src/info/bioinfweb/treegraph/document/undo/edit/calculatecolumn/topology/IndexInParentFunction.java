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
package info.bioinfweb.treegraph.document.undo.edit.calculatecolumn.topology;


import info.bioinfweb.treegraph.document.undo.edit.CalculateColumnEdit;
import info.bioinfweb.treegraph.document.undo.edit.calculatecolumn.noarg.NoArgDoubleFunction;



/**
 * Function used with {@link CalculateColumnEdit} that returns the index of the current node in its parent node.
 * If there is no parent node -1 is returned.
 * 
 * @author Ben St&ouml;ver
 * @since 2.4.0
 * @see IsRootFunction
 */
public class IndexInParentFunction extends NoArgDoubleFunction {
	public IndexInParentFunction(CalculateColumnEdit edit) {
	  super(edit);
  }

	
	@Override
  public String getName() {
	  return "indexInParent";
  }


	@Override
  protected double calculateResult() {
		if (getEdit().isEvaluating()) {
			return 0;
		}
		else {
			if (getEdit().getPosition().hasParent()) {
				return getEdit().getPosition().getParent().getChildren().indexOf(getEdit().getPosition());
			}
			else {
				return -1;
			}
		}
  }
}
