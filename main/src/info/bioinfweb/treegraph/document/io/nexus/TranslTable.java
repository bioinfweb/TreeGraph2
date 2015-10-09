/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2015  Ben Stöver, Sarah Wiechers, Kai Müller
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
package info.bioinfweb.treegraph.document.io.nexus;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;



/**
 * Stores the content of a taxon table in a nexus document. It is possible to access taxon
 * names by their identifier or their position.
 * 
 * @author Ben St&ouml;ver
 */
public class TranslTable {
  private Map<String, Integer> keyMap = new TreeMap<String, Integer>();
  private List<String> nameList = new ArrayList<String>();
  
  
  public void add(String key, String value) {
  	keyMap.put(key, new Integer(size()));
  	nameList.add(value);
  }
  
  
  public int size() {
  	return nameList.size();
  }
  
  
  public String get(int pos) {
  	return nameList.get(pos);
  }
  
  
  public String get(String key) {
  	Integer index = keyMap.get(key);
  	if (index != null) {
  		return get(index.intValue());
  	}
  	else {
  		return null;
  	}
  }
  
  
  public void clear() {
  	keyMap.clear();
  	nameList.clear();
  }
}