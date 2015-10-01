/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2015  Ben Stöver, Sarah Wiechers, Kai Müller
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


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.io.xtg.XTGReader;
import info.bioinfweb.commons.SystemUtils;

import org.junit.* ;

import static org.junit.Assert.* ;



public class RerootByLeafSetEditTest {
	private Document createDocument() {
		try {
			return new XTGReader().read(new File("data" + SystemUtils.FILE_SEPARATOR + "RerootByLeaves.xtg"));
		}
		catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
			return null;  // unreachable code
		}
	}
	
	
  @Test
  public void test_redo_distantNodes() {
  	Document document = createDocument();
  	List<Node> leaves = new ArrayList<Node>(2);
  	leaves.add(document.getTree().getNodeByUniqueName("624i1qcsfd"));
  	leaves.add(document.getTree().getNodeByUniqueName("j99xcxa0in"));

  	RerootByLeafSetEdit edit = new RerootByLeafSetEdit(document, leaves);
  	document.executeEdit(edit);
  	
  	final String newRootID = "newRoot";
  	List<Node> rootChildren = document.getTree().getPaintStart().getChildren();
  	assertTrue(rootChildren.get(0).getAfferentBranch().getHiddenDataMap().containsKey(newRootID)
  			|| rootChildren.get(1).getAfferentBranch().getHiddenDataMap().containsKey(newRootID));  	
  	assertEquals(1, edit.getAlternativeRootingPoints().size());
  	assertTrue(edit.getAlternativeRootingPoints().iterator().next().getHiddenDataMap().containsKey(newRootID));
  }
	
	
  @Test
  public void test_redo_closeNodes() {
  	Document document = createDocument();
  	List<Node> leaves = new ArrayList<Node>(2);
  	leaves.add(document.getTree().getNodeByUniqueName("bh0q07axdt"));
  	leaves.add(document.getTree().getNodeByUniqueName("624i1qcsfd"));

  	RerootByLeafSetEdit edit = new RerootByLeafSetEdit(document, leaves);
  	document.executeEdit(edit);
  	
  	final String newRootName = "ztkxbqbhnl";
  	List<Node> rootChildren = document.getTree().getPaintStart().getChildren();
  	assertTrue(rootChildren.get(0).getUniqueName().equals(newRootName)
  			|| rootChildren.get(1).getUniqueName().equals(newRootName));  	
  	assertEquals(0, edit.getAlternativeRootingPoints().size());
  }
	
	
  @Test
  public void test_redo_singleNode() {
  	final String uniqueName = "bh0q07axdt";

  	Document document = createDocument();
  	List<Node> leaves = new ArrayList<Node>(2);
  	leaves.add(document.getTree().getNodeByUniqueName(uniqueName));

  	RerootByLeafSetEdit edit = new RerootByLeafSetEdit(document, leaves);
  	document.executeEdit(edit);
  	
  	List<Node> rootChildren = document.getTree().getPaintStart().getChildren();
  	assertTrue(rootChildren.get(0).getUniqueName().equals(uniqueName)
  			|| rootChildren.get(1).getUniqueName().equals(uniqueName));  	
  	assertEquals(0, edit.getAlternativeRootingPoints().size());
  }
}
