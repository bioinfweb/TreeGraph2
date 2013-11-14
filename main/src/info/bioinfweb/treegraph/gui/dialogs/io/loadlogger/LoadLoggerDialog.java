/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2011  Ben Stöver, Kai Müller
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
import info.webinsel.util.log.AbstractApplicationLogger;
import info.webinsel.util.log.ApplicationLogger;
import info.webinsel.util.log.ApplicationLoggerMessage;

import javax.swing.JDialog;
import javax.swing.JPanel;
import java.awt.Frame;
import javax.swing.BoxLayout;

import javax.swing.JScrollPane;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JList;



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
	
	
	private ApplicationLogger logger = new AbstractApplicationLogger() {
				@Override
				public void addMessage(ApplicationLoggerMessage message) {
					getMessageListModel().add(message);
				}
			};
	
	private JPanel jContentPane = null;
	private JScrollPane messagesScrollPane = null;
	private JPanel buttonsPanel = null;
	private JButton closeButton = null;
	private JList messageList = null;

	
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
		logger.addMessage(message);
	}


	public void addMessage(String message, int helpCode) {
		logger.addMessage(message, helpCode);
	}


	public void addMessage(String message) {
		logger.addMessage(message);
	}


	public void addWarning(String message, int helpCode) {
		logger.addWarning(message, helpCode);
	}


	public void addWarning(String message) {
		logger.addWarning(message);
	}
	
	
	@Override
	public void addError(String message) {
		logger.addError(message);
	}


	public void addError(String message, int helpCode) {
		logger.addError(message, helpCode);
	}


	public void addError(Throwable throwable, boolean includeStackTrace) {
		logger.addError(throwable, includeStackTrace);
	}


	public void addError(Throwable throwable, boolean includeStackTrace, int helpCode) {
		logger.addError(throwable, includeStackTrace, helpCode);
	}


	public void clearMessages() {
		getMessageListModel().clear();
	}
	
	
	/**
	 * Packs, positions and shows this modal dialog if the message list is not empty.
	 * The message list is cleared after the dialog is closed.
	 */
	public void display() {
		if (getMessageListModel().getSize() > 0) {
			pack();
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
			jContentPane.setLayout(new BoxLayout(getJContentPane(), BoxLayout.Y_AXIS));
			jContentPane.add(getMessagesScrollPane(), null);
			jContentPane.add(getButtonsPanel(), null);
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
			messagesScrollPane.setViewportView(getMessageList());
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
					getMessageListModel().clear();  // List shall be empty so that the dialog can be reused.
					setVisible(false);
				}
			});
		}
		return closeButton;
	}


	/**
	 * This method initializes messageList	
	 * 	
	 * @return javax.swing.JList	
	 */
	private JList getMessageList() {
		if (messageList == null) {
			messageList = new JList(new MessagesListModel());
		}
		return messageList;
	}
	
	
	/**
	 * Returns the model of {@link #getMessageList()}.
	 * @return
	 */
	private MessagesListModel getMessageListModel() {
		return (MessagesListModel)getMessageList().getModel();
	}
}