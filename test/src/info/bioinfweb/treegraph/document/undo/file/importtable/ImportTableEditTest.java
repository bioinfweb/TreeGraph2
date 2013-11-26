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


import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.TextLabel;
import info.bioinfweb.treegraph.document.io.ReadWriteFactory;
import info.bioinfweb.treegraph.document.io.ReadWriteFormat;
import info.bioinfweb.treegraph.document.nodebranchdata.NewHiddenNodeDataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.NewTextLabelAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.UniqueNameAdapter;
import info.webinsel.util.SystemUtils;

import java.io.File;

import org.junit.* ;


import static org.junit.Assert.* ;


public class ImportTableEditTest {
  @Test
  public void test_redo_noHeadin() {
  	final String fileName = "TableUnique_noHeadings_noSkipped.txt";
  	final boolean containsHeadings = false;
  	final int linesToSkip = 0;
  	final String id1 = "HND";
  	final String id2 = "Label";
  	
  	ImportTableParameters parameters = new ImportTableParameters();
  	parameters.setTableFile(new File("data" + SystemUtils.FILE_SEPARATOR + "importTable" + 
  	    SystemUtils.FILE_SEPARATOR + fileName));
  	parameters.setColumnSeparator('\t');
  	parameters.setHeadingContained(containsHeadings);
  	parameters.setLinesToSkip(linesToSkip);
  	parameters.setKeyAdapter(UniqueNameAdapter.getSharedInstance());
  	parameters.setImportAdapters(
  			new NodeBranchDataAdapter[]{new NewHiddenNodeDataAdapter(id1), new NewTextLabelAdapter(id2)});
  	parameters.setParseNumbericValues(false);
  	parameters.setIgnoreWhitespace(true);
  	
  	try {
  		ImportTableData data = new ImportTableData(parameters);
  	  Document document = ReadWriteFactory.getInstance().getReader(ReadWriteFormat.XTG).read(
  	  		new File("data" + SystemUtils.FILE_SEPARATOR + "importTable" + SystemUtils.FILE_SEPARATOR + "Tree.xtg"));
  	  document.executeEdit(new ImportTableEdit(document, parameters, data));
  	  
  	  assertEquals("V00", document.getTree().getNodeByUniqueName("1pi7kjb0dm").getHiddenDataMap().get(id1).getText());
  	  assertEquals("V01", document.getTree().getNodeByUniqueName("tbsecbvo6k").getHiddenDataMap().get(id1).getText());
  	  assertEquals("V02", document.getTree().getNodeByUniqueName("h2xfhl93eu").getHiddenDataMap().get(id1).getText());
  	  assertEquals("V03", document.getTree().getNodeByUniqueName("xrqw8ac9bc").getHiddenDataMap().get(id1).getText());
  	  assertEquals("V04", document.getTree().getNodeByUniqueName("s11ocm2f6o").getHiddenDataMap().get(id1).getText());
  	  assertEquals("V05", document.getTree().getNodeByUniqueName("iowa01faj8").getHiddenDataMap().get(id1).getText());
  	  assertEquals("V06", document.getTree().getNodeByUniqueName("fvp06elgwh").getHiddenDataMap().get(id1).getText());

  	  assertEquals("V12",	((TextLabel)document.getTree().getNodeByUniqueName("h2xfhl93eu").getAfferentBranch().
  	  	  getLabels().get(id2)).getData().getText());
  	  assertEquals("V14",	((TextLabel)document.getTree().getNodeByUniqueName("s11ocm2f6o").getAfferentBranch().
  	  	  getLabels().get(id2)).getData().getText());
  	}
  	catch (Exception e) {
  		e.printStackTrace();
  		fail(e.getMessage());
  	}
  }
}
