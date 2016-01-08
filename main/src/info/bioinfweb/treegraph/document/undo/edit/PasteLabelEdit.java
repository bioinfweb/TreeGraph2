/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2015-2016  Ben Stöver, Sarah Wiechers
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
import info.bioinfweb.treegraph.document.TextLabel;



/**
 * Edit used to paste a label from the clipboard into the document.
 * <p>
 * The only difference to {@link InsertLabelEdit} is a different return value in {@link #getPresentationName()}.
 * 
 * @author Ben St&ouml;ver
 */
public class PasteLabelEdit extends InsertLabelEdit {
	public PasteLabelEdit(Document document, Branch branch, Label label) {
		super(document, label, branch.getLabels());
	}


	public String getPresentationName() {
		if (label instanceof TextLabel) {
			return "Paste text-label \"" + ((TextLabel)label).getData() + "\"";
		}
		else {
			return "Paste graphical label";
		}
	}
}