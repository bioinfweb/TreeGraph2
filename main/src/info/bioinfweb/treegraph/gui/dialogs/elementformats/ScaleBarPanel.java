/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2011, 2013-2017  Ben Stöver, Sarah Wiechers, Kai Müller
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


import info.bioinfweb.treegraph.document.ScaleBar;
import info.bioinfweb.treegraph.document.format.ScaleAlignment;
import info.bioinfweb.treegraph.document.format.ScaleBarFormats;
import info.bioinfweb.treegraph.document.format.ScaleValue;
import info.bioinfweb.treegraph.document.format.operate.FormatOperator;
import info.bioinfweb.treegraph.document.format.operate.ScaleBarFormatsOperator;
import info.bioinfweb.treegraph.gui.dialogs.DistanceValueInput;
import info.bioinfweb.treegraph.gui.treeframe.TreeSelection;
import info.bioinfweb.commons.swing.DecimalInput;
import info.bioinfweb.commons.swing.SwingChangeMonitor;
import java.awt.GridBagLayout;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;
import java.awt.Font;
import java.awt.Color;
import javax.swing.JRadioButton;
import java.awt.GridBagConstraints;
import java.awt.event.ItemEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import javax.swing.JLabel;
import java.awt.Insets;



/**
 * Panel of the {@link ElementFormatsDialog} used to edit the scale bar formats.
 * 
 * @author Ben St&ouml;ver
 * @see ElementFormatsDialog
 */
public class ScaleBarPanel extends JPanel implements ElementFormatsTab {
	private static final long serialVersionUID = 1L;
	
	
	private ElementFormatsDialog owner = null;
	
	private JPanel alignmentPanel = null;
	private JRadioButton leftRadioButton = null;
	private JRadioButton rightRadioButton = null;
	private JRadioButton treeWidthRadioButton = null;
	private ButtonGroup alignGroup = null;  //  @jve:decl-index=0:
	private SwingChangeMonitor alignMonitor = new SwingChangeMonitor();  //  @jve:decl-index=0:
	private SwingChangeMonitor increaseMonitor = new SwingChangeMonitor();  //  @jve:decl-index=0:
	private SwingChangeMonitor startMonitor = new SwingChangeMonitor();  //  @jve:decl-index=0:
	private JPanel intervalPanel = null;
	private DistanceValueInput treeDistanceInput = null;
	private ScaleValueInput widthInput = null;
	private DistanceValueInput heightInput = null;
	private DecimalInput smallIntervalInput = null;
	private DecimalInput longIntervalInput = null;
	private JPanel dimensionsPanel = null;
	private JPanel startPanel = null;
	private JRadioButton zeroLeftRadioButton = null;
	private JRadioButton zeroRightRadioButton = null;
	private ButtonGroup zeroPosGroup = null;  //  @jve:decl-index=0:
	private JPanel labelingPanel = null;
	private JPanel directionPanel = null;
	private JRadioButton incRadioButton = null;
	private JRadioButton decRadioButton = null;
	private ButtonGroup increaseGroup = null;  //  @jve:decl-index=0:
	private JLabel smallIntervalLengthLabel = null;
	private JLabel longIntervalLengthLabel = null;
	private ScaleBarIntervalKeyListener smallIntervalKeyListener = null;
	private ScaleBarIntervalKeyListener longIntervalKeyListener = null;  //  @jve:decl-index=0:

	
	/**
	 * This is the default constructor
	 */
	public ScaleBarPanel(ElementFormatsDialog owner) {
		super();
		this.owner = owner;
		initialize();
	}

	
	public void addOperators(List<FormatOperator> operators) {
		if (alignMonitor.hasChanged() || startMonitor.hasChanged() || 
				increaseMonitor.hasChanged() || widthInput.getChangeMonitor().hasChanged() || 
				heightInput.getChangeMonitor().hasChanged() ||
				treeDistanceInput.getChangeMonitor().hasChanged() ||
				smallIntervalInput.getChangeMonitor().hasChanged() || 
				longIntervalInput.getChangeMonitor().hasChanged()) {
			
			ScaleBarFormats formats = new ScaleBarFormats();
			
			if (getLeftRadioButton().isSelected()) {
				formats.setAlignment(ScaleAlignment.LEFT);
			}
			else if (getRightRadioButton().isSelected()) {
				formats.setAlignment(ScaleAlignment.RIGHT);
			}
			else {
				formats.setAlignment(ScaleAlignment.TREE_WIDTH);
			}
			
			treeDistanceInput.assignValueTo(formats.getTreeDistance());
			widthInput.assignValueTo(formats.getWidth());
			heightInput.assignValueTo(formats.getHeight());
			formats.setStartLeft(getZeroLeftRadioButton().isSelected());
			formats.setIncreasing(getIncRadioButton().isSelected());
			formats.setSmallInterval(smallIntervalInput.parseFloat());
			formats.setLongInterval(longIntervalInput.parseInt());
			
			operators.add(new ScaleBarFormatsOperator(formats));
		}
	}


	public void addError(List<String> list) {
		if (widthInput.getValue().getStoredValue() <= 0) {
			list.add("The width of the scale bar must be greater than 0.");
		}
		if (heightInput.getValue().getInMillimeters() <= 0) {
			list.add("The height of the scale bar must be greater than 0.");
		}
		if (smallIntervalInput.parseFloat() <= 0) {
			list.add("The length of a scale bar small interval must be greater than 0.");
		}
		if (longIntervalInput.parseFloat() <= 0) {
			list.add("The length of a scale bar long interval must be greater than 0.");
		}
	}


	public void resetChangeMonitors() {
		alignMonitor.reset();
		treeDistanceInput.getChangeMonitor().reset();
		widthInput.getChangeMonitor().reset();
		heightInput.getChangeMonitor().reset();
		smallIntervalInput.getChangeMonitor().reset();
		longIntervalInput.getChangeMonitor().reset();
		startMonitor.reset();
		increaseMonitor.reset();
	}


	public boolean setValues(TreeSelection selection) {
		boolean result = selection.containsType(ScaleBar.class);
		if (result) {
			ScaleBarFormats formats = selection.getFirstElementOfType(ScaleBar.class).getFormats();
			switch (formats.getAlignment()) {
				case LEFT:
					getLeftRadioButton().setSelected(true);
					break;
				case RIGHT:
					getRightRadioButton().setSelected(true);
					break;
				default:  // TREE_WIDTH
					getTreeWidthRadioButton().setSelected(true);
				  break;
			}
			treeDistanceInput.setValue(formats.getTreeDistance());
			widthInput.setBranchLengthScale(owner.getDocument().getTree().getFormats().getBranchLengthScale().getInMillimeters());
			widthInput.setValue(formats.getWidth());
			heightInput.setValue(formats.getHeight());
			smallIntervalInput.setValue(formats.getSmallInterval());
			longIntervalInput.setValue(formats.getLongInterval());
			getSmallIntervalKeyListener().refreshText();
			getLongIntervalKeyListener().refreshText();
			if (formats.isStartLeft()) {
				getZeroLeftRadioButton().setSelected(true);
			}
			else {
				getZeroRightRadioButton().setSelected(true);
			}
			if (formats.isIncreasing()) {
				getIncRadioButton().setSelected(true);
			}
			else {
				getDecRadioButton().setSelected(true);
			}
		}
		return result;
	}


	public String title() {
		return "Scale bar formats";
	}
	
	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(320, 400);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.add(getAlignmentPanel(), null);
		this.add(getDimensionsPanel(), null);
		this.add(getLabelingPanel(), null);
		this.add(getIntervalPanel(), null);
	}


	/**
	 * This method initializes anchorPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getAlignmentPanel() {
		if (alignmentPanel == null) {
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.gridx = 0;
			gridBagConstraints11.gridy = 0;
			gridBagConstraints11.anchor = GridBagConstraints.WEST;
			gridBagConstraints11.weightx = 1.0;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 2;
			gridBagConstraints2.anchor = GridBagConstraints.WEST;
			gridBagConstraints2.weightx = 1.0;
			gridBagConstraints2.gridy = 0;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 1;
			gridBagConstraints1.anchor = GridBagConstraints.WEST;
			gridBagConstraints1.weightx = 1.0;
			gridBagConstraints1.gridy = 0;
			alignmentPanel = new JPanel();
			alignmentPanel.setLayout(new GridBagLayout());
			alignmentPanel.setBorder(BorderFactory.createTitledBorder(null, "Alignment", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			alignmentPanel.add(getRightRadioButton(), gridBagConstraints1);
			alignmentPanel.add(getTreeWidthRadioButton(), gridBagConstraints2);
			alignmentPanel.add(getLeftRadioButton(), gridBagConstraints11);
			getAlignGroup();
		}
		return alignmentPanel;
	}


	/**
	 * This method initializes leftRadioButton	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getLeftRadioButton() {
		if (leftRadioButton == null) {
			leftRadioButton = new JRadioButton();
			leftRadioButton.setText("Left");
			leftRadioButton.setSelected(true);
			leftRadioButton.getModel().addChangeListener(alignMonitor);
		}
		return leftRadioButton;
	}


	/**
	 * This method initializes rightRadioButton	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getRightRadioButton() {
		if (rightRadioButton == null) {
			rightRadioButton = new JRadioButton();
			rightRadioButton.setText("Right");
			rightRadioButton.getModel().addChangeListener(alignMonitor);
		}
		return rightRadioButton;
	}


	/**
	 * This method initializes treeWidthRadioButton	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getTreeWidthRadioButton() {
		if (treeWidthRadioButton == null) {
			treeWidthRadioButton = new JRadioButton();
			treeWidthRadioButton.setText("Whole tree width (left and right)");
			treeWidthRadioButton.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(java.awt.event.ItemEvent e) {
					widthInput.setEnabled(e.getStateChange() != ItemEvent.SELECTED);
				}
			});
			treeWidthRadioButton.getModel().addChangeListener(alignMonitor);
		}
		return treeWidthRadioButton;
	}


	private ButtonGroup getAlignGroup() {
		if (alignGroup == null) {
			alignGroup = new ButtonGroup();
			alignGroup.add(getLeftRadioButton());
			alignGroup.add(getRightRadioButton());
			alignGroup.add(getTreeWidthRadioButton());
		}
		return alignGroup;
	}
	
	
	private ScaleBarIntervalKeyListener getSmallIntervalKeyListener() {
		getIntervalPanel();
		return smallIntervalKeyListener;
	}


	private ScaleBarIntervalKeyListener getLongIntervalKeyListener() {
		getIntervalPanel();
		return longIntervalKeyListener;
	}


	/**
	 * This method initializes decimalPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getIntervalPanel() {
		if (intervalPanel == null) {
			GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
			gridBagConstraints21.gridx = 0;
			gridBagConstraints21.anchor = GridBagConstraints.EAST;
			gridBagConstraints21.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints21.gridy = 3;
			longIntervalLengthLabel = new JLabel();
			GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
			gridBagConstraints12.gridx = 0;
			gridBagConstraints12.anchor = GridBagConstraints.EAST;
			gridBagConstraints12.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints12.gridy = 1;
			smallIntervalLengthLabel = new JLabel();
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.anchor = GridBagConstraints.WEST;
			gridBagConstraints.gridy = -1;
			gridBagConstraints.gridx = -1;
			intervalPanel = new JPanel();
			intervalPanel.setLayout(new GridBagLayout());
			intervalPanel.setBorder(BorderFactory.createTitledBorder(null, "Intervals", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			intervalPanel.add(smallIntervalLengthLabel, gridBagConstraints12);
			intervalPanel.add(longIntervalLengthLabel, gridBagConstraints21);
			
			smallIntervalInput = new DecimalInput("Small interval [branch length units]: ", intervalPanel, 0, 
					DecimalInput.FLOAT_FORMAT);
			longIntervalInput = new DecimalInput("Long interval [small intervals]: ", intervalPanel, 2, 
					DecimalInput.INTEGER_FORMAT);
			
			smallIntervalKeyListener = new ScaleBarIntervalKeyListener(
					owner, smallIntervalLengthLabel, smallIntervalInput, null); 
			longIntervalKeyListener = new ScaleBarIntervalKeyListener(owner, longIntervalLengthLabel, 
					smallIntervalInput,	longIntervalInput); 
			smallIntervalInput.getTextField().addKeyListener(smallIntervalKeyListener);
			smallIntervalInput.getTextField().addKeyListener(longIntervalKeyListener);
			longIntervalInput.getTextField().addKeyListener(longIntervalKeyListener);
		}
		return intervalPanel;
	}


	/**
	 * This method initializes widthPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getDimensionsPanel() {
		if (dimensionsPanel == null) {
			dimensionsPanel = new JPanel();
			dimensionsPanel.setLayout(new GridBagLayout());
			dimensionsPanel.setBorder(BorderFactory.createTitledBorder(null, "Dimensions", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			treeDistanceInput = new DistanceValueInput("Distance to tree: ", dimensionsPanel, 0);
			widthInput = new ScaleValueInput("Width: ", dimensionsPanel, 2);
			heightInput = new DistanceValueInput("Height (without label text): ", dimensionsPanel, 4);
		}
		return dimensionsPanel;
	}


	/**
	 * This method initializes startPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getStartPanel() {
		if (startPanel == null) {
			GridBagConstraints rightGBC = new GridBagConstraints();
			rightGBC.anchor = GridBagConstraints.WEST;
			rightGBC.gridx = 1;
			rightGBC.gridy = 0;
			rightGBC.weightx = 1.0;
			rightGBC.fill = GridBagConstraints.NONE;
			GridBagConstraints leftGBC = new GridBagConstraints();
			leftGBC.anchor = GridBagConstraints.WEST;
			leftGBC.weightx = 1.0;
			leftGBC.fill = GridBagConstraints.NONE;
			leftGBC.gridx = 0;
			leftGBC.gridy = 0;
			startPanel = new JPanel();
			startPanel.setLayout(new GridBagLayout());
			startPanel.setBorder(BorderFactory.createTitledBorder(null, "Start labeling", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			startPanel.add(getZeroLeftRadioButton(), leftGBC);
			startPanel.add(getZeroRightRadioButton(), rightGBC);
			getZeroPosGroup();
		}
		return startPanel;
	}


	/**
	 * This method initializes zeroLeftRadioButton	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getZeroLeftRadioButton() {
		if (zeroLeftRadioButton == null) {
			zeroLeftRadioButton = new JRadioButton();
			zeroLeftRadioButton.setText("Zero on the left");
			zeroLeftRadioButton.setSelected(true);
			zeroLeftRadioButton.addChangeListener(startMonitor);
		}
		return zeroLeftRadioButton;
	}


	/**
	 * This method initializes ueroRightRadioButton	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getZeroRightRadioButton() {
		if (zeroRightRadioButton == null) {
			zeroRightRadioButton = new JRadioButton();
			zeroRightRadioButton.setText("Zero on the right");
		}
		return zeroRightRadioButton;
	}


	private ButtonGroup getZeroPosGroup() {
		if (zeroPosGroup == null) {
			zeroPosGroup = new ButtonGroup();
			zeroPosGroup.add(getZeroLeftRadioButton());
			zeroPosGroup.add(getZeroRightRadioButton());
		}
		return zeroPosGroup;
	}


	/**
	 * This method initializes labelingPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getLabelingPanel() {
		if (labelingPanel == null) {
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.fill = GridBagConstraints.BOTH;
			gridBagConstraints5.gridx = 2;
			gridBagConstraints5.weightx = 1.0;
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.fill = GridBagConstraints.BOTH;
			gridBagConstraints4.gridx = 1;
			gridBagConstraints4.gridy = 0;
			gridBagConstraints4.weightx = 1.0;
			labelingPanel = new JPanel();
			labelingPanel.setLayout(new GridBagLayout());
			labelingPanel.add(getStartPanel(), gridBagConstraints4);
			labelingPanel.add(getDirectionPanel(), gridBagConstraints5);
		}
		return labelingPanel;
	}


	/**
	 * This method initializes directionPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getDirectionPanel() {
		if (directionPanel == null) {
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.gridx = 1;
			gridBagConstraints7.anchor = GridBagConstraints.WEST;
			gridBagConstraints7.fill = GridBagConstraints.NONE;
			gridBagConstraints7.weightx = 1.0;
			gridBagConstraints7.gridy = 0;
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.gridx = 0;
			gridBagConstraints6.anchor = GridBagConstraints.WEST;
			gridBagConstraints6.fill = GridBagConstraints.NONE;
			gridBagConstraints6.weightx = 1.0;
			gridBagConstraints6.gridy = 0;
			directionPanel = new JPanel();
			directionPanel.setLayout(new GridBagLayout());
			directionPanel.setBorder(BorderFactory.createTitledBorder(null, "Labeling direction", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			directionPanel.add(getIncRadioButton(), gridBagConstraints6);
			directionPanel.add(getDecRadioButton(), gridBagConstraints7);
			getIncreaseGroup();
		}
		return directionPanel;
	}


	/**
	 * This method initializes incRadioButton	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getIncRadioButton() {
		if (incRadioButton == null) {
			incRadioButton = new JRadioButton();
			incRadioButton.setText("Increasing");
			incRadioButton.setSelected(true);
			incRadioButton.addChangeListener(increaseMonitor);
		}
		return incRadioButton;
	}


	/**
	 * This method initializes decRadioButton	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getDecRadioButton() {
		if (decRadioButton == null) {
			decRadioButton = new JRadioButton();
			decRadioButton.setText("Decreasing");
		}
		return decRadioButton;
	}


	private ButtonGroup getIncreaseGroup() {
		if (increaseGroup == null) {
			increaseGroup = new ButtonGroup();
			increaseGroup.add(getIncRadioButton());
			increaseGroup.add(getDecRadioButton());
		}
		return increaseGroup;
	}	
}