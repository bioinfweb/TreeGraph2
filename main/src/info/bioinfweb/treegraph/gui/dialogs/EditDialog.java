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
package info.bioinfweb.treegraph.gui.dialogs;


import java.awt.Dialog;
import java.awt.Frame;

import info.bioinfweb.treegraph.Main;
import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.gui.treeframe.TreeSelection;
import info.bioinfweb.wikihelp.client.OkCancelApplyWikiHelpDialog;



/**
 * Abstract class implementing basic functionality for dialogs used to edit a TreeGraph 2 document.
 * Provides access to the currently active document and its current selection.
 * 
 * @author Ben St&ouml;ver
 */
public abstract class EditDialog extends OkCancelApplyWikiHelpDialog {
	private Document document = null;
	private TreeSelection selection = null;
	private NodeBranchDataAdapter selectedAdapter = null;

	
	public EditDialog(Frame owner) {
		super(owner, true, true, Main.getInstance().getWikiHelp());
	}

	
	public EditDialog(Dialog owner) {
		super(owner, true, true, Main.getInstance().getWikiHelp());
	}
	
	
	public boolean execute(Document document, TreeSelection selection, NodeBranchDataAdapter selectedAdapter) {
		setDocument(document);
		setSelection(selection);
		setSelectedAdapter(selectedAdapter);
		boolean result = false;
		if (onExecute()){
			result = execute();
		}
		return result;
	}
	
	
	protected abstract boolean onExecute();
	
	
	protected Document getDocument() {
		return document;
	}


	protected void setDocument(Document document) {
		this.document = document;
	}


	protected TreeSelection getSelection() {
		return selection;
	}


	protected void setSelection(TreeSelection selection) {
		this.selection = selection;
	}


	protected NodeBranchDataAdapter getSelectedAdapter() {
		return selectedAdapter;
	}


	protected void setSelectedAdapter(NodeBranchDataAdapter selectedAdapter) {
		this.selectedAdapter = selectedAdapter;
	}
}