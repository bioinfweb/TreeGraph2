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
package info.bioinfweb.treegraph.gui.dialogs.elementformats;


import info.bioinfweb.treegraph.document.*;
import info.bioinfweb.treegraph.document.format.operate.FormatOperator;
import info.bioinfweb.treegraph.document.undo.format.OperatorsEdit;
import info.bioinfweb.treegraph.gui.dialogs.EditDialog;

import javax.swing.JPanel;
import java.awt.Component;
import java.awt.Frame;
import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;

import java.util.List;
import java.util.Vector;



/**
 * Dialog used to edit the element formats of a document element. The single tabs are implemented in 
 * single classes in this package.
 * 
 * @author Ben St&ouml;ver
 */
public class ElementFormatsDialog extends EditDialog {
	private static final long serialVersionUID = 1L;
	
	
	private JPanel jContentPane = null;
	private JTabbedPane formatsTabbedPane = null;
	private Vector<ElementFormatTab> tabs = null;  //  @jve:decl-index=0:


	/**
	 * @param owner
	 */
	public ElementFormatsDialog(Frame owner) {
		super(owner);
		setHelpCode(49);
		initialize();
		setLocationRelativeTo(owner);
	}


	@Override
	public Document getDocument() {
		return super.getDocument();
	}


	private void resetChangeMonitors() {
		for (int i = 0; i < getTabs().size(); i++) {
			getTabs().get(i).resetChangeMonitors();
		}
	}
	
	
	private boolean checkInputs() {
		List<String> errors = new Vector<String>();
		for (int i = 0; i < getTabs().size(); i++) {
			if (getFormatsTabbedPane().isEnabledAt(i)) {
				getTabs().get(i).addError(errors);
			}
		}
		
		boolean result = errors.size() == 0;
		if (!result) {
			String text = "One or more of your inputs are invalid:\n\n";
			for (int i = 0; i < errors.size(); i++) {
				text += "- " + errors.get(i) + "\n";
			}
			JOptionPane.showMessageDialog(this, text, "Invalid inputs",	JOptionPane.ERROR_MESSAGE);
		}
		return result;
	}
	
	
	@Override
	protected boolean onExecute() {
		boolean result = !getSelection().isEmpty(); 
		if (result) {
			for (int i = 0; i < getFormatsTabbedPane().getTabCount(); i++) {
				getFormatsTabbedPane().setEnabledAt(i, 
						getTabs().get(i).setValues(getSelection()));
			}
			
			// Make shure an enabled tab is selected:
			if (!getFormatsTabbedPane().isEnabledAt(getFormatsTabbedPane().getSelectedIndex())) {
				int index = 0;
				while (!getFormatsTabbedPane().isEnabledAt(index)) {
					index++;
				}
				getFormatsTabbedPane().setSelectedIndex(index);
			}
			
			resetChangeMonitors();			
		}
		return result;
	}


	@Override
	protected boolean apply() {
		boolean result = checkInputs();
		if (result) {
			Vector<FormatOperator> operators = new Vector<FormatOperator>();
			for (int i = 0; i < getFormatsTabbedPane().getTabCount(); i++) {
				getTabs().get(i).addOperators(operators);
			}
			
			if (operators.size() > 0) {
				getDocument().executeEdit(new OperatorsEdit(getDocument(), 
						getSelection().toArray(
								new ConcretePaintableElement[getSelection().size()]), 
						operators.toArray(new FormatOperator[operators.size()])));
				resetChangeMonitors();
			}
		}
		return result;
	}


	public Vector<ElementFormatTab> getTabs() {
		if (tabs == null) {
			tabs = new Vector<ElementFormatTab>();
			tabs.add(new LinePanel());
			tabs.add(new FontFormatsPanel());
			tabs.add(new FontColorPanel());
			tabs.add(new DecimalFormatPanel());
			tabs.add(new NodeMarginPanel());
			tabs.add(new BranchPanel());
			tabs.add(new LabelPanel());
			tabs.add(new IconPieChartLabelPanel());
			tabs.add(new LegendPanel());
			tabs.add(new ScaleBarPanel(this));
		}
		return tabs;
	}


	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setTitle("Element formats");
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
			jContentPane.add(getFormatsTabbedPane(), null);
			jContentPane.add(getButtonsPanel(), null);
		}
		return jContentPane;
	}


	/**
	 * This method initializes formatsTabbedPane	
	 * 	
	 * @return javax.swing.JTabbedPane	
	 */
	private JTabbedPane getFormatsTabbedPane() {
		if (formatsTabbedPane == null) {
			formatsTabbedPane = new JTabbedPane();
			for (int i = 0; i < getTabs().size(); i++) {
				formatsTabbedPane.addTab(getTabs().get(i).title(), 
						(Component)getTabs().get(i));
			}
		}
		return formatsTabbedPane;
	}
}