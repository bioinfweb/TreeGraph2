package info.bioinfweb.treegraph.document;


import java.lang.reflect.Method;
import java.util.List;

import info.bioinfweb.treegraph.document.format.ElementFormats;
import info.bioinfweb.treegraph.document.format.LabelFormats;
import info.webinsel.util.test.PrivateTester;

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
		
		Method calculateNewLinePostition = PrivateTester.getPrivateMethod(Labels.class, "calculateNewLinePosition", Label.class, LabelLine.class);
		Method getLines = PrivateTester.getPrivateMethod(Labels.class, "getLines", boolean.class);
		
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
