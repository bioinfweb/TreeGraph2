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
package info.bioinfweb.treegraph.document;


import info.bioinfweb.treegraph.document.tools.IDManager;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;



public class HiddenDataMap implements Map<String, TextElementData> {
  private HashMap<String, TextElementData> map = new HashMap<String, TextElementData>();
  private Node owner = null;

  
	/**
	 * Creates a new hidden data map.
	 * 
	 * @param owner - the node this map (or its branch) is attached to
	 * @since 2.0.48
	 */
	public HiddenDataMap(Node owner) {
		super();
		this.owner = owner;
	}


	/**
	 * Returns the node this map (or its branch) is attached to.
	 * 
	 * @since 2.0.48
	 */
	public Node getOwner() {
		return owner;
	}


	/* (non-Javadoc)
	 * @see java.util.Map#clear()
	 */
	@Override
	public void clear() {
		map.clear();
	}

	
	/* (non-Javadoc)
	 * @see java.util.Map#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	
	/**
	 * Adds a new entry to this map. Current entries with this ID attached to the same node are replaced. 
	 * (This includes hidden data entries in other maps as well as labels.) In contrast to 
	 * {@link #put(String, TextElementData)} which implements the {@link Map} interface, this method has the 
	 * return type {@link Object} and returns any element that was previously associated with the specified ID.
	 * 
	 * @return the previous element which was attached to this node under this ID (This can either 
	 *         be a {@link TextElementData} object, if a hidden data entry was replaced or an instance 
	 *         of a class inherited from {@link Label}), if a label with the specified ID was attached 
	 *         to the node attached to this map.
   *         
	 * @see #put(String, TextElementData)
	 */
	public Object putForID(String id, TextElementData value) {
		Object result = null;
		if (getOwner() != null) {
			result = IDManager.removeElementWithID(getOwner(), id);
		}
		map.put(id, value);
		return result;
	}

	
	/**
	 * Adds a new entry to this map. Current entries with this ID attached to the same node are replaced. 
	 * (This includes hidden data entries in other maps as well as labels.)
	 * 
	 * @return the previous {@link TextElementData} element which was attached to this node under this ID 
	 *         (This can either be a previous hidden data entry was replaced or the text of an {@link TextLabel}), 
	 *         if a text label with the specified ID was attached to the node. If another type of label (containing
	 *         no text was associated with the specified ID or nothing was stored for this ID, {@code null} is returned.        
	 * 
	 * @see #putForID(String, TextElementData)
	 */
	@Override
	public TextElementData put(String id, TextElementData value) {
		Object result = putForID(id, value);
		if (result instanceof TextElementData) {
			return (TextElementData)result;
		}
		else if (result instanceof TextElement) {  // return text of TextLabel
			return ((TextElement)result).getData();
		}
		else {
			return null;
		}
	}

	
	/* (non-Javadoc)
	 * @see java.util.Map#size()
	 */
	@Override
	public int size() {
		return map.size();
	}

	
	/* (non-Javadoc)
	 * @see java.util.Map#values()
	 */
	@Override
	public Collection<TextElementData> values() {
		return map.values();
	}


	public Iterator<String> idIterator() {
		return map.keySet().iterator();
	}


	/* (non-Javadoc)
	 * @see java.util.Map#containsKey(java.lang.Object)
	 */
	@Override
	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}


	/* (non-Javadoc)
	 * @see java.util.Map#containsValue(java.lang.Object)
	 */
	@Override
	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}


	/* (non-Javadoc)
	 * @see java.util.Map#entrySet()
	 */
	@Override
	public Set<Entry<String, TextElementData>> entrySet() {
		return map.entrySet();
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object other) {
		return map.equals(other);
	}


	/* (non-Javadoc)
	 * @see java.util.Map#get(java.lang.Object)
	 */
	@Override
	public TextElementData get(Object key) {
		return map.get(key);
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return map.hashCode();
	}


	/* (non-Javadoc)
	 * @see java.util.Map#keySet()
	 */
	@Override
	public Set<String> keySet() {
		return map.keySet();
	}


	/**
	 * Adds all elements of the specified map to this map.
	 * 
	 * @since 2.0.48
	 */
	@Override
	public void putAll(Map<? extends String, ? extends TextElementData> m) {
		// This method cannot be delegated to map.putAll() because it would not remove labels and 
		// elements from the other map of this node which have the same ID.
		
		Iterator<? extends String> i = m.keySet().iterator();
		while (i.hasNext()) {
			String id = i.next();
			put(id, m.get(id));
		}
	}


	/* (non-Javadoc)
	 * @see java.util.Map#remove(java.lang.Object)
	 */
	@Override
	public TextElementData remove(Object key) {
		return map.remove(key);
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return map.toString();
	}
	
	
	/**
	 * Tests if the specified map contains one or more IDs that are also contained in this 
	 * map.
	 * 
	 * @param other - the other hidden data map to be compared
	 * @return {@code true} if at least one element with the same ID was found in each map, {@code false} otherwise.
	 */
	public boolean containsSameID(HiddenDataMap other) {
		Iterator<String> iterator = idIterator();
		while (iterator.hasNext()) {
			if (other.containsKey(iterator.next())) {
				return true;
			}
		}
		return false;
	}
	
	
	/**
	 * Clears the current contents of the map and fills it with copies of the contents
	 * of the given map.
	 * 
	 * @param other - the source map for the contents to be assigned
	 */
	public void assign(HiddenDataMap other) {
		clear();
		Iterator<String> iterator = other.idIterator();
		while (iterator.hasNext()) {
			String id = iterator.next();
			put(id, other.get(id).clone());
		}
	}
}