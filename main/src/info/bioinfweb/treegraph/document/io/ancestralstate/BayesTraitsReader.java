/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2011, 2013-2017  Ben Stöver, Sarah Wiechers, Kai Müller
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


import info.bioinfweb.commons.io.PeekReader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class BayesTraitsReader {
	private static final int PEEK_BUFFER_SIZE = 1024 * 1024;
	
	public static final String ROOT_NAME = "Root";
	
	private static final String MRCA_COMMAND = "MRCA:";
	private static final String NODE_COMMAND = "Node:";
	private static final String BAYESIAN_TABLE_START = "Iteration\t";
	private static final String LIKELIHOOD_TABLE_START = "Tree No\t";
	
	private static final Pattern LEAF_NAME_PATTERN = Pattern.compile("\\s*\\d+\\s+(\\S+)\\s*");
	private static final Pattern HEADING = Pattern.compile("(\\S+)\\s(?:\\-\\s)?(\\w\\(.+\\))");  // Headings may have one of the following forms: "name - S(n) - P(n)", "name - P(n)", "name - P(n, m)", "name P(n)". (At least these are the forms observed until now.)  
	private static final Pattern STATE_PATTERN = Pattern.compile("P\\((.+)\\)");
	private static final Pattern SITE_AND_STATE_PATTERN = Pattern.compile("S\\((.+)\\)\\s-\\sP\\((.+)\\)");
	
	
	public BayesTraitsReader() {
		super();
	}
	
	
	public String[] getHeadingParts(String heading) throws IOException {
		String[] siteAndState = new String[2];
		Matcher stateMatcher = STATE_PATTERN.matcher(heading);
		Matcher siteAndStateMatcher = SITE_AND_STATE_PATTERN.matcher(heading);
		
		if (siteAndStateMatcher.matches()){
			siteAndState[0] = siteAndStateMatcher.group(1);
			siteAndState[1] = siteAndStateMatcher.group(2);
		}
		else if (stateMatcher.matches()) {
			siteAndState[0] = "0";
			siteAndState[1] = stateMatcher.group(1);			
		}
		else {
			throw new IOException("No character states could be identified in the input file.\n\n"
					+ "Please send a bug report or contact the developers, if your input file was correct.");
		}	
		
		if (siteAndState[1].contains(",")) {
			siteAndState[1] = "(" + siteAndState[1] +")";
		}
		
		return siteAndState;
	}


	private CharSequence peekMRCALine(PeekReader reader) throws IOException {
		PeekReader.ReadResult result = reader.peekLine();
		if (result.isCompletelyRead()) {
			return result.getSequence();
		}
		else {  //TODO Is there a better way for solving this? Is it at all likely, that MRCA lines are that long?
			throw new IOException("A line in the input file containing an MRCA definition was longer than the reader buffer. "
					+ "Please send a bug report or contact the developers, if your input file was correct.");
		}
	}
	
	
	private AncestralStateData readMRCA(PeekReader reader) throws IOException {
		String line = reader.readLine().getSequence().toString();		
		String[] parts = line.split("\\s+");
		AncestralStateData result = new AncestralStateData(parts[1]);
		
		line = peekMRCALine(reader).toString(); 
		Matcher matcher = LEAF_NAME_PATTERN.matcher(line);
		while (matcher.matches()) {	
			result.getLeafNames().add(matcher.group(1));
			reader.readLine();
			line = peekMRCALine(reader).toString();
			matcher = LEAF_NAME_PATTERN.matcher(line);
		}
		return result;
	}
	
	
	private void readTable(PeekReader reader, Map<String, AncestralStateData> nodes) throws IOException {
		String line = reader.readLine().getSequence().toString();		
		String[] parts = line.split("\\t");
		List<String> nodeNames = new ArrayList<String>();
		List<String> probabilityKeys = new ArrayList<String>();
		
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
		
		while (reader.peek() != -1) {
			line = reader.readLine().getSequence().toString();
			parts = line.split("\\t");
			for (int i = 0; i < parts.length; i++) {
				if (nodeNames.get(i) != null) {
					String[] headingParts = getHeadingParts(probabilityKeys.get(i));
					nodes.get(nodeNames.get(i)).addToProbability(headingParts[0], headingParts[1], parts[i]);
				}
			}		
		}		
		
		for (int i = 0; i < nodeNames.size(); i++) {
			if (nodeNames.get(i) != null) {
				String[] headingParts = getHeadingParts(probabilityKeys.get(i));
				nodes.get(nodeNames.get(i)).normalizeProbability(headingParts[0], headingParts[1]);
			}
		}		
	}
	
	
	public Map<String, AncestralStateData> read(String fileName) throws IOException {
		PeekReader reader = new PeekReader(new BufferedReader(new FileReader(fileName)), PEEK_BUFFER_SIZE);
		
		Map<String, AncestralStateData> result = new HashMap<String, AncestralStateData>();
		result.put(ROOT_NAME, new AncestralStateData(ROOT_NAME));
		try {
			while (reader.peek() != -1) {
				if (reader.isNext(MRCA_COMMAND) || reader.isNext(NODE_COMMAND)) {
					AncestralStateData data = readMRCA(reader);
					result.put(data.getName(), data);
				}
				else if (reader.isNext(BAYESIAN_TABLE_START) || reader.isNext(LIKELIHOOD_TABLE_START)) {
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
