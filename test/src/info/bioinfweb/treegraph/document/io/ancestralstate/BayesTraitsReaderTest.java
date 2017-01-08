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
package info.bioinfweb.treegraph.document.io.ancestralstate;


import info.bioinfweb.commons.SystemUtils;

import java.io.IOException;
import java.util.Map;

import org.junit.* ;

import static org.junit.Assert.* ;



public class BayesTraitsReaderTest {
	@Test
	public void test_read() {
		BayesTraitsReader bayesReader = new BayesTraitsReader();		
		try {
			Map<String, AncestralStateData> nodes = bayesReader.read("data" + SystemUtils.FILE_SEPARATOR + "ancestralState" + SystemUtils.FILE_SEPARATOR + "Primates.txt.log.txt");
			assertEquals(6, nodes.size());
			assertEquals("Root", nodes.get("Root").getName());
			assertEquals("Pan", nodes.get("Pan").getName());
			assertTrue(nodes.get("Root").getLeafNames().isEmpty());
			assertEquals(2, nodes.get("Pan").getLeafNames().size());
			assertEquals("Pan_paniscus", nodes.get("Pan").getLeafNames().get(0));
//			assertEquals(0.669772491, nodes.get("Root").getProbabilities().get("S(1) - P(0)"), 0.000001);
//			assertEquals(0.996520048, nodes.get("Pan").getProbabilities().get("S(0) - P(1)"), 0.000001);  //TODO Check with LibreOffice	
			assertEquals(1, nodes.get("Root").getProbability("0", "0") + nodes.get("Root").getProbability("0", "1"), 0.000000001);	
			assertEquals(1, nodes.get("Root").getProbability("1", "0") + nodes.get("Root").getProbability("1", "1"), 0.000000001);
			assertEquals(1, nodes.get("Pan").getProbability("0", "0") + nodes.get("Pan").getProbability("0", "1"), 0.000000001);
			assertEquals(1, nodes.get("Pan").getProbability("1", "0") + nodes.get("Pan").getProbability("1", "1"), 0.000000001);
			assertEquals(1, nodes.get("HomoPan").getProbability("0", "0") + nodes.get("HomoPan").getProbability("0", "1"), 0.000000001);
			assertEquals(1, nodes.get("HomoPan").getProbability("1", "0") + nodes.get("HomoPan").getProbability("1", "1"), 0.000000001);
			assertEquals(1, nodes.get("GorillaHomoPan").getProbability("0", "0") + nodes.get("GorillaHomoPan").getProbability("0", "1"), 0.000000001);	
			assertEquals(1, nodes.get("GorillaHomoPan").getProbability("1", "0") + nodes.get("GorillaHomoPan").getProbability("1", "1"), 0.000000001);
			assertEquals(1, nodes.get("Pongo").getProbability("0", "0") + nodes.get("Pongo").getProbability("0", "1"), 0.000000001);
			assertEquals(1, nodes.get("Pongo").getProbability("1", "0") + nodes.get("Pongo").getProbability("1", "1"), 0.000000001);
			assertEquals(1, nodes.get("GreatApes").getProbability("0", "0") + nodes.get("GreatApes").getProbability("0", "1"), 0.000000001);
			assertEquals(1, nodes.get("GreatApes").getProbability("1", "0") + nodes.get("GreatApes").getProbability("1", "1"), 0.000000001);
			
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
