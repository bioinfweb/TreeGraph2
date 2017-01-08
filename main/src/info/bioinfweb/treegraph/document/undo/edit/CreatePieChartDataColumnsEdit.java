/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2011, 2013-2017  Ben Stöver, Sarah Wiechers, Kai Müller
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
package info.bioinfweb.treegraph.document.undo.edit;


import java.util.List;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.change.DocumentChangeType;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.undo.DocumentEdit;
import info.bioinfweb.treegraph.document.undo.nodebranchdata.NodeBranchDataColumnBackup;
import info.bioinfweb.treegraph.document.undo.nodebranchdata.NodeBranchDataEdit;



/**
 * Creates a set of columns that can be the data basis for pie chart labels, where each column contains the probability for one
 * character state. The information for these columns is token from an existing node/branch data column that contains just
 * character states for one character. This feature can e.g. be used if terminal character states imported from a table (that
 * was e.g. the input for <i>BayesTraits</i> shall be displayed as pie chart label (each displaying one state with 100 %).
 * 
 * @author Ben St&ouml;ver
 * @since 2.12.0
 */
public class CreatePieChartDataColumnsEdit extends DocumentEdit {
	private NodeBranchDataAdapter source;
	private String idPrefix;
	private List<NodeBranchDataColumnBackup> backups;
	

	public CreatePieChartDataColumnsEdit(Document document, NodeBranchDataAdapter source, String idPrefix) {
	  super(document, DocumentChangeType.TOPOLOGICAL_BY_RENAMING);
	  this.source = source;
	  this.idPrefix = idPrefix;
	  
	  
  }


	@Override
  public String getPresentationName() {
	  // TODO Auto-generated method stub
	  return null;
  }


	@Override
  public void redo() throws CannotRedoException {
	  // TODO Auto-generated method stub
	  super.redo();
  }


	@Override
  public void undo() throws CannotUndoException {
	  // TODO Auto-generated method stub
	  super.undo();
  }
}
