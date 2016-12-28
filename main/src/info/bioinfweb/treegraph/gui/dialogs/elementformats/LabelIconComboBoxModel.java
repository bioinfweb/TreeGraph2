/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2011, 2013-2016  Ben Stöver, Sarah Wiechers, Kai Müller
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
package info.bioinfweb.treegraph.gui.dialogs.elementformats;


import info.bioinfweb.treegraph.graphics.positionpaint.label.icons.LabelIcon;
import info.bioinfweb.treegraph.graphics.positionpaint.label.icons.LabelIconMap;

import java.util.Iterator;
import java.util.Vector;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;



/**
 * Model used with the combo box in {@link IconPieChartLabelPanel} to select the icon type.
 * 
 * @author Ben St&ouml;ver
 * @since 2.0.25
 */
public class LabelIconComboBoxModel extends AbstractListModel<String> implements ComboBoxModel<String> {
	private Vector<String> items = new Vector<String>();
	private String selected = null;
	
	
	public LabelIconComboBoxModel() {
		super();
		fill();
	}
	
	
	private void fill() {
		Iterator<LabelIcon> icons = LabelIconMap.getInstance().values().iterator();
		while (icons.hasNext()) {
			items.add(icons.next().id());
		}
	}


	public String getElementAt(int pos) {
		return items.get(pos);
	}


	public int getSize() {
		return items.size();
	}


	public String getSelectedItem() {
		return selected;
	}


	public void setSelectedItem(Object item) {
		if (item == null) {
			selected = null;
		}
		else if ((item instanceof String) && items.contains(item)) {
			selected = (String)item;
		}
		else {
			return;  // no contentsChanged event!
		}
		fireContentsChanged(this, 0, items.size() - 1);  //TODO Interval verkleinern?
	}
}