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
package info.bioinfweb.treegraph.document;


import info.webinsel.util.RandomValues;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Vector;



public class IDManager {
	public static final String RAND_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ01234567890";
	public static final int RAND_LENGTH = 12;
	
	
	private static final Comparator<String> STRING_COMPARATOR = new Comparator<String>() {
			   public int compare(String s1, String s2){
			      return s1.compareTo(s2);
			   }
			};
	
	
  /**
   * Adds all label IDs to the specified list. If one ID is present several times, it is 
   * only added once. Empty IDs ("") are not added. 
   * @param labels
   * @param above
   * @param list
   */
  private static void searchLabelIDsInLabelBlock(Labels labels, boolean above, Class<? extends Label> labelClass, Vector<String> list) {
  	
  	for (int lineNo = 0; lineNo < labels.lineCount(above); lineNo++) {
			for (int lineIndex = 0; lineIndex < labels.labelCount(above, lineNo); lineIndex++) {
				Label l = labels.get(above, lineNo, lineIndex);
				String id = l.getID();
				if (!id.equals("") && (id != null) && !list.contains(id) && labelClass.isInstance(l)) {
					list.add(id);
				}
			}
		}
  }
  
  
  private static void searchHiddenDataIDs(HiddenDataElement element, Vector<String> list) {
  	Iterator<String> iterator = element.getHiddenDataMap().idIterator();
  	while (iterator.hasNext()) {
  		String id = iterator.next(); 
			if ((!id.equals("")) && !list.contains(id)) {
				list.add(id);
			}
  	}
  }
  
  
  private static void searchIDsInSubtree(Node root, Vector<String> list, Class<? extends Label> labelClass, 
  		boolean includeNodeHiddenData,	boolean includeBranchHiddenData) {
  	
  	if (root != null) {
	  	if (root.hasAfferentBranch()) {
	  		Labels labels = root.getAfferentBranch().getLabels();
	  		if (labelClass != null) {
	  			searchLabelIDsInLabelBlock(labels, true, labelClass, list);
	    		searchLabelIDsInLabelBlock(labels, false, labelClass, list);
	  		}
	  		if (includeNodeHiddenData) {
	  			searchHiddenDataIDs(root, list);
	  		}
	  		if (includeBranchHiddenData) {
	  			searchHiddenDataIDs(root.getAfferentBranch(), list);
	  		}
	  	}
	  	
	  	for (int i = 0; i < root.getChildren().size(); i++) {
				searchIDsInSubtree(root.getChildren().get(i), list, 
						labelClass, includeNodeHiddenData, includeBranchHiddenData);
			}
  	}
  }
  
  
  /**
   * Searches for all IDs (hidden data and label IDs) present in the subtree under 
   * root.
   * @param root - the root node of the subtree to be searched. 
   * @return a list of all IDs (every string is contained only once)
   */
  public static String[] getIDs(Node root) {
  	Vector<String> list = getIDVector(root);
  	return list.toArray(new String[list.size()]);
  }
  
  
  /**
   * Test whether the subtree under <code>root</code> contains at least one ID element (label or 
   * hidden data)
   * @param root
   * @return <code>true</code> if an ID element was found
   */
  public static boolean containsIDElements(Node root) {
    if (root == null) {
    	return false;
    }
  	if (!root.getAfferentBranch().getLabels().isEmpty() || !root.getHiddenDataMap().isEmpty() ||
  			!root.getAfferentBranch().getHiddenDataMap().isEmpty()) {
  		return true;
  	}
  	
  	for (int i = 0; i < root.getChildren().size(); i++) {
  		if (containsIDElements(root.getChildren().get(i))) {
  			return true;
  		}
		}
  	return false;
  }
  
  
  /**
   * Searches for all label IDs present in the subtree under root.
   * @param root - the root node of the subtree to be searched. 
   * @return a list of all IDs (every string is contained only once)
   */
  public static String[] getLabelIDs(Node root, Class<? extends Label> labelClass) {
  	Vector<String> list = getLabelIDVector(root, labelClass);
  	return list.toArray(new String[list.size()]);
  }
  
  
  /**
   * Searches for all the IDs of hidden data that is assigned to a branch present in the 
   * subtree under root.
   * @param root - the root node of the subtree to be searched. 
   * @return a list of all IDs (every string is contained only once)
   */
  public static String[] getHiddenBranchDataIDs(Node root) {
  	Vector<String> list = getHiddenBranchDataIDVector(root);
  	return list.toArray(new String[list.size()]);
  }
  
  
  /**
   * Searches for all the IDs of hidden data that is assigned to a node present in the 
   * subtree under root.
   * @param root - the root node of the subtree to be searched. 
   * @return a list of all IDs (every string is contained only once)
   */
  public static String[] getHiddenNodeDataIDs(Node root) {
  	Vector<String> list = getHiddenNodeDataIDVector(root);
  	return list.toArray(new String[list.size()]);
  }
  
  
  /**
   * Returns a vector of IDs which is sorted alphabetically.
   */
  private static Vector<String> getVector(Node root, Class<? extends Label> labelClass, 
  		boolean includeNodeHiddenData, boolean includeBranchHiddenData) {
  	
  	Vector<String> list = new Vector<String>();
  	searchIDsInSubtree(root, list, labelClass, includeNodeHiddenData, 
  			includeBranchHiddenData);
  	Collections.sort(list, STRING_COMPARATOR);
  	return list;
  }
  
  
  /**
   * Searches for all IDs (labels, hidden data) present in the subtree under root.
   * @param root - the root node of the subtree to be searched. 
   * @return a list of all IDs (every string is contained only once)
   */
  public static Vector<String> getIDVector(Node root) {
  	return getVector(root, Label.class, true, true);
  }
  
  
  /**
   * Searches for all label IDs present in the subtree under root.
   * @param root - the root node of the subtree to be searched. 
   * @return a list of all label IDs (every string is contained only once)
   */
  public static Vector<String> getLabelIDVector(Node root, Class<? extends Label> labelClass) {
  	return getVector(root, labelClass, false, false);
  }
  
  
  public static Vector<String> getHiddenBranchDataIDVector(Node root) {
  	return getVector(root, null, false, true);
  }
  
  
  public static Vector<String> getHiddenNodeDataIDVector(Node root) {
  	return getVector(root, null, true, false);
  }
  
  
  private static void renameLabelIDInLabelBlock(String oldName, String newName, 
  		Labels labels, boolean above) {
  	
  	for (int lineNo = 0; lineNo < labels.lineCount(above); lineNo++) {
			for (int lineIndex = 0; lineIndex < labels.labelCount(above, lineNo); lineIndex++) {
				Label l = labels.get(above, lineNo, lineIndex);
				if (oldName.equals(l.getID())) {
					l.setID(newName);
				}
			}
		}
  }
  
  
  private static void renameHiddenDataID(HiddenDataElement element, String oldName, 
  		String newName) {
  	
  	TextElementData value = element.getHiddenDataMap().get(oldName);
  	if (value != null) {
  		element.getHiddenDataMap().remove(oldName);
  		element.getHiddenDataMap().put(newName, value);
  	}
  }
  
  
  /**
   * Changes all occurrences of the given ID in the subtree under root.
   * @param root - the root node of the subtree where the renaming should take place 
   */
  private static void renameIDSubtree(String oldName, String newName, Node root) {
  	if (root.hasAfferentBranch()) {
  		renameLabelIDInLabelBlock(oldName, newName, 
  				root.getAfferentBranch().getLabels(), true);
  		renameLabelIDInLabelBlock(oldName, newName, 
  				root.getAfferentBranch().getLabels(), false);
  		renameHiddenDataID(root.getAfferentBranch(), oldName, newName);
  		renameHiddenDataID(root, oldName, newName);
  	}
  	
  	for (int i = 0; i < root.getChildren().size(); i++) {
			renameIDSubtree(oldName, newName, root.getChildren().get(i));
		}
  }
  
  
  /**
   * Updates the renamed ID if it is referenced by pie chart label in the subtree under 
   * <code>root</code>.  
   * @param oldName - the current ID
   * @param newName - the new ID
   * @param root - the root of the subtree to be updated
   */
  private static void updatePieChartLabels(String oldName, String newName, Node root) {
  	PieChartLabel[] labels = root.getAfferentBranch().getLabels().toPieChartLabelArray();
  	for (int i = 0; i < labels.length; i++) {
			for (int j = 0; j < labels[i].valueCount(); j++) {
				if (oldName.equals(labels[i].getValueID(j))) {
					labels[i].setValueID(j, newName);
				}
			}
		}
  	
  	for (int i = 0; i < root.getChildren().size(); i++) {
			updatePieChartLabels(oldName, newName, root.getChildren().get(i));
		}
  }
  
  
  /**
   * Renames a data ID in the specified tree and updates possible links to this ID in pie chart labels.
   * @param oldName - the current ID
   * @param newName - the new ID
   * @param tree - the tree to rename the IDs in
   */
  public static void renameID(String oldName, String newName, Tree tree) {
  	if (!tree.isEmpty()) {
  		renameIDSubtree(oldName, newName, tree.getPaintStart());
  		updatePieChartLabels(oldName, newName, tree.getPaintStart());
  	}
  }
  
  
  public static String newID(Vector<String> ids) {
  	String result;
  	do {
  		result = RandomValues.randChars(RAND_CHARS, RAND_LENGTH);
  	} while (ids.contains(result));
  	return result;
  }
  
  
  /**
   * Returns the text element data specified by the given ID which is linked to this node (either from a text 
   * label, hidden node data or hidden branch data). (The subtree is not searched.)  
   * @param node - the node to which the returned value is linked
   * @return the data or <code>null</code> of the specified ID is not present
   * @since 2.0.43
   */
  public static TextElementData getDataByID(Node node, String id) {
  	TextElementData result = null;
  	Label l = node.getAfferentBranch().getLabels().get(id);
  	if ((l != null) && (l instanceof TextLabel)) {
  		result = ((TextLabel)l).getData();
  	}
  	else {
  		result = node.getHiddenDataMap().get(id);
  		if (result == null) {
    		result = node.getAfferentBranch().getHiddenDataMap().get(id);
  		}
  	}
  	return result;
  }
  
  
  /**
   * Returns the first label of the specified type with the specified ID to be found in the subtree under <code>root</code>.
   * @param root - the root of the subtree to be searched
   * @param elementClass - the class defining the sought-after type(s) of labels
   * @param id - the ID of the sought-after label
   * @return the label of <code>null</code> of none was found
   */
  public static Label getFirstLabel(Node root, Class<? extends Label> elementClass, String id) {
  	Label l = root.getAfferentBranch().getLabels().get(id);  // Only the first label with the specified ID needs to be tested, because IDs should be unique on a node.
  	if (elementClass.isInstance(l)) {
  		return l;
  	}
  	else {
  		for (int i = 0; i < root.getChildren().size(); i++) {
				l = getFirstLabel(root.getChildren().get(i), elementClass, id);
				if (elementClass.isInstance(l)) {
					return l;
				}
			}
  	}
  	return null;
  }
  
  
  
  public static boolean idExists(Node root, String id) {
  	return getIDVector(root).contains(id);
  }
}