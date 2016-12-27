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
package info.bioinfweb.treegraph.gui.dialogs.io;


import info.bioinfweb.treegraph.Main;
import info.bioinfweb.treegraph.document.Branch;
import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.gui.treeframe.TreeScrollPane;
import info.bioinfweb.wikihelp.client.OkCancelApplyWikiHelpDialog;

import java.awt.BorderLayout;
import java.awt.Frame;

import javax.swing.JPanel;



public class ExportBranchLabelCaptionDialog extends OkCancelApplyWikiHelpDialog {
	private JPanel jContentPane = null;
	private TreeScrollPane outputDocumentScrollPane;
	
	
	public ExportBranchLabelCaptionDialog(Frame owner) {
		super(owner, true, Main.getInstance().getWikiHelp());
		initialize();
		setHelpCode(91);
		setLocationRelativeTo(owner);
	}

	
	@Override
	protected boolean apply() {
		// TODO Auto-generated method stub
		return true;
	}
	
	
	public boolean execute(Branch branch) {
		Node root = getOutputDocument().getTree().getPaintStart();
		root.setAfferentBranch(branch.clone());
		root.getHiddenDataMap().clear();
		root.getHiddenDataMap().putAll(branch.getTargetNode().getHiddenDataMap());  //TODO This step can be removed, if new pie chart label classes do not need source data anymore.
		//TODO Replace pie chart labels by special super class instances.
		//TODO Replace value of text labels by label ID.
		//TODO Show icon label ID somewhere?
		
		getOutputDocument().getTree().updateElementSet();
		getOutputDocumentScrollPane().getTreeViewPanel().setDocument(getOutputDocument());  // Necessary to reposition elements.
		
		return execute();
	}
	
	
	private Document getOutputDocument() {
		return getOutputDocumentScrollPane().getTreeViewPanel().getDocument();
	}
	
	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setTitle("Export pie chart label explanation");
		this.setContentPane(getJContentPane());
		this.pack();
	}


	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout(0, 0));
			
			jContentPane.add(getOutputDocumentScrollPane(), BorderLayout.CENTER);
			
			getApplyButton().setVisible(false);
			getOkButton().setText("Next >");
			jContentPane.add(getButtonsPanel(), BorderLayout.SOUTH);
		}
		return jContentPane;
	}
	
	
	private TreeScrollPane getOutputDocumentScrollPane() {
		if (outputDocumentScrollPane == null) {
			Document document = new Document(false);
			document.getTree().setPaintStart(Node.newInstanceWithBranch());
			document.getTree().updateElementSet();
			outputDocumentScrollPane = new TreeScrollPane(document);
		}
		return outputDocumentScrollPane;
	}
}
