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
package info.bioinfweb.treegraph.gui.treeframe;


import info.bioinfweb.treegraph.document.Branch;
import info.bioinfweb.treegraph.document.PaintableElement;
import info.bioinfweb.treegraph.document.Label;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.TreeElement;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;



/**
 * This class manages the selection of multiple elements in a tree view panel.
 * 
 * @author Ben St&ouml;ver
 */
public class TreeSelection implements Collection<PaintableElement> {
	private TreeViewPanel owner = null;
	private HashSet<PaintableElement> elements = new HashSet<PaintableElement>();
	private boolean valueIsAdjusting = false;


	public TreeSelection(TreeViewPanel owner) {
		super();
		this.owner = owner;
	}
	
	
	private void fireSelectionChanged() {
		if (!valueIsAdjusting) {
			owner.fireSelectionChanged();
		}
	}
	
	
	public PaintableElement first() {
		return elements.iterator().next();
	}


	@Override
	public Iterator<PaintableElement> iterator() {
		return elements.iterator();
	}


	@Override
	public <T> T[] toArray(T[] arr) {
		return elements.toArray(arr);
	}


	@Override
  public Object[] toArray() {
	  return elements.toArray();
  }


	@Override
  public boolean containsAll(Collection<?> c) {
	  return elements.containsAll(c);
  }


	@Override
  public boolean removeAll(Collection<?> c) {
	  boolean result = elements.removeAll(c);
	  fireSelectionChanged();
	  return result;
  }


	@Override
  public boolean retainAll(Collection<?> c) {
		boolean result = elements.retainAll(c);
	  fireSelectionChanged();
	  return result;
  }


	public boolean getValueIsAdjusting() {
		return valueIsAdjusting;
	}


	/**
	 * Sets the <code>valueIsAdjusting</code> property, which indicates whether or not upcoming 
	 * selection changes should be considered part of a single change. If this property is set to
	 * <code>true</code> no changes are reported to the registered listeners until it is set to 
	 * <code>false</code> again. (The listeners are informed directly of the property is set back
	 * to <code>false</code>.)
	 * 
	 * @param valueIsAdjusting - the new value of the property
	 */
	public void setValueIsAdjusting(boolean valueIsAdjusting) {
		if (!valueIsAdjusting && this.valueIsAdjusting) {
			owner.fireSelectionChanged();
		}
		this.valueIsAdjusting = valueIsAdjusting;
	}


	/**
	 * Sets the specified element as the only selected element.
	 * 
	 * @param element - the element to select or {@code null} if no element should be selected
	 */
	public void set(PaintableElement element) {
		if ((element == null) || owner.getDocument().getTree().contains(element)) {
			elements.clear();
			
			if (element != null) {
				elements.add(element);
				if (!valueIsAdjusting) {
					owner.scrollElementToVisible(element);
				}
			}
			if (!valueIsAdjusting) {
				owner.fireSelectionChanged();
			}
		}
		else {
			throw new IllegalArgumentException("The element \"" + element + 
							"\" can only be selected if it is contained in the associated tree.");
		}
	}
	
	
	@Override
	public boolean add(PaintableElement element) {
		if (!owner.getDocument().getTree().contains(element)) {
			throw new IllegalArgumentException(
					"The element \"" + element + "\" can only be added to the selection if it is contained in the associated tree.");
		}
		else if (element != null) {
			boolean result = true;
			if (!contains(element)) {  // kein Element doppelt hinzufg.
				result = elements.add(element) && result;
				if (!valueIsAdjusting) {
					owner.scrollElementToVisible(element);
					owner.fireSelectionChanged();
				}
			}
			return result;
		}
		else {
			return false;
		}
	}
	
	
	@Override
	public boolean addAll(Collection<? extends PaintableElement> collection) {
		boolean result = elements.addAll(collection);
		if (result && !valueIsAdjusting) {
			owner.scrollElementToVisible(collection.iterator().next());
			owner.fireSelectionChanged();
		}
		return result;
	}


	@Override
	public boolean remove(Object element) {
		if (element != null) {
			boolean result = elements.remove(element);
			fireSelectionChanged();
			return result;
		}
		else {
			return false;
		}
	}
	
	
	@Override
	public void clear() {
		set(null);
	}
	
	
	/**
	 * Returns the number of selected elements.
	 * @see #elementCount(Class)
	 */
	@Override
	public int size() {
		return elements.size();
	}


	@Override
	public boolean isEmpty() {
		return elements.isEmpty();
	}


	@Override
	public boolean contains(Object element) {
		return elements.contains(element);
	}
	
	
	/**
	 * Tests if at least one elements of the specified type is selected.
	 * 
	 * @param elementClass - defines the element type
	 * @return <code>true</code> at least one is selected
	 * @since 2.0.43
	 * @see #isEmpty()
	 */
	public boolean containsType(Class<? extends PaintableElement> elementClass) {
		Iterator<PaintableElement> iterator = iterator();
		while (iterator.hasNext()) {
			if (elementClass.isInstance(iterator.next())) {
				return true;
			}
		}
		return false;
	}
	
	
	/**
	 * Tests if only elements of the specified type are selected.
	 * 
	 * @param elementClass - defines the element type
	 * @return <code>true</code> if no other elements are selected
	 * @since 2.0.43
	 */
	public boolean containsOnlyType(Class<? extends PaintableElement> elementClass) {
		boolean containsType = false;
		Iterator<PaintableElement> iterator = iterator();
		while (iterator.hasNext()) {
			if (elementClass.isInstance(iterator.next())) {
				containsType = true;
			}
			else {
				return false;
			}
		}
		return containsType;
	}
	
	
	/**
	 * Checks if the selection contains only terminal nodes.
	 * 
	 * @return {@code true} if only terminal nodes (leaves) are selected, {@code false otherwise}.
	 * @since 2.0.52
	 */
	public boolean containsOnlyLeafNodes() {
		boolean containsType = false;
		Iterator<PaintableElement> iterator = iterator();
		while (iterator.hasNext()) {
			PaintableElement element = iterator.next();
			if (!((element instanceof Node) && ((Node)element).isLeaf())) {
				return false;
			}
			else {
				containsType = true;
			}
		}
  	return containsType;
	}
	
	
	/**
	 * Returns the first selected element of the specified type.
	 * 
	 * @param <T> - the class of the returned element (determined by <code>elementClass</code>)
	 * @param elementClass - the class of the sought-after element
	 * @return the sought-after element or <code>null</code> of none of this type is selected
	 * @since 2.0.43
	 */
	public <T extends PaintableElement> T getFirstElementOfType(Class<T> elementClass) {
		Iterator<PaintableElement> iterator = iterator();
		while (iterator.hasNext()) {
			PaintableElement element = iterator.next(); 
			if (elementClass.isInstance(element)) {
				return (T)element;
			}
		}
  	return null;
	}
	
	
	public Node getFirstNodeBranchOrRoot() {
		Node root = getFirstElementOfType(Node.class);
		if (root == null) {
			Branch branch = getFirstElementOfType(Branch.class);
			if (branch != null) {
				root = branch.getTargetNode();
			}
			else {
				root = owner.getDocument().getTree().getPaintStart();
			}
		}
		return root;
	}
	
	
  public Node getFirstLeaf() {
		Iterator<PaintableElement> iterator = iterator();
		while (iterator.hasNext()) {
			PaintableElement element = iterator.next();
			if ((element instanceof Node) && ((Node)element).isLeaf()) {
				return (Node)element;
			}
		}
  	return null;
  }
  
  
	/**
	 * Returns all selected elements of the specified type.
	 * 
	 * @param <T> the class of the returned elements (determined by <code>elementClass</code>)
	 * @param elementClass the class of the sought-after elements
	 * @param leavesOnly Specify {@code true} here if only selected elements attached to leaf nodes or
	 *        {@code false} if all selected elements of the specified type shall be returned.
	 * @return an array of the sought-after elements
	 * @since 2.5.0
	 */
	@SuppressWarnings("unchecked")
	public <T extends PaintableElement> T[] getAllElementsOfType(Class<T> elementClass, boolean leavesOnly) {
		LinkedList<PaintableElement> list = new LinkedList<PaintableElement>();
		
		Iterator<PaintableElement> iterator = iterator();
		while (iterator.hasNext()) {
			PaintableElement element = iterator.next();
			if (elementClass.isInstance(element) && 
					(!leavesOnly || ((element instanceof TreeElement) && ((TreeElement)element).getLinkedNode().isLeaf()))) {
				
				list.add(element);
			}
		}
  	return list.toArray((T[])Array.newInstance(elementClass, list.size()));
	}
	
	
	/**
	 * Returns all selected elements of the specified type.
	 * 
	 * @param <T> - the class of the returned elements (determined by <code>elementClass</code>)
	 * @param elementClass - the class of the sought-after elements
	 * @return an array of the sought-after elements
	 * @since 2.0.43
	 */
	public <T extends PaintableElement> T[] getAllElementsOfType(Class<T> elementClass) {
		return getAllElementsOfType(elementClass, false);
	}
	
	
  /**
   * Returns a list of the IDs of the labels that are currently selected. If a label
   * without an ID is selected <code>""</code> is included in the result.
   * 
   * @return the list of IDs (Every ID is included only once.)
   */
  public String[] containedLabelIDs() {
		Vector<String> result = new Vector<String>();
		Label[] labels = getAllElementsOfType(Label.class);
		for (int i = 0; i < labels.length; i++) {
			String id = labels[i].getID();
			if (!result.contains(id)) {
				result.add(id);
			}
		}
		return result.toArray(new String[result.size()]);
  }
  
  
	/**
	 * Returns the number of all selected elements of the specified type.
	 * 
	 * @param elementClass - the class of the elements to be counted
	 * @return the number of elements
	 * @since 2.0.43
	 * @see #size()
	 */
	public int elementCount(Class<? extends PaintableElement> elementClass) {
		int result = 0;
		
		Iterator<PaintableElement> iterator = iterator();
		while (iterator.hasNext()) {
			if (elementClass.isInstance(iterator.next())) {
				result++;
			}
		}
  	return result;
	}
}