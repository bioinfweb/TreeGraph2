/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2015  Ben Stöver, Kai Müller
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


import info.bioinfweb.treegraph.document.Branch;
import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.Label;
import info.bioinfweb.treegraph.document.change.DocumentChangeType;
import info.bioinfweb.treegraph.document.undo.ComposedDocumentEdit;



/**
 * Used to insert a copy of the specified label onto each specified branch.
 * <p>
 * Note that the specified instance itself will not be inserted anywhere directly.
 * 
 * @author Ben St&ouml;ver
 * @since 2.0.26
 */
public class InsertLabelsEdit extends ComposedDocumentEdit {
	public InsertLabelsEdit(Document document, Label label, Branch[] branches) {
		super(document, DocumentChangeType.TOPOLOGICAL_BY_RENAMING);  // A label could be inserted with the ID of the default leafs adapter.
		
		for (int i = 0; i < branches.length; i++) {
			InsertLabelEdit edit = new InsertLabelEdit(document, label.clone(), branches[i].getLabels());
			edit.setIsSubedit(true);
			getEdits().add(edit);
		}
	}


	public String getPresentationName() {
		return "Insert label(s)";
	}
}