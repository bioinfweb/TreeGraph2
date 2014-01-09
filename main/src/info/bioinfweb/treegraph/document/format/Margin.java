/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2014  Ben Stöver, Kai Müller
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




public class Margin {
  private DistanceValue left = null;
  private DistanceValue top = null;
  private DistanceValue right = null;
  private DistanceValue bottom = null;
  
  
  public Margin() {
    left = new DistanceValue();
    top = new DistanceValue();
    right = new DistanceValue();
    bottom = new DistanceValue();
  }
  
  
  public Margin(float margin) {
    left = new DistanceValue(margin);
    top = new DistanceValue(margin);
    right = new DistanceValue(margin);
    bottom = new DistanceValue(margin);
  }
  
  
  /**Initializes the margin with values in millimeters. 
   * @param left the left margin in millimeters
   * @param top the top margin in millimeters
   * @param right the right margin in millimeters
   * @param bottom the bottom margin in millimeters
   */
  public Margin(float left, float top, float right, float bottom) {
    this.left = new DistanceValue(left);
    this.top = new DistanceValue(top);
    this.right = new DistanceValue(right);
    this.bottom = new DistanceValue(bottom);
	}


  public Margin(DistanceValue left, DistanceValue top, DistanceValue right, DistanceValue bottom) {
		this.left = left;
		this.top = top;
		this.right = right;
		this.bottom = bottom;
	}


	public DistanceValue getBottom() {
		return bottom;
	}


	public DistanceValue getLeft() {
		return left;
	}


	public DistanceValue getRight() {
		return right;
	}


	public DistanceValue getTop() {
		return top;
	}


  public void assign(Margin other) {
  	getLeft().assign(other.getLeft());
  	getTop().assign(other.getTop());
  	getRight().assign(other.getRight());
  	getBottom().assign(other.getBottom());
  }
	
	
	@Override
	protected Margin clone() {
		return new Margin(getLeft().clone(), getTop().clone(), getRight().clone(), getBottom().clone());
	}


	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((bottom == null) ? 0 : bottom.hashCode());
		result = PRIME * result + ((left == null) ? 0 : left.hashCode());
		result = PRIME * result + ((right == null) ? 0 : right.hashCode());
		result = PRIME * result + ((top == null) ? 0 : top.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Margin other = (Margin) obj;
		if (bottom == null) {
			if (other.bottom != null)
				return false;
		} else if (!bottom.equals(other.bottom))
			return false;
		if (left == null) {
			if (other.left != null)
				return false;
		} else if (!left.equals(other.left))
			return false;
		if (right == null) {
			if (other.right != null)
				return false;
		} else if (!right.equals(other.right))
			return false;
		if (top == null) {
			if (other.top != null)
				return false;
		} else if (!top.equals(other.top))
			return false;
		return true;
	}
}