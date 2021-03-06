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
package info.bioinfweb.treegraph.gui.dialogs.elementformats;


import info.bioinfweb.treegraph.document.Label;
import info.bioinfweb.treegraph.document.format.LabelFormats;
import info.bioinfweb.treegraph.document.format.operate.FormatOperator;
import info.bioinfweb.treegraph.document.format.operate.LabelAboveOperator;
import info.bioinfweb.treegraph.document.format.operate.LabelLineNumberOperator;
import info.bioinfweb.treegraph.document.format.operate.LabelLinePositionOperator;
import info.bioinfweb.treegraph.document.format.operate.LabelMarginBottomOperator;
import info.bioinfweb.treegraph.document.format.operate.LabelMarginLeftOperator;
import info.bioinfweb.treegraph.document.format.operate.LabelMarginRightOperator;
import info.bioinfweb.treegraph.document.format.operate.LabelMarginTopOperator;
import info.bioinfweb.treegraph.gui.dialogs.MarginInput;
import info.bioinfweb.treegraph.gui.treeframe.TreeSelection;
import info.bioinfweb.commons.Math2;
import info.bioinfweb.commons.text.StringUtils;
import info.bioinfweb.commons.swing.SwingChangeMonitor;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;
import javax.swing.text.NumberFormatter;



/**
 * Panel of the {@link ElementFormatsDialog} used to edit the general label formats.
 * 
 * @author Ben St&ouml;ver
 * @see LabelFormats
 */
public class LabelPanel extends JPanel implements ElementFormatsTab {
	private static final long serialVersionUID = 1L;

	
	private JRadioButton aboveRadioButton = null;
	private JRadioButton belowRadioButton = null;
	private JLabel lineNoLabel = null;
	private JFormattedTextField lineNoTextField = null;
	private JLabel linePosLabel = null;
	private JFormattedTextField linePosTextField = null;
	private ButtonGroup aboveBelowGroup = null;
	private SwingChangeMonitor aboveMonitor = new SwingChangeMonitor();
	private SwingChangeMonitor lineNoMonitor = new SwingChangeMonitor();
	private SwingChangeMonitor linePosMonitor = new SwingChangeMonitor();
	private JPanel aboveBelowPanel = null;
	private JPanel labelNumbersPanel = null;
	private JPanel marginPanel = null;
	private MarginInput marginInput = null;

	
	/**
	 * This is the default constructor
	 */
	public LabelPanel() {
		super();
		initialize();
	}


	public boolean setValues(TreeSelection selection) {
		Label first = selection.getFirstElementOfType(Label.class);
		boolean result = (first != null);
		if (result) {
			LabelFormats f = first.getFormats();
	  	getAboveRadioButton().setSelected(f.isAbove());
	  	getBelowRadioButton().setSelected(!f.isAbove());
			getLineNoTextField().setText(StringUtils.INTEGER_FORMAT.format(f.getLineNumber()));
			getLinePosTextField().setText(StringUtils.DOUBLE_FORMAT.format(f.getLinePosition()));
			getMarginInput().setValue(f.getMargin());
		}
		return result;
	}


	public String title() {
		return "Label formats";
	}


	public void addOperators(List<FormatOperator> operators) {
		if (aboveMonitor.hasChanged()) {
			operators.add(new LabelAboveOperator(getAboveRadioButton().isSelected()));
		}
		if (lineNoMonitor.hasChanged()) {
			operators.add(new LabelLineNumberOperator(Integer.parseInt(getLineNoTextField().getText())));
		}
		if (linePosMonitor.hasChanged()) {
			operators.add(new LabelLinePositionOperator(Math2.parseDouble(getLinePosTextField().getText())));
		}
		if (getMarginInput().getLeft().getChangeMonitor().hasChanged()) {
			operators.add(new LabelMarginLeftOperator(getMarginInput().getLeft().getValue()));
		}
		if (getMarginInput().getTop().getChangeMonitor().hasChanged()) {
			operators.add(new LabelMarginTopOperator(getMarginInput().getTop().getValue()));
		}
		if (getMarginInput().getRight().getChangeMonitor().hasChanged()) {
			operators.add(new LabelMarginRightOperator(getMarginInput().getRight().getValue()));
		}
		if (getMarginInput().getBottom().getChangeMonitor().hasChanged()) {
			operators.add(new LabelMarginBottomOperator(getMarginInput().getBottom().getValue()));
		}
	}


	public void addError(List<String> list) {}


	public void resetChangeMonitors() {
		aboveMonitor.reset();
		lineNoMonitor.reset();
		linePosMonitor.reset();
		getMarginInput().resetChangeMonitors();
	}
	
	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.add(getAboveBelowPanel(), null);
		this.add(getLabelNumbersPanel(), null);
		this.add(getMarginPanel(), null);
	}

	
	/**
	 * This method initializes labelAboveBelowPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getAboveBelowPanel() {
		if (aboveBelowPanel == null) {
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.gridx = 1;
			gridBagConstraints7.fill = GridBagConstraints.NONE;
			gridBagConstraints7.anchor = GridBagConstraints.WEST;
			gridBagConstraints7.gridy = 0;
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.gridx = 0;
			gridBagConstraints6.fill = GridBagConstraints.NONE;
			gridBagConstraints6.anchor = GridBagConstraints.WEST;
			gridBagConstraints6.weightx = 1.0;
			gridBagConstraints6.gridy = 0;
			aboveBelowPanel = new JPanel();
			aboveBelowPanel.setLayout(new GridBagLayout());
			aboveBelowPanel.setBorder(BorderFactory.createTitledBorder(null, "Position to branch", 
					TitledBorder.DEFAULT_JUSTIFICATION,	TitledBorder.DEFAULT_POSITION, 
					new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			aboveBelowPanel.add(getAboveRadioButton(), gridBagConstraints6);
			aboveBelowPanel.add(getBelowRadioButton(), gridBagConstraints7);
			getAboveBelowGroup();  // Elemente gruppieren
		}
		return aboveBelowPanel;
	}


	public ButtonGroup getAboveBelowGroup() {
		if (aboveBelowGroup == null) {
			aboveBelowGroup = new ButtonGroup();
			aboveBelowGroup.add(getAboveRadioButton());
			aboveBelowGroup.add(getBelowRadioButton());
		}
		return aboveBelowGroup;
	}


	/**
	 * This method initializes aboveRadioButton	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getAboveRadioButton() {
		if (aboveRadioButton == null) {
			aboveRadioButton = new JRadioButton();
			aboveRadioButton.setText("Above the branch");
			aboveRadioButton.getModel().addItemListener(aboveMonitor);
		}
		return aboveRadioButton;
	}


	/**
	 * This method initializes belowRadioButton	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getBelowRadioButton() {
		if (belowRadioButton == null) {
			belowRadioButton = new JRadioButton();
			belowRadioButton.setText("Below the branch");
			belowRadioButton.getModel().addItemListener(aboveMonitor);
		}
		return belowRadioButton;
	}


	/**
	 * This method initializes labelNumbersPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getLabelNumbersPanel() {
		if (labelNumbersPanel == null) {
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints3.gridy = 0;
			gridBagConstraints3.weightx = 1.0;
			gridBagConstraints3.gridx = 3;
			GridBagConstraints linePosLabelGBC = new GridBagConstraints();
			linePosLabelGBC.gridx = 2;
			linePosLabelGBC.anchor = GridBagConstraints.WEST;
			linePosLabelGBC.gridy = 0;
			linePosLabel = new JLabel();
			linePosLabel.setText("Line position: ");
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints1.gridy = 0;
			gridBagConstraints1.weightx = 1.0;
			gridBagConstraints1.gridx = 1;
			GridBagConstraints lineNoLabelGBC = new GridBagConstraints();
			lineNoLabelGBC.gridx = 0;
			lineNoLabelGBC.anchor = GridBagConstraints.WEST;
			lineNoLabelGBC.gridy = 0;
			lineNoLabel = new JLabel();
			lineNoLabel.setText("Line number: ");
			labelNumbersPanel = new JPanel();
			labelNumbersPanel.setLayout(new GridBagLayout());
			labelNumbersPanel.setBorder(BorderFactory.createTitledBorder(null, "Line position", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			labelNumbersPanel.add(lineNoLabel, lineNoLabelGBC);
			labelNumbersPanel.add(getLineNoTextField(), gridBagConstraints1);
			labelNumbersPanel.add(linePosLabel, linePosLabelGBC);
			labelNumbersPanel.add(getLinePosTextField(), gridBagConstraints3);
		}
		return labelNumbersPanel;
	}
	
	
	/**
	 * This method initializes lineNoTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JFormattedTextField getLineNoTextField() {
		if (lineNoTextField == null) {
			NumberFormatter format = new NumberFormatter(StringUtils.INTEGER_FORMAT);
			format.setValueClass(Integer.class);
			format.setMinimum(new Integer(0));
			format.setAllowsInvalid(false);
			lineNoTextField = new JFormattedTextField(format);
			lineNoTextField.getDocument().addDocumentListener(lineNoMonitor);
		}
		return lineNoTextField;
	}

	
	/**
	 * This method initializes linePosTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JFormattedTextField getLinePosTextField() {
		if (linePosTextField == null) {
			linePosTextField = new JFormattedTextField(StringUtils.DOUBLE_FORMAT);
			linePosTextField.getDocument().addDocumentListener(linePosMonitor);
		}
		return linePosTextField;
	}


	/**
	 * This method initializes marginPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getMarginPanel() {
		if (marginPanel == null) {
			marginPanel = new JPanel();
			marginPanel.setLayout(new GridBagLayout());
			marginPanel.setBorder(BorderFactory.createTitledBorder(null, "Margin", 
					TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, 
					new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			marginInput = new MarginInput("", marginPanel, 0);
		}
		return marginPanel;
	}


	private MarginInput getMarginInput() {
		getMarginPanel();
		return marginInput;
	}
}