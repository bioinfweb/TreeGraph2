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
package info.bioinfweb.treegraph.gui.dialogs.io.exporttographic;


import info.bioinfweb.treegraph.Main;
import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.format.DistanceDimension;
import info.bioinfweb.treegraph.document.format.DistanceValue;
import info.bioinfweb.treegraph.document.io.xtg.XTGFilter;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.graphics.export.GraphicFilter;
import info.bioinfweb.treegraph.graphics.export.GraphicFormat;
import info.bioinfweb.treegraph.graphics.export.GraphicWriter;
import info.bioinfweb.treegraph.graphics.export.GraphicWriterFactory;
import info.bioinfweb.treegraph.graphics.positionpaint.PositionPaintFactory;
import info.bioinfweb.treegraph.gui.dialogs.DistanceValueInput;
import info.bioinfweb.treegraph.gui.dialogs.ResolutionInput;
import info.bioinfweb.treegraph.gui.dialogs.io.FileDialog;
import info.bioinfweb.treegraph.gui.mainframe.MainFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeInternalFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeSelection;
import info.bioinfweb.treegraph.gui.treeframe.TreeViewPanel;
import info.bioinfweb.commons.Math2;
import info.bioinfweb.commons.collections.ParameterMap;
import info.bioinfweb.wikihelp.client.WikiHelpOptionPane;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Frame;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DecimalFormat;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JRadioButton;



public class ExportToGraphicDialog extends FileDialog {
	public static final String UNIT_MILLIMETERS = DistanceValueInput.UNIT_MILLIMETERS;
	public static final String UNIT_POINTS = DistanceValueInput.UNIT_POINTS;
	public static final String UNIT_PIXELS = "px";
	
	private static final long serialVersionUID = 1L;
	
	
	private JPanel jContentPane = null;
	private JPanel dimensionsPanel = null;
	private ResolutionInput resolutionInput = null;
	private JLabel widthLabel = null;
	private JLabel heightLabel = null;
	private JTextField widthTextField = null;
	private JTextField heightTextField = null;
	private JComboBox unitComboBox = null;
	private JLabel spacerLabel = null;
	private JFileChooser fileChooser;  //  This field must not be set to anything (e.g. null) because the initialization performed by the super constructor (FileDialog) would be overwritten then.
	private JPanel fileChooserPanel = null;
	private JPanel outerPreferencesPanel = null;
	private JPanel commandPanel = null;
	private JRadioButton openRadioButton = null;
	private JRadioButton printRadioButton = null;
	private JRadioButton noneRadioButton = null;
	private ButtonGroup commandGroup = null;
	
	/** 
	 * Used to convert the width and height values if their unit is changed by the user. 
	 */
	private int lastSelection = -1;
  private DecimalFormat decimalFormat = new DecimalFormat("##########.######");  // Bei zu vielen Nachkommastellen entstehen Rundungsfehler, da DecimalFormat double verwendet und nur float gespeichert wird.  //  @jve:decl-index=0:
  private float aspectRatio = 1;
  private PreferencesPanel preferencesPanel = null;  //  @jve:decl-index=0:
  private TreeInternalFrame treeFrame = null;
	
	
	/**
	 * @param owner
	 */
	public ExportToGraphicDialog(Frame owner) {
		super(owner, FileDialog.Option.ASK_TO_OVERWRITE);
		initialize();
		setHelpCode(3);
		setLocationRelativeTo(owner);
	}
	
	
	/**
	 * This method throws a Runtime exception if it is called. Use 
	 * {@link ExportToGraphicDialog#execute(Document, TreeSelection)} instead.
	 */
	@Override
	public boolean execute(Document document, TreeSelection selection, NodeBranchDataAdapter selectedAdapter) {
		throw new RuntimeException("This method cannot be called in this class. Call " +
				"execute(TreeInternalFrame frame) instead.");
	}


	/**
	 * This method should be called instead of 
	 * {@link ExportToGraphicDialog#execute(Document, TreeSelection)}.
	 * @param frame
	 * @return
	 * @see info.bioinfweb.treegraph.gui.dialogs.EditDialog#execute(info.bioinfweb.treegraph.document.Document, info.bioinfweb.treegraph.gui.treeframe.TreeSelection)
	 */
	public boolean execute(TreeInternalFrame frame) {
		this.treeFrame = frame;
		
		DistanceDimension d = frame.getDocument().getTree().getPaintDimension(
				frame.getTreeViewPanel().getPainterType());
		getUnitComboBox().setSelectedItem(UNIT_MILLIMETERS);
		getWidthTextField().setText(decimalFormat.format(d.getWidth().getInMillimeters()));
		getHeightTextField().setText(decimalFormat.format(d.getHeight().getInMillimeters()));
		aspectRatio = d.getWidth().getInMillimeters() / d.getHeight().getInMillimeters();
		
		resolutionInput.getComboBox().setSelectedItem(ResolutionInput.UNIT_PPMM);
		resolutionInput.setResolution(TreeViewPanel.PIXELS_PER_MM_100);
		
		lastSelection = getUnitComboBox().getSelectedIndex();

		return super.execute(frame.getDocument(), frame.getTreeViewPanel().getSelection(), 
				frame.getSelectedAdapter());
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
		return true;
	}


	private float convertToPixels(String text) {
		float result = Math2.parseFloat(text);
		if (getUnitComboBox().getSelectedItem().equals(UNIT_POINTS)) {
			result = DistanceValue.pointsToMillimeters(result);
		}  // Millimeter oder Pixel m�ssen nicht konvertiret werden.
		return result;
	}
	
	
	/** 
	 * Returns the width entered by the user in pixels or millimeters. (Use 
	 * <code>dimensionsInPixels()</code> to dertermine the unit.)
	 * @return the width the image should have
	 */
	private float getImageWidth() {
		return convertToPixels(getWidthTextField().getText());
	}
	
	
	/** 
	 * Return the height entered by the user in pixels or millimeters. (Use 
	 * <code>dimensionsInPixels()</code> to dertermine the unit.)
	 * @return the height the image should have
	 */
	private float getImageHeight() {
		return convertToPixels(getHeightTextField().getText());
	}


	private float getPixelsPerMillimeter() {
		return resolutionInput.getPixelsPerMillimeter();
	}
	
	
	private boolean dimensionsInPixels() {
		return getUnitComboBox().getSelectedItem().equals(UNIT_PIXELS);
	}	
	
	
	/**
	 * Returns the graphic format the user selected.
	 * @return
	 */
	private GraphicFormat getFormat() {
		return ((GraphicFilter)getFileChooser().getFileFilter()).getFormat();
	}
	
	
	/**
	 * Returns the graphic writer hints that have been specified by the user. This also includes format
	 * specific hints.
	 * @return
	 */
	private ParameterMap getHints() {
		ParameterMap result = new ParameterMap();
		result.put(GraphicWriter.KEY_PIXELS_PER_MILLIMETER, 
  			new Float(getPixelsPerMillimeter()));
		result.put(GraphicWriter.KEY_DIMENSIONS_IN_PIXELS, 
				new Boolean(dimensionsInPixels()));
		result.put(GraphicWriter.KEY_WIDTH, new Float(getImageWidth()));
		result.put(GraphicWriter.KEY_HEIGHT, new Float(getImageHeight()));
		
		if (preferencesPanel != null) {
			preferencesPanel.addHints(result);
		}
		return result;
	}


	/**
	 * Returns the selected file with a valid extension.
	 * @return
	 */
	@Override
	protected File getSelectedFile() {
		File result = fileChooser.getSelectedFile();
		if (result != null) {
			GraphicFilter filter = (GraphicFilter)getFileChooser().getFileFilter(); 
			if (!filter.validExtension(result.getName())) {
				result = new File(result.getAbsolutePath() + filter.getDefaultExtension());
			}
		}
		return result;
	}


	@Override
	protected boolean onApply(File file) {
		Cursor cursor = MainFrame.getInstance().getCursor();
		MainFrame.getInstance().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		try {
      try {
      	GraphicWriterFactory.getInstance().getWriter(getFormat()).write(getDocument(), 
      			PositionPaintFactory.getInstance().getPainter(
								treeFrame.getTreeViewPanel().getPainterType()), getHints(), file);
      	
      	if (Desktop.isDesktopSupported()) {
      		try {
		      	if (getOpenRadioButton().isSelected()) {
	      			Desktop.getDesktop().open(file);
		      	}
		      	else if (getPrintRadioButton().isSelected()) {
		      		Desktop.getDesktop().print(file);
		      	}
      		}
      		catch (IOException e) {
      			String command = "open";
		      	if (getPrintRadioButton().isSelected()) {
		      		command = "print";
		      	}
      			JOptionPane.showMessageDialog(this, "The error \"" + 
      					e.getMessage() + "\" occured when trying to " + command + 
      					" the file \"" + file.getAbsolutePath() + "\".", "Error",  
      					JOptionPane.ERROR_MESSAGE);
      		}
      	}
      	else if (!getNoneRadioButton().isSelected()) {
    			JOptionPane.showMessageDialog(this, "Your system does not support " +
    					"opening or printing files from here.", 
    					"Commands not supported",JOptionPane.ERROR_MESSAGE);
      	}
			}
      catch (IOException ex) {
      	JOptionPane.showMessageDialog(this, "The excpeption \"" + ex.getMessage() + 
      			"\" occurred when trying to write to the file \"" + file.getAbsolutePath() + "\".", "Error", 
      			JOptionPane.ERROR_MESSAGE);
      }
			catch (OutOfMemoryError ex) {
				WikiHelpOptionPane.showMessageDialog(MainFrame.getInstance(), 
						"There is not enough memory avialable.\n" + 
						"You can try to run TreeGraph with the following command line options for " +
						"the Java Virtual Machine from the TreeGraph installation directory:\n" + 
						"\"java -Xms64m -Xmx1024m -jar TreeGraph.jar\"", 
						Main.getInstance().getWikiHelp(), 33);
				return false;
			}
		}
		finally {
			MainFrame.getInstance().setCursor(cursor);
		}
		return true;
	}


	private void convertValue(JTextField textField, ItemEvent e) {
		String result = "";
		float value = Math2.parseFloat(textField.getText());
  	switch (lastSelection) {  
  		case 0:  // Millimeter
  	  	if (e.getItem().equals(UNIT_POINTS)) {
  	  		result = decimalFormat.format(DistanceValue.millimetersToPoints(value));
  	  	}
  	  	else if (e.getItem().equals(UNIT_PIXELS)) {
  	  		result = decimalFormat.format(resolutionInput.millimetersToPixels(value));
  	  	}
  	    break;
  		case 1:  // Points
  			value = DistanceValue.pointsToMillimeters(value);
  	  	if (e.getItem().equals(UNIT_MILLIMETERS)) {
  	  		result = decimalFormat.format(value);
  	  	}
  	  	else if (e.getItem().equals(UNIT_PIXELS)) {
  	  		result = decimalFormat.format(resolutionInput.millimetersToPixels(value));
  	  	}
  			break;
  		case 2:  // Pixels
  			value = resolutionInput.pixelsToMillimeters(value);
  	  	if (e.getItem().equals(UNIT_MILLIMETERS)) {
  	  		result = decimalFormat.format(value);
  	  	}
  	  	else if (e.getItem().equals(UNIT_POINTS)) {
  	  		result = decimalFormat.format(DistanceValue.millimetersToPoints(value));
  	  	}
  			break;
  	}
		textField.setText(result);
	}
	
	
	private void setPereferencesPanel(GraphicFormat format) {
		preferencesPanel = null;
		if (format != null) {
			preferencesPanel = PreferencesPanelFactory.getInstance().getPanel(format);
		}
		
		getOuterPreferencesPanel().removeAll();
		if (preferencesPanel != null) {
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			gridBagConstraints.fill = GridBagConstraints.BOTH;
			gridBagConstraints.weightx = 1.0;
			gridBagConstraints.weighty = 1.0;
			getOuterPreferencesPanel().add((JComponent)preferencesPanel, 
					gridBagConstraints);
			getOuterPreferencesPanel().setVisible(true);
		}
		else {
			getOuterPreferencesPanel().setVisible(false);
		}
		pack();
	}
	
	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setTitle("Export to graphic/ PDF");
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
			jContentPane.add(getDimensionsPanel(), null);
			jContentPane.add(getOuterPreferencesPanel(), null);
			jContentPane.add(getCommandPanel(), null);
			getApplyButton().setVisible(false);
			getOkButton().setText("Export");
			jContentPane.add(getButtonsPanel(), null);
		}
		return jContentPane;
	}


	/**
	 * This method initializes pngPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getDimensionsPanel() {
		if (dimensionsPanel == null) {
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.gridx = 0;
			gridBagConstraints11.gridy = 2;
			spacerLabel = new JLabel();
			spacerLabel.setText(" ");
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints5.gridy = 0;
			gridBagConstraints5.weightx = 1.0;
			gridBagConstraints5.gridheight = 2;
			gridBagConstraints5.gridx = 2;
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints4.gridy = 1;
			gridBagConstraints4.weightx = 1.0;
			gridBagConstraints4.gridx = 1;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints2.gridy = 0;
			gridBagConstraints2.weightx = 1.0;
			gridBagConstraints2.gridx = 1;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.anchor = GridBagConstraints.WEST;
			gridBagConstraints1.gridy = 1;
			heightLabel = new JLabel();
			heightLabel.setText("Height: ");
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.anchor = GridBagConstraints.WEST;
			gridBagConstraints.gridy = 0;
			widthLabel = new JLabel();
			widthLabel.setText("Width: ");
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 1;
			gridBagConstraints3.gridwidth = 1;
			gridBagConstraints3.anchor = GridBagConstraints.WEST;
			gridBagConstraints3.gridy = 1;
			dimensionsPanel = new JPanel();
			dimensionsPanel.setLayout(new GridBagLayout());
			dimensionsPanel.setBorder(BorderFactory.createTitledBorder(null, "Dimensions and resolution", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			dimensionsPanel.add(widthLabel, gridBagConstraints);
			dimensionsPanel.add(heightLabel, gridBagConstraints1);
			dimensionsPanel.add(getWidthTextField(), gridBagConstraints2);
			dimensionsPanel.add(getHeightTextField(), gridBagConstraints4);
			dimensionsPanel.add(getUnitComboBox(), gridBagConstraints5);
			dimensionsPanel.add(spacerLabel, gridBagConstraints11);
			resolutionInput = new ResolutionInput("Resolution: ", dimensionsPanel, 3);
		}
		return dimensionsPanel;
	}


	/**
	 * This method initializes widthTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getWidthTextField() {
		if (widthTextField == null) {
			widthTextField = new JTextField();
			widthTextField.addKeyListener(new java.awt.event.KeyAdapter() {   
				@Override
				public void keyReleased(java.awt.event.KeyEvent e) {    
					try {
						getHeightTextField().setText(decimalFormat.format(
								Math2.parseFloat(widthTextField.getText()) / aspectRatio));
					}
					catch (NumberFormatException ex) {
						getHeightTextField().setText("");
					}
				}
			});
		}
		return widthTextField;
	}


	/**
	 * This method initializes heightTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getHeightTextField() {
		if (heightTextField == null) {
			heightTextField = new JTextField();
			heightTextField.addKeyListener(new java.awt.event.KeyAdapter() {
				@Override
				public void keyReleased(java.awt.event.KeyEvent e) {
					try {
						getWidthTextField().setText(decimalFormat.format(
								Math2.parseFloat(heightTextField.getText()) * aspectRatio));
					}
					catch (NumberFormatException ex) {
						getWidthTextField().setText("");
					}
				}
			});
		}
		return heightTextField;
	}


	/**
	 * This method initializes unitComboBox	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getUnitComboBox() {
		if (unitComboBox == null) {
			unitComboBox = new JComboBox();
			unitComboBox.addItem(UNIT_MILLIMETERS);
			unitComboBox.addItem(UNIT_POINTS);
			unitComboBox.addItem(UNIT_PIXELS);
			unitComboBox.addItemListener(new ItemListener() {
			    public void itemStateChanged(ItemEvent e) {
			  	  if (e.getStateChange() == ItemEvent.SELECTED) {
			  	  	convertValue(getWidthTextField(), e);
			  	  	convertValue(getHeightTextField(), e);
			  			lastSelection = getUnitComboBox().getSelectedIndex();
			  	  }
			    }
	  	  });
		}
		return unitComboBox;
	}


	/**
	 * <p>This method initializes fileChooser.</p>
	 * <p>It should only be called if <code>GraphicWriterFactory</code> contains
	 * at least one file filter.</p>	
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
			GraphicFilter[] filters = GraphicWriterFactory.getInstance().getAllFilters();
			for (int i = 0; i < filters.length; i++) {
  			fileChooser.addChoosableFileFilter(filters[i]);
			}
  		fileChooser.setFileFilter(filters[0]);
			setPereferencesPanel((filters[0]).getFormat());
  		
			fileChooser.addPropertyChangeListener(new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent e) {
					if (e.getPropertyName().equals(JFileChooser.FILE_FILTER_CHANGED_PROPERTY)) {
						if (e.getNewValue() instanceof GraphicFilter) {
							setPereferencesPanel(((GraphicFilter)e.getNewValue()).getFormat());
						}
						else {
							setPereferencesPanel(null);
						}
					}
				}
			});
		}
		return fileChooser;
	}


	/**
	 * This method initializes fileChooserPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getFileChooserPanel() {
		if (fileChooserPanel == null) {
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.fill = GridBagConstraints.BOTH;
			gridBagConstraints7.weighty = 1.0;
			gridBagConstraints7.weightx = 1.0;
			fileChooserPanel = new JPanel();
			fileChooserPanel.setLayout(new GridBagLayout());
			fileChooserPanel.setBorder(BorderFactory.createTitledBorder(null, "File", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			fileChooserPanel.add(getFileChooser(), gridBagConstraints7);
		}
		return fileChooserPanel;
	}


	/**
	 * This method initializes preferencesPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getOuterPreferencesPanel() {
		if (outerPreferencesPanel == null) {
			outerPreferencesPanel = new JPanel();
			outerPreferencesPanel.setLayout(new GridBagLayout());
			outerPreferencesPanel.setBorder(BorderFactory.createTitledBorder(null, "Options", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
		}
		return outerPreferencesPanel;
	}


	/**
	 * This method initializes commandPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getCommandPanel() {
		if (commandPanel == null) {
			GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			gridBagConstraints9.gridx = 2;
			gridBagConstraints9.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints9.weightx = 1.0;
			gridBagConstraints9.anchor = GridBagConstraints.CENTER;
			gridBagConstraints9.gridy = 0;
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.gridx = 1;
			gridBagConstraints8.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints8.weightx = 1.0;
			gridBagConstraints8.anchor = GridBagConstraints.CENTER;
			gridBagConstraints8.gridy = 0;
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.gridx = 0;
			gridBagConstraints6.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints6.weightx = 1.0;
			gridBagConstraints6.anchor = GridBagConstraints.CENTER;
			gridBagConstraints6.gridy = 0;
			commandPanel = new JPanel();
			commandPanel.setLayout(new GridBagLayout());
			commandPanel.setBorder(BorderFactory.createTitledBorder(null, "Command", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			commandPanel.add(getOpenRadioButton(), gridBagConstraints6);
			commandPanel.add(getPrintRadioButton(), gridBagConstraints8);
			commandPanel.add(getNoneRadioButton(), gridBagConstraints9);
			getCommandGroup();
		}
		return commandPanel;
	}


	/**
	 * This method initializes openRadioButton	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getOpenRadioButton() {
		if (openRadioButton == null) {
			openRadioButton = new JRadioButton();
			openRadioButton.setText("Open");
		}
		return openRadioButton;
	}


	/**
	 * This method initializes printRadioButton	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getPrintRadioButton() {
		if (printRadioButton == null) {
			printRadioButton = new JRadioButton();
			printRadioButton.setText("Print");
		}
		return printRadioButton;
	}


	/**
	 * This method initializes noneRadioButton	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getNoneRadioButton() {
		if (noneRadioButton == null) {
			noneRadioButton = new JRadioButton();
			noneRadioButton.setText("None");
			noneRadioButton.setSelected(true);
		}
		return noneRadioButton;
	}


	public ButtonGroup getCommandGroup() {
		if (commandGroup == null) {
			commandGroup = new ButtonGroup();
			commandGroup.add(getOpenRadioButton());
			commandGroup.add(getPrintRadioButton());
			commandGroup.add(getNoneRadioButton());
		}
		return commandGroup;
	}
}