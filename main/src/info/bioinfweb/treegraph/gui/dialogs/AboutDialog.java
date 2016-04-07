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


import info.bioinfweb.treegraph.Main;
import info.bioinfweb.commons.io.TextReader;
import javax.swing.JPanel;

import java.awt.Desktop;
import java.awt.Frame;
import javax.swing.JDialog;
import javax.swing.JTabbedPane;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.HyperlinkEvent.EventType;

import java.awt.Insets;



/**
 * The about dialog of TreeGraph 2.
 * 
 * @author Ben St&ouml;ver
 */
public class AboutDialog extends JDialog {
	public static final String RESOURCES_PATH = "/resources/about/";  //  @jve:decl-index=0:
	
	private static final long serialVersionUID = 1L;
	
	public final HyperlinkListener HYPERLINK_LISTENER = 		
		  new javax.swing.event.HyperlinkListener() {
					public void hyperlinkUpdate(javax.swing.event.HyperlinkEvent e) {
						if (e.getEventType().equals(EventType.ACTIVATED)) {
							try {
								Desktop.getDesktop().browse(e.getURL().toURI());
							}
							catch (Exception ex) {
								JOptionPane.showMessageDialog(getOwner(), 
										"An error occurred when trying open the selected link.", 
										"Navigation failed,", JOptionPane.ERROR_MESSAGE);
							}
						}
					}
				};
	
	
	private JPanel jContentPane = null;
	private JTabbedPane contentsTabbedPane = null;
	private JPanel generalPanel = null;
	private JPanel gplPanel = null;
	private JPanel apachePanel = null;
	private JLabel batikLabel = null;
	private JPanel buttonPanel = null;
	private JButton closeButton = null;
	private JEditorPane apacheTextArea = null;
	private JScrollPane apacheScrollPane = null;
	private JScrollPane gplScrollPane = null;
	private JEditorPane gplEditorPane = null;
	private JScrollPane generalScrollPane = null;
	private JEditorPane generalEditorPane = null;
	
	
	/**
	 * @param owner
	 */
	public AboutDialog(Frame owner) {
		super(owner);
		initialize();
		setLocationRelativeTo(owner);
	}
	
	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		setSize(700, 600);
		setContentPane(getJContentPane());
		setTitle("About TreeGraph 2");
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
			jContentPane.add(getContentsTabbedPane(), null);
			jContentPane.add(getButtonPanel(), null);
		}
		return jContentPane;
	}


	/**
	 * This method initializes contentsTabbedPane	
	 * 	
	 * @return javax.swing.JTabbedPane	
	 */
	private JTabbedPane getContentsTabbedPane() {
		if (contentsTabbedPane == null) {
			contentsTabbedPane = new JTabbedPane();
			contentsTabbedPane.addTab("Info", getGeneralPanel());
			contentsTabbedPane.addTab("General public lincence", getGPLPanel());
			contentsTabbedPane.addTab("Apache licence", getApachePanel());
		}
		return contentsTabbedPane;
	}


	/**
	 * This method initializes generalPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getGeneralPanel() {
		if (generalPanel == null) {
			generalPanel = new JPanel();
			generalPanel.setLayout(new BoxLayout(getGeneralPanel(), BoxLayout.Y_AXIS));
			generalPanel.add(getGeneralScrollPane(), null);
		}
		return generalPanel;
	}


	/**
	 * This method initializes gplPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getGPLPanel() {
		if (gplPanel == null) {
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.fill = GridBagConstraints.BOTH;
			gridBagConstraints2.weighty = 1.0;
			gridBagConstraints2.weightx = 1.0;
			gplPanel = new JPanel();
			gplPanel.setLayout(new GridBagLayout());
			gplPanel.add(getGplScrollPane(), gridBagConstraints2);
		}
		return gplPanel;
	}


	/**
	 * This method initializes apachePanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getApachePanel() {
		if (apachePanel == null) {
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.fill = GridBagConstraints.BOTH;
			gridBagConstraints1.gridy = 1;
			gridBagConstraints1.ipadx = 0;
			gridBagConstraints1.ipady = -2564;
			gridBagConstraints1.weightx = 1.0;
			gridBagConstraints1.weighty = 1.0;
			gridBagConstraints1.gridx = 0;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.insets = new Insets(0, 0, 0, 0);
			gridBagConstraints.gridy = 0;
			gridBagConstraints.ipadx = -610;
			gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints.weightx = 1.0;
			gridBagConstraints.gridx = 0;
			batikLabel = new JLabel();
			batikLabel.setText("<html>TreeGraph uses the <a href=\"http://xmlgraphics.apache.org/batik/\">Batik SVG Toolkit</a> (http://xmlgraphics.apache.org/batik/) from the Apache XML Graphics Project wich is included in this release. It is distributed under Apache Public Licence which you can read below:</html>");
			apachePanel = new JPanel();
			apachePanel.setLayout(new GridBagLayout());
			apachePanel.add(batikLabel, gridBagConstraints);
			apachePanel.add(getApacheScrollPane(), gridBagConstraints1);
		}
		return apachePanel;
	}


	/**
	 * This method initializes buttonPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getButtonPanel() {
		if (buttonPanel == null) {
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.insets = new Insets(3, 3, 3, 3);
			gridBagConstraints3.weighty = 1.0;
			gridBagConstraints3.weightx = 1.0;
			buttonPanel = new JPanel();
			buttonPanel.setLayout(new GridBagLayout());
			buttonPanel.add(getCloseButton(), gridBagConstraints3);
		}
		return buttonPanel;
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
					setVisible(false);
				}
			});
		}
		return closeButton;
	}


	/**
	 * This method initializes apacheTextArea	
	 * 	
	 * @return javax.swing.JTextArea	
	 */
	private JEditorPane getApacheTextArea() {
		if (apacheTextArea == null) {
			String text;
			try {
				text = TextReader.readText(Object.class.getResource("/resources/about/Apache.txt"));
			}
			catch (IOException e) {
				text = "<html><body>Unable to read licence file. Licence is available at <a href=\"http://xmlgraphics.apache.org/batik/license.html\">http://xmlgraphics.apache.org/batik/license.html</a>.</body></html>";
			}
			
			apacheTextArea = new JEditorPane("text/text", text);
			apacheTextArea.setCaretPosition(0);
			apacheTextArea.setEnabled(false);
		}
		return apacheTextArea;
	}


	/**
	 * This method initializes apacheScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getApacheScrollPane() {
		if (apacheScrollPane == null) {
			apacheScrollPane = new JScrollPane();
			apacheScrollPane.setViewportView(getApacheTextArea());
		}
		return apacheScrollPane;
	}


	/**
	 * This method initializes gplScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getGplScrollPane() {
		if (gplScrollPane == null) {
			gplScrollPane = new JScrollPane();
			gplScrollPane.setViewportView(getGplEditorPane());
		}
		return gplScrollPane;
	}


	/**
	 * This method initializes gplTextArea	
	 * 	
	 * @return javax.swing.JTextArea	
	 */
	private JEditorPane getGplEditorPane() {
		if (gplEditorPane == null) {
			String text;
			try {
				text = TextReader.readText(Object.class.getResource("/resources/about/GPL.html"));
			}
			catch (IOException e) {
				text = "<html>Unable to read license file. Licence is available at " +
						"<a href=\"http://treegraph.web-insel.info/License\">" +
						"http://treegraph.bioinfweb.info/License</a>.</html>";
			}
			gplEditorPane = new JEditorPane();
			gplEditorPane.setContentType("text/html");
			gplEditorPane.setText(text);
			gplEditorPane.setCaretPosition(0);
			gplEditorPane.setEditable(false);
			gplEditorPane.addHyperlinkListener(HYPERLINK_LISTENER);
		}
		return gplEditorPane;
	}


	/**
	 * This method initializes generalScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getGeneralScrollPane() {
		if (generalScrollPane == null) {
			generalScrollPane = new JScrollPane();
			generalScrollPane.setViewportView(getGeneralEditorPane());
		}
		return generalScrollPane;
	}


	private static String getResourcePath(String file) {
		return AboutDialog.class.getResource(RESOURCES_PATH + file).toString();
	}
	
	
	/**
	 * This method initializes generalEditorPane	
	 * 	
	 * @return javax.swing.JEditorPane	
	 */
	private JEditorPane getGeneralEditorPane() {
		if (generalEditorPane == null) {
			generalEditorPane = new JEditorPane();
			generalEditorPane.setContentType("text/html");
			generalEditorPane.setText("<html>" +
					"<head><link rel='stylesheet' type='text/css' href='" + 
					    getResourcePath("Style.css") + "'></head>" +
					"<body>" +
					"<h1>TreeGraph " + Main.getInstance().getVersion().toString() + "</h1>" +
					"<p>Development: <a href='http://bioinfweb.info/People/Stoever'>Ben St&ouml;ver</a>, " +
							"<a href='http://bioinfweb.info/People/Wiechers'>Sarah Wiechers</a>, " + 
					    "<a href='http://bioinfweb.info/People/Mueller'>Kai M&uuml;ller</a><br />" +
					"Website: <a href='http://treegraph.bioinfweb.info/'>treegraph.bioinfweb.info</a><br />" +
					"Copyright 2007-2016 Ben St&ouml;ver, Sarah Wiechers, Kai M&uuml;ller. All rights reserved.</p>" +
					
					"<p><b>Publication:</b><br /> St&ouml;ver B C, M&uuml;ller K F: " +
					    "<a href='http://dx.doi.org/10.1186/1471-2105-11-7'>TreeGraph 2: Combining and " +
					    "visualizing evidence from different phylogenetic analyses.</a> " +
					    "<i>BMC Bioinformatics</i> 2010, <b>11</b>:7<br/>" +
					    "More publications can be found on the <a href='http://treegraph.bioinfweb.info/Publications'>website</a>.</p>" +
					
					"<p>This program is free software: you can redistribute it and/or modify it " +
					"under the terms of the GNU General Public License (see General Public " +
					"License tab) as published by the Free Software Foundation, either version 3 " +
					"of the License, or (at your option) any later version.</p>" +
					
					"<p>This program is distributed in the hope that it will be useful, but " +
					"WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY " +
					"or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for " +
					"more details.</p>" +
					
  				"<p>The included Apache Commons, Batic and l2fprod-common Libraries are distributed under " +
					"Apache Public Licence (see Apache Public Licence tab).</p>" +
					
					"<p><b>The following libraries are used by TreeGraph 2:</b></p>" +
					"<ul>" +
						"<li>bioinfweb.commons.java (<a href='http://commons.bioinfweb.info/Java/'>http://commons.bioinfweb.info/Java/</a>)</li>" +
					  "<li>Apache Commons (<a href='http://commons.apache.org/'>http://commons.apache.org/</a>)</li>" +
					  "<li>Apache Batik SVG Toolkit (<a href='http://xmlgraphics.apache.org/batik/'>http://xmlgraphics.apache.org/batik/</a>)</li>" +
					  "<li>FreeHEP Java Libraries (<a href='http://java.freehep.org/'>http://java.freehep.org/</a>)</li>" +
					  "<li>Java Math Expression Parser (<a href='http://sourceforge.net/projects/jep/'>http://sourceforge.net/projects/jep/</a>)</li>" +
					  "<li>Tango Desktop Project (<a href='http://tango.freedesktop.org/'>http://tango.freedesktop.org/</a>)</li>" +
					"</ul>" +
					"<p>See <a href='http://treegraph.bioinfweb.info/Development/Libraries'>here</a> for more information." +
					"</body></html>");			
			generalEditorPane.setCaretPosition(0);
			generalEditorPane.setEditable(false);
			generalEditorPane.addHyperlinkListener(HYPERLINK_LISTENER);
		}
		return generalEditorPane;
	}
}