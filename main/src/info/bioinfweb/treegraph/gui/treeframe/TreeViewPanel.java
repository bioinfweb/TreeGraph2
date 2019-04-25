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
package info.bioinfweb.treegraph.gui.treeframe;


import info.bioinfweb.treegraph.document.*;
import info.bioinfweb.treegraph.document.change.DocumentChangeEvent;
import info.bioinfweb.treegraph.document.change.DocumentListener;
import info.bioinfweb.treegraph.document.format.DistanceDimension;
import info.bioinfweb.treegraph.document.format.DistanceValue;
import info.bioinfweb.treegraph.document.format.PieChartLabelFormats;
import info.bioinfweb.treegraph.graphics.positionpaint.PositionPaintFactory;
import info.bioinfweb.treegraph.graphics.positionpaint.PositionPaintType;
import info.bioinfweb.treegraph.gui.mainframe.MainFrame;
import info.bioinfweb.commons.graphics.GraphicsUtils;
import info.bioinfweb.commons.swing.scrollpaneselector.ScrollPaneSelectable;

import java.util.*;

import javax.swing.*;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;

import javax.swing.event.ChangeEvent;



/**
 * Shows the tree saved in the associated document in a swing GUI.
 * <p>
 * This class acts as the view-element in the MVC-paradigm, while the model is a {@code Document}-class.
 * 
 * @author Ben St&ouml;ver
 */
public class TreeViewPanel extends JPanel implements DocumentListener, Scrollable, ScrollPaneSelectable {
	private static final long serialVersionUID = 1;
	
	public static final Dimension START_MIN_SIZE = new Dimension(100, 60);
	public static final float SELECTION_MARGIN = 2;  // in px
	public static final float PIXELS_PER_MM_100 = 2.8346456692919635439270879919f;  // This could also be calculated system dependent using Toolkit.getScreenResolution().
	public static final float MIN_ZOOM = 0.1f;
	public static final Color DEFAULT_SELECTION_COLOR = Color.BLUE.brighter();
	public static final Color ALTERNATIVE_SELECTION_COLOR = Color.WHITE;
	public static final int MAX_SELECTION_COLOR_DIF = 200;

	
	private TreeSelection selection = new TreeSelection(this);
	private Document document = null;
	private float zoom = 1f;
	private PositionPaintType painterType = PositionPaintFactory.getDefaultType();
	private Vector<TreeViewPanelListener> treeViewListeners = new Vector<TreeViewPanelListener>(2, 2);
	
	
	/**
	 * Creates a new swing-panel which shows the tree contained in document.
	 * This class registers itself as a view at the given document.
	 * 
	 * @param document the document to show
	 */
	public TreeViewPanel(Document document) {
		super();
		setLayout(null);
		setDocument(document);
		ToolTipManager.sharedInstance().registerComponent(this);
	}
	

	public float pixelsPerMillimeter() {
		return PIXELS_PER_MM_100 * getZoom();
	}
  
  
	public TreeSelection getSelection() {
		return selection;
	}


	/**
	 * Returns the currently associated document.
	 * 
	 * @return the currently associated document
	 */
	public Document getDocument() {
		return document;
	}


	/**
	 * Sets this view to a new model and repaints the element using the data of the 
	 * new document. This view is automatically unregistered at the old and registered at 
	 * the new document.
	 * 
	 * @param document the model to associate with
	 */
	public void setDocument(Document document) {
		if (this.document != null) {  // Beim Aufruf im Konstruktor.
			this.document.removeView(this);
		}
		
		if (document != null) {
			this.document = document;
			document.addView(this);
			document.registerPositioner(painterType);  //TODO Deregistrierung an entsprechenden Stellen implementieren!
		  PositionPaintFactory.getInstance().getPositioner(painterType).positionAll(document, 1f);
			getSelection().set(null);
		  changeHappened(new DocumentChangeEvent(document, null));
		}
	}


	public float getZoom() {
		return zoom;
	}


	public void setZoom(float zoom) {
		zoom = Math.max(MIN_ZOOM, zoom);
		this.zoom = zoom;
		assignPaintSize();
		repaint();
		fireZoomChanged();
	}


	public PositionPaintType getPainterType() {
		return painterType;
	}


	public void setPainterType(PositionPaintType type) {
		PositionPaintType oldType = painterType;
		painterType = type;
	  if (getDocument() != null) {
	  	document.unregisterPositioner(oldType);  //TODO Führt zu einem Fehler, falls ein anderes TreeViewPanel hierzu ebenfalls einen Positioner angemeldet hat.
			document.registerPositioner(painterType);
		  PositionPaintFactory.getInstance().getPositioner(painterType).positionAll(document, 1f);
		  changeHappened(new DocumentChangeEvent(document, null));
	  }
	}


	public void addTreeViewPanelListener(TreeViewPanelListener listener) {
		treeViewListeners.add(listener);
	}
	
	
	public boolean removeTreeViewPanelListener(TreeViewPanelListener listener) {
		return treeViewListeners.remove(listener);
	}
	
	
	public void fireSelectionChanged() {
		repaint();
		MainFrame.getInstance().getActionManagement().refreshActionStatus();  //TODO besser: MainFrame als Listener registrieren 
		for (int i = 0; i < treeViewListeners.size(); i++) {
			treeViewListeners.get(i).selectionChanged(new ChangeEvent(this));
		}
	}
	
	
	private void fireZoomChanged() {
		for (int i = 0; i < treeViewListeners.size(); i++) {
			treeViewListeners.get(i).zoomChanged(new ChangeEvent(this));
		}
	}
	
	
	private void fireSizeChanged() {
		for (int i = 0; i < treeViewListeners.size(); i++) {
			treeViewListeners.get(i).sizeChanged(new ChangeEvent(this));
		}
	}
	
	
	private void assignPaintSize() {
		DistanceDimension d = getDocument().getTree().getPaintDimension(painterType);
		setSize(d.getWidth().getRoundedInPixels(pixelsPerMillimeter()), d.getHeight().getRoundedInPixels(pixelsPerMillimeter()));
		setPreferredSize(getSize());  //TODO was ist der genaue Unterschied der beiden Größen?
		fireSizeChanged();
	}
	
	
  @Override
	protected void paintComponent(Graphics g) {
  	super.paintComponent(g);
  	Graphics2D g2 = (Graphics2D)g;
  	g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
  	g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
  	g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		PositionPaintFactory.getInstance().getPainter(painterType).paintTree(
				g2, getVisibleRect(), getDocument(), getSelection(), pixelsPerMillimeter(), false);
	}


  @Override
	public void paintPreview(Graphics2D g, double scale) {
  	g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
  	g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
  	g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		PositionPaintFactory.getInstance().getPainter(painterType).paintTree(g, getDocument(), 
				getSelection(), pixelsPerMillimeter() * (float)scale, false);
	}


	/* (non-Javadoc)
	 * @see DocumentView#changeHappened()
	 */
  @Override
	public void changeHappened(DocumentChangeEvent e) {
		getSelection().setValueIsAdjusting(true);
		try {
			Iterator<PaintableElement> iterator = getSelection().iterator();
			while (iterator.hasNext()) {
				if (!document.getTree().contains(iterator.next())) {  // Das markierte Element könnte entfernt worden sein.
					iterator.remove();
				}
			}
		}
		finally {
			getSelection().setValueIsAdjusting(false);
		}
		MainFrame.getInstance().getActionManagement().refreshActionStatus();
		assignPaintSize();
		repaint();
	}


  @Override
	public Dimension getPreferredScrollableViewportSize() {
		return getPreferredSize();
	}


  @Override
	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		return 20; //TODO Welcher Wert ist hier sinnvoll?
	}


  @Override
	public boolean getScrollableTracksViewportHeight() {
		return false;
	}


  @Override
	public boolean getScrollableTracksViewportWidth() {
		return false;
	}


  @Override
	public int getScrollableUnitIncrement(Rectangle arg0, int arg1, int arg2) {
		return 20; //TODO Welcher Wert ist hier sinnvoll?
	}


	/**
	 * This method is overwritten to show the internal node names or label IDs as tooltip 
	 * text.
	 * 
	 * @param e - the <code>MouseEvent</code> that lead to the call of this method
	 * @see javax.swing.JComponent#getToolTipText(java.awt.event.MouseEvent)
	 */
	@Override
	public String getToolTipText(MouseEvent e) {
		PaintableElement element = PositionPaintFactory.getInstance().getPositioner(
				getPainterType()).elementToPosition(
						getDocument(), 
						DistanceValue.pixelsToMillimeters(e.getX(), pixelsPerMillimeter()), 
						DistanceValue.pixelsToMillimeters(e.getY(),	pixelsPerMillimeter()),
						TreeViewPanel.SELECTION_MARGIN);
		
		if (element instanceof Node) {
			String text = ""; //+ ((Node)element).getPosition(getPainterType()).getHeightAbove() + " ";
			if (!((Node)element).isLeaf()) {
				text += "Internal node name: \"" + ((Node)element).getData() + "\" \n";
			}
			if (((Node)element).hasUniqueName()) {
				text += "Unique name: \"" + ((Node)element).getUniqueName() + "\""; 
			}
			if (!text.equals("")) {
				return text;
			}
		}
		else if (element instanceof Branch) {
			if (((Branch)element).hasLength()) {
				return "Branch length: " + ((Branch)element).getLength();
			}
			else {
				return "No branch length assigned.";
			}
		}
		else if (element instanceof Label) {
			String id = ((Label)element).getID();
			StringBuilder result = new StringBuilder();
			if (id.equals("")) {
				result.append("<html>No label ID defined for this label.");
			}
			result.append("<html>Label ID: \"" + id + "\"");
			
			if (element instanceof PieChartLabel) {
				PieChartLabel label = (PieChartLabel)element;
				if (!label.getSectionDataList().isEmpty()) {
					result.append(" (states: ");
					PieChartLabelFormats formats = label.getFormats();
					for (int i = 0; i < label.getSectionDataList().size(); i++) {
						result.append("<span style='color: ");
						result.append(GraphicsUtils.colorToHexString(formats.getPieColor(i)));
						result.append(";'>");
						result.append(label.getSectionDataList().get(i).getValueColumnID());
						result.append("</span>");
						if (i < label.getSectionDataList().size() - 1) {
							result.append(", ");
						}
					}
					result.append(")");
				}
			}
			result.append("</html>");
			return result.toString();
		}
		return null;
	}
	
	
	public void scrollElementToVisible(PaintableElement element) {
		scrollRectToVisible(element.getPosition(getPainterType()).toRect(pixelsPerMillimeter()));
	}
	
	
	public static Color selectionColor(Color bgColor) {
		if (GraphicsUtils.brightnessDifference(bgColor, DEFAULT_SELECTION_COLOR) > MAX_SELECTION_COLOR_DIF) {
			return DEFAULT_SELECTION_COLOR;
		}
		else {
			return ALTERNATIVE_SELECTION_COLOR;
		}
	}
}