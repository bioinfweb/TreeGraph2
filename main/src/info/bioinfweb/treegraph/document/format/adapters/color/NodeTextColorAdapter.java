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
package info.bioinfweb.treegraph.document.format.adapters.color;


import info.bioinfweb.treegraph.document.Node;

import java.awt.Color;



/**
 * @author Ben St&ouml;ver
 * @since 2.0.23
 */
public class NodeTextColorAdapter implements ColorAdapter {
	public void setColor(Color color, Node node) {
		node.getFormats().setTextColor(color);
	}

	
	public Color getColor(Node node) {
		return node.getFormats().getTextColor();
	}


	@Override
	public String toString() {
		return "Text colors of nodes";
	}
}