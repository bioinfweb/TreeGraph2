package info.bioinfweb.treegraph.document;


import info.bioinfweb.treegraph.document.undo.file.ancestralstate.AncestralStateImportParameters;

import org.junit.* ;

import static org.junit.Assert.* ;



public class TextElementDataTest {
	@Test
	public void test_equals_createEditedValue() {
		AncestralStateImportParameters parameters = new AncestralStateImportParameters();
		parameters.setDistinguishSpaceUnderscore(false);
		TextElementData data1 = parameters.createEditedValue("Pan troglodytes");
		TextElementData data2 = parameters.createEditedValue("Pan_troglodytes");
		assertTrue(data1.equals(data2));	
	}
}
