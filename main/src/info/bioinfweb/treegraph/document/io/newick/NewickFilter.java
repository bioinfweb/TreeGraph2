/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2015  Ben Stöver, Kai Müller
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
package info.bioinfweb.treegraph.document.io.newick;


import info.bioinfweb.treegraph.document.io.AbstractFilter;



public class NewickFilter extends AbstractFilter {
  public static final String[] EXTENSIONS 
      = {".tre", ".tree", ".trees", ".nwk", ".con"};
  

	public boolean validExtension(String name) {
		name = name.toLowerCase();
		for (int i = 0; i < EXTENSIONS.length; i++) {
			if (name.endsWith(EXTENSIONS[i])) {
				return true;
			}
		}
		return false;
	}

	
	@Override
	public String getDescription() {
		return "Newick file (*.tre*; *.con; *.nwk)";
	}


	public String getDefaultExtension() {
		return EXTENSIONS[0];
	}
}