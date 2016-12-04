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


import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.Action;
import javax.swing.JOptionPane;

import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.graphics.export.GraphicFormat;
import info.bioinfweb.treegraph.graphics.export.GraphicWriter;
import info.bioinfweb.treegraph.graphics.export.GraphicWriterFactory;
import info.bioinfweb.treegraph.graphics.positionpaint.PositionPaintFactory;
import info.bioinfweb.treegraph.gui.actions.DocumentAction;
import info.bioinfweb.treegraph.gui.mainframe.MainFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeInternalFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeSelection;
import info.bioinfweb.commons.collections.ParameterMap;



public class ExportToPDFAction extends DocumentAction {
	public static final String FILE_PREFIX = "Tree";
	public static final String FILE_SUFFIX = ".pdf";
	
	
	public ExportToPDFAction(MainFrame mainFrame) {
		super(mainFrame);
		putValue(Action.NAME, "Export to PDF"); 
		putValue(Action.SHORT_DESCRIPTION, "Export to PDF");
		loadSymbols("PDF");
	}
	
	
	@Override
	protected void onActionPerformed(ActionEvent e, TreeInternalFrame frame) {
  	if (Desktop.isDesktopSupported()) {
			try {
				File file = File.createTempFile(FILE_PREFIX, FILE_SUFFIX);
				ParameterMap hints = new ParameterMap();
				hints.put(GraphicWriter.KEY_DIMENSIONS_IN_PIXELS, true);  // Otherwise PDFs of large trees are empty. (Why?)
		  	GraphicWriterFactory.getInstance().getWriter(GraphicFormat.PDF).write(frame.getDocument(), 
		  			PositionPaintFactory.getInstance().getPainter(frame.getTreeViewPanel().getPainterType()), 
		  			hints, file);
	    	Desktop.getDesktop().open(file);
			}
			catch (Exception ex) {
				JOptionPane.showMessageDialog(MainFrame.getInstance(), "The error \"" + ex.getMessage() + 
						"\" occured when trying to create the PDF file.", "IO Error", JOptionPane.ERROR_MESSAGE);
			}
  	}
  	else {
			JOptionPane.showMessageDialog(MainFrame.getInstance(), "Your system does not support " +
					"this function. Use \"File\" -> \"Export to graphic/PDF...\" instead.", 
					"Function not supported", JOptionPane.ERROR_MESSAGE);
  	}
	}

	
	@Override
	public void setEnabled(Document document, TreeSelection selection, NodeBranchDataAdapter tableAdapter) {
		setEnabled((document != null));
	}
}