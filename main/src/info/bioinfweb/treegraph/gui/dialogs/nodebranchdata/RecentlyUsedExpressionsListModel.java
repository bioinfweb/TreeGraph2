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
package info.bioinfweb.treegraph.gui.dialogs.nodebranchdata;


import info.bioinfweb.commons.SystemUtils;
import info.bioinfweb.treegraph.Main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.DosFileAttributes;

import javax.swing.DefaultComboBoxModel;
import javax.swing.MutableComboBoxModel;



/**
 * Manages the list of recently used expressions.
 * 
 * @author Ben St&ouml;ver
 * @since 2.0.24
 */
public class RecentlyUsedExpressionsListModel extends DefaultComboBoxModel<String> implements MutableComboBoxModel<String> {
	public static final String FILE_NAME_PREFIX = "RecentlyUsedExpressions";
	public static final String FILE_NAME_EXTENSION = ".txt";
	public static final int MAXIMAL_COUNT = 25;
	
	
	private String fileName = "RecentlyUsedExpressions.txt";
	
	
	public RecentlyUsedExpressionsListModel(String fileNameSuffix) {
	  super();
	  this.fileName = FILE_NAME_PREFIX + fileNameSuffix + FILE_NAME_EXTENSION;
  }


	public String getFileName() {
		return Main.CONFIG_DIR + System.getProperty("file.separator") + fileName;
	}
	
	
	private void cutToSize() {
		if (getSize() > MAXIMAL_COUNT) {
			for (int i = getSize() - 1; i >= MAXIMAL_COUNT; i--) {
				removeElementAt(i);
			}
		}
	}
	
	
	/**
	 * Adds a new element to the list. If an equal element is already contained, it will be removed and the new element added to 
	 * the front.
	 */
	@Override
  public void addElement(String expression) {
		removeElement(expression);  // Possibly remove the element, if it is already contained at another position.
	  super.addElement(expression);  // Add the element to the top of the list.
		cutToSize();
  }
  
  
	/**
	 * Loads the list of recently used expressions from the TreeGraph configuration directory if the according
	 * file exists.
	 * 
	 * @throws IOException
	 */
	public void loadList() throws IOException {
		removeAllElements();
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(getFileName()));
			try {
				String line = reader.readLine();
				while (line != null) {
					addElement(line);
					line = reader.readLine();
				}
				cutToSize();
			}
			finally {
				reader.close();
			}
		}
		catch (FileNotFoundException e) {}  // list will remain empty then
	}
	
	
	/**
	 * Writes the list of recently used expressions to the TreeGraph configuration directory.
	 *  
	 * @throws IOException
	 */
	public void saveList() throws IOException {
		File file = new File(getFileName());
		if (file.getParentFile().mkdirs() && SystemUtils.IS_OS_WINDOWS) {
			Path path = Paths.get(file.getParent());
			try {
				Files.setAttribute(path, "dos:hidden", true);
			}
			catch (UnsupportedOperationException e) {}
		}
		
		PrintWriter writer = new PrintWriter(file);
		try {
			for (int i = 0; i < getSize(); i++) {
				writer.println(getElementAt(i));
			}
		}
		finally {
			writer.close();
		}
	}
}