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
package info.bioinfweb.treegraph.document.undo.file.importtable;


import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import info.bioinfweb.treegraph.document.TextElementData;
import info.bioinfweb.commons.Math2;
import info.bioinfweb.commons.io.TableReader;



/**
 * Loads and stores the contents of a table file to be imported in node/branch data columns.
 * 
 * @author BenStoever
 * @since 2.0.50
 */
public class ImportTableData {
  private String[][] data;
  private int rowOffset = 0;
  private boolean containsHeadings = false;
  private Map<TextElementData, Integer> keyToLineMap = new TreeMap<TextElementData, Integer>();

  
	/**
	 * Creates a new instance of this class and reads the contents from the table file specified
	 * in {@code parameters}.
	 * 
	 * @param parameters - the parameters specifying the table to be loaded
	 * @throws IOException
	 * @throws DuplicateKeyException - if the first column of the loaded table contains duplicate entries
	 *         (also depending on the way keys are treated as specified in {@code parameters}).
	 * @throws InsufficientTableSizeException - if the input file does not at least contain two columns and one row
	 */
	public ImportTableData(ImportTableParameters parameters) 
			throws IOException, DuplicateKeyException, InsufficientTableSizeException {
		
	  super();
	  
	  data = TableReader.readTable(parameters.getTableFile(), parameters.getColumnSeparator());
	  rowOffset = parameters.getLinesToSkip();
	  containsHeadings = parameters.isHeadingContained();
	  if (containsHeadings) {
	  	rowOffset++;
	  }
	  
	  if ((columnCount() >= 1) && (rowCount() >= 1)) {
	  	processKeyColumn(parameters);
	  }
	  else {
	  	throw new InsufficientTableSizeException(columnCount(), rowCount());
	  }
  }
	
	
	private void processKeyColumn(ImportTableParameters parameters) throws DuplicateKeyException {
		keyToLineMap.clear();
		DuplicateKeyException exception = null;
		for (int row = 0; row < rowCount(); row++) {
			TextElementData key = parameters.createEditedValue(data[0][row + rowOffset]);
			if (keyToLineMap.containsKey(key)) {  // Duplicate key value
				if (exception == null) {
					exception = new DuplicateKeyException();
				}
				exception.addKey(key.toString());
			}
			else {
				keyToLineMap.put(key, row);
			}
    }
		
		if (exception != null) {
			throw exception;
		}
	}
	
	
	public int getRowByKey(TextElementData key) {
		Integer result = keyToLineMap.get(key);
		if (result == null) {
			return -1;
		}
		else {
			return result;
		}
	}
	
	
	public Set<TextElementData> keySet() {
	  return keyToLineMap.keySet();
  }


	/**
	 * Returns a cell value from the loaded table.
	 * 
	 * @param column - the column index of the cell (The first column after the key column has the index 0.)
	 * @param row - the row index of the cell (starting with 0)
	 */
	public String getTableValue(int column, int row) {
		if (Math2.isBetween(column, 0, columnCount() - 1) && Math2.isBetween(row, 0, rowCount() - 1)) {
			return data[column + 1][row + rowOffset];  // first column contains the unprocessed keys
		}
		else {
			throw new IllegalArgumentException("Invalid column " + column + " and/or invalid row " + row + ".");
		}
	}
	
	
	public String getUnprocessedKey(int row) {
		if (Math2.isBetween(row, 0, rowCount() - 1)) {
			return data[0][row + rowOffset];  // first column contains the unprocessed keys
		}
		else {
			throw new IllegalArgumentException("Invalid invalid row index " + row + ".");
		}
	}
	
	
	public String getHeading(int column) {
		if (containsHeadings()) {
			if (Math2.isBetween(column, 0, columnCount() - 1)) {
				return data[column + 1][rowOffset - 1];
			}
			else {
				throw new IllegalArgumentException("Invalid column index " + column + ".");
			}
		}
		else {
			return "";
		}
	}
	
	
	public int columnCount() {
		return Math.max(data.length - 1, 0);
	}
	
	
	public int rowCount() {
		if (data.length > 0) {
			return data[0].length - rowOffset;
		}
		else {
			return 0;
		}
	}


	public boolean containsHeadings() {
		return containsHeadings;
	}
}
