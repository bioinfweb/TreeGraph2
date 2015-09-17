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
package info.bioinfweb.treegraph.document;


import info.bioinfweb.treegraph.document.format.ElementFormats;
import info.bioinfweb.treegraph.document.format.TextLabelFormats;



/**
 * @author Ben St&ouml;ver
 * @since 2.0.26
 */
public class TextLabel extends Label implements TextElement {
	private TextElementData data = new TextElementData();
	private TextLabelFormats formats = new TextLabelFormats(this);
	
	
	public TextLabel(Labels labels) {
		super(labels);
	}


	public void assignTextElementData(TextElement other) {
		getData().assign(other.getData());
	}

	
	public TextElementData getData() {
		return data;
	}

	
	@Override
	public TextLabelFormats getFormats() {
		return formats;
	}

	
	public void setFormats(ElementFormats formats) {
		((TextLabelFormats)formats).setOwner(this);
		this.formats = (TextLabelFormats)formats;
		reinsert();
	}

	
	/**
	 * Returns a deep copy of this text label. Note that the linked {@link Labels}-object
	 * of the returned object is <code>null</code> no matter if this object was linked or not.
	 */
	@Override
	public TextLabel clone() {
		TextLabel result = new TextLabel(null);
		result.assignTextElementData(this);
		result.assignLabelData(this);
		result.setFormats(getFormats().clone());
		return result;
	}
}