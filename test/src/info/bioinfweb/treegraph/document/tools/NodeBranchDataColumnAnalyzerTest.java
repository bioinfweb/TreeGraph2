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
package info.bioinfweb.treegraph.document.tools;


import org.junit.* ;

import info.bioinfweb.commons.SystemUtils;
import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.io.ReadWriteFactory;
import info.bioinfweb.treegraph.document.io.ReadWriteFormat;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.TextLabelAdapter;
import info.bioinfweb.treegraph.document.tools.NodeBranchDataColumnAnalyzer.ColumnStatus;

import static org.junit.Assert.* ;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;



public class NodeBranchDataColumnAnalyzerTest {
	private Document document = loadDocument();
	
	
	private Document loadDocument() {
	  try {
			return ReadWriteFactory.getInstance().getReader(ReadWriteFormat.XTG).read(
					new File("data" + SystemUtils.FILE_SEPARATOR + "columnStatus" + SystemUtils.FILE_SEPARATOR + "ColumnTest.xtg"));
		} 
	  catch (Exception e) {
			e.printStackTrace();
	  	throw new InternalError(e);
		}		
	}
	
	
	private void assertColumnStatus(Document document, ColumnStatus status, String suffix) {
		assertEquals(status, NodeBranchDataColumnAnalyzer.analyzeColumnStatus(document.getTree(), new TextLabelAdapter(status.name() + suffix)));
	}
	
	
	@Test
	public void test_analyzeColumnStatus() throws Exception {
		assertColumnStatus(document, ColumnStatus.ALL_NUMERIC_INTERNAL_IN_RANGE, "");
		assertColumnStatus(document, ColumnStatus.ALL_NUMERIC_OR_PARSABLE_INTERNAL_IN_RANGE, "");
		assertColumnStatus(document, ColumnStatus.ALL_NUMERIC_INTERNAL, "");
		assertColumnStatus(document, ColumnStatus.ALL_NUMERIC_OR_PARSABLE_INTERNAL, "");
		assertColumnStatus(document, ColumnStatus.ALL_NUMERIC_OR_PARSABLE, "_numericOnLeaf");
		assertColumnStatus(document, ColumnStatus.ALL_NUMERIC_OR_PARSABLE, "_numericOnLeaf_parsable");
		assertColumnStatus(document, ColumnStatus.SOME_NUMERIC_OR_PARSABLE, "");
		assertColumnStatus(document, ColumnStatus.NO_NUMERIC_OR_PARSABLE, "");
	}
	

	private void assertAdapter(NodeBranchDataAdapter adapter, String id) {
		assertTrue(adapter instanceof TextLabelAdapter);
		assertEquals(id, ((TextLabelAdapter)adapter).getID());
	}
	
	
	@Test
	public void test_sortColumnListByStatus() throws Exception {
		List<NodeBranchDataAdapter> list = new ArrayList<NodeBranchDataAdapter>();
		list.add(new TextLabelAdapter("ALL_NUMERIC_OR_PARSABLE_numericOnLeaf"));
		list.add(new TextLabelAdapter("ALL_NUMERIC_INTERNAL_IN_RANGE"));
		list.add(new TextLabelAdapter("ALL_NUMERIC_OR_PARSABLE_INTERNAL"));
		list.add(new TextLabelAdapter("SOME_NUMERIC_OR_PARSABLE"));
		list.add(new TextLabelAdapter("ALL_NUMERIC_OR_PARSABLE_INTERNAL_IN_RANGE"));
		list.add(new TextLabelAdapter("NO_NUMERIC_OR_PARSABLE"));
		list.add(new TextLabelAdapter("ALL_NUMERIC_INTERNAL"));
		
		NodeBranchDataColumnAnalyzer.sortColumnListByStatus(document.getTree(), list);
		
		Iterator<NodeBranchDataAdapter> iterator = list.iterator();
		assertAdapter(iterator.next(), "ALL_NUMERIC_INTERNAL_IN_RANGE");
		assertAdapter(iterator.next(), "ALL_NUMERIC_OR_PARSABLE_INTERNAL_IN_RANGE");
		assertAdapter(iterator.next(), "ALL_NUMERIC_INTERNAL");
		assertAdapter(iterator.next(), "ALL_NUMERIC_OR_PARSABLE_INTERNAL");
		assertAdapter(iterator.next(), "ALL_NUMERIC_OR_PARSABLE_numericOnLeaf");
		assertAdapter(iterator.next(), "SOME_NUMERIC_OR_PARSABLE");
		assertAdapter(iterator.next(), "NO_NUMERIC_OR_PARSABLE");
	}
}
