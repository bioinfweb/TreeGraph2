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


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.SystemColor;

import javax.swing.JComponent;
import javax.swing.JTable;

import info.bioinfweb.commons.graphics.FontCalculator;
import info.bioinfweb.treegraph.document.nodebranchdata.MetadataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;



public class DataColumnHeadingComponent extends JComponent {
	public static final int TEXT_HEIGHT = 14;
	public static final int TEXT_MARGIN = 1;
	public static final int LINE_WIDTH = 1;
	public static final int VERTICAL_LINE_DISTANCE = 3;
	public static final int HORIZONTAL_LINE_DISTANCE = 5;
	public static final int LEVEL_HEIGHT = TEXT_HEIGHT + 2 * TEXT_MARGIN + 2 * VERTICAL_LINE_DISTANCE + LINE_WIDTH;
	public static final String FONT_NAME = Font.SANS_SERIF;
	public static final int FONT_STYLE = Font.BOLD;
	
	
	private JTable table;
	private boolean isSelected;
	private boolean hasFocus; 
	private int column;
	
	
	public DataColumnHeadingComponent(JTable table) {
		super();
		this.table = table;
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
		return new Dimension(40, LEVEL_HEIGHT * getTableModel().getMaxTreeDepth());  //TODO Specify a preferred width that depends on the title length in the future?
	}


	@Override
	public void paint(Graphics g) {
		super.paint(g);
	
		g.setColor(SystemColor.menuText);
		g.setFont(new Font(FONT_NAME, FONT_STYLE, 1).deriveFont(
				FontCalculator.getInstance().getFontSizeByTextHeight(TEXT_HEIGHT, FONT_NAME, FONT_STYLE)));
		
		NodeBranchDataAdapter adapter = getTableModel().getAdapter(column);
		if (adapter instanceof MetadataAdapter) {
			int level = ((MetadataAdapter) adapter).getPath().getElementList().size();  // Values start with one, because the node and branch roots are painted on level 0.
			int textBaseY = TEXT_HEIGHT + TEXT_MARGIN + level * LEVEL_HEIGHT;
			g.drawString(table.getColumnName(column), TEXT_MARGIN, textBaseY);
			
			g.drawLine(HORIZONTAL_LINE_DISTANCE, textBaseY + TEXT_MARGIN, HORIZONTAL_LINE_DISTANCE, getHeight());
		}
		else {
			//TODO Paint headers of other columns (or use a different header component there)
		}
		
//		g.setColor(Color.GREEN);
//		g.drawLine(0, 0, getSize().width, getSize().height);
//		if (table != null) {
//			g.drawString(table.getColumnName(column), 2, 8);
//		}
	}
}
