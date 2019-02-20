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
package info.bioinfweb.treegraph.gui.treeframe.table;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import info.bioinfweb.treegraph.document.metadata.MetadataNode;
import info.bioinfweb.treegraph.document.metadata.MetadataNodeList;
import info.bioinfweb.treegraph.document.metadata.MetadataTree;
import info.bioinfweb.treegraph.document.metadata.ResourceMetadataNode;



public class ColumnHeadingPaintInfoList extends ArrayList<ColumnHeadingPaintInfo> {
	private DocumentTableModel model;
	private int maxTreeDepth = 1;
	
	
	public ColumnHeadingPaintInfoList(DocumentTableModel model) {
		super();
		this.model = model;
		model.addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
				refreshContents();
			}
		});
		
		refreshContents();
	}


	public int getMaxTreeDepth() {
		return maxTreeDepth;
	}


	private int determineHorizontalLines(List<MetadataNode> children, ColumnHeadingPaintInfo currentInfo) {
		int treeDepth = 0;
		add(currentInfo);
		
		Iterator<MetadataNode> iterator = children.iterator();
		while (iterator.hasNext()) {
			MetadataNode child = iterator.next();

			if (child instanceof ResourceMetadataNode) {
				MetadataNodeList grandchildren = ((ResourceMetadataNode)child).getChildren();
				treeDepth = Math.max(treeDepth, determineHorizontalLines(grandchildren, ColumnHeadingPaintInfo.createChildInstance(
						currentInfo, (grandchildren.size() > 0), !iterator.hasNext(), currentInfo.isLastUnderParent())));
			}
			else {  // LiteralMetadataNode
				add(ColumnHeadingPaintInfo.createChildInstance(currentInfo, false, !iterator.hasNext(), currentInfo.isLastUnderParent()));
				treeDepth = Math.max(treeDepth, 1);
			}
		}
		
		return treeDepth + 1;
	}
	
	
	private int determineHorizontalLines(MetadataTree tree) {
		return determineHorizontalLines(tree.getChildren(), new ColumnHeadingPaintInfo(new boolean[]{tree.getChildren().size() > 0}, true));
	}
	
	
	public void refreshContents() {
		clear();
		
		add(new ColumnHeadingPaintInfo(new boolean[]{false}, true));  // unique node name column
		maxTreeDepth = Math.max(
				determineHorizontalLines(model.getNodeTree()),  // node metadata columns
				determineHorizontalLines(model.getBranchTree()));  // branch metadata columns
		
		while (size() < model.getColumnCount()) {
			add(new ColumnHeadingPaintInfo(new boolean[]{false}, true));  // text label columns  //TODO This can be removed when these columns are not present anymore.
		}
	}
}
