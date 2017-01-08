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
package info.bioinfweb.treegraph.document.topologicalcalculation;


import org.junit.* ;

import static org.junit.Assert.* ;



public class LeafSetTest {
	private void showLeafField(LeafSet leafField) {
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
	public void test_setChildren(){
		LeafSet testLeafField = new LeafSet(35);
		testLeafField.setChild(23, true);
		assertEquals(true, testLeafField.isChild(23));

		testLeafField.setChild(23,false);
		assertEquals(false, testLeafField.isChild(23));
	}
	
	
	@Test
	public void test_complementTest(){
		LeafSet testLeafField = new LeafSet(37);
		for ( int i = 0; i < 37; i++){
			testLeafField.setChild(i, i%2 == 0); 
//				System.out.println("" +  (i%2 == 0)+ " " + testLeafField.isChild(i));
			//showLeafField(testLeafField);
		}
		
		LeafSet complementLeafField = testLeafField.complement();
		//System.out.println();
		for ( int j = 0; j < 37; j++){
			assertEquals("" + j, complementLeafField.isChild(j), !testLeafField.isChild(j));
		}
	}
	
	
	@Test
	public void test_containsAny() {
		LeafSet set = new LeafSet(41);
		set.setChild(0, true);
		set.setChild(1, true);
		set.setChild(2, true);
		set.setChild(3, true);
		set.setChild(4, true);
		set.setChild(5, true);
		set.setChild(8, true);
		set.setChild(22, true);
		set.setChild(23, true);
		set.setChild(24, true);
		set.setChild(39, true);
		set.setChild(40, true);
		
		LeafSet subset = new LeafSet(41);
		subset.setChild(1, true);
		subset.setChild(4, true);
		subset.setChild(8, true);
		subset.setChild(24, true);
		subset.setChild(40, true);
		
		assertTrue(set.containsAll(subset));
		
		subset.setChild(7, true);
		assertFalse(set.containsAll(subset));
	}
}
