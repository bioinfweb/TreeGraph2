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


import info.bioinfweb.treegraph.document.CornerRadiusElement;
import info.bioinfweb.treegraph.document.LineElement;
import info.bioinfweb.treegraph.document.format.LineFormats;
import info.bioinfweb.treegraph.document.format.operate.CornerRadiusOperator;
import info.bioinfweb.treegraph.document.format.operate.FormatOperator;
import info.bioinfweb.treegraph.document.format.operate.LineColorOperator;
import info.bioinfweb.treegraph.document.format.operate.LineWidthOperator;
import info.bioinfweb.treegraph.gui.dialogs.DistanceValueInput;
import info.bioinfweb.treegraph.gui.treeframe.TreeSelection;
import info.bioinfweb.commons.swing.SwingChangeMonitor;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JColorChooser;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;



/**
 * Panel of the {@link ElementFormatsDialog} used to edit the line formats.
 * 
 * @author Ben St&ouml;ver
 * @see LineFormats
 */
public class LinePanel extends JPanel implements ElementFormatsTab {
	private static final long serialVersionUID = 1L;


	private JColorChooser colorChooser = null;
	private SwingChangeMonitor colorMonitor = new SwingChangeMonitor();  //  @jve:decl-index=0:
	private JPanel lineColorPanel = null;
	private JPanel otherLineFormatsPanel = null;
	private DistanceValueInput lineWidthInput = null; 
	private DistanceValueInput cornerRadiusInput = null;

	
	/**
	 * This is the default constructor
	 */
	public LinePanel() {
		super();
		initialize();
	}


	public boolean setValues(TreeSelection selection) {
		LineElement lineElement = selection.getFirstElementOfType(LineElement.class);
		boolean result = (lineElement != null);
		if (result) {
			getColorChooser().setColor(lineElement.getFormats().getLineColor());
			lineWidthInput.setValue(lineElement.getFormats().getLineWidth());
			
			CornerRadiusElement cornerRadiusElement = 
				  selection.getFirstElementOfType(CornerRadiusElement.class);
			cornerRadiusInput.setEnabled(cornerRadiusElement != null);
			if (cornerRadiusElement != null) {
				cornerRadiusInput.setValue(cornerRadiusElement.getFormats().getCornerRadius());
			}
		}
		return result;
	}


	public String title() {
		return "Line formats";
	}


	public void addOperators(List<FormatOperator> operators) {
		if (colorMonitor.hasChanged()) {
			operators.add(new LineColorOperator(getColorChooser().getColor()));
		}
		if (lineWidthInput.getChangeMonitor().hasChanged()) {
			operators.add(new LineWidthOperator(lineWidthInput.getValue()));
		}
		if (cornerRadiusInput.getChangeMonitor().hasChanged()) {
			operators.add(new CornerRadiusOperator(cornerRadiusInput.getValue()));
		}
	}


	public void addError(List<String> list) {
		if (lineWidthInput.getValue().getInMillimeters() < 0) {
			list.add("The line width cannot be less than 0.");
		}
		if (cornerRadiusInput.getValue().getInMillimeters() < 0) {
			list.add("The corner radius cannot be less than 0.");
		}
	}


	public void resetChangeMonitors() {
		colorMonitor.reset();
		lineWidthInput.getChangeMonitor().reset();
		cornerRadiusInput.getChangeMonitor().reset();
	}


	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.add(getLineColorPanel(), null);
		this.add(getOtherLineFormatsPanel(), null);
	}

	
	/**
	 * This method initializes lineColorChooser	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JColorChooser getColorChooser() {
		if (colorChooser == null) {
			colorChooser = new JColorChooser(Color.BLACK);
			colorChooser.getSelectionModel().addChangeListener(colorMonitor);
		}
		return colorChooser;
	}


	/**
	 * This method initializes lineColorPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getLineColorPanel() {
		if (lineColorPanel == null) {
			lineColorPanel = new JPanel();
			lineColorPanel.setLayout(new BoxLayout(getLineColorPanel(), BoxLayout.Y_AXIS));
			lineColorPanel.setBorder(BorderFactory.createTitledBorder(null, "Line color:", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			lineColorPanel.add(getColorChooser(), null);
		}
		return lineColorPanel;
	}


	/**
	 * This method initializes otherLineFormatsPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getOtherLineFormatsPanel() {
		if (otherLineFormatsPanel == null) {
			otherLineFormatsPanel = new JPanel();
			otherLineFormatsPanel.setLayout(new GridBagLayout());
			otherLineFormatsPanel.setBorder(BorderFactory.createTitledBorder(null, "", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
      lineWidthInput = new DistanceValueInput("Line width: ", otherLineFormatsPanel, 0);
      cornerRadiusInput = new DistanceValueInput("Corner radius: ", otherLineFormatsPanel, 2);
		}
		return otherLineFormatsPanel;
	}	
}