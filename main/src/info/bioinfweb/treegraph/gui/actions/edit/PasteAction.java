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
package info.bioinfweb.treegraph.gui.actions.edit;


import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.Action;
import javax.swing.KeyStroke;

import info.bioinfweb.treegraph.Main;
import info.bioinfweb.treegraph.document.Branch;
import info.bioinfweb.treegraph.document.ContrastManager;
import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.Label;
import info.bioinfweb.treegraph.document.Legend;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.PaintableElement;
import info.bioinfweb.treegraph.document.clipboard.ClipboardContentType;
import info.bioinfweb.treegraph.document.clipboard.TreeClipboard;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.undo.DocumentEdit;
import info.bioinfweb.treegraph.document.undo.edit.PasteAllLabelsEdit;
import info.bioinfweb.treegraph.document.undo.edit.PasteLabelEdit;
import info.bioinfweb.treegraph.document.undo.edit.PasteLegendEdit;
import info.bioinfweb.treegraph.document.undo.edit.PasteRootEdit;
import info.bioinfweb.treegraph.document.undo.edit.PasteSiblingEdit;
import info.bioinfweb.treegraph.document.undo.edit.PasteSubtreeEdit;
import info.bioinfweb.treegraph.gui.actions.DocumentAction;
import info.bioinfweb.treegraph.gui.dialogs.CollidingIDsDialog;
import info.bioinfweb.treegraph.gui.mainframe.MainFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeInternalFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeSelection;



public class PasteAction extends DocumentAction {
	private ContrastManager contrastManager = new ContrastManager();
	
	
	public PasteAction(MainFrame mainFrame) {
		super(mainFrame);
		putValue(Action.NAME, "Paste"); 
	  putValue(Action.MNEMONIC_KEY, KeyEvent.VK_P);
		putValue(Action.SHORT_DESCRIPTION, "Paste"); 
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke('V', 
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
	  loadSymbols("Paste");
	}

	
	@Override
	public void setEnabled(Document document, TreeSelection selection, NodeBranchDataAdapter tableAdapter) {
		TreeClipboard clipboard = Main.getInstance().getClipboard();
		
		boolean pasteNode = (document != null) && document.getTree().isEmpty() && 
	      clipboard.getContentType().equals(ClipboardContentType.SUBTREE);
		boolean pasteLabel = false;
		boolean pasteLegend = false;
		
    if (selection != null) {
    	switch (selection.size()) {
    	  case 1: 
  				pasteNode = pasteNode || ((selection.containsType(Node.class) || selection.containsType(Branch.class))
  						&& clipboard.getContentType().equals(ClipboardContentType.SUBTREE));
  				
  				pasteLabel = selection.containsType(Branch.class) && 
  						(clipboard.getContentType().equals(ClipboardContentType.LABEL) ||
  						 clipboard.getContentType().equals(ClipboardContentType.LABELS));
  		
  				pasteLegend = selection.containsType(Node.class) && 
  			      clipboard.getContentType().equals(ClipboardContentType.LEGEND);
  				break;
    	  case 2:
  				pasteLegend = selection.containsOnlyType(Node.class) && 
			        clipboard.getContentType().equals(ClipboardContentType.LEGEND);
				break;
    	}
    }

		setEnabled(pasteNode || pasteLabel || pasteLegend);
	}

	
	@Override
	protected void onActionPerformed(ActionEvent e, TreeInternalFrame frame) {
		PaintableElement selected = null;
		if (!frame.getDocument().getTree().isEmpty()) {
			selected = frame.getTreeViewPanel().getSelection().first(); 
		}
		TreeClipboard clipboard = Main.getInstance().getClipboard(); 
		DocumentEdit edit = null;
		switch (clipboard.getContentType()) {
			case SUBTREE:
				Node root = clipboard.getSubtree();
				Legend[] legends = clipboard.getSubtreeLegends();
				if (contrastManager.ensureContrast(frame.getDocument(), root, legends)) {
					if (selected instanceof Node) {
						edit = new PasteSubtreeEdit(frame.getDocument(), 
								(Node)selected, root, legends);
					}
					else if (selected instanceof Branch) {
						edit = new PasteSiblingEdit(frame.getDocument(),
								((Branch)selected).getTargetNode(), root, legends);
					}
					else {  // Document is empty (selected == null)
						edit = new PasteRootEdit(frame.getDocument(), root, legends);
					}
				}
				break;
			case LABEL:
				Label label = clipboard.getLabel();
				if (contrastManager.ensureContrast(frame.getDocument(), label)) {
					label.setID(CollidingIDsDialog.getInstance().checkConflicts(new Branch[]{(Branch)selected}, label.getID()));
  				edit = new PasteLabelEdit(frame.getDocument(), (Branch)selected, label);
				}
				break;
			case LABELS:
				Label[] labels = clipboard.getLabelList();
				if (contrastManager.ensureContrast(frame.getDocument(), labels)) {
					List<String> reservedIDs = new ArrayList<String>(labels.length);
					for (int i = 0; i < labels.length; i++) {
						labels[i].setID(CollidingIDsDialog.getInstance().checkConflicts(
								new Branch[]{(Branch)selected}, labels[i].getID(), reservedIDs));
						reservedIDs.add(labels[i].getID());  // IDs of new labels must also be unique.
          }
  				edit = new PasteAllLabelsEdit(frame.getDocument(), (Branch)selected, labels);
				}
				break;
			case LEGEND:
				Legend legend = clipboard.getLegend();
				if (contrastManager.ensureContrast(frame.getDocument(), legend)) {
					Node anchor2 = null;
					if (frame.getTreeViewPanel().getSelection().size() > 1) {
						Iterator<PaintableElement> iterator = 
							  frame.getTreeViewPanel().getSelection().iterator();
						iterator.next();  // Erstes �berspringen
						anchor2 = (Node)iterator.next();
					}
					edit = new PasteLegendEdit(frame.getDocument(), (Node)selected, anchor2, legend);
				}
				break;
		}
		
		if (edit != null) {
			frame.getDocument().executeEdit(edit);
		}
	}
}