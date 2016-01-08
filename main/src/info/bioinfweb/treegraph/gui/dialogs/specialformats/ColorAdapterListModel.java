/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2015-2016  Ben Stöver, Sarah Wiechers
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
package info.bioinfweb.treegraph.gui.dialogs.specialformats;


import info.bioinfweb.treegraph.document.IDManager;
import info.bioinfweb.treegraph.document.Label;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.TextLabel;
import info.bioinfweb.treegraph.document.format.adapters.color.*;

import java.util.List;
import java.util.Vector;

import javax.swing.AbstractListModel;
import javax.swing.ListModel;



/**
 * @author Ben St&ouml;ver
 * @since 2.0.23
 */
public class ColorAdapterListModel extends AbstractListModel implements ListModel {
	private List<ColorAdapter> adapters = new Vector<ColorAdapter>();
	

	/**
	 * Creates all color adapters valid for the subtree under <code>root</code>. 
	 * @param root - the root of the subtree that shall be used to search for label IDs 
	 */
	public void setAdapters(Node root) {
		if (!adapters.isEmpty()) {
			int end = getSize() - 1;
			adapters.clear();
			fireIntervalRemoved(this, 0, end);
		}
		
		adapters.add(new BranchLineColorAdapter());
		adapters.add(new NodeLineColorAdapter());
		adapters.add(new NodeTextColorAdapter());
		String[] ids = IDManager.getLabelIDs(root, Label.class);
		List<String> textLabelIDs = IDManager.getLabelIDListFromSubtree(root, TextLabel.class);
		for (int i = 0; i < ids.length; i++) {
			adapters.add(new LabelLineColorAdapter(ids[i]));
			if (textLabelIDs.contains(ids[i])) {
				adapters.add(new LabelTextColorAdapter(ids[i]));
			}
		}
		fireIntervalAdded(this, 0, getSize() - 1);
	}
	

	public ColorAdapter getElementAt(int pos) {
		return adapters.get(pos);
	}

	
	public int getSize() {
		return adapters.size();
	}
}