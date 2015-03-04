/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2015  Ben Stöver, Kai Müller
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
package info.bioinfweb.treegraph.gui.dialogs.elementformats.piecolor;


import info.bioinfweb.treegraph.gui.dialogs.elementformats.IconPieChartLabelPanel;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;



/**
 * Renders a cell in the pie color table of the {@link IconPieChartLabelPanel}.
 * @author Ben St&ouml;ver
 * @since 2.0.43
 */
public class PieColorCellRenderer extends JLabel implements ListCellRenderer {
	private PieColorIcon icon = new PieColorIcon();
	
	
  public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
    boolean cellHasFocus) {
  	
    setText(value.toString());
    if (isSelected) {
      setBackground(list.getSelectionBackground());
      setForeground(list.getSelectionForeground());
    } 
    else {
      setBackground(list.getBackground());
      setForeground(list.getForeground());
    }
    if (value instanceof PieColorListEntry) {
    	icon.setColor(((PieColorListEntry)value).getColor());
      setIcon(icon);
    }
    setEnabled(list.isEnabled());
    setFont(list.getFont());
    setOpaque(true);
    return this;
  }
}