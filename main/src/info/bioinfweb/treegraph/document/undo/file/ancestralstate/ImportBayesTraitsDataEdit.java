package info.bioinfweb.treegraph.document.undo.file.ancestralstate;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import info.bioinfweb.treegraph.document.Branch;
import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.PieChartLabel;
import info.bioinfweb.treegraph.document.TextElementData;
import info.bioinfweb.treegraph.document.undo.WarningMessageEdit;
import info.bioinfweb.treegraph.document.undo.edit.InsertLabelsEdit;
import info.bioinfweb.treegraph.document.undo.file.AddSupportValuesEdit;
import info.bioinfweb.treegraph.document.undo.topologicalcalculation.AbstractTopologicalCalculationEdit;
import info.bioinfweb.treegraph.document.undo.topologicalcalculation.LeafSet;
import info.bioinfweb.treegraph.gui.actions.file.ImportTableAction;



public class ImportBayesTraitsDataEdit extends AbstractTopologicalCalculationEdit implements WarningMessageEdit {
	private AncestralStateImportParameters parameters;	

	private Set<String> nodesNotFound = new TreeSet<String>();


	public ImportBayesTraitsDataEdit(Document document,
			AncestralStateImportParameters parameters) {
		
		super(document, parameters.getKeyAdapter(), false);  //TODO User parameter for processRooted needed?
		this.parameters = parameters;
	}
	
	
	public boolean isAllNodesFound() {
		return nodesNotFound.isEmpty();
	}

	
	public Set<String> getNodesNotFound() {
		return nodesNotFound;
	}


	@Override
  public String getWarningText() {
	  return "";
  }


	@Override
  public boolean hasWarnings() {
	  return !isAllNodesFound();
  }


	private Node findReconstructedNode(AncestralStateData data) {
		LeafSet leafSet = new LeafSet(getLeafCount());
		if (data.getName().equals(BayesTraitsReader.ROOT_NAME)) {
			for (int i = 0; i < leafSet.size(); i++) {
				leafSet.setChild(i, true);
			}
		}
		else {
			Iterator<String> iterator = data.getLeafNames().iterator();
			while (iterator.hasNext()) {
			int index = getLeafIndex(parameters.createEditedValue(iterator.next()), parameters);
				if (index != -1 ) {				
					leafSet.setChild(index, true);
				}
				else {
					return null;
				}
			}
		}
		return findSourceNodeWithAllLeafs(getDocument().getTree().getPaintStart(), leafSet).getNode(); //TODO handle isDownwards() == false
	}


	@Override
	public String getPresentationName() {
		return "Import BayesTraits log data";
	}


	@Override
	protected void performRedo() {
		addLeafSets(getDocument().getTree().getPaintStart(), parameters.getKeyAdapter());
		
		for (String internalNodeName : parameters.getData().keySet()) {
			Node internalNode = findReconstructedNode(parameters.getData().get(internalNodeName));
			Branch branch = internalNode.getAfferentBranch();
			int count = 0;
			
			Iterator<String> iterator1 = parameters.getData().get(internalNodeName).getCharacterMap().keySet().iterator();
			while (iterator1.hasNext()) {
				String character = iterator1.next();
				PieChartLabel label = new PieChartLabel(branch.getLabels());
				label.setID(character);
				Iterator<String> iterator2 = parameters.getData().get(internalNodeName).getCharacterMap().get(character).keySet().iterator();
				while (iterator2.hasNext()) {
					String characterState = iterator2.next();
					label.addValueID(parameters.getData().get(internalNodeName).getHeadings()[count]);
					if (internalNode != null) {
						parameters.getImportAdapters()[count].
						setDecimal(internalNode, parameters.getData().get(internalNodeName).getCharacterMap().get(character).get(characterState));
					}
					else {
						nodesNotFound.add(parameters.getData().get(internalNodeName).getName());
					}	
					count += 1;
				}
				internalNode.getAfferentBranch().getLabels().add(label);
			}	
		}		
//		for (int i = 0; i < parameters.getData().get("Root").getProbabilitySize(); i += 2) {
//			PieChartLabel label = new PieChartLabel(null);
//			label.setID("Label" + i);
//			label.addValueID(parameters.getData().get("Root").getProbabilityKey(i));
//			label.addValueID(parameters.getData().get("Root").getProbabilityKey(i+1));
//			InsertLabelsEdit labelsEdit = new InsertLabelsEdit(getDocument(), label, branches);
//			getDocument().executeEdit(labelsEdit);
//		}		
	}
}