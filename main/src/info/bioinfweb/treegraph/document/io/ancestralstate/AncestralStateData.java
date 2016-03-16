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
package info.bioinfweb.treegraph.document.io.ancestralstate;


import info.bioinfweb.commons.Math2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;



public class AncestralStateData {
	private String name;
	private List<String> leafNames = new ArrayList<String>();
	private SortedMap<String, SortedMap<String, Double>> characterMap = new TreeMap<String, SortedMap<String, Double>>();
	
	
	public AncestralStateData(String name) {
		super();
		this.name = name;
	}


	public String getName() {
		return name;
	}
	
	
	public List<String> getLeafNames() {
		return leafNames;
	}
	
	
	public SortedMap<String, SortedMap<String, Double>> getSiteMap() {
		return characterMap;
	}


	public int getCharacterStateCount() {
		int count = 0;
		Iterator<String> iterator = characterMap.keySet().iterator(); 
		while (iterator.hasNext()) {
			count += characterMap.get(iterator.next()).size();			
		}
		return count;
	}
	
	
	public int getStateCountPerSite (String character) {
		return characterMap.get(character).size();
	}
	
	
	public int getSiteCount() {
		return characterMap.size();
	}
	
	
	public void normalizeProbability(String siteName, String stateName, double lineCounter) {
		Double value = characterMap.get(siteName).get(stateName);
		if (value != null) {
			value /= lineCounter;
			characterMap.get(siteName).put(stateName, value);
		}
	}
	
	
	public void addToProbability(String siteName, String stateName, String addend) {		
		if (characterMap.get(siteName) == null) {			
			characterMap.put(siteName, new TreeMap<String, Double>());
		}
		if (!addend.equals("--")) {
			double addValue = Math2.parseDouble(addend);
			Double value = characterMap.get(siteName).get(stateName);
			if (value == null) {
				value = 0.0;
				characterMap.get(siteName).put(stateName, value);
			}
			value += addValue;
			characterMap.get(siteName).put(stateName, value);
		}
		else {
			characterMap.get(siteName).put(stateName, null);
		}
	}
}
