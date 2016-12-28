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


import info.bioinfweb.treegraph.document.Legend;
import info.bioinfweb.treegraph.document.format.LegendFormats;
import info.bioinfweb.treegraph.document.format.LegendStyle;
import info.bioinfweb.treegraph.document.format.TextOrientation;
import info.bioinfweb.treegraph.document.format.operate.FormatOperator;
import info.bioinfweb.treegraph.document.format.operate.LegendMarginBottomOperator;
import info.bioinfweb.treegraph.document.format.operate.LegendMarginLeftOperator;
import info.bioinfweb.treegraph.document.format.operate.LegendMarginRightOperator;
import info.bioinfweb.treegraph.document.format.operate.LegendMarginTopOperator;
import info.bioinfweb.treegraph.document.format.operate.LegendPositionOperator;
import info.bioinfweb.treegraph.document.format.operate.LegendStyleOperator;
import info.bioinfweb.treegraph.document.format.operate.MinTreeDistanceOperator;
import info.bioinfweb.treegraph.document.format.operate.TextOrientationOperator;
import info.bioinfweb.treegraph.gui.dialogs.DistanceValueInput;
import info.bioinfweb.treegraph.gui.dialogs.MarginInput;
import info.bioinfweb.treegraph.gui.treeframe.TreeSelection;
import info.bioinfweb.commons.swing.SwingChangeMonitor;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.text.DecimalFormat;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;



/**
 * Panel of the {@link ElementFormatsDialog} used to edit the legend formats.
 * 
 * @author Ben St&ouml;ver
 * @see LegendFormats
 */
public class LegendPanel extends JPanel implements ElementFormatsTab {
	private static final long serialVersionUID = 1L;


	private DecimalFormat decimalFormat = new DecimalFormat("#########");  //  @jve:decl-index=0:
	private JPanel legendStylePanel = null;
	private JRadioButton bracketRadioButton = null;
	private JRadioButton braceRadioButton = null;
	private JPanel legendOrientationPanel = null;
	private JRadioButton horizontalRadioButton = null;
	private JRadioButton upRadioButton = null;
	private JRadioButton downRadioButton = null;
	private ButtonGroup styleGroup = null;  //  @jve:decl-index=0:
	private ButtonGroup orientationGroup = null;  //  @jve:decl-index=0:
	private SwingChangeMonitor styleMonitor = new SwingChangeMonitor();  //  @jve:decl-index=0:
	private SwingChangeMonitor orientationMonitor = new SwingChangeMonitor();  //  @jve:decl-index=0:
	private JPanel legendNumbersPanel = null;
	private JFormattedTextField legendPositionTextField = null;
	private SwingChangeMonitor legendPositionMonitor = new SwingChangeMonitor();  //  @jve:decl-index=0:
	private DistanceValueInput minTreeDistanceInput = null;
	private JLabel legendOrderLabel = null;
	private JPanel marginPanel = null;
	private MarginInput marginInput = null;

	
	/**
	 * This is the default constructor
	 */
	public LegendPanel() {
		super();
		initialize();
	}


	public boolean setValues(TreeSelection selection) {
		Legend first = selection.getFirstElementOfType(Legend.class);
		boolean result = (first != null);
		if (result) {
			LegendFormats f = first.getFormats();
	  	getBraceRadioButton().setSelected(
	  			f.getLegendStyle().equals(LegendStyle.BRACE));
	  	getBracketRadioButton().setSelected(
	  			f.getLegendStyle().equals(LegendStyle.BRACKET));
	  	getHorizontalRadioButton().setSelected(
	  			f.getOrientation().equals(TextOrientation.HORIZONTAL));
	  	getUpRadioButton().setSelected(
	  			f.getOrientation().equals(TextOrientation.UP));
	  	getDownRadioButton().setSelected(
	  			f.getOrientation().equals(TextOrientation.DOWN));
			getLegendPositionTextField().setText(decimalFormat.format(f.getPosition()));
			minTreeDistanceInput.setValue(f.getMinTreeDistance());
			getMarginInput().setValue(first.getFormats().getMargin());
		}
		return result;
	}


	public String title() {
		return "Legend formats";
	}


	public void addOperators(List<FormatOperator> operators) {
		if (styleMonitor.hasChanged()) {
			LegendStyle style = LegendStyle.BRACKET;
			if (getBraceRadioButton().isSelected()) {
				style = LegendStyle.BRACE;
			}
			operators.add(new LegendStyleOperator(style));			
		}
		if (orientationMonitor.hasChanged()) {
	    TextOrientation orientation = TextOrientation.DOWN;
			if (getHorizontalRadioButton().isSelected()) {
				orientation = TextOrientation.HORIZONTAL;
			}
			else if (getUpRadioButton().isSelected()) {
				orientation = TextOrientation.UP;
			}
			operators.add(new TextOrientationOperator(orientation));
		}
		if (legendPositionMonitor.hasChanged()) {
			operators.add(new LegendPositionOperator(Integer.parseInt(
					getLegendPositionTextField().getText())));
		}
		if (minTreeDistanceInput.getChangeMonitor().hasChanged()) {
			operators.add(new MinTreeDistanceOperator(minTreeDistanceInput.getValue()));
		}
		if (getMarginInput().getLeft().getChangeMonitor().hasChanged()) {
			operators.add(new LegendMarginLeftOperator(getMarginInput().getLeft().getValue()));
		}
		if (getMarginInput().getTop().getChangeMonitor().hasChanged()) {
			operators.add(new LegendMarginTopOperator(getMarginInput().getTop().getValue()));
		}
		if (getMarginInput().getRight().getChangeMonitor().hasChanged()) {
			operators.add(new LegendMarginRightOperator(getMarginInput().getRight().getValue()));
		}
		if (getMarginInput().getBottom().getChangeMonitor().hasChanged()) {
			operators.add(new LegendMarginBottomOperator(getMarginInput().getBottom().getValue()));
		}
	}


	public void addError(List<String> list) {}


	public void resetChangeMonitors() {
		styleMonitor.reset();
		orientationMonitor.reset();
		legendPositionMonitor.reset();
		minTreeDistanceInput.getChangeMonitor().reset();
		getMarginInput().resetChangeMonitors();
	}


	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.add(getLegendStylePanel(), null);
		this.add(getLegendOrientationPanel(), null);
		this.add(getLegendNumbersPanel(), null);
		this.add(getMarginPanel(), null);
	}

	
	/**
	 * This method initializes legendStylePanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getLegendStylePanel() {
		if (legendStylePanel == null) {
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 1;
			gridBagConstraints1.anchor = GridBagConstraints.WEST;
			gridBagConstraints1.weightx = 1.0;
			gridBagConstraints1.fill = GridBagConstraints.NONE;
			gridBagConstraints1.gridy = 0;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.anchor = GridBagConstraints.WEST;
			gridBagConstraints.weightx = 1.0;
			gridBagConstraints.fill = GridBagConstraints.NONE;
			gridBagConstraints.gridy = 0;
			legendStylePanel = new JPanel();
			legendStylePanel.setLayout(new GridBagLayout());
			legendStylePanel.setBorder(BorderFactory.createTitledBorder(null, "Style:", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			legendStylePanel.add(getBracketRadioButton(), gridBagConstraints);
			legendStylePanel.add(getBraceRadioButton(), gridBagConstraints1);
			getStyleGroup();  // Elemente gruppieren
		}
		return legendStylePanel;
	}


	/**
	 * This method initializes bracketRadioButton	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getBracketRadioButton() {
		if (bracketRadioButton == null) {
			bracketRadioButton = new JRadioButton();
			bracketRadioButton.setText("Edged bracket");
			bracketRadioButton.getModel().addChangeListener(styleMonitor);
		}
		return bracketRadioButton;
	}


	/**
	 * This method initializes braceRadioButton	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getBraceRadioButton() {
		if (braceRadioButton == null) {
			braceRadioButton = new JRadioButton();
			braceRadioButton.setText("Brace");
			braceRadioButton.getModel().addChangeListener(styleMonitor);
		}
		return braceRadioButton;
	}

	
	public ButtonGroup getStyleGroup() {
		if (styleGroup == null) {
			styleGroup = new ButtonGroup();
			styleGroup.add(getBraceRadioButton());
			styleGroup.add(getBracketRadioButton());
		}
		return styleGroup;
	}


	/**
	 * This method initializes legendOrientationPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getLegendOrientationPanel() {
		if (legendOrientationPanel == null) {
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.gridx = 2;
			gridBagConstraints4.anchor = GridBagConstraints.WEST;
			gridBagConstraints4.weightx = 1.0;
			gridBagConstraints4.fill = GridBagConstraints.NONE;
			gridBagConstraints4.gridy = 0;
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 1;
			gridBagConstraints3.anchor = GridBagConstraints.WEST;
			gridBagConstraints3.weightx = 1.0;
			gridBagConstraints3.fill = GridBagConstraints.NONE;
			gridBagConstraints3.gridy = 0;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.anchor = GridBagConstraints.WEST;
			gridBagConstraints2.weightx = 1.0;
			gridBagConstraints2.fill = GridBagConstraints.NONE;
			gridBagConstraints2.gridy = 0;
			legendOrientationPanel = new JPanel();
			legendOrientationPanel.setLayout(new GridBagLayout());
			legendOrientationPanel.setBorder(BorderFactory.createTitledBorder(null, "Text orientation:", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			legendOrientationPanel.add(getHorizontalRadioButton(), gridBagConstraints2);
			legendOrientationPanel.add(getUpRadioButton(), gridBagConstraints3);
			legendOrientationPanel.add(getDownRadioButton(), gridBagConstraints4);
			getOrientationGroup();  // Elemente gruppieren
		}
		return legendOrientationPanel;
	}
	
	
	/**
	 * This method initializes horizontalRadioButton	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getHorizontalRadioButton() {
		if (horizontalRadioButton == null) {
			horizontalRadioButton = new JRadioButton();
			horizontalRadioButton.setText("Horizontal");
			horizontalRadioButton.getModel().addChangeListener(orientationMonitor);
		}
		return horizontalRadioButton;
	}


	/**
	 * This method initializes upRadioButton	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getUpRadioButton() {
		if (upRadioButton == null) {
			upRadioButton = new JRadioButton();
			upRadioButton.setText("Upwards");
			upRadioButton.getModel().addChangeListener(orientationMonitor);
		}
		return upRadioButton;
	}


	/**
	 * This method initializes downRadioButton	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getDownRadioButton() {
		if (downRadioButton == null) {
			downRadioButton = new JRadioButton();
			downRadioButton.setText("Downwards");
			downRadioButton.getModel().addChangeListener(orientationMonitor);
		}
		return downRadioButton;
	}
	
	
	public ButtonGroup getOrientationGroup() {
		if (orientationGroup == null) {
			orientationGroup = new ButtonGroup();
			orientationGroup.add(getHorizontalRadioButton());
			orientationGroup.add(getUpRadioButton());
			orientationGroup.add(getDownRadioButton());
		}
		return orientationGroup;
	}
	
	
	/**
	 * This method initializes legendNumbersPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getLegendNumbersPanel() {
		if (legendNumbersPanel == null) {
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.gridx = 0;
			gridBagConstraints11.anchor = GridBagConstraints.WEST;
			gridBagConstraints11.gridy = 0;
			legendOrderLabel = new JLabel();
			legendOrderLabel.setText("Position index (order): ");
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints.gridwidth = 2;
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 0;
			gridBagConstraints.weightx = 1.0;
			legendNumbersPanel = new JPanel();
			legendNumbersPanel.setLayout(new GridBagLayout());
			legendNumbersPanel.setBorder(BorderFactory.createTitledBorder(null, "Position:", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			legendNumbersPanel.add(getLegendPositionTextField(), gridBagConstraints);
			legendNumbersPanel.add(legendOrderLabel, gridBagConstraints11);
      minTreeDistanceInput = new DistanceValueInput("Minimal distance to the tree: ", legendNumbersPanel, 1);
		}
		return legendNumbersPanel;
	}


	/**
	 * This method initializes positionTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JFormattedTextField getLegendPositionTextField() {
		if (legendPositionTextField == null) {
			legendPositionTextField = new JFormattedTextField(decimalFormat);
			legendPositionTextField.getDocument().addDocumentListener(
					legendPositionMonitor);
		}
		return legendPositionTextField;
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
			marginPanel.setBorder(BorderFactory.createTitledBorder(null, "Margin:", 
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