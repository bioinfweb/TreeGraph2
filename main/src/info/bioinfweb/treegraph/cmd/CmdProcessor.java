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
package info.bioinfweb.treegraph.cmd;


import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.text.DecimalFormat;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import info.bioinfweb.treegraph.Main;
import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.format.TextFormats;
import info.bioinfweb.treegraph.document.io.DocumentReader;
import info.bioinfweb.treegraph.document.io.ReadWriteFactory;
import info.bioinfweb.treegraph.document.io.ReadWriteFormat;
import info.bioinfweb.treegraph.document.nodebranchdata.BranchLengthAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.TextLabelAdapter;
import info.bioinfweb.treegraph.gui.mainframe.MainFrame;
import info.webinsel.updatecenter.dataxchange.VersionList;
import info.webinsel.updatecenter.dataxchange.VersionListXMLReader;
import info.webinsel.util.CommandLineReader;
import info.webinsel.util.Math2;
import info.webinsel.util.appversion.ApplicationVersion;



public class CmdProcessor {
	public static final String LAST_VERSION_CHECK_PREF_KEY = "lastVersionCheck";
	public static final long VERSION_CHECK_INTERVAL = 1000 * 60 * 60;  // 1 h
	
	public static final String VERSION_OPTION = "-version";
	public static final String CONVERT_OPTION = "-convert";
	public static final String XTG_OPTION = "-xtg";
	public static final String NEWICK_OPTION = "-newick";
	public static final String NEXUS_OPTION = "-nexus";
	public static final String IMAGE_OPTION = "-image";

	public static final String DEFAULT_LABEL_ID = "internals";
	
	
	private File initialFile = null;
  
  
	public File getInitialFile() {
		return initialFile;
	}
	
	
	private void checkUpdate() {
		try {
			String lastChecked = Main.getInstance().getPreferences().get(LAST_VERSION_CHECK_PREF_KEY, null);
			if ((lastChecked == null) || (Math2.parseDouble(lastChecked) + VERSION_CHECK_INTERVAL <= System.currentTimeMillis())) {
				VersionList list = new VersionListXMLReader().read(new URL(Main.LATEST_VERSION_URL).openStream());
				ApplicationVersion remoteVersion = list.getEntries().get(0).getVersion();
				if (Main.getInstance().getVersion().getBuildNumber() < remoteVersion.getBuildNumber()) {
					if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(null, 
							"There is a newer version of TreeGraph 2 available for download.\n" +
							"(You are using " + Main.getInstance().getVersion() + " and could update to " + remoteVersion + ".)\n\n" +
							"Do you want to go to the download page now?", "Newer version available", 
							JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE)) {
						
						Main.getInstance().getWikiHelp().setPage(Main.DOWNLOAD_URL);
					}
					Main.getInstance().getPreferences().put(LAST_VERSION_CHECK_PREF_KEY, "" + System.currentTimeMillis());
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();  //TODO evtl. spezielle Ausgabe oder gar keine Ausgabe
		}
	}


	private void startUI() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		checkUpdate();
		SwingUtilities.invokeLater(MainFrame.getInstance());
  }
	
	
	private void convert(CommandLineReader reader) {
		File source = new File(reader.getArg(1));
		boolean readingComplete = false;
		try {
			DocumentReader documentReader = ReadWriteFactory.getInstance().getReader(source);
			if (reader != null) {
				Document document = documentReader.read(source, CmdLoadLogger.getInstance(), 
						new TextLabelAdapter(DEFAULT_LABEL_ID, 
								new DecimalFormat(TextFormats.DEFAULT_DECIMAL_FORMAT_EXPR)), 
						BranchLengthAdapter.getSharedInstance());
				readingComplete = true;
				ReadWriteFormat format = null;
				if (XTG_OPTION.equals(reader.getArg(2))) {
					format = ReadWriteFormat.XTG;
				}
				else if (NEWICK_OPTION.equals(reader.getArg(2))) {
					format = ReadWriteFormat.NEWICK;
				}
				else if (NEXUS_OPTION.equals(reader.getArg(2))) {
					format = ReadWriteFormat.NEXUS;
				}
				
				if (format != null) {
					File dest = new File(reader.getArg(3));
					if (dest.exists()) {
						System.out.println("The file \"" + dest.getAbsolutePath() + "\" already exists. Output aborted.");
					}
					else {
						ReadWriteFactory.getInstance().getWriter(format).write(document, dest);
						System.out.println("Output to \"" + reader.getArg(3) + "\" completed.");
					}
				}
				else {
					System.out.println("\"" + reader.getArg(2) + "\" is not a valid target format.");
					System.out.println("(See http://treegraph.bioinfweb.info/Help/wiki/Command_line_options for details.)");
				}
			}
		}
		catch (FileNotFoundException e) {
			String path;
			if (readingComplete) {
				path = reader.getArg(3);
			}
			else {
				path = source.getAbsolutePath();
			}
			System.out.println("The file \"" + path + "\" could not be found.");
		}
		catch (Exception e) {
			System.out.println("The exception \"" + e.toString() + "\" occurred.");
		}
	}
	
	
  public void process(CommandLineReader reader) {
		if ((reader.argCount() > 0) && reader.getArg(0).equals(VERSION_OPTION)) {
			System.out.println(Main.getInstance().getVersion());
		}
		else if ((reader.argCount() >= 3) && 
				(reader.getArg(0).toLowerCase().equals(CONVERT_OPTION))) {
			
			convert(reader);
		}
		else if ((reader.argCount() >= 3) && 
				(reader.getArg(0).toLowerCase().equals(CmdProcessor.IMAGE_OPTION))) {
			
			ImageGenerator.generate(reader);
		}
		else {
			//TODO Andere Instanz abfragen und evtl. benachrichtigen
			if ((reader.argCount() == 1)) {
				initialFile = new File(reader.getArg(0));
			}
			startUI();
		}
  }
}