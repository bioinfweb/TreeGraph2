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
package info.bioinfweb.treegraph.gui.dialogs.elementformats.piecolor;


import info.bioinfweb.treegraph.gui.dialogs.elementformats.IconPieChartLabelPanel;

import java.awt.Color;



/**
 * Represents an entry in the pie color list of the {@link IconPieChartLabelPanel}.
 * @author Ben St&ouml;ver
 * @since 2.0.43
 */
public class PieColorListEntry {
  private String id;
  private Color color;
  
  
	public PieColorListEntry(String id, Color color) {
		super();
		this.id = id;
		this.color = color;
	}


	@Override
	public String toString() {
		return id;
	}


	public String getID() {
		return id;
	}


	public void setID(String id) {
		this.id = id;
	}


	public Color getColor() {
		return color;
	}


	public void setColor(Color color) {
		this.color = color;
	}
}