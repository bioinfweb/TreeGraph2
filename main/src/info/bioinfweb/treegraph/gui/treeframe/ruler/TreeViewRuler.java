/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2015-2016  Ben Stöver, Sarah Wiechers
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
package info.bioinfweb.treegraph.gui.treeframe.ruler;


import info.bioinfweb.treegraph.document.format.DistanceDimension;
import info.bioinfweb.treegraph.document.format.DistanceValue;
import info.bioinfweb.treegraph.gui.treeframe.TreeViewPanel;
import info.bioinfweb.treegraph.gui.treeframe.TreeViewPanelListener;
import info.bioinfweb.commons.Math2;
import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.SystemColor;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import javax.swing.JPanel;
import javax.swing.Scrollable;
import javax.swing.event.ChangeEvent;



/**
 * Component to display a ruler next to a tree.
 * 
 * @author Ben St&ouml;ver
 */
public class TreeViewRuler extends JPanel 
    implements Scrollable, TreeViewPanelListener, TreeViewRulerConstants {
	
	/** The maximal length of a dash */
	public static final float DASH_LENGTH = 10;
	
	/** The minimal distance that two dashes must have to be painted */
	public static final float MIN_DASH_DISTANCE = 2.5f;
	
	/** The distance of the labels to the border of the component */
	public static final float LABEL_DISTANCE = -2.2f;
	
	/** The factor to calculate the length of the smallest dashes from the maximal length */
	public static final float SMALL_FACTOR = 0.5f;
	
	/** The factor to calculate the length of the medium dashes from the maximal length */
	public static final float MEDIUM_FACTOR = 0.8f;

	/** 
	 * This string is used to test if the interval between two main dashes is smaller than 
	 * the usual label text. 
	 */
	public static final String LABEL_LENGTH_STANDARD = "000";

	
	private RulerOrientation orientation = RulerOrientation.VERTICAL;
	private RulerUnit unit = RulerUnit.CENTIMETER;
	private TreeViewPanel treeViewPanel = null;
	

	public TreeViewRuler(RulerOrientation orientation, TreeViewPanel treeViewPanel) {
		super();
		this.orientation = orientation;
		this.treeViewPanel = treeViewPanel;
		treeViewPanel.addTreeViewPanelListener(this);
		sizeChanged(new ChangeEvent(this));
	}


	public RulerUnit getUnit() {
		return unit;
	}


	public void setUnit(RulerUnit unit) {
		this.unit = unit;
		repaint();
	}


	public RulerOrientation getOrientation() {
		return orientation;
	}


	public Dimension getPreferredScrollableViewportSize() {
		return getPreferredSize();
	}


	public int getScrollableBlockIncrement(Rectangle arg0, int arg1, int arg2) {
		return 20; //TODO Welcher Wert ist hier sinnvoll?
	}


	public boolean getScrollableTracksViewportHeight() {
		return false;
	}


	public boolean getScrollableTracksViewportWidth() {
		return false;
	}


	public int getScrollableUnitIncrement(Rectangle arg0, int arg1, int arg2) {
		return 20; //TODO Welcher Wert ist hier sinnvoll?
	}


	public void selectionChanged(ChangeEvent e) {}


	public void zoomChanged(ChangeEvent e) {
		repaint();
	}


	public void sizeChanged(ChangeEvent e) {
		Dimension d = treeViewPanel.getPreferredSize();
		if (orientation.equals(RulerOrientation.HORIZONTAL)) {
			d.height = SMALL_LENGTH;
		}
		else {
			d.width = SMALL_LENGTH;
		}
		setSize(d);
		setPreferredSize(d);
	}


	@Override
	public Dimension getMinimumSize() {
		Dimension result;
		if (orientation.equals(RulerOrientation.HORIZONTAL)) {
			result = new Dimension(0, SMALL_LENGTH);
		}
		else {
			result = new Dimension(SMALL_LENGTH, 0);
		}
		return result;
	}
	

	@Override
	protected void paintComponent(Graphics g1) {
		Graphics2D g = (Graphics2D)g1;
  	g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
  			RenderingHints.VALUE_ANTIALIAS_ON);
  	
    // Calculate intervals:
  	float stepLength;
    int interval1;
    int interval2;
    if (getUnit().equals(RulerUnit.CENTIMETER)) {
    	stepLength = treeViewPanel.pixelsPerMillimeter();  // 1 mm
    	interval1 = 5;  // 5 mm
    	interval2 = 10;  // 1 cm
    }
    else {
    	stepLength = treeViewPanel.pixelsPerMillimeter() / DistanceValue.POINTS_PER_MM;  // 1 pt
    	interval1 = 12;  // 1 Teil-Linie
    	interval2 = 72;  // 1 in
    }
  	
    // Calculate length data:
    int end;
    DistanceDimension d = treeViewPanel.getDocument().getTree().getPaintDimension(
    		treeViewPanel.getPainterType());
    if (orientation.equals(RulerOrientation.HORIZONTAL)) {
      end = Math2.roundUp(d.getWidth().getInPixels(treeViewPanel.pixelsPerMillimeter())
      	                	/ stepLength);
  		g.setColor(SystemColor.menu);
  		g.fillRect(0, 0, Math2.roundUp(end * stepLength), SMALL_LENGTH);
  		g.setColor(SystemColor.menuText);
      g.draw(new Line2D.Float(0, SMALL_LENGTH - 1, end * stepLength, SMALL_LENGTH - 1));  // base line
    } 
    else {
      end = Math2.roundUp(d.getHeight().getInPixels(treeViewPanel.pixelsPerMillimeter()) 
      		                / stepLength);
  		g.setColor(SystemColor.menu);
  		g.fillRect(0, 0, SMALL_LENGTH, Math2.roundUp(end * stepLength));
  		g.setColor(SystemColor.menuText);
      g.draw(new Line2D.Float(SMALL_LENGTH - 1, 0, SMALL_LENGTH - 1, end * stepLength));  // base line
    }
    
    // Text data:
    g.setFont(FONT);
    FontMetrics fm = g.getFontMetrics();
    int labelInterval = interval2 * Math2.roundUp(
    		fm.stringWidth(LABEL_LENGTH_STANDARD) / (interval2 * stepLength));
    int ascent = fm.getAscent(); 
    
    for (int pos = 0; pos < end; pos++) {
    	float paintPos = pos * stepLength;
			float dashLength = DASH_LENGTH;
			boolean draw = true;
			if (pos % interval2 != 0) {
				if (pos % interval1 == 0) {
					dashLength *= MEDIUM_FACTOR;
					draw = (stepLength * interval1) >= MIN_DASH_DISTANCE;
				}
				else {
					dashLength *= SMALL_FACTOR;
					draw = stepLength >= MIN_DASH_DISTANCE;
				}
			}
			
	    if (draw) {
	  		g.setStroke(new BasicStroke(0, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
	  		Path2D path = new  Path2D.Float();
				if (orientation.equals(RulerOrientation.HORIZONTAL)) {
		  		path.moveTo(paintPos - 0.5, SMALL_LENGTH);
		  		path.lineTo(paintPos + 0.5, SMALL_LENGTH);
		  		path.lineTo(paintPos + 0.5, SMALL_LENGTH - dashLength);
		  		path.lineTo(paintPos - 0.5, SMALL_LENGTH - dashLength);
		  		path.closePath();
		    }
		    else {
		  		path.moveTo(SMALL_LENGTH, paintPos - 0.5);
		  		path.lineTo(SMALL_LENGTH, paintPos + 0.5);
		  		path.lineTo(SMALL_LENGTH - dashLength, paintPos + 0.5);
		  		path.lineTo(SMALL_LENGTH - dashLength, paintPos - 0.5);
		  		path.closePath();
		    }
	  		g.fill(path);
	  		
	  		if (pos % labelInterval == 0) {
	  			AffineTransform oldTrans = g.getTransform();
	  			AffineTransform trans = (AffineTransform)oldTrans.clone(); 
	  			float x;
	  			float y; 
	  			String text = "" + (pos / interval2);
	  			if (orientation.equals(RulerOrientation.HORIZONTAL)) {
	  				x = paintPos;
	  				y = LABEL_DISTANCE; 
	  			}
	  			else {
	  				x = LABEL_DISTANCE;
	  				y = paintPos + fm.stringWidth(text) - 1;
	  				trans.quadrantRotate(3, x, y);
	  				g.setTransform(trans);
	  			}
	  			g.drawString(text, x, y + ascent);
	  			g.setTransform(oldTrans);
	  		}
	    }
		}
	}
}