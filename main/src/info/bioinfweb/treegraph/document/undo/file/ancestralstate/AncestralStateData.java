package info.bioinfweb.treegraph.document.undo.file.ancestralstate;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;



public class AncestralStateData {
	private String name;
	private List<String> leafNames = new ArrayList<String>();
	
	private List<String> probabilityKeys = new ArrayList<String>();
	private Map<String, Double> probabilities = new TreeMap<String, Double>();
	
	
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
	
	
	public Double getProbability(String key) {
		return probabilities.get(key);
	}
	
	
	public Double getProbability(int index) {
		return probabilities.get(probabilityKeys.get(index));
	}
	
	
	public String getProbabilityKey(int index) {
		return probabilityKeys.get(index);
	}
	
	
	public int getProbabilitySize() {
		return probabilityKeys.size();
	}
		
	
	public double normalizeProbability(String name, double lineCounter) {
		Double value = probabilities.get(name);
		value /= lineCounter;
		probabilities.put(name, value);
		return value;
	}
	
	
	public double addToProbability(String name, double addend) {
		Double value = getProbability(name);
		if (value == null) {
			value = 0.0;
			probabilityKeys.add(name);
		}
		value += addend;
		probabilities.put(name, value);
		return value;
	}
}
