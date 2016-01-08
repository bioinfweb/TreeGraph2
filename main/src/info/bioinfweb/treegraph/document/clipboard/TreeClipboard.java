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
package info.bioinfweb.treegraph.document.clipboard;


import java.util.Vector;

import info.bioinfweb.treegraph.document.*;



/**
 * This class is a clipboard for tree elements. It can store a single label, a list of 
 * labels or a node with its subtree. Note that only one of the three types of elements
 * can be stored at one time.
 * 
 * @author Ben St&ouml;ver
 */
public class TreeClipboard {
  private Node subtreeRoot = null;
  private Legend[] subtreeLegends = null;
  private Vector<Label> labelList = new Vector<Label>();
  private Label label = null;
  private Legend legend = null;
  
  
  public void copySubtree(Tree tree, Node root) {
  	clear();
  	subtreeRoot = root.cloneWithSubtree(true);  //Unique names used for legends
    subtreeLegends = TreeSerializer.getLegendsInSubtree(tree, root);
    for (int i = 0; i < subtreeLegends.length; i++) {
			subtreeLegends[i] = subtreeLegends[i].clone();
		}
  }
  
  
  public void copyLabel(Label label) {
  	clear();
  	this.label = label.clone();
  }
  
  
  private void copyLabelBlock(Labels labels, boolean above) {
  	for (int lineNo = 0; lineNo < labels.lineCount(above); lineNo++) {
			for (int lineIndex = 0; lineIndex < labels.labelCount(above, lineNo); lineIndex++) {
				labelList.add(labels.get(above, lineNo, lineIndex).clone());
			}
		}
  }
  
  
  public void copyLabels(Labels labels) {
  	clear();
  	copyLabelBlock(labels, true);
  	copyLabelBlock(labels, false);
  }
  
  
  public void copyLegend(Legend legend) {
  	clear();
  	this.legend = legend.clone();
  }
  
  
	/**
	 * Returns a deep copy of the label in the clipboard.
	 * 
	 * @throws info.bioinfweb.treegraph.document.clipboard.TreeClipboardException if no label is stored. 
	 */
  public Label getLabel() {
  	if (label != null) {
  		return label.clone();
		}
		else {
			throw new TreeClipboardException("No label in clipboard");
		}
	}


	/**
	 * Returns a deep copy of the label list in the clipboard.
	 * 
	 * @throws info.bioinfweb.treegraph.document.clipboard.TreeClipboardException if no label list is stored. 
	 */
	public Label[] getLabelList() {
		if (!labelList.isEmpty()) {
			Label[] result = new Label[labelList.size()];
			for (int i = 0; i < labelList.size(); i++) {
				result[i] = labelList.get(i).clone();
			}
			return result;
		}
		else {
			throw new TreeClipboardException("No label list in clipboard");
		}
	}


	/**
	 * Returns a deep copy of the subtree in the clipboard. Note that the returned nodes have the same 
	 * unique names as the original nodes had.
	 * 
	 * @throws info.bioinfweb.treegraph.document.clipboard.TreeClipboardException if no subtree is stored. 
	 */
	public Node getSubtree() {
		if (subtreeRoot != null) {
			return subtreeRoot.cloneWithSubtree(true);
		}
		else {
			throw new TreeClipboardException("No subtree in clipboard");
		}
	}


	/**
	 * Returns deep copies of the legends attached to the subtree in the clipboard.
	 * 
	 * @return the legends or an empty array if the subtree contains no legends
	 * @throws info.bioinfweb.treegraph.document.clipboard.TreeClipboardException if no subtree is stored. 
	 */
	public Legend[] getSubtreeLegends() {
		if (subtreeRoot != null) {
			Legend[] result = new Legend[subtreeLegends.length];
			for (int i = 0; i < result.length; i++) {
				result[i] = subtreeLegends[i].clone();
			}
			return result;
		}
		else {
			throw new TreeClipboardException("No subtree in clipboard");
		}
	}


	/**
	 * Returns a deep copy of the legend in the clipboard.
	 * 
	 * @throws info.bioinfweb.treegraph.document.clipboard.TreeClipboardException if no label is stored. 
	 */
  public Legend getLegend() {
  	if (legend != null) {
  		return legend.clone();
		}
		else {
			throw new TreeClipboardException("No legend in clipboard");
		}
	}


	/**
	 * Clears all stored data (labelList, label, subtree).
	 */
	public void clear() {
  	subtreeRoot = null;
  	subtreeLegends = null;
  	labelList.clear();
  	label = null;
  	legend = null;
  }
  
  
  public boolean isEmpty() {
  	return (subtreeRoot == null) && labelList.isEmpty()	&& 
  	       (label == null) && (legend == null);
  }
  
  
  public ClipboardContentType getContentType() {
  	if (subtreeRoot != null) {
  		return ClipboardContentType.SUBTREE;
  	}
  	else if (label != null) {
  		return ClipboardContentType.LABEL;
  	}
  	else if (legend != null) {
  		return ClipboardContentType.LEGEND;
  	}
  	else if (!labelList.isEmpty()) {
  		return ClipboardContentType.LABELS;
  	}
  	else {
  		return ClipboardContentType.EMPTY;
  	}
  }
}