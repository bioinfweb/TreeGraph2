package info.bioinfweb.treegraph.document.undo.file.ancestralstate;


import info.bioinfweb.commons.Math2;
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
					nodes.get(nodeNames.get(i)).addToProbability(headingParts[0], headingParts[1], Math2.parseDouble(parts[i]));
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
				if (reader.isNext(MRCA_COMMAND)) {
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
