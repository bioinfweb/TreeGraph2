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
package info.bioinfweb.treegraph.document.format;


import java.awt.Color;



public class NodeFormats extends ConcreteTextFormats 
    implements ElementFormats, LineFormats, CornerRadiusFormats, Cloneable {
	
	private DistanceValue lineWidth = new DistanceValue(LineFormats.DEFAULT_LINE_WIDTH_IN_MM);
  private DistanceValue cornerRadius = new DistanceValue(CornerRadiusFormats.STD_EDGE_RADIUS_IN_MM);
  private Color lineColor = LineFormats.DEFAULT_LINE_COLOR;
	private Margin leafMargin = new Margin(1f, 0.3f, 1f, 0.3f);  // -> NodeFormats

  
	public DistanceValue getLineWidth() {
		return lineWidth;
	}

	
	/**
	 * The line wich is dasplayed for an node can be rounded at ist top and bottom end
	 * The inner radius (without the line width) of the quater of a circle at the ends
	 * of this line is specified by the corner radius. 
	 * @return the corner radius for this node
	 */
	public DistanceValue getCornerRadius() {
		return cornerRadius;
	}


  public Color getLineColor() {
		return lineColor;
	}


	public void setLineColor(Color color) {
		lineColor = color;
	}
	
	
	public Margin getLeafMargin() {
		return leafMargin;
	}


	public void assignLineFormats(LineFormats other) {
		setLineColor(other.getLineColor());
		getLineWidth().assign(other.getLineWidth());
	}
	
	
	public void assignCornerRadiusFormats(CornerRadiusFormats other) {
		getCornerRadius().assign(other.getCornerRadius());
	}
	
	
	public void assignNodeFormats(NodeFormats other) {
		getLeafMargin().assign(other.getLeafMargin());
	}


	public void assign(NodeFormats other) {
		assignTextFormats(other);
		assignLineFormats(other);
		assignCornerRadiusFormats(other);
		assignNodeFormats(other);
	}


	@Override
	public NodeFormats clone() {
		NodeFormats result = new NodeFormats();
		result.assign(this);
		return result;
	}
}