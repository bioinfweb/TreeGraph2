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
package info.bioinfweb.treegraph.gui.dialogs;


import info.bioinfweb.treegraph.document.undo.format.ScaleBranchLengthsEdit;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import java.awt.Dimension;
import java.awt.Frame;
import javax.swing.BoxLayout;
import java.awt.GridBagLayout;
import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;
import java.awt.Font;
import java.awt.Color;



/**
 * Implementation of the scale branch lengths dialog.
 * 
 * @author Ben St&ouml;ver
 * @since 2.0.23
 */
public class ScaleBranchLengthsDialog extends EditDialog {
	private static final long serialVersionUID = 1L;

	
	private JPanel jContentPane = null;
	private JPanel lengthPanel = null;
	private DistanceValueInput lengthInput = null;  //  @jve:decl-index=0:

	
	/**
	 * @param owner
	 */
	public ScaleBranchLengthsDialog(Frame owner) {
		super(owner);
		initialize();
	}

	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		setHelpCode(35);
		setMinimumSize(new Dimension(400, 150));
		setTitle("Scale branch lengths");
		setContentPane(getJContentPane());
		pack();
	}

	
	@Override
	protected boolean onExecute() {
		boolean result = getDocument().getTree().hasAllBranchLengths();
		if (result) {
			getLengthInput().setValue(getDocument().getTree().longestPath() *
					getDocument().getTree().getFormats().getBranchLengthScale().getInMillimeters());
		}
		return result;
	}


	@Override
	protected boolean apply() {
		boolean result = getLengthInput().getValue().getInMillimeters() > 0; 
		if (result) {
			getDocument().executeEdit(new ScaleBranchLengthsEdit(getDocument(), 
					getLengthInput().getValue().getInMillimeters()));
		}
		else {
			JOptionPane.showMessageDialog(this, "The lengths of the longest path must be " +
					"greater than 0.", "Invalid length", JOptionPane.ERROR_MESSAGE);
		}
		return result;
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
			jContentPane.add(getLengthPanel(), null);
			jContentPane.add(getButtonsPanel(), null);
		}
		return jContentPane;
	}


	/**
	 * This method initializes lengthPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getLengthPanel() {
		if (lengthPanel == null) {
			lengthPanel = new JPanel();
			lengthPanel.setLayout(new GridBagLayout());
			lengthPanel.setBorder(BorderFactory.createTitledBorder(null, 
					"Length of the longest path", TitledBorder.DEFAULT_JUSTIFICATION, 
					TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), 
					new Color(51, 51, 51)));
			lengthInput = new DistanceValueInput("", lengthPanel, 0);
		}
		return lengthPanel;
	}
	
	
	/**
	 * Returns the length input and creates it if necessary.
	 * @return
	 */
	private DistanceValueInput getLengthInput() {
		if (lengthInput == null) {
			getLengthPanel();
		}
		return lengthInput;
	}
}