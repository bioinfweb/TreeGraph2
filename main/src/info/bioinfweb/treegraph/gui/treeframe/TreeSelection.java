/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2013  Ben St�ver, Kai M�ller
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


import info.bioinfweb.treegraph.document.ConcretePaintableElement;
import info.bioinfweb.treegraph.document.Label;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.PaintableElement;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeSet;
import java.util.Vector;



/**
 * This class manages the selection of multiple elements in an tree view panel.
 * 
 * @author Ben St&ouml;ver
 */
public class TreeSelection {
	private TreeViewPanel owner = null;
	private HashSet<ConcretePaintableElement> elements = new HashSet<ConcretePaintableElement>();
	private boolean valueIsAdjusting = false;


	public TreeSelection(TreeViewPanel owner) {
		super();
		this.owner = owner;
	}
	
	
	public ConcretePaintableElement first() {
		return elements.iterator().next();
	}


	public Iterator<ConcretePaintableElement> iterator() {
		return elements.iterator();
	}


	public <T> T[] toArray(T[] arr) {
		return elements.toArray(arr);
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
	 * @param element - the element to select or <code>nil</code> if no element should be#
	 *        selected
	 */
	public void set(ConcretePaintableElement element) {
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
			throw new IllegalArgumentException("An element can only be selected if it is contained in the associated tree.");
		}
	}
	
	
	public void add(ConcretePaintableElement element) {
		if ((element != null) && owner.getDocument().getTree().contains(element)) {
			if (!contains(element)) {  // kein Element doppelt hinzufg.
				elements.add(element);
				if (!valueIsAdjusting) {
					owner.scrollElementToVisible(element);
					owner.fireSelectionChanged();
				}
			}
		}
		else {
			throw new IllegalArgumentException("An element can only be added to the selection if it is contained in the associated tree and not nil.");
		}
	}
	
	
	public boolean addAll(Collection<? extends ConcretePaintableElement> collection) {
		boolean result = elements.addAll(collection);
		if (result && !valueIsAdjusting) {
			owner.scrollElementToVisible(collection.iterator().next());
			owner.fireSelectionChanged();
		}
		return result;
	}


	public void remove(ConcretePaintableElement element) {
		if (element != null) {
			elements.remove(element);
			if (!valueIsAdjusting) {
				owner.fireSelectionChanged();
			}
		}
	}
	
	
	public void clear() {
		set(null);
	}
	
	
	/**
	 * Returns the number of selected elements.
	 * @see #elementCount(Class)
	 */
	public int size() {
		return elements.size();
	}


	public boolean isEmpty() {
		return elements.isEmpty();
	}


	public boolean contains(ConcretePaintableElement element) {
		return elements.contains(element);
	}
	
	
	/**
	 * Tests if at least one elements of the specified type is selected.
	 * @param elementClass - defines the element type
	 * @return <code>true</code> at least one is selected
	 * @since 2.0.43
	 * @see #isEmpty()
	 */
	public boolean containsType(Class<? extends PaintableElement> elementClass) {
		Iterator<ConcretePaintableElement> iterator = iterator();
		while (iterator.hasNext()) {
			if (elementClass.isInstance(iterator.next())) {
				return true;
			}
		}
		return false;
	}
	
	
	/**
	 * Tests if only elements of the specified type are selected.
	 * @param elementClass - defines the element type
	 * @return <code>true</code> if no other elements are selected
	 * @since 2.0.43
	 */
	public boolean containsOnlyType(Class<? extends PaintableElement> elementClass) {
		Iterator<ConcretePaintableElement> iterator = iterator();
		while (iterator.hasNext()) {
			if (!elementClass.isInstance(iterator.next())) {
				return false;
			}
		}
		return true;
	}
	
	
	/**
	 * Returns the first selected element of the specified type.
	 * @param <T> - the class of the returned element (determined by <code>elementClass</code>)
	 * @param elementClass - the class of the sought-after element
	 * @return the sought-after element or <code>null</code> of none of this type is selected
	 * @since 2.0.43
	 */
	public <T extends PaintableElement> T getFirstElementOfType(Class<T> elementClass) {
		Iterator<ConcretePaintableElement> iterator = iterator();
		while (iterator.hasNext()) {
			ConcretePaintableElement element = iterator.next(); 
			if (elementClass.isInstance(element)) {
				return (T)element;
			}
		}
  	return null;
	}
	
	
  public Node getFirstLeaf() {
		Iterator<ConcretePaintableElement> iterator = iterator();
		while (iterator.hasNext()) {
			ConcretePaintableElement element = iterator.next();
			if ((element instanceof Node) && ((Node)element).isLeaf()) {
				return (Node)element;
			}
		}
  	return null;
  }
  
  
	/**
	 * Returns all selected elements of the specified type.
	 * @param <T> - the class of the returned elements (determined by <code>elementClass</code>)
	 * @param elementClass - the class of the sought-after elements
	 * @return an array of the sought-after elements
	 * @since 2.0.43
	 */
	public <T extends PaintableElement> T[] getAllElementsOfType(Class<T> elementClass) {
		LinkedList<PaintableElement> list = new LinkedList<PaintableElement>();
		
		Iterator<ConcretePaintableElement> iterator = iterator();
		while (iterator.hasNext()) {
			ConcretePaintableElement element = iterator.next();
			if (elementClass.isInstance(element)) {
				list.add(element);
			}
		}
  	return list.toArray((T[])Array.newInstance(elementClass, list.size()));
	}
	
	
  /**
   * Returns a list of the IDs of the labels that are currently selected. If a label
   * without an ID is selected <code>""</code> is included in the result.
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
	 * @param elementClass - the class of the elements to be counted
	 * @return the number of elements
	 * @since 2.0.43
	 * @see #size()
	 */
	public int elementCount(Class<? extends PaintableElement> elementClass) {
		int result = 0;
		
		Iterator<ConcretePaintableElement> iterator = iterator();
		while (iterator.hasNext()) {
			if (elementClass.isInstance(iterator.next())) {
				result++;
			}
		}
  	return result;
	}
}