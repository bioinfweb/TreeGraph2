/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2013  Ben Stöver, Kai Müller
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
package info.bioinfweb.treegraph.document.undo.file.addsupportvalues;


import org.junit.* ;

import static org.junit.Assert.* ;



public class LeafFieldTest {	
	private void showLeafField(LeafField leafField) {
		for (int i = 0; i < leafField.size(); i++) {
			if (leafField.isChild(i)) {
				System.out.print("1");
			}
			else {
				System.out.print("0");
			}
		}
		System.out.println();
	}

	
	@Test
	public void setChildrenTest(){
		LeafField testLeafField = new LeafField(35);
		testLeafField.setChild(23, true);
		assertEquals(true, testLeafField.isChild(23));

		testLeafField.setChild(23,false);
		assertEquals(false, testLeafField.isChild(23));
	}
	
	
	@Test
	public void complementTest(){
		LeafField testLeafField = new LeafField(37);
		for ( int i = 0; i < 37; i++){
			testLeafField.setChild(i, i%2 == 0); 
//				System.out.println("" +  (i%2 == 0)+ " " + testLeafField.isChild(i));
			showLeafField(testLeafField);
		}
		
		LeafField complementLeafField = testLeafField.complement();
		System.out.println();
		for ( int j = 0; j < 37; j++){
			assertEquals("" + j, complementLeafField.isChild(j), !testLeafField.isChild(j));
		}
	}
}
