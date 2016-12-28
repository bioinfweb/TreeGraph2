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
package info.bioinfweb.treegraph.gui.dialogs.elementformats;


import java.util.List;

import info.bioinfweb.treegraph.document.format.operate.FormatOperator;
import info.bioinfweb.treegraph.gui.treeframe.TreeSelection;



/**
 * Classes that implement single tabs of the {@link ElementFormatsDialog} must implement this interface.
 * 
 * @author Ben St&ouml;ver
 * @see ElementFormatsDialog
 */
public interface ElementFormatsTab {
  /**
   * The values of the first compatible element in the selection should be set to
   * the input components of this tab.
   * @param selection - the currently selected tree elements
   * @return <code>false</code> if no compatible element was selected 
   */
  public boolean setValues(TreeSelection selection);
  
  public String title();
  
	public void resetChangeMonitors();
	
	public void addOperators(List<FormatOperator> operators);
	
	/**
	 * Adds error messages to the given list, if this tab contains any invalid user inputs.
	 * @param list - the list to add the messaged to
	 */
	public void addError(List<String> list);
}