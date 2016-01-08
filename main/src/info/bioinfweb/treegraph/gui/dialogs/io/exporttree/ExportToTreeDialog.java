/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2015-2016  Ben Stöver, Sarah Wiechers
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
package info.bioinfweb.treegraph.gui.dialogs.io.exporttree;


import info.bioinfweb.treegraph.document.io.DocumentFilter;
import info.bioinfweb.treegraph.document.io.DocumentWriter;
import info.bioinfweb.treegraph.document.io.ReadWriteFactory;
import info.bioinfweb.treegraph.document.io.ReadWriteFormat;
import info.bioinfweb.treegraph.document.io.ReadWriteParameterMap;
import info.bioinfweb.treegraph.document.io.TreeFilter;
import info.bioinfweb.treegraph.document.io.nexus.NexusFilter;
import info.bioinfweb.treegraph.document.io.xtg.XTGFilter;
import info.bioinfweb.treegraph.gui.dialogs.io.FileDialog;
import info.bioinfweb.treegraph.gui.mainframe.MainFrame;

import java.awt.Frame;
import java.awt.Font;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.io.File;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;



public class ExportToTreeDialog extends FileDialog {
	private static final long serialVersionUID = 1L;
	
	
	private JPanel jContentPane = null;
	private JFileChooser fileChooser;  //  This field must not be set to anything (e.g. null) because the initialization performed by the super constructor (FileDialog) would be overwritten than.
	private NexusFilter nexusFilter;  //  This field must not be set to anything (e.g. null).
	private JPanel filePanel;
	private JPanel additionalDataOuterPanel;   //  This field must not be set to anything (e.g. null).
	private TreeFormatPanel additionalDataPanel;


	/**
	 * @param owner
	 */
	public ExportToTreeDialog(Frame owner) {
		super(owner, FileDialog.Option.ASK_TO_OVERWRITE);
		
		initialize();
		setHelpCode(82);
		setLocationRelativeTo(owner);
	}


	@Override
	protected boolean onExecute() {
		String name = getDocument().getDefaultNameOrPath();
		if (name.endsWith(XTGFilter.EXTENSION)) {
			name = name.substring(0, name.length() - XTGFilter.EXTENSION.length());
		}
		if (name.length() > 0) {
			getFileChooser().setSelectedFile(new File(name));
		}
		additionalDataPanel.initializeContents(getDocument());
		return true;
	}


	@Override
	protected File getSelectedFile() {
		File result = super.getSelectedFile();
		if (result != null) {
			DocumentFilter filter = (DocumentFilter)getFileChooser().getFileFilter(); 
			if (!filter.validExtension(result.getAbsolutePath())) {
				result = new File(result.getAbsolutePath() + filter.getDefaultExtension());
			}
		}
		return result;
	}

	
	@Override
	protected boolean onApply(File file) {
		DocumentWriter writer;
		if (((DocumentFilter)getFileChooser().getFileFilter()).equals(nexusFilter)) {
			writer = ReadWriteFactory.getInstance().getWriter(ReadWriteFormat.NEXUS);
		}
		else {
			writer = ReadWriteFactory.getInstance().getWriter(ReadWriteFormat.NEWICK);
		}
		
		try {
			ReadWriteParameterMap properties = new ReadWriteParameterMap();
			if (additionalDataPanel != null) {
				additionalDataPanel.addProperties(properties);
			}
			writer.write(getDocument(), file, properties);
			return true;
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(MainFrame.getInstance(), 
					"An error occured when trying to write to the file \"" + 
					file.getAbsolutePath() + "\".\nError message: \"" + e.getMessage() + "\"", 
					"Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}


	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setTitle("Export to tree format");
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
			jContentPane.add(getFilePanel());
			jContentPane.add(getAdditionalDataOuterPanel());
			getApplyButton().setVisible(false);
			getOkButton().setText("Export");
			jContentPane.add(getButtonsPanel());
		}
		return jContentPane;
	}
	
	
	private JPanel getFilePanel() {
		if (filePanel == null) {
			filePanel = new JPanel();
			filePanel.setBorder(BorderFactory.createTitledBorder(null, "File", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, 
					new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			filePanel.add(getFileChooser());
		}
		return filePanel;
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
			fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
			fileChooser.setControlButtonsAreShown(false);
			
			if (fileChooser.getFileFilter() != null) {  // "Alle Dateien"-Filter entfernen
				fileChooser.removeChoosableFileFilter(fileChooser.getFileFilter());
			}
			nexusFilter = new NexusFilter();
			fileChooser.addChoosableFileFilter(nexusFilter);
			fileChooser.addChoosableFileFilter(ReadWriteFactory.getInstance().getFilter(ReadWriteFormat.NEWICK));
			fileChooser.setFileFilter(nexusFilter);
			setAdditionalDataOuterPanel(nexusFilter.getFormat());
//			System.out.println("Component count after set: " + getAdditionalDataOuterPanel().getComponentCount());
			
			fileChooser.addPropertyChangeListener(new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent e) {
					if (e.getPropertyName().equals(JFileChooser.FILE_FILTER_CHANGED_PROPERTY)) {
						if (e.getNewValue() instanceof TreeFilter) {
							setAdditionalDataOuterPanel(((TreeFilter)e.getNewValue()).getFormat());
						}
						else {
							System.out.println("Null.");
							setAdditionalDataOuterPanel(null);
						}
					}
				}
			});
		}
		return fileChooser;
	}
	
	
	private JPanel getAdditionalDataOuterPanel() {
		if (additionalDataOuterPanel == null) {
			additionalDataOuterPanel = new JPanel();
			additionalDataOuterPanel.setLayout(new BoxLayout(additionalDataOuterPanel, BoxLayout.Y_AXIS));
		}
		return additionalDataOuterPanel;
	}
	
	
	private void setAdditionalDataOuterPanel(ReadWriteFormat format) {
		additionalDataPanel = null;		
		if (format != null) {
			additionalDataPanel = AdditionalDataPanelFactory.getInstance().getPanel(format);
		}
		getAdditionalDataOuterPanel().removeAll();
		if (additionalDataPanel != null) {
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			gridBagConstraints.fill = GridBagConstraints.BOTH;
			gridBagConstraints.weightx = 1.0;
			gridBagConstraints.weighty = 1.0;
			getAdditionalDataOuterPanel().add((JComponent)additionalDataPanel, gridBagConstraints);
			additionalDataPanel.initializeContents(getDocument());
			getAdditionalDataOuterPanel().setVisible(true);
		}
		else {
			getAdditionalDataOuterPanel().setVisible(false);
		}
		
		pack();
	}
}