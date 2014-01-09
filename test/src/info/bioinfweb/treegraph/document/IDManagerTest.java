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


import info.webinsel.util.junit.TestTools;

import java.lang.reflect.Method;
import java.util.Vector;

import org.junit.* ;


import static org.junit.Assert.* ;



/**
 * Tests {@link IDManager}.
 * 
 * @author BenStoever
 */
public class IDManagerTest {
	public static final String TEXT_LABEL_ID = "Text";
	public static final String ICON_LABEL_ID = "Icon";
	public static final String PIE_CHART_LABEL_ID = "PieChart";
	
	
	private Tree createBasicTree(boolean secondLevel, boolean text, boolean icon, boolean pieChart) {
  	Tree result = new Tree();
  	result.setPaintStart(Node.getInstanceWithBranch());
  	result.getPaintStart().getChildren().add(Node.getInstanceWithBranch());
  	result.getPaintStart().getChildren().add(Node.getInstanceWithBranch());

  	Labels labels = result.getPaintStart().getAfferentBranch().getLabels();
  	if (secondLevel) {
  		labels = result.getPaintStart().getChildren().get(0).getAfferentBranch().getLabels();
  	}
  	
  	if (text) {
  		TextLabel label = new TextLabel(labels);
    	label.setID(TEXT_LABEL_ID);
    	labels.add(label);
  	}
  	
    if (icon) {
    	IconLabel label = new IconLabel(labels);
    	label.setID(ICON_LABEL_ID);
    	labels.add(label);
    }
  	
  	if (pieChart) {
  		PieChartLabel label = new PieChartLabel(labels);
    	label.setID(PIE_CHART_LABEL_ID);
    	labels.add(label);
  	}
  	
  	return result;
  }
  
  
  @Test
  public void test_getFirstLabel_TextLabel_firstLevel() {
   	Tree tree = createBasicTree(false, true, true, true);
  	
  	assertEquals(TEXT_LABEL_ID, IDManager.getFirstLabel(tree.getPaintStart(), Label.class, TEXT_LABEL_ID).getID());
  	assertEquals(TEXT_LABEL_ID, IDManager.getFirstLabel(tree.getPaintStart(), TextLabel.class, TEXT_LABEL_ID).getID());
  	assertEquals(null, IDManager.getFirstLabel(tree.getPaintStart(), GraphicalLabel.class, TEXT_LABEL_ID));
  	assertEquals(null, IDManager.getFirstLabel(tree.getPaintStart(), IconLabel.class, TEXT_LABEL_ID));
  	assertEquals(null, IDManager.getFirstLabel(tree.getPaintStart(), PieChartLabel.class, TEXT_LABEL_ID));
  	assertEquals(null, IDManager.getFirstLabel(tree.getPaintStart(), Label.class, "otherID"));
  }
  
  
  @Test
  public void test_getFirstLabel_TextLabel_secondLevel() {
   	Tree tree = createBasicTree(true, true, true, true);
  	
  	assertEquals(TEXT_LABEL_ID, IDManager.getFirstLabel(tree.getPaintStart(), Label.class, TEXT_LABEL_ID).getID());
  	assertEquals(TEXT_LABEL_ID, IDManager.getFirstLabel(tree.getPaintStart(), TextLabel.class, TEXT_LABEL_ID).getID());
  	assertEquals(null, IDManager.getFirstLabel(tree.getPaintStart(), GraphicalLabel.class, TEXT_LABEL_ID));
  	assertEquals(null, IDManager.getFirstLabel(tree.getPaintStart(), IconLabel.class, TEXT_LABEL_ID));
  	assertEquals(null, IDManager.getFirstLabel(tree.getPaintStart(), PieChartLabel.class, TEXT_LABEL_ID));
  	assertEquals(null, IDManager.getFirstLabel(tree.getPaintStart(), Label.class, "otherID"));
  }
  
  
  @Test
  public void test_getFirstLabel_IconLabel() {
   	Tree tree = createBasicTree(true, true, true, true);
  	
  	assertEquals(ICON_LABEL_ID, IDManager.getFirstLabel(tree.getPaintStart(), Label.class, ICON_LABEL_ID).getID());
  	assertEquals(null, IDManager.getFirstLabel(tree.getPaintStart(), TextLabel.class, ICON_LABEL_ID));
  	assertEquals(ICON_LABEL_ID, IDManager.getFirstLabel(tree.getPaintStart(), GraphicalLabel.class, ICON_LABEL_ID).getID());
  	assertEquals(ICON_LABEL_ID, IDManager.getFirstLabel(tree.getPaintStart(), IconLabel.class, ICON_LABEL_ID).getID());
  	assertEquals(null, IDManager.getFirstLabel(tree.getPaintStart(), PieChartLabel.class, ICON_LABEL_ID));
  	assertEquals(null, IDManager.getFirstLabel(tree.getPaintStart(), Label.class, "otherID"));
  }
  
  
  @Test
  public void test_getFirstLabel_PieChartLabel() {
   	Tree tree = createBasicTree(true, true, true, true);
  	
  	assertEquals(PIE_CHART_LABEL_ID, IDManager.getFirstLabel(tree.getPaintStart(), Label.class, PIE_CHART_LABEL_ID).getID());
  	assertEquals(null, IDManager.getFirstLabel(tree.getPaintStart(), TextLabel.class, PIE_CHART_LABEL_ID));
  	assertEquals(PIE_CHART_LABEL_ID, IDManager.getFirstLabel(tree.getPaintStart(), GraphicalLabel.class, PIE_CHART_LABEL_ID).getID());
  	assertEquals(null, IDManager.getFirstLabel(tree.getPaintStart(), IconLabel.class, PIE_CHART_LABEL_ID));
  	assertEquals(PIE_CHART_LABEL_ID, IDManager.getFirstLabel(tree.getPaintStart(), PieChartLabel.class, PIE_CHART_LABEL_ID).getID());
  	assertEquals(null, IDManager.getFirstLabel(tree.getPaintStart(), Label.class, "otherID"));
  }
  
  
  @Test
  public void test_searchLabelIDsInLabelBlock_absent() {
  	Method method = TestTools.getPrivateMethod(IDManager.class, "searchLabelIDsInLabelBlock", Labels.class, boolean.class, Class.class, Vector.class);
  	
  	Tree tree = createBasicTree(true, false, false, true);
  	Vector<String> list = new Vector<String>();
  	
  	try {
  		method.invoke(null, tree.getPaintStart().getAfferentBranch().getLabels(), true, TextLabel.class, list);
    	assertEquals(0, list.size());
  	}
  	catch (Exception e) {
  		e.printStackTrace();
  		fail();
  	}
  }
  
  
  @Test
  public void test_searchLabelIDsInLabelBlock_present() {
  	Method method = TestTools.getPrivateMethod(IDManager.class, "searchLabelIDsInLabelBlock", Labels.class, boolean.class,  Class.class, Vector.class);
  	
  	Tree tree = createBasicTree(false, true, false, true);
  	Vector<String> list = new Vector<String>();
  	
  	try {
  		method.invoke(null, tree.getPaintStart().getAfferentBranch().getLabels(), true, TextLabel.class, list);
    	assertEquals(1, list.size());
  	}
  	catch (Exception e) {
  		e.printStackTrace();
  		fail();
  	}
  }
  
  
  @Test
  public void test_getLabelIDVector() {
  	Tree tree = createBasicTree(false, false, false, true);
  	assertEquals(0, IDManager.getLabelIDVectorFromSubtree(tree.getPaintStart(), TextLabel.class).size());
  }
}