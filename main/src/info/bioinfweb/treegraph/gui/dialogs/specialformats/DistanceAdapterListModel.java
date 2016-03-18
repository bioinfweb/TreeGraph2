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
package info.bioinfweb.treegraph.gui.dialogs.specialformats;


import info.bioinfweb.treegraph.document.GraphicalLabel;
import info.bioinfweb.treegraph.document.IDManager;
import info.bioinfweb.treegraph.document.IconLabel;
import info.bioinfweb.treegraph.document.Label;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.TextLabel;
import info.bioinfweb.treegraph.document.Tree;
import info.bioinfweb.treegraph.document.format.adapters.distance.*;
import info.bioinfweb.treegraph.document.tools.TreeSerializer;

import java.util.List;
import java.util.Vector;

import javax.swing.AbstractListModel;
import javax.swing.ListModel;



/**
 * @author Ben St&ouml;ver
 * @since 2.0.23
 */
public class DistanceAdapterListModel extends AbstractListModel implements ListModel {
	private List<DistanceAdapter> adapters = new Vector<DistanceAdapter>();
	

	/**
	 * Creates all distance adapters valid for the whole tree.
	 * @param tree  
	 * @param includeLegendFormats - defines whether legend formats should be selectable 
	 */
	public void setAdapters(Tree tree, boolean includeLegendFormats) {
		setAdapters(tree, tree.getPaintStart(), includeLegendFormats);
	}
	
	
	/**
	 * Creates all distance adapters valid for the subtree under <code>root</code>.
	 * @param tree - the tree that contains <code>root</code> (used to determine whether legends
	 *        formats can be selected) 
	 * @param root - the root of the subtree that shall be used to search for label IDs
	 * @param includeLegendFormats - defines whether legend formats should be selectable 
	 */
	public void setAdapters(Tree tree, Node root, boolean includeLegendFormats) {
		if (!adapters.isEmpty()) {
			int end = getSize() - 1;
			adapters.clear();
			fireIntervalRemoved(this, 0, end);
		}
		
		adapters.add(new BranchLineWidthAdapter());
		adapters.add(new BranchMinLengthAdapter());
		adapters.add(new BranchSpaceAboveAdapter());
		adapters.add(new BranchSpaceBelowAdapter());
		adapters.add(new NodeLineWidthAdapter());
		adapters.add(new NodeTextHeightAdapter());
		adapters.add(new CornerRadiusAdapter());
		adapters.add(new LeafMarginLeftAdapter());
		adapters.add(new LeafMarginRightAdapter());
		adapters.add(new LeafMarginTopAdapter());
		adapters.add(new LeafMarginBottomAdapter());
		
		String[] ids = IDManager.getLabelIDs(root, TextLabel.class);
		for (int i = 0; i < ids.length; i++) {
			adapters.add(new LabelTextHeightAdapter(ids[i]));
		}
		
		ids = IDManager.getLabelIDs(root, GraphicalLabel.class);
		for (int i = 0; i < ids.length; i++) {
			adapters.add(new LabelWidthAdapter(ids[i]));
		}
		for (int i = 0; i < ids.length; i++) {
			adapters.add(new LabelHeightAdapter(ids[i]));
		}
		for (int i = 0; i < ids.length; i++) {
			adapters.add(new LabelLineWidthAdapter(ids[i]));
		}
		
		ids = IDManager.getLabelIDs(root, Label.class);
		for (int i = 0; i < ids.length; i++) {
			adapters.add(new LabelMarginLeftAdapter(ids[i]));
		}
		for (int i = 0; i < ids.length; i++) {
			adapters.add(new LabelMarginRightAdapter(ids[i]));
		}
		for (int i = 0; i < ids.length; i++) {
			adapters.add(new LabelMarginTopAdapter(ids[i]));
		}
		for (int i = 0; i < ids.length; i++) {
			adapters.add(new LabelMarginBottomAdapter(ids[i]));
		}
		
    if (includeLegendFormats && TreeSerializer.getLegendsInSubtree(tree, root).length > 0) {
    	adapters.add(new LegendLineWidthAdapter());
  		adapters.add(new LegendTextHeightAdapter());
  		adapters.add(new LegendSpacingAdapter());
  		adapters.add(new LegendMarginLeftAdapter());
  		adapters.add(new LegendMarginRightAdapter());
  		adapters.add(new LegendMarginTopAdapter());
  		adapters.add(new LegendMarginBottomAdapter());
    }
		
		fireIntervalAdded(this, 0, getSize() - 1);
	}
	

	public DistanceAdapter getElementAt(int pos) {
		return adapters.get(pos);
	}

	
	public int getSize() {
		return adapters.size();
	}
}