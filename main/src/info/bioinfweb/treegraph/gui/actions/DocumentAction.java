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
package info.bioinfweb.treegraph.gui.actions;


import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.JOptionPane;

import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.gui.mainframe.MainFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeInternalFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeSelection;
import info.bioinfweb.commons.swing.ExtendedAbstractAction;



/**
 * Implements basic functionality for all actions in TreeGraph 2 which are applied on
 * opened documents. All subclasses have access 
 * @author Ben St&ouml;ver
 */
public abstract class DocumentAction extends ExtendedAbstractAction {
	public static final int MISSING_KEY_OUTPUT_CHARS_PER_LINE = 100;	
	public static final int MAX_MISSING_KEY_OUTPUT_LINES = 10;
	
	private MainFrame mainFrame = null;
	
	
	public DocumentAction(MainFrame mainFrame) {
	  this.mainFrame = mainFrame;	
	}
	
  
	/**
	 * Subclasses should implement this method instead of overriding 
	 * {@link DocumentAction#actionPerformed(ActionEvent)}.
	 * @param e
	 * @param frame - the internal frame which was active while the user performed this action
	 */
	protected abstract void onActionPerformed(ActionEvent e, TreeInternalFrame frame);
	
	
	public abstract void setEnabled(Document document, TreeSelection selection, 
			NodeBranchDataAdapter tableAdapter);
	
  
  /**
   * Tests if exactly one element (not none and not more) is contained in the given 
   * selection.
   * @param selection
   * @return
   */
  protected static boolean oneElementSelected(TreeSelection selection) {
  	return (selection != null) && (selection.size() == 1);
  }
	
	
	protected MainFrame getMainFrame() {
		return mainFrame;
	}


	/**
	 * Determines the currently active internal frame and passes it to 
	 * {@link DocumentAction#onActionPerformed(ActionEvent, TreeInternalFrame)}. If no active
	 * frame is found an error message is displayed and 
	 * {@link DocumentAction#onActionPerformed(ActionEvent, TreeInternalFrame)} is not called.
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		TreeInternalFrame frame = mainFrame.getActiveTreeFrame();
		if (frame != null) {
			onActionPerformed(e, frame);
		}
		else {
			JOptionPane.showMessageDialog(frame, "There is no document selected.", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	
	public static String createElementList(Collection<String> collection, boolean delimitElements) {
		return createElementList(collection.iterator(), collection.size(), delimitElements);
	}
	
	
	public static String createElementList(Iterator<String> iterator, int size, boolean delimitElements) {
		StringBuffer result = new StringBuffer((MAX_MISSING_KEY_OUTPUT_LINES + 1) * MISSING_KEY_OUTPUT_CHARS_PER_LINE);  // one line more because single lines might be longer than MISSING_KEY_OUTPUT_CHARS_PER_LINE if keys overlap 
		int charCount = 0;
		int lineCount = 0;
		int elementCount = 0;
		
		while (iterator.hasNext() && (lineCount < MAX_MISSING_KEY_OUTPUT_LINES)) {
			String element = iterator.next();
			elementCount++;
			
			if (delimitElements) {
				result.append("\"");
				result.append(element);
				result.append("\"");
			}
			else {
				result.append(element);
			}			
			
			charCount += element.length();
			if (iterator.hasNext()) {
				if (charCount >= MISSING_KEY_OUTPUT_CHARS_PER_LINE) {
					result.append("\n");
					charCount = 0;
					lineCount++;
				}
				else {
					result.append(", ");
				}
			}
		}
		if (iterator.hasNext()) {
			result.append("... (" + (size - elementCount) + " more missing elements not shown here.)");
		}
		return result.toString();
	}
}