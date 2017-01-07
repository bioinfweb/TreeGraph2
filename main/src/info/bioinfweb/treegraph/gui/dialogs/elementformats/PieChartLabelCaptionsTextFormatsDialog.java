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


import info.bioinfweb.treegraph.Main;
import info.bioinfweb.treegraph.document.format.PieChartLabelFormats;
import info.bioinfweb.treegraph.document.format.operate.AbstractTextOperator;
import info.bioinfweb.treegraph.document.format.operate.FormatOperator;
import info.bioinfweb.treegraph.document.format.operate.PieChartCaptionTextOperator;
import info.bioinfweb.wikihelp.client.OkCancelApplyWikiHelpDialog;

import java.awt.Component;
import java.awt.Dialog;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;



public class PieChartLabelCaptionsTextFormatsDialog extends OkCancelApplyWikiHelpDialog {
	private List<ElementFormatsTab> tabs;
	
	private JPanel jContentPane;
	private JTabbedPane tabbedPane;
	
	
	public PieChartLabelCaptionsTextFormatsDialog(Dialog owner) {
		super(owner, true, Main.getInstance().getWikiHelp());
		setHelpCode(92);
		initialize();
		setLocationRelativeTo(owner);
	}

	
	public void resetChangeMonitors() {
		for (ElementFormatsTab tab : getTabs()) {
			tab.resetChangeMonitors();
		}
	}
	
	
	@Override
	public boolean execute() {
		boolean result = super.execute();
		if (!result) {  // Make sure changes not applied, of cancel is selected:
			resetChangeMonitors();
		}
		return result;
	}


	@Override
	protected boolean apply() {
		return ElementFormatsDialog.checkInputs(this, getTabbedPane(), getTabs());
	}
	
	
	public void addOperators(List<FormatOperator> operators) {
		List<FormatOperator> directOperators = ElementFormatsDialog.getTabOperators(getTabs());
		for (FormatOperator directOperator : directOperators) {
			operators.add(new PieChartCaptionTextOperator((AbstractTextOperator)directOperator));
		}
	}

	
	public void setValues(PieChartLabelFormats f) {
		for (ElementFormatsTab tab : getTabs()) {  // This implementation needs to be adjusted if additional tab types would be added to the list!
			if (tab instanceof FontFormatsPanel) {
				((FontFormatsPanel)tab).setValue(f.getCaptionsTextFormats());
			}
			else if (tab instanceof FontColorPanel) {
				((FontColorPanel)tab).setColor(f.getCaptionsTextFormats().getTextColor());
			}
			else if (tab instanceof DecimalFormatPanel) {
				((DecimalFormatPanel)tab).setValues(f.getCaptionsTextFormats());
			}
		}
	}
	
	
	public List<ElementFormatsTab> getTabs() {
		if (tabs == null) {
			tabs = new ArrayList<ElementFormatsTab>();
			tabs.add(new FontFormatsPanel(false));
			tabs.add(new FontColorPanel());
			tabs.add(new DecimalFormatPanel());
		}
		return tabs;
	}


	private void initialize() {
		setContentPane(getJContentPane());
		setTitle("Pie chart label captions text formats");
		pack();
	}
	
	
	public JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BoxLayout(jContentPane, BoxLayout.Y_AXIS));
			
			jContentPane.add(getTabbedPane());
			
			getApplyButton().setVisible(false);
			jContentPane.add(getButtonsPanel());
		}
		return jContentPane;
	}


	private JTabbedPane getTabbedPane() {
		if (tabbedPane == null) {
			tabbedPane = new JTabbedPane(JTabbedPane.TOP);
			for (ElementFormatsTab tab : getTabs()) {
				tabbedPane.addTab(tab.title(),	(Component)tab);
			}
		}
		return tabbedPane;
	}
}
