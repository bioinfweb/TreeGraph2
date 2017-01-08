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
package info.bioinfweb.treegraph.gui.dialogs.io.exporttree;


import info.bioinfweb.commons.collections.ParameterMap;
import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.io.ReadWriteParameterMap;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;



public class NexusFormatPanel extends NewickFormatPanel implements TreeFormatPanel {
	private JPanel nexusOptionsPanel = null;
	private JCheckBox exportTaxaTable = null;
	private JCheckBox useTranslTable = null;

	
	public NexusFormatPanel() {
		super();
		initialize();
	}
	

	@Override
	public void addProperties(ParameterMap properties) {
		super.addProperties(properties);
		properties.put(ReadWriteParameterMap.KEY_EXPORT_TAXA_BLOCK, exportTaxaTable.isSelected());
		properties.put(ReadWriteParameterMap.KEY_USE_TRANSL_TABLE, useTranslTable.isSelected());
	}


	@Override
	public void initializeContents(Document document) {
		super.initializeContents(document);		
	}


	private void initialize() {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.add(getNodeNameFormatPanel());
		this.add(getNexusOptionsPanel());
		this.add(getSourceDataPanel());
		exportTaxaTable.setSelected(true);
		useTranslTable.setSelected(true);
	}


	private JPanel getNexusOptionsPanel() {
		if (nexusOptionsPanel == null) {
			nexusOptionsPanel = new JPanel();
			nexusOptionsPanel.setLayout(new GridBagLayout());
			nexusOptionsPanel.setBorder(new TitledBorder(null, "Nexus file components", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, 
					new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			
			GridBagConstraints gbc_exportTaxaTable = new GridBagConstraints();
			gbc_exportTaxaTable.gridx = 0;
			gbc_exportTaxaTable.anchor = GridBagConstraints.WEST;
			gbc_exportTaxaTable.gridy = 0;
			gbc_exportTaxaTable.weightx = 1.0;
			exportTaxaTable = new JCheckBox();
			exportTaxaTable.setText("Export taxa block");
			
			GridBagConstraints gbc_useTranslTable = new GridBagConstraints();
			gbc_useTranslTable.gridx = 1;
			gbc_useTranslTable.anchor = GridBagConstraints.WEST;
			gbc_useTranslTable.insets = new Insets(0, 0, 0, 0);
			gbc_useTranslTable.gridy = 0;
			gbc_useTranslTable.weightx = 1.0;
			useTranslTable = new JCheckBox();
			useTranslTable.setText("Use translation table");
			
			nexusOptionsPanel.add(exportTaxaTable, gbc_exportTaxaTable);
			nexusOptionsPanel.add(useTranslTable, gbc_useTranslTable);
		}
		return nexusOptionsPanel;
	}
}
