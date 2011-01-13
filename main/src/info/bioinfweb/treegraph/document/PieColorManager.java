/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2011  Ben Stöver, Kai Müller
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
package info.bioinfweb.treegraph.document;


import info.bioinfweb.treegraph.document.format.PieChartLabelFormats;

import java.awt.Color;
import java.util.List;
import java.util.Vector;



/**
 * Defines the default colors for the pies in pie chart labels. 
 * 
 * @author Ben St&ouml;ver
 * @since 2.0.43
 */
public class PieColorManager {
  public static final Color[] DEFAULT_COLORS = {Color.BLUE, Color.RED, Color.GREEN, Color.ORANGE, 
  	Color.CYAN, Color.MAGENTA, Color.YELLOW, Color.PINK};  //TODO Farben evtl. überarbeiten
 
  
  private static PieColorManager firstInstance = null;
  
  private List<Color> colors = new Vector<Color>();  //TODO Später erlauben mit benutzerdefinierten Werten zu belegen


	private PieColorManager() {
		super();
		
		for (int i = 0; i < DEFAULT_COLORS.length; i++) {
			colors.add(DEFAULT_COLORS[i]);
		}
	}
  
  
  public static PieColorManager getInstance() {
  	if (firstInstance == null) {
  		firstInstance = new PieColorManager();
  	}
  	return firstInstance;
  }
  
  
  public Color colorForIndex(int index) {
  	Color result = colors.get(index % colors.size());
  	int div = index / colors.size();
  	if (div > 0) {
  		if (div % 2 == 0) {
  			for (int i = 0; i < div / 2; i++) {
					result = result.darker();
				}
  		}
  		else {
  			for (int i = 0; i < div / 2 + 1; i++) {
					result = result.brighter();
				}
  		}
  	}
  	return result;
  }
  
  
  public void addDefaultColors(PieChartLabelFormats formats, int newSize) {
  	while (formats.pieColorCount() < newSize) {
  		formats.addPieColor(colorForIndex(formats.pieColorCount()));
  	}
  }
}