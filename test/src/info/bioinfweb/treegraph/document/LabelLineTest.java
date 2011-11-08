package info.bioinfweb.treegraph.document;

import org.junit.* ;

import static org.junit.Assert.* ;

public class LabelLineTest {
	
	@Test	
	public void test_getIndexBeforeLinePos(){
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
		//assertEquals(13.5, actual)
		
		
	}
	
}
