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
package info.bioinfweb.treegraph.document.undo.file.importtable;


import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import info.bioinfweb.treegraph.document.TextElementData;
import info.bioinfweb.commons.SystemUtils;

import org.junit.* ;

import static org.junit.Assert.* ;



public class ImportTableDataTest {
	private Map<TextElementData, Integer> createUniqueNamesKeyMap() {
		Map<TextElementData, Integer> result = new TreeMap<TextElementData, Integer>();
		result.put(new TextElementData("1pi7kjb0dm"), 0);
		result.put(new TextElementData("tbsecbvo6k"), 1);
		result.put(new TextElementData("h2xfhl93eu"), 2);
		result.put(new TextElementData("xrqw8ac9bc"), 3);
		result.put(new TextElementData("s11ocm2f6o"), 4);
		result.put(new TextElementData("iowa01faj8"), 5);
		result.put(new TextElementData("fvp06elgwh"), 6);
		return result;
	}
	
	
	private void test_tableUnique(String fileName, boolean containsHeadings, int linesToSkip) throws Exception {
  	ImportTableParameters parameters = new ImportTableParameters();
  	parameters.setTableFile(new File("data" + SystemUtils.FILE_SEPARATOR + "importTable" + 
  	    SystemUtils.FILE_SEPARATOR + fileName));
  	parameters.setColumnSeparator('\t');
  	parameters.setHeadingContained(containsHeadings);
  	parameters.setLinesToSkip(linesToSkip);
  	
		final int columnCount = 3;
		final int rowCount = 7;
		
		ImportTableData data = new ImportTableData(parameters);
		data.processKeyColumn(0, parameters);
		
		// Test size:
		assertEquals(columnCount, data.columnCount());
		assertEquals(rowCount, data.rowCount());
		
		// Test headings:
		assertEquals(containsHeadings, data.containsHeadings());
		if (containsHeadings) {
			assertEquals("Data1", data.getHeading(1));
			assertEquals("Data2", data.getHeading(2));
		}
		
		// Test keys:
		Map<TextElementData, Integer> keyMap = createUniqueNamesKeyMap();
		assertEquals(keyMap.keySet(), data.keySet());
		Iterator<TextElementData> keyIterator = keyMap.keySet().iterator();
		while (keyIterator.hasNext()) {
			TextElementData key = keyIterator.next();
			assertEquals((int)keyMap.get(key), data.getRowByKey(key));
		}
		
		// Test contents:
		for (int row = 0; row < rowCount; row++) {
      assertEquals("V0" + row, data.getTableValue(1, row));
    }
    assertEquals("V12", data.getTableValue(2, 2));
    assertEquals("V14", data.getTableValue(2, 4));
	}
	
	
  @Test
  public void test_constructor_noHeadings_noSkipped() throws Exception {
  	test_tableUnique("TableUnique_noHeadings_noSkipped.txt", false, 0);
  }
	
	
  @Test
  public void test_constructor_noHeadings_3skipped() throws Exception {
  	test_tableUnique("TableUnique_noHeadings_3skipped.txt", false, 3);
  }
	
	
  @Test
  public void test_constructor_headings_noSkipped() throws Exception {
  	test_tableUnique("TableUnique_headings_noSkipped.txt", true, 0);
  }
	
	
  @Test
  public void test_constructor_headings_3skipped() throws Exception {
  	test_tableUnique("TableUnique_headings_3skipped.txt", true, 3);
  }
  
  
  @Test(expected=DuplicateKeyException.class)
  public void test_constructor_diplucateKey() throws Exception {
  	ImportTableParameters parameters = new ImportTableParameters();
  	parameters.setTableFile(new File("data" + SystemUtils.FILE_SEPARATOR + "importTable" + 
  	    SystemUtils.FILE_SEPARATOR + "TableUnique_noHeadings_noSkipped_diplicateKey.txt"));
  	parameters.setColumnSeparator('\t');
  	parameters.setHeadingContained(false);
  	parameters.setLinesToSkip(0);
  	
 		new ImportTableData(parameters).processKeyColumn(0, parameters);;
  }	
  
  
  @Test(expected=InsufficientTableSizeException.class)
  public void test_constructor_insufficientTableSize() throws Exception {
  	ImportTableParameters parameters = new ImportTableParameters();
  	parameters.setTableFile(new File("data" + SystemUtils.FILE_SEPARATOR + "importTable" + 
  	    SystemUtils.FILE_SEPARATOR + "TableUnique_noHeadings_noSkipped_noDataColumns.txt"));
  	parameters.setColumnSeparator('\t');
  	parameters.setHeadingContained(false);
  	parameters.setLinesToSkip(0);
  	
 		new ImportTableData(parameters);
  }	
  
  
  @Test
  public void test_constructor_emptyDataColumn() throws Exception {
  	ImportTableParameters parameters = new ImportTableParameters();
  	parameters.setTableFile(new File("data" + SystemUtils.FILE_SEPARATOR + "importTable" + 
  	    SystemUtils.FILE_SEPARATOR + "TableUnique_noHeadings_noSkipped_emptyDataColumn.txt"));
  	parameters.setColumnSeparator('\t');
  	parameters.setHeadingContained(false);
  	parameters.setLinesToSkip(0);
  	
		final int columnCount = 2;
		final int rowCount = 7;
		
		ImportTableData data = new ImportTableData(parameters);
		
		// Test size:
		assertEquals(columnCount, data.columnCount());
		assertEquals(rowCount, data.rowCount());
		
		// Test headings:
		assertFalse(data.containsHeadings());
		
		// Test contents:
		for (int row = 0; row < rowCount; row++) {
      assertEquals("", data.getTableValue(1, row));
    }
  }	
}
