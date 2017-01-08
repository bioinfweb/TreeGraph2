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
package info.bioinfweb.treegraph.document.undo.edit;


import info.bioinfweb.commons.SystemUtils;
import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.NodeType;
import info.bioinfweb.treegraph.document.TextElementData;
import info.bioinfweb.treegraph.document.io.xtg.XTGReader;
import info.bioinfweb.treegraph.document.io.xtg.XTGWriter;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeNameAdapter;
import info.bioinfweb.treegraph.document.tools.TreeSerializer;
import info.bioinfweb.treegraph.document.undo.CompareTextElementDataParameters;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.* ;

import static org.junit.Assert.* ;



/**
 * Tests {@link SortLeavesEdit}.
 * 
 * @author Ben St&ouml;ver
 * @since 2.2.0
 */
public class SortLeavesEditTest {
	private Document createDocument() {
		try {
			return new XTGReader().read(new File("data" + SystemUtils.FILE_SEPARATOR + "sortLeaves" + SystemUtils.FILE_SEPARATOR + 
					"SortLeaves.xtg"));
		}
		catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
			return null;  // unreachable code
		}
	}
	
	
	/**
	 * Optional method to be used during the debug phase.
	 * 
	 * @param document
	 */
	private void writeTree(Document document) {
  	try {
  		new XTGWriter().write(document, new File("data" + SystemUtils.FILE_SEPARATOR + "sortLeaves" + 
  				SystemUtils.FILE_SEPARATOR + "SortLeavesOut.xtg"));
  	}
  	catch (Exception e) {
  		e.printStackTrace();
  	}
	}
	
	
  @Test
  public void testDistinctOrdering() {
  	List<TextElementData> order = new ArrayList<TextElementData>();
  	order.add(new TextElementData("L"));
  	order.add(new TextElementData("J"));
  	order.add(new TextElementData("K"));
  	order.add(new TextElementData("M"));
  	order.add(new TextElementData("C"));
  	order.add(new TextElementData("D"));
  	order.add(new TextElementData("B"));
  	order.add(new TextElementData("A"));
  	order.add(new TextElementData("G"));
  	order.add(new TextElementData("H"));
  	order.add(new TextElementData("I"));
  	order.add(new TextElementData("E"));
  	order.add(new TextElementData("F"));
  	
  	Document document = createDocument();
  	SortLeavesEdit edit = new SortLeavesEdit(document, document.getTree().getPaintStart(), order, 
  			NodeNameAdapter.getSharedInstance(), new CompareTextElementDataParameters());
  	document.executeEdit(edit);
  	
  	List<Node> leaves = TreeSerializer.getElementsInSubtreeAsList(document.getTree().getPaintStart(), NodeType.LEAVES, Node.class);
  	for (int i = 0; i < order.size(); i++) {
	    assertEquals(order.get(i).getText(), leaves.get(i).getData().getText());
    }
  }
	
	
  @Test
  public void testConflictingOrderingUnchanged() {
  	List<TextElementData> order = new ArrayList<TextElementData>();
  	order.add(new TextElementData("B"));
  	order.add(new TextElementData("A"));  // Switch between A and B is topologically impossible.
  	order.add(new TextElementData("C"));
  	order.add(new TextElementData("D"));
  	order.add(new TextElementData("E"));
  	order.add(new TextElementData("F"));
  	order.add(new TextElementData("G"));
  	order.add(new TextElementData("H"));
  	order.add(new TextElementData("I"));
  	order.add(new TextElementData("J"));
  	order.add(new TextElementData("K"));
  	order.add(new TextElementData("L"));
  	order.add(new TextElementData("M"));
  	
  	Document document = createDocument();
  	SortLeavesEdit edit = new SortLeavesEdit(document, document.getTree().getPaintStart(), order, 
  			NodeNameAdapter.getSharedInstance(), new CompareTextElementDataParameters());
  	document.executeEdit(edit);
  	
  	List<Node> leaves = TreeSerializer.getElementsInSubtreeAsList(document.getTree().getPaintStart(), NodeType.LEAVES, Node.class);
    assertEquals("A", leaves.get(0).getData().getText());
    assertEquals("B", leaves.get(1).getData().getText());
  	for (int i = 2; i < order.size(); i++) {
	    assertEquals(order.get(i).getText(), leaves.get(i).getData().getText());
    }
  }
	
	
  @Test
  public void testConflictingOrderingChanged() {
  	List<TextElementData> order = new ArrayList<TextElementData>();
  	order.add(new TextElementData("B"));
  	order.add(new TextElementData("C"));
  	order.add(new TextElementData("A"));  // Switch between A and B + C is topologically impossible.
  	order.add(new TextElementData("D"));
  	order.add(new TextElementData("E"));
  	order.add(new TextElementData("F"));
  	order.add(new TextElementData("G"));
  	order.add(new TextElementData("H"));
  	order.add(new TextElementData("I"));
  	order.add(new TextElementData("J"));
  	order.add(new TextElementData("K"));
  	order.add(new TextElementData("L"));
  	order.add(new TextElementData("M"));
  	
  	Document document = createDocument();
  	SortLeavesEdit edit = new SortLeavesEdit(document, document.getTree().getPaintStart(), order, 
  			NodeNameAdapter.getSharedInstance(), new CompareTextElementDataParameters());
  	document.executeEdit(edit);
  	
  	List<Node> leaves = TreeSerializer.getElementsInSubtreeAsList(document.getTree().getPaintStart(), NodeType.LEAVES, Node.class);
    assertEquals("B", leaves.get(0).getData().getText());
    assertEquals("C", leaves.get(1).getData().getText());
    assertEquals("D", leaves.get(2).getData().getText());
    assertEquals("A", leaves.get(3).getData().getText());
  	for (int i = 4; i < order.size(); i++) {
	    assertEquals(order.get(i).getText(), leaves.get(i).getData().getText());
    }
  }
}
