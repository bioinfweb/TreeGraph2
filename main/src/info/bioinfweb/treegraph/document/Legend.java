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
package info.bioinfweb.treegraph.document;


import info.bioinfweb.treegraph.document.format.*;
import info.bioinfweb.treegraph.document.position.LegendPositionData;
import info.bioinfweb.treegraph.graphics.positionpaint.PositionPaintType;



public class Legend extends AbstractTextElement implements LineElement, CornerRadiusElement {
	private Legends legends = null;
  private LegendFormats formats = new LegendFormats(this);
  
  
  public Legend(Legends legends) {
  	this.legends = legends ;
  }
  
  
	public Legends getLegends() {
		return legends;
	}


	public void setLegends(Legends legends) {
		this.legends = legends;
	}


  public LegendFormats getFormats() {
		return formats;
	}


	public void setFormats(ElementFormats formats) {
		this.formats = (LegendFormats)formats;
	}

	
	@Override
	public LegendPositionData getPosition(PositionPaintType type) {
		LegendPositionData result = (LegendPositionData)positions.get(type);
		if (result == null) {
			result = new LegendPositionData();
			positions.put(type, result);
		}
		
		return result;
	}


	/* (non-Javadoc)
	 * @see info.bioinfweb.treegraph.document.Legends#reinsert(Legend)
	 */
	public void reinsert() {
		if (getLegends() != null) {
			getLegends().reinsert(this);
		}
	}
	
	
	/**
	 * This method currently does not do anything but should be called in the 
	 * <code>clone()</code>-method of derived classes anyway because <code>Legend</code>
	 * might contain data that needs to be copied in future versions.<br>
	 * Note that this method does and will not copy the <code>LegendFormats</code>-object.
	 */
	public void assignLegendData(Legend other) {
		// currently nothing to do
	}


	/**
	 * Returns a deep copy of this legend. Note that the linked {@link Legends}-object
	 * of the returned object is <code>null</code> no matter if this object was linked or not.
	 * @see info.bioinfweb.treegraph.document.AbstractPaintableElement#clone()
	 */
	@Override
	public Legend clone() {
		Legend result = new Legend(null);
		result.assignTextElementData(this);
		result.assignLegendData(this);
		result.formats = getFormats().clone();
		result.formats.setOwner(result);
		return result;
	}
}