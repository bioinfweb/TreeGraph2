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


import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.Legend;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.undo.DocumentEdit;



public abstract class InsertRemoveLegendEdit extends DocumentEdit {
  private Legend legend = null;
  private Node[] anchor = new Node[2];

  
	public InsertRemoveLegendEdit(Document document, Legend legend, Node anchor0, Node anchor1) {
		super(document);
		this.legend = legend;
		anchor[0] = anchor0;
		anchor[1] = anchor1;
	}
	
	
	protected void insert() {
		for (int i = 0; i < anchor.length; i++) {
			if (anchor[i] != null) {
				legend.getFormats().setAnchor(i, anchor[i]);
			}
		}
		
		document.getTree().getLegends().insert(legend);
	}
	
	
	protected void remove() {
		document.getTree().getLegends().remove(legend);
	}
}