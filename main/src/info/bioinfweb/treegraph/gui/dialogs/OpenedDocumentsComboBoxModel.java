/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2014  Ben Stöver, Kai Müller
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
package info.bioinfweb.treegraph.gui.dialogs;


import java.util.Iterator;

import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.gui.mainframe.MainFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeInternalFrame;

import javax.swing.DefaultComboBoxModel;



/**
 * A model for combo boxes allowing to choose one of the currently opened documents.
 * 
 * @author Ben St&ouml;ver
 * @since 2.2.0
 */
public class OpenedDocumentsComboBoxModel extends DefaultComboBoxModel<Document> {
	/**
	 * Creates a new empty instance of this class. To use the model {@link #refreshDocuments()} needs to be called first.
	 */
	public OpenedDocumentsComboBoxModel() {
	  super();
  }
	

	/**
	 * Refreshes the contents of this model to contain all currently opened documents.
	 */
	public void refreshDocuments() {
		removeAllElements();
		Iterator<TreeInternalFrame> iterator = MainFrame.getInstance().treeFrameIterator();
		while (iterator.hasNext()) {
			addElement(iterator.next().getDocument());
		}
	}


	@Override
  public Document getSelectedItem() {
	  return (Document)super.getSelectedItem();
  }
}
