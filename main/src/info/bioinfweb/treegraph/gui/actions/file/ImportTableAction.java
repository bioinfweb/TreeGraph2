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

import javax.swing.Action;
import javax.swing.JOptionPane;

import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.undo.file.ImportTableEdit;
import info.bioinfweb.treegraph.gui.actions.DocumentAction;
import info.bioinfweb.treegraph.gui.dialogs.io.table.AssignImportColumnsDialog;
import info.bioinfweb.treegraph.gui.dialogs.io.table.ImportTableDialog;
import info.bioinfweb.treegraph.gui.mainframe.MainFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeInternalFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeSelection;
import info.webinsel.util.io.TableReader;



/**
 * @author Ben St&ouml;ver
 * @since 2.0.24
 */
public class ImportTableAction extends DocumentAction {
	private ImportTableDialog importTableDialog = null;
	private AssignImportColumnsDialog assignImportColumnsDialog = null;
	
	
	
	public ImportTableAction(MainFrame mainFrame) {
		super(mainFrame);
		putValue(Action.NAME, "Import table as node/branch data..."); 
	  putValue(Action.MNEMONIC_KEY, KeyEvent.VK_I);
	}
	
	
	private ImportTableDialog getImportTableDialog() {
		if (importTableDialog == null) {
			importTableDialog = new ImportTableDialog(getMainFrame());
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
			String path = getImportTableDialog().getSelectedFile().getAbsolutePath();
			
			try {
				String[][] data = TableReader.readTable(
						new FileInputStream(path), getImportTableDialog().getSeparator()); 
				if (data != null) {
					NodeBranchDataAdapter[] adapters = 
					  	getAssignImportColumnsDialog().execute(data.length, 
					  			frame.getDocument().getTree());
					if (adapters != null) {
						frame.getDocument().executeEdit(new ImportTableEdit(frame.getDocument(), data, adapters));
					}
				}
				else {
					JOptionPane.showMessageDialog(MainFrame.getInstance(), "The specified file does not contain enough columns. It has to contain at least two.", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
			catch (FileNotFoundException ex) {
				JOptionPane.showMessageDialog(MainFrame.getInstance(), "The path \"" + path + "\" is invalid.", "Error", JOptionPane.ERROR_MESSAGE);
			}
			catch (SecurityException ex) {
				JOptionPane.showMessageDialog(MainFrame.getInstance(), "The permission for writing to the file \"" + path + "\" was denied.", "Error", JOptionPane.ERROR_MESSAGE);
			}
			catch (IOException ex) {
				JOptionPane.showMessageDialog(MainFrame.getInstance(), "The error \"" + ex.getMessage() + "\" occured when writing to the file \"" + path + "\".", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	
	@Override
	public void setEnabled(Document document, TreeSelection selection, NodeBranchDataAdapter tableAdapter) {
		setEnabled((document != null) && !document.getTree().isEmpty());
	}

}