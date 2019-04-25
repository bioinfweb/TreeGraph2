/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2011, 2013-2019  Ben Stöver, Sarah Wiechers, Kai Müller
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
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.regex.Pattern;
import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.Label;
import info.bioinfweb.treegraph.document.Labels;
import info.bioinfweb.treegraph.document.Legends;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.TextElement;
import info.bioinfweb.treegraph.document.TextLabel;
import info.bioinfweb.treegraph.document.Tree;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.undo.edit.ReplaceInNodeDataEdit;
import info.bioinfweb.treegraph.gui.actions.DocumentAction;
import info.bioinfweb.treegraph.gui.dialogs.SearchTextDialog;
import info.bioinfweb.treegraph.gui.mainframe.MainFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeInternalFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeSelection;



public class SearchTextAction extends DocumentAction {
	private SearchTextDialog dialog;
	private TreeSelection selection;
	private Pattern pattern;
	
	
	public SearchTextAction(MainFrame mainFrame) {
		super(mainFrame);
		putValue(Action.NAME, "Search text..."); 
	  putValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY, 7);
	  putValue(Action.MNEMONIC_KEY, KeyEvent.VK_T);
		putValue(Action.SHORT_DESCRIPTION, "Search text");
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke('F', 
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		loadSymbols("Search");
		
		dialog = new SearchTextDialog(mainFrame);
	}

	
	private boolean matches(TextElement element) {
		return pattern.matcher(element.getData().formatValue(
				element.getFormats().getDecimalFormat())).find();
	}	
	
	
	private void searchLabelBlock(Labels labels, boolean above) {
		for (int lineIndex = 0; lineIndex < labels.lineCount(above); lineIndex++) {
			for (int linePos = 0; linePos < labels.labelCount(above, lineIndex); linePos++) {
				Label label = labels.get(above, lineIndex, linePos);
				if ((label instanceof TextLabel) && matches((TextLabel)label)) {
					selection.add(label);
				}
			}
		}
	}
	
	
	private void searchSubtree(Node root) {
		if ((root.isLeaf() || dialog.includeInternalNodeNames()) && matches(root)) {
			selection.add(root);
		}
		searchLabelBlock(root.getAfferentBranch().getLabels(), true);
		searchLabelBlock(root.getAfferentBranch().getLabels(), false);
		
		for (int i = 0; i < root.getChildren().size(); i++) {
			searchSubtree(root.getChildren().get(i));
		}
	}
	
	
	private void searchLegends(Legends legends) {
		for (int i = 0; i < legends.size(); i++) {
			if (matches(legends.get(i))) {
				selection.add(legends.get(i));
			}
		}
	}
	
	
	private void searchScaleBar(Tree tree) {
		if (tree.getFormats().getShowScaleBar() && matches(tree.getScaleBar())) {
			selection.add(tree.getScaleBar());
		}
	}
	
	
	@Override
	protected void onActionPerformed(ActionEvent e, TreeInternalFrame frame) {
		selection = frame.getTreeViewPanel().getSelection();
		if (dialog.execute(frame.getDocument(), selection, frame.getSelectedAdapter())) {
			selection.clear();
			pattern = ReplaceInNodeDataEdit.generatePattern(
					dialog.getText(),	dialog.caseSensitive(), dialog.wordsOnly());
			
			Tree tree = frame.getDocument().getTree();
			searchSubtree(tree.getPaintStart());
			searchLegends(tree.getLegends());
			searchScaleBar(tree);
			
			if (selection.isEmpty()) {
				JOptionPane.showMessageDialog(null, "No elements were found.", "No results", 
			      JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}


	@Override
	public void setEnabled(Document document, TreeSelection selection, NodeBranchDataAdapter tableAdapter) {
		setEnabled((document != null) && 
				((!document.getTree().isEmpty()) || !document.getTree().getLegends().isEmpty()));
	}
}