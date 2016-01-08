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


import info.bioinfweb.commons.changemonitor.ChangeMonitor;
import info.bioinfweb.treegraph.document.Branch;
import info.bioinfweb.treegraph.document.format.BranchFormats;
import info.bioinfweb.treegraph.document.format.operate.ConstantWidthOperator;
import info.bioinfweb.treegraph.document.format.operate.FormatOperator;
import info.bioinfweb.treegraph.document.format.operate.MinLengthOperator;
import info.bioinfweb.treegraph.document.format.operate.MinSpaceAboveOperator;
import info.bioinfweb.treegraph.document.format.operate.MinSpaceBelowOperator;
import info.bioinfweb.treegraph.gui.dialogs.DistanceValueInput;
import info.bioinfweb.treegraph.gui.treeframe.TreeSelection;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;



/**
 * Panel of the {@link ElementFormatsDialog} used to edit the branch formats.
 * 
 * @author Ben St&ouml;ver
 * @see ElementFormatsDialog
 * @see BranchFormats
 */
public class BranchPanel extends JPanel implements ElementFormatTab {
	private static final long serialVersionUID = 1L;

	
	private DistanceValueInput minLengthInput = null;  //  @jve:decl-index=0:
	private DistanceValueInput spaceAboveInput = null;
	private DistanceValueInput spaceBelowInput = null;
	private JLabel spacer = null;
	private JCheckBox constantWidthCheckBox = null;
	private ChangeMonitor constantWidthMonitor = new ChangeMonitor();  //  @jve:decl-index=0:

	
	/**
	 * This is the default constructor
	 */
	public BranchPanel() {
		super();
		initialize();
	}


	public boolean setValues(TreeSelection selection) {
		Branch first = selection.getFirstElementOfType(Branch.class);
		boolean result = (first != null);
		if (result) {
			BranchFormats f = first.getFormats();
			minLengthInput.setValue(f.getMinLength());
			spaceAboveInput.setValue(f.getMinSpaceAbove());
			spaceBelowInput.setValue(f.getMinSpaceBelow());
			getConstantWidthCheckBox().setSelected(f.isConstantWidth());
		}
		return result;
	}


	public String title() {
		return "Branch formats";
	}


	public void addOperators(List<FormatOperator> operators) {
		if (minLengthInput.getChangeMonitor().hasChanged()) {
			operators.add(new MinLengthOperator(minLengthInput.getValue()));
		}
		if (spaceAboveInput.getChangeMonitor().hasChanged()) {
			operators.add(new MinSpaceAboveOperator(spaceAboveInput.getValue()));
		}
		if (spaceBelowInput.getChangeMonitor().hasChanged()) {
			operators.add(new MinSpaceBelowOperator(spaceBelowInput.getValue()));
		}
		if (constantWidthMonitor.hasChanged()) {
			operators.add(new ConstantWidthOperator(
					getConstantWidthCheckBox().isSelected()));
		}
	}


	public void addError(List<String> list) {
		if (minLengthInput.getValue().getInMillimeters() < 0) {
			list.add("The minimal length of a branch cannot be less than 0.");
		}
		if (spaceAboveInput.getValue().getInMillimeters() < 0) {
			list.add("The minimal Minimal space above a branch cannot be less than 0.");
		}
		if (spaceBelowInput.getValue().getInMillimeters() < 0) {
			list.add("The minimal Minimal space below a branch cannot be less than 0.");
		}
	}


	public void resetChangeMonitors() {
		minLengthInput.getChangeMonitor().reset();
		spaceAboveInput.getChangeMonitor().reset();
		spaceBelowInput.getChangeMonitor().reset();
		constantWidthMonitor.reset();
	}


	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.gridx = 0;
		gridBagConstraints2.gridwidth = 3;
		gridBagConstraints2.anchor = GridBagConstraints.WEST;
		gridBagConstraints2.gridy = 7;
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 6;
		spacer = new JLabel();
		spacer.setText(" ");
		this.setLayout(new GridBagLayout());
		this.setBorder(BorderFactory.createTitledBorder(null, "Distances", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
		this.add(spacer, gridBagConstraints);
		this.add(getConstantWidthCheckBox(), gridBagConstraints2);
    minLengthInput = new DistanceValueInput("Minimal length: ", this, 0);
    spaceAboveInput = new DistanceValueInput("Minimal space above: ", this, 2);
    spaceBelowInput = new DistanceValueInput("Minimal space below: ", this, 4);
	}

	
	/**
	 * This method initializes constantWidthCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getConstantWidthCheckBox() {
		if (constantWidthCheckBox == null) {
			constantWidthCheckBox = new JCheckBox();
			constantWidthCheckBox.setText("Use constant line width (don't taper to parent nodes line width)");
			constantWidthCheckBox.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(java.awt.event.ItemEvent e) {
					constantWidthMonitor.registerChange();
				}
			});
		}
		return constantWidthCheckBox;
	}
}