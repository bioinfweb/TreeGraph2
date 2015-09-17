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
package info.bioinfweb.treegraph.document.undo.file.ancestralstate;


import info.bioinfweb.commons.io.PeekReader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class BayesTraitsReader {
	public static final String ROOT_NAME = "Root";	
	private static final String MRCA_COMMAND = "MRCA:";
	private static final String NODE_COMMAND = "Node:";
	private static final String TABLE_START = "Iteration\t";
	
	private static final Pattern LEAF_NAME_PATTERN = Pattern.compile("\\s*\\d+\\s+(\\S+)\\s*");
	private static final Pattern HEADING = Pattern.compile("(\\S+)\\s\\-\\s(.+)");
	private static final Pattern CHARACTER_STATE_PATTERN = Pattern.compile("S\\((.+)\\)\\s\\-\\sP\\((.+)\\)");
	
	
	public BayesTraitsReader() {
		super();
	}
	
	
	public String[] getHeadingParts(String heading) {
		String[] characterStates = new String[2];
		Matcher matcher = CHARACTER_STATE_PATTERN.matcher(heading);
		if (matcher.matches()) {
			characterStates[0] = matcher.group(1);
			characterStates[1] = matcher.group(2);
		}
		return characterStates;
	}


	private AncestralStateData readMRCA(PeekReader reader) throws IOException {
		String line = reader.readLine().getSequence().toString();		
		String[] parts = line.split("\\s+");
		AncestralStateData result = new AncestralStateData(parts[1]);
		
		line = reader.peekLine().getSequence().toString();  //TODO Later: Handle reader.peekLine().isCompletelyRead() == false (buffer size exceeded or use very high buffer size) 
		Matcher matcher = LEAF_NAME_PATTERN.matcher(line);
		while (matcher.matches()) {	
			result.getLeafNames().add(matcher.group(1));
			reader.readLine();
			line = reader.peekLine().getSequence().toString();  //TODO Later: Handle reader.peekLine().isCompletelyRead() == false (buffer size exceeded or use very high buffer size)
			matcher = LEAF_NAME_PATTERN.matcher(line);
		}
		return result;
	}
	
	
	private void readTable(PeekReader reader, Map<String, AncestralStateData> nodes) throws IOException{
		String line = reader.readLine().getSequence().toString();		
		String[] parts = line.split("\\t");
		List<String> nodeNames = new ArrayList<String>();
		List<String> probabilityKeys = new ArrayList<String>();
		int lineCounter = 0;
		
		for (int i = 0; i < parts.length; i++) {
			Matcher matcher = HEADING.matcher(parts[i]);
			if (matcher.matches()) {
				nodeNames.add(matcher.group(1));
				probabilityKeys.add(matcher.group(2));			
			}
			else {
				nodeNames.add(null);
				probabilityKeys.add(null);		
			}
		}
		
		line = reader.peekLine().getSequence().toString();
		while (reader.peek() != -1) {
			parts = line.split("\\t");			
			for (int i = 0; i < parts.length; i++) {
				if (nodeNames.get(i) != null) {
					String[] headingParts = getHeadingParts(probabilityKeys.get(i));				
					nodes.get(nodeNames.get(i)).addToProbability(headingParts[0], headingParts[1], parts[i]);
				}
			}		
			lineCounter += 1;
			line = reader.readLine().getSequence().toString();
		}
		
		for (int i = 0; i < nodeNames.size(); i++) {
			if (nodeNames.get(i) != null) {
				String[] headingParts = getHeadingParts(probabilityKeys.get(i));
				nodes.get(nodeNames.get(i)).normalizeProbability(headingParts[0], headingParts[1], lineCounter);
			}
		}		
	}
	
	
	public Map<String, AncestralStateData> read(String fileName) throws IOException {
		PeekReader reader = new PeekReader(new BufferedReader(new FileReader(fileName)));
		
		Map<String, AncestralStateData> result = new TreeMap<String, AncestralStateData>();
		result.put(ROOT_NAME, new AncestralStateData(ROOT_NAME));
		try {
			while (reader.peek() != -1) {
				if (reader.isNext(MRCA_COMMAND) || reader.isNext(NODE_COMMAND)) {
					AncestralStateData data = readMRCA(reader);
					result.put(data.getName(), data);
				}
				else if (reader.isNext(TABLE_START)) {
					readTable(reader, result);
				}
				else {
					reader.readLine();
				}
			}
		}
		finally {
			reader.close();
		}
		return result;
	}
}
