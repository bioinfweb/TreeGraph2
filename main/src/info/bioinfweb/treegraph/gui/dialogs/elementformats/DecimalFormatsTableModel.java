/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2015  Ben Stöver, Sarah Wiechers, Kai Müller
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
package info.bioinfweb.treegraph.gui.dialogs.elementformats;


import java.util.Vector;
import javax.swing.table.AbstractTableModel;



/**
 * Table modes used with the {@link DecimalFormatPanel}.
 * 
 * @author Ben St&ouml;ver
 * @see DecimalFormatPanel
 * @see ElementFormatsDialog
 */
public class DecimalFormatsTableModel extends AbstractTableModel {
	private Vector<String[]> entries = new Vector<String[]>();
	
	
	public DecimalFormatsTableModel() {
		super();
		fillEntryList();
	}
	
	
	private void addEntry(String description, String format) {
		String[] entry = new String[2];
		entry[0] = description;
		entry[1] = format;
		entries.add(entry);
	}
	
	
	private void fillEntryList() {
		addEntry("Integer", "0");
		addEntry("Always one decimal place", "0.0");
		addEntry("Always two decimal places", "0.00");
		addEntry("Up to seven decimal places", "0.#######");
		addEntry("Integer with grouping separator at thousand", "#,###");
		addEntry("Decimal places and grouping separator at thousand", "#,###.#######");
		addEntry("Scientific notation", "0.#######E0");
		addEntry("Scientific notation with two decimal places", "0.00E0");
		addEntry("Percent", "#%");
		addEntry("Percent with one decimal place", "#.0%");
		addEntry("Integer in brackets", "[0];[-0]");
		addEntry("Always one decimal place in brackets", "[0.0];[-0.0]");
		addEntry("Always two decimal places in brackets", "[0.00];[-0.00]");
		addEntry("Up to seven decimal places in brackets", "[0.#######];[-0.#######]");
		addEntry("Scientific notation in brackets", "[0.#######E0];[-0.#######E0]");
		addEntry("Scientific notation (two decimal places, brackets)", "[0.00E0];[-0.00E0]");
		addEntry("Percent in brackets", "[#%];[-#%]");
		addEntry("Percent with one decimal place in brackets", "[#.0%];[-#.0%]");
	}


	@Override
	public Class<?> getColumnClass(int col) {
  	return String.class;
	}

	
	@Override
	public String getColumnName(int col) {
		if (col == 0) {
			return "Description";
		}
		else {  // nur noch 1 m�glich
			return "Format";
		}
	}

	
	public int getColumnCount() {
		return 2;
	}

	
	public int getRowCount() {
		return entries.size();
	}

	
	public Object getValueAt(int row, int col) {
		return entries.get(row)[col];
	}
}