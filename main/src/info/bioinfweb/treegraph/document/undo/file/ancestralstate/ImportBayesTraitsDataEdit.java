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


import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import info.bioinfweb.treegraph.document.Branch;
import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.PieChartLabel;
import info.bioinfweb.treegraph.document.change.DocumentChangeType;
import info.bioinfweb.treegraph.document.nodebranchdata.IDElementAdapter;
import info.bioinfweb.treegraph.document.topologicalcalculation.LeafSet;
import info.bioinfweb.treegraph.document.undo.AbstractTopologicalCalculationEdit;
import info.bioinfweb.treegraph.document.undo.WarningMessageEdit;



public class ImportBayesTraitsDataEdit extends AbstractTopologicalCalculationEdit implements WarningMessageEdit {
	private AncestralStateImportParameters parameters;

	private Set<String> nodesNotFound = new TreeSet<String>();
	private Set<String> nodeDataNotFound = new TreeSet<String>();
	


	public ImportBayesTraitsDataEdit(Document document,
			AncestralStateImportParameters parameters) {
		
		super(document, DocumentChangeType.TOPOLOGICAL_BY_RENAMING, parameters.getKeyAdapter(), false);  //TODO User parameter for processRooted needed?
		this.parameters = parameters;
	}

	
	public boolean isAllNodesFound() {
		return nodesNotFound.isEmpty();
	}
	
	
	public boolean isAllNodeDataFound() {
		return nodeDataNotFound.isEmpty();
	}
	
	
	public Set<String> getNodesNotFound() {
		return nodesNotFound;
	}

	
	public Set<String> getNodeDataNotFound() {
		return nodeDataNotFound;
	}
	
	
	@Override
  public String getWarningText() {
		String message = "The following MRCA/node definitions could not be reconstructed, because one or more\n"
				+ "of the referenced terminal nodes is not contained in the current tree document:\n\n";
		Iterator<String> iterator = nodesNotFound.iterator(); 
		while (iterator.hasNext()) {
			message = message + "\"" + iterator.next() + "\"" + "\n";
		}
	  return message;
  }
	
	
  public String getNodeDataNotFoundWarningText() {
		String message = "No probability data for the following internal nodes could be found:\n\n";
		Iterator<String> iterator = nodeDataNotFound.iterator(); 
		while (iterator.hasNext()) {
			message = message + "\"" + iterator.next() + "\"" + "\n";
		}
		message = message + "\nMake sure the BayesTraits analysis worked correctly.";
	  return message;
  }
  

  @Override
  public boolean hasWarnings() {
	  return !isAllNodesFound();
  }
	
	
  public boolean hasNodeDataNotFoundWarnings() {
	  return !isAllNodeDataFound();
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
			int index = getLeafIndex(iterator.next());
				if (index != -1 ) {				
					leafSet.setChild(index, true);
				}
				else {
					return null;
				}
			}
		}
		return findSourceNodeWithAllLeafs(getDocument().getTree(), getDocument().getTree().getPaintStart(), leafSet).getNode(); //TODO handle isDownwards() == false
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
			
			if (internalNode != null) {
				Branch branch = internalNode.getAfferentBranch();
				int importAdapterIndex = 0;
				Iterator<String> characterIterator = parameters.getData().get(internalNodeName).getCharacterMap().keySet().iterator();
				int characterIndex = 0;
				parameters.getInternalNodeNamesAdapter().setText(internalNode, internalNodeName);
				while (characterIterator.hasNext()) {
					String labelID = parameters.getPieChartLabelIDs()[characterIndex];
					PieChartLabel label = null;
					if (labelID != null) {
						label = new PieChartLabel(branch.getLabels());
						label.setID(parameters.getPieChartLabelIDs()[characterIndex]);
					}
					
					String characterKey = characterIterator.next();
					Iterator<String> stateIterator = parameters.getData().get(internalNodeName).getCharacterMap().get(characterKey).keySet().iterator();
					while (stateIterator.hasNext()) {
						if (labelID != null) {
							label.addValueID(((IDElementAdapter)parameters.getImportAdapters()[importAdapterIndex]).getID());
						}
						
						Double probability = parameters.getData().get(internalNodeName).getCharacterMap().get(characterKey).get(stateIterator.next());
						if (probability != null) {
							parameters.getImportAdapters()[importAdapterIndex].setDecimal(internalNode, probability);
						}
						else {
							nodeDataNotFound.add(parameters.getData().get(internalNodeName).getName());
							parameters.getImportAdapters()[importAdapterIndex].setText(internalNode, "--");
						}
						importAdapterIndex += 1;
					}
					
					if (labelID != null) {
						internalNode.getAfferentBranch().getLabels().add(label);
					}
					characterIndex += 1;
				}
			}
			else {
				nodesNotFound.add(parameters.getData().get(internalNodeName).getName());
			}	
		}	
	}
}