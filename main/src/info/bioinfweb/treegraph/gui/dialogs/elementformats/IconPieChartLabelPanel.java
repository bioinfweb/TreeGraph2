/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2015-2016  Ben Stöver, Sarah Wiechers, Kai Müller
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
package info.bioinfweb.treegraph.gui.dialogs.elementformats;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import info.bioinfweb.treegraph.document.IconLabel;
import info.bioinfweb.treegraph.document.PieChartLabel;
import info.bioinfweb.treegraph.document.format.GraphicalLabelFormats;
import info.bioinfweb.treegraph.document.format.IconLabelFormats;
import info.bioinfweb.treegraph.document.format.PieChartLabelFormats;
import info.bioinfweb.treegraph.document.format.operate.FormatOperator;
import info.bioinfweb.treegraph.document.format.operate.IconFilledOperator;
import info.bioinfweb.treegraph.document.format.operate.InternalPieChartLinesOperator;
import info.bioinfweb.treegraph.document.format.operate.LabelHeightOperator;
import info.bioinfweb.treegraph.document.format.operate.IconOperator;
import info.bioinfweb.treegraph.document.format.operate.LabelWidthOperator;
import info.bioinfweb.treegraph.document.format.operate.NullPieChartLinesOperator;
import info.bioinfweb.treegraph.document.format.operate.PieColorOperator;
import info.bioinfweb.treegraph.gui.dialogs.DistanceValueInput;
import info.bioinfweb.treegraph.gui.dialogs.elementformats.piecolor.PieColorCellRenderer;
import info.bioinfweb.treegraph.gui.dialogs.elementformats.piecolor.PieColorListEntry;
import info.bioinfweb.treegraph.gui.treeframe.TreeSelection;
import info.bioinfweb.commons.changemonitor.ChangeMonitor;
import info.bioinfweb.commons.swing.SwingChangeMonitor;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JList;



/**
 * Panel of the {@link ElementFormatsDialog} used to edit the icon label color.
 * 
 * @author Ben St&ouml;ver
 * @since 2.0.25
 */
public class IconPieChartLabelPanel extends JPanel implements ElementFormatTab {
	public static final int PREVIEW_SIZE = 50;
	
	
	private SwingChangeMonitor iconMonitor = new SwingChangeMonitor();  //  @jve:decl-index=0:
	private SwingChangeMonitor iconFilledMonitor = new SwingChangeMonitor();  //  @jve:decl-index=0:
	private ChangeMonitor pieColorMonitor = new ChangeMonitor();  //  @jve:decl-index=0:
	private ChangeMonitor internalLinesMonitor = new ChangeMonitor();  //  @jve:decl-index=0:
	private ChangeMonitor nullLinesMonitor = new ChangeMonitor();  //  @jve:decl-index=0:
	private JPanel iconPanel = null;
	private JComboBox iconComboBox = null;
	private IconPreviewPanel iconPreviewPanel = null;
	private DistanceValueInput iconWidthInput = null; 
	private DistanceValueInput iconHeightInput = null;
	private JCheckBox iconFilledCheckBox = null;
	private JPanel dimensionPanel = null;
	private JPanel pieChartPanel = null;
	private JLabel previewLabel = null;
	private JScrollPane colorListScrollPane = null;
	private JList colorList = null;
	private JLabel colorLabel = null;
	private JCheckBox showInternalLinesCheckBox = null;
	private JCheckBox showNullLinesCheckBox = null;
	
	
	/**
	 * This is the default constructor
	 */
	public IconPieChartLabelPanel() {
		super();
		initialize();
		setIconValues(new IconLabelFormats(null));  // Set default formats
	}


	public void addError(List<String> list) {}
	
	
	public String title() {
		return "Icon/pie chart label formats";
	}
	
	
	public boolean setValues(TreeSelection selection) {
		IconLabel iconLabel = selection.getFirstElementOfType(IconLabel.class);
		PieChartLabel pieChartLabel = selection.getFirstElementOfType(PieChartLabel.class);
		boolean iconLabelSel = (iconLabel != null);
		boolean pieChartLabelSel = (pieChartLabel != null);
		
    getIconComboBox().setEnabled(iconLabelSel);
    getIconFilledCheckBox().setEnabled(iconLabelSel);
		if (iconLabelSel) {
			setIconValues(iconLabel.getFormats());
		}
		
		getColorList().setEnabled(pieChartLabelSel);
		getShowInternalLinesCheckBox().setEnabled(pieChartLabelSel);
		getShowNullLinesCheckBox().setEnabled(pieChartLabelSel);
		if (pieChartLabelSel) {
			PieChartLabelFormats f = pieChartLabel.getFormats();
			if (!iconLabelSel) {
				getWidthInput().setValue(f.getWidth());
				getHeightInput().setValue(f.getHeight());
			}
			
			getColorListModel().clear();
			for (int i = 0; i < pieChartLabel.valueCount(); i++) {
				getColorListModel().addElement(new PieColorListEntry(pieChartLabel.getValueID(i), 
						f.getPieColor(i)));
			}
			getShowInternalLinesCheckBox().setSelected(f.getShowInternalLines());
			getShowNullLinesCheckBox().setSelected(f.getShowNullLines());
		}
		return (iconLabelSel || pieChartLabelSel);
	}
	
	
	private void setIconValues(IconLabelFormats f) {
		getWidthInput().setValue(f.getWidth());
		getHeightInput().setValue(f.getHeight());
		getIconComboBox().setSelectedItem(f.getIcon());
		getIconFilledCheckBox().setSelected(f.getIconFilled());
	}
	
	
	private Color[] getColorArray() {
		Color[] colors = new Color[getColorListModel().size()];
		for (int i = 0; i < colors.length; i++) {
			colors[i] = getColorListEntry(i).getColor();
		}
		return colors;
	}
	
	
	public void addOperators(List<FormatOperator> operators) {
		if (iconMonitor.hasChanged()) {
			operators.add(new IconOperator(getIconComboBoxModel().getSelectedItem()));
		}
		if (getWidthInput().getChangeMonitor().hasChanged()) {
			operators.add(new LabelWidthOperator(getWidthInput().getValue()));
		}
		if (getHeightInput().getChangeMonitor().hasChanged()) {
			operators.add(new LabelHeightOperator(getHeightInput().getValue()));
		}
		if (iconFilledMonitor.hasChanged()) {
			operators.add(new IconFilledOperator(getIconFilledCheckBox().isSelected()));
		}
		if (pieColorMonitor.hasChanged()) {
			operators.add(new PieColorOperator(getColorArray()));
		}
		if (internalLinesMonitor.hasChanged()) {
			operators.add(new InternalPieChartLinesOperator(getShowInternalLinesCheckBox().isSelected()));
		}
		if (nullLinesMonitor.hasChanged()) {
			operators.add(new NullPieChartLinesOperator(getShowNullLinesCheckBox().isSelected()));
		}
	}
	
	
	public void resetChangeMonitors() {
		iconMonitor.reset();
		getWidthInput().getChangeMonitor().reset();
		getHeightInput().getChangeMonitor().reset();
		iconFilledMonitor.reset();
		pieColorMonitor.reset();
		internalLinesMonitor.reset();
		nullLinesMonitor.reset();
	}
	
	
	/**
	 * Sets the values of a formats object according to the user input. Special icon or pie chart formats are also
	 * set, if an corresponding instance is passed.
	 * @param f - the formats object to store the user input
	 */
	public void setLabelFormats(GraphicalLabelFormats f) {
		f.getWidth().assign(getWidthInput().getValue());
		f.getHeight().assign(getHeightInput().getValue());
		if (f instanceof IconLabelFormats) {
			IconLabelFormats iconFormats = (IconLabelFormats)f;
			iconFormats.setIcon(getIconComboBoxModel().getSelectedItem());
			iconFormats.setIconFilled(getIconFilledCheckBox().isSelected());
		}
		else if (f instanceof PieChartLabelFormats) {
			PieChartLabelFormats pieChartFormats = (PieChartLabelFormats)f;
			Color[] colors = getColorArray();
			for (int i = 0; i < colors.length; i++) {
				pieChartFormats.setPieColor(i, colors[i]);
			}
			pieChartFormats.setShowInternalLines(getShowInternalLinesCheckBox().isSelected());
			pieChartFormats.setShowNullLines(getShowNullLinesCheckBox().isSelected());
		}
	}
	
	
	public void setPieChartElementsVisible(boolean visible) {
		getPieChartPanel().setVisible(visible);
	}
	
	
	private void paintIconPreview() {
		getIconPreviewPanel().setIcon(getIconComboBoxModel().getSelectedItem());
		getIconPreviewPanel().getIconWidth().assign(getWidthInput().getValue());
		getIconPreviewPanel().getIconHeight().assign(getHeightInput().getValue());
		getIconPreviewPanel().setIconFilled(getIconFilledCheckBox().isSelected());
		getIconPreviewPanel().repaint();
	}
	
	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.add(getDimensionPanel(), null);
		this.add(getIconPanel(), null);
		this.add(getPieChartPanel(), null);
	}

	
	/**
	 * This method initializes iconPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getIconPanel() {
		if (iconPanel == null) {
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.gridx = 0;
			gridBagConstraints11.anchor = GridBagConstraints.WEST;
			gridBagConstraints11.gridy = 3;
			previewLabel = new JLabel();
			previewLabel.setText("Preview: ");
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.fill = GridBagConstraints.BOTH;
			gridBagConstraints.gridheight = 1;
			gridBagConstraints.gridwidth = 1;
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 3;
			gridBagConstraints.weightx = 1.0;
			gridBagConstraints.weighty = 1.0;
			gridBagConstraints.insets = new Insets(3, 3, 3, 3);
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.gridx = 0;
			gridBagConstraints8.gridwidth = 2;
			gridBagConstraints8.anchor = GridBagConstraints.WEST;
			gridBagConstraints8.gridy = 2;
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints4.gridy = 1;
			gridBagConstraints4.weightx = 1.0;
			gridBagConstraints4.gridwidth = 2;
			gridBagConstraints4.gridx = 0;
			iconPanel = new JPanel();
			iconPanel.setLayout(new GridBagLayout());
			iconPanel.setBorder(BorderFactory.createTitledBorder(null, "Icon", 
					TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, 
					new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			iconPanel.add(getIconComboBox(), gridBagConstraints4);
			iconPanel.add(getIconFilledCheckBox(), gridBagConstraints8);
			iconPanel.add(getIconPreviewPanel(), gridBagConstraints);
			iconPanel.add(previewLabel, gridBagConstraints11);
		}
		return iconPanel;
	}
	
	
	private DistanceValueInput getWidthInput() {
		getIconPanel();
		return iconWidthInput;
	}


	private DistanceValueInput getHeightInput() {
		getIconPanel();
		return iconHeightInput;
	}


	/**
	 * This method initializes iconComboBox	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getIconComboBox() {
		if (iconComboBox == null) {
			iconComboBox = new JComboBox(new LabelIconComboBoxModel());
			iconComboBox.addItemListener(iconMonitor);
			iconComboBox.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(java.awt.event.ItemEvent e) {
					if (getIconComboBoxModel().getSelectedItem() != null) {
						paintIconPreview();
					}
				}
			});
		}
		return iconComboBox;
	}
	
	
	private LabelIconComboBoxModel getIconComboBoxModel() {
		return (LabelIconComboBoxModel)getIconComboBox().getModel();
	}


	/**
	 * This method initializes iconPreviewPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private IconPreviewPanel getIconPreviewPanel() {
		if (iconPreviewPanel == null) {
			iconPreviewPanel = new IconPreviewPanel();
			iconPreviewPanel.setMinimumSize(new Dimension(PREVIEW_SIZE, PREVIEW_SIZE));
		}
		return iconPreviewPanel;
	}


	/**
	 * This method initializes iconFilledCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getIconFilledCheckBox() {
		if (iconFilledCheckBox == null) {
			iconFilledCheckBox = new JCheckBox();
			iconFilledCheckBox.setText("Fill icon");
			iconFilledCheckBox.addChangeListener(iconFilledMonitor);
			iconFilledCheckBox.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(java.awt.event.ItemEvent e) {
					paintIconPreview();
				}
			});
		}
		return iconFilledCheckBox;
	}


	/**
	 * This method initializes dimensionPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getDimensionPanel() {
		if (dimensionPanel == null) {
			dimensionPanel = new JPanel();
			dimensionPanel.setLayout(new GridBagLayout());
			dimensionPanel.setBorder(BorderFactory.createTitledBorder(null, "Dimensions", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
      iconWidthInput = new DistanceValueInput("Width: ", dimensionPanel, 1, 1);
      iconHeightInput = new DistanceValueInput("Height: ", dimensionPanel, 1, 3);
		}
		return dimensionPanel;
	}


	/**
	 * This method initializes pieChartPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getPieChartPanel() {
		if (pieChartPanel == null) {
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.gridx = 1;
			gridBagConstraints5.gridy = 2;
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 0;
			gridBagConstraints3.anchor = GridBagConstraints.WEST;
			gridBagConstraints3.gridy = 2;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.anchor = GridBagConstraints.WEST;
			gridBagConstraints2.gridwidth = 2;
			gridBagConstraints2.gridy = 0;
			colorLabel = new JLabel();
			colorLabel.setText("Sector colors:");
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.fill = GridBagConstraints.BOTH;
			gridBagConstraints1.gridy = 1;
			gridBagConstraints1.weightx = 1.0;
			gridBagConstraints1.weighty = 1.0;
			gridBagConstraints1.gridwidth = 2;
			gridBagConstraints1.gridx = 0;
			pieChartPanel = new JPanel();
			pieChartPanel.setLayout(new GridBagLayout());
			pieChartPanel.setBorder(BorderFactory.createTitledBorder(null, "Pie chart", 
					TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, 
					new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			pieChartPanel.add(getColorListScrollPane(), gridBagConstraints1);
			pieChartPanel.add(colorLabel, gridBagConstraints2);
			pieChartPanel.add(getShowInternalLinesCheckBox(), gridBagConstraints3);
			pieChartPanel.add(getShowNullLinesCheckBox(), gridBagConstraints5);
		}
		return pieChartPanel;
	}


	/**
	 * This method initializes colorListScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getColorListScrollPane() {
		if (colorListScrollPane == null) {
			colorListScrollPane = new JScrollPane();
			colorListScrollPane.setViewportView(getColorList());
		}
		return colorListScrollPane;
	}


	/**
	 * This method initializes colorList	
	 * 	
	 * @return javax.swing.JList	
	 */
	private JList getColorList() {
		if (colorList == null) {
			colorList = new JList(new DefaultListModel());
			colorList.setCellRenderer(new PieColorCellRenderer());
			colorList.addMouseListener(new java.awt.event.MouseAdapter() {
				public void mouseClicked(java.awt.event.MouseEvent e) {
					if (e.getClickCount() >= 2) {
						int index = getColorList().getSelectedIndex();
						if (index != -1) {
							PieColorListEntry entry = getColorListEntry(index);
							Color color = JColorChooser.showDialog(getTopLevelAncestor(), "Choose pie color", 
									entry.getColor());
							if (color != null) {
								entry.setColor(color);
								getColorListModel().set(index, entry);  // Repaint
								pieColorMonitor.registerChange();
							}
						}
					}
				}
			});
		}
		return colorList;
	}
	
	
	private DefaultListModel getColorListModel() {
		return (DefaultListModel)getColorList().getModel();
	}
	
	
	private PieColorListEntry getColorListEntry(int index) {
		return (PieColorListEntry)getColorListModel().get(index);
	}


	/**
	 * This method initializes showInternalLinesCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getShowInternalLinesCheckBox() {
		if (showInternalLinesCheckBox == null) {
			showInternalLinesCheckBox = new JCheckBox();
			showInternalLinesCheckBox.setText("Show internal lines");
			showInternalLinesCheckBox.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(java.awt.event.ItemEvent e) {
					internalLinesMonitor.registerChange();
				}
			});
		}
		return showInternalLinesCheckBox;
	}


	/**
	 * This method initializes showNullLinesCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getShowNullLinesCheckBox() {
		if (showNullLinesCheckBox == null) {
			showNullLinesCheckBox = new JCheckBox();
			showNullLinesCheckBox.setText("Show sectors with a value of 0");
			showNullLinesCheckBox.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(java.awt.event.ItemEvent e) {
					nullLinesMonitor.registerChange();
				}
			});
		}
		return showNullLinesCheckBox;
	}
}