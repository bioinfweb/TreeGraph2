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


import info.bioinfweb.treegraph.document.Branch;
import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.PieChartLabel;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.tools.TreeSerializer;
import info.bioinfweb.treegraph.gui.CurrentDirectoryModel;
import info.bioinfweb.treegraph.gui.actions.DocumentAction;
import info.bioinfweb.treegraph.gui.mainframe.MainFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeInternalFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeSelection;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;



public class ExportPieChartLabelColorsAction extends DocumentAction {
	private JFileChooser fileChooser = null;
	private FileNameExtensionFilter textFilter;
	
	
	public ExportPieChartLabelColorsAction(MainFrame mainFrame) {
		super(mainFrame);
		putValue(Action.NAME, "Export pie chart label colors..."); 
		putValue(Action.SHORT_DESCRIPTION, "Export pie chart label colors");
	}
	

	private void writeTable(File file, Branch branch) throws FileNotFoundException {
		PrintWriter writer = new PrintWriter(file);
		try {
			writer.println("Pie chart label ID\tSource data column ID\tData caption\tColor\tRed\tGreen\tBlue");
			PieChartLabel[] labels = TreeSerializer.getElementsOnNode(branch.getTargetNode(), PieChartLabel.class);
			for (PieChartLabel label : labels) {
				for (int i = 0; i < label.getSectionDataList().size(); i++) {
					writer.write(label.getID());
					writer.write('\t');
					writer.write(label.getSectionDataList().get(i).getValueColumnID());
					writer.write('\t');
					writer.write(label.getSectionDataList().get(i).getCaption());
					writer.write('\t');
					
					Color color = label.getFormats().getPieColor(i);
					writer.write(String.format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue()));
					writer.write('\t');
					writer.print(color.getRed());
					writer.write('\t');
					writer.print(color.getGreen());
					writer.write('\t');
					writer.print(color.getBlue());
					writer.write('\t');
					writer.println();
				}
			}
		}
		finally {
			writer.close();
		}
	}
	
	
	private JFileChooser getFileChooser() {
		if (fileChooser == null) {
			fileChooser = new JFileChooser();
			fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
			fileChooser.setDialogTitle("Export pie chart label colors");
			fileChooser.setMultiSelectionEnabled(false);
			textFilter = new FileNameExtensionFilter("Text files (*.txt)", "txt");
			fileChooser.setFileFilter(textFilter);
			CurrentDirectoryModel.getInstance().addFileChooser(fileChooser);
		}
		return fileChooser;
	}


	@Override
	protected void onActionPerformed(ActionEvent e, TreeInternalFrame frame) {
		if (getFileChooser().showSaveDialog(getMainFrame()) == JFileChooser.APPROVE_OPTION) {
			File file = getFileChooser().getSelectedFile();
			
			// Add default extension if text filter is selected:
			if (textFilter.equals(getFileChooser().getFileFilter())) {
				String extension = "." + textFilter.getExtensions()[0];  
				if (!file.getAbsolutePath().endsWith(extension)) {
					file = new File(file.getAbsolutePath() + extension);
				}
			}
			
			// Check overwrite and write output:
			if (!file.exists() || (JOptionPane.showConfirmDialog(getMainFrame(), "The file \"" + file.getAbsolutePath() + 
						"\" already exists.\nDo you want to overwrite it?", "Overwrite table file", 
						JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION)) {
				
				try {
					writeTable(file, frame.getTreeViewPanel().getSelection().getFirstElementOfType(Branch.class));  // The correct selection is not checked again here.
				}
				catch (IOException | SecurityException ex) {
					JOptionPane.showMessageDialog(getMainFrame(), "The error \"" + ex.getLocalizedMessage() + 
							"\"\n occurred when trying to write the file \"" + file.getAbsolutePath() + "\".", "Error when writing file", 
							JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}

	
	@Override
	public void setEnabled(Document document, TreeSelection selection, NodeBranchDataAdapter tableAdapter) {
		setEnabled(oneElementSelected(selection) && selection.containsType(Branch.class) && 
				selection.getFirstElementOfType(Branch.class).getLabels().contains(PieChartLabel.class));
	}
}
