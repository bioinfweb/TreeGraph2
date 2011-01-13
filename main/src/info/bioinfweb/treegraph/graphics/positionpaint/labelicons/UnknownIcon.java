/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2011  Ben Stöver, Kai Müller
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
package info.bioinfweb.treegraph.graphics.positionpaint.labelicons;


import info.bioinfweb.treegraph.document.format.IconLabelFormats;

import java.awt.Font;
import java.awt.Graphics2D;



/**
 * This icon shall be displayed if an unknown icon ID was specified (e.g. in a document created with a newer
 * version of TreeGraph 2).
 * @author Ben St&ouml;ver
 * @since 2.0.25
 */
public class UnknownIcon implements LabelIcon {
	public static final String TEXT = "?";
	
	
	/**
	 * Displays a questionmark.
	 * @see info.bioinfweb.treegraph.graphics.positionpaint.labelicons.LabelIcon#paint(java.awt.Graphics2D, float, float, info.bioinfweb.treegraph.document.format.LabelFormats, float)
	 */
	public void paint(Graphics2D g, float x, float y, IconLabelFormats formats, float pixelsPerMillimeter) {
		Font font = new Font(Font.SANS_SERIF, Font.PLAIN, 
				Math.round(0.8f * formats.getHeight().getInPixels(pixelsPerMillimeter))); 
		
		g.setColor(formats.getLineColor());
		g.setFont(font);
		g.drawString(TEXT, x, y + g.getFontMetrics(font).getAscent());
	}

	
	/**
	 * Returns <code>null</code>.
	 * @see info.bioinfweb.treegraph.graphics.positionpaint.labelicons.LabelIcon#id()
	 */
	public String id() {
		return null;
	}
}