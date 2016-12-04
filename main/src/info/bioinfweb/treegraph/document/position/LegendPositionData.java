/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2011, 2013-2016  Ben Stöver, Sarah Wiechers, Kai Müller
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



public class LegendPositionData extends PositionData {
  private PositionData textPos = new PositionData();
  private PositionData linePos = new PositionData();
  private DistanceValue spacing = new DistanceValue();

  
	public PositionData getTextPos() {
		return textPos;
	}

	
	/**
	 * The position if the icon (for labels) or the bracket (for legends).
	 * @return
	 */
	public PositionData getLinePos() {
		return linePos;
	}


	/**
	 * The spacing between the icon or bracket and the text.
	 * @return
	 */
	public DistanceValue getSpacing() {
		return spacing;
	}


	@Override
	public DistanceValue getHeight() {
		if (getTextPos().getHeight().getInMillimeters() > getLinePos().getHeight().getInMillimeters()) {
			super.getHeight().assign(getTextPos().getHeight());
		}
		else {
			super.getHeight().assign(getLinePos().getHeight());
		}
		return super.getHeight();
	}


	@Override
	public DistanceValue getLeft() {
		if (getTextPos().getLeft().getInMillimeters() < getLinePos().getLeft().getInMillimeters()) {
			super.getLeft().assign(getTextPos().getLeft());
		}
		else {
			super.getLeft().assign(getLinePos().getLeft());
		}
		return super.getLeft();
	}


	@Override
	public DistanceValue getTop() {
		if (getTextPos().getTop().getInMillimeters() < getLinePos().getTop().getInMillimeters()) {
			super.getTop().assign(getTextPos().getTop());
		}
		else {
			super.getTop().assign(getLinePos().getTop());
		}
		return super.getTop();
	}


	@Override
	public DistanceValue getWidth() {
		super.getWidth().assign(getTextPos().getWidth());
		super.getWidth().add(getSpacing());
		super.getWidth().add(getLinePos().getWidth());
		return super.getWidth();
	}


	@Override
	public boolean contains(float x, float y, float margin) {
		return getLinePos().contains(x, y, margin) || getTextPos().contains(x, y, margin);
	}
}