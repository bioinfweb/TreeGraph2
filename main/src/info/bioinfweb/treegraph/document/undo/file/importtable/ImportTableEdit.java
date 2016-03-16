/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2015-2016  Ben Stöver, Sarah Wiechers, Kai Müller
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
package info.bioinfweb.treegraph.document.undo.file.importtable;


import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.TextElementData;
import info.bioinfweb.treegraph.document.change.DocumentChangeType;
import info.bioinfweb.treegraph.document.undo.DocumentEdit;
import info.bioinfweb.treegraph.document.undo.WarningMessageEdit;
import info.bioinfweb.treegraph.document.undo.nodebranchdata.NodeBranchDataColumnBackup;
import info.bioinfweb.treegraph.gui.actions.file.ImportTableAction;
import info.bioinfweb.commons.Math2;



/**
 * Imports a table from a text file into node/branch data columns.
 * 
 * @see ImportTableParameters
 * @see ImportTableData
 * @author Ben St&ouml;ver
 * @since 2.0.24
 */
public class ImportTableEdit extends DocumentEdit implements WarningMessageEdit {
  private ImportTableParameters parameters;
  private ImportTableData data;
  private NodeBranchDataColumnBackup[] backups;
  private Set<String> keysNotInTree = new TreeSet<String>();
  
  
	public ImportTableEdit(Document document, ImportTableParameters parameters, ImportTableData data) {
		super(document, DocumentChangeType.TOPOLOGICAL_BY_RENAMING);
		this.parameters = parameters;
		this.data = data;
		backups = createBackups();
	}
	
	
	/**
	 * Returns {@code false}, if the imported table contained a key that was not found in the tree under 
	 * the specified parameters, {@code true} otherwise. 
	 */
	public boolean isAllKeysFound() {
		return keysNotInTree.isEmpty();
	}


	/**
	 * Returns a collection of keys found in the table that were not contained in the specified node/branch 
	 * data column of the tree under the specified parameters.
	 */
	public Set<String> getKeysNotInTree() {
		return keysNotInTree;
	}


	@Override
  public String getWarningText() {
	  return "The following entries in the key column of the table could not be found in the specified\n" + 
		    "node/branch data column of the tree:\n\n" + ImportTableAction.createElementList(getKeysNotInTree()) + "\n\n" +
		    "The cells in the according lines have not been imported.\n(" + ImportTableAction.PARAMETER_MESSAGE + ")";
  }


	@Override
  public boolean hasWarnings() {
	  return !isAllKeysFound();
  }


	private NodeBranchDataColumnBackup[] createBackups() {
		NodeBranchDataColumnBackup[] result = new NodeBranchDataColumnBackup[parameters.getImportAdapters().length];
		for (int i = 0; i < result.length; i++) {
			result[i] = new NodeBranchDataColumnBackup(parameters.getImportAdapters()[i], getDocument().getTree().getPaintStart());
		}
		return result;
	}

	
	private void addNodesByData(Collection<Node> result, Node root, TextElementData data) {
		TextElementData rootData = parameters.getKeyAdapter().toTextElementData(root);
    if (rootData.equals(data) ||  // second condition is only evaluated if necessary
    		parameters.createEditedValue(rootData.toString()).equals(data)) {
    	
    	result.add(root);
    }
    
    Iterator<Node> iterator = root.getChildren().iterator();
    while (iterator.hasNext()) {
    	addNodesByData(result, iterator.next(), data);
    }
	}
	
	
	private Collection<Node> getNodesByData(TextElementData data) {
		Collection<Node> result = new Vector<Node>(8);
		addNodesByData(result, getDocument().getTree().getPaintStart(), data);
		return result;
	}
	
	
  /**
   * Writes the imported table to the tree.
   */
  private void importData() {
  	keysNotInTree.clear();
  	if (parameters.getImportAdapters().length == data.columnCount()) {
  		Iterator<TextElementData> keyIterator = data.keySet().iterator();
  		while (keyIterator.hasNext()) {  // iterate over rows
  			TextElementData key = keyIterator.next();
				Collection<Node> nodes = getNodesByData(key);
				if (nodes.size() > 0) {
					int row = data.getRowByKey(key);
					Iterator<Node> nodeIterator = nodes.iterator();
					while (nodeIterator.hasNext()) {  // iterate over all nodes affected by the current row
						Node currentNode = nodeIterator.next();
						for (int column = 0; column < parameters.getImportAdapters().length; column++) {  // iterate over columns
							String value = data.getTableValue(column, row);
							if (parameters.isParseNumericValues() && Math2.isDecimal(value)) {
								parameters.getImportAdapters()[column].setDecimal(currentNode, Math2.parseDouble(value));
							}
							else {
								parameters.getImportAdapters()[column].setText(currentNode, value);
							}
	          }
					}
				}
				else {
					keysNotInTree.add(data.getUnprocessedKey(data.getRowByKey(key)));
				}
  		}
  	}
  	else {
  		throw new IllegalArgumentException("The number of adapters and columns do not match.");
  	}
  }
  
  
	@Override
	public void redo() throws CannotRedoException {
		importData();
		super.redo();
	}


	@Override
	public void undo() throws CannotUndoException {
		for (int i = 0; i < backups.length; i++) {
			backups[i].restore(getDocument().getTree().getPaintStart());
		}
		super.undo();
	}


	public String getPresentationName() {
		return "Import table";
	}
}