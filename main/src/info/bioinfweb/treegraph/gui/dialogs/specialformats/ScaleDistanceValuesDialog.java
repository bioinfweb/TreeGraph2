/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2015  Ben Stöver, Kai Müller
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
package info.bioinfweb.treegraph.gui.dialogs.specialformats;


import info.bioinfweb.treegraph.document.Branch;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.format.adapters.distance.DistanceAdapter;
import info.bioinfweb.treegraph.document.undo.format.ScaleDistanceValuesEdit;
import info.bioinfweb.commons.swing.DecimalInput;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import java.awt.Frame;
import javax.swing.BoxLayout;
import java.awt.GridBagLayout;
import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Color;
import java.util.Vector;



/**
 * @author Ben St&ouml;ver
 * @since 2.0.24
 */
public class ScaleDistanceValuesDialog extends SpecialFormatsDialog {
	public static final float DEFAULT_SCALE = 1f;
	
	private static final long serialVersionUID = 1L;

	
	private JPanel jContentPane = null;
	private JPanel scalePanel = null;
	private DecimalInput scaleInput = null;

	
	/**
	 * @param owner
	 */
	public ScaleDistanceValuesDialog(Frame owner) {
		super(owner);
		initialize();
	}

	
	@Override
	protected DistanceAdapterListModel createTargetListModel() {
		return new DistanceAdapterListModel();
	}


	@Override
	protected DistanceAdapterListModel getTargetListModel() {
		return (DistanceAdapterListModel)super.getTargetListModel();
	}


	@Override
	protected boolean customizeTarget() {
		Node root = getSelection().getFirstElementOfType(Node.class);
		if (root == null) {
			if (getSelection().containsType(Branch.class)) {
				root = getSelection().getFirstElementOfType(Branch.class).getTargetNode();
			}
		}
		
		if (root != null) {
			getTargetListModel().setAdapters(getDocument().getTree(), root, true);
			getTargetList().setSelectionInterval(0, getTargetListModel().getSize() - 1);
		}
		return root != null;
	}


	@Override
	protected boolean apply() {
		float factor = scaleInput.parseFloat();
		boolean result = factor > 0; 
		if (result) {
			Vector<DistanceAdapter> adapters = new Vector<DistanceAdapter>();
			int[] selection = getTargetList().getSelectedIndices();
			for (int i = 0; i < selection.length; i++) {
				adapters.add(getTargetListModel().getElementAt(selection[i]));
			}
			
			Node root = getSelection().getFirstElementOfType(Node.class);
			if (root == null) {
				root = getSelection().getFirstElementOfType(Branch.class).getTargetNode();
			}
			
			getDocument().executeEdit(new ScaleDistanceValuesEdit(getDocument(),
					root, factor, 
					adapters.toArray(new DistanceAdapter[adapters.size()])));
		}
		else {
			JOptionPane.showMessageDialog(this, "The scale factor must be greater than " +
					"zero.", "Invalid scale factor", JOptionPane.ERROR_MESSAGE);
		}
		return result;
	}


	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		setHelpCode(42);
		setMinimumSize(new Dimension(300, 100));
		setContentPane(getJContentPane());
		setTitle("Scale distance values");
		pack();
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
			jContentPane.add(getScalePanel(), null);
			jContentPane.add(getTargetPanel(), null);
			jContentPane.add(getButtonsPanel(), null);
		}
		return jContentPane;
	}


	/**
	 * This method initializes rescalePanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getScalePanel() {
		if (scalePanel == null) {
			scalePanel = new JPanel();
			scalePanel.setLayout(new GridBagLayout());
			scalePanel.setBorder(BorderFactory.createTitledBorder(null, "Scale factor", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			scaleInput = new DecimalInput("", scalePanel, 0, DecimalInput.FLOAT_FORMAT);
			scaleInput.setValue(DEFAULT_SCALE);
		}
		return scalePanel;
	}


	public DecimalInput getScaleInput() {
		getScalePanel();
		return scaleInput;
	}
}