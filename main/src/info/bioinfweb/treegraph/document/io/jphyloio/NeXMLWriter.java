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
package info.bioinfweb.treegraph.document.io.jphyloio;


import java.io.IOException;
import java.io.OutputStream;

import javax.swing.JOptionPane;

import info.bioinfweb.jphyloio.JPhyloIOEventWriter;
import info.bioinfweb.jphyloio.ReadWriteParameterNames;
import info.bioinfweb.jphyloio.dataadapters.implementations.ListBasedDocumentDataAdapter;
import info.bioinfweb.jphyloio.dataadapters.implementations.store.StoreTreeNetworkGroupDataAdapter;
import info.bioinfweb.jphyloio.events.LinkedLabeledIDEvent;
import info.bioinfweb.jphyloio.events.type.EventContentType;
import info.bioinfweb.jphyloio.factory.JPhyloIOReaderWriterFactory;
import info.bioinfweb.jphyloio.formats.JPhyloIOFormatIDs;
import info.bioinfweb.treegraph.Main;
import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.io.AbstractDocumentWriter;
import info.bioinfweb.treegraph.document.io.ReadWriteParameterMap;
import info.bioinfweb.treegraph.gui.mainframe.MainFrame;



public class NeXMLWriter extends AbstractDocumentWriter {
	protected JPhyloIOReaderWriterFactory factory = new JPhyloIOReaderWriterFactory();
	
	
	protected void writeTree(Document document, String formatID, OutputStream stream) {
		// Create data adapters:
		ListBasedDocumentDataAdapter documentAdapter = new ListBasedDocumentDataAdapter();
		StoreTreeNetworkGroupDataAdapter treeGroup = new StoreTreeNetworkGroupDataAdapter(
				new LinkedLabeledIDEvent(EventContentType.TREE_NETWORK_GROUP, "treeGroup", null, null), null);
		documentAdapter.getTreeNetworkGroups().add(treeGroup);
		treeGroup.getTreesAndNetworks().add(new TreeDataAdapter(document));
		
		// Define writer parameters:
		info.bioinfweb.jphyloio.ReadWriteParameterMap parameters = new info.bioinfweb.jphyloio.ReadWriteParameterMap();
		parameters.put(ReadWriteParameterNames.KEY_APPLICATION_NAME, Main.APPLICATION_NAME);
		parameters.put(ReadWriteParameterNames.KEY_APPLICATION_VERSION, Main.getInstance().getVersion());
		parameters.put(ReadWriteParameterNames.KEY_APPLICATION_URL, Main.TG_URL);
		JPhyloIOTools.addTreeGraphObjectTranslators(parameters.getObjectTranslatorFactory(), true);
		
		// Write document:
		JPhyloIOEventWriter writer = factory.getWriter(formatID);
		try {
			writer.writeDocument(documentAdapter, stream, parameters);
		}
		catch (IOException ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(MainFrame.getInstance(), "The error \"" + ex.getLocalizedMessage() + "\" occurred.", 
					"Error", JOptionPane.ERROR_MESSAGE);
		}
	}


	@Override
	public void write(Document document, OutputStream stream, ReadWriteParameterMap properties) throws Exception {
		try {
			writeTree(document, JPhyloIOFormatIDs.NEXML_FORMAT_ID, stream);
		}
		finally {
			stream.close();
		}
	}
}
