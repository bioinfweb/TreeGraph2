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
package info.bioinfweb.treegraph.gui.dialogs.io;


import info.bioinfweb.treegraph.Main;
import info.bioinfweb.treegraph.document.io.newick.NewickException;
import info.bioinfweb.wikihelp.client.OkCancelApplyWikiHelpDialog;

import javax.swing.JPanel;
import javax.swing.BoxLayout;

import java.awt.Dialog;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;
import java.awt.Color;



/**
 * This dialog is used to display an {@link NewickException}.
 * 
 * @author Ben St&ouml;ver
 */
public class NewickExceptionDialog extends OkCancelApplyWikiHelpDialog {
	private static final long serialVersionUID = 1L;

	
	private JPanel jContentPane = null;
	private JPanel messagePanel = null;
	private JLabel sourceBeforeLabel = null;
	private JLabel headingLabel = null;
	private JLabel sourceAfterLabel = null;
	private JLabel messageLabel = null;
	private JLabel messageContentLabel = null;
	private JLabel positionLabel = null;
	private JLabel positionContentLabel = null;
	private JLabel sourceLabel = null;
	private JLabel positionArrowLabel = null;


	/**
	 * @param owner
	 */
	public NewickExceptionDialog(Dialog owner) {
		super(owner, Main.getInstance().getWikiHelp());
		setHelpCode(53);
		initialize();
	}
	
	
	public void show(NewickException exception) {
		messageContentLabel.setText(exception.getMessage());
		positionContentLabel.setText("" + exception.getPosition());
		sourceBeforeLabel.setText(exception.getSourceBefore());
		sourceAfterLabel.setText(exception.getSourceAfter());
		
		pack();
		setLocationRelativeTo(getOwner());
		execute();
	}

	
	@Override
	protected boolean apply() {
		return true;
	}


	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		setSize(500, 200);
		setTitle("Newick syntax error");
		setContentPane(getJContentPane());
		getApplyButton().setVisible(false);
		getCancelButton().setVisible(false);
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
			jContentPane.add(getMessagePanel(), null);
			jContentPane.add(getButtonsPanel(), null);
		}
		return jContentPane;
	}


	/**
	 * This method initializes messagePanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getMessagePanel() {
		if (messagePanel == null) {
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.gridx = 2;
			gridBagConstraints8.anchor = GridBagConstraints.WEST;
			gridBagConstraints8.gridy = 4;
			positionArrowLabel = new JLabel();
			positionArrowLabel.setText("^");
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.gridx = 0;
			gridBagConstraints7.anchor = GridBagConstraints.WEST;
			gridBagConstraints7.insets = new Insets(3, 3, 0, 0);
			gridBagConstraints7.gridy = 3;
			sourceLabel = new JLabel();
			sourceLabel.setText("Source: ");
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.gridx = 1;
			gridBagConstraints6.anchor = GridBagConstraints.WEST;
			gridBagConstraints6.insets = new Insets(3, 3, 3, 0);
			gridBagConstraints6.gridwidth = 2;
			gridBagConstraints6.gridy = 2;
			positionContentLabel = new JLabel();
			positionContentLabel.setText("JLabel");
			positionContentLabel.setFont(new Font(Font.DIALOG, Font.PLAIN, 12));
			positionContentLabel.setDisplayedMnemonic(KeyEvent.VK_UNDEFINED);
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.gridx = 0;
			gridBagConstraints5.anchor = GridBagConstraints.WEST;
			gridBagConstraints5.insets = new Insets(3, 3, 3, 0);
			gridBagConstraints5.gridy = 2;
			positionLabel = new JLabel();
			positionLabel.setText("Position: ");
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.gridx = 1;
			gridBagConstraints4.anchor = GridBagConstraints.WEST;
			gridBagConstraints4.insets = new Insets(3, 3, 3, 0);
			gridBagConstraints4.gridwidth = 2;
			gridBagConstraints4.gridy = 1;
			messageContentLabel = new JLabel();
			messageContentLabel.setText("JLabel");
			messageContentLabel.setFont(new Font(Font.DIALOG, Font.PLAIN, 12));
			messageContentLabel.setDisplayedMnemonic(KeyEvent.VK_UNDEFINED);
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 0;
			gridBagConstraints3.anchor = GridBagConstraints.WEST;
			gridBagConstraints3.insets = new Insets(3, 3, 3, 0);
			gridBagConstraints3.gridy = 1;
			messageLabel = new JLabel();
			messageLabel.setText("Message: ");
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 2;
			gridBagConstraints2.anchor = GridBagConstraints.WEST;
			gridBagConstraints2.insets = new Insets(3, 0, 0, 0);
			gridBagConstraints2.gridy = 3;
			sourceAfterLabel = new JLabel();
			sourceAfterLabel.setText("JLabel");
			sourceAfterLabel.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.gridwidth = 3;
			gridBagConstraints1.insets = new Insets(0, 3, 3, 0);
			gridBagConstraints1.gridy = 0;
			headingLabel = new JLabel();
			headingLabel.setFont(new Font(Font.DIALOG, Font.BOLD, 12));
			headingLabel.setText("The Newick string in the file you are trying to open seems to contain a syntax error.");
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 1;
			gridBagConstraints.anchor = GridBagConstraints.EAST;
			gridBagConstraints.insets = new Insets(3, 3, 0, 0);
			gridBagConstraints.gridy = 3;
			sourceBeforeLabel = new JLabel();
			sourceBeforeLabel.setText("JLabel");
			sourceBeforeLabel.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
			messagePanel = new JPanel();
			messagePanel.setLayout(new GridBagLayout());
			messagePanel.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
			messagePanel.setBorder(BorderFactory.createTitledBorder(null, "", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			messagePanel.add(sourceBeforeLabel, gridBagConstraints);
			messagePanel.add(headingLabel, gridBagConstraints1);
			messagePanel.add(sourceAfterLabel, gridBagConstraints2);
			messagePanel.add(messageLabel, gridBagConstraints3);
			messagePanel.add(messageContentLabel, gridBagConstraints4);
			messagePanel.add(positionLabel, gridBagConstraints5);
			messagePanel.add(positionContentLabel, gridBagConstraints6);
			messagePanel.add(sourceLabel, gridBagConstraints7);
			messagePanel.add(positionArrowLabel, gridBagConstraints8);
		}
		return messagePanel;
	}
}