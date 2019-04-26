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
package info.bioinfweb.treegraph.document.undo.file;


import static info.bioinfweb.treegraph.test.TestTools.*;


import info.bioinfweb.commons.SystemUtils;
import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.Tree;
import info.bioinfweb.treegraph.document.io.ReadWriteFactory;
import info.bioinfweb.treegraph.document.io.ReadWriteFormat;
import info.bioinfweb.treegraph.document.nodebranchdata.HiddenBranchDataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeNameAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.TextIDElementType;
import info.bioinfweb.treegraph.document.nodebranchdata.TextLabelAdapter;

import java.io.File;

import org.junit.Test;



public class AddSupportValuesEditTest {
	private static final String IMPORT_ID = "import";
	private static final NodeBranchDataAdapter SUPPORT_COLUMN = 
			new HiddenBranchDataAdapter(IMPORT_ID + AddSupportValuesEdit.SUPPORT_NAME);
	private static final NodeBranchDataAdapter CONFLICT_COLUMN = 
			new HiddenBranchDataAdapter(IMPORT_ID + AddSupportValuesEdit.CONFLICT_NAME);

	
	//TODO Add more test cases
	//     - Test case where only one terminal from the combined set is above a node (inverted case of test_polytomyIntoSolvedAddTerminals())
	//     - Test conflicting values for all situations
	
	private Tree executeEdit(String targetName, String sourceName, boolean rooted) throws Exception {
	  Document target = ReadWriteFactory.getInstance().getReader(ReadWriteFormat.XTG).read(
	  		new File("data" + SystemUtils.FILE_SEPARATOR + "addSupport" + SystemUtils.FILE_SEPARATOR + targetName));		
	  Document source = ReadWriteFactory.getInstance().getReader(ReadWriteFormat.XTG).read(
	  		new File("data" + SystemUtils.FILE_SEPARATOR + "addSupport" + SystemUtils.FILE_SEPARATOR + sourceName));		

	  AddSupportValuesParameters parameters = new AddSupportValuesParameters();
		parameters.setIdPrefix(IMPORT_ID);
		parameters.setSourceDocument(source);
		parameters.setSourceLeavesColumn(NodeNameAdapter.getSharedInstance());
		parameters.setTerminalsAdapter(NodeNameAdapter.getSharedInstance());
		parameters.setSourceSupportColumn(new TextLabelAdapter("support"));
		parameters.setTargetType(TextIDElementType.HIDDEN_BRANCH_DATA);
		parameters.setRooted(rooted);
		
		target.executeEdit(new AddSupportValuesEdit(target, parameters));
		
		return target.getTree();
	}
	
	
	@Test
	public void test_conflictIntoSolved() throws Exception {
		Tree tree = executeEdit("Solved.xtg", "Conflict.xtg", false);
		
		assertNoAnnotation(tree, "egkw6pwe2g", SUPPORT_COLUMN);
		assertNoAnnotation(tree, "egkw6pwe2g", CONFLICT_COLUMN);
		
		assertNoAnnotation(tree, "x7474zcnij", SUPPORT_COLUMN);
		assertNoAnnotation(tree, "x7474zcnij", CONFLICT_COLUMN);
		
		assertNoAnnotation(tree, "yt4x200u57", SUPPORT_COLUMN);
		assertAnnotation(tree, "yt4x200u57", CONFLICT_COLUMN, 2.0);
		
		assertNoAnnotation(tree, "051l0uv3ok", SUPPORT_COLUMN);
		assertAnnotation(tree, "051l0uv3ok", CONFLICT_COLUMN, 2.0);
	}
	
	
	@Test
	public void test_polytomyIntoSolved() throws Exception {
		Tree tree = executeEdit("Solved.xtg", "Polytomy.xtg", true);
		
		assertNoAnnotation(tree, "egkw6pwe2g", SUPPORT_COLUMN);
		assertNoAnnotation(tree, "egkw6pwe2g", CONFLICT_COLUMN);
		
		assertAnnotation(tree, "x7474zcnij", SUPPORT_COLUMN, 2.0);
		assertNoAnnotation(tree, "x7474zcnij", CONFLICT_COLUMN);
		
		assertAnnotation(tree, "yt4x200u57", SUPPORT_COLUMN, 1.0);
		assertNoAnnotation(tree, "yt4x200u57", CONFLICT_COLUMN);
		
		assertNoAnnotation(tree, "051l0uv3ok", SUPPORT_COLUMN);
		assertNoAnnotation(tree, "051l0uv3ok", CONFLICT_COLUMN);
	}
	
	
	@Test
	public void test_polytomyIntoSolvedUnrooted() throws Exception {
		Tree tree = executeEdit("Solved.xtg", "Polytomy.xtg", false);
		
		assertNoAnnotation(tree, "egkw6pwe2g", SUPPORT_COLUMN);
		assertNoAnnotation(tree, "egkw6pwe2g", CONFLICT_COLUMN);
		
		assertNoAnnotation(tree, "x7474zcnij", SUPPORT_COLUMN);  // The 2.0 does not separate more then one terminal on each side if the tree is considered unrooted.
		assertNoAnnotation(tree, "x7474zcnij", CONFLICT_COLUMN);
		
		assertAnnotation(tree, "yt4x200u57", SUPPORT_COLUMN, 1.0);
		assertNoAnnotation(tree, "yt4x200u57", CONFLICT_COLUMN);
		
		assertNoAnnotation(tree, "051l0uv3ok", SUPPORT_COLUMN);
		assertNoAnnotation(tree, "051l0uv3ok", CONFLICT_COLUMN);
	}
	
	
	@Test
	public void test_solvedIntoPolytomy() throws Exception {
		Tree tree = executeEdit("Polytomy.xtg", "Solved.xtg", true);
		
		assertNoAnnotation(tree, "egkw6pwe2g", SUPPORT_COLUMN);
		assertNoAnnotation(tree, "egkw6pwe2g", CONFLICT_COLUMN);
		
		assertNoAnnotation(tree, "mcvscb0eq5", SUPPORT_COLUMN);
		assertNoAnnotation(tree, "mcvscb0eq5", CONFLICT_COLUMN);
		
		assertAnnotation(tree, "yt4x200u57", SUPPORT_COLUMN, 2.0);
		assertNoAnnotation(tree, "yt4x200u57", CONFLICT_COLUMN);
	}


	@Test
	public void test_solvedIntoPolytomyUnrooted() throws Exception {
		Tree tree = executeEdit("Polytomy.xtg", "Solved.xtg", false);
		
		assertNoAnnotation(tree, "egkw6pwe2g", SUPPORT_COLUMN);
		assertNoAnnotation(tree, "egkw6pwe2g", CONFLICT_COLUMN);
		
		assertNoAnnotation(tree, "mcvscb0eq5", SUPPORT_COLUMN);
		assertNoAnnotation(tree, "mcvscb0eq5", CONFLICT_COLUMN);
		
		assertAnnotation(tree, "yt4x200u57", SUPPORT_COLUMN, 2.0);
		assertNoAnnotation(tree, "yt4x200u57", CONFLICT_COLUMN);
	}


	@Test
	public void test_polytomyIntoSolvedAddTerminals() throws Exception {
		Tree tree = executeEdit("SolvedAddTerminals.xtg", "PolytomyAddTerminals.xtg", true);
		
		assertNoAnnotation(tree, "egkw6pwe2g", SUPPORT_COLUMN);
		assertNoAnnotation(tree, "egkw6pwe2g", CONFLICT_COLUMN);
		
		assertAnnotation(tree, "x7474zcnij", SUPPORT_COLUMN, 2.0);
		assertNoAnnotation(tree, "x7474zcnij", CONFLICT_COLUMN);
		
		assertAnnotation(tree, "yt4x200u57", SUPPORT_COLUMN, 1.0);
		assertNoAnnotation(tree, "yt4x200u57", CONFLICT_COLUMN);
		
		assertNoAnnotation(tree, "051l0uv3ok", SUPPORT_COLUMN);
		assertNoAnnotation(tree, "051l0uv3ok", CONFLICT_COLUMN);
		
		assertNoAnnotation(tree, "71ralxvypp", SUPPORT_COLUMN);
		assertNoAnnotation(tree, "71ralxvypp", CONFLICT_COLUMN);
	}
	
	
	@Test
	public void test_polytomyIntoSolvedAddTerminalsUnrooted() throws Exception {
		Tree tree = executeEdit("SolvedAddTerminals.xtg", "PolytomyAddTerminals.xtg", false);
		
		assertNoAnnotation(tree, "egkw6pwe2g", SUPPORT_COLUMN);
		assertNoAnnotation(tree, "egkw6pwe2g", CONFLICT_COLUMN);
		
		assertNoAnnotation(tree, "x7474zcnij", SUPPORT_COLUMN);  // The 2.0 does not separate more then one terminal on each side if the tree is considered unrooted.
		assertNoAnnotation(tree, "x7474zcnij", CONFLICT_COLUMN);
		
		assertAnnotation(tree, "yt4x200u57", SUPPORT_COLUMN, 1.0);
		assertNoAnnotation(tree, "yt4x200u57", CONFLICT_COLUMN);
		
		assertNoAnnotation(tree, "051l0uv3ok", SUPPORT_COLUMN);
		assertNoAnnotation(tree, "051l0uv3ok", CONFLICT_COLUMN);
		
		assertNoAnnotation(tree, "71ralxvypp", SUPPORT_COLUMN);
		assertNoAnnotation(tree, "71ralxvypp", CONFLICT_COLUMN);
	}
	
	
	@Test
	public void test_solvedIntoPolytomyAddTerminals() throws Exception {
		Tree tree = executeEdit("PolytomyAddTerminals.xtg", "SolvedAddTerminals.xtg", true);
		
		assertNoAnnotation(tree, "egkw6pwe2g", SUPPORT_COLUMN);
		assertNoAnnotation(tree, "egkw6pwe2g", CONFLICT_COLUMN);
		
		assertNoAnnotation(tree, "mcvscb0eq5", SUPPORT_COLUMN);
		assertNoAnnotation(tree, "mcvscb0eq5", CONFLICT_COLUMN);
		
		assertAnnotation(tree, "yt4x200u57", SUPPORT_COLUMN, 2.0);
		assertNoAnnotation(tree, "yt4x200u57", CONFLICT_COLUMN);
		
		assertNoAnnotation(tree, "cz8kravy5o", SUPPORT_COLUMN);
		assertNoAnnotation(tree, "cz8kravy5o", CONFLICT_COLUMN);
	}
	
	
	@Test
	public void test_solvedIntoPolytomyAddTerminalsUnrooted() throws Exception {
		Tree tree = executeEdit("PolytomyAddTerminals.xtg", "SolvedAddTerminals.xtg", false);
		
		assertNoAnnotation(tree, "egkw6pwe2g", SUPPORT_COLUMN);
		assertNoAnnotation(tree, "egkw6pwe2g", CONFLICT_COLUMN);
		
		assertNoAnnotation(tree, "mcvscb0eq5", SUPPORT_COLUMN);
		assertNoAnnotation(tree, "mcvscb0eq5", CONFLICT_COLUMN);
		
		assertAnnotation(tree, "yt4x200u57", SUPPORT_COLUMN, 2.0);
		assertNoAnnotation(tree, "yt4x200u57", CONFLICT_COLUMN);
		
		assertNoAnnotation(tree, "cz8kravy5o", SUPPORT_COLUMN);
		assertNoAnnotation(tree, "cz8kravy5o", CONFLICT_COLUMN);
	}

	
	@Test
	public void test_polytomyIntoSolvedAddTerminalsAtRootUnrooted1() throws Exception {
		Tree tree = executeEdit("SolvedAddTerminals.xtg", "PolytomyAddTerminalsAtRoot.xtg", false);
		
		assertNoAnnotation(tree, "egkw6pwe2g", SUPPORT_COLUMN);
		assertNoAnnotation(tree, "egkw6pwe2g", CONFLICT_COLUMN);
		
		assertNoAnnotation(tree, "x7474zcnij", SUPPORT_COLUMN);  // The 2.0 does not separate more then one terminal on each side if the tree is considered unrooted.
		assertNoAnnotation(tree, "x7474zcnij", CONFLICT_COLUMN);
		
		assertAnnotation(tree, "yt4x200u57", SUPPORT_COLUMN, 1.0);
		assertNoAnnotation(tree, "yt4x200u57", CONFLICT_COLUMN);
		
		assertNoAnnotation(tree, "051l0uv3ok", SUPPORT_COLUMN);
		assertNoAnnotation(tree, "051l0uv3ok", CONFLICT_COLUMN);
		
		assertNoAnnotation(tree, "71ralxvypp", SUPPORT_COLUMN);
		assertNoAnnotation(tree, "71ralxvypp", CONFLICT_COLUMN);
	}

	
	@Test
	public void test_polytomyIntoSolvedAddTerminalsAtRootUnrooted2() throws Exception {
		Tree tree = executeEdit("SolvedAddTerminalsAtRoot.xtg", "PolytomyAddTerminals.xtg", false);
		
		assertNoAnnotation(tree, "egkw6pwe2g", SUPPORT_COLUMN);
		assertNoAnnotation(tree, "egkw6pwe2g", CONFLICT_COLUMN);
		
		assertNoAnnotation(tree, "x7474zcnij", SUPPORT_COLUMN);  // The 2.0 does not separate more then one terminal on each side if the tree is considered unrooted.
		assertNoAnnotation(tree, "x7474zcnij", CONFLICT_COLUMN);
		
		assertAnnotation(tree, "yt4x200u57", SUPPORT_COLUMN, 1.0);
		assertNoAnnotation(tree, "yt4x200u57", CONFLICT_COLUMN);
		
		assertNoAnnotation(tree, "051l0uv3ok", SUPPORT_COLUMN);
		assertNoAnnotation(tree, "051l0uv3ok", CONFLICT_COLUMN);
		
		assertNoAnnotation(tree, "71ralxvypp", SUPPORT_COLUMN);
		assertNoAnnotation(tree, "71ralxvypp", CONFLICT_COLUMN);
	}

	
	@Test
	public void test_polytomyIntoSolvedAddTerminalsAtRootUnrooted3() throws Exception {
		Tree tree = executeEdit("SolvedAddTerminalsAtRoot.xtg", "PolytomyAddTerminalsAtRoot.xtg", false);
		
		assertNoAnnotation(tree, "egkw6pwe2g", SUPPORT_COLUMN);
		assertNoAnnotation(tree, "egkw6pwe2g", CONFLICT_COLUMN);
		
		assertNoAnnotation(tree, "x7474zcnij", SUPPORT_COLUMN);  // The 2.0 does not separate more then one terminal on each side if the tree is considered unrooted.
		assertNoAnnotation(tree, "x7474zcnij", CONFLICT_COLUMN);
		
		assertAnnotation(tree, "yt4x200u57", SUPPORT_COLUMN, 1.0);
		assertNoAnnotation(tree, "yt4x200u57", CONFLICT_COLUMN);
		
		assertNoAnnotation(tree, "051l0uv3ok", SUPPORT_COLUMN);
		assertNoAnnotation(tree, "051l0uv3ok", CONFLICT_COLUMN);
		
		assertNoAnnotation(tree, "71ralxvypp", SUPPORT_COLUMN);
		assertNoAnnotation(tree, "71ralxvypp", CONFLICT_COLUMN);
	}

	
	@Test
	public void test_twoGroupsFromPaintStartAbove() throws Exception {
		Tree tree = executeEdit("Solved.xtg", "SolvedTwoGroupsFromPaintStart_supportAbove.xtg", false);
		
		assertNoAnnotation(tree, "egkw6pwe2g", SUPPORT_COLUMN);
		assertNoAnnotation(tree, "egkw6pwe2g", CONFLICT_COLUMN);
		
		assertNoAnnotation(tree, "x7474zcnij", SUPPORT_COLUMN);
		assertNoAnnotation(tree, "x7474zcnij", CONFLICT_COLUMN);
		
		assertAnnotation(tree, "yt4x200u57", SUPPORT_COLUMN, 2.0);
		assertNoAnnotation(tree, "yt4x200u57", CONFLICT_COLUMN);
		
		assertAnnotation(tree, "051l0uv3ok", SUPPORT_COLUMN, 1.0);
		assertNoAnnotation(tree, "051l0uv3ok", CONFLICT_COLUMN);
	}

	
	@Test
	public void test_twoGroupsFromPaintStartAbove_rooted() throws Exception {
		Tree tree = executeEdit("Solved.xtg", "SolvedTwoGroupsFromPaintStart_supportAbove.xtg", true);
		
		assertNoAnnotation(tree, "egkw6pwe2g", SUPPORT_COLUMN);
		assertNoAnnotation(tree, "egkw6pwe2g", CONFLICT_COLUMN);
		
		assertNoAnnotation(tree, "x7474zcnij", SUPPORT_COLUMN);
		assertAnnotation(tree, "x7474zcnij", CONFLICT_COLUMN, 2.0);
		
		assertAnnotation(tree, "yt4x200u57", SUPPORT_COLUMN, 2.0);
		assertNoAnnotation(tree, "yt4x200u57", CONFLICT_COLUMN);
		
		assertAnnotation(tree, "051l0uv3ok", SUPPORT_COLUMN, 1.0);
		assertNoAnnotation(tree, "051l0uv3ok", CONFLICT_COLUMN);
	}

	
	@Test
	public void test_twoGroupsFromPaintStartBelow() throws Exception {
		Tree tree = executeEdit("Solved.xtg", "SolvedTwoGroupsFromPaintStart_supportBelow.xtg", false);
		
		assertNoAnnotation(tree, "egkw6pwe2g", SUPPORT_COLUMN);
		assertNoAnnotation(tree, "egkw6pwe2g", CONFLICT_COLUMN);
		
		assertNoAnnotation(tree, "x7474zcnij", SUPPORT_COLUMN);
		assertNoAnnotation(tree, "x7474zcnij", CONFLICT_COLUMN);
		
		assertAnnotation(tree, "yt4x200u57", SUPPORT_COLUMN, 2.0);
		assertNoAnnotation(tree, "yt4x200u57", CONFLICT_COLUMN);
		
		assertAnnotation(tree, "051l0uv3ok", SUPPORT_COLUMN, 1.0);
		assertNoAnnotation(tree, "051l0uv3ok", CONFLICT_COLUMN);
	}

	
	@Test
	public void test_twoGroupsFromPaintStartBelow_rooted() throws Exception {
		Tree tree = executeEdit("Solved.xtg", "SolvedTwoGroupsFromPaintStart_supportBelow.xtg", true);
		
		assertNoAnnotation(tree, "egkw6pwe2g", SUPPORT_COLUMN);
		assertNoAnnotation(tree, "egkw6pwe2g", CONFLICT_COLUMN);
		
		assertNoAnnotation(tree, "x7474zcnij", SUPPORT_COLUMN);
		assertAnnotation(tree, "x7474zcnij", CONFLICT_COLUMN, 2.0);
		
		assertNoAnnotation(tree, "yt4x200u57", SUPPORT_COLUMN);
		assertNoAnnotation(tree, "yt4x200u57", CONFLICT_COLUMN);
		
		assertAnnotation(tree, "051l0uv3ok", SUPPORT_COLUMN, 1.0);
		assertNoAnnotation(tree, "051l0uv3ok", CONFLICT_COLUMN);
	}

	
	@Test
	public void test_twoGroupsFromPaintStartAbove_addTerminalAbove() throws Exception {
		Tree tree = executeEdit("Solved.xtg", "SolvedTwoGroupsFromPaintStart_supportBelow_addTerminalAbove.xtg", false);
		
		assertNoAnnotation(tree, "egkw6pwe2g", SUPPORT_COLUMN);
		assertNoAnnotation(tree, "egkw6pwe2g", CONFLICT_COLUMN);
		
		assertNoAnnotation(tree, "x7474zcnij", SUPPORT_COLUMN);
		assertNoAnnotation(tree, "x7474zcnij", CONFLICT_COLUMN);
		
		assertAnnotation(tree, "yt4x200u57", SUPPORT_COLUMN, 2.0);
		assertNoAnnotation(tree, "yt4x200u57", CONFLICT_COLUMN);
		
		assertAnnotation(tree, "051l0uv3ok", SUPPORT_COLUMN, 1.0);
		assertNoAnnotation(tree, "051l0uv3ok", CONFLICT_COLUMN);
	}

	
	@Test
	public void test_twoGroupsFromPaintStartAbove_addTerminalBelow() throws Exception {
		Tree tree = executeEdit("Solved.xtg", "SolvedTwoGroupsFromPaintStart_supportBelow_addTerminalBelow.xtg", false);
		
		assertNoAnnotation(tree, "egkw6pwe2g", SUPPORT_COLUMN);
		assertNoAnnotation(tree, "egkw6pwe2g", CONFLICT_COLUMN);
		
		assertNoAnnotation(tree, "x7474zcnij", SUPPORT_COLUMN);
		assertNoAnnotation(tree, "x7474zcnij", CONFLICT_COLUMN);
		
		assertAnnotation(tree, "yt4x200u57", SUPPORT_COLUMN, 2.0);
		assertNoAnnotation(tree, "yt4x200u57", CONFLICT_COLUMN);
		
		assertAnnotation(tree, "051l0uv3ok", SUPPORT_COLUMN, 1.0);
		assertNoAnnotation(tree, "051l0uv3ok", CONFLICT_COLUMN);
	}
}
