/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2013  Ben Stöver, Kai Müller
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
package info.bioinfweb.treegraph.graphics.positionpaint;


import info.bioinfweb.treegraph.document.*;
import info.bioinfweb.treegraph.gui.treeframe.TreeSelection;

import java.awt.Graphics2D;
import java.awt.Rectangle;




public interface TreePainter {
	/**
	 * Paints the whole specified document.
	 * @param g - the graphics objects used to paint
	 * @param document - the document to be painted
	 * @param selection - a list of the selected elements
	 * @param pixelsPerMm - the scale used for painting
	 * @param transparent - determines weather the background should be filled in the background color of the document
	 */
	public void paintTree(Graphics2D g, Document document, TreeSelection selection, 
			float pixelsPerMm, boolean transparent);

	
	/**
	 * Paints the specified rectangle of the specified document.
	 * @param g - the graphics objects used to paint
	 * @param visibleRect - the rectangle of the document which is visible (in pixel coordiantes)
	 * @param document - the document to be painted
	 * @param selection - a list of the selected elements
	 * @param pixelsPerMm - the scale used for painting
	 * @param transparent - determines weather the background should be filled in the background color of the document
	 */
	public void paintTree(Graphics2D g, Rectangle visibleRect, Document document, TreeSelection selection, 
			float pixelsPerMm, boolean transparent);
}