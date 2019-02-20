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
package info.bioinfweb.treegraph.gui.treeframe.table;


import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.SystemColor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JTable;

import info.bioinfweb.commons.graphics.FontCalculator;
import info.bioinfweb.treegraph.document.metadata.MetadataNodeList;
import info.bioinfweb.treegraph.document.metadata.MetadataPathElement;
import info.bioinfweb.treegraph.document.metadata.ResourceMetadataNode;
import info.bioinfweb.treegraph.document.nodebranchdata.BranchLengthAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.MetadataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeNameAdapter;



public class DataColumnHeadingComponent extends JComponent {
	public static final int TEXT_HEIGHT = 14;  //TODO Make value dependent on OS preferences/dialog font height.
	public static final int TEXT_MARGIN = 1;
	public static final int LINE_WIDTH = 1;
	public static final int VERTICAL_LINE_DISTANCE = 3;
	public static final int HORIZONTAL_LINE_DISTANCE = 5;
	public static final int LEVEL_HEIGHT = TEXT_HEIGHT + 2 * TEXT_MARGIN + 2 * HORIZONTAL_LINE_DISTANCE + LINE_WIDTH;
	public static final String FONT_NAME = Font.SANS_SERIF;
	public static final int FONT_STYLE = Font.BOLD;
	
	
	private JTable table;
	private ColumnHeadingPaintInfoList paintInfos;
	private boolean isSelected;
	private boolean hasFocus; 
	private int column;
	
	
	public DataColumnHeadingComponent(JTable table) {
		super();
		this.table = table;
		this.paintInfos = new ColumnHeadingPaintInfoList(getTableModel());
	}


	public void setData(boolean isSelected, boolean hasFocus, int column) {
		this.isSelected = isSelected;
		this.hasFocus = hasFocus;
		this.column = column;
	}
	
	
	private DocumentTableModel getTableModel() {
		return (DocumentTableModel)table.getModel();
	}
	
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(40, LEVEL_HEIGHT * paintInfos.getMaxTreeDepth());  //TODO Specify a preferred width that depends on the title length in the future?
	}
	
	
	@Override
	public void paint(Graphics g1) {
		super.paint(g1);
		Graphics2D g = (Graphics2D)g1;
		
		g.setColor(SystemColor.menuText);
		g.setStroke(new BasicStroke(LINE_WIDTH));
		g.setFont(new Font(FONT_NAME, FONT_STYLE, 1).deriveFont(
				FontCalculator.getInstance().getFontSizeByTextHeight(TEXT_HEIGHT, FONT_NAME, FONT_STYLE)));
		
		//TODO Indicate selection and focus?
		
		// Draw text:
		ColumnHeadingPaintInfo info = paintInfos.get(column);
		int y = info.getLabelLevel() * LEVEL_HEIGHT + TEXT_HEIGHT + TEXT_MARGIN;
		g.drawString(table.getColumnName(column), TEXT_MARGIN, y);

		// Draw vertical line to data column: 
		y += TEXT_MARGIN;
		g.drawLine(VERTICAL_LINE_DISTANCE, y, VERTICAL_LINE_DISTANCE, getHeight());
		
		// Draw horizontal lines from grandparents and above:
		for (int level = 0; level < info.getLabelLevel() - 1; level++) {
			if (info.hasLine(level)) {
				y = (level + 1) * LEVEL_HEIGHT - HORIZONTAL_LINE_DISTANCE - LINE_WIDTH;
				g.drawLine(0, y, getWidth(), y);
			}
		}
		
		// Draw line from parent:
		if ((info.getLabelLevel() > 0) && info.hasLine(info.getLabelLevel() - 1)) {  // (hasLine() for the parent level should always be true.)
			int x = getWidth();
			if (info.isLastUnderParent()) {
				x = VERTICAL_LINE_DISTANCE;
			}
			y = info.getLabelLevel() * LEVEL_HEIGHT - HORIZONTAL_LINE_DISTANCE - LINE_WIDTH;
			
			g.drawLine(0, y, x, y);  // horizontal line
			g.drawLine(VERTICAL_LINE_DISTANCE, y, VERTICAL_LINE_DISTANCE, y + HORIZONTAL_LINE_DISTANCE);  // vertical line
		}
		
		// Draw line to children:
		if (info.hasLine(info.getLabelLevel())) {
			y = (info.getLabelLevel() + 1) * LEVEL_HEIGHT - HORIZONTAL_LINE_DISTANCE - LINE_WIDTH;
			g.drawLine(VERTICAL_LINE_DISTANCE, y, getWidth(), y);
		}
	}
}
