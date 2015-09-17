/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2015  Ben Stöver, Sarah Wiechers, Kai Müller
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


import info.bioinfweb.commons.Math2;



/**
 * Stores the length of a branch length scale bar. In can be stored in the units of the branch lengths
 * or in millimeters.
 * @author Ben St&ouml;ver
 */
public class ScaleValue {
  private boolean inScaleUnits = true;
  private DistanceValue distanceValue = new DistanceValue();
  
  
	public ScaleValue() {
		super();
	}


	public ScaleValue(float inMillimeters) {
		super();
		setInMillimeters(inMillimeters);
	}


	public boolean isInScaleUnits() {
		return inScaleUnits;
	}
	
	
	public void setInUnits(float units) {
		inScaleUnits = true;
		distanceValue.setInMillimeters(units);
	}


	public float getInUnits(float branchLengthScale) {
		if (inScaleUnits) {
			return distanceValue.getInMillimeters();
		}
		else {
			return millimetersToUnits(distanceValue.getInMillimeters(), branchLengthScale);
		}
	}


	public void setInMillimeters(float value) {
		inScaleUnits = false;
		distanceValue.setInMillimeters(value);
	}
  
  
	public float getInMillimeters(float branchLengthScale) {
		if (inScaleUnits) {
			return millimetersToUnits(distanceValue.getInMillimeters(), branchLengthScale);
		}
		else {
			return distanceValue.getInMillimeters();
		}
	}
  
  
	public void setInPixels(float value, float pixelsPerMillimeter) {
		inScaleUnits = false;
		distanceValue.setInPixels(value, pixelsPerMillimeter);
	}
  
  
	public float getInPixels(float pixelsPerMillimeter, float branchLengthScale) {
		return  DistanceValue.millimetersToPixels(getInMillimeters(branchLengthScale), pixelsPerMillimeter);
	}
  
  
	public int getRoundedInPixels(float pixelsPerMillimeter, float branchLengthScale) {
		return Math2.roundUp(getInPixels(pixelsPerMillimeter, branchLengthScale));
	}
  
  
	public void setInPoints(float value) {
		inScaleUnits = false;
		distanceValue.setInPoints(value);
	}
	
	
	public float getInPoints(float mmPerBranchLength) {
		return DistanceValue.millimetersToPoints(getInMillimeters(mmPerBranchLength));
	}
	
	
	/**
	 * Returns the stored value in millimeters or in branch length units.
	 * @return the stored value
	 * @see info.webinsel.treegraph.document.format.ScaleValue.isInScaleUnits()
	 */
	public float getStoredValue() {
		return distanceValue.getInMillimeters();
	}
  
  
	public static float unitsToMillimeters(float units, float mmPerBranchLength) {
		return mmPerBranchLength * units;
	}
	
	
	public static float millimetersToUnits(float mm, float branchLengthScale) {
		return mm / branchLengthScale;
	}
  
  
	public static float unitsToPoints(float units, float mmPerBranch) {
		return DistanceValue.millimetersToPoints(
				unitsToMillimeters(units, mmPerBranch));
	}
	
	
	public static float pointsToUnits(float points, float mmPerBranchLength) {
		return millimetersToUnits(
				DistanceValue.pointsToMillimeters(points), mmPerBranchLength);
	}
	
	
	public static float unitsToPixels(float units, float pixelsPerMillimeter, 
			float mmPerBranchLength) {
		
		return DistanceValue.millimetersToPixels(
				unitsToMillimeters(units, mmPerBranchLength), pixelsPerMillimeter);
	}
	
	
	/**
	 * @param units
	 * @param branchLengthScale - e.g. millimeters per branch length if the result should be 
	 *        in millimeters or points per branch length if the result should be in points
	 * @return
	 */
	public static float branchLengthToDistance(float units, float branchLengthScale) {
		return branchLengthScale * units;
	}
	
	
	public void assign(ScaleValue other) {
		if (other.isInScaleUnits()) {
			setInUnits(other.distanceValue.getInMillimeters());
		}
		else {
			setInMillimeters(other.distanceValue.getInMillimeters());
		}
	}
}