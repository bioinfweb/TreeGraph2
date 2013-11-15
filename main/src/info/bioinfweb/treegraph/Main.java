/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2013  Ben Stöver, Kai Müller
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
package info.bioinfweb.treegraph;


import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.SplashScreen;

import info.bioinfweb.treegraph.cmd.CmdProcessor;
import info.bioinfweb.treegraph.document.clipboard.TreeClipboard;
import info.webinsel.errorreport.swing.SwingErrorReporter;
import info.webinsel.util.CommandLineReader;
import info.webinsel.util.ProgramMainClass;
import info.webinsel.util.appversion.ApplicationType;
import info.webinsel.util.appversion.ApplicationVersion;
import info.webinsel.util.io.DefaultNameManager;
import info.webinsel.wikihelp.client.SwingErrorHandler;
import info.webinsel.wikihelp.client.WikiHelp;



/**
 * This is the main class that starts TreeGraph 2. It is implemented as a singleton.
 * 
 * @author Ben St&ouml;ver
 */
public class Main extends ProgramMainClass {
	public static final String CONFIG_DIR = "config" + System.getProperty("file.separator");
	
	public static final String TG_URL = "http://treegraph.bioinfweb.info/"; 
	public static final String ERROR_URL = TG_URL + "errorreport/ApplicationReport.jsp"; 
	public static final String WIKI_URL = TG_URL + "Help/";
	public static final String LATEST_VERSION_URL = TG_URL + "UpdateCenter/App/VersionList.jsp";
	public static final String DOWNLOAD_URL = TG_URL + "UpdateCenter/App/DownloadPage.jsp"; 
	
	public static final Font SPLASH_SCREEN_FONT = new Font(Font.DIALOG, Font.PLAIN, 12); 
	public static final Color SPLASH_SCREEN_FONT_COLOR = Color.BLACK; 
	public static final int SPLASH_SCREEN_BORDER = 4; 
	
	
	private static Main firstInstance = null;

	private SwingErrorReporter errorReporter;
	private WikiHelp wikiHelp = new WikiHelp(WIKI_URL, new SwingErrorHandler(WIKI_URL));
	private TreeClipboard clipboard = new TreeClipboard();
	private CmdProcessor cmdProcessor = new CmdProcessor();
	private DefaultNameManager nameManager = new DefaultNameManager("NewTree");
	
	
	private Main() {
		super(new ApplicationVersion(2, 0, 49, 276, ApplicationType.BETA));
		errorReporter = new SwingErrorReporter(ERROR_URL, getVersion());
	}
	
	
	public static Main getInstance() {
		if (firstInstance == null) {
			firstInstance = new Main();
		}
		return firstInstance;
	}
	
	
	public SwingErrorReporter getErrorReporter() {
		return errorReporter;
	}


	public CmdProcessor getCmdProcessor() {
		return cmdProcessor;
	}


	public WikiHelp getWikiHelp() {
		return wikiHelp;
	}


	public TreeClipboard getClipboard() {
		return clipboard;
	}


	public DefaultNameManager getNameManager() {
		return nameManager;
	}


	private void customizeSpashScreen() {
		//TODO Warum funktioniert das nicht?
		SplashScreen splash = SplashScreen.getSplashScreen();
		if (splash != null) {
			Graphics2D g = (Graphics2D)splash.createGraphics();
			if (g != null) {
				Dimension size = splash.getSize();
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g.setComposite(AlphaComposite.Clear);
				g.setPaintMode();
				
				FontMetrics fm = g.getFontMetrics();
				String text = getVersion().toString();
				g.drawString(text, (int)size.getWidth() - fm.stringWidth(text) - SPLASH_SCREEN_BORDER,
						(int)size.getHeight() - fm.getHeight() - SPLASH_SCREEN_BORDER);
				splash.update();
				//System.out.println(((int)size.getHeight() - fm.getHeight() - SPLASH_SCREEN_BORDER) + " " + ((int)size.getWidth() - fm.stringWidth(text) - SPLASH_SCREEN_BORDER));
			}
		}
	}
	
	
	private void startApplication(String[] args) {
		Thread.setDefaultUncaughtExceptionHandler(getErrorReporter());
		System.setProperty("sun.awt.exception.handler", AWTExceptionHandler.class.getName());  // May not work in future Java versions.
		
		customizeSpashScreen();
		cmdProcessor.process(new CommandLineReader(args));
	}
	
	
	/**
	 * Start this application
	 * @param args - the command line parameters
	 */
	public static void main(String[] args) {
		getInstance().startApplication(args);
	}
}