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
package info.bioinfweb.treegraph.gui.dialogs.elementformats;


import info.bioinfweb.commons.changemonitor.ChangeMonitor;
import info.bioinfweb.commons.swing.SwingChangeMonitor;
import info.bioinfweb.treegraph.document.TextElement;
import info.bioinfweb.treegraph.document.format.ConcreteTextFormats;
import info.bioinfweb.treegraph.document.format.TextFormats;
import info.bioinfweb.treegraph.document.format.operate.FontNameOperator;
import info.bioinfweb.treegraph.document.format.operate.FormatOperator;
import info.bioinfweb.treegraph.document.format.operate.TextHeightOperator;
import info.bioinfweb.treegraph.document.format.operate.TextStyleOperator;
import info.bioinfweb.treegraph.gui.dialogs.DistanceValueInput;
import info.bioinfweb.treegraph.gui.treeframe.TreeSelection;
import info.bioinfweb.treegraph.gui.treeframe.TreeViewPanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;



/**
 * Panel of the {@link ElementFormatsDialog} used to edit the font formats (not including the font color).
 * 
 * @author Ben St&ouml;ver
 * @see ElementFormatsDialog
 */
public class FontFormatsPanel extends JPanel implements ElementFormatsTab {
	private static final long serialVersionUID = 1L;
	private static final int VISIBLE_ROW_COUNT = 8;
	
	
	private JPanel fontFamilyPanel = null;
	private JPanel fontStylePanel = null;
	private JPanel fontHeightPanel = null;
	private JPanel previewPanel = null;
	private JCheckBox boldCheckBox = null;
	private JCheckBox italicCheckBox = null;
	private JCheckBox underlineCheckBox = null;
	private DistanceValueInput fontHeightInput = null;
	private JTextField previewTextField = null;
	private JList<String> fontFamilyList = null;
	private JTextField fontFamilyTextField = null;
	
	private SwingChangeMonitor fontFamilyMonitor = new SwingChangeMonitor();
	private ChangeMonitor boldMonitor = new ChangeMonitor();
	private ChangeMonitor italicMonitor = new ChangeMonitor();
	private ChangeMonitor underlineMonitor = new ChangeMonitor();


	/**
	 * This is the default constructor
	 */
	public FontFormatsPanel(boolean showTextHeightInput) {
		super();
		initialize(showTextHeightInput);
	}


	@Override
	public boolean setValues(TreeSelection selection) {
		TextElement first = selection.getFirstElementOfType(TextElement.class);
		boolean result = (first != null);
		if (result) {
			setValue(first.getFormats());
		}
		return result;
	}


	@Override
	public String title() {
		return "Font formats";
	}


	@Override
	public void addOperators(List<FormatOperator> list) {
		if (fontFamilyMonitor.hasChanged()) {
			list.add(new FontNameOperator(getFontFamilyTextField().getText()));
		}
		if (boldMonitor.hasChanged()) {
			list.add(new TextStyleOperator(getBoldCheckBox().isSelected(), 
					TextFormats.BOLD));
		}
		if (italicMonitor.hasChanged()) {
			list.add(new TextStyleOperator(getItalicCheckBox().isSelected(), 
					TextFormats.ITALIC));
		}
		if (underlineMonitor.hasChanged()) {
			list.add(new TextStyleOperator(getUnderlineCheckBox().isSelected(),
					TextFormats.UNDERLINE));
		}
		if ((fontHeightInput != null) && fontHeightInput.getChangeMonitor().hasChanged()) {
			list.add(new TextHeightOperator(getFontHeightInput().getValue()));
		}
	}
	
	
	@Override
	public void addError(List<String> list) {
		if ((fontHeightInput != null) && fontHeightInput.getValue().getInMillimeters() < 0) {
			list.add("The font height cannot be less than 0.");
		}
	}


	private DistanceValueInput getFontHeightInput() {
		return fontHeightInput;
	}


	public void setValue(TextFormats f) {
		getFontFamilyTextField().setText(f.getFontName());
		selectInFontList(f.getFontName());
		if (getFontHeightInput() != null) {
			getFontHeightInput().setValue(f.getTextHeight());  // Muss vor CheckBoxes kommen, da sonst Fehler in preview() auftritt
		}
		getBoldCheckBox().getModel().setSelected(f.hasTextStyle(TextFormats.BOLD));
		getItalicCheckBox().getModel().setSelected(f.hasTextStyle(TextFormats.ITALIC));
		getUnderlineCheckBox().getModel().setSelected(f.hasTextStyle(TextFormats.UNDERLINE));
	}
	
	
	public void assignValue(TextFormats f) {
		f.setFontName(getFontFamilyTextField().getText());
		f.setTextStyle(TextFormats.PLAIN);
		if (getFontHeightInput() != null) {
			getFontHeightInput().assignValueTo(f.getTextHeight());
		}
		if (getBoldCheckBox().getModel().isSelected()) {
			f.addTextStyle(TextFormats.BOLD);
		}
		if (getItalicCheckBox().getModel().isSelected()) {
			f.addTextStyle(TextFormats.ITALIC);
		}
		if (getUnderlineCheckBox().getModel().isSelected()) {
			f.addTextStyle(TextFormats.UNDERLINE);
		}
	}
	
	
	public void resetChangeMonitors() {
		fontFamilyMonitor.reset();
		if (fontHeightInput != null) {
			fontHeightInput.getChangeMonitor().reset();
		}
		boldMonitor.reset();
		italicMonitor.reset();
		underlineMonitor.reset();
	}
	
	
	public void preview() {
		TextFormats f = new ConcreteTextFormats();
		assignValue(f);
		getPreviewTextField().setFont(f.getFont(TreeViewPanel.PIXELS_PER_MM_100));
	}
	
	
	/**
	 * Selects an entry in the font list which is equal or similar to the specified string.
	 * @param value
	 */
	private void selectInFontList(String value) {
		if (getFontFamilyList().getModel().getSize() > 0) {
			int pos = 0;
			while ((pos < getFontFamilyList().getModel().getSize()) && 
					(getFontFamilyList().getModel().getElementAt(pos).toString().compareToIgnoreCase(value) < 0)) {
				
				pos++;
			}
			getFontFamilyList().setSelectedIndex(pos);
			getFontFamilyList().ensureIndexIsVisible(pos);
		}
		else {
			getFontFamilyList().clearSelection();
		}
  }
	
	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize(boolean showTextHeightInput) {
		this.setSize(300, 200);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.add(getFontFamilyPanel(), null);
		this.add(getFontStylePanel(), null);
		if (showTextHeightInput) {
			this.add(getFontHeightPanel(), null);
		}
		this.add(getPreviewPanel(), null);
	}


	/**
	 * This method initializes fontFamilyPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getFontFamilyPanel() {
		if (fontFamilyPanel == null) {
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.fill = GridBagConstraints.BOTH;
			gridBagConstraints2.gridy = 1;
			gridBagConstraints2.weightx = 1.0;
			gridBagConstraints2.weighty = 1.0;
			gridBagConstraints2.gridx = 0;
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints11.gridy = 0;
			gridBagConstraints11.weightx = 1.0;
			gridBagConstraints11.gridx = 0;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.fill = GridBagConstraints.BOTH;
			gridBagConstraints.gridy = 0;
			gridBagConstraints.weightx = 1.0;
			gridBagConstraints.weighty = 1.0;
			gridBagConstraints.gridx = 0;
			fontFamilyPanel = new JPanel();
			fontFamilyPanel.setLayout(new GridBagLayout());
			fontFamilyPanel.setBorder(BorderFactory.createTitledBorder(null, "Font family", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			fontFamilyPanel.add(getFontFamilyTextField(), gridBagConstraints11);
			fontFamilyPanel.add(new JScrollPane(getFontFamilyList()), gridBagConstraints2);
		}
		return fontFamilyPanel;
	}


	/**
	 * This method initializes fontStylePanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getFontStylePanel() {
		if (fontStylePanel == null) {
			fontStylePanel = new JPanel();
			fontStylePanel.setLayout(new FlowLayout());
			fontStylePanel.setBorder(BorderFactory.createTitledBorder(null, "Style", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			fontStylePanel.add(getBoldCheckBox(), null);
			fontStylePanel.add(getItalicCheckBox(), null);
			fontStylePanel.add(getUnderlineCheckBox(), null);
			fontStylePanel.setMaximumSize(new Dimension(fontStylePanel.getMaximumSize().width, fontStylePanel.getMinimumSize().height));  // Otherwise this component would be enlarged if the dialog height is increased.
		}
		return fontStylePanel;
	}


	/**
	 * This method initializes fontHeightPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getFontHeightPanel() {
		if (fontHeightPanel == null) {
			fontHeightPanel = new JPanel();
			fontHeightPanel.setLayout(new GridBagLayout());
			fontHeightPanel.setBorder(BorderFactory.createTitledBorder(null, "Font height", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			fontHeightInput = new DistanceValueInput("", fontHeightPanel, 0);
			fontHeightPanel.setMaximumSize(new Dimension(fontHeightPanel.getMaximumSize().width, fontHeightPanel.getMinimumSize().height));  // Otherwise this component would be enlarged if the dialog height is increased.
		}
		return fontHeightPanel;
	}


	/**
	 * This method initializes previewPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getPreviewPanel() {
		if (previewPanel == null) {
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.gridy = 0;
			gridBagConstraints1.weightx = 1.0;
			previewPanel = new JPanel();
			previewPanel.setLayout(new GridBagLayout());
			previewPanel.setBorder(BorderFactory.createTitledBorder(null, "Preview", 
					TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, 
					new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			previewPanel.add(getPreviewTextField(), gridBagConstraints1);
			previewPanel.setMaximumSize(new Dimension(previewPanel.getMaximumSize().width, previewPanel.getMinimumSize().height));  // Otherwise this component would be enlarged if the dialog height is increased.
		}
		return previewPanel;
	}


	/**
	 * This method initializes boldCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getBoldCheckBox() {
		if (boldCheckBox == null) {
			boldCheckBox = new JCheckBox();
			boldCheckBox.setText("Bold");
			boldCheckBox.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(java.awt.event.ItemEvent e) {
					boldMonitor.registerChange();
					preview();
				}
			});
		}
		return boldCheckBox;
	}


	/**
	 * This method initializes italicCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getItalicCheckBox() {
		if (italicCheckBox == null) {
			italicCheckBox = new JCheckBox();
			italicCheckBox.setText("Italic");
			italicCheckBox.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(java.awt.event.ItemEvent e) {
					italicMonitor.registerChange();
					preview();
				}
			});
		}
		return italicCheckBox;
	}


	/**
	 * This method initializes underlineCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getUnderlineCheckBox() {
		if (underlineCheckBox == null) {
			underlineCheckBox = new JCheckBox();
			underlineCheckBox.setText("Underline");
			underlineCheckBox.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(java.awt.event.ItemEvent e) {
					underlineMonitor.registerChange();
				}
			});
		}
		return underlineCheckBox;
	}


	/**
	 * This method initializes previewTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getPreviewTextField() {
		if (previewTextField == null) {
			previewTextField = new JTextField();
			previewTextField.setText("abcdefghijklmnopqrstuvwxyz0123456789");
			previewTextField.setEditable(false);
		}
		return previewTextField;
	}


	/**
	 * This method initializes fontFamilyList	
	 * 	
	 * @return javax.swing.JList	
	 */
	private JList<String> getFontFamilyList() {
		if (fontFamilyList == null) {
			fontFamilyList = new JList<String>(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
			fontFamilyList.setVisibleRowCount(VISIBLE_ROW_COUNT);
			fontFamilyList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			fontFamilyList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
					public void valueChanged(javax.swing.event.ListSelectionEvent e) {
						if (!e.getValueIsAdjusting()) {
							if (getFontFamilyList().isFocusOwner()) {  // Muss ebenfalls geprüft werden, da es sonst zu Division durch Null kommt, die irgendwie mit dem Wechselseitigen Aufruf der beiden Listener bei der Aktualisierung in onExecute kommt. (!getFontFamilyTextField().isFocusOwner() reicht als Bedingung nicht.)								
								getFontFamilyTextField().setText(fontFamilyList.getModel().getElementAt(
										fontFamilyList.getSelectedIndex()).toString());
								preview();
							}
						}
					}
				});
		}
		return fontFamilyList;
	}


	/**
	 * This method initializes fontFamilyTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getFontFamilyTextField() {
		if (fontFamilyTextField == null) {
			fontFamilyTextField = new JTextField();
			fontFamilyTextField.addKeyListener(new java.awt.event.KeyAdapter() {
				@Override
				public void keyReleased(KeyEvent e) {
					selectInFontList(getFontFamilyTextField().getText());
				}
			});
			fontFamilyTextField.getDocument().addDocumentListener(fontFamilyMonitor);
		}
		return fontFamilyTextField;
	}

}