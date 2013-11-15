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
package info.bioinfweb.treegraph.document.position;


import info.bioinfweb.treegraph.document.format.DistanceValue;
import info.webinsel.util.Math2;
import java.awt.Rectangle;



public class PositionData implements Cloneable {
	private DistanceValue left = new DistanceValue(0);
	private DistanceValue top = new DistanceValue(0);
	private DistanceValue width = new DistanceValue(0);
	private DistanceValue height = new DistanceValue(0);
	

	public DistanceValue getHeight() {
		return height;
	}


	public DistanceValue getLeft() {
		return left;
	}


	public DistanceValue getTop() {
		return top;
	}


	public DistanceValue getWidth() {
		return width;
	}

	
	public float getRightInMillimeters() {
		return getLeft().getInMillimeters() + getWidth().getInMillimeters();
	}

	
	public float getBottomInMillimeters() {
		return getTop().getInMillimeters() + getHeight().getInMillimeters();
	}
	
	
	public float getRightInPixels(float pixelsPerMillimeter) {
		return getLeft().getInPixels(pixelsPerMillimeter) + getWidth().getInPixels(pixelsPerMillimeter);
	}

	
	public float getBottomInPixels(float pixelsPerMillimeter) {
		return getTop().getInPixels(pixelsPerMillimeter) + getHeight().getInPixels(pixelsPerMillimeter);
	}

	
	public boolean contains(DistanceValue x, DistanceValue y, float margin) {
		return contains(x.getInMillimeters(), y.getInMillimeters(), margin);
	}
	
	
	public boolean contains(float x, float y, float margin) {
		return (Math2.isBetween(x, getLeft().getInMillimeters() - margin, getRightInMillimeters() + margin) 
				 && Math2.isBetween(y, getTop().getInMillimeters() - margin, getBottomInMillimeters() + margin));
	}
	
	
  public Rectangle toRect(float pixelsPerMillimeter) {
  	Rectangle result = new Rectangle(
  			getLeft().getRoundedInPixels(pixelsPerMillimeter),
  			getTop().getRoundedInPixels(pixelsPerMillimeter),
  			getWidth().getRoundedInPixels(pixelsPerMillimeter),
  			getHeight().getRoundedInPixels(pixelsPerMillimeter));
  	return result;
  }
	
	
	@Override
	public PositionData clone() throws CloneNotSupportedException {
		return (PositionData)super.clone();
	}


	@Override
	public String toString() {
		return super.toString() + "[left=" + getLeft() + ",top=" + getTop() + ",width=" + getWidth() + ",height=" + getHeight() + "]";
	}
}