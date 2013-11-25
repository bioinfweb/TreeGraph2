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


import info.bioinfweb.treegraph.document.io.DocumentReader;
import info.bioinfweb.treegraph.document.io.ReadWriteFactory;
import info.bioinfweb.treegraph.document.io.ReadWriteParameterMap;
import info.bioinfweb.treegraph.document.io.SupportedFormatsFilter;
import info.bioinfweb.treegraph.document.io.newick.NewickException;
import info.bioinfweb.treegraph.document.io.newick.NewickFilter;
import info.bioinfweb.treegraph.document.io.nexus.NexusFilter;
import info.bioinfweb.treegraph.document.io.xtg.XTGFilter;
import info.bioinfweb.treegraph.document.nodebranchdata.BranchLengthAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.NewTextLabelAdapter;
import info.bioinfweb.treegraph.gui.dialogs.io.loadlogger.LoadLoggerDialog;
import info.bioinfweb.treegraph.gui.dialogs.nodebranchdatainput.NewNodeBranchDataInput;
import info.bioinfweb.treegraph.gui.mainframe.MainFrame;

import javax.swing.JPanel;
import java.awt.Frame;
import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import java.awt.GridBagLayout;
import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;
import java.awt.Font;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import javax.swing.JCheckBox;



public class OpenDialog extends FileDialog {
	public static final String DEFAULT_NODE_NAME_ID = "support";
	public static final String DEFAULT_BRANCH_LENGTH_ID = "lengths";
	
	private static final long serialVersionUID = 1L;
	
	
	private JPanel jContentPane = null;
	private JPanel fileChooserPanel = null;
	private JFileChooser fileChooser = null;
	private JPanel internalNodesPanel = null;
	private NewNodeBranchDataInput nodeNamesDataInput = null;
	private NewNodeBranchDataInput branchLengthsDataInput = null;
	private JCheckBox translateInternalNodesCheckBox = null;
	private JPanel branchLengthsPanel = null;
	
	private XTGFilter xtgFilter = null;  //  @jve:decl-index=0:
	private NexusFilter nexusFilter = null;  //  @jve:decl-index=0:
	private NewickFilter newickFilter = null;  //  @jve:decl-index=0:
	private NewickExceptionDialog newickExceptionDialog = null;


	/**
	 * @param owner
	 */
	public OpenDialog(Frame owner) {
		super(owner, FileDialog.Option.FILE_MUST_EXEST);
		initialize();
		setLocationRelativeTo(owner);
		setHelpCode(50);
	}


	@Override
	protected boolean onExecute() {
		return true;
	}


	@Override
	protected boolean onApply(File file) {
		try {
			boolean open = true;
			if (MainFrame.getInstance().getInternalFrameByFile(file) != null) {
				open = (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(this, 
						"The file \"" + file.getAbsolutePath() + "\" is already open.\n" +
						"Do you want to open it another time?", "Warning", 
						JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE));
			}
			
			if (open) {
				DocumentReader reader = ReadWriteFactory.getInstance().getReader(
						getFileChooser().getSelectedFile());
				if (reader != null) {
					ReadWriteParameterMap parameterMap = new ReadWriteParameterMap();
					parameterMap.putApplicationLogger(LoadLoggerDialog.getInstance());
					parameterMap.put(ReadWriteParameterMap.KEY_INTERNAL_NODE_NAMES_ADAPTER, 
							getNodeNamesDataInput().getSelectedAdapter());
					parameterMap.put(ReadWriteParameterMap.KEY_BRANCH_LENGTH_ADAPTER, 
							getBranchLengthsDataInput().getSelectedAdapter());
					parameterMap.put(ReadWriteParameterMap.KEY_TREE_SELECTOR, TreeSelectionDialog.getInstance());
					parameterMap.put(ReadWriteParameterMap.KEY_TRANSLATE_INTERNAL_NODE_NAMES, 
							getTranslateInternalNodesCheckBox().isSelected());
					
					MainFrame.getInstance().addInternalFrame(reader.read(file, parameterMap));
					LoadLoggerDialog.getInstance().display();
				}
				else {
					JOptionPane.showMessageDialog(this, "The selected file has an unsupported format.", 
							"Unsupported file format", JOptionPane.ERROR_MESSAGE);
					return false;
				}
			}
			return true;
		}
		catch (NewickException e) {
			if (newickExceptionDialog == null) {
				newickExceptionDialog = new NewickExceptionDialog(this);
			}
			newickExceptionDialog.show(e);
			e.printStackTrace();
			return false;
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(null, "The error \"" + e.toString() + "\" occured\n" +
					"when trying to open the file \"" + file.getAbsolutePath() + "\".", "Error",	
					JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			return false;
		}
	}


	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setTitle("Open tree file");
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
			jContentPane.add(getInternalNodesPanel(), null);
			jContentPane.add(getBranchLengthsPanel(), null);
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
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.fill = GridBagConstraints.BOTH;
			gridBagConstraints.weightx = 1.0;
			gridBagConstraints.weighty = 1.0;
			gridBagConstraints.gridy = 0;
			fileChooserPanel = new JPanel();
			fileChooserPanel.setLayout(new GridBagLayout());
			fileChooserPanel.setBorder(BorderFactory.createTitledBorder(null, "File", 
					TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, 
					new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			fileChooserPanel.add(getFileChooser(), gridBagConstraints);
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

			if (fileChooser.getFileFilter() != null) {  // "Alle Dateien"-Filter entfernen
				fileChooser.removeChoosableFileFilter(fileChooser.getFileFilter());
			}
			SupportedFormatsFilter supportedFormatsFilter = new SupportedFormatsFilter();
			fileChooser.addChoosableFileFilter(supportedFormatsFilter);
			xtgFilter = new XTGFilter();
			fileChooser.addChoosableFileFilter(xtgFilter);
			nexusFilter = new NexusFilter();
			fileChooser.addChoosableFileFilter(nexusFilter);
			newickFilter = new NewickFilter();
			fileChooser.addChoosableFileFilter(newickFilter);
			//fileChooser.addChoosableFileFilter(new TGFFilter());
			fileChooser.setFileFilter(supportedFormatsFilter);
			
			fileChooser.addPropertyChangeListener(new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent e) {
					File file = getFileChooser().getSelectedFile();
					if (e.getPropertyName().equals(JFileChooser.FILE_FILTER_CHANGED_PROPERTY)) {
						boolean enableAdapters = (file != null) &&	!(e.getNewValue() instanceof XTGFilter);
						getNodeNamesDataInput().setEnabled(enableAdapters);
						getTranslateInternalNodesCheckBox().setEnabled((file != null) &&	
								((e.getNewValue() instanceof NexusFilter) || 
										(e.getNewValue() instanceof SupportedFormatsFilter)));
						getBranchLengthsDataInput().setEnabled(enableAdapters);
					}
					else if (e.getPropertyName().equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY)) {
						boolean enableAdapters = (file != null) && 
						    nexusFilter.validExtension(file.getAbsolutePath()) &&
						    newickFilter.validExtension(file.getAbsolutePath());  // nur validExtension() um Öffnen jder Datei zu verhindern
						getNodeNamesDataInput().setEnabled(enableAdapters);
						getTranslateInternalNodesCheckBox().setEnabled((file != null) && 
								nexusFilter.accept(file));
						getBranchLengthsDataInput().setEnabled(enableAdapters);
					}
				}
			});
		}
		return fileChooser;
	}


	/**
	 * This method initializes internalNodesPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getInternalNodesPanel() {
		if (internalNodesPanel == null) {
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.anchor = GridBagConstraints.WEST;
			gridBagConstraints1.gridwidth = 2;
			gridBagConstraints1.gridy = 1;
			internalNodesPanel = new JPanel();
			internalNodesPanel.setLayout(new GridBagLayout());
			internalNodesPanel.setBorder(BorderFactory.createTitledBorder(null, "Import internal nodes as", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			internalNodesPanel.add(getTranslateInternalNodesCheckBox(), gridBagConstraints1);
			nodeNamesDataInput = new NewNodeBranchDataInput(internalNodesPanel, 0, 0, true);
			nodeNamesDataInput.setAdapters(null, false, true, false, false, true);
			nodeNamesDataInput.setSelectedAdapter(NewTextLabelAdapter.class);
			nodeNamesDataInput.setID(DEFAULT_NODE_NAME_ID);
		}
		return internalNodesPanel;
	}


	private NewNodeBranchDataInput getNodeNamesDataInput() {
		getInternalNodesPanel();
		return nodeNamesDataInput;
	}


	/**
	 * This method initializes translateInternalNodesCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getTranslateInternalNodesCheckBox() {
		if (translateInternalNodesCheckBox == null) {
			translateInternalNodesCheckBox = new JCheckBox();
			translateInternalNodesCheckBox.setText("Translate internal node names");
		}
		return translateInternalNodesCheckBox;
	}


	/**
	 * This method initializes branchLengthsPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getBranchLengthsPanel() {
		if (branchLengthsPanel == null) {
			branchLengthsPanel = new JPanel();
			branchLengthsPanel.setLayout(new GridBagLayout());
			branchLengthsPanel.setBorder(BorderFactory.createTitledBorder(null, "Branch lengths", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			branchLengthsDataInput = new NewNodeBranchDataInput(branchLengthsPanel, 0, 0, true);
			branchLengthsDataInput.setAdapters(null, false, false, true, true, true);
			branchLengthsDataInput.setSelectedAdapter(BranchLengthAdapter.class);
			branchLengthsDataInput.setID(DEFAULT_BRANCH_LENGTH_ID);
		}
		return branchLengthsPanel;
	}
	
	
	private NewNodeBranchDataInput getBranchLengthsDataInput() {
		getBranchLengthsPanel();
		return branchLengthsDataInput;
	}
}