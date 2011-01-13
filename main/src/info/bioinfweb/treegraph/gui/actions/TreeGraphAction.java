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
package info.bioinfweb.treegraph.gui.actions;


import javax.swing.*;



public abstract class TreeGraphAction extends AbstractAction {
  /**
   * Loads the small and large icon for this action from <i>/resources/symbols/</i> in the
   * the JAR file.
   * @param name - the prexif of the file name of the image files
   */
  protected void loadSymbols(String name) {
	  putValue(Action.SMALL_ICON, new ImageIcon(Object.class.getResource("/resources/symbols/" + name + "16.png")));
	  putValue(Action.LARGE_ICON_KEY, new ImageIcon(Object.class.getResource("/resources/symbols/" + name + "22.png")));
  }
}