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
package info.bioinfweb.treegraph.document.undo.file;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Vector;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.TextElementData;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.undo.DocumentEdit;
import info.bioinfweb.treegraph.document.undo.edit.InvalidFormatException;
import info.webinsel.util.Math2;
import info.webinsel.util.io.TableReader;



public class ApplyNameTableEdit extends DocumentEdit {
	private NodeBranchDataAdapter adapter = null;
  private List<String> oldNames = null;
  private List<String> newNames = null;
  private List<TextElementData> savedValues = new Vector<TextElementData>();
  private boolean ignoreWhitespaces;
  private boolean caseSensitive;
  private boolean parseNumericValues;
  
  
	public ApplyNameTableEdit(Document document, NodeBranchDataAdapter adapter, 
			List<String> oldNames, List<String> newNames, boolean ignoreWhitespaces, 
			boolean caseSensitive, boolean parseNumericValues) {
		
		super(document);
		this.adapter = adapter;
		this.oldNames = oldNames;
		this.newNames = newNames;
		this.ignoreWhitespaces = ignoreWhitespaces;
		this.caseSensitive = caseSensitive;
		this.parseNumericValues = parseNumericValues;
		
		if (ignoreWhitespaces) {
			for (int i = 0; i < oldNames.size(); i++) {
				oldNames.set(i, oldNames.get(i).trim());
			}
		}
	}


	/**
	 * Returns the index of the first string that represents the specified 
	 * <code>double</code> value.
	 * @param list
	 * @param value
	 * @return the index or -1 of the value is not found
	 */
	private int findDecimal(List<String> list, double value) {
		for (int i = 0; i < list.size(); i++) {
			try {
				if (Math2.parseDouble(list.get(i)) == value) {
					return i;
				}
			}
			catch (NumberFormatException e) {}  // nothing to do
		}
		return -1;
	}	
	
	
	private int findString(List<String> list, String value) {
		if (!caseSensitive) {
			value = value.toLowerCase();
		}
		for (int i = 0; i < list.size(); i++) {
			String current = list.get(i);
			if (!caseSensitive) {
				current = current.toLowerCase();
			}
			if (value.equals(current)) {
				return i;
			}
		}
		return -1;
	}
	
	
	private void rename(Node root, List<String> oldNames, List<String> newNames) {
		int pos = -1;
		if (adapter.isString(root)) {
			String value = adapter.getText(root);
	    savedValues.add(new TextElementData(value));
			if (ignoreWhitespaces) {
				value = value.trim();
			}
			pos = findString(oldNames, value);
		}
		else if (adapter.isDecimal(root)) {
			double value = adapter.getDecimal(root);
	    savedValues.add(new TextElementData(value));
			pos = findDecimal(oldNames, value);
		}
		else {
			savedValues.add(null);  // Make sure undo method finds the correct number of entries.
		}
		if (pos != -1) {
			if (parseNumericValues && Math2.isDecimal(newNames.get(pos))) {
				adapter.setDecimal(root, Math2.parseDouble(newNames.get(pos)));
			}
			else {
				adapter.setText(root, newNames.get(pos));
			}
		}
		
		for (int i = 0; i < root.getChildren().size(); i++) {
			rename(root.getChildren().get(i), oldNames, newNames);
		}
	}
	
	
	@Override
	public void redo() throws CannotRedoException {
		if (!document.getTree().isEmpty()) {
			savedValues.clear();
			rename(document.getTree().getPaintStart(), oldNames, newNames);
			super.redo();
		}
	}
	
	
	private int restoreNames(Node root, int pos) {
		if (savedValues.get(pos) != null) {
			if (savedValues.get(pos).isDecimal()) {
				adapter.setDecimal(root, savedValues.get(pos).getDecimal());
			}
			else {
				adapter.setText(root, savedValues.get(pos).getText());
			}
		}
		pos++;
		
		for (int i = 0; i < root.getChildren().size(); i++) {
			pos = restoreNames(root.getChildren().get(i), pos);
		}
		return pos;
	}


	@Override
	public void undo() throws CannotUndoException {
		if (!document.getTree().isEmpty()) {
			restoreNames(document.getTree().getPaintStart(), 0);
			super.undo();
		}
	}


	public String getPresentationName() {
		return "Apply name table to " + adapter.toString();
	}
	
	
  public static void loadNameTable(File file, Vector<String> oldNames, 
  		Vector<String> newNames, char separator) 
      throws FileNotFoundException, IOException, InvalidFormatException {
  	
  	loadNameTable(new FileInputStream(file), oldNames, newNames, separator);
  }
	
	
	public static void loadNameTable(InputStream stream, Vector<String> oldNames, 
			Vector<String> newNames, char separator) 
	    throws IOException, InvalidFormatException {
		
		String[][] table = TableReader.readTable(stream, separator);
		
		if ((table.length >= 2) && (table[0].length > 0)) {
			for (int lineNo = 0; lineNo < table[0].length; lineNo++) {
				oldNames.add(table[0][lineNo]);
				newNames.add(table[1][lineNo]);
		  }
		}
		else {
			throw new InvalidFormatException("The table does not have at least one line and two columns.");
		}
	}
}