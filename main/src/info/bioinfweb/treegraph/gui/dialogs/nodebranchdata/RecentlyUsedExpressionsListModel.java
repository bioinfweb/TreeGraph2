/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2011, 2013-2016  Ben Stöver, Sarah Wiechers, Kai Müller
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


import info.bioinfweb.commons.io.IOUtils;
import info.bioinfweb.treegraph.Main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.ListModel;



/**
 * Manages the list of recently used expressions.
 * @author Ben St&ouml;ver
 * @since 2.0.24
 */
public class RecentlyUsedExpressionsListModel extends AbstractListModel<String> implements ListModel<String> {
	public static final String FILE_NAME = "RecentlyUsedExpressions.txt";
	public static final int MAXIMAL_COUNT = 25;
	
	
	private List<String> expressions = new ArrayList<String>();
	
	
	public RecentlyUsedExpressionsListModel() {
		super();
	}
	
	
	public String getFileName() {
		return IOUtils.getClassDir(getClass()) + System.getProperty("file.separator") + Main.CONFIG_DIR + 
		    FILE_NAME;
	}
	
	
	private void cutToSize() {
		if (expressions.size() > MAXIMAL_COUNT) {
			int formerSize = expressions.size();
			for (int i = expressions.size() - 1; i >= MAXIMAL_COUNT; i--) {
				expressions.remove(i);
			}
			fireIntervalRemoved(this, expressions.size(), formerSize - 1);
		}
	}
	
	
	/**
	 * Adds <code>expression</code> to the list. If it is already contained, it will be moved to the front.
	 * @param expression
	 */
	public void addExpression(String expression) {
		int pos = expressions.indexOf(expression);
		if (pos != -1) {
			expressions.remove(pos);
			fireIntervalRemoved(this, pos, pos);
		}
		expressions.add(0, expression);
		fireIntervalAdded(this, 0, 0);
		cutToSize();
	}
	
	
	/**
	 * Loads the list of recently used expressions from the TreeGraph configuration directory if the according
	 * file exists. 
	 * @throws IOException
	 */
	public void loadList() throws IOException {
		int size = getSize();
		expressions.clear();
		fireIntervalRemoved(this, 0, size - 1);
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(getFileName()));
			try {
				String line = reader.readLine();
				while (line != null) {
					expressions.add(line);
					line = reader.readLine();
				}
				cutToSize();
			}
			finally {
				fireIntervalAdded(this, 0, getSize() - 1);
				reader.close();
			}
		}
		catch (FileNotFoundException e) {}  // list will remain empty then
	}
	
	
	/**
	 * Writes the list of recently used expressions to the TreeGraph configuration directory. 
	 * @throws IOException
	 */
	public void saveList() throws IOException {
		File file = new File(getFileName());
		file.getParentFile().mkdirs();
		PrintWriter writer = new PrintWriter(file);
		try {
			for (int i = 0; i < expressions.size(); i++) {
				writer.println(expressions.get(i));
			}
		}
		finally {
			writer.close();
		}
	}
	

	public String getElementAt(int pos) {
		return expressions.get(pos);
	}

	
	public int getSize() {
		return expressions.size();
	}
}