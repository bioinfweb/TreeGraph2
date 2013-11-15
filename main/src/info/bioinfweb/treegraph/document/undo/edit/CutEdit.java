/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2013  Ben Stöver, Kai Müller
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


import info.bioinfweb.treegraph.document.ConcretePaintableElement;
import info.bioinfweb.treegraph.document.Document;



/**
 * The impelemention of this edit is the same as it is at <code>DeleteEdit</code>. The
 * copying is done by the action-object, becuase the copying does not have to be redone. 
 * @author Ben St&ouml;ver
 */
public class CutEdit extends DeleteEdit {
	public CutEdit(Document document, ConcretePaintableElement[] elements) {
		super(document, elements, true);  //TODO evtl. gelöschte Legenden mitkopieren und dann keine Warnung mehr ausgeben?
	}

	
	@Override
	public String getPresentationName() {
		return "Cut element(s)";
	}
}