package info.bioinfweb.treegraph.document.undo.file.ancestralstate;


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
	
	
	public SortedMap<String, SortedMap<String, Double>> getCharacterMap() {
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
	
	
	public int getStateCountPerCharacter (String character) {
		return characterMap.get(character).size();
	}
	
	
	@Deprecated
	public String[] getHeadings() {
		String[] headings = new String[getCharacterStateCount()];
		Iterator<String> iterator1 = characterMap.keySet().iterator();
		int count = 0;
		while (iterator1.hasNext()) {
			String characterName = iterator1.next();
			Iterator<String> iterator2 = characterMap.get(characterName).keySet().iterator();
			while (iterator2.hasNext()) {
				String characterStateName = iterator2.next();
				headings[count] = characterName + "." + characterStateName;
				count += 1;
			}
		}
		return headings;
	}
	
	
	public int getCharacterCount() {
		return characterMap.size();
	}
	
	
	public double normalizeProbability(String characterName, String characterStateName, double lineCounter) {
		Double value = characterMap.get(characterName).get(characterStateName);
		value /= lineCounter;
		characterMap.get(characterName).put(characterStateName, value);
		return value;
	}
	
	
	public double addToProbability(String characterName, String characterStateName, double addend) {		
		if (characterMap.get(characterName) == null) {			
			characterMap.put(characterName, new TreeMap<String, Double>());
		}
		
		Double value = characterMap.get(characterName).get(characterStateName);
		if (value == null) {
			value = 0.0;
			characterMap.get(characterName).put(characterStateName, value);
		}
		value += addend;
		characterMap.get(characterName).put(characterStateName, value);
		return value;
	}
}
