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


import info.bioinfweb.commons.*;



/**
 * This class represents a distance in the document. Distances are internaly stored in 
 * millimeters (mm) but can be converted tp pixels (px) and DTP-Points (pt). Converting
 * values in millimeters to pixels depends on the resolution (e.g. dpi) of the generated image
 * or the zoomfactor of the view displaying the document.
 * @author BenStoever
 */
public class DistanceValue implements Cloneable {
	public static final float POINTS_PER_MM = 72f / 25.4f;  // 1 pt = 1/72 Zoll; 1 Zoll = 25.4 mm
	
	
  private float millimeters = 0;  // in mm

  
	public DistanceValue() {}
	
	
	public DistanceValue(float inMillimeters) {
		setInMillimeters(inMillimeters);
	}
  
  
	public float getInMillimeters() {
		return millimeters;
	}

	
	public void setInMillimeters(float value) {
		this.millimeters = value;
	}
  
  
	public float getInPixels(float pixelsPerMillimeter) {
		return millimeters * pixelsPerMillimeter;
	}

	
	/**Gives the uprounded value in pixels */
	public int getRoundedInPixels(float pixelsPerMillimeter) {
		return Math2.roundUp(millimeters * pixelsPerMillimeter);
	}

	
	public void setInPixels(float value, float pixelsPerMillimeter) {
		this.millimeters = pixelsToMillimeters(value, pixelsPerMillimeter);
	}
  
  
	/**
	 * Returns the value in DTP-Points (1 pt = 0.3527777 mm).
	 * @return vlaue in DTP-Points
	 */
	public float getInPoints() {
		return millimeters * POINTS_PER_MM;
	}

	
	public void setInPoints(float value) {
		millimeters = value / POINTS_PER_MM;
	}


	public void assign(DistanceValue other) {
		setInMillimeters(other.getInMillimeters());
	}
	
	
	public void add(DistanceValue other) {
		add(other.getInMillimeters());
	}
	
	
	/**
	 * Adds the specified value to the stored value in millimeters.
	 * @param addend The addend in millimeters
	 */
	public void add(float addend) {
		setInMillimeters(getInMillimeters() + addend);
	}
	
	
	public static float pixelsToMillimeters(float pixels, float pixelsPerMillimeter) {
		return pixels / pixelsPerMillimeter;
	}
	
	
	public static float millimetersToPixels(float mm, float pixelsPerMillimeter) {
		return mm * pixelsPerMillimeter;
	}
	
	
	public static float millimetersToPoints(float mm) {
		return mm * POINTS_PER_MM;
	}
	
	
	public static float pointsToMillimeters(float pt) {
		return pt / POINTS_PER_MM;
	}
	
	
	@Override
	public DistanceValue clone() {
		try {
			return (DistanceValue)super.clone();
		}
		catch (CloneNotSupportedException e) {
			
			throw new InternalError();  // If this happens an unqualified anquestor was used.
		}
	}


	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(millimeters);
		result = PRIME * result + (int) (temp ^ (temp >>> 32));
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		final DistanceValue other = (DistanceValue) obj;
		if (Double.doubleToLongBits(millimeters) != Double.doubleToLongBits(other.millimeters))
			return false;
		return true;
	}
}