/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2011, 2013-2017  Ben Stöver, Sarah Wiechers, Kai Müller
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


import info.bioinfweb.treegraph.document.Label;
import info.bioinfweb.treegraph.graphics.positionpaint.label.icons.LabelIconMap;



public class IconLabelFormats extends GraphicalLabelFormats implements LineFormats {
  private String icon = LabelIconMap.getInstance().getDefaultID();
  private boolean iconFilled = true;


	public IconLabelFormats(Label owner, boolean above, int line, int linePosition) {
		super(owner, above, line, linePosition);
	}


	public IconLabelFormats(Label owner) {
		super(owner);
	}


	public String getIcon() {
		return icon;
	}


	public void setIcon(String icon) {
		this.icon = icon;
	}
	
	
	public boolean getIconFilled() {
		return iconFilled;
	}


	public void setIconFilled(boolean iconFilled) {
		this.iconFilled = iconFilled;
	}
	
	
	public void assignIconLabelFormats(IconLabelFormats other) {
		setIcon(other.getIcon());
		setIconFilled(other.getIconFilled());
	}


	public void assign(IconLabelFormats other) {
		assignLabelFormats(other);
		assignLineFormats(other);
		assignGraphicalLabelFormats(other);
		assignIconLabelFormats(other);
	}
	
	
	@Override
	public IconLabelFormats clone() {
		IconLabelFormats result = new IconLabelFormats(null);
		result.assign(this);
		return result;
	}
}