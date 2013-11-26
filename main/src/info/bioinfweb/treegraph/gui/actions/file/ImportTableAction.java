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
package info.bioinfweb.treegraph.gui.actions.file;


import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.JOptionPane;

import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.io.newick.NewickStringChars;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.undo.file.importtable.DuplicateKeyException;
import info.bioinfweb.treegraph.document.undo.file.importtable.ImportTableData;
import info.bioinfweb.treegraph.document.undo.file.importtable.ImportTableEdit;
import info.bioinfweb.treegraph.document.undo.file.importtable.ImportTableParameters;
import info.bioinfweb.treegraph.document.undo.file.importtable.InsufficientTableSizeException;
import info.bioinfweb.treegraph.gui.actions.DocumentAction;
import info.bioinfweb.treegraph.gui.dialogs.io.importtable.AssignImportColumnsDialog;
import info.bioinfweb.treegraph.gui.dialogs.io.importtable.SelectImportTableDialog;
import info.bioinfweb.treegraph.gui.mainframe.MainFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeInternalFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeSelection;
import info.webinsel.util.SystemUtils;
import info.webinsel.util.io.TableReader;



/**
 * Used to import a table from a text file to node/branch data columns.
 * 
 * @author Ben St&ouml;ver
 * @since 2.0.24
 */
public class ImportTableAction extends DocumentAction {
	private SelectImportTableDialog importTableDialog = null;
	private AssignImportColumnsDialog assignImportColumnsDialog = null;
	
	
	
	public ImportTableAction(MainFrame mainFrame) {
		super(mainFrame);
		putValue(Action.NAME, "Import table as node/branch data..."); 
	  putValue(Action.MNEMONIC_KEY, KeyEvent.VK_I);
	}
	
	
	private SelectImportTableDialog getImportTableDialog() {
		if (importTableDialog == null) {
			importTableDialog = new SelectImportTableDialog(getMainFrame());
		}
		return importTableDialog;
	}	

	
	private AssignImportColumnsDialog getAssignImportColumnsDialog() {
		if (assignImportColumnsDialog == null) {
			assignImportColumnsDialog = new AssignImportColumnsDialog(getMainFrame());
		}
		return assignImportColumnsDialog;
	}	

	
	@Override
	protected void onActionPerformed(ActionEvent e, TreeInternalFrame frame) {
		if (getImportTableDialog().execute(frame.getDocument(), frame.getTreeViewPanel().getSelection(), 
						frame.getSelectedAdapter())) {
			ImportTableParameters parameters = new ImportTableParameters();
			getImportTableDialog().assignParameters(parameters);
			
			try {
				ImportTableData data = new ImportTableData(parameters);
				if (data != null) {
					getAssignImportColumnsDialog().execute(parameters, data, frame.getDocument().getTree());
					if (parameters.getImportAdapters() != null) {
						frame.getDocument().executeEdit(new ImportTableEdit(frame.getDocument(), parameters, data));
					}
				}
			}
			catch (FileNotFoundException ex) {
				JOptionPane.showMessageDialog(MainFrame.getInstance(), "The path \"" + parameters.getTableFile().getAbsolutePath() + 
						"\" is invalid.", "Error", JOptionPane.ERROR_MESSAGE);
			}
			catch (SecurityException ex) {
				JOptionPane.showMessageDialog(MainFrame.getInstance(), "The permission for writing to the file \"" + 
			      parameters.getTableFile().getAbsolutePath() + "\" was denied.", "Error", JOptionPane.ERROR_MESSAGE);
			}
			catch (IOException ex) {
				JOptionPane.showMessageDialog(MainFrame.getInstance(), "The error \"" + ex.getMessage() + 
						"\" occured when writing to the file \"" + parameters.getTableFile().getAbsolutePath() + "\".", 
						"Error", JOptionPane.ERROR_MESSAGE);
			}
			catch (DuplicateKeyException ex) {
				StringBuffer message = new StringBuffer();
				message.append("The first column of the imported table file (keys to identify nodes) contained the\n");
				message.append("follwing entries multiple times:\n\n");
				Iterator<String> iterator = ex.getKeys().iterator();
				while (iterator.hasNext()) {
					message.append(iterator.next());
					message.append('\n');
				}
				message.append("Note that the parameter setting (e.g. whitespace treatment, case sensitivity)\n");
				message.append("influences if two entries are consideres equal.)");
				JOptionPane.showMessageDialog(MainFrame.getInstance(), message, "Error", JOptionPane.ERROR_MESSAGE);
			}
			catch (InsufficientTableSizeException ex) {
				JOptionPane.showMessageDialog(MainFrame.getInstance(), ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	
	@Override
	public void setEnabled(Document document, TreeSelection selection, NodeBranchDataAdapter tableAdapter) {
		setEnabled((document != null) && !document.getTree().isEmpty());
	}

}