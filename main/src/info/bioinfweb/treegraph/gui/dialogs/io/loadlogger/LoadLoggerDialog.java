/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2015-2016  Ben Stöver, Sarah Wiechers
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
package info.bioinfweb.treegraph.gui.dialogs.io.loadlogger;


import info.bioinfweb.treegraph.document.io.DocumentReader;
import info.bioinfweb.treegraph.gui.mainframe.MainFrame;
import info.bioinfweb.commons.log.ApplicationLogger;
import info.bioinfweb.commons.log.ApplicationLoggerMessage;
import info.bioinfweb.commons.log.JTextAreaApplicationLogger;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.FlowLayout;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.JTextArea;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;



/**
 * A dialog displaying messages from classes implementing {@link DocumentReader}. It is implemented
 * as a singleton.
 * 
 * @since 2.0.42
 * @author Ben St&ouml;ver
 */
public class LoadLoggerDialog extends JDialog implements ApplicationLogger {
	private static final long serialVersionUID = 1L;
	
	private static LoadLoggerDialog firstInstance = null;
	
	
	private ApplicationLogger logger = null;
	private JPanel jContentPane = null;
	private JScrollPane messagesScrollPane = null;
	private JPanel buttonsPanel = null;
	private JButton closeButton = null;
	private JTextArea textArea;

	
	/**
	 * @param owner
	 */
	private LoadLoggerDialog(Frame owner) {
		super(owner, true);
		initialize();
	}
	
	
	/**
	 * Returns the singleton instance of this class. This method must not be called from 
	 * {@link MainFrame#getInstance()}.
	 * 
	 * @return the singleton instance of this class
	 */
	public static LoadLoggerDialog getInstance() {
		if (firstInstance == null) {
			firstInstance = new LoadLoggerDialog(MainFrame.getInstance());
		}
		return firstInstance;
	}

	
	public void addMessage(ApplicationLoggerMessage message) {
		getLogger().addMessage(message);
	}


	public void addMessage(String message, int helpCode) {
		getLogger().addMessage(message, helpCode);
	}


	public void addMessage(String message) {
		getLogger().addMessage(message);
	}


	public void addWarning(String message, int helpCode) {
		getLogger().addWarning(message, helpCode);
	}


	public void addWarning(String message) {
		getLogger().addWarning(message);
	}
	
	
	@Override
	public void addError(String message) {
		getLogger().addError(message);
	}


	public void addError(String message, int helpCode) {
		getLogger().addError(message, helpCode);
	}


	public void addError(Throwable throwable, boolean includeStackTrace) {
		getLogger().addError(throwable, includeStackTrace);
	}


	public void addError(Throwable throwable, boolean includeStackTrace, int helpCode) {
		getLogger().addError(throwable, includeStackTrace, helpCode);
	}


	public void clearMessages() {
		getTextArea().setText("");
	}	
	
	
	/**
	 * Packs, positions and shows this modal dialog if the message list is not empty.
	 * The message list is cleared after the dialog is closed.
	 */
	public void display() {
		if (getTextArea().getText().length() > 0) {
			//pack();
			setLocationRelativeTo(getOwner());
			setVisible(true);
		}
	}
	
	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		setTitle("File opened with messages");
		setSize(new Dimension(600, 400));
		setContentPane(getJContentPane());
	}

	
	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			GridBagLayout gbl_jContentPane = new GridBagLayout();
			gbl_jContentPane.columnWeights = new double[]{0.0, Double.MIN_VALUE};
			gbl_jContentPane.rowWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
			jContentPane.setLayout(gbl_jContentPane);
			GridBagConstraints gbc_messagesScrollPane = new GridBagConstraints();
			gbc_messagesScrollPane.weighty = 1.0;
			gbc_messagesScrollPane.weightx = 1.0;
			gbc_messagesScrollPane.fill = GridBagConstraints.BOTH;
			gbc_messagesScrollPane.insets = new Insets(0, 0, 5, 0);
			gbc_messagesScrollPane.gridx = 0;
			gbc_messagesScrollPane.gridy = 0;
			jContentPane.add(getMessagesScrollPane(), gbc_messagesScrollPane);
			GridBagConstraints gbc_buttonsPanel = new GridBagConstraints();
			gbc_buttonsPanel.anchor = GridBagConstraints.SOUTH;
			gbc_buttonsPanel.fill = GridBagConstraints.HORIZONTAL;
			gbc_buttonsPanel.gridx = 0;
			gbc_buttonsPanel.gridy = 1;
			jContentPane.add(getButtonsPanel(), gbc_buttonsPanel);
		}
		return jContentPane;
	}


	/**
	 * This method initializes messagesScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getMessagesScrollPane() {
		if (messagesScrollPane == null) {
			messagesScrollPane = new JScrollPane();
			messagesScrollPane.setViewportView(getTextArea());
		}
		return messagesScrollPane;
	}


	/**
	 * This method initializes buttonsPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getButtonsPanel() {
		if (buttonsPanel == null) {
			buttonsPanel = new JPanel();
			buttonsPanel.setLayout(new FlowLayout());
			buttonsPanel.setEnabled(false);
			buttonsPanel.add(getCloseButton(), null);
		}
		return buttonsPanel;
	}


	/**
	 * This method initializes closeButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getCloseButton() {
		if (closeButton == null) {
			closeButton = new JButton();
			closeButton.setText("Close");
			closeButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					clearMessages();  // List shall be empty so that the dialog can be reused.
					setVisible(false);
				}
			});
		}
		return closeButton;
	}
	
	
	public ApplicationLogger getLogger() {
		if (logger == null) {
			logger = new JTextAreaApplicationLogger(getTextArea());
		}
		return logger;
	}
	
	
	private JTextArea getTextArea() {
		if (textArea == null) {
			textArea = new JTextArea();
			textArea.setWrapStyleWord(true);
			textArea.setLineWrap(true);
			textArea.setEditable(false);
		}
		return textArea;
	}
}