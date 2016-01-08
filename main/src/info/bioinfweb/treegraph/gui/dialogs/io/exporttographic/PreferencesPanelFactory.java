/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2015-2016  Ben Stöver, Sarah Wiechers
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
package info.bioinfweb.treegraph.gui.dialogs.io.exporttographic;


import info.bioinfweb.treegraph.graphics.export.GraphicFormat;

import java.util.EnumMap;



/**
 * This class provides instances of the accordant preferences panels to the different graphic formats
 * TreeGraph can export data to.
 * @author Ben St&ouml;ver
 */
public class PreferencesPanelFactory {
  private static PreferencesPanelFactory firstInstance = null;
  
  private EnumMap<GraphicFormat, PreferencesPanel> panels = 
  	  new EnumMap<GraphicFormat, PreferencesPanel>(GraphicFormat.class);

  
	private PreferencesPanelFactory() {
		super();
		fillList();
	}
	
	
	public static PreferencesPanelFactory getInstance() {
		if (firstInstance == null) {
			firstInstance = new PreferencesPanelFactory();
		}
		return firstInstance;
	}
  
	
  private void fillList() {
  	TransparentBgPrefPanel transparenrtBgPrefPanel = new TransparentBgPrefPanel();
  	panels.put(GraphicFormat.SVG, transparenrtBgPrefPanel);
	  panels.put(GraphicFormat.PNG, transparenrtBgPrefPanel);
	  panels.put(GraphicFormat.EMF, transparenrtBgPrefPanel);
	  panels.put(GraphicFormat.TIFF, new TIFFPrefPanel());
  	panels.put(GraphicFormat.JPEG, new JPEGPrefPanel());
  }
  

  public PreferencesPanel getPanel(GraphicFormat format) {
  	return panels.get(format);
  }
}