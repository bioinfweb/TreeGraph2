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
package info.bioinfweb.treegraph.document.undo;


import javax.swing.JOptionPane;
import javax.swing.undo.CannotUndoException;

import info.bioinfweb.treegraph.Main;
import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.Legend;
import info.bioinfweb.treegraph.document.Legends;
import info.bioinfweb.treegraph.document.Node;
import info.webinsel.wikihelp.client.WikiHelpOptionPane;



/**
 * This class should be a superclass of all classes that might delete nodes which are anchors to
 * legends. It offers the methods <code>saveLegends()</code> which can be called from the 
 * <code>redo()</code>-method and <code>restoreLegends()</code> which can be called from the 
 * <code>undo()</code>-method.
 * @author Ben St&ouml;ver
 */
public abstract class SaveLegendsEdit extends DocumentEdit implements WarningEdit {
	private Legend[] legendsSave = null;
  private boolean legendsReanchored = false;
  private boolean legendsRemoved = false;
  private boolean showWarnings = false;
  private int helpTopic = 0;
	
	
	public SaveLegendsEdit(Document document) {
		super(document);
	}


	protected Legend[] getLegendsSave() {
		return legendsSave;
	}

	
	protected void saveLegends() {
		if (legendsSave == null) {
			legendsSave = document.getTree().getLegends().toArray(new Legend[0]);
			for (int i = 0; i < legendsSave.length; i++) {
				legendsSave[i] = legendsSave[i].clone();
			}
		}
	}
	
	
	protected void restoreLegends() throws CannotUndoException {
		if (legendsSave == null) {
			throw new CannotUndoException();
		}
		else {
			Legends legends = document.getTree().getLegends();
			legends.clear();
			for (int i = 0; i < legendsSave.length; i++) {
				legends.insert(legendsSave[i]);
			}
		}
	}
	
	
	public boolean getLegendsReanchored() {
		return legendsReanchored;
	}


	protected void setLegendsReanchored(boolean legendsReanchored) {
		this.legendsReanchored = legendsReanchored;
	}


	public boolean getLegendsRemoved() {
		return legendsRemoved;
	}


	protected void setLegendsRemoved(boolean legendsRemoved) {
		this.legendsRemoved = legendsRemoved;
	}


	public boolean getShowWarnings() {
		return showWarnings;
	}


	public void setShowWarnings(boolean showWarnings) {
		this.showWarnings = showWarnings;
	}
	
	
  protected int getHelpTopic() {
		return helpTopic;
	}


	protected void setHelpTopic(int helpTopic) {
		this.helpTopic = helpTopic;
	}


	protected String getWarningsMessages() {
		String msg = "";
		if (getLegendsRemoved()) {
			msg += "- One or more legends that were only anchored inside the removed subtree were removed as well.\n";
		}
		if (getLegendsReanchored()) {
			msg += "- One or more legends that were anchored inside the removed subtree were reanchored.\n";
		}
		return msg;
  }
	
	
	protected void showWarningDialog() {
		if (getShowWarnings()) {
			String msg = getWarningsMessages();
			if (!"".equals(msg)) {
				WikiHelpOptionPane.showMessageDialog(null, "This process produced warnings:\n\n" + msg + 
						"\nYou can use the undo-function to restore lost or changed data.", "Warning", 
						JOptionPane.WARNING_MESSAGE, Main.getInstance().getWikiHelp(), helpTopic);
			}
		}
	}
	
	
	/**
	 * This method can be used to adopt the legends of a document prior to deleting a subtree.
	 * If all anchors of a legend are contained in the deleted subtree, the legend is 
	 * deleted as well. If only one of the anchors is in the deleted subtree this anchor is
	 * deleted but the other remains unchanged (if necessary the former second anchor becomes the 
	 * first) and the legend is left in the document.
	 * @param root - the root node of the subtree that will be deleted later
	 */
	protected void editSubtreeLegends(Node root) {
		Legends legends = document.getTree().getLegends();
		for (int i = 0; i < legends.size(); i++) {
			Legend l = legends.get(i);
			Node secondAnchor = l.getFormats().getAnchor(1);
			boolean firstIn = root.containedInSubtree(l.getFormats().getAnchor(0));
			boolean secondIn = root.containedInSubtree(secondAnchor);
			if ((firstIn && secondIn) || (firstIn && (secondAnchor == null))) {
				legends.remove(l);
				i--;  // Index hat sich durch das Löschen verschoben.
				setLegendsRemoved(true);
			}
			else if (secondIn) {
				l.getFormats().setAnchor(1, null);
				setLegendsReanchored(true);
			}
			else if (firstIn) {
				l.getFormats().setAnchor(0, l.getFormats().getAnchor(1));
				l.getFormats().setAnchor(1, null);
				setLegendsReanchored(true);
			}
		}
	}
}