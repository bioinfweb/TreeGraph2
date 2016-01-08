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


import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.format.operate.FormatOperator;
import info.bioinfweb.treegraph.document.format.operate.LeafMarginBottomOperator;
import info.bioinfweb.treegraph.document.format.operate.LeafMarginLeftOperator;
import info.bioinfweb.treegraph.document.format.operate.LeafMarginRightOperator;
import info.bioinfweb.treegraph.document.format.operate.LeafMarginTopOperator;
import info.bioinfweb.treegraph.gui.dialogs.MarginInput;
import info.bioinfweb.treegraph.gui.treeframe.TreeSelection;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;



/**
 * Panel of the {@link ElementFormatsDialog} used to edit the margin around a terminal 
 * node.
 * 
 * @author Ben St&ouml;ver
 * @since 2.0.25
 */
public class NodeMarginPanel extends JPanel implements ElementFormatTab {
	private static final long serialVersionUID = 1L;
	

	private JPanel marginPanel = null;
	private MarginInput marginInput = null;


	/**
	 * This is the default constructor
	 */
	public NodeMarginPanel() {
		super();
		initialize();
	}

	
	public String title() {
		return "Leaf margin";
	}


	public boolean setValues(TreeSelection selection) {
		Node first = selection.getFirstElementOfType(Node.class);  // Soll explizit auch bei internen Knoten m�glich sein.
		boolean result = (first != null);
		if (result) {
			getMarginInput().setValue(first.getFormats().getLeafMargin());
		}
		return result;
	}


	public void addError(List<String> list) {}


	public void addOperators(List<FormatOperator> operators) {
		if (getMarginInput().getLeft().getChangeMonitor().hasChanged()) {
			operators.add(new LeafMarginLeftOperator(getMarginInput().getLeft().getValue()));
		}
		if (getMarginInput().getTop().getChangeMonitor().hasChanged()) {
			operators.add(new LeafMarginTopOperator(getMarginInput().getTop().getValue()));
		}
		if (getMarginInput().getRight().getChangeMonitor().hasChanged()) {
			operators.add(new LeafMarginRightOperator(getMarginInput().getRight().getValue()));
		}
		if (getMarginInput().getBottom().getChangeMonitor().hasChanged()) {
			operators.add(new LeafMarginBottomOperator(getMarginInput().getBottom().getValue()));
		}
	}


	public void resetChangeMonitors() {
		getMarginInput().resetChangeMonitors();
	}


	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(getMarginPanel(), null);
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