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
package info.bioinfweb.treegraph.gui.dialogs.io;


import java.awt.Dialog;
import java.awt.Frame;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.plaf.basic.BasicFileChooserUI;

import info.bioinfweb.treegraph.gui.CurrentDirectoryModel;
import info.bioinfweb.treegraph.gui.dialogs.EditDialog;



/**
 * All dialogs that deal with files should be inherited from this class.
 * 
 * @author Ben St&ouml;ver
 */
public abstract class FileDialog extends EditDialog {
	public enum Option {
		NONE, FILE_MUST_EXEST, ASK_TO_OVERWRITE;
	}
	
	
	private Option option = Option.NONE;
	
	
	public FileDialog(Dialog owner, Option option) {
		super(owner);
		this.option = option;
		init();
	}

	
	public FileDialog(Frame owner, Option option) {
		super(owner);
		this.option = option;
		init();
	}
	
	
	private void init() {
		CurrentDirectoryModel.getInstance().addFileChooser(getFileChooser());
	}
	
	
	protected abstract JFileChooser getFileChooser();
	
	
	/**
	 * Override this method if you e.g. want to add a default extension. (Note that this method
	 * could also return <code>null</code> if no file is selected.)
	 * @return the selected file or <code>null</code>
	 */
	protected File getSelectedFile() {
		return getFileChooser().getSelectedFile();
	}
	
	
	/**
	 * This method is called in the implementation of apply if an valid file was selected.
	 * @param file - the file selected by the user
	 * @return
	 */
	protected abstract boolean onApply(File file);
	
	
	@Override
	protected boolean apply() {
		if (getFileChooser().getUI() instanceof BasicFileChooserUI) {
      ((BasicFileChooserUI)getFileChooser().getUI()).getApproveSelectionAction().actionPerformed(null);  // Needed to apply the entered text
		}
		getFileChooser().approveSelection();
    
		File file = getSelectedFile(); 
		if (file != null) {
			boolean exists = file.exists();
			if (!option.equals(Option.FILE_MUST_EXEST) || exists) {
				boolean write = true;
				if (option.equals(Option.ASK_TO_OVERWRITE) && exists) {
					write = (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(this, 
							"The file \"" + file.getAbsolutePath() + "\" already exists.\n" +
							"Do you want to overwrite it?", "Overwrite file", JOptionPane.YES_NO_OPTION, 
							JOptionPane.WARNING_MESSAGE));
				}
				if (write) {
					return onApply(file);
				}
			}
			else {
				JOptionPane.showMessageDialog(this,	"The file \"" + file.getAbsolutePath() + 
						"\" could not be found.", "File not found", JOptionPane.ERROR_MESSAGE);
			}
		}
		else {
			JOptionPane.showMessageDialog(this,	"You did not select any file.",	
					"No file selected", JOptionPane.ERROR_MESSAGE);
		}
		return false;
	}
}