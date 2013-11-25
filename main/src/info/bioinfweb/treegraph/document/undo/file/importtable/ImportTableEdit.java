/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2013  Ben Stöver, Kai Müller
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
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.TextElementData;
import info.bioinfweb.treegraph.document.Tree;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.undo.DocumentEdit;
import info.bioinfweb.treegraph.document.undo.NodeBranchDataBackup;
import info.bioinfweb.treegraph.gui.mainframe.MainFrame;
import info.webinsel.util.Math2;
import info.webinsel.util.junit.TestTools;



/**
 * Imports a table from a text file into node/branch data columns.
 * 
 * @see ImportTableParameters
 * @see ImportTableData
 * @author Ben St&ouml;ver
 * @since 2.0.24
 */
public class ImportTableEdit extends DocumentEdit {
  private ImportTableParameters parameters;
  private ImportTableData data;
  private NodeBranchDataBackup[] backups;
  
  
	public ImportTableEdit(Document document, ImportTableParameters parameters, ImportTableData data) {
		
		super(document);
		this.parameters = parameters;
		this.data = data;
		backups = createBackups();
	}
	
	
	private NodeBranchDataBackup[] createBackups() {
		NodeBranchDataBackup[] result = new NodeBranchDataBackup[parameters.getImportAdapters().length];
		for (int i = 0; i < result.length; i++) {
			result[i] = new NodeBranchDataBackup(parameters.getImportAdapters()[i], document.getTree().getPaintStart());
		}
		return result;
	}

	
	private void addNodesByData(Collection<Node> result, Node root, TextElementData data) {
    if (root.getData().equals(data) ||  // second condition is only evaluated if necessary
    		ImportTableData.createEditedValue(root.getData().getText(), parameters).equals(data)) {
    	
    	result.add(root);
    }
    
    Iterator<Node> iterator = root.getChildren().iterator();
    while (iterator.hasNext()) {
    	addNodesByData(result, iterator.next(), data);
    }
	}
	
	
	private Collection<Node> getNodesByData(TextElementData data) {
		Collection<Node> result = new Vector<Node>(8);
		addNodesByData(result, document.getTree().getPaintStart(), data);
		return result;
	}
	
	
  /**
   * @param adapters
   * @param data
   * @return <code>true</code>, if all data has been imported, <code>false</code>, if
   *         one or more unique node name was not found in the current tree
   */
  private boolean importData() {
  	boolean allImported = true;
  	System.out.println(parameters.getImportAdapters().length + ", " +  data.columnCount());
  	if (parameters.getImportAdapters().length == data.columnCount()) {
  		Iterator<TextElementData> keyIterator = data.keySet().iterator();
  		while (keyIterator.hasNext()) {  // iterate over rows
  			TextElementData key = keyIterator.next();
				Collection<Node> nodes = getNodesByData(key);
				if (nodes.size() > 0) {
					int row = data.getRowByKey(key);
					Iterator<Node> nodeIterator = nodes.iterator();
					while (nodeIterator.hasNext()) {  // iterate over all nodes affected by the current row
						for (int column = 0; column < parameters.getImportAdapters().length; column++) {  // iterate over columns
							Node currentNode = nodeIterator.next();
							String value = data.getTableValue(column, row);
							if (parameters.isParseNumbericValues() && Math2.isDecimal(value)) {
								parameters.getImportAdapters()[column].setDecimal(currentNode, Math2.parseDouble(value));
							}
							else {
								parameters.getImportAdapters()[column].setText(currentNode, value);
							}
	          }
					}
				}
				else {
					allImported = false;
				}
  		}
  	}
  	else {
  		throw new IllegalArgumentException("The number of adapters and columns do not match.");
  	}
  	return allImported;
  }
  
  
	@Override
	public void redo() throws CannotRedoException {
		if (importData()) {
			JOptionPane.showMessageDialog(MainFrame.getInstance(), 
					"One or more entries of the table could not be imported because the \n" +
					"current tree does not contain a node with the specified unique name,.", "Warning", 
					JOptionPane.WARNING_MESSAGE);
		}
		super.redo();
	}


	@Override
	public void undo() throws CannotUndoException {
		for (int i = 0; i < backups.length; i++) {
			backups[i].restore(document.getTree().getPaintStart());
		}
		super.undo();
	}


	public String getPresentationName() {
		return "Import table";
	}
}