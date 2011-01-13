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


import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;



public class HiddenDataMap implements Cloneable, Map<String, TextElementData> {
	
  private HashMap<String, TextElementData> map = new HashMap<String, TextElementData>();

  
	/* (non-Javadoc)
	 * @see java.util.Map#clear()
	 */
	public void clear() {
		map.clear();
	}

	
	/* (non-Javadoc)
	 * @see java.util.Map#isEmpty()
	 */
	public boolean isEmpty() {
		return map.isEmpty();
	}

	
	/* (non-Javadoc)
	 * @see java.util.Map#put(java.lang.Object, java.lang.Object)
	 */
	public TextElementData put(String key, TextElementData value) {
		return map.put(key, value);
	}

	
	/* (non-Javadoc)
	 * @see java.util.Map#size()
	 */
	public int size() {
		return map.size();
	}

	
	/* (non-Javadoc)
	 * @see java.util.Map#values()
	 */
	public Collection<TextElementData> values() {
		return map.values();
	}


	public Iterator<String> idIterator() {
		return map.keySet().iterator();
	}


	/* (non-Javadoc)
	 * @see java.util.Map#containsKey(java.lang.Object)
	 */
	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}


	/* (non-Javadoc)
	 * @see java.util.Map#containsValue(java.lang.Object)
	 */
	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}


	/* (non-Javadoc)
	 * @see java.util.Map#entrySet()
	 */
	public Set<Entry<String, TextElementData>> entrySet() {
		return map.entrySet();
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object other) {
		return map.equals(other);
	}


	/* (non-Javadoc)
	 * @see java.util.Map#get(java.lang.Object)
	 */
	public TextElementData get(Object key) {
		return map.get(key);
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return map.hashCode();
	}


	/* (non-Javadoc)
	 * @see java.util.Map#keySet()
	 */
	public Set<String> keySet() {
		return map.keySet();
	}


	/* (non-Javadoc)
	 * @see java.util.Map#putAll(java.util.Map)
	 */
	public void putAll(Map<? extends String, ? extends TextElementData> m) {
		map.putAll(m);
	}


	/* (non-Javadoc)
	 * @see java.util.Map#remove(java.lang.Object)
	 */
	public TextElementData remove(Object key) {
		return map.remove(key);
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return map.toString();
	}
	
	
	/**
	 * Tests if the specified map contains one or more IDs that are also contained in this 
	 * map.
	 * @param other
	 * @return
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


	/** 
	 * Returns a deep copy of this map and its contents.
	 * @see java.lang.Object#clone()
	 */
	@Override
	public HiddenDataMap clone() {
		HiddenDataMap result = new HiddenDataMap();
		result.assign(this);
		return result;
	}
}