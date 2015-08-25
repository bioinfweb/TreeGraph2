package info.bioinfweb.treegraph.document.undo.file.ancestralstate;


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
			assertEquals(1, nodes.get("Root").getCharacterMap().get("0").get("0") + nodes.get("Root").getCharacterMap().get("0").get("1"), 0.000000001);	
			assertEquals(1, nodes.get("Root").getCharacterMap().get("1").get("0") + nodes.get("Root").getCharacterMap().get("1").get("1"), 0.000000001);
			assertEquals(1, nodes.get("Pan").getCharacterMap().get("0").get("0") + nodes.get("Pan").getCharacterMap().get("0").get("1"), 0.000000001);
			assertEquals(1, nodes.get("Pan").getCharacterMap().get("1").get("0") + nodes.get("Pan").getCharacterMap().get("1").get("1"), 0.000000001);
			assertEquals(1, nodes.get("HomoPan").getCharacterMap().get("0").get("0") + nodes.get("HomoPan").getCharacterMap().get("0").get("1"), 0.000000001);
			assertEquals(1, nodes.get("HomoPan").getCharacterMap().get("1").get("0") + nodes.get("HomoPan").getCharacterMap().get("1").get("1"), 0.000000001);
			assertEquals(1, nodes.get("GorillaHomoPan").getCharacterMap().get("0").get("0") + nodes.get("GorillaHomoPan").getCharacterMap().get("0").get("1"), 0.000000001);	
			assertEquals(1, nodes.get("GorillaHomoPan").getCharacterMap().get("1").get("0") + nodes.get("GorillaHomoPan").getCharacterMap().get("1").get("1"), 0.000000001);
			assertEquals(1, nodes.get("Pongo").getCharacterMap().get("0").get("0") + nodes.get("Pongo").getCharacterMap().get("0").get("1"), 0.000000001);
			assertEquals(1, nodes.get("Pongo").getCharacterMap().get("1").get("0") + nodes.get("Pongo").getCharacterMap().get("1").get("1"), 0.000000001);
			assertEquals(1, nodes.get("GreatApes").getCharacterMap().get("0").get("0") + nodes.get("GreatApes").getCharacterMap().get("0").get("1"), 0.000000001);
			assertEquals(1, nodes.get("GreatApes").getCharacterMap().get("1").get("0") + nodes.get("GreatApes").getCharacterMap().get("1").get("1"), 0.000000001);
			
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
