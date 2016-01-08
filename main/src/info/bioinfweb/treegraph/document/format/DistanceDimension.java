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



public class DistanceDimension implements Cloneable {
  private DistanceValue width = null;
  private DistanceValue height = null;
  
  
	public DistanceDimension() {
	  width = new DistanceValue();
	  height = new DistanceValue();
	}
	
	
	public DistanceDimension(DistanceValue width, DistanceValue height) {
		this.width = width;
		this.height = height;
	}
  
  
  public DistanceValue getHeight() {
		return height;
	}
	
	
	public void setHeight(DistanceValue height) {
		this.height = height;
	}
	
	
	public DistanceValue getWidth() {
		return width;
	}
	
	
	public void setWidth(DistanceValue width) {
		this.width = width;
	}

	
	public void assign(DistanceDimension other) {
		getWidth().assign(other.getWidth());
		getHeight().assign(other.getHeight());
	}
	
	
	@Override
	public DistanceDimension clone() {
		return new DistanceDimension(getWidth().clone(), getHeight().clone());
	}


	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((height == null) ? 0 : height.hashCode());
		result = PRIME * result + ((width == null) ? 0 : width.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final DistanceDimension OTHER = (DistanceDimension) obj;
		if (height == null) {
			if (OTHER.height != null) {
				return false;
			}
		} 
		else if (!height.equals(OTHER.height)) {
			return false;
		}
		if (width == null) {
			if (OTHER.width != null) {
				return false;
			}
		} 
		else if (!width.equals(OTHER.width)) {
			return false;
		}
		return true;
	}
}