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
package info.bioinfweb.treegraph.gui.dialogs.specialformats;


import info.bioinfweb.treegraph.gui.dialogs.EditDialog;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.border.TitledBorder;



/**
 * Superclass of all dialogs which set special formats (e.g. formats by node/branch data).
 * @author Ben St&ouml;ver
 * @since 2.0.24
 */
public abstract class SpecialFormatsDialog extends EditDialog {
	private JPanel targetPanel = null;
	private JScrollPane targetScrollPane = null;
	private JList targetList = null;
	private ListModel targetListModel = null;
	
	
	public SpecialFormatsDialog(Dialog owner) {
		super(owner);
	}

	
	public SpecialFormatsDialog(Frame owner) {
		super(owner);
	}


	/**
	 * This method is called from {@link SpecialFormatsDialog#onExecute()}.
	 * {@link FormatsByNodeBranchDataDialog#pack()} will be called after this method.
	 * @return whether the dialog can be displayed
	 */
	protected abstract boolean customizeTarget();
	
	
	/**
	 * Subclasses should implement {@link SpecialFormatsDialog#customizeTarget()} instead of
	 * overriding this method. ({@link SpecialFormatsDialog#customizeTarget()} is called from
	 * here if {@link SpecialFormatsDialog#customizeTarget()} returned <code>true</code>.)
	 * @see info.bioinfweb.treegraph.gui.dialogs.EditDialog#onExecute()
	 */
	@Override
	protected boolean onExecute() {
  	boolean result = customizeTarget();
	  if (result) {
		  pack();
		}
		return result;
	}
	
	
	/**
	 * This method initializes targetPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	protected JPanel getTargetPanel() {
		if (targetPanel == null) {
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.fill = GridBagConstraints.BOTH;
			gridBagConstraints.weighty = 1.0;
			gridBagConstraints.weightx = 1.0;
			targetPanel = new JPanel();
			targetPanel.setLayout(new GridBagLayout());
			targetPanel.setBorder(BorderFactory.createTitledBorder(null, "Target", 
					TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, 
					new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			targetPanel.add(getTargetScrollPane(), gridBagConstraints);
			targetPanel.setAlignmentY(Component.CENTER_ALIGNMENT);
		}
		return targetPanel;
	}


	/**
	 * This method initializes targetScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	protected JScrollPane getTargetScrollPane() {
		if (targetScrollPane == null) {
			targetScrollPane = new JScrollPane();
			targetScrollPane.setViewportView(getTargetList());
		}
		return targetScrollPane;
	}


	/**
	 * This method initializes targetList	
	 * 	
	 * @return javax.swing.JList	
	 */
	protected JList getTargetList() {
		if (targetList == null) {
			targetList = new JList(getTargetListModel());
			targetList.setVisibleRowCount(8);
		}
		return targetList;
	}


	/**
	 * Implementing classes can specify their target list model here. 
	 * @return
	 */
	protected abstract ListModel createTargetListModel();


	protected ListModel getTargetListModel() {
		if (targetListModel == null) {
			targetListModel = createTargetListModel();
		}
		return targetListModel;
	}
}