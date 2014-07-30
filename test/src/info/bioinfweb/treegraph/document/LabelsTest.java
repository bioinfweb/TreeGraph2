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


import java.lang.reflect.Method;
import java.util.List;

import info.bioinfweb.commons.testing.TestTools;

import org.junit.* ;

import static org.junit.Assert.* ;


public class LabelsTest {
	@Test
	public void test_getLastLinePos(){
	 	Tree tree = new Tree();
	 	tree.setPaintStart(Node.getInstanceWithBranch());
	  	
		Labels labels = tree.getPaintStart().getAfferentBranch().getLabels();
		TextLabel t1 = new TextLabel(null);
		TextLabel t2 = new TextLabel(null);
		t1.getFormats().setAbove(true);
		t1.getFormats().setLineNumber(0);
		t1.getFormats().setLinePosition(18);
		t1.setID("1");
		t2.getFormats().setAbove(true);
		t2.getFormats().setLineNumber(0);
		t2.getFormats().setLinePosition(9);
		t2.setID("2");
		labels.add(t1);
		labels.add(t2);
		
		//System.out.println(t1.getFormats().getLinePosition());
		//System.out.println(t2.getFormats().getLinePosition());
		assertEquals(18 + Labels.DEFAULT_LINE_INDEX_INCREMENT, labels.getLastLinePos(true, 0), 0.000001);
	}
	
	
	@Test	
	public void test_calculateNewLinePosition(){
  	Tree tree = new Tree();
  	tree.setPaintStart(Node.getInstanceWithBranch());
  	
		Labels labels = tree.getPaintStart().getAfferentBranch().getLabels();
		TextLabel t1 = new TextLabel(null);
		TextLabel t2 = new TextLabel(null);
		t1.getFormats().setAbove(true);
		t1.getFormats().setLineNumber(0);
		t1.getFormats().setLinePosition(18);
		t1.setID("1");
		t2.getFormats().setAbove(true);
		t2.getFormats().setLineNumber(0);
		t2.getFormats().setLinePosition(9);
		t2.setID("2");
		TextLabel t3 = new TextLabel(null);
		t3.getFormats().setAbove(true);
		t3.getFormats().setLineNumber(0);
		t3.getFormats().setLinePosition(9);
		t3.setID("3");
		labels.add(t1);
		labels.add(t2);
		//labels.add(t3);
		
		Method calculateNewLinePostition = TestTools.getPrivateMethod(Labels.class, "calculateNewLinePosition", Label.class, LabelLine.class);
		Method getLines = TestTools.getPrivateMethod(Labels.class, "getLines", boolean.class);
		
		try {
			calculateNewLinePostition.invoke(labels, t3, ((List<LabelLine>) getLines.invoke(labels, true)).get(0));
			assertEquals((t1.getFormats().getLinePosition() + t2.getFormats().getLinePosition()) / 2, 
					t3.getFormats().getLinePosition(), 0.000001);
		}
		catch (Exception e) {
			fail();
		}		
	}
	

}
