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
package info.bioinfweb.treegraph.gui.dialogs.elementformats;


import java.util.List;

import info.bioinfweb.treegraph.document.TextElement;
import info.bioinfweb.treegraph.document.format.operate.FormatOperator;
import info.bioinfweb.treegraph.document.format.operate.TextColorOperator;
import info.bioinfweb.treegraph.gui.treeframe.TreeSelection;
import info.bioinfweb.commons.swing.SwingChangeMonitor;
import javax.swing.JColorChooser;



/**
 * Panel of the {@link ElementFormatsDialog} used to edit the font color.
 * 
 * @author Ben St&ouml;ver
 */
public class FontColorPanel extends JColorChooser implements ElementFormatTab {
	private static final long serialVersionUID = 1L;
	
	
	private SwingChangeMonitor colorMonitor = new SwingChangeMonitor();
	
	
	public FontColorPanel() {
		super();
		getSelectionModel().addChangeListener(colorMonitor);
	}


	public boolean setValues(TreeSelection selection) {
		TextElement first = selection.getFirstElementOfType(TextElement.class);
		boolean result = (first != null);
		if (result) {
			setColor(first.getFormats().getTextColor());
		}
		return result;
	}


	public String title() {
		return "Font color";
	}


	public void addOperators(List<FormatOperator> operators) {
		if (colorMonitor.hasChanged()) {
			operators.add(new TextColorOperator(getColor()));
		}
	}


	public void addError(List<String> list) {}


	public void resetChangeMonitors() {
		colorMonitor.reset();
	}
}