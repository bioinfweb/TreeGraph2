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
package info.bioinfweb.treegraph.gui.treeframe;


import java.util.*;
import javax.swing.event.*;



/**
 * Classes that have to be informed about changes happening in a {@link TreeViewPanel} should implement
 * this interface.
 * 
 * @author Ben St&ouml;ver
 */
public interface TreeViewPanelListener extends EventListener {
  /**
   * Called after the selection of paintable elements in a tree (and the node/branch data table) changed.
   * 
   * @param e a change event with the {@link TreeViewPanel} as the source
   */
  public void selectionChanged(ChangeEvent e);
  
  /**
   * Called after a highlighting group of paintable elements in a tree (and the node/branch data table) changed.
   * 
   * @param e a change event with the {@link HighlightedGroup} as the source (Note that this event does not use the {@link TreeViewPanel} as its 
   *        source. This can be determined using {@link HighlightedGroup#getOwner()} if necessary.)
   * @since 2.16.0
   */
  public void highlightingChanged(ChangeEvent e);
  
  /**
   * Called after the tree was zoomed in or out.
   * 
   * @param e a change event with the {@link TreeViewPanel} as the source
   */
  public void zoomChanged(ChangeEvent e);
  
  /**
   * Called after the size of the tree changed. That can happen due to edits of the tree. Zoom changes are not covered by this event.
   * 
   * @param e a change event with the {@link TreeViewPanel} as the source
   */
  public void sizeChanged(ChangeEvent e);
}