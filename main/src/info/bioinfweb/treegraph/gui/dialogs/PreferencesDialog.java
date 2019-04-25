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
package info.bioinfweb.treegraph.gui.dialogs;


import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.prefs.Preferences;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import info.bioinfweb.treegraph.Main;
import info.bioinfweb.treegraph.PreferencesConstants;
import info.bioinfweb.treegraph.gui.mainframe.MainFrame;
import info.bioinfweb.wikihelp.client.OkCancelApplyWikiHelpDialog;



public class PreferencesDialog extends OkCancelApplyWikiHelpDialog implements PreferencesConstants {
	//TODO Add options to hide further warning messages?
	
	private JPanel preferencesPanel;
	private JCheckBox checkForUpdatesCB;

	
	/**
	 * Create the dialog.
	 */
	public PreferencesDialog() {
		super(MainFrame.getInstance(), true, Main.getInstance().getWikiHelp());
		setTitle("TreeGraph 2 Preferences");
		setHelpCode(97);  //TODO Specify help code target and write article
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		getContentPane().add(getPreferencesPanel());
		getContentPane().add(getButtonsPanel());
		pack();
		setLocationRelativeTo(getOwner());
	}
	
	
	@Override
	protected void addMoreButtons(JPanel buttonPanel) {
		JButton restoreDefaultsButton = new JButton("Restore defaults");
		restoreDefaultsButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						getCheckForUpdatesCB().setSelected(DO_VERSION_CHECK_DEFAULT_VALUE);
					}
				});
		
		buttonPanel.add(restoreDefaultsButton);
	}


	private JPanel getPreferencesPanel() {
		if (preferencesPanel == null) {
			preferencesPanel = new JPanel();
			GridBagLayout layout = new GridBagLayout();
			layout.columnWidths = new int[]{0, 0};
			layout.rowHeights = new int[]{0, 0, 0};
			layout.columnWeights = new double[]{0.0, Double.MIN_VALUE};
			layout.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
			preferencesPanel.setLayout(layout);
			preferencesPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
			
			GridBagConstraints checkForUpdatesGBC = new GridBagConstraints();
			checkForUpdatesGBC.insets = new Insets(0, 0, 5, 0);
			checkForUpdatesGBC.gridx = 0;
			checkForUpdatesGBC.gridy = 0;
			preferencesPanel.add(getCheckForUpdatesCB(), checkForUpdatesGBC);
		}
		return preferencesPanel;
	}


	private JCheckBox getCheckForUpdatesCB() {
		if (checkForUpdatesCB == null) {
			checkForUpdatesCB = new JCheckBox("Check for updates on start");
		}
		return checkForUpdatesCB;
	}


	@Override
	public boolean execute() {
		Preferences preferences = Main.getInstance().getPreferences();
		getCheckForUpdatesCB().setSelected(preferences.getBoolean(DO_VERSION_CHECK_PREF_KEY, DO_VERSION_CHECK_DEFAULT_VALUE));
		
		return super.execute();
	}


	@Override
	protected boolean apply() {
		Preferences preferences = Main.getInstance().getPreferences();
		preferences.putBoolean(DO_VERSION_CHECK_PREF_KEY, getCheckForUpdatesCB().isSelected());
		
		return true;
	}
}
