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
package info.bioinfweb.treegraph.document.format;


import java.awt.Color;



public class BranchFormats implements ElementFormats, LineFormats, Cloneable {
	public static final boolean STD_CONSTANT_WIDTH = false;
	public static final float DEFAULT_MIN_LENGTH = 4f;
	public static final float STD_MIN_SPACE_ABOVE = 0f;
	public static final float STD_MIN_SPACE_BELOW = 0f;
	
	
	private Color lineColor = LineFormats.DEFAULT_LINE_COLOR;
  private DistanceValue lineWidth = new DistanceValue(LineFormats.DEFAULT_LINE_WIDTH_IN_MM);
  private boolean constantWidth = STD_CONSTANT_WIDTH;
  private DistanceValue minSpaceAbove = new DistanceValue(STD_MIN_SPACE_ABOVE);
  private DistanceValue minSpaceBelow = new DistanceValue(STD_MIN_SPACE_BELOW);
  private DistanceValue minLength = new DistanceValue(DEFAULT_MIN_LENGTH);
  
  
	public DistanceValue getLineWidth() {
		return lineWidth;
	}
	
	
	public DistanceValue getMinLength() {
		return minLength;
	}
	
	
	public DistanceValue getMinSpaceAbove() {
		return minSpaceAbove;
	}
	
	
	public DistanceValue getMinSpaceBelow() {
		return minSpaceBelow;
	}


	public Color getLineColor() {
		return lineColor;
	}


	public void setLineColor(Color color) {
		lineColor = color;
	}
	
	
	public boolean isConstantWidth() {
		return constantWidth;
	}


	public void setConstantWidth(boolean constantWidth) {
		this.constantWidth = constantWidth;
	}


	public void assignLineFormats(LineFormats other) {
		setLineColor(other.getLineColor());
		getLineWidth().assign(other.getLineWidth());
	}
	
	
	public void assignBranchFormats(BranchFormats other) {
		setConstantWidth(other.isConstantWidth());
		getMinSpaceAbove().assign(other.getMinSpaceAbove());
		getMinSpaceBelow().assign(other.getMinSpaceBelow());
		getMinLength().assign(other.getMinLength());
	}
	
	
	public void assign(BranchFormats other) {
		assignLineFormats(other);
		assignBranchFormats(other);
	}
	
	
	@Override
	public BranchFormats clone() {
		BranchFormats result = new BranchFormats();
		result.assign(this);
		return result;
	}
}