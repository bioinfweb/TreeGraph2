/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2013  Ben Stöver, Kai Müller
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


import info.bioinfweb.treegraph.document.ScaleBar;
import info.bioinfweb.treegraph.document.io.newick.BranchLengthsScaler;

import java.awt.Color;



/**
 * Stores the formats of a {@link ScaleBar}.
 * 
 * @author Ben St&ouml;ver
 */
public class ScaleBarFormats extends ConcreteTextFormats implements LineFormats {
	public static final ScaleAlignment DEFAULT_ALIGNMENT = ScaleAlignment.LEFT; 
	
	public static final float DEFAULT_TREE_DISTANCE_IN_MM = 2; 
	
	public static final float DEFAULT_WIDTH_IN_MM = 20; 
	
	public static final float DEFAULT_HEIGHT_IN_MM = 3;
	
	/**
	 * The default small interval length in branch length units.
	 */
	public static final float DEFAULT_SMALL_INTERVAL = 10;
	
	/**
	 * The default small interval length in millimeters.
	 * @see BranchLengthsScaler
	 */
	public static final float DEFAULT_SMALL_INTERVAL_IN_MM = 1; 
	
	public static final int DEFAULT_LONG_INTERVAL = 10; 
	
	public static final boolean DEFAULT_START_LEFT = true; 
	
	public static final boolean DEFAULT_INCREASING = true; 
	
	public static final float DEFAULT_TEXT_HEIGHT_IN_MM = TextLabelFormats.DEFAULT_TEXT_HEIGHT_IN_MM;
	
	
	private DistanceValue lineWidth = new DistanceValue(LineFormats.DEFAULT_LINE_WIDTH_IN_MM);
  private Color lineColor = LineFormats.DEFAULT_LINE_COLOR;
  private ScaleAlignment alignment = DEFAULT_ALIGNMENT;
  private DistanceValue treeDistance = new DistanceValue(DEFAULT_TREE_DISTANCE_IN_MM);
  private ScaleValue width = new ScaleValue(DEFAULT_WIDTH_IN_MM);
  private DistanceValue height = new DistanceValue(DEFAULT_HEIGHT_IN_MM);
  private float smallInterval = DEFAULT_SMALL_INTERVAL;
  private int longInterval = DEFAULT_LONG_INTERVAL;
  private boolean startLeft = DEFAULT_START_LEFT;
  private boolean increasing = DEFAULT_INCREASING;
  
	
	public ScaleBarFormats() {
		super();
		getTextHeight().setInMillimeters(DEFAULT_TEXT_HEIGHT_IN_MM);
	}


	public Color getLineColor() {
		return lineColor;
	}
	

	public void setLineColor(Color color) {
		lineColor = color;
	}

	
	public DistanceValue getLineWidth() {
		return lineWidth;
	}
	
	
	public ScaleAlignment getAlignment() {
		return alignment;
	}


	public void setAlignment(ScaleAlignment scaleAnchor) {
		this.alignment = scaleAnchor;
	}


	public DistanceValue getTreeDistance() {
		return treeDistance;
	}


	public ScaleValue getWidth() {
		return width;
	}


	/**
	 * Defines the height of the long dashes (the height of the text below is not included.)
	 * @return
	 */
	public DistanceValue getHeight() {
		return height;
	}


	/**
	 * The long interval specifies how often the scale bar is labeles with a value. The unit used to 
	 * define it is the number of small intervals. 
	 * @return
	 */
	public int getLongInterval() {
		return longInterval;
	}


	public void setLongInterval(int longInterval) {
		this.longInterval = longInterval;
	}


	/**
	 * The small interval speccifies the distance between the small dashes of the branch length scale 
	 * bar in branch length units. 
	 * @return
	 */
	public float getSmallInterval() {
		return smallInterval;
	}


	public void setSmallInterval(float smallInterval) {
		this.smallInterval = smallInterval;
	}

	
	public boolean isIncreasing() {
		return increasing;
	}


	public void setIncreasing(boolean increasing) {
		this.increasing = increasing;
	}


	public boolean isStartLeft() {
		return startLeft;
	}


	public void setStartLeft(boolean startLeft) {
		this.startLeft = startLeft;
	}


	public void assignLineFormats(LineFormats other) {
		setLineColor(other.getLineColor());
		getLineWidth().assign(other.getLineWidth());
	}


	public void assignScaleBarFormats(ScaleBarFormats other) {
		setAlignment(other.getAlignment());
		getTreeDistance().assign(other.getTreeDistance());
		getWidth().assign(other.getWidth());
		getHeight().assign(other.getHeight());
		setSmallInterval(other.getSmallInterval());
		setLongInterval(other.getLongInterval());
		setStartLeft(other.isStartLeft());
		setIncreasing(other.isIncreasing());
	}
	
	
	public void assign(ScaleBarFormats other) {
		assignTextFormats(other);
		assignLineFormats(other);
		assignScaleBarFormats(other);
	}


	@Override
	public ScaleBarFormats clone() {
		ScaleBarFormats result = new ScaleBarFormats();
		result.assign(this);
		return result;
	}
}