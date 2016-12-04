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
package info.bioinfweb.treegraph.document.undo;


import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import info.bioinfweb.treegraph.document.Branch;
import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.TreePath;
import info.bioinfweb.treegraph.document.change.DocumentChangeType;



/**
 * This class is the ancestor to all edits that are too complex to have an own {@link #undo()}
 * method. Descendant classes do not specify a {@link #redo()} and an {@link #undo()} method 
 * but override the abstract method {@link #performRedo()}.
 * <p>
 * This class creates a copy of the old tree in the document before calling {@link #performRedo()}
 * so all classes implementing {@link #performRedo()} have to translate references to elements of
 * the old tree by using the {@link #findEquivilant(Node)} or {@link #findEquivalent(Branch)} methods.
 * 
 * @author Ben St&ouml;ver
 */
public abstract class ComplexDocumentEdit extends DocumentEdit {
	private Node oldRoot = null;
	private Node newRoot = null;
	private boolean firstRedone = false;
	
	
	public ComplexDocumentEdit(Document document, DocumentChangeType changeType) {
		super(document, changeType);
		oldRoot = document.getTree().getPaintStart();
		newRoot = oldRoot.cloneWithSubtree(true);  // Unique names are copied as well.
		// Legends do not have to be copied since they are anchored by unique node names.
		//TODO A legend could be anchored on a deleted node!
	}
	
	
	/**
	 * Returns the node in the new tree (copy of the old) with the equivalent position
	 * to <code>old</code>.
	 * 
	 * @param old - the node in the old tree
	 * @return the node in the new tree
	 */
	public Node findEquivilant(Node old) {
		return new TreePath(old).findNode(newRoot);
	}

	
	/**
	 * Returns the branch in the new tree (copy of the old) with the equivalent position
	 * to <code>old</code>.
	 * 
	 * @param old - the branch in the old tree
	 * @return the branch in the new tree
	 */
	public Branch findEquivalent(Branch old) {
		return new TreePath(old.getTargetNode()).findNode(newRoot).getAfferentBranch();
	}

	
	protected abstract void performRedo();
	
	
	@Override
	public void redo() throws CannotRedoException {
		getDocument().getTree().setPaintStart(newRoot);
		
		getDocument().getTree().updateElementSet();  // This is done again in super.redo(), but should also be done before performRedo(), so that these collections contain values according to the copied tree this method works on.
		getDocument().getTree().assignUniqueNames();
		
  	if (!firstRedone) {
			performRedo();  // Can't be called in the constructor already
			newRoot = getDocument().getTree().getPaintStart();  // In case the root changed in performRedo()
			firstRedone = true;
		}
		super.redo();
	}


	@Override
	public void undo() throws CannotUndoException {
		getDocument().getTree().setPaintStart(oldRoot);
		super.undo();
	}
}