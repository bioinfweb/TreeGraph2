package info.bioinfweb.treegraph.document.undo.file.ancestralstate;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;



public class AncestralStateData {
	private String name;
	private List<String> leafNames = new ArrayList<String>();
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
	
	
	public Map<String, Double> getProbabilities() {
		return probabilities;
	}
	
	
	public double addToProbability(String name, double addend) {
		Double value = probabilities.get(name);
		if (value == null) {
			value = 0.0;
		}
		value += addend;
		probabilities.put(name, value);
		return value;
	}
}
