/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2015  Ben St�ver, Kai M�ller
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
package info.bioinfweb.treegraph.gui.dialogs.editelement;


import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import info.bioinfweb.treegraph.document.undo.ImportTextElementDataParameters;
import info.bioinfweb.treegraph.gui.dialogs.EditDialog;
import info.bioinfweb.treegraph.gui.dialogs.ImportTextElementDataParametersPanel;
import info.bioinfweb.treegraph.gui.mainframe.MainFrame;



public class SynchronizeTreeSelectionDialog extends EditDialog {
	private JPanel jContentPane = null;
	private ImportTextElementDataParametersPanel textElementDataParametersPanel = null;
	private ImportTextElementDataParameters textElementDataParameters = null;
	
	
	public SynchronizeTreeSelectionDialog(MainFrame mainFrame) {
		super(mainFrame);
//		setHelpCode(); //TODO set correct help code
		initialize();
		setLocationRelativeTo(mainFrame);
	}
	

	@Override
	protected boolean onExecute() {		
		textElementDataParametersPanel.assignFromParameters(textElementDataParameters);
		return true;
	}

	
	@Override
	protected boolean apply() {
		textElementDataParametersPanel.assignToParameters(textElementDataParameters);
		return true;
	}
	
	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		setTitle("Tree selection synchronization compare parameters");
		setContentPane(getJContentPane());
		pack();
	}
	
	
	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BoxLayout(getJContentPane(), BoxLayout.Y_AXIS));
			jContentPane.add(getTextElementDataParametersPanel(), null);
			jContentPane.add(getButtonsPanel(), null);
			getApplyButton().setVisible(false);
		}
		return jContentPane;
	}
	
	
	private ImportTextElementDataParametersPanel getTextElementDataParametersPanel() {
		if (textElementDataParametersPanel == null) {
			textElementDataParametersPanel = new ImportTextElementDataParametersPanel();
			textElementDataParametersPanel.setBorder(BorderFactory.createTitledBorder(null, "", 
					TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, 
					new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
		}
		return textElementDataParametersPanel;
	}


	public ImportTextElementDataParameters getTextElementDataParameters() {
		return textElementDataParameters;
	}
}
