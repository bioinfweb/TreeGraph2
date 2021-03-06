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


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import info.bioinfweb.commons.SystemUtils;
import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.TextLabel;
import info.bioinfweb.treegraph.document.io.ReadWriteFactory;
import info.bioinfweb.treegraph.document.io.ReadWriteFormat;
import info.bioinfweb.treegraph.document.nodebranchdata.NewHiddenNodeDataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.NewTextLabelAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeNameAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.UniqueNameAdapter;

import java.io.File;

import org.junit.Test;



public class ImportTableEditTest {
  @Test
  public void test_redo_uniqueNoHeading() throws Exception {
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
  			new NodeBranchDataAdapter[]{UniqueNameAdapter.getSharedInstance(), new NewHiddenNodeDataAdapter(id1), new NewTextLabelAdapter(id2)});
  	parameters.setParseNumericValues(false);
  	parameters.setIgnoreWhitespace(true);
  	
		ImportTableData data = new ImportTableData(parameters);
		data.processKeyColumn(0, parameters);
	  Document document = ReadWriteFactory.getInstance().getReader(ReadWriteFormat.XTG).read(
	  		new File("data" + SystemUtils.FILE_SEPARATOR + "importTable" + SystemUtils.FILE_SEPARATOR + "Tree.xtg"));
	  ImportTableEdit edit = new ImportTableEdit(document, parameters, data);
	  document.executeEdit(edit);
	  
	  assertTrue(edit.isAllKeysFound());
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
  
  
  private void test_NodeNames(String treeFile, String tableFile, boolean ignoreWhitespace, boolean parseNumbers, 
  		boolean distinguishSpaceUnderscore, boolean caseSensitive) throws Exception {
  	
  	final boolean containsHeadings = false;
  	final int linesToSkip = 0;
  	final String id1 = "HND";
  	final String id2 = "Label";
  	
  	ImportTableParameters parameters = new ImportTableParameters();
  	parameters.setTableFile(new File("data" + SystemUtils.FILE_SEPARATOR + "importTable" + 
  	    SystemUtils.FILE_SEPARATOR + tableFile));
  	parameters.setColumnSeparator('\t');
  	parameters.setHeadingContained(containsHeadings);
  	parameters.setLinesToSkip(linesToSkip);
  	parameters.setKeyAdapter(NodeNameAdapter.getSharedInstance());
  	parameters.setImportAdapters(
  			new NodeBranchDataAdapter[]{new UniqueNameAdapter(), new NewHiddenNodeDataAdapter(id1), new NewTextLabelAdapter(id2)});
  	parameters.setParseNumericValues(parseNumbers);
  	parameters.setIgnoreWhitespace(ignoreWhitespace);
  	parameters.setDistinguishSpaceUnderscore(distinguishSpaceUnderscore);
  	parameters.setCaseSensitive(caseSensitive);
  	
		ImportTableData data = new ImportTableData(parameters);
		data.processKeyColumn(0, parameters);
		
	  Document document = ReadWriteFactory.getInstance().getReader(ReadWriteFormat.XTG).read(
	  		new File("data" + SystemUtils.FILE_SEPARATOR + "importTable" + SystemUtils.FILE_SEPARATOR + treeFile));
	  ImportTableEdit edit = new ImportTableEdit(document, parameters, data);
	  document.executeEdit(edit);
	  
	  assertTrue(edit.isAllKeysFound());
    Node node = document.getTree().getPaintStart().getChildren().get(1);  // D
	  assertEquals("L4", node.getHiddenDataMap().get(id1).getText());
	  node = node.getParent().getChildren().get(0).getChildren().get(1);  // C
	  assertEquals("L3", node.getHiddenDataMap().get(id1).getText());
	  assertEquals("LL3", ((TextLabel)node.getAfferentBranch().getLabels().get(id2)).getData().getText());
	  node = node.getParent().getChildren().get(0).getChildren().get(1);  // B
	  assertEquals("L2", node.getHiddenDataMap().get(id1).getText());
	  node = node.getParent().getChildren().get(0);  // A
 	  assertEquals("L1", node.getHiddenDataMap().get(id1).getText());
  }
  
  
  @Test
  public void test_redo_nodeNames() throws Exception {
  	test_NodeNames("Tree.xtg", "TableNodeNames.txt", false, false, true, true);
  }
  
  
  /**
   * Tests the options {@code ignoreWhitespace, distinguishSpaceUnderscore} and {@code caseSensitive}.
   * @throws Exception 
   */
  @Test
  public void test_redo_nodeNames_tableOptions() throws Exception {
  	test_NodeNames("Tree.xtg", "TableNodeNames_options.txt", true, true, false, false);
  }
  
  
  /**
   * Tests the options {@code ignoreWhitespace, distinguishSpaceUnderscore} and {@code caseSensitive}.
   * @throws Exception 
   */
  @Test
  public void test_redo_nodeNames_treeOptions() throws Exception {
  	test_NodeNames("Tree_options.xtg", "TableNodeNames.txt", true, true, false, false);
  }
  
  
  /**
   * Tests the option {@code parseNumericValues}.
   * Decimal values shall not be parsed, because the won't be unique anymore than.
   * @throws Exception 
   */
  @Test
  public void test_redo_nodeNames_numbersAsText() throws Exception {
  	test_NodeNames("Tree_numbersAsText.xtg", "TableNodeNames_numbersAsText.txt", false, false, true, true);
  }
  
  
  private void testNodeNamesNumbersAsDecimal(String treeName) throws Exception {
  	final String id = "ID";
  	ImportTableParameters parameters = new ImportTableParameters();
  	parameters.setTableFile(new File("data" + SystemUtils.FILE_SEPARATOR + "importTable" + 
  	    SystemUtils.FILE_SEPARATOR + "TableNodeNames_numbersAsDec.txt"));
  	parameters.setColumnSeparator('\t');
  	parameters.setHeadingContained(false);
  	parameters.setLinesToSkip(0);
  	parameters.setKeyAdapter(NodeNameAdapter.getSharedInstance());
  	parameters.setImportAdapters(new NodeBranchDataAdapter[]{new NodeNameAdapter(), new NewHiddenNodeDataAdapter(id)});
  	parameters.setParseNumericValues(true);
  	
		ImportTableData data = new ImportTableData(parameters);
		data.processKeyColumn(0, parameters);
	  Document document = ReadWriteFactory.getInstance().getReader(ReadWriteFormat.XTG).read(
	  		new File("data" + SystemUtils.FILE_SEPARATOR + "importTable" + SystemUtils.FILE_SEPARATOR + treeName));
	  ImportTableEdit edit = new ImportTableEdit(document, parameters, data);
	  document.executeEdit(edit);

	  assertTrue(edit.isAllKeysFound());
    Node node = document.getTree().getPaintStart().getChildren().get(1);  // 2.1
	  assertTrue(node.getHiddenDataMap().isEmpty());
	  node = node.getParent().getChildren().get(0).getChildren().get(1);  // 2.0
	  assertEquals("L2", node.getHiddenDataMap().get(id).getText());
	  node = node.getParent().getChildren().get(0).getChildren().get(1);  // 2
	  assertEquals("L2", node.getHiddenDataMap().get(id).getText());
	  node = node.getParent().getChildren().get(0);  // 1
	  assertEquals("L1", node.getHiddenDataMap().get(id).getText());
  }
  
  
  @Test
  public void test_redo_nodeNames_numbersAsDecimal() throws Exception {
  	testNodeNamesNumbersAsDecimal("Tree_numbersAsText.xtg");
  }
  
  
  @Test
  public void test_redo_nodeNames_numbersAsDecimalGerman() throws Exception {
  	testNodeNamesNumbersAsDecimal("Tree_numbersAsText_german.xtg");
  }
  
  
  private void testNodeNamesNumbersAsTextException(String tableName) {
  	ImportTableParameters parameters = new ImportTableParameters();
  	parameters.setTableFile(new File("data" + SystemUtils.FILE_SEPARATOR + "importTable" + 
  	    SystemUtils.FILE_SEPARATOR + tableName));
  	parameters.setColumnSeparator('\t');
  	parameters.setHeadingContained(false);
  	parameters.setLinesToSkip(0);
  	parameters.setKeyAdapter(NodeNameAdapter.getSharedInstance());
  	parameters.setParseNumericValues(true);
  	
  	try {
  		ImportTableData data = new ImportTableData(parameters);
  		data.processKeyColumn(0, parameters);
  	  Document document = ReadWriteFactory.getInstance().getReader(ReadWriteFormat.XTG).read(
  	  		new File("data" + SystemUtils.FILE_SEPARATOR + "importTable" + SystemUtils.FILE_SEPARATOR + "Tree_numbersAsText.xtg"));
  	  ImportTableEdit edit = new ImportTableEdit(document, parameters, data);
  	  document.executeEdit(edit);
    	fail("DuplicateKeyException not thrown.");
  	}
  	catch (DuplicateKeyException e) {}  // expected program flow, nothing to do
  	catch (Exception e) {
  		e.printStackTrace();
  		fail(e.getMessage());
  	}
  }
  

  @Test
  public void test_redo_nodeNames_numbersAsDecException() {
  	testNodeNamesNumbersAsTextException("TableNodeNames_numbersAsText.txt");
  }
  

  @Test
  public void test_redo_nodeNames_numbersAsDecGermanException() {
  	testNodeNamesNumbersAsTextException("TableNodeNames_numbersAsText_german.txt");
  }
  
  
  @Test
  public void test__redo_replaceNodeNames() throws Exception {
  	final boolean containsHeadings = false;
  	final int linesToSkip = 0;
  	
  	ImportTableParameters parameters = new ImportTableParameters();
  	parameters.setTableFile(new File("data" + SystemUtils.FILE_SEPARATOR + "importTable" + 
  	    SystemUtils.FILE_SEPARATOR + "TableNodeNames_replace.txt"));
  	parameters.setColumnSeparator('\t');
  	parameters.setHeadingContained(containsHeadings);
  	parameters.setLinesToSkip(linesToSkip);
  	parameters.setKeyAdapter(NodeNameAdapter.getSharedInstance());
  	parameters.setImportAdapters(new NodeBranchDataAdapter[]{NodeNameAdapter.getSharedInstance(), NodeNameAdapter.getSharedInstance()});
  	parameters.setParseNumericValues(false);
  	parameters.setIgnoreWhitespace(false);
  	parameters.setDistinguishSpaceUnderscore(true);
  	parameters.setCaseSensitive(true);
  	
		ImportTableData data = new ImportTableData(parameters);
		data.processKeyColumn(0, parameters);
		
	  Document document = ReadWriteFactory.getInstance().getReader(ReadWriteFormat.XTG).read(
	  		new File("data" + SystemUtils.FILE_SEPARATOR + "importTable" + SystemUtils.FILE_SEPARATOR + "Tree.xtg"));
	  ImportTableEdit edit = new ImportTableEdit(document, parameters, data);
	  document.executeEdit(edit);
	  
	  assertTrue(edit.isAllKeysFound());
    Node node = document.getTree().getPaintStart().getChildren().get(1);  // D
	  assertEquals("Node 4", node.getData().getText());
	  node = node.getParent().getChildren().get(0).getChildren().get(1);  // C
	  assertEquals("Node 3", node.getData().getText());
	  node = node.getParent().getChildren().get(0).getChildren().get(1);  // B
	  assertEquals("Node 2", node.getData().getText());
	  node = node.getParent().getChildren().get(0);  // A
	  assertEquals("Node 1", node.getData().getText());
  }
}
