/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2015-2016  Ben Stöver, Sarah Wiechers, Kai Müller
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


import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.Legend;
import info.bioinfweb.treegraph.document.Legends;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.commons.graphics.GraphicsUtils;

import java.awt.Color;



/**
 * Provides general format functionalities.
 * @author Ben St&ouml;ver
 */
public class FormatUtils {
	/**
	 * The minimal difference of a foreground color to the background color.
	 */
	public static int MIN_COLOR_DIFFERENCE = 150;
	
	
	/**
	 * Returns the default line color or its inversion depending in the background color
	 * of the specified document.
	 * @param document
	 * @return
	 */
	public static Color getLineColor(Document document) {
		return GraphicsUtils.getContrastColor(LineFormats.DEFAULT_LINE_COLOR, 
				document.getTree().getFormats().getBackgroundColor(), MIN_COLOR_DIFFERENCE);
	}
	
	
	/**
	 * Returns the default text color or its inversion depending in the background color
	 * of the specified document.
	 * @param document
	 * @return
	 */
	public static Color getTextColor(Document document) {
		return GraphicsUtils.getContrastColor(TextFormats.DEFAULT_TEXT_COLOR, 
				document.getTree().getFormats().getBackgroundColor(), MIN_COLOR_DIFFERENCE);
	}
	
	
	/**
	 * Creates a node with an afferent branch to be inserted in the specified document. 
	 * Their formats are determined by the specified parent node. 
	 * @param parent - the future parent node of the returned node
	 * @see FormatUtils#createNode(Document)
	 */
	public static Node createNode(Node parent) {
		Node result = Node.newInstanceWithBranch();
		result.getFormats().assign(parent.getFormats());
		result.getAfferentBranch().getFormats().assign(parent.getAfferentBranch().getFormats());
		return result;
	}
	
	
	/**
	 * Creates a node with an afferent branch to be inserted in the specified document.
	 * This method is useful to create a new root node for an empty document. Otherwise
	 * {@link FormatUtils#createNode(Node)} will probably more useful. 
	 * Their text- and line-colors are set depending on the document background color. 
	 * @param document - the document the returned node shall be inserted to
	 */
	public static Node createNode(Document document) {
		Node result = Node.newInstanceWithBranch();
		Color lineColor = getLineColor(document); 
		result.getFormats().setLineColor(lineColor);
		result.getAfferentBranch().getFormats().setLineColor(lineColor);
		result.getFormats().setTextColor(getTextColor(document));			
		return result;
	}
	
	
	/**
	 * Creates a legend to be inserted in the specified document. Its formats are 
	 * determined by the specified anchor node. 
	 * @param parent - the future anchor node of the returned legend
	 * @param legends - the legends object the the returned legend will be inserted to 
	 */
	public static Legend createLegend(Node anchor, Legends legends) {
		Legend result = new Legend(legends);
		result.getFormats().setLineColor(anchor.getFormats().getLineColor());
		result.getFormats().setTextColor(anchor.getFormats().getTextColor());
		return result;
	}
}