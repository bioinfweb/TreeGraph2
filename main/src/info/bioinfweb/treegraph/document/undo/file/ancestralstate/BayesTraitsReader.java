package info.bioinfweb.treegraph.document.undo.file.ancestralstate;

import info.bioinfweb.commons.io.PeekReader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class BayesTraitsReader {
	private static final String ROOT_NAME = "Root";	
	private static final String MRCA_COMMAND = "MRCA:";
	private static final String TABLE_START = "Iteration";
	private static final String CHARACTER_PREFIX = "S(";
	private static final String CHARACTER_STATE_PREFIX = "P(";
	private static final String SUFFIX = ")";
	
	private static final Pattern LEAF_NAME_PATTERN = Pattern.compile("\\s+\\d+\\s+(\\S+)\\s*");
	
	
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
	
	
	private void readTable(PeekReader reader, Map<String, AncestralStateData> nodes) {
		//TODO Zuordnungsarrays erzeugen AncestralStateData, probability keys
		//TODO Datenzeilen lesen und addieren, Zeilen zählen
		//TODO Durchschnitt berechnen
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
			}
		}
		finally {
			reader.close();
		}
		return result;
	}
}
