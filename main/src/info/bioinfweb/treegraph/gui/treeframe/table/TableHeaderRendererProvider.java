/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2011, 2013-2017  Ben Stöver, Sarah Wiechers, Kai Müller
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
package info.bioinfweb.treegraph.gui.treeframe.table;


import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;



/**
 * Creates and provides renderers for the heading cells of the metadata table of a document. The returned renderers together display the metadata tree
 * above the table.
 * 
 * @author Ben St&ouml;ver
 */
public class TableHeaderRendererProvider implements TableCellRenderer {
	//TODO Minimal widths for some cells may need to be provided. Can this be done using the minWidth of the returned components or must it be done somewhere else?
	//TODO When is getTableCellRendererComponent() called? How can the renderer for a certain column be changes, when the metadata tree changes? Do any additional 
	//     events need to be triggered?

	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		// TODO Auto-generated method stub
		return null;
	}
}
