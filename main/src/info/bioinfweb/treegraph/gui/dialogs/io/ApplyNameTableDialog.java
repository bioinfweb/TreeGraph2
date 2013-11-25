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


import info.bioinfweb.treegraph.Main;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.undo.edit.InvalidFormatException;
import info.bioinfweb.treegraph.document.undo.file.ApplyNameTableEdit;
import info.bioinfweb.treegraph.gui.CurrentDirectoryModel;
import info.bioinfweb.treegraph.gui.dialogs.EditDialog;
import info.bioinfweb.treegraph.gui.dialogs.nodebranchdatainput.NodeDataComboBoxModel;
import info.bioinfweb.treegraph.gui.mainframe.MainFrame;
import info.webinsel.wikihelp.client.WikiHelpOptionPane;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.Frame;
import javax.swing.BoxLayout;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.basic.BasicFileChooserUI;
import java.awt.GridBagLayout;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;

import java.awt.Dialog;
import java.awt.Font;
import java.awt.Color;
import java.awt.GridBagConstraints;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import java.awt.Insets;
import javax.swing.JLabel;



public class ApplyNameTableDialog extends EditDialog {
	private static final long serialVersionUID = 1L;
	
	
	private JPanel jContentPane = null;
	private JFileChooser fileChooser = null;
	private TableSeparatorPanel separatorPanel = null;
	private JPanel nodeDataPanel = null;
	private JComboBox adapterComboBox = null;
	private JPanel fileChooserPanel = null;
	private JCheckBox whitespaceCheckBox = null;
	private JCheckBox parseNumericValuesCheckBox = null;
	private JCheckBox caseSensitiveCheckBox = null;
	private JLabel nodeDataLabel = null;


	/**
	 * @param owner
	 * @wbp.parser.constructor
	 */
	public ApplyNameTableDialog(Frame owner) {
		super(owner);
		setHelpCode(17);
		initialize();
		setLocationRelativeTo(owner);
	}


	public ApplyNameTableDialog(Dialog owner) {
		super(owner);
		initialize();
		setLocationRelativeTo(owner);
	}


	@Override
	protected boolean onExecute() {
		((NodeDataComboBoxModel)getAdapterComboBox().getModel()).setAdapters(
				getDocument().getTree());
		return true;
	}


	@Override
	protected boolean apply() {
		if (getFileChooser().getUI() instanceof BasicFileChooserUI) {
      ((BasicFileChooserUI)getFileChooser().getUI()).getApproveSelectionAction().actionPerformed(null);  // Needed to apply the entered text
      if (getSeparatorPanel().checkInput()) {
	      char separator = getSeparatorPanel().getSeparator();
	      File file = getFileChooser().getSelectedFile();
	      if (file != null) {
	  			Vector<String> oldNames = new Vector<String>();
	  			Vector<String> newNames = new Vector<String>();
	  			
	  			try {
	  				ApplyNameTableEdit.loadNameTable(file, oldNames, newNames, separator);
	  			}
	  			catch (FileNotFoundException e) {
		  			JOptionPane.showMessageDialog(MainFrame.getInstance(), "The file \"" +
		  					file.getAbsolutePath() + "\" could not be found.", "Error", 
		  					JOptionPane.ERROR_MESSAGE);
		  			return false;
	  			}
	  			catch (IOException e) {
		  			JOptionPane.showMessageDialog(MainFrame.getInstance(), "An error occured " +
		  					"while trying to read from the file \"" +	file.getAbsolutePath() + "\".", 
		  					"Error", JOptionPane.ERROR_MESSAGE);
		  			return false;
	  			}
	  			catch (InvalidFormatException e) {
	  				WikiHelpOptionPane.showMessageDialog(MainFrame.getInstance(), "The file \"" +
		  					file.getAbsolutePath() + "\" is malformed.", "Error", 
		  					JOptionPane.ERROR_MESSAGE, Main.getInstance().getWikiHelp(), 32);
		  			return false;
	  			}
	  			getDocument().executeEdit(new ApplyNameTableEdit(getDocument(),
	  					(NodeBranchDataAdapter)getAdapterComboBox().getSelectedItem(),
					    oldNames, newNames, getWhitespaceCheckBox().isSelected(),
					    getCaseSensitiveCheckBox().isSelected(),
					    getParseNumericValuesCheckBox().isSelected()));
	  			return true;
	  		}
	      else {
	  			JOptionPane.showMessageDialog(MainFrame.getInstance(), "You have to select a " +
	  					"file.", "Error", JOptionPane.ERROR_MESSAGE);
	  			return false;
	      }
      }
      else {
  			return false;
      }
    }
		else {
			JOptionPane.showMessageDialog(MainFrame.getInstance(), "Internal error.", "Error", 
					JOptionPane.ERROR_MESSAGE);
			return true;
		}
	}


	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		//this.setSize(500, 500);
		this.setTitle("Apply name table file");
		this.setContentPane(getJContentPane());
		this.pack();
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
			jContentPane.add(getFileChooserPanel(), null);
			jContentPane.add(getSeparatorPanel(), null);
			jContentPane.add(getNodeDataPanel(), null);
			jContentPane.add(getButtonsPanel(), null);
		}
		return jContentPane;
	}


	/**
	 * This method initializes fileChooser	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JFileChooser getFileChooser() {
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


	/**
	 * This method initializes nodeDataPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getNodeDataPanel() {
		if (nodeDataPanel == null) {
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.gridx = 0;
			gridBagConstraints7.gridy = 0;
			nodeDataLabel = new JLabel();
			nodeDataLabel.setText("Target node data: ");
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.gridx = 0;
			gridBagConstraints6.anchor = GridBagConstraints.WEST;
			gridBagConstraints6.ipadx = 0;
			gridBagConstraints6.insets = new Insets(0, 0, 0, 0);
			gridBagConstraints6.gridwidth = 2;
			gridBagConstraints6.gridy = 2;
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.gridx = 0;
			gridBagConstraints5.anchor = GridBagConstraints.WEST;
			gridBagConstraints5.gridwidth = 2;
			gridBagConstraints5.gridy = 3;
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.gridx = 0;
			gridBagConstraints4.insets = new Insets(0, 0, 0, 0);
			gridBagConstraints4.gridwidth = 2;
			gridBagConstraints4.anchor = GridBagConstraints.WEST;
			gridBagConstraints4.fill = GridBagConstraints.NONE;
			gridBagConstraints4.gridy = 1;
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints3.gridy = 0;
			gridBagConstraints3.weightx = 1.0;
			gridBagConstraints3.gridx = 1;
			nodeDataPanel = new JPanel();
			nodeDataPanel.setLayout(new GridBagLayout());
			nodeDataPanel.setBorder(BorderFactory.createTitledBorder(null, "", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			nodeDataPanel.add(getAdapterComboBox(), gridBagConstraints3);
			nodeDataPanel.add(getWhitespaceCheckBox(), gridBagConstraints4);
			nodeDataPanel.add(getParseNumericValuesCheckBox(), gridBagConstraints5);
			nodeDataPanel.add(getCaseSensitiveCheckBox(), gridBagConstraints6);
			nodeDataPanel.add(nodeDataLabel, gridBagConstraints7);
		}
		return nodeDataPanel;
	}


	/**
	 * This method initializes adapterComboBox	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getAdapterComboBox() {
		if (adapterComboBox == null) {
			adapterComboBox = new JComboBox(new NodeDataComboBoxModel());
		}
		return adapterComboBox;
	}


	/**
	 * This method initializes fileChooserPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getFileChooserPanel() {
		if (fileChooserPanel == null) {
			fileChooserPanel = new JPanel();
			fileChooserPanel.setLayout(new GridBagLayout());
			fileChooserPanel.setBorder(BorderFactory.createTitledBorder(null, "Name table file:", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			fileChooserPanel.add(getFileChooser(), new GridBagConstraints());
		}
		return fileChooserPanel;
	}


	/**
	 * This method initializes whitespaceCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getWhitespaceCheckBox() {
		if (whitespaceCheckBox == null) {
			whitespaceCheckBox = new JCheckBox();
			whitespaceCheckBox.setSelected(true);
			whitespaceCheckBox.setText("Ignore leading and trailing white spaces");
		}
		return whitespaceCheckBox;
	}


	/**
	 * This method initializes parseNumericValuesCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getParseNumericValuesCheckBox() {
		if (parseNumericValuesCheckBox == null) {
			parseNumericValuesCheckBox = new JCheckBox();
			parseNumericValuesCheckBox.setText("Parse numeric values");
			parseNumericValuesCheckBox.setSelected(true);
		}
		return parseNumericValuesCheckBox;
	}


	/**
	 * This method initializes caseSensitiveCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getCaseSensitiveCheckBox() {
		if (caseSensitiveCheckBox == null) {
			caseSensitiveCheckBox = new JCheckBox();
			caseSensitiveCheckBox.setText("Case sensitive");
		}
		return caseSensitiveCheckBox;
	}
}