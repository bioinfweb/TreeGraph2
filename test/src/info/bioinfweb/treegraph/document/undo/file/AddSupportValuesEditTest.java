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
package info.bioinfweb.treegraph.document.undo.file;


import info.bioinfweb.commons.SystemUtils;
import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.io.ReadWriteFactory;
import info.bioinfweb.treegraph.document.io.ReadWriteFormat;

import java.io.File;

import org.junit.Test;



public class AddSupportValuesEditTest {
	//TODO Testfälle: Source in Target, Target in Source und beides zum jeweils unterschiedlichen zusätlichen und/oder fehlenden Taxa
	
	@Test
	public void test_polytomyMatching() throws Exception {
	  Document target = ReadWriteFactory.getInstance().getReader(ReadWriteFormat.XTG).read(
	  		new File("data" + SystemUtils.FILE_SEPARATOR + "addSupport" + SystemUtils.FILE_SEPARATOR + "Tree.xtg"));		
		
		AddSupportValuesParameters parameters = new AddSupportValuesParameters();
		parameters.setIdPrefix("id");
		//parameters.setSourceDocument(sourceDocument);
		
		//AddSupportValuesEdit edit = new AddSupportValuesEdit(targetDocument, parameters)
	}
}
