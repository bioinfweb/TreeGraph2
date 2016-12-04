/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2011, 2013-2016  Ben Stöver, Sarah Wiechers, Kai Müller
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
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.undo.file.importtable.DuplicateKeyException;
import info.bioinfweb.treegraph.document.undo.file.importtable.ImportTableData;
import info.bioinfweb.treegraph.document.undo.file.importtable.ImportTableEdit;
import info.bioinfweb.treegraph.document.undo.file.importtable.ImportTableParameters;
import info.bioinfweb.treegraph.document.undo.file.importtable.InsufficientTableSizeException;
import info.bioinfweb.treegraph.gui.actions.DocumentAction;
import info.bioinfweb.treegraph.gui.dialogs.io.imexporttable.AssignImportTableColumnsDialog;
import info.bioinfweb.treegraph.gui.dialogs.io.imexporttable.SelectImportTableDialog;
import info.bioinfweb.treegraph.gui.mainframe.MainFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeInternalFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeSelection;



/**
 * Used to import a table from a text file to node/branch data columns.
 * 
 * @author Ben St&ouml;ver
 * @since 2.0.24
 */
public class ImportTableAction extends DocumentAction {
	public static final String PARAMETER_MESSAGE = 
			"Note that the parameter settings (e.g. whitespace treatment, case sensitivity)\n" +
			"influences if two entries are considered equal.";
	
	
	private SelectImportTableDialog importTableDialog = null;
	private AssignImportTableColumnsDialog assignImportColumnsDialog = null;
	
	
	
	public ImportTableAction(MainFrame mainFrame) {
		super(mainFrame);
		putValue(Action.NAME, "Import table as node/branch data..."); 
	  putValue(Action.MNEMONIC_KEY, KeyEvent.VK_I);
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F9, InputEvent.SHIFT_MASK));
	}
	
	
	private SelectImportTableDialog getImportTableDialog() {
		if (importTableDialog == null) {
			importTableDialog = new SelectImportTableDialog(getMainFrame());
		}
		return importTableDialog;
	}	

	
	private AssignImportTableColumnsDialog getAssignImportColumnsDialog() {
		if (assignImportColumnsDialog == null) {
			assignImportColumnsDialog = new AssignImportTableColumnsDialog(getMainFrame());
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
					if ((parameters.getImportAdapters() != null) && (parameters.getImportAdapters().length > 0)) {  // parameters.getImportAdapters().length == 0, if the user canceled in the second dialog
						ImportTableEdit edit = new ImportTableEdit(frame.getDocument(), parameters, data);
						frame.getDocument().executeEdit(edit);
						if (edit.hasWarnings()) {
							JOptionPane.showMessageDialog(MainFrame.getInstance(), edit.getWarningText(), 
							    "Warning", JOptionPane.WARNING_MESSAGE);
						}
					}
				}
			}
			catch (FileNotFoundException ex) {
				JOptionPane.showMessageDialog(MainFrame.getInstance(), "The file \"" + parameters.getTableFile().getAbsolutePath() + 
						"\" was not found.", "Error", JOptionPane.ERROR_MESSAGE);
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
				JOptionPane.showMessageDialog(MainFrame.getInstance(),
						"The first column of the imported table file (keys to identify nodes) contained the\n" +
				    "following entries multiple times:\n\n" + createElementList(ex.getKeys(), true) + "\n\n" +
						PARAMETER_MESSAGE, "Error", JOptionPane.ERROR_MESSAGE);
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