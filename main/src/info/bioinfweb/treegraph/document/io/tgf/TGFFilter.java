/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2015-2016  Ben Stöver, Sarah Wiechers, Kai Müller
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
package info.bioinfweb.treegraph.document.io.tgf;


import info.bioinfweb.treegraph.document.io.AbstractFilter;
import info.bioinfweb.treegraph.document.io.ReadWriteFormat;
import info.bioinfweb.treegraph.document.io.TreeFilter;



public class TGFFilter extends AbstractFilter implements TreeFilter {
  @Override
	public ReadWriteFormat getFormat() {
  	return ReadWriteFormat.TGF;
	}


	public static final String EXTENSION = ".tgf";
	
	
	public boolean validExtension(String name) {
		return name.toLowerCase().endsWith(EXTENSION);
	}
	
	
	@Override
	public String getDescription() {
		return "TreeGraph format from version 1.x (*.tgf)";
	}


	public String getDefaultExtension() {
		return EXTENSION;
	}
}