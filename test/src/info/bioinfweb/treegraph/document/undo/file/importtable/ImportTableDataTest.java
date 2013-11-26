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
package info.bioinfweb.treegraph.document.undo.file.importtable;


import java.io.File;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import info.bioinfweb.treegraph.document.TextElementData;
import info.webinsel.util.SystemUtils;

import org.junit.* ;


import static org.junit.Assert.* ;



public class ImportTableDataTest {
	private Set<TextElementData> createUniqueNamesKeySet() {
		Set<TextElementData> result = new TreeSet<TextElementData>();
		result.add(new TextElementData("1pi7kjb0dm"));
		result.add(new TextElementData("tbsecbvo6k"));
		result.add(new TextElementData("h2xfhl93eu"));
		result.add(new TextElementData("xrqw8ac9bc"));
		result.add(new TextElementData("s11ocm2f6o"));
		result.add(new TextElementData("iowa01faj8"));
		result.add(new TextElementData("fvp06elgwh"));
		return result;
	}
	
	
	private void test_tableUnique(String fileName, boolean containsHeadings, int linesToSkip) {
  	ImportTableParameters parameters = new ImportTableParameters();
  	parameters.setTableFile(new File("data" + SystemUtils.FILE_SEPARATOR + "importTable" + 
  	    SystemUtils.FILE_SEPARATOR + fileName));
  	parameters.setColumnSeparator('\t');
  	parameters.setHeadingContained(containsHeadings);
  	parameters.setLinesToSkip(linesToSkip);
  	
  	try {
  		final int columnCount = 2;
  		final int rowCount = 7;
  		
  		ImportTableData data = new ImportTableData(parameters);
  		assertEquals(columnCount, data.columnCount());
  		assertEquals(rowCount, data.rowCount());
  		
  		assertEquals(containsHeadings, data.containsHeadings());
  		if (containsHeadings) {
  			assertEquals("Data1", data.getHeading(0));
  			assertEquals("Data2", data.getHeading(1));
  		}
  		
  		assertEquals(createUniqueNamesKeySet(), data.keySet());
  		
  		for (int row = 0; row < rowCount; row++) {
	      assertEquals("V0" + row, data.getTableValue(0, row));
      }
      assertEquals("V12", data.getTableValue(1, 2));
      assertEquals("V14", data.getTableValue(1, 4));
  	}
  	catch (Exception e) {
  		e.printStackTrace();
  		fail(e.getMessage());
  	}
	}
	
	
  @Test
  public void test_constructor__noHeadings_noSkipped() {
  	test_tableUnique("TableUnique_noHeadings_noSkipped.txt", false, 0);
  }
	
	
  @Test
  public void test_constructor_noHeadings_3skipped() {
  	test_tableUnique("TableUnique_noHeadings_3skipped.txt", false, 3);
  }
	
	
  @Test
  public void test_constructor_headings_noSkipped() {
  	test_tableUnique("TableUnique_headings_noSkipped.txt", true, 0);
  }
	
	
  @Test
  public void test_constructor_headings_3skipped() {
  	test_tableUnique("TableUnique_headings_3skipped.txt", true, 3);
  }
}
