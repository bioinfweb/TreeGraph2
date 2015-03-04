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
package info.bioinfweb.treegraph.gui.mainframe;


import info.bioinfweb.treegraph.graphics.positionpaint.PositionPaintFactory;
import info.bioinfweb.treegraph.graphics.positionpaint.PositionPaintType;
import info.bioinfweb.treegraph.gui.actions.ActionManagement;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JPanel;
import javax.swing.JToolBar;



/**
 * Component used to display the wrapable tool bar of TreeGraph 2.
 * 
 * @author Ben St&ouml;ver
 */
public class ToolBarPanel extends JPanel {
	public enum ToolBarOrientation {
		HORIZONTAL, VERTICAL;
	}
	
	
	private MainFrame mainFrame = null;
	private ToolBarOrientation orientation = ToolBarOrientation.VERTICAL;
	private JToolBar upperToolBar = null;
	private JToolBar lowerToolBar = null;
	private GridBagConstraints editFormatsGBC = new GridBagConstraints();
	private GridBagConstraints fileViewWindowGBC = new GridBagConstraints();
	
	
	public ToolBarPanel(MainFrame mainFrame) {
		super();
		this.mainFrame = mainFrame;
		setLayout(new GridBagLayout());
		
		fileViewWindowGBC.fill = GridBagConstraints.NONE;
		fileViewWindowGBC.gridx = 0;
		fileViewWindowGBC.gridy = 0;
		fileViewWindowGBC.anchor = GridBagConstraints.WEST;
		add(getUpperToolBar(), fileViewWindowGBC);
		
		editFormatsGBC.fill = GridBagConstraints.HORIZONTAL;
		editFormatsGBC.gridx = 0;
		editFormatsGBC.gridy = 1;
		editFormatsGBC.weightx = 2.0;
		editFormatsGBC.anchor = GridBagConstraints.WEST;
		add(getLowerToolBar(), editFormatsGBC);
		
		addComponentListener(new java.awt.event.ComponentAdapter() {
			public void componentResized(java.awt.event.ComponentEvent e) {
				int width = (int)getUpperToolBar().getPreferredSize().getWidth() 
				    + (int)getLowerToolBar().getPreferredSize().getWidth();
				if (getWidth() >= width) {
					setOrientation(ToolBarOrientation.HORIZONTAL);
				}
				else {
					setOrientation(ToolBarOrientation.VERTICAL);
				}
			}
		});
	}


	public ToolBarOrientation getOrientation() {
		return orientation;
	}


	public void setOrientation(ToolBarOrientation alignment) {
		if (alignment == null) {
			throw new NullPointerException("Alignment cannnot be null.");
		}
		else if (!this.orientation.equals(alignment)) {
  		this.orientation = alignment;
  		
  		removeAll();
  		if (alignment.equals(ToolBarOrientation.VERTICAL)) {
  			editFormatsGBC.gridx = 0;
  			editFormatsGBC.gridy = 1;
  		}
  		else {  // ToolBarAlignment.HORIZONTAL
  			editFormatsGBC.gridx = 1;
  			editFormatsGBC.gridy = 0;
  		}
  		add(getUpperToolBar(), fileViewWindowGBC);
  		add(getLowerToolBar(), editFormatsGBC);
  		mainFrame.validate();
		}
	}


	private ActionManagement getActionManagement() {
		return mainFrame.getActionManagement();
	}
	
	
	/**
	 * This method initializes fileviewWindowToolBar	
	 * 	
	 * @return javax.swing.JToolBar	
	 */
	private JToolBar getUpperToolBar() {
		if (upperToolBar == null) {
			upperToolBar = new JToolBar();
			upperToolBar.setFloatable(false);
			upperToolBar.setRollover(true);
			upperToolBar.add(getActionManagement().get("file.newDocument"));
			upperToolBar.add(getActionManagement().get("file.open"));
			upperToolBar.add(getActionManagement().get("file.save"));
			upperToolBar.add(getActionManagement().get("file.exportPDF"));
			upperToolBar.addSeparator();
			upperToolBar.add(getActionManagement().get("edit.undo"));
			upperToolBar.add(getActionManagement().get("edit.redo"));
			upperToolBar.addSeparator();
			upperToolBar.add(getActionManagement().get("edit.copyElement"));
			upperToolBar.add(getActionManagement().get("edit.cut"));
			upperToolBar.add(getActionManagement().get("edit.paste"));
			upperToolBar.add(getActionManagement().get("edit.delete"));
			upperToolBar.addSeparator();
			upperToolBar.add(getActionManagement().get("edit.moveSubtreeUp"));
			upperToolBar.add(getActionManagement().get("edit.moveSubtreeDown"));
			upperToolBar.addSeparator();
    }
		return upperToolBar;
	}
	
	
	/**
	 * This method initializes editFormatToolBar	
	 * 	
	 * @return javax.swing.JToolBar	
	 */
	private JToolBar getLowerToolBar() {
		if (lowerToolBar == null) {
			lowerToolBar = new JToolBar();
			lowerToolBar.setFloatable(false);
			lowerToolBar.setRollover(true);
			lowerToolBar.add(getActionManagement().get("format.editDocumentFormats"));
			lowerToolBar.add(getActionManagement().get("format.showRooted"));
			lowerToolBar.add(getActionManagement().get("format.showScaleBar"));
			lowerToolBar.addSeparator();
			lowerToolBar.add(getActionManagement().get("view.setZoomToOriginal"));
			lowerToolBar.add(getActionManagement().get("view.fitZoomToWidth"));
			lowerToolBar.add(getActionManagement().get("view.fitZoomToWidthHeight"));
			lowerToolBar.addSeparator();
			for (PositionPaintType type: PositionPaintType.values()) {
				lowerToolBar.add(getActionManagement().get(
						"view.setPainterID_" + PositionPaintFactory.getInstance().getName(type)));
			}
			lowerToolBar.addSeparator();
			lowerToolBar.add(getActionManagement().get("window.previousDocument"));
			lowerToolBar.add(getActionManagement().get("window.nextDocument"));
			lowerToolBar.addSeparator();
			lowerToolBar.add(getActionManagement().get("help.contents"));
		}
		return lowerToolBar;
	}
}