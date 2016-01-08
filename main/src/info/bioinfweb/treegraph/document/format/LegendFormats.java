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
package info.bioinfweb.treegraph.document.format;


import info.bioinfweb.treegraph.document.Legend;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.graphics.positionpaint.PositionPaintType;

import java.awt.*;



public class LegendFormats extends ConcreteTextFormats 
    implements ElementFormats, LineFormats, TextFormats, CornerRadiusFormats, Cloneable {
	
	public static final TextOrientation DEFAULT_ORIENTATION = TextOrientation.HORIZONTAL;
	public static final LegendStyle DEFAULT_LEGEND_STYLE = LegendStyle.BRACKET;
	public static final float DEFAULT_SPACING_IN_MM = 1f;
	public static final int DEFAULT_POSITION = 0;
	
	
	private Legend owner = null;
	private TextOrientation orientation = DEFAULT_ORIENTATION;
	private LegendStyle legendStyle = DEFAULT_LEGEND_STYLE;
	private Color lineColor = LineFormats.DEFAULT_LINE_COLOR;
	private DistanceValue lineWidth = new DistanceValue(LineFormats.DEFAULT_LINE_WIDTH_IN_MM);
	private DistanceValue cornerRadius = new DistanceValue(CornerRadiusFormats.STD_EDGE_RADIUS_IN_MM);
	private Margin margin = new Margin(1f, 0f, 1f, 0f);
	private DistanceValue spacing = new DistanceValue(DEFAULT_SPACING_IN_MM);
	private int position = DEFAULT_POSITION;
	private DistanceValue minTreeDistance = new DistanceValue(0f);
  private String[] anchorNames = new String[2];
	
	
	public LegendFormats(Legend owner) {
		super();
		this.owner = owner;
  	for (int i = 0; i < 2; i++) {
			setAnchor(i, null);
		}
	}


	public Legend getOwner() {
		return owner;
	}


	public void setOwner(Legend owner) {
		this.owner = owner;
	}


	public TextOrientation getOrientation() {
		return orientation;
	}


	public void setOrientation(TextOrientation orientation) {
		this.orientation = orientation;
	}


	public Color getLineColor() {
		return lineColor;
	}
	
	
	public void setLineColor(Color lineColor) {
		this.lineColor = lineColor;
	}
	
	
	public DistanceValue getLineWidth() {
		return lineWidth;
	}
	
	
	/**
	 * The position of a legend defined in which order vertically overlapping legends are
	 * positioned. Legends with higher position numbers are displayed further right.
	 * @return the position number (can also be negative)
	 */
	public int getPosition() {
		return position;
	}
	
	
	/**
	 * The position of a legend defined in which order vertically overlapping legends are
	 * positioned. Legends with higher position numbers are displayed further right.<br>
	 * The assinged legend (if present) is reinserted into its <code>Legends</code>-object
	 * to ensure the correct order.
	 * @param position - the new position number (can also be negative)
	 */
	public void setPosition(int position) {
		if (this.position != position) {
			this.position = position;
			if (getOwner() != null) {
				getOwner().reinsert();
			}
		}
	}
	
	
	public DistanceValue getMinTreeDistance() {
		return minTreeDistance;
	}


	public LegendStyle getLegendStyle() {
		return legendStyle;
	}
	
	
	public void setLegendStyle(LegendStyle style) {
		this.legendStyle = style;
	}
	
	
	public Margin getMargin() {
		return margin;
	}


	/**
	 * The legend spacing is the distance between the bracket and the text of a legend.
	 */
	public DistanceValue getSpacing() {
		return spacing;
	}


	public DistanceValue getCornerRadius() {
		return cornerRadius;
	}


	public String getAnchorName(int no)  {
  	return anchorNames[no];
  }
  
  
  public void setAnchorName(int no, String value) {
  	anchorNames[no] = value;
  }
  
  
  public Node getAnchor(int no) {
  	return getOwner().getLegends().getTree().getNodeByUniqueName(getAnchorName(no));
  }
  
  
  public void setAnchor(int no, Node node) {
  	if (node == null) {
  		anchorNames[no] = null;
  	}
  	else {
    	anchorNames[no] = node.getUniqueName();
  	}
  }
  
  
  /**
   * Sorts the anchors by the y-coordinates of their according terminal subnodes. The 
   * upper most one becomes first. If only one anchor is specified it is made shure that
   * the secound anchor is <code>null</code>.<br>
   * Note that the anchors of a legend can be only positioned by the y-values for one
   * painter ID at a time. Therefor positioners have to call this method every time they
   * are executed.
   * @param type - the painter type for the y-coordinates
   */
  public void sortAnchors(PositionPaintType type) {
  	if (hasOneAnchor()) {
  		if (getAnchor(0) == null) {
  			anchorNames[0] = anchorNames[1];
  			anchorNames[1] = null;
  		}
  	}
  	else if (getAnchor(0).getPosition(type).getTop().getInMillimeters() > getAnchor(1).getPosition(type).getTop().getInMillimeters()) {
  		String temp =	anchorNames[0];
  		anchorNames[0] = anchorNames[1];
  		anchorNames[1] = temp;
  	}
  }


	/**
	 * Tests whether only one anchor is specified.  
	 */
	public boolean hasOneAnchor() {
		return (getAnchorName(0) == null) || (getAnchorName(1) == null);
	}
  
  
	public void assignLineFormats(LineFormats other) {
		setLineColor(other.getLineColor());
		getLineWidth().assign(other.getLineWidth());
	}


	public void assignCornerRadiusFormats(CornerRadiusFormats other) {
		getCornerRadius().assign(other.getCornerRadius());
	}
	
	
	/**
	 * Adopts the position values and the anchors but not the owning legend object.
	 * @param other - the object that contains the source values
	 */
	public void assignLegendPosition(LegendFormats other) {
 		setPosition(other.getPosition());
 		for (int i = 0; i <= 1; i++) {
 	 		setAnchorName(i, other.getAnchorName(i));
		}
 		getMinTreeDistance().assign(other.getMinTreeDistance());
	}
	
	
	public void assignLegendFormats(LegendFormats other) {
		setOrientation(other.getOrientation());
		setLegendStyle(other.getLegendStyle());
		getMargin().assign(other.getMargin());
		getSpacing().assign(other.getSpacing());
	}
	
	
	public void assign(LegendFormats other) {
		assignTextFormats(other);
		assignLineFormats(other);
		assignCornerRadiusFormats(other);
		assignLegendFormats(other);
		assignLegendPosition(other);
		setOwner(other.getOwner());  // assignLegendPosition() must be called before to avaiod a reinsert of the legend 
	}
	

	/**
	 * Returns a deep copy of this class. (Note that the value of <code>owner</code> is 
	 * copied as well althogh the owning legend is only connected to this and not to the
	 * returned object.)
	 */
	@Override
	public LegendFormats clone() {
		LegendFormats result = new LegendFormats(null);
		result.assign(this);
		return result;
	}
}