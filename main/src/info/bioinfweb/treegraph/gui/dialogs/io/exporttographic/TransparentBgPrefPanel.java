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
package info.bioinfweb.treegraph.gui.dialogs.io.exporttographic;


import info.bioinfweb.treegraph.graphics.export.GraphicWriter;
import info.bioinfweb.commons.collections.ParameterMap;

import java.awt.GridBagLayout;
import javax.swing.JPanel;
import javax.swing.JCheckBox;
import java.awt.GridBagConstraints;



public class TransparentBgPrefPanel extends JPanel implements PreferencesPanel {
	private static final long serialVersionUID = 1L;
	
	
	private JCheckBox transparentCheckBox = null;

	
	/**
	 * This is the default constructor
	 */
	public TransparentBgPrefPanel() {
		super();
		initialize();
	}

	
	public void addHints(ParameterMap hints) {
		hints.put(GraphicWriter.KEY_TRANSPARENT, 
				new Boolean(getTransparentCheckBox().isSelected()));
	}


	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	protected void initialize() {
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.gridy = 0;
		this.setLayout(new GridBagLayout());
		this.add(getTransparentCheckBox(), gridBagConstraints);
	}


	/**
	 * This method initializes transparentCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getTransparentCheckBox() {
		if (transparentCheckBox == null) {
			transparentCheckBox = new JCheckBox();
			transparentCheckBox.setText("Transparent background");
		}
		return transparentCheckBox;
	}
}