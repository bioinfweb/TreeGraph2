/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2011, 2013-2019  Ben Stöver, Sarah Wiechers, Kai Müller
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
import info.bioinfweb.treegraph.document.format.ScaleBarFormats;



/**
 * This element represents a scale bar for the branch length values.<br>
 * Its text property stores the name of the unit that is displayed.
 * @author Ben St&ouml;ver
 */
public class ScaleBar extends AbstractTextElement 
    implements PaintableElement, LineElement, Cloneable {
	
	private ScaleBarFormats formats = new ScaleBarFormats();
	
	
	@Override
	public ScaleBarFormats getFormats() {
		return formats;
	}

	
	public void setFormats(ElementFormats formats) {
		this.formats = (ScaleBarFormats)formats;
	}


	/**
	 * This method currently does not do anything but should be called in the 
	 * <code>clone()</code>-method of derived classes anyway because <code>ScaleBar</code>
	 * might contain data that needs to be copied in future versions.<br>
	 * Note that this method does and will not copy the <code>ScaleBarFormats</code>-object.
	 */
	public void assignScaleBarData(ScaleBar other) {
		// currently nothing to do
	}
	
	
	@Override
	public ScaleBar clone() {
		ScaleBar result = new ScaleBar();
		result.assignTextElementData(this);
		result.assignScaleBarData(this);
		result.setFormats(getFormats().clone());
		return null;
	}
}