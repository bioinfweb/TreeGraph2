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
package info.bioinfweb.treegraph.gui.dialogs.io.imexporttable;


import info.bioinfweb.treegraph.document.undo.file.importtable.ImportTableParameters;
import info.bioinfweb.treegraph.gui.dialogs.io.FileDialog;
import info.bioinfweb.treegraph.gui.dialogs.io.TableSeparatorPanel;
import info.bioinfweb.treegraph.gui.dialogs.io.TextFileFilter;
import info.bioinfweb.treegraph.gui.dialogs.nodebranchdata.NodeBranchDataColumnsDialog;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;



/**
 * Dialog that prompts the user for a table file to be imported to node/branch data columns as well as
 * several import options. This dialog is displayed in a sequence before {@link NodeBranchDataColumnsDialog}.
 * 
 * @author Ben St&ouml;ver
 */
public class SelectImportTableDialog extends FileDialog {
	private static final long serialVersionUID = 1L;

	
	private JPanel jContentPane = null;
	private JPanel filePanel = null;
	private TableSeparatorPanel separatorPanel = null;
	private JFileChooser fileChooser;  //  This field must not be set to anything (e.g. null) because the initialization performed by the super constructor (FileDialog) would be overwritten than.
	private JPanel nodeIdentificationPanel;
	private JLabel separatorLabel;
	private JLabel linesToSkipLabel;
	private JCheckBox columnHeadingsCheckBox;
	private JSpinner linesToSkipSpinner;


	/**
	 * @param owner
	 */
	public SelectImportTableDialog(Frame owner) {
		super(owner, FileDialog.Option.FILE_MUST_EXIST);
		initialize();
		setLocationRelativeTo(owner);
	}
	
	
  public void assignParameters(ImportTableParameters parameters) {
		parameters.setTableFile(getSelectedFile());
		parameters.setColumnSeparator(getSeparatorPanel().getSeparator());
		parameters.setLinesToSkip((Integer)getLinesToSkipSpinner().getValue());
		parameters.setHeadingContained(getColumnHeadingsCheckBox().isSelected());
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
		setHelpCode(88);
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
			FileFilter textFilter = new TextFileFilter();
			fileChooser.addChoosableFileFilter(textFilter);
			fileChooser.setFileFilter(textFilter);
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
			jContentPane.add(getNodeIdentificationPanel());
			jContentPane.add(getButtonsPanel(), null);
			getApplyButton().setVisible(false);
			getOkButton().setText("Next >");
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
			separatorPanel.setBorder(null);
		}
		return separatorPanel;
	}
	
	
	private JPanel getNodeIdentificationPanel() {
		if (nodeIdentificationPanel == null) {
			nodeIdentificationPanel = new JPanel();
			nodeIdentificationPanel.setBorder(BorderFactory.createTitledBorder(null, "Table preferences", 
					TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, 
					new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			GridBagLayout gbl_nodeIdentificationPanel = new GridBagLayout();
			gbl_nodeIdentificationPanel.columnWidths = new int[]{0, 0, 0};
			gbl_nodeIdentificationPanel.rowHeights = new int[]{0, 0, 0, 0, 0};
			gbl_nodeIdentificationPanel.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
			gbl_nodeIdentificationPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
			nodeIdentificationPanel.setLayout(gbl_nodeIdentificationPanel);
			GridBagConstraints gbc_separatorLabel = new GridBagConstraints();
			gbc_separatorLabel.anchor = GridBagConstraints.NORTHWEST;
			gbc_separatorLabel.insets = new Insets(0, 0, 5, 5);
			gbc_separatorLabel.gridx = 0;
			gbc_separatorLabel.gridy = 0;
			nodeIdentificationPanel.add(getSeparatorLabel(), gbc_separatorLabel);
			GridBagConstraints gbc_separatorPanel = new GridBagConstraints();
			gbc_separatorPanel.fill = GridBagConstraints.HORIZONTAL;
			gbc_separatorPanel.weighty = 1.0;
			gbc_separatorPanel.insets = new Insets(0, 0, 5, 0);
			gbc_separatorPanel.gridx = 1;
			gbc_separatorPanel.gridy = 0;
			nodeIdentificationPanel.add(getSeparatorPanel(), gbc_separatorPanel);
			GridBagConstraints gbc_linesToSkipLabel = new GridBagConstraints();
			gbc_linesToSkipLabel.anchor = GridBagConstraints.WEST;
			gbc_linesToSkipLabel.insets = new Insets(0, 0, 5, 5);
			gbc_linesToSkipLabel.gridx = 0;
			gbc_linesToSkipLabel.gridy = 1;
			nodeIdentificationPanel.add(getLinesToSkipLabel(), gbc_linesToSkipLabel);
			GridBagConstraints gbc_linesToSkipSpinner = new GridBagConstraints();
			gbc_linesToSkipSpinner.fill = GridBagConstraints.HORIZONTAL;
			gbc_linesToSkipSpinner.insets = new Insets(0, 0, 5, 0);
			gbc_linesToSkipSpinner.gridx = 1;
			gbc_linesToSkipSpinner.gridy = 1;
			nodeIdentificationPanel.add(getLinesToSkipSpinner(), gbc_linesToSkipSpinner);
			GridBagConstraints gbc_columnHeadingsCheckBox = new GridBagConstraints();
			gbc_columnHeadingsCheckBox.insets = new Insets(0, 0, 5, 0);
			gbc_columnHeadingsCheckBox.gridwidth = 2;
			gbc_columnHeadingsCheckBox.anchor = GridBagConstraints.WEST;
			gbc_columnHeadingsCheckBox.gridx = 0;
			gbc_columnHeadingsCheckBox.gridy = 2;
			nodeIdentificationPanel.add(getColumnHeadingsCheckBox(), gbc_columnHeadingsCheckBox);
		}
		return nodeIdentificationPanel;
	}
	
	
	private JLabel getSeparatorLabel() {
		if (separatorLabel == null) {
			separatorLabel = new JLabel("Values separated by: ");
		}
		return separatorLabel;
	}
	
	
	private JLabel getLinesToSkipLabel() {
		if (linesToSkipLabel == null) {
			linesToSkipLabel = new JLabel("Number of lines to skip before heading or data:");
		}
		return linesToSkipLabel;
	}
	
	
	private JCheckBox getColumnHeadingsCheckBox() {
		if (columnHeadingsCheckBox == null) {
			columnHeadingsCheckBox = new JCheckBox("First line (after skipped lines) contains column headings");
		}
		return columnHeadingsCheckBox;
	}
	
	
	private JSpinner getLinesToSkipSpinner() {
		if (linesToSkipSpinner == null) {
			linesToSkipSpinner = new JSpinner();
			linesToSkipSpinner.setModel(new SpinnerNumberModel(new Integer(0), new Integer(0), null, new Integer(1)));
		}
		return linesToSkipSpinner;
	}
}