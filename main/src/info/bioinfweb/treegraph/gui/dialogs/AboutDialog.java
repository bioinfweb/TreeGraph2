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


import java.awt.Frame;
import java.io.IOException;

import info.bioinfweb.commons.io.TextReader;
import info.bioinfweb.treegraph.Main;



/**
 * The about dialog of <i>TreeGraph 2</i>.
 * 
 * @author Ben St&ouml;ver
 */
public class AboutDialog extends info.bioinfweb.commons.swing.AboutDialog {
	public static final int GENERAL_TAB_INDEX = 0;
	public static final int PRIVACY_POLICY_TAB_INDEX = 3;
	public static final String RESOURCES_PATH = "/resources/about/";  //  @jve:decl-index=0:
	
	private static final long serialVersionUID = 1L;
	
	//private JHTMLLabel batikLabel = null;
	
	
	/**
	 * @param owner
	 */
	public AboutDialog(Frame owner) {
		super(owner);
		addTabs();
		setSize(700, 600);
		setTitle("About TreeGraph 2");
		setLocationRelativeTo(owner);
	}
	
	
	private void addContentFromFile(String title, String path, String altURL) {
		String text;
		try {
			text = TextReader.readText(AboutDialog.class.getResource(path));
		}
		catch (IOException e) {
			text = "<html><body>Unable to read licence file. Licence is available at <a href=\"" + altURL + "\">\" + altURL + \"</a>.</body></html>";
		}
		
		addTab(title, null, "text/html", text, null);
	}
	
	
	private void addTabs() {
		addTab("General", null, "text/html", getGeneralContent(), null);
		addContentFromFile("TreeGraph 2 License", "/resources/about/GPL.html", "http://treegraph.bioinfweb.info/License");  //TODO Add link label to panel or include link in HTML.
		addContentFromFile("Apache License", "/resources/about/ApacheLicense.html", "http://www.apache.org/licenses/LICENSE-2.0.html");
		addContentFromFile("Privacy Policy", "/resources/about/PrivacyPolicy.en.html", "http://r.bioinfweb.info/TGPrivacyEN");  //TODO Create page and redirection
		addContentFromFile("Datenschutzerklärung", "/resources/about/PrivacyPolicy.de.html", "http://r.bioinfweb.info/TGPrivacyDE");  //TODO Create page and redirection
		//TODO Does only the general tab use a CSS? If so, should it be changed?
	}


	private static String getResourcePath(String file) {
		return AboutDialog.class.getResource(RESOURCES_PATH + file).toString();
	}
	
	
	private String getGeneralContent() {
			return 
					"<html>" +
					"<head><link rel='stylesheet' type='text/css' href='" + getResourcePath("Style.css") + "'></head>" +
					"<body>" +
					"<h1><i>TreeGraph " + Main.getInstance().getVersion().toString() + "</i></h1>" +
					"<p>Development: <a href='http://bioinfweb.info/People/Stoever'>Ben St&ouml;ver</a>, " +
							"<a href='http://bioinfweb.info/People/Wiechers'>Sarah Wiechers</a>, " + 
					    "<a href='http://bioinfweb.info/People/Mueller'>Kai M&uuml;ller</a><br />" +
					"Copyright 2007-2011, 2013-2019 Ben St&ouml;ver, Sarah Wiechers, Kai M&uuml;ller. All rights reserved.</p>" +
					"<p>Website: <a href='http://treegraph.bioinfweb.info/'>treegraph.bioinfweb.info</a><br />" +
					"ResearchGate: <a href='http://r.bioinfweb.info/RGTreeGraph2'>r.bioinfweb.info/RGTreeGraph2</a><br />" +
					"GitHub: <a href='https://github.com/bioinfweb/TreeGraph2'>github.com/bioinfweb/TreeGraph2</a><br />" +
					"Twitter: <a href='http://twitter.com/bioinfweb'>twitter.com/bioinfweb</a></p>" +
					
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
					
  				"<p>The included libraries Apache Commons, Batic and l2fprod-common are distributed under " +
					    "Apache Licence (see Apache Licence tab).</p>" +
					
					"<p><b>The following libraries are used by <i>TreeGraph 2</i>:</b></p>" +
					"<ul>" +
					  "<li><i>bioinfweb.commons.java</i> (<a href='http://commons.bioinfweb.info/Java/'>http://commons.bioinfweb.info/Java/</a>)</li>" +
					  "<li><i>JPhyloIO</i> (<a href='http://bioinfweb.info/JPhyloIO/'>http://bioinfweb.info/JPhyloIO/</a>)</li>" +
					  "<li><i>Apache Commons</i> (<a href='http://commons.apache.org/'>http://commons.apache.org/</a>)</li>" +
					  "<li><i>Apache Batik SVG Toolkit</i> (<a href='http://xmlgraphics.apache.org/batik/'>http://xmlgraphics.apache.org/batik/</a>)</li>" +
					  "<li><i>FreeHEP Java Libraries</i> (<a href='http://java.freehep.org/'>http://java.freehep.org/</a>)</li>" +
					  "<li><i>Java Math Expression Parser</i> (<a href='http://sourceforge.net/projects/jep/'>http://sourceforge.net/projects/jep/</a>)</li>" +
					  "<li><i>Tango Desktop Project</i> (<a href='http://tango.freedesktop.org/'>http://tango.freedesktop.org/</a>)</li>" +
					"</ul>" +
					"<p>See <a href='http://treegraph.bioinfweb.info/Development/Libraries'>here</a> for more information." +
					"</body></html>";			
	}
}