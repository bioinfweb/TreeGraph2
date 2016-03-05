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
package info.bioinfweb.treegraph.graphics.positionpaint;


import info.bioinfweb.treegraph.document.*;
import info.bioinfweb.treegraph.document.format.*;
import info.bioinfweb.treegraph.document.position.LegendPositionData;
import info.bioinfweb.treegraph.document.position.NodePositionData;
import info.bioinfweb.treegraph.document.position.PositionData;
import info.bioinfweb.treegraph.graphics.positionpaint.labelicons.LabelIconMap;
import info.bioinfweb.treegraph.gui.treeframe.TreeSelection;
import info.bioinfweb.treegraph.gui.treeframe.TreeViewPanel;
import info.bioinfweb.commons.Math2;
import info.bioinfweb.commons.graphics.FontCalculator;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.BasicStroke;
import java.awt.geom.*;



/**
 * Paints the document in the rectangular cladogram view if it was previously positioned.
 * @author Ben St&ouml;ver
 */
public class RectangularCladogramPainter implements TreePainter {
	public static final float SELECTION_DISTANCE = 2;  // px  //TODO ggf. mit TreeViewPanel.SELECTION_MARGIN vereinigen.
	public static final float UNDERLINE_DISTANCE_FACTOR = 0.15f;
	public static final float UNDERLINE_LINE_WIDTH_FACTOR = 0.05f;
	public static final float UNDERLINE_X_SHIFT_FACTOR = 0.07f;
	public static final float SHORT_DASH_SCALE_FACTOR = 0.5f;
	
	
	private static RectangularCladogramPainter firstInstance = null; 
	
	protected final PositionPaintType type = PositionPaintFactory.getInstance().getType(this);
	private Graphics2D g = null;
	private Rectangle visibleRect = null;
	private Document document = null;
	private TreeSelection selection = null;
	private float pixelsPerMillimeter = 1;
	
	
	protected RectangularCladogramPainter() {}
	
	
	public static RectangularCladogramPainter getInstance() {
		if (firstInstance == null) {
			firstInstance = new RectangularCladogramPainter();
		}
		return firstInstance;
	}
	
	
	private void paintSelection(ConcretePaintableElement element) {
		if ((selection != null) && selection.contains(element)) {
			g.setColor(TreeViewPanel.selectionColor(
					document.getTree().getFormats().getBackgroundColor()));
			
			PositionData pd = element.getPosition(type);
			g.draw(new Rectangle2D.Float(
					pd.getLeft().getInPixels(pixelsPerMillimeter) - SELECTION_DISTANCE, 
					pd.getTop().getInPixels(pixelsPerMillimeter) - SELECTION_DISTANCE, 
					pd.getWidth().getInPixels(pixelsPerMillimeter) + 2 * SELECTION_DISTANCE, 
					pd.getHeight().getInPixels(pixelsPerMillimeter) + 2 * SELECTION_DISTANCE));
		}
	}
		
	
	private float paintText(String text, TextFormats f, float x, float y) {
		Font font = f.getFont(pixelsPerMillimeter);
		g.setColor(f.getTextColor());
		g.setFont(font);
		g.drawString(text, x, y);
		
		float width = FontCalculator.getInstance().getWidth(font, text);
		if (f.hasTextStyle(TextFormats.UNDERLINE)) {
			float height = FontCalculator.getInstance().getHeight(font);
			Stroke oldStroke = g.getStroke();
			g.setStroke(new BasicStroke(UNDERLINE_LINE_WIDTH_FACTOR * height));
			x += UNDERLINE_X_SHIFT_FACTOR * height;  // Linie ist sonst nicht genau unter dem Text 
			y += UNDERLINE_DISTANCE_FACTOR * height;
			g.draw(new Line2D.Float(x, y, x + width, y));  //TODO Liniendicke setzen
			g.setStroke(oldStroke);
		}
		
		return width;
	}
	
	
	private void paintBranch(Branch b) {
		PositionData pd = b.getPosition(type);
		BranchFormats bf = b.getFormats();
		Node n = b.getTargetNode();
		float startWidth;
		boolean parentHasEdgeRadius = false;
		if (n.hasParent() && !bf.isConstantWidth()) {
			startWidth = n.getParent().getFormats().getLineWidth().getInPixels(pixelsPerMillimeter);
			parentHasEdgeRadius = n.getParent().getFormats().getCornerRadius().getInMillimeters() > 0;
		}
		else {
			startWidth = bf.getLineWidth().getInPixels(pixelsPerMillimeter);
		}
		
		float middle = pd.getTop().getInPixels(pixelsPerMillimeter) + 0.5f * pd.getHeight().getInPixels(pixelsPerMillimeter);
		float halfHeightLeft = 0.5f * startWidth;
		float halfHeightRight = 0.5f * b.getFormats().getLineWidth().getInPixels(pixelsPerMillimeter);
		float left = pd.getLeft().getInPixels(pixelsPerMillimeter);
		if ((!n.isFirst() && !n.isLast()) || !parentHasEdgeRadius) {
			left = (int)left;  //�berlappung der Linien erreichen
		}
		float right = pd.getRightInPixels(pixelsPerMillimeter);
		if (!n.isLeaf()) {
			right = Math2.roundUp(right);  //�berlappung der Linien erreichen
		}
		
		Path2D path = new  Path2D.Float();
		path.moveTo(left, middle - halfHeightLeft);
		path.lineTo(right, middle - halfHeightRight);
		path.lineTo(right, middle + halfHeightRight);
		path.lineTo(left, middle + halfHeightLeft);
		path.closePath();
		
		g.setColor(b.getFormats().getLineColor());
		g.fill(path);
		
  	paintSelection(b);
	}
	
	
	/**
	 * Tests of at least two of the specified angles are valid floating point values greater than zero.
	 * @param angles - the an array of angles
	 * @return <code>true</code> if att least two valid angles were found
	 */
	private boolean twoSectorsNotNull(double[] angles) {
		int count = 0;
		for (int i = 0; i < angles.length; i++) {
			if (!Double.isNaN(angles[i]) && (angles[i] > 0)) {
				count++;
			}
		}
		return (count >= 2);
	}
	
	
	private void paintLabelBlock(Labels labels, boolean above) {
		for (int lineNo = 0; lineNo < labels.lineCount(above); lineNo++) {
			for (int i = 0; i < labels.labelCount(above, lineNo); i++) {
				Label label = labels.get(above, lineNo, i);
				PositionData pd = label.getPosition(type);
				
				if (label instanceof TextLabel) {
					TextLabelFormats f = ((TextLabel)label).getFormats(); 
					paintText(((TextLabel)label).getData().formatValue(f.getDecimalFormat()), f, 
							pd.getLeft().getInPixels(pixelsPerMillimeter), 
							pd.getTop().getInPixels(pixelsPerMillimeter) + 
							g.getFontMetrics(f.getFont(pixelsPerMillimeter)).getAscent());
				}
				else if (label instanceof IconLabel) {
					IconLabelFormats f = ((IconLabel)label).getFormats();
					LabelIconMap.getInstance().get(f.getIcon()).paint(g, 
							pd.getLeft().getInPixels(pixelsPerMillimeter), 
							pd.getTop().getInPixels(pixelsPerMillimeter), f, pixelsPerMillimeter);
				}
				else if (label instanceof PieChartLabel) {
					PieChartLabel l = (PieChartLabel)label;
					double[] angles = l.getPieChartAngles();
					double startAngle = 0;
					boolean twoValid = twoSectorsNotNull(angles);
					PieChartLabelFormats f = l.getFormats();
					Stroke stroke = g.getStroke();
					try {
				    g.setStroke(new BasicStroke(f.getLineWidth().getInPixels(pixelsPerMillimeter)));
				    float x = pd.getLeft().getInPixels(pixelsPerMillimeter);
				    float y = pd.getTop().getInPixels(pixelsPerMillimeter);
				    float width = pd.getWidth().getInPixels(pixelsPerMillimeter);
				    float height = pd.getHeight().getInPixels(pixelsPerMillimeter);
				    
				    // Draw arc areas:
				    Arc2D.Double[] arcs = new Arc2D.Double[angles.length];
						for (int j = 0; j < angles.length; j++) {
							if (f.getShowNullLines() || (!Double.isNaN(angles[j]) && (angles[j] > 0))) {
								arcs[j] = new Arc2D.Double(x, y, width, height, 
										Math.round(startAngle), Math.round(angles[j]), Arc2D.PIE);  // Rounding is necessary, as a workaround for rendering errors in SVG, if too small angles are written. (A higher precision of 0.1° already leads to deformed arcs.)
								g.setColor(f.getPieColor(j));
								g.fill(arcs[j]);
								startAngle += angles[j];
							}
						}
						
						// Draw arc borders:
						g.setColor(f.getLineColor());
						if (f.getShowInternalLines() && (twoValid || f.getShowNullLines())) {
							startAngle = 0.0;
							for (int j = 0; j < arcs.length; j++) {  // Borders need to be drawn after all areas, because borders of null wide parts would be hidden in SVG otherwise.
								if (f.getShowNullLines() || (!Double.isNaN(angles[j]) && (angles[j] > 0))) {
									g.draw(arcs[j]);
									startAngle += angles[j];
								}
							}
						}
						g.draw(new Ellipse2D.Float(x, y, width, height));  // In some cases the whole border may already have been painted, but not always.
					}
					finally {
						g.setStroke(stroke);
					}
				}
				else {
					throw new InternalError("Unsupported label of type " + label.getClass().getCanonicalName() + " found.");
				}
				
  			paintSelection(label);
			}
		}
	}
	
	
	/**
	 * Paints the given node.
	 * @param n the node to paint
	 * @return the corner radius actually used
	 */
	private void paintInternalNode(Node n) {
		Stroke oldStroke = g.getStroke();
		PositionData pd = n.getPosition(type);
		
		// Draw vertical line:
		float width = n.getFormats().getLineWidth().getInPixels(pixelsPerMillimeter);
		float left = pd.getLeft().getInPixels(pixelsPerMillimeter) + 0.5f * width;  // angegeben Koordinaten befinden sich in der Mitte einer Linie
		float dY = -0.5f * width;
		float cornerRadius = Math.min(n.getFormats().getCornerRadius().getInPixels(pixelsPerMillimeter), pd.getWidth().getInPixels(pixelsPerMillimeter) - width);
		if (cornerRadius > 0) {
		  dY = width + cornerRadius;
		}
		float top = pd.getTop().getInPixels(pixelsPerMillimeter) + dY;
		float height = Math.max(0, pd.getHeight().getInPixels(pixelsPerMillimeter) - 2 * dY);

		g.setStroke(new BasicStroke(width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
		g.setColor(n.getFormats().getLineColor());
		if (cornerRadius > 0) {
			Path2D path = new  Path2D.Float();
			float length = 2 * cornerRadius;
			float topEdge = pd.getTop().getInPixels(pixelsPerMillimeter);
			float verticalLeft = left + cornerRadius + 0.5f * width;
			//path.append(new Line2D.Float(verticalLeft, topEdge, verticalLeft, topEdge), false);
			path.append(new Line2D.Float(Math2.roundUp(verticalLeft), topEdge, verticalLeft, topEdge), false);  //Linie nach rechts verl�ngern um �berlappung zu erreichen
			path.append(new Arc2D.Float(left, topEdge, length, length, 90, 90, Arc2D.OPEN), true);
			
  		path.append(new Line2D.Float(left, top, left, top + height), true);
  		
			height += 2 * width;
			path.append(new Arc2D.Float(left, topEdge + height, length, length, 180, 90, Arc2D.OPEN), true);
			path.append(new Line2D.Float(verticalLeft, topEdge + height + 2 * cornerRadius, Math2.roundUp(verticalLeft), topEdge + height + 2 * cornerRadius), true);  //Linie nach rechts verl�ngern um �berlappung zu erreichen

			g.draw(path);
		}
		else {
			float upperHeightDiff = 0;
			float lowerHeightDiff = 0;
			BranchFormats formats = n.getChildren().get(0).getAfferentBranch().getFormats(); 
			if (formats.isConstantWidth()) {
				upperHeightDiff = 0.5f * (formats.getLineWidth().getInPixels(pixelsPerMillimeter) - 
						n.getFormats().getLineWidth().getInPixels(pixelsPerMillimeter));
			}
			formats = n.getChildren().get(n.getChildren().size() - 1).getAfferentBranch().getFormats(); 
			if (formats.isConstantWidth()) {
				lowerHeightDiff = 0.5f * (formats.getLineWidth().getInPixels(pixelsPerMillimeter) - 
						n.getFormats().getLineWidth().getInPixels(pixelsPerMillimeter));
			}
  		g.draw(new Line2D.Float(left, top - upperHeightDiff, left, top + height + lowerHeightDiff));
		}
		g.setStroke(oldStroke);
		
		paintSelection(n);
	}
	
	
	private void paintPointNode(Node n) {
		float radius = 1.5f * n.getFormats().getLineWidth().getInPixels(pixelsPerMillimeter);
		PositionData pd = n.getPosition(type);
		g.setColor(n.getFormats().getLineColor());
		g.fill(new Ellipse2D.Float(
				pd.getLeft().getInPixels(pixelsPerMillimeter) + 0.5f * pd.getWidth().getInPixels(pixelsPerMillimeter) - radius,
				pd.getTop().getInPixels(pixelsPerMillimeter) + 0.5f * pd.getHeight().getInPixels(pixelsPerMillimeter) - radius,
				2 *  radius, 2 *  radius));
		
		paintSelection(n);
	}
	
	
	private void paintLeaf(Node leaf) {
		PositionData pd = leaf.getPosition(type);
		NodeFormats f = leaf.getFormats();
		Margin m = leaf.getFormats().getLeafMargin();
		paintText(leaf.getData().formatValue(f.getDecimalFormat()), f, 
				pd.getLeft().getInPixels(pixelsPerMillimeter) + 
				m.getLeft().getInPixels(pixelsPerMillimeter), 
				pd.getTop().getInPixels(pixelsPerMillimeter) + 
				m.getTop().getInPixels(pixelsPerMillimeter) + 
				g.getFontMetrics(f.getFont(pixelsPerMillimeter)).getAscent());
		paintSelection(leaf);
	}
	
	
	private boolean subtreeVisible(Node root) {
		NodePositionData pd = root.getPosition(type);
		float middle = pd.getTop().getInPixels(pixelsPerMillimeter) + 
		    0.5f * pd.getHeight().getInPixels(pixelsPerMillimeter);
		boolean notVisible = 
			  (root.getAfferentBranch().getPosition(type).getLeft().getRoundedInPixels(pixelsPerMillimeter) > (visibleRect.x + visibleRect.width)) ||
			  (visibleRect.y > middle + Math2.roundUp(pd.getHeightBelow() * pixelsPerMillimeter)) || 
			  (visibleRect.y + visibleRect.height < middle - Math2.roundUp(pd.getHeightAbove() * pixelsPerMillimeter));
		return !notVisible;
	}
	
	
	private void paintSubtree(Node root) {
		if (subtreeVisible(root)) {
			if (root.hasAfferentBranch()) {
				if ((root.hasParent() || document.getTree().getFormats().getShowRooted())) {
					paintBranch(root.getAfferentBranch());
				}
				paintLabelBlock(root.getAfferentBranch().getLabels(), true);
				paintLabelBlock(root.getAfferentBranch().getLabels(), false);
			}
			if (root.getChildren().size() == 0) {
				paintLeaf(root);
			}
			else {
				if (root.getChildren().size() == 1) {
					paintPointNode(root);
				}
				else {
					paintInternalNode(root);
				}
				for (int i = 0; i < root.getChildren().size(); i++) {
					paintSubtree(root.getChildren().get(i));
				}
			}
		}
	}
	
	
	private void paintScaleBar(ScaleBar scaleBar) {
		Stroke previousStroke = g.getStroke();
		
		ScaleBarFormats f = scaleBar.getFormats();
		PositionData pd = scaleBar.getPosition(type);
		g.setColor(f.getLineColor());
    g.setStroke(new BasicStroke(f.getLineWidth().getInPixels(pixelsPerMillimeter)));
    
    // Base line:
    float baseLineY = pd.getTop().getInPixels(pixelsPerMillimeter) + 
        f.getHeight().getInPixels(pixelsPerMillimeter);
    float left = pd.getLeft().getInPixels(pixelsPerMillimeter);
    float right = pd.getLeft().getInPixels(pixelsPerMillimeter) + 
		    pd.getWidth().getInPixels(pixelsPerMillimeter); 
    g.draw(new Line2D.Float(left, baseLineY, right, baseLineY));
    
    // Dashes:
    float branchLengthScale = 
  	    document.getTree().getFormats().getBranchLengthScale().getInPixels(pixelsPerMillimeter);
    float shortDashY = baseLineY - 
        (f.getHeight().getInPixels(pixelsPerMillimeter) * SHORT_DASH_SCALE_FACTOR); 
    float longDashY = pd.getTop().getInPixels(pixelsPerMillimeter);
    FontMetrics fm = g.getFontMetrics(f.getFont(pixelsPerMillimeter));
    
    float start = left;
    float end = right;
    float step = ScaleValue.branchLengthToDistance(f.getSmallInterval(), branchLengthScale);
    if (!f.isStartLeft()) {
    	start = right;
    	end = left;
    	step = -step;
    }
    int dashCount = 0;
		g.setStroke(new BasicStroke(0, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
		float halfLineWidth = f.getLineWidth().getInPixels(pixelsPerMillimeter) / 2; 
    for (float x = start; ((x <= end) && (step > 0) || (x >= end) && (step < 0)); 
        x += step) {
    	
  		Path2D path = new  Path2D.Float();
    	if (dashCount % f.getLongInterval() == 0) {
        // Prepare dash:
	  		path.moveTo(x - halfLineWidth, longDashY);
	  		path.lineTo(x + halfLineWidth, longDashY);
        
        // Paint values:
        float value = dashCount * f.getSmallInterval();
        if (!f.isIncreasing()) {
        	value = -value;
        }
        String labelText;
       	labelText = f.getDecimalFormat().format(value);
        
        float labelWidth = fm.stringWidth(labelText);
        float labelX = x;
        if (!f.isStartLeft()) {
        	labelX -= labelWidth;
        }
        
        if ((labelX >= left) && (labelX + labelWidth <= right)) {
        	paintText(labelText, scaleBar.getFormats(), labelX, baseLineY + fm.getAscent());
      		g.setColor(f.getLineColor());  // paintText changes the color
        }
    	}
    	else {
	  		path.moveTo(x - halfLineWidth, shortDashY);
	  		path.lineTo(x + halfLineWidth, shortDashY);
    	}
    	
    	// Complete dash:
  		path.lineTo(x + halfLineWidth, baseLineY);
  		path.lineTo(x - halfLineWidth, baseLineY);
  		path.closePath();
  		g.fill(path);
  		
    	dashCount++;
		}
    
    // Paint unit text:
    String text = scaleBar.getData().formatValue(scaleBar.getFormats().getDecimalFormat());
    float unitX = left;
    if (!f.isStartLeft()) {
    	unitX = right - fm.stringWidth(text);
    }
  	paintText(text, scaleBar.getFormats(), unitX, 
  			baseLineY + fm.getHeight() + fm.getAscent());
  	g.setStroke(previousStroke);
    
		paintSelection(scaleBar);
	}
	
	
	private boolean legendVisible(Legend l) {
		LegendPositionData pd = l.getPosition(type);
		boolean notVisible =
			(visibleRect.x > Math2.roundUp(pd.getRightInPixels(pixelsPerMillimeter))) ||
		  ((visibleRect.x + visibleRect.width) < pd.getLeft().getInPixels(pixelsPerMillimeter)) ||
		  (visibleRect.y > Math2.roundUp(pd.getBottomInPixels(pixelsPerMillimeter))) || 
		  (visibleRect.y + visibleRect.height < pd.getTop().getInPixels(pixelsPerMillimeter));
		return !notVisible;
	}
	
	
	private void paintLegend(Legend l) {
		if (legendVisible(l)) {
			Stroke oldStroke = g.getStroke();
			LegendPositionData pd = l.getPosition(type);
			LegendFormats f = l.getFormats();
			
			// Klammer zeichnen:
			float lineWidth = f.getLineWidth().getInPixels(pixelsPerMillimeter);
			g.setColor(f.getLineColor());
	    g.setStroke(new BasicStroke(lineWidth));
	    if (f.getLegendStyle().equals(LegendStyle.BRACE)) {
	  		float cornerRadius = Math.min(f.getCornerRadius().getInPixels(pixelsPerMillimeter), 
	  				0.5f * (pd.getLinePos().getWidth().getInPixels(pixelsPerMillimeter) - lineWidth 
	  						- f.getSpacing().getInPixels(pixelsPerMillimeter)));  // LegendSpacing ist in der gespeicherten Breite der Klammer enthalten.
	  		float left = pd.getLinePos().getLeft().getInPixels(pixelsPerMillimeter) + 0.5f * lineWidth + cornerRadius;  // angegeben Koordinaten befinden sich in der Mitte einer Linie
	  		float dY = lineWidth + cornerRadius;
	  		float top = pd.getLinePos().getTop().getInPixels(pixelsPerMillimeter) + dY;
	  		float height = Math.max(0, pd.getLinePos().getHeight().getInPixels(pixelsPerMillimeter) - 2 * dY);
	  		
	  		if (cornerRadius > 0) {
	  			// Senkrechte Linien:
	    		g.draw(new Line2D.Float(left, top, left, top + 0.5f * height - cornerRadius));
	    		g.draw(new Line2D.Float(left, top + 0.5f * height + cornerRadius, left, top + height));
	    		
	    		// Mittlere B�gen:
	    		float middleTop = top + 0.5f * height - cornerRadius;
	    		float length = 2 * cornerRadius;
	  			g.draw(new Arc2D.Float(left, middleTop - cornerRadius, length, length, 180, 90, Arc2D.OPEN));
	  			g.draw(new Arc2D.Float(left, middleTop + cornerRadius, length, length, 90, 90, Arc2D.OPEN));
	  			
	  			// �u�ere B�gen:
	  			height += 2 * lineWidth;
	  			top = pd.getLinePos().getTop().getInPixels(pixelsPerMillimeter);
	  			left -= 2 * cornerRadius;  // Linke obere Ecke des Kreises wird angegeben.
	  			g.draw(new Arc2D.Float(left, top, length, length, 0, 90, Arc2D.OPEN));
	  			g.draw(new Arc2D.Float(left, top + height, length, length, 270, 90, Arc2D.OPEN));
	  		}
	  		else {  // nur eine durchgehende sekrechte Linie wenn kein cornerRadius vorhanden
	    		g.draw(new Line2D.Float(left, top, left, top + height));
	  		}
	    }
	    else {  // f.getLegendStyle().equals(LegendStyle.BRACKET)
	    	float left = pd.getLinePos().getLeft().getInPixels(pixelsPerMillimeter);
	    	float top = pd.getLinePos().getTop().getInPixels(pixelsPerMillimeter);
	    	float cornerRadius = f.getCornerRadius().getInPixels(pixelsPerMillimeter);
	    	float height = pd.getLinePos().getHeight().getInPixels(pixelsPerMillimeter);
	    	
	  		g.draw(new Line2D.Float(left, top, left + cornerRadius, top));
	  		g.draw(new Line2D.Float(left + cornerRadius, top, left + cornerRadius, top + height));
	  		g.draw(new Line2D.Float(left, top + height, left + cornerRadius, top + height));
	    }
			g.setStroke(oldStroke);
			
			// Text ausgeben:
			AffineTransform oldTrans = g.getTransform();
			AffineTransform trans = (AffineTransform)oldTrans.clone(); 
			TextOrientation orient = f.getOrientation();
			float ascent = 
				  g.getFontMetrics(l.getFormats().getFont(pixelsPerMillimeter)).getAscent();
			float x;
			float y; 
			if (orient.equals(TextOrientation.UP)) {
				x = pd.getTextPos().getLeft().getInPixels(pixelsPerMillimeter);
				y = pd.getTextPos().getBottomInPixels(pixelsPerMillimeter);
				trans.quadrantRotate(3, x, y);
				g.setTransform(trans);
			}
			else if (orient.equals(TextOrientation.DOWN)) {
				x = pd.getTextPos().getRightInPixels(pixelsPerMillimeter);
				y = pd.getTextPos().getTop().getInPixels(pixelsPerMillimeter); 
				trans.quadrantRotate(1, x, y);
				g.setTransform(trans);
			}
			else {
				x = pd.getTextPos().getLeft().getInPixels(pixelsPerMillimeter);
				y = pd.getTextPos().getTop().getInPixels(pixelsPerMillimeter); 
			}
			paintText(l.getData().formatValue(f.getDecimalFormat()), f, x, y + ascent);
			g.setTransform(oldTrans);
			
			paintSelection(l);
		}
	}
	
	
	@Override
	public void paintTree(Graphics2D g, Document document,
			TreeSelection selection, float pixelsPerMm, boolean transparent) {
		
		paintTree(g, null, document, selection, pixelsPerMm, transparent);
	}


	public void paintTree(Graphics2D g, Rectangle visibleRect, Document document, TreeSelection selection, 
			float pixelsPerMillimeter, boolean transparent) {
		
		this.g = g;
		DistanceDimension d = document.getTree().getPaintDimension(type);
		if (visibleRect == null) {
			visibleRect = new Rectangle(d.getWidth().getRoundedInPixels(pixelsPerMillimeter), 
					d.getHeight().getRoundedInPixels(pixelsPerMillimeter));
		}
		this.visibleRect = visibleRect;
		this.document = document;
		this.selection = selection;
		this.pixelsPerMillimeter = pixelsPerMillimeter;
		
		if (!transparent) {
			g.setColor(document.getTree().getFormats().getBackgroundColor());
			g.fillRect(visibleRect.x, visibleRect.y, visibleRect.width, visibleRect.height);
		}
		
		if (!document.getTree().isEmpty()) {
			paintSubtree(document.getTree().getPaintStart());
		}
		if (document.getTree().getFormats().getShowScaleBar()) {
			paintScaleBar(document.getTree().getScaleBar());
		}
		
		Legends legends = document.getTree().getLegends();
		for (int i = 0; i < legends.size(); i++) {
			paintLegend(legends.get(i));
		}
	}
}