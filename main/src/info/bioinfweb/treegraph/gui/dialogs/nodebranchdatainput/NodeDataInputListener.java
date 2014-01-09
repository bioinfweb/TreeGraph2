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
package info.bioinfweb.treegraph.gui.dialogs.nodebranchdatainput;



public interface NodeDataInputListener {
  /**
   * This Method is called if the user selects "label with new ID" or "branch data with new ID" from
   * the combo box of <code>NewNodeDataInput</code>.
   * @param selected - <code>true</code> if one if the new ID entries was selected, <code>false</code>
   *        if another entry was selected (and a new ID entry was deselected).
   */
  public void newIDSelected(boolean selected);
}