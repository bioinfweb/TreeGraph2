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
package info.bioinfweb.treegraph.document;


import info.bioinfweb.treegraph.document.format.ElementFormats;
import info.bioinfweb.treegraph.document.format.IconLabelFormats;



/**
 * @author Ben St&ouml;ver
 *@since 2.0.26
 */
public class IconLabel extends GraphicalLabel implements LineElement {
	private IconLabelFormats formats = new IconLabelFormats(this);
	
	
	public IconLabel(Labels labels) {
		super(labels);
	}

	
	@Override
	public IconLabelFormats getFormats() {
		return formats;
	}

	
	public void setFormats(ElementFormats formats) {
		((IconLabelFormats)formats).setOwner(this);
		this.formats = (IconLabelFormats)formats;
		reinsert();
	}

	
	/**
	 * Returns a deep copy of this icon label. Note that the linked {@link Labels}-object
	 * of the returned object is <code>null</code> no matter if this object was linked or not.
	 */
	@Override
	public Label clone() {
		IconLabel result = new IconLabel(null);
		result.assignLabelData(this);
		result.setFormats(getFormats().clone());
		return result;
	}
}