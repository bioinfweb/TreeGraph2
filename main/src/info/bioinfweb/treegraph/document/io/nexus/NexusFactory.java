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
package info.bioinfweb.treegraph.document.io.nexus;


import info.bioinfweb.treegraph.document.io.AbstractFilter;
import info.bioinfweb.treegraph.document.io.DocumentReader;
import info.bioinfweb.treegraph.document.io.DocumentWriter;
import info.bioinfweb.treegraph.document.io.SingleReadWriteFactory;



public class NexusFactory implements SingleReadWriteFactory {
	public AbstractFilter getFilter() {
		return new NexusFilter();
	}

	
	public DocumentReader getReader() {
		return new NexusReader();
	}
	

	public DocumentWriter getWriter() {
		return new NexusWriter();
	}

	
	public boolean hasReader() {
		return true;
	}

	
	public boolean hasWriter() {
		return true;
	}
}