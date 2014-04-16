/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2014  Ben Stöver, Kai Müller
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
package info.bioinfweb.treegraph.document;


import info.bioinfweb.treegraph.document.io.xtg.XTGReader;
import info.bioinfweb.treegraph.document.nodebranchdata.BranchLengthAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeNameAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.TextLabelAdapter;
import info.bioinfweb.commons.SystemUtils;

import java.io.File;
import java.text.DecimalFormat;

import org.junit.* ;


import static org.junit.Assert.* ;



public class TreeTest {
	private Document createDocument() {
		try {
			return new XTGReader().read(new File("data" + SystemUtils.FILE_SEPARATOR + "getFirstNodeByData.xtg"));
		}
		catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
			return null;  // unreachable code
		}
	}
	
	
  @Test
  public void test_getFirstNodeByData() {
  	Document document = createDocument();
  	Tree tree = document.getTree();
  	
  	assertEquals("c81wbd5x0g", tree.getFirstNodeByData(NodeNameAdapter.getSharedInstance(), "A").getUniqueName());
  	assertEquals("thfuyevs93", tree.getFirstNodeByData(NodeNameAdapter.getSharedInstance(), 2.43).getUniqueName());
  	
  	TextLabelAdapter labelAdapter = new TextLabelAdapter("Label", new DecimalFormat());
  	assertEquals("thfuyevs93", tree.getFirstNodeByData(labelAdapter, "Text").getUniqueName());
  	assertEquals("wxon0b0cj4", tree.getFirstNodeByData(labelAdapter, 24.5).getUniqueName());

  	assertNull(tree.getFirstNodeByData(NodeNameAdapter.getSharedInstance(), "NotInTree"));
  	assertNull(tree.getFirstNodeByData(NodeNameAdapter.getSharedInstance(), ""));
  	
  	assertNull(tree.getFirstNodeByData(BranchLengthAdapter.getSharedInstance(), 18.0));
  	assertNull(tree.getFirstNodeByData(BranchLengthAdapter.getSharedInstance(), 0.0));
  }
}
