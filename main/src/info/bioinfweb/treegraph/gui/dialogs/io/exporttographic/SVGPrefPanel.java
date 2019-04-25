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
package info.bioinfweb.treegraph.gui.dialogs.io.exporttographic;


import info.bioinfweb.commons.collections.ParameterMap;
import info.bioinfweb.treegraph.graphics.export.svg.SVGWriter;

import java.awt.GridBagConstraints;

import javax.swing.JCheckBox;



public class SVGPrefPanel extends TransparentBgPrefPanel {
	private JCheckBox textAsShapesCheckBox;  // Must not be set to anything (e.g. null)!
	
	
	@Override
	public void addHints(ParameterMap hints) {
		super.addHints(hints);
		hints.put(SVGWriter.KEY_TEXT_AS_SHAPES,	new Boolean(getTextAsShapesCheckBox().isSelected()));
	}

	
	@Override
	protected void initialize() {
		super.initialize();
		
		GridBagConstraints checkBoxGBC = new GridBagConstraints();
		checkBoxGBC.fill = GridBagConstraints.HORIZONTAL;
		checkBoxGBC.gridx = 0;
		checkBoxGBC.gridy = 1;
		checkBoxGBC.weightx = 1.0;
		add(getTextAsShapesCheckBox(), checkBoxGBC);
	}

	
	private JCheckBox getTextAsShapesCheckBox() {
		if (textAsShapesCheckBox == null) {
			textAsShapesCheckBox = new JCheckBox();
			textAsShapesCheckBox.setText("Text as shapes");
			textAsShapesCheckBox.setSelected(false);
		}
		return textAsShapesCheckBox;
	}
}
