/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2011, 2013-2017  Ben Stöver, Sarah Wiechers, Kai Müller
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
package info.bioinfweb.treegraph.gui.mainframe;


import java.util.Iterator;

import info.bioinfweb.treegraph.gui.treeframe.TreeInternalFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeViewPanel;



/**
 * Used together with {@link TreeSelectionSynchronizer} to iterate over all {@link TreeViewPanel}s in the currently
 * open documents.
 * 
 * @author Ben St&ouml;ver
 * @since 2.5.0
 */
public class TreeViewPanelIterable implements Iterable<TreeViewPanel> {
	@Override
  public Iterator<TreeViewPanel> iterator() {
		final Iterator<TreeInternalFrame> iterator = MainFrame.getInstance().treeFrameIterator();
	  return new Iterator<TreeViewPanel>() {
			@Override
      public boolean hasNext() {
	      return iterator.hasNext();
      }
			

			@Override
      public TreeViewPanel next() {
	      return iterator.next().getTreeViewPanel();
      }
			

			@Override
			public void remove() {
				throw new UnsupportedOperationException("This iterator does not support removing elements."); 
			}
		};
  }
}
