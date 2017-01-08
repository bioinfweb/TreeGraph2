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
package info.bioinfweb.treegraph.document.io.nexus;


import java.io.File;
import java.io.FileReader;

import info.bioinfweb.treegraph.document.io.AbstractFilter;
import info.bioinfweb.treegraph.document.io.ReadWriteFormat;
import info.bioinfweb.treegraph.document.io.TreeFilter;



/**
 * Filter for Nexus files.
 * 
 * @author Ben St&ouml;ver
 *
 */
public class NexusFilter extends AbstractFilter implements TreeFilter {
  @Override
	public ReadWriteFormat getFormat() {
  	return ReadWriteFormat.NEXUS;
	}


	public static final String[] EXTENSIONS 
      = {".tre", ".tree", ".trees", ".con", ".nex", ".nexus"};

  
	public boolean validExtension(String name) {
		name = name.toLowerCase();
		for (int i = 0; i < EXTENSIONS.length; i++) {
			if (name.endsWith(EXTENSIONS[i])) {
				return true;
			}
		}
		return false;
	}


	/**
	 * Tests if the specified file has a valid extension and starts with <code>#NEXUS</code>.
	 * @param file - the file to be tested
	 */
	@Override
	public boolean accept(File f) {
		boolean result = super.accept(f);
		
		if (result && f.isFile()) {
			try {
				FileReader reader = new FileReader(f);  // Should not be buffered for performance reasons.
				try {
					char[] firstChars = new char[NexusParser.FIRST_LINE.length()];
					int charsRead = reader.read(firstChars, 0, NexusParser.FIRST_LINE.length());
					if (charsRead == NexusParser.FIRST_LINE.length()) {
						for (int i = 0; i < firstChars.length; i++) {
							if (Character.toLowerCase(firstChars[i]) != 
								  Character.toLowerCase(NexusParser.FIRST_LINE.charAt(i))) {
								
								result = false;
								break;
							}
						}
					}
					else {
						result = false;
					}
				}
				finally {
					reader.close();
				}
			}
			catch (Exception e) {
				return false;
			}
		}
		return result;
	}


	@Override
	public String getDescription() {
		return "Nexus files (*.tre*; *.con; *.nex*)";
	}


	public String getDefaultExtension() {
		return ".tre";
	}
}