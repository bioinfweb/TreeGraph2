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
package info.bioinfweb.treegraph.document.io;


import info.bioinfweb.treegraph.document.io.newick.NewickFactory;
import info.bioinfweb.treegraph.document.io.nexus.NexusFactory;
import info.bioinfweb.treegraph.document.io.phyloxml.PhyloXMLFactory;
import info.bioinfweb.treegraph.document.io.tgf.TGFFactory;
import info.bioinfweb.treegraph.document.io.xtg.XTGFactory;
import info.bioinfweb.treegraph.document.io.xtg.XTGFilter;

import java.io.File;
import java.util.EnumMap;



public class ReadWriteFactory {
	private static ReadWriteFactory firstInstance = null;
	
	private EnumMap<ReadWriteFormat, SingleReadWriteFactory> factories = new EnumMap<ReadWriteFormat, SingleReadWriteFactory>(ReadWriteFormat.class);

	
	private ReadWriteFactory() {
  	fillList();
  }
  
  
  private void fillList() {
  	factories.put(ReadWriteFormat.XTG, new XTGFactory());
  	factories.put(ReadWriteFormat.NEWICK, new NewickFactory());   // Muss vor NEXUS eingef�gt werden (Why should this help? The map is not sorted.)
  	factories.put(ReadWriteFormat.NEXUS, new NexusFactory());
  	factories.put(ReadWriteFormat.TGF, new TGFFactory());  //TODO Warum steht das hier? Ist doch noch gar nicht fertig.
  	factories.put(ReadWriteFormat.PHYLO_XML, new PhyloXMLFactory());
  }
  
  
  public static ReadWriteFactory getInstance() {
  	if (firstInstance == null) {
  		firstInstance = new ReadWriteFactory();
  	}
  	return firstInstance;
  }
	
	
	/**
	 * Returns the file format guessed from the file extension. (If several formats with the same extension 
	 * exist the first in the list is returned.)
	 * 
	 * @param name
	 * @return
	 */
	public ReadWriteFormat formatByFileName(String name) {
  	for (ReadWriteFormat f: ReadWriteFormat.values()) {
			if (factories.get(f).getFilter().validExtension(name)) {
				return f;
			}
		}
  	return null;
  }
	
	
	public ReadWriteFormat formatByFile(File file) {
		ReadWriteFormat result = formatByFileName(file.getName());
		if (result != null) {
			if (result.equals(ReadWriteFormat.NEWICK) || result.equals(ReadWriteFormat.NEXUS)) {  // In diesem Fall ist aufgrund der Dateierweiterung nicht klar, ob es sich um NEWICK oder NEXUS handelt.
				if (getFilter(ReadWriteFormat.NEXUS).accept(file)) {  // Datei wird ge�ffnet und gepr�ft, ob "#NEXUS" vorhanden ist.
					result = ReadWriteFormat.NEXUS;
				}
				else {
					result = ReadWriteFormat.NEWICK;
				}
			}
			else if (result.equals(ReadWriteFormat.XTG) || result.equals(ReadWriteFormat.PHYLO_XML)) {
				if (file.getAbsolutePath().endsWith(XTGFilter.EXTENSION) || 
						getFilter(ReadWriteFormat.XTG).accept(file)) {
					result = ReadWriteFormat.XTG;
				}
				else if (getFilter(ReadWriteFormat.PHYLO_XML).accept(file)) {
					result = ReadWriteFormat.PHYLO_XML;
				}
				else {
					result = null;
				}
			}
		}
		return result;
	}
	
	
	public DocumentReader getReader(File file) {
  	ReadWriteFormat f = formatByFile(file);
  	if (f != null) {
  		return getReader(f);
  	}
  	else {
  		return null;
  	}
  }
  
  
  public boolean hasReader(ReadWriteFormat f) {
  	return factories.get(f).hasReader();
  }
	
	
  public boolean hasWriter(ReadWriteFormat f) {
  	return factories.get(f).hasWriter();
  }
	
	
	public DocumentReader getReader(ReadWriteFormat f) {
  	return factories.get(f).getReader();
  }
  
  
  public DocumentWriter getWriter(ReadWriteFormat f) {
  	return factories.get(f).getWriter();
  }
  
  
  public AbstractFilter getFilter(ReadWriteFormat f) {
  	return factories.get(f).getFilter();
  }


	public int size() {
		return factories.size();
	}
}