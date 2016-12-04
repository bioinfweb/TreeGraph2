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
package info.bioinfweb.treegraph.document.undo.format;


import info.bioinfweb.treegraph.document.Legend;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.Tree;
import info.bioinfweb.treegraph.document.format.LegendFormats;
import info.bioinfweb.treegraph.document.format.adapters.distance.AbstractLegendAdapter;
import info.bioinfweb.treegraph.document.format.adapters.distance.DistanceAdapter;
import info.bioinfweb.treegraph.document.tools.TreeSerializer;

import java.util.List;
import java.util.Vector;



/**
 * Backups all distance values specified by the constructor parameter <code>targetAdapters</code>
 * including legends line widths and text heights.
 * @author Ben St&ouml;ver
 * @since 2.0.24
 */
public class DistanceValuesBackup {
	private Node root;
	private Legend[] legends;
	private DistanceAdapter[] targetAdapters;
	private List<Float>[] oldTreeValues;
	private boolean storeLegendFormats = false;
  private List<LegendFormats> oldLegendFormats = new Vector<LegendFormats>();
	
	
	public DistanceValuesBackup(Tree tree, Node root, DistanceAdapter[] targetAdapters) {
		super();
		this.root = root;
		this.targetAdapters = targetAdapters;
		legends = TreeSerializer.getLegendsInSubtree(tree, root);
		
		saveOldTreeValues();
		saveOldLegendValues();
	}

	
	/**
	 * Returns whether legend formats have been stored. This is <code>true</code> if the specified
	 * <code>targetAdapters</code> contained instances of subclasses of {@link AbstractLegendAdapter} 
	 * and does not necessarily mean that concrete values were found in the specified subtree. 
	 * @return
	 */
	public boolean containsLegendFormats() {
		return storeLegendFormats;
	}
	

	public Legend[] getLegends() {
		return legends;
	}


	private void saveOldTreeValues() {
		oldTreeValues = new Vector[targetAdapters.length];
		for (int i = 0; i < oldTreeValues.length; i++) {
			oldTreeValues[i]  = new Vector<Float>();
		}
		saveOldSubtreeValues(root);
	}

	
	/**
	 * Saves the old widths in a linear list (ordering is root, child 0..n)
	 * @param root
	 */
	private void saveOldSubtreeValues(Node root) {
		for (int i = 0; i < targetAdapters.length; i++) {
			oldTreeValues[i].add(new Float(targetAdapters[i].getDistance(root)));
		}
		
		for (int i = 0; i < root.getChildren().size(); i++) {
			saveOldSubtreeValues(root.getChildren().get(i));
		}
	}
	
	
	private void saveOldLegendValues() {
		for (int i = 0; i < targetAdapters.length; i++) {
			if (targetAdapters[i] instanceof AbstractLegendAdapter) {
				storeLegendFormats = true;
				break;
			}
		}
		
		if (storeLegendFormats) {
			for (int i = 0; i < legends.length; i++) {
				oldLegendFormats.add(legends[i].getFormats().clone());
			}
		}
	}
	
	
	public void restoreValues() {
		restoreSubtreeValues(root, 0);
		
		if (storeLegendFormats) {
			for (int i = 0; i < legends.length; i++) {
				legends[i].getFormats().assign(oldLegendFormats.get(i));
			}
		}
	}
	
	
	/**
	 * Assigns the previously saved line widths to the branches in the subtree under 
	 * root.
	 * @param root
	 * @param index - the index of the value for root in <code>oldWidths</code>
	 * @return the next unused index of <code>oldWidths</code>
	 */
	private int restoreSubtreeValues(Node root, int index) {
		for (int i = 0; i < targetAdapters.length; i++) {
			targetAdapters[i].setDistance(oldTreeValues[i].get(index).floatValue(), root);
		}
		index++;
		
		for (int i = 0; i < root.getChildren().size(); i++) {
			index = restoreSubtreeValues(root.getChildren().get(i), index);
		}
		return index;
	}	
}