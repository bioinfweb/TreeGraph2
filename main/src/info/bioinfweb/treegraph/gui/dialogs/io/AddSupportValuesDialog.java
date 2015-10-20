/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2015  Ben Stöver, Sarah Wiechers, Kai Müller
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


import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.IDManager;
import info.bioinfweb.treegraph.document.io.DocumentReader;
import info.bioinfweb.treegraph.document.io.ReadWriteFactory;
import info.bioinfweb.treegraph.document.io.ReadWriteFormat;
import info.bioinfweb.treegraph.document.io.ReadWriteParameterMap;
import info.bioinfweb.treegraph.document.io.nexus.NexusFilter;
import info.bioinfweb.treegraph.document.nodebranchdata.TextElementDataAdapter;
import info.bioinfweb.treegraph.document.undo.file.AddSupportValuesEdit;
import info.bioinfweb.treegraph.document.undo.file.AddSupportValuesEdit.TargetType;
import info.bioinfweb.treegraph.gui.dialogs.io.loadlogger.LoadLoggerDialog;
import info.bioinfweb.treegraph.gui.dialogs.nodebranchdatainput.NodeBranchDataInput;
import info.bioinfweb.treegraph.gui.mainframe.MainFrame;

import javax.swing.JPanel;
import java.awt.Frame;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import java.awt.GridBagLayout;
import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;
import java.awt.Font;
import java.awt.Color;
import javax.swing.JTextField;
import java.awt.GridBagConstraints;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JCheckBox;



public class AddSupportValuesDialog extends FileDialog {
	private static final long serialVersionUID = 1L;
	
	
	private JPanel jContentPane = null;
	private JPanel fileChooserPanel = null;
	private JFileChooser fileChooser;  // This field must not be set to anything (e.g. null) because the initialization performed by the super constructor (FileDialog) would be overwritten than.
	private JPanel idNamePanel = null;
	private JTextField idNameTextField = null;
	private NodeBranchDataInput terminalDataInput = null;
	private JLabel prefixLabel = null;
	private JPanel targetTypePanel = null;
	private JRadioButton labelRadioButton = null;
	private JRadioButton branchDataRadioButton = null;
	private JRadioButton nodeDataRadioButton = null;
	private JPanel terminalsPanel = null;
	private JLabel terminalsLabel = null;
	private ButtonGroup targetDataButtonGroup = null;  //  @jve:decl-index=0:
	private ButtonGroup importButtonGroup = null;  //  @jve:decl-index=0:
	private JCheckBox translateInternalsCheckBox = null;
	private JPanel importPanel = null;
	private JRadioButton nodeNamesRadioButton = null;
	private JRadioButton branchLengthsRadioButton = null;

	private NexusFilter nexusFilter;  // This field must not be set to anything (e.g. null)


	/**
	 * Creates a new instance of this class.
	 * 
	 * @param owner - the parent frame
	 */
	public AddSupportValuesDialog(Frame owner) {
		super(owner, FileDialog.Option.FILE_MUST_EXIST);
		setHelpCode(12);
		initialize();
		setLocationRelativeTo(owner);
  }


	@Override
	protected boolean onExecute() {
		if (!getDocument().getTree().isEmpty()) {
			getIDNameTextField().setText(IDManager.newID(
					IDManager.getIDListFromSubtree(getDocument().getTree().getPaintStart())));
			getTerminalDataInput().setAdapters(getDocument().getTree(), false, true, false, false, false, "");
			getTerminalDataInput().setSelectedAdapter(getDocument().getDefaultLeafAdapter());
			return true;
		}
		else {
			JOptionPane.showMessageDialog(MainFrame.getInstance(), 
					"The document cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}


	@Override
	protected boolean onApply(File file) {
		if (getIDNameTextField().getText().length() > 1) {
      DocumentReader reader = ReadWriteFactory.getInstance().getReader(file);
			TargetType targetType = TargetType.LABEL;
			if (getBranchDataRadioButton().isSelected()) {
				targetType = TargetType.HIDDEN_BRANCH_DATA;
			}
			else if (getNodeDataRadioButton().isSelected()) {
				targetType = TargetType.HIDDEN_NODE_DATA;
			}
			
			try {
				ReadWriteParameterMap parameterMap = new ReadWriteParameterMap();
				parameterMap.putApplicationLogger(LoadLoggerDialog.getInstance());
				parameterMap.put(ReadWriteParameterMap.KEY_TREE_SELECTOR, TreeSelectionDialog.getInstance());
				parameterMap.put(ReadWriteParameterMap.KEY_TRANSLATE_INTERNAL_NODE_NAMES, 
						getTranslateInternalsCheckBox().isSelected());
				Document sourceDoc = reader.read(getFileChooser().getSelectedFile(), parameterMap);
				
				boolean rooted = sourceDoc.getTree().getFormats().getShowRooted();
				if (rooted != getDocument().getTree().getFormats().getShowRooted()) {
					String[] options = {"Regard both trees as unrooted", "Regard both trees as rooted", "Cancel"};
					switch (JOptionPane.showOptionDialog(this, "The source and the target are incompatible because " +
							"they are not both rooted or both unrooted.\n\nWhat do you want to do?", 
							"Incompatible trees",	JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, 
							null, options, options[0])) {
					
					  case 0:
					  	rooted = false;
	  				  break;
					  case 1:
					  	rooted = true;
	  				  break;
					  default:
					  	return false;
		  		}
				}
				
				AddSupportValuesEdit edit = AddSupportValuesEdit.createInstance(
  					getDocument(), sourceDoc,
	  				(TextElementDataAdapter)getTerminalDataInput().getSelectedAdapter(), targetType,
	  				getIDNameTextField().getText(),	getNodeNamesRadioButton().isSelected(), rooted);
				
  			if (edit != null) {
  				getDocument().executeEdit(edit);
					LoadLoggerDialog.getInstance().display();
  				return true;
  			}
  			else {
  				JOptionPane.showMessageDialog(this, "The selected document contains " +
  						"non-decimal data on its internal nodes. Only decimal support " +
  						"values can be processed.", "Invalid source document", 
  						JOptionPane.ERROR_MESSAGE);
  			}
  		}
  		catch (Exception e) {
  			e.printStackTrace();
				JOptionPane.showMessageDialog(null, "The error \"" + e.getMessage() + 
						"\" occured when trying to open the file \"" + file.getAbsolutePath() + 
						"\"", "Error", JOptionPane.ERROR_MESSAGE);
				return false;
  		}
		}
		else {
			JOptionPane.showMessageDialog(MainFrame.getInstance(), "There was no label ID" +
					" specified.", "Error", JOptionPane.ERROR_MESSAGE);
		}
		return false;
	}


	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setTitle("Add support values");
		this.setContentPane(getJContentPane());
		getApplyButton().setVisible(false);
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
			jContentPane.add(getImportPanel(), null);
			jContentPane.add(getTerminalsPanel(), null);
			jContentPane.add(getIdNamePanel(), null);
			jContentPane.add(getButtonsPanel(), null);
		}
		return jContentPane;
	}


	/**
	 * This method initializes fileChooserPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getFileChooserPanel() {
		if (fileChooserPanel == null) {
			fileChooserPanel = new JPanel();
			fileChooserPanel.setLayout(new BoxLayout(getFileChooserPanel(), BoxLayout.Y_AXIS));
			fileChooserPanel.setBorder(BorderFactory.createTitledBorder(null, "Source file", 
					TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, 
					new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			fileChooserPanel.add(getFileChooser(), null);
		}
		return fileChooserPanel;
	}


	/**
	 * This method initializes fileChooser	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	@Override
	protected JFileChooser getFileChooser() {
		if (fileChooser == null) {
			fileChooser = new JFileChooser();
			fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
			fileChooser.setControlButtonsAreShown(false);
			
			nexusFilter = new NexusFilter();
			if (fileChooser.getFileFilter() != null) {  // "Alle Datein"-Filter entfernen
				fileChooser.removeChoosableFileFilter(fileChooser.getFileFilter());
			}
			fileChooser.addChoosableFileFilter(nexusFilter);
			fileChooser.addChoosableFileFilter(ReadWriteFactory.getInstance().getFilter(ReadWriteFormat.NEWICK));
			fileChooser.setFileFilter(nexusFilter);
			
			fileChooser.addPropertyChangeListener(new PropertyChangeListener() {
						public void propertyChange(PropertyChangeEvent e) {
							if (e.getPropertyName().equals(JFileChooser.FILE_FILTER_CHANGED_PROPERTY)) {
								getTranslateInternalsCheckBox().setEnabled(e.getNewValue() instanceof NexusFilter);
							}
							else if (e.getPropertyName().equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY)) {
								File file = getFileChooser().getSelectedFile();
								getTranslateInternalsCheckBox().setEnabled((file != null) && 
										nexusFilter.accept(file));
							}
						}
					});
		}
		return fileChooser;
	}


	/**
	 * This method initializes idNamePanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getIdNamePanel() {
		if (idNamePanel == null) {
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints2.weightx = 1.0;
			gridBagConstraints2.gridwidth = 2;
			gridBagConstraints2.gridy = 0;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.gridy = 1;
			prefixLabel = new JLabel();
			prefixLabel.setText("ID prefix: ");
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints.gridy = 1;
			gridBagConstraints.weightx = 1.0;
			gridBagConstraints.gridx = 1;
			idNamePanel = new JPanel();
			idNamePanel.setLayout(new GridBagLayout());
			idNamePanel.setBorder(BorderFactory.createTitledBorder(null, "Target data", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			idNamePanel.add(getIDNameTextField(), gridBagConstraints);
			idNamePanel.add(prefixLabel, gridBagConstraints1);
			idNamePanel.add(getTargetTypePanel(), gridBagConstraints2);
		}
		return idNamePanel;
	}


	/**
	 * This method initializes idNameTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getIDNameTextField() {
		if (idNameTextField == null) {
			idNameTextField = new JTextField();
		}
		return idNameTextField;
	}


	/**
	 * This method initializes targetTypePanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getTargetTypePanel() {
		if (targetTypePanel == null) {
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.gridx = 2;
			gridBagConstraints5.weightx = 1.0;
			gridBagConstraints5.anchor = GridBagConstraints.EAST;
			gridBagConstraints5.gridy = 0;
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.gridx = 1;
			gridBagConstraints4.weightx = 1.0;
			gridBagConstraints4.gridy = 0;
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.anchor = GridBagConstraints.WEST;
			gridBagConstraints3.weightx = 1.0;
			targetTypePanel = new JPanel();
			targetTypePanel.setLayout(new GridBagLayout());
			targetTypePanel.add(getLabelRadioButton(), gridBagConstraints3);
			targetTypePanel.add(getBranchDataRadioButton(), gridBagConstraints4);
			targetTypePanel.add(getNodeDataRadioButton(), gridBagConstraints5);
			getTargetDataButtonGroup();
		}
		return targetTypePanel;
	}


	/**
	 * This method initializes labelRadioButton	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getLabelRadioButton() {
		if (labelRadioButton == null) {
			labelRadioButton = new JRadioButton();
			labelRadioButton.setText("New labels");
			labelRadioButton.setSelected(true);
		}
		return labelRadioButton;
	}


	/**
	 * This method initializes branchDataRadioButton	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getBranchDataRadioButton() {
		if (branchDataRadioButton == null) {
			branchDataRadioButton = new JRadioButton();
			branchDataRadioButton.setText("New hidden branch data");
		}
		return branchDataRadioButton;
	}


	/**
	 * This method initializes nodeDataRadioButton	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getNodeDataRadioButton() {
		if (nodeDataRadioButton == null) {
			nodeDataRadioButton = new JRadioButton();
			nodeDataRadioButton.setText("New hidden node data");
		}
		return nodeDataRadioButton;
	}


	private ButtonGroup getTargetDataButtonGroup() {
		if (targetDataButtonGroup == null) {
			targetDataButtonGroup = new ButtonGroup();
			targetDataButtonGroup.add(getLabelRadioButton());
			targetDataButtonGroup.add(getBranchDataRadioButton());
			targetDataButtonGroup.add(getNodeDataRadioButton());
		}
		return targetDataButtonGroup;
	}


	private ButtonGroup getImportButtonGroup() {
		if (importButtonGroup == null) {
			importButtonGroup = new ButtonGroup();
			importButtonGroup.add(getNodeNamesRadioButton());
			importButtonGroup.add(getBranchLengthsRadioButton());
		}
		return targetDataButtonGroup;
	}


	/**
	 * This method initializes terminalsPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getTerminalsPanel() {
		if (terminalsPanel == null) {
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.gridx = 0;
			gridBagConstraints6.gridy = 0;
			terminalsLabel = new JLabel();
			terminalsLabel.setText("Node data that contains same terminal names as used in the import file: ");
			terminalsPanel = new JPanel();
			terminalsPanel.setLayout(new GridBagLayout());
			terminalsPanel.setBorder(BorderFactory.createTitledBorder(null, "Terminals", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			terminalsPanel.add(terminalsLabel, gridBagConstraints6);
			terminalDataInput = new NodeBranchDataInput(terminalsPanel, 1, 0);
		}
		return terminalsPanel;
	}


	public NodeBranchDataInput getTerminalDataInput() {
		getTerminalsPanel();
		return terminalDataInput;
	}


	/**
	 * This method initializes translateInternalsCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getTranslateInternalsCheckBox() {
		if (translateInternalsCheckBox == null) {
			translateInternalsCheckBox = new JCheckBox();
			translateInternalsCheckBox.setText("Translate internal node names");
		}
		return translateInternalsCheckBox;
	}


	/**
	 * This method initializes importPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getImportPanel() {
		if (importPanel == null) {
			GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			gridBagConstraints9.gridx = 1;
			gridBagConstraints9.anchor = GridBagConstraints.WEST;
			gridBagConstraints9.weightx = 1.0;
			gridBagConstraints9.gridy = 0;
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.gridx = 0;
			gridBagConstraints8.anchor = GridBagConstraints.WEST;
			gridBagConstraints8.weightx = 1.0;
			gridBagConstraints8.gridy = 0;
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.weightx = 1.0;
			gridBagConstraints7.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints7.anchor = GridBagConstraints.CENTER;
			gridBagConstraints7.gridy = 1;
			gridBagConstraints7.gridwidth = 1;
			gridBagConstraints7.weighty = 0.0;
			importPanel = new JPanel();
			importPanel.setLayout(new GridBagLayout());
			importPanel.setBorder(BorderFactory.createTitledBorder(null, "Import", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			importPanel.add(getTranslateInternalsCheckBox(), gridBagConstraints7);
			importPanel.add(getNodeNamesRadioButton(), gridBagConstraints8);
			importPanel.add(getBranchLengthsRadioButton(), gridBagConstraints9);
			getImportButtonGroup();
		}
		return importPanel;
	}


	/**
	 * This method initializes nodeNamesRadioButton	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getNodeNamesRadioButton() {
		if (nodeNamesRadioButton == null) {
			nodeNamesRadioButton = new JRadioButton();
			nodeNamesRadioButton.setText("Node names");
			nodeNamesRadioButton.setSelected(true);
			nodeNamesRadioButton.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(java.awt.event.ItemEvent e) {
					getTranslateInternalsCheckBox().setEnabled(
							getNodeNamesRadioButton().isSelected());
				}
			});
		}
		return nodeNamesRadioButton;
	}


	/**
	 * This method initializes branchLengthsRadioButton	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getBranchLengthsRadioButton() {
		if (branchLengthsRadioButton == null) {
			branchLengthsRadioButton = new JRadioButton();
			branchLengthsRadioButton.setText("Branch length");
		}
		return branchLengthsRadioButton;
	}
}