/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2014  Ben Stöver, Kai Müller
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


import info.bioinfweb.treegraph.document.TextElement;
import info.bioinfweb.treegraph.document.format.BranchFormats;
import info.bioinfweb.treegraph.document.format.TextFormats;
import info.bioinfweb.treegraph.document.format.operate.DecimalFormatOperator;
import info.bioinfweb.treegraph.document.format.operate.FormatOperator;
import info.bioinfweb.treegraph.gui.treeframe.TreeSelection;
import info.webinsel.util.swing.LocaleComboBoxModel;
import info.webinsel.util.swing.SwingChangeMonitor;
import java.awt.GridBagLayout;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import java.awt.GridBagConstraints;
import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.Font;
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import java.awt.Insets;



/**
 * Panel of the {@link ElementFormatsDialog} used to edit the decimal formats of text elements.
 * 
 * @author Ben St&ouml;ver
 * @see ElementFormatsDialog
 */
public class DecimalFormatPanel extends JPanel implements ElementFormatTab {
	private static final long serialVersionUID = 1L;
	public static final double FORMAT_EXAMPLE = 1125024.87356218; 
	
	
	private JPanel formatPanel = null;
	private JPanel examplePanel = null;
	private JLabel formatLabel = null;
	private JTextField formatTextField = null;
	private JTable predifinedTable = null;
	private JComboBox localeComboBox = null;
	private JLabel localeLabel = null;
	private SwingChangeMonitor changeMonitor = new SwingChangeMonitor();  //  @jve:decl-index=0:
	private JScrollPane tableScrollPane = null;
	private JLabel positiveExampleLabel = null;
	private JLabel negativeExampleLabel = null;
	private JLabel hintLabel = null;


	/**
	 * This is the default constructor
	 */
	public DecimalFormatPanel() {
		super();
		initialize();
	}


	private Locale getLocaleValue() {
		return ((LocaleComboBoxModel)getLocaleComboBox().getModel()).
		    getSelectedLocale();
	}
	
	
	private DecimalFormat getDecimalFormat() {
		DecimalFormat result;
		try {
  		result = new DecimalFormat(getFormatTextField().getText());
		}
		catch (IllegalArgumentException e) {
			result = new DecimalFormat();
			JOptionPane.showMessageDialog(null, "The pattern \"" + getFormatTextField().getText() + "\" is invalid.", "Invalid pattern", JOptionPane.ERROR_MESSAGE);
		}
		result.setDecimalFormatSymbols(new DecimalFormatSymbols(getLocaleValue()));
		return result;
	}


	public void addOperators(List<FormatOperator> operators) {
		if (changeMonitor.hasChanged()) {
			operators.add(new DecimalFormatOperator(getDecimalFormat(), getLocaleValue()));
			//TODO Kann Formatsangabe ungültig sein?
		}
	}
	
	
	public void addError(List<String> list) {}


	public void resetChangeMonitors() {
		changeMonitor.reset();
	}


	public boolean setValues(TreeSelection selection) {
		boolean result = selection.containsType(TextElement.class);
		if (result) {
			TextFormats f = selection.getFirstElementOfType(TextElement.class).getFormats();
			getFormatTextField().setText(f.getDecimalFormat().toPattern());
			getLocaleComboBox().getModel().setSelectedItem(f.getLocale().getDisplayName());
			updateExample();
		}
		return result;
	}


	public String title() {
		return "Decimal format";
	}
	
	
	private void updateExample() {
		DecimalFormat f = getDecimalFormat();
		getPositiveExampleLabel().setText(f.format(FORMAT_EXAMPLE));
		getNegativeExampleLabel().setText(f.format(-FORMAT_EXAMPLE));
	}


	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(300, 200);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.add(getFormatPanel(), null);
		this.add(getExamplePanel(), null);
	}


	/**
	 * This method initializes formatPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getFormatPanel() {
		if (formatPanel == null) {
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.gridx = 0;
			gridBagConstraints7.gridwidth = 2;
			gridBagConstraints7.anchor = GridBagConstraints.WEST;
			gridBagConstraints7.insets = new Insets(7, 0, 3, 0);
			gridBagConstraints7.gridy = 3;
			hintLabel = new JLabel();
			hintLabel.setText("A definition of the format string syntax can be found in the help (button below).");
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.fill = GridBagConstraints.BOTH;
			gridBagConstraints6.gridy = 1;
			gridBagConstraints6.weightx = 1.0;
			gridBagConstraints6.weighty = 1.0;
			gridBagConstraints6.gridx = 1;
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.gridx = 0;
			gridBagConstraints5.anchor = GridBagConstraints.WEST;
			gridBagConstraints5.gridy = 2;
			localeLabel = new JLabel();
			localeLabel.setText("Locale: ");
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints4.gridy = 2;
			gridBagConstraints4.weightx = 1.0;
			gridBagConstraints4.insets = new Insets(2, 0, 0, 0);
			gridBagConstraints4.gridx = 1;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints2.gridy = 0;
			gridBagConstraints2.weightx = 1.0;
			gridBagConstraints2.insets = new Insets(0, 0, 2, 0);
			gridBagConstraints2.gridx = 1;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.anchor = GridBagConstraints.WEST;
			gridBagConstraints1.gridy = 0;
			formatLabel = new JLabel();
			formatLabel.setText("Format:");
			formatPanel = new JPanel();
			formatPanel.setLayout(new GridBagLayout());
			formatPanel.setBorder(BorderFactory.createTitledBorder(null, "Format", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			formatPanel.add(formatLabel, gridBagConstraints1);
			formatPanel.add(getFormatTextField(), gridBagConstraints2);
			formatPanel.add(getLocaleComboBox(), gridBagConstraints4);
			formatPanel.add(localeLabel, gridBagConstraints5);
			formatPanel.add(getTableScrollPane(), gridBagConstraints6);
			formatPanel.add(hintLabel, gridBagConstraints7);
		}
		return formatPanel;
	}


	/**
	 * This method initializes examplePanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getExamplePanel() {
		if (examplePanel == null) {
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.weightx = 1.0;
			gridBagConstraints3.insets = new Insets(0, 0, 5, 0);
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 1;
			gridBagConstraints.fill = GridBagConstraints.NONE;
			gridBagConstraints.anchor = GridBagConstraints.CENTER;
			gridBagConstraints.weightx = 1.0;
			gridBagConstraints.insets = new Insets(0, 0, 5, 0);
			gridBagConstraints.gridy = 0;
			negativeExampleLabel = new JLabel();
			negativeExampleLabel.setText(" ");
			positiveExampleLabel = new JLabel();
			positiveExampleLabel.setText(" ");
			examplePanel = new JPanel();
			examplePanel.setLayout(new GridBagLayout());
			examplePanel.setBorder(BorderFactory.createTitledBorder(null, "Example", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			examplePanel.add(positiveExampleLabel, gridBagConstraints3);
			examplePanel.add(negativeExampleLabel, gridBagConstraints);
		}
		return examplePanel;
	}
	
	
	private JLabel getPositiveExampleLabel() {
		getExamplePanel();
		return positiveExampleLabel;
	}
	
	
	private JLabel getNegativeExampleLabel() {
		getExamplePanel();
		return negativeExampleLabel;
	}
	
	
	/**
	 * This method initializes formatTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getFormatTextField() {
		if (formatTextField == null) {
			formatTextField = new JTextField();
			formatTextField.getDocument().addDocumentListener(new DocumentListener() {
				public void changedUpdate(DocumentEvent e) {
					updateExample();
				}

				public void insertUpdate(DocumentEvent e) {
					updateExample();
				}

				public void removeUpdate(DocumentEvent e) {
					updateExample();
				}
			});
			formatTextField.getDocument().addDocumentListener(changeMonitor);
		}
		return formatTextField;
	}


	/**
	 * This method initializes predifinedTable	
	 * 	
	 * @return javax.swing.JTable	
	 */
	private JTable getPredifinedTable() {
		if (predifinedTable == null) {
			predifinedTable = new JTable(new DecimalFormatsTableModel());
			predifinedTable.setColumnSelectionAllowed(false);
			predifinedTable.setRowSelectionAllowed(true);
			predifinedTable.getTableHeader().setReorderingAllowed(false);
			predifinedTable.getSelectionModel().setSelectionMode(
					ListSelectionModel.SINGLE_SELECTION);
			predifinedTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {  //TODO Muss zusätzlich auch ein Model-Listener her? Muss der Tastenstatus initielisiert werden?
			  public void valueChanged(ListSelectionEvent e) {
			  	getFormatTextField().setText(
			  			getPredifinedTable().getModel().getValueAt(getPredifinedTable().
			  					getSelectedRows()[getPredifinedTable().getSelectedRowCount() - 1], 1).toString());
			  }
		  });
		}
		return predifinedTable;
	}


	/**
	 * This method initializes localeComboBox	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getLocaleComboBox() {
		if (localeComboBox == null) {
			localeComboBox = new JComboBox(new LocaleComboBoxModel(DecimalFormatSymbols.getAvailableLocales()));
			localeComboBox.addItemListener(changeMonitor);
			localeComboBox.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(java.awt.event.ItemEvent e) {
					updateExample();
				}
			});
		}
		return localeComboBox;
	}


	/**
	 * This method initializes tableScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getTableScrollPane() {
		if (tableScrollPane == null) {
			tableScrollPane = new JScrollPane();
			tableScrollPane.setViewportView(getPredifinedTable());
		}
		return tableScrollPane;
	}

}