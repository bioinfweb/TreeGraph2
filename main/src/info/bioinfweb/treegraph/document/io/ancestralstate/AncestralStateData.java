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
package info.bioinfweb.treegraph.document.io.ancestralstate;


import info.bioinfweb.commons.Math2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections4.map.ListOrderedMap;



public class AncestralStateData {
	private static class ProbabilityData {
		public double probability = 0.0;
		public long elementCount = 0;
	}
	
	
	private String name;
	private List<String> leafNames = new ArrayList<String>();
	private ListOrderedMap<String, ListOrderedMap<String, ProbabilityData>> siteMap = new ListOrderedMap<String, ListOrderedMap<String, ProbabilityData>>();
	
	
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
	
	
	public Iterator<String> getSiteIterator() {
		return siteMap.keySet().iterator();
	}
	
	
	public Iterator<String> getKeyIterator(String siteName) {
		return siteMap.get(siteName).keySet().iterator();
	}
	
	
	public double getProbability(String siteName, String stateName) {
		ProbabilityData data = siteMap.get(siteName).get(stateName);
		if (data == null) {
			return Double.NaN;
		}
		else {
			return data.probability;
		}
	}
	
	
	public int getCharacterStateCount() {
		int count = 0;
		Iterator<String> iterator = siteMap.keySet().iterator(); 
		while (iterator.hasNext()) {
			count += siteMap.get(iterator.next()).size();			
		}
		return count;
	}
	
	
	public int getStateCountPerSite (String character) {
		return siteMap.get(character).size();
	}
	
	
	public int getSiteCount() {
		return siteMap.size();
	}
	
	
	public void normalizeProbability(String siteName, String stateName) {
		ProbabilityData data = siteMap.get(siteName).get(stateName);
		if (data != null) {
			data.probability /= data.elementCount;
			data.elementCount = -1;
		}
	}
	
	
	public void addToProbability(String siteName, String stateName, String addend) {		
		if (siteMap.get(siteName) == null) {			
			siteMap.put(siteName, new ListOrderedMap<String, ProbabilityData>());
		}
		if (!addend.equals("--")) {
			double addValue = Math2.parseDouble(addend);
			ProbabilityData data = siteMap.get(siteName).get(stateName);
			if (data == null) {
				data = new ProbabilityData();
				siteMap.get(siteName).put(stateName, data);
			}
			
			if (data.elementCount == -1) {
				throw new IllegalStateException("Value is already normalized.");
			}
			else {
				data.probability += addValue;
				data.elementCount++;
			}
		}
	}
}
