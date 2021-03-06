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
package info.bioinfweb.treegraph.document.change;


import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.undo.DocumentEdit;

import javax.swing.event.ChangeEvent;



/**
 * Event that indicates all types of changes in TreeGraph 2 document.
 * 
 * @author Ben St&ouml;ver
 */
public class DocumentChangeEvent extends ChangeEvent {
	private DocumentEdit edit;
	
	
	public DocumentChangeEvent(Document document, DocumentEdit edit) {
		super(document);
		if ((edit != null) && (!edit.getDocument().equals(document))) {
			throw new IllegalArgumentException("The document specified as the source of this event must be equal to the document "
							+ "associated with the specified edit.");
		}
		this.edit = edit;
	}
	

	@Override
	public Document getSource() {
		return (Document)super.getSource();
	}


	public DocumentEdit getEdit() {
		return edit;
	}
}