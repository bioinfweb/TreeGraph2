/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2011  Ben Stöver, Kai Müller
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
package info.bioinfweb.treegraph.document.undo.file;


import javax.swing.JOptionPane;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.undo.DocumentEdit;
import info.bioinfweb.treegraph.document.undo.NodeBranchDataBackup;
import info.bioinfweb.treegraph.gui.mainframe.MainFrame;



/**
 * @author Ben St&ouml;ver
 * @since 2.0.24
 */
public class ImportTableEdit extends DocumentEdit {
  private String[][] data;
  private NodeBranchDataAdapter[] adapters;
  private NodeBranchDataBackup[] backups;
  
  
	public ImportTableEdit(Document document, String[][] data, NodeBranchDataAdapter[] adapters) {
		super(document);
		this.data = data;
		this.adapters = adapters;
		backups = createBackups();
	}
	
	
	private NodeBranchDataBackup[] createBackups() {
		NodeBranchDataBackup[] result = new NodeBranchDataBackup[adapters.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = new NodeBranchDataBackup(adapters[i], document.getTree().getPaintStart());
		}
		return result;
	}


  /**
   * @param adapters
   * @param data
   * @return <code>true</code>, if all data has been imported, <code>false</code>, if
   *         one or more unique node name was not found in the current tree
   */
  public boolean importData(NodeBranchDataAdapter[] adapters, String[][] data) {
  	boolean allImported = true;
  	if (adapters.length == data.length) {
			for (int row = 0; row < data[0].length; row++) {
				Node node = document.getTree().getNodeByUniqueName(data[0][row]);
				if (node != null) {
	    		for (int col = 1; col < adapters.length; col++) {  // unique name überspringen
	    			if (!data[col][row].equals("")) {
	    				try {
		    				adapters[col].setDecimal(node, Double.parseDouble(data[col][row]));
	    				}
	    				catch (NumberFormatException e) {
	    					adapters[col].setText(node, data[col][row]);
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
		if (importData(adapters, data)) {
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