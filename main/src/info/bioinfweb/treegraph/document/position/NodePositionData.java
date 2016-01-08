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
package info.bioinfweb.treegraph.document.position;



public class NodePositionData extends PositionData {
	private float heightAbove = 0;
	private float difAbove = 0; 
	private float heightBelow = 0;
	private float difBelow = 0;
	
	
	public float getDifAbove() {
		return difAbove;
	}
	
	
	public void setDifAbove(float difAbove) {
		this.difAbove = difAbove;
	}
	
	
	public float getDifBelow() {
		return difBelow;
	}
	
	
	public void setDifBelow(float difBelow) {
		this.difBelow = difBelow;
	}
	
	
	public float getHeightAbove() {
		return heightAbove;
	}
	
	
	public void setHeightAbove(float heightAbove) {
		this.heightAbove = heightAbove;
	}
	
	
	public float getHeightBelow() {
		return heightBelow;
	}
	
	
	public void setHeightBelow(float heightBelow) {
		this.heightBelow = heightBelow;
	} 
}