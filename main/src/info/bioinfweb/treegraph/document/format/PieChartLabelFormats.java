/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2011, 2013-2016  Ben Stöver, Sarah Wiechers, Kai Müller
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
package info.bioinfweb.treegraph.document.format;


import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import info.bioinfweb.treegraph.document.Label;
import info.bioinfweb.treegraph.document.PieColorManager;



/**
 * Stores the formats of pie chart label.
 * 
 * @author Ben St&ouml;ver
 * @since 2.0.43
 */
public class PieChartLabelFormats extends GraphicalLabelFormats implements LineFormats {
	public static final boolean DEFAULT_SHOW_INTERNAL_LINES = true;
	public static final boolean DEFAULT_SHOW_LINES_FOR_ZERO = false;
	
	
  private List<Color> pieColors = new ArrayList<Color>();
  private boolean showInternalLines = DEFAULT_SHOW_INTERNAL_LINES;
  private boolean showLinesForZero = DEFAULT_SHOW_LINES_FOR_ZERO; 


	public PieChartLabelFormats(Label owner, boolean above, int line, int linePosition) {
		super(owner, above, line, linePosition);
	}

	
	public PieChartLabelFormats(Label owner) {
		super(owner);
	}


  public boolean addPieColor(Color color) {
		return pieColors.add(color);
	}


  public void addPieColor(int index, Color color) {
		pieColors.add(index, color);
	}


	/**
	 * Returns the stored color with the specified index. If the index is out of the rage of the 
	 * currently stored field, new colors are added.
	 * @param index - the index of the color
	 * @return
	 */
	public Color getPieColor(int index) {
		if (pieColorCount() <= index) {
			PieColorManager.getInstance().addDefaultColors(this, index + 1);
		}
		return pieColors.get(index);
	}


	public Color setPieColor(int index, Color color) {
		if (pieColorCount() <= index) {
			PieColorManager.getInstance().addDefaultColors(this, index + 1);
		}
		return pieColors.set(index, color);
	}


	public int pieColorCount() {
		return pieColors.size();
	}


	public boolean getShowInternalLines() {
		return showInternalLines;
	}


	public void setShowInternalLines(boolean showInternalLines) {
		this.showInternalLines = showInternalLines;
	}


	public boolean getShowLinesForZero() {
		return showLinesForZero;
	}


	public void setShowLinesForZero(boolean showNullLines) {
		this.showLinesForZero = showNullLines;
	}


	public void assignPieChartLabelFormats(PieChartLabelFormats other) {
  	pieColors.clear();
  	pieColors.addAll(other.pieColors);
  	setShowInternalLines(other.getShowInternalLines());
  	setShowLinesForZero(other.getShowLinesForZero());
  }
	
	
	public void assign(PieChartLabelFormats other) {
		assignLabelFormats(other);
		assignLineFormats(other);
		assignGraphicalLabelFormats(other);
		assignPieChartLabelFormats(other);
	}
	
	
	@Override
	public PieChartLabelFormats clone() {
		PieChartLabelFormats result = new PieChartLabelFormats(null);
		result.assign(this);
		return result;
	}
}