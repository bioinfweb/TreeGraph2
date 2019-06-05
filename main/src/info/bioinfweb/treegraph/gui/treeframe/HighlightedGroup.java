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
package info.bioinfweb.treegraph.gui.treeframe;


import java.awt.Color;

import info.bioinfweb.commons.graphics.GraphicsUtils;



/**
 * Model class containing a group paintable elements that are highlighted in a specific color inside an instance of {@link TreeViewPanel}.
 * 
 * @author Ben St&ouml;ver
 * @since 2.16.0
 * @see ElementHighlighting
 */
public class HighlightedGroup extends TreeSelection {
	private String name;
	private Color primaryColor; 
	private Color alternativeColor;
	
	
	public HighlightedGroup(TreeViewPanel owner, String name, Color primaryColor, Color alternativeColor) {
		super(owner);
		if (name == null) {
			throw new IllegalArgumentException("name must not be null.");
		}
		else if (name.equals("")) {
			throw new IllegalArgumentException("name must not be an empty string.");
		}
		else if (primaryColor == null) {
			throw new IllegalArgumentException("primaryColor must not be null.");
		}
		else if (alternativeColor == null) {
			throw new IllegalArgumentException("alternativeColor must not be null.");
		}
		else {
			this.name = name;
			this.primaryColor = primaryColor;
			this.alternativeColor = alternativeColor;
		}
	}


	public String getName() {
		return name;
	}


	public Color getPrimaryColor() {
		return primaryColor;
	}
	// If a setter should be added it must trigger an event that causes the tree and table to be repainted.


	public Color getAlternativeColor() {
		return alternativeColor;
	} 
	// If a setter should be added it must trigger an event that causes the tree and table to be repainted.
	
	
	public Color suitableColor(Color bgColor) {
		if (GraphicsUtils.brightnessDifference(bgColor, getPrimaryColor()) > TreeViewPanel.MAX_SELECTION_COLOR_DIF) {
			return getPrimaryColor();
		}
		else {
			return getAlternativeColor();
		}
	}

}
