/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2014  Ben St�ver, Kai M�ller
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
package info.bioinfweb.treegraph.document.format.adapters.distance;


import info.bioinfweb.treegraph.document.format.DistanceValue;
import info.bioinfweb.treegraph.document.format.GraphicalLabelFormats;



/**
 * Format adapter for editing the line width of graphical labels. 
 * 
 * @author Ben St&ouml;ver
 * @since 2.3.0
 */
public class LabelLineWidthAdapter extends AbstractGraphicalLabelDistanceAdapter {
	public LabelLineWidthAdapter(String id) {
		super(id);
	}


	@Override
  protected DistanceValue getGraphicalLabelDistanceValue(GraphicalLabelFormats formats) {
	  return formats.getLineWidth();
  }


	@Override
	public String toString() {
		return toString("Label line widths");
	}
}