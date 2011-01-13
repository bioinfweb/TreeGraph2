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
package info.bioinfweb.treegraph.gui.dialogs.io;


import info.bioinfweb.treegraph.gui.CurrentDirectoryModel;

import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;

import java.awt.Frame;
import java.awt.Font;
import java.awt.Color;
import java.io.File;



public class ImportTableDialog extends FileDialog {
	private static final long serialVersionUID = 1L;

	
	private JPanel jContentPane = null;
	private JPanel filePanel = null;
	private TableSeparatorPanel separatorPanel = null;
	private JFileChooser fileChooser = null;


	/**
	 * @param owner
	 */
	public ImportTableDialog(Frame owner) {
		super(owner,FileDialog.Option.FILE_MUST_EXEST);
		initialize();
		setLocationRelativeTo(owner);
	}
	
	
	public File getSelectedFile() {
		return getFileChooser().getSelectedFile();
	}
	
	
	public char getSeparator() {
		return getSeparatorPanel().getSeparator();
	}

	
	@Override
	protected boolean onApply(File file) {
		return true;
	}


	@Override
	protected boolean onExecute() {
		return true;
	}


	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		setTitle("Import table as node/branch data");
		setContentPane(getJContentPane());
		pack();
	}

	
	@Override
	protected JFileChooser getFileChooser() {
		if (fileChooser == null) {
			fileChooser = new JFileChooser();
			fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
			fileChooser.setControlButtonsAreShown(false);
			FileFilter textFiler = new FileFilter() {
				public boolean accept(File file) {
					return file.getAbsolutePath().endsWith(".txt") || file.isDirectory();
				}
			
				
				@Override
				public String getDescription() {
					return "Text files (*.txt)";
				}
      };
			fileChooser.addChoosableFileFilter(textFiler);
			fileChooser.setFileFilter(textFiler);
			CurrentDirectoryModel.getInstance().addFileChooser(fileChooser);
		}
		return fileChooser;
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
			jContentPane.add(getFilePanel(), null);
			jContentPane.add(getSeparatorPanel(), null);
			jContentPane.add(getButtonsPanel(), null);
		}
		return jContentPane;
	}

	
	/**
	 * This method initializes filePanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getFilePanel() {
		if (filePanel == null) {
			filePanel = new JPanel();
			filePanel.setLayout(new BoxLayout(getFilePanel(), BoxLayout.Y_AXIS));
			filePanel.setBorder(BorderFactory.createTitledBorder(null, "File", 
					TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, 
					new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			filePanel.add(getFileChooser(), null);
		}
		return filePanel;
	}

	
	/**
	 * This method initializes separatorPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private TableSeparatorPanel getSeparatorPanel() {
		if (separatorPanel == null) {
			separatorPanel = new TableSeparatorPanel();
		}
		return separatorPanel;
	}
}