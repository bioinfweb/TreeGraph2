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
package info.bioinfweb.treegraph.gui.dialogs.io.exporttographic;


import info.bioinfweb.commons.collections.ParameterMap;



/**
 * This interface should be implemented by all panels that are used to edit format specific 
 * preferences in the <code>ExportToGraphics</code>-dialog.
 * 
 * @author Ben St&ouml;ver
 */
public interface PreferencesPanel {
  /**
   * This method should add the graphic writer hints that are specified by the components
   * contained in the particular implementation
   * 
   * @param hints - the map to add the hints to
   */
  public void addHints(ParameterMap hints);
}