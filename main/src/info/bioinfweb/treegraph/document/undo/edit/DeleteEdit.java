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
package info.bioinfweb.treegraph.document.undo.edit;



import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import info.bioinfweb.treegraph.document.Branch;
import info.bioinfweb.treegraph.document.ConcretePaintableElement;
import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.Label;
import info.bioinfweb.treegraph.document.Legend;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.Tree;
import info.bioinfweb.treegraph.document.undo.DocumentEdit;
import info.bioinfweb.treegraph.document.undo.WarningEdit;



/**
 * Removes all elements currently selected. <code>RemoveSubtreeEdit</code>, 
 * <code>RemoveLabelEdit</code> and <code>RemoveLegendEdit</code> are used internally. 
 * @author Ben St&ouml;ver
 */
public class DeleteEdit extends DocumentEdit implements WarningEdit {
  private HashSet<ConcretePaintableElement> elements; 
  private Vector<DocumentEdit> edits = new Vector<DocumentEdit>();
  private boolean showWarnings = true;
  
  
	public DeleteEdit(Document document, ConcretePaintableElement[] elements, boolean showWarnings) {
		super(document);
		this.showWarnings = showWarnings;
		this.elements = new HashSet<ConcretePaintableElement>();
		for (int i = 0; i < elements.length; i++) {
			this.elements.add(elements[i]);
		}
		
		deleteSubtreeElements();
		createSubedits();
	}
  
 
	public boolean getShowWarnings() {
		return showWarnings;
	}


	public void setShowWarnings(boolean showWarnings) {
		this.showWarnings = showWarnings;
	}


	private boolean containsParent(Node node) {
		boolean result = false;
		node = node.getParent();  // Nicht prüfen, ob Knoten selbst enthalten ist.
		while ((!result) && (node != null)) {
			result = elements.contains(node);
			node = node.getParent();
		}
		return result;
	}
	
	
	private void deleteSubtreeElements() {
		Iterator<ConcretePaintableElement> iterator = elements.iterator();
		while (iterator.hasNext()) {
			ConcretePaintableElement element = iterator.next();
			Node node = Tree.getLinkedNode(element);
			if ((node != null) && 
					(((node != element) && (elements.contains(node))) || (containsParent(node)))) {
				
				iterator.remove();
			}
		}
	}
	
	
	private void createSubedits() {
		Iterator<ConcretePaintableElement> iterator = elements.iterator();
		while (iterator.hasNext()) {
			DocumentEdit edit = null;
			ConcretePaintableElement element = iterator.next();
			
			if (element instanceof Branch) {
				element = ((Branch)element).getTargetNode();
			}
			
			if (element instanceof Node) {
				int index = 0;
				if (((Node)element).hasParent()) {
					index = ((Node)element).getParent().getChildren().indexOf(
							((Node)element));
				}
				edit = new RemoveSubtreeEdit(document, ((Node)element).getParent(), 
						((Node)element), index, getShowWarnings());
			}
			else if (element instanceof Label) {
				edit = new RemoveLabelEdit(document, ((Label)element), 
						((Label)element).getLabels());
			}
			else if (element instanceof Legend) {
				edit = new RemoveLegendEdit(document, (Legend)element);
			}
			
			if (edit != null) {
				edit.setIsSubedit(true);
				edits.add(edit);
			}
		}
	}


	@Override
	public void redo() throws CannotRedoException {
		for (int i = 0; i < edits.size(); i++) {
			edits.get(i).redo();
		}
		super.redo();
	}


	@Override
	public void undo() throws CannotUndoException {
		for (int i = edits.size() - 1; i >= 0; i--) {
			edits.get(i).undo();
		}
		super.undo();
	}


	public String getPresentationName() {
		return "Delete element(s)";
	}
}