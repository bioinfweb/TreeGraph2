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
package info.bioinfweb.treegraph.gui.dialogs.io.exporttree;


import info.bioinfweb.treegraph.document.io.ReadWriteFormat;
import java.util.EnumMap;



public class AdditionalDataPanelFactory {
	private static AdditionalDataPanelFactory firstInstance = null;
	
	private EnumMap<ReadWriteFormat, TreeFormatPanel> panels = 
  	  new EnumMap<ReadWriteFormat, TreeFormatPanel>(ReadWriteFormat.class);

  
	private AdditionalDataPanelFactory() {
		super();
		fillList();
	}
	
	
	public static AdditionalDataPanelFactory getInstance() {
		if (firstInstance == null) {
			firstInstance = new AdditionalDataPanelFactory();
		}
		return firstInstance;
	}
  
	
  private void fillList() {
  	panels.put(ReadWriteFormat.NEXUS, new NexusFormatPanel());
  	panels.put(ReadWriteFormat.NEWICK, new NewickFormatPanel());
  }
  

  public TreeFormatPanel getPanel(ReadWriteFormat format) {
  	return panels.get(format);
  }
}
