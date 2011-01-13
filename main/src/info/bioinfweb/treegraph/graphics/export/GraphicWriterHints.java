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
package info.bioinfweb.treegraph.graphics.export;

import java.util.HashMap;
import java.util.Set;



public class GraphicWriterHints {
  private HashMap<String, Object> map = new HashMap<String, Object>();

  
	public Object put(String key, Object value) {
		return map.put(key, value);
	}
	

	public Object get(String key) {
		return map.get(key);
	}
	
	
	public String getString(String key, String defaultValue) {
		Object result = get(key);
		if (result != null) {
			return result.toString();
		}
		else {
			return defaultValue;
		}
	}

	
	public float getFloat(String key, float defaultValue) {
		Object result = get(key);
		if (result instanceof Float) {
			return (Float)result;
		}
		else {
			return defaultValue;
		}
	}

	
	public boolean getBoolean(String key, boolean defaultValue) {
		Object result = get(key);
		if (result instanceof Boolean) {
			return (Boolean)result;
		}
		else {
			return defaultValue;
		}
	}

	
	public Set<String> keySet() {
		return map.keySet();
	}

	
	public void clear() {
		map.clear();
	}

	
	public int size() {
		return map.size();
	}


	public boolean containsKey(String key) {
		return map.containsKey(key);
	}

	
}