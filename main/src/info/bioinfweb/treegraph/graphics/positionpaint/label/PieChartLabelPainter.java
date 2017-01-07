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
package info.bioinfweb.treegraph.graphics.positionpaint.label;


import info.bioinfweb.commons.graphics.FontCalculator;
import info.bioinfweb.treegraph.document.PieChartLabel;
import info.bioinfweb.treegraph.document.format.DistanceDimension;
import info.bioinfweb.treegraph.document.format.PieChartLabelCaptionContentType;
import info.bioinfweb.treegraph.document.format.PieChartLabelCaptionLinkType;
import info.bioinfweb.treegraph.document.format.PieChartLabelFormats;
import info.bioinfweb.treegraph.document.format.TextFormats;
import info.bioinfweb.treegraph.graphics.positionpaint.PositionPaintUtils;
import info.bioinfweb.treegraph.graphics.positionpaint.positiondata.PieChartLabelPositionData;
import info.bioinfweb.treegraph.graphics.positionpaint.positiondata.PositionData;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;



public class PieChartLabelPainter extends AbstractGraphicalLabelPainter<PieChartLabel, PieChartLabelPositionData> {
	/** The fraction of the pie chart radius measured from outside from where the caption link lines shall start. */
	public static final float LINE_START_DISTANCE_FACTOR = 0.9f;

	/** The fraction of pie chart radius forming the minimal distance between the pie chart center and the link line start points. */
	public static final float MINIMAL_CENTER_DISTANCE_FACTOR = 0.2f;
	
	/** The line spacing between captions. */
	public static final float RELATIVE_CAPTION_LINE_DISTANCE = 0.3f;
	
	/** The fraction of the caption text height that shall be used as the horizontal distance between captions links and charts. */
	public static final float CAPTION_DISTANCE_FACTOR = 0.3f;
	

	private static double editAngle(double angle) {  //TODO Shift could be customized by the label formats in the future. See also #144.
		angle = angle + 90.0;
		if (angle < 360.0) {
			angle -= 360.0;
		}
		return angle;
	}
	
	
	private List<Point2D.Float> calculateStartPoints(PieChartLabel label,	PieChartLabelPositionData positionData) {
		 List<Point2D.Float> result = new ArrayList<Point2D.Float>(label.getSectionDataList().size()); 
		
		// Calculate center and radius:
		PieChartLabelFormats f = label.getFormats();
		float centerX = 0.5f * f.getWidth().getInMillimeters(); 
		float centerY = 0.5f * f.getHeight().getInMillimeters();
		float halfLineWidth = 0.5f * f.getLineWidth().getInMillimeters();
		float rX = Math.max(MINIMAL_CENTER_DISTANCE_FACTOR * centerX, LINE_START_DISTANCE_FACTOR * centerX - halfLineWidth); 
		float rY = Math.max(MINIMAL_CENTER_DISTANCE_FACTOR * centerY, LINE_START_DISTANCE_FACTOR * centerY - halfLineWidth);
		
		// Calculate points:
		double[] angles = label.getPieChartAngles();
		double startAngle = 0.0;
		for (int i = 0; i < angles.length; i++) {
			double angle = Math.toRadians(editAngle(startAngle + 0.5 * angles[i]));  // Angles in Graphics2D are in degrees.
			startAngle += angles[i];
			result.add(new Point2D.Float((float)(centerX + rX * Math.cos(angle)), (float)(centerY - rY * Math.sin(angle))));  // y coordinates are screen coordinates
    }
		return result;
	}
	
	
	private void createOrderedCaptionPositions(PieChartLabelPositionData positionData, List<Point2D.Float> startPoints) {
		// Create position data list:
		for (int i = 0; i < startPoints.size(); i++) {
			PieChartLabelPositionData.CaptionPositionData data = new PieChartLabelPositionData.CaptionPositionData(i);
			Point2D.Float point = startPoints.get(i);
			data.getLineStartX().setInMillimeters(point.x);
			data.getLineStartY().setInMillimeters(point.y);
			positionData.getCaptionPositions().add(data);
    }
		
		// Sort position data list by y-coordinates:
		Collections.sort(positionData.getCaptionPositions(), new Comparator<PieChartLabelPositionData.CaptionPositionData>() {
			@Override
			public int compare(PieChartLabelPositionData.CaptionPositionData o1, PieChartLabelPositionData.CaptionPositionData o2) {
				return Math.round(Math.signum(o1.getLineStartY().getInMillimeters() - o2.getLineStartY().getInMillimeters()));
			}
		});
		
		// Swap captions with close y-coordinates depending on their column:
		float previousDistance = Float.POSITIVE_INFINITY;
		for (int i = 0; i < positionData.getCaptionPositions().size() - 1; i++) {
			PieChartLabelPositionData.CaptionPositionData current = positionData.getCaptionPositions().get(i);
			PieChartLabelPositionData.CaptionPositionData next = positionData.getCaptionPositions().get(i + 1);
			boolean left = i % 2 == 0;
			float currentDistanceY = next.getLineStartY().getInMillimeters() - current.getLineStartY().getInMillimeters();
			float nextDistanceY = Float.POSITIVE_INFINITY;
			if (i + 2 < positionData.getCaptionPositions().size()) {
				nextDistanceY = positionData.getCaptionPositions().get(i + 2).getLineStartY().getInMillimeters() - 
						next.getLineStartY().getInMillimeters();
			}
			if ((currentDistanceY <= nextDistanceY) && (currentDistanceY <= previousDistance) &&
					((left && current.getLineStartX().getInMillimeters() > next.getLineStartX().getInMillimeters()) ||
							!left && current.getLineStartX().getInMillimeters() < next.getLineStartX().getInMillimeters())) {
				
				positionData.getCaptionPositions().set(i, next);
				positionData.getCaptionPositions().set(i + 1, current);
				i++;  // Avoid swapping the same value more than once.
			}
			previousDistance = currentDistanceY;
		}
	}
	
	
	@Override
	protected void doCalculatePositionData(PieChartLabel label,	PieChartLabelPositionData positionData) {
		PieChartLabelFormats f = label.getFormats();
		positionData.getCaptionPositions().clear();
		
		// Position heading:
		PositionData titlePosition = positionData.getTitlePosition();
		titlePosition.getTop().setInMillimeters(0f);  // Left is set below.
		if (f.isShowTitle()) {  // Empty stings will also have a height.
			DistanceDimension dim = PositionPaintUtils.calculateTextDimension(label);
			titlePosition.getWidth().assign(dim.getWidth());;
			titlePosition.getHeight().assign(dim.getHeight());;
		}
		else {
			titlePosition.getWidth().setInMillimeters(0f);
			titlePosition.getHeight().setInMillimeters(0f);
		}
		
		// Chart dimensions are defined by the formats:
		positionData.getChartPosition().getWidth().assign(f.getWidth());
		positionData.getChartPosition().getHeight().assign(f.getHeight());
		
		if (PieChartLabelCaptionContentType.NONE.equals(f.getCaptionsContentType()) || label.getSectionDataList().isEmpty()) {
			positionData.getChartPosition().getTop().setInMillimeters(0f);
			positionData.getChartPosition().getLeft().setInMillimeters(0f);
			positionData.assignPositionData(positionData.getChartPosition());
		}
		else {
			int captionColumnCount = label.getSectionDataList().size() > 1 ? 2 : 1;
			
			// Calculate caption font height:
			TextFormats captionFormats = f.getCaptionsTextFormats();
			int captionsPerSide = label.getSectionDataList().size() / 2 + label.getSectionDataList().size() % 2;
			float captionHeight = f.getHeight().getInMillimeters() / 
							(captionsPerSide + (captionsPerSide - 1) * RELATIVE_CAPTION_LINE_DISTANCE);
			f.getCaptionsTextFormats().getTextHeight().setInMillimeters(captionHeight);  // Format can be set here, since it cannot be edited from the GUI and is not written to XTG.
			float captionLineHeight = (1 + RELATIVE_CAPTION_LINE_DISTANCE) * captionHeight;
			float captionDistance = CAPTION_DISTANCE_FACTOR * captionHeight;

			// Calculate caption order:
			float captionChartSpace;  // The space used for color boxes or caption lines.
			if (f.getCaptionsLinkType().equals(PieChartLabelCaptionLinkType.COLORED_BOXES)) {
				captionChartSpace = captionHeight + captionDistance;
			}
			else {
				captionChartSpace = captionDistance;
			}
			createOrderedCaptionPositions(positionData, calculateStartPoints(label, positionData));  // For colored boxes points are also needed to calculate the best fitting order.
			
			// Determine column widths:
			float leftColumnWidth = 0f;
			float rightColumnWidth = 0f;  // Remains 0 if only one columns is present.
			for (int i = 0; i < positionData.getCaptionPositions().size(); i++) {
				PieChartLabelPositionData.CaptionPositionData data = positionData.getCaptionPositions().get(i);
				float width = FontCalculator.getInstance().getTextWidthToTextHeigth(captionFormats.getFontName(), 
								captionFormats.getTextStyle(), label.getCaptionText(data.getCaptionIndex()), captionHeight);
				if (i % 2 == 0) {  // left
					leftColumnWidth = Math.max(leftColumnWidth, width);
				}
				else {  // right
					rightColumnWidth = Math.max(rightColumnWidth, width);
				}
				data.getWidth().setInMillimeters(width);
			}
			float leftColumnX = 0f;
			float rightColumnX = leftColumnWidth + f.getWidth().getInMillimeters() + captionColumnCount * (captionChartSpace + captionDistance);
			float chartAndCaptionsWidth = rightColumnX + rightColumnWidth;
			float chartAndCaptionsY = (1 + RELATIVE_CAPTION_LINE_DISTANCE) * positionData.getTitlePosition().getHeight().getInMillimeters();
			
			// Adjust values depending on title width:
			float halfWidthDifference = 0.5f * (titlePosition.getWidth().getInMillimeters() - chartAndCaptionsWidth);
			if (halfWidthDifference > 0) {  // title longer than chart and captions
				leftColumnX = halfWidthDifference;
				rightColumnX += halfWidthDifference;
				titlePosition.getLeft().setInMillimeters(0f);
			}
			else {
				titlePosition.getLeft().setInMillimeters(-halfWidthDifference);
			}
			
			// Set chart position and overall label dimensions:
			positionData.getChartPosition().getLeft().setInMillimeters(leftColumnX + leftColumnWidth + captionChartSpace + captionDistance); 
			positionData.getChartPosition().getTop().setInMillimeters(chartAndCaptionsY);
			positionData.getWidth().setInMillimeters(
					Math.max(chartAndCaptionsWidth, titlePosition.getWidth().getInMillimeters()));
			positionData.getHeight().setInMillimeters(chartAndCaptionsY + f.getHeight().getInMillimeters());
			
			// Set remaining position data:
			float yLeft = chartAndCaptionsY;
			float yRight = chartAndCaptionsY;
			if (label.getSectionDataList().size() % 2 != 0) {
				yRight += 0.5f * captionLineHeight;
			}
			for (int i = 0; i < positionData.getCaptionPositions().size(); i++) {
				PieChartLabelPositionData.CaptionPositionData data = positionData.getCaptionPositions().get(i);
				if (i % 2 == 0) {  // left
					data.getLeft().setInMillimeters(leftColumnX + leftColumnWidth - data.getWidth().getInMillimeters());  // right bound
					data.getTop().setInMillimeters(yLeft);
					yLeft += captionLineHeight;
				}
				else {  // right
					data.getLeft().setInMillimeters(rightColumnX);
					data.getTop().setInMillimeters(yRight);
					yRight += captionLineHeight;
				}
				data.getHeight().setInMillimeters(captionHeight);
      }
		}
	}


	/**
	 * Tests of at least two of the specified angles are valid floating point values greater than zero.
	 * 
	 * @param angles the an array of angles
	 * @return {@code true} if at least two valid angles were found
	 */
	private boolean twoSectorsNotZero(double[] angles) {
		int count = 0;
		for (int i = 0; i < angles.length; i++) {
			if (!Double.isNaN(angles[i]) && (angles[i] > 0)) {
				count++;
				if (count == 2) {
					return true;
				}
			}
		}
		return (count >= 2);
	}
	
	
	private void paintPieChart(Graphics2D g, float pixelsPerMillimeter, float x, float y, float width, float height, PieChartLabel label) {
		PieChartLabel l = (PieChartLabel)label;
		double[] angles = l.getPieChartAngles();
		double startAngle = 0;
		boolean twoValid = twoSectorsNotZero(angles);
		PieChartLabelFormats f = l.getFormats();
		
    // Draw arc areas:
    Arc2D.Double[] arcs = new Arc2D.Double[angles.length];
		for (int j = 0; j < angles.length; j++) {
			if (f.isShowLinesForZero() || (!Double.isNaN(angles[j]) && (angles[j] > 0))) {
				arcs[j] = new Arc2D.Double(x, y, width, height, 
						Math.round(editAngle(startAngle)), Math.round(angles[j]), Arc2D.PIE);  // Rounding is necessary, as a workaround for rendering errors in SVG, if too small angles are written (see bug #104). (A higher precision of 0.1° already leads to deformed arcs.)
				g.setColor(f.getPieColor(j));
				g.fill(arcs[j]);
				startAngle += angles[j];
			}
		}
		
		// Draw arc borders:
		g.setColor(f.getLineColor());
		if (f.isShowInternalLines() && (twoValid || f.isShowLinesForZero())) {
			startAngle = 0.0;
			for (int j = 0; j < arcs.length; j++) {  // Borders need to be drawn after all areas, because borders of null wide parts would be hidden in SVG otherwise.
				if (f.isShowLinesForZero() || (!Double.isNaN(angles[j]) && (angles[j] > 0))) {
					g.draw(arcs[j]);
					startAngle += angles[j];
				}
			}
		}
		g.draw(new Ellipse2D.Float(x, y, width, height));  // In some cases the whole border may already have been painted, but not always.
	}
	
	
	private void paintCaptions(Graphics2D g, float pixelsPerMillimeter, PieChartLabelPositionData positionData, PieChartLabel label) {
		if (!positionData.getCaptionPositions().isEmpty()) {  //TODO Check if two are valid instead?
			PieChartLabelFormats f = label.getFormats();

			float edgeLength = positionData.getCaptionPositions().get(0).getHeight().getInPixels(pixelsPerMillimeter);
			float captionDistance = CAPTION_DISTANCE_FACTOR * edgeLength;
			float halfCaptionDistance = 0.5f * captionDistance;
			float xLeft = positionData.getLeft().getInPixels(pixelsPerMillimeter) + 
					positionData.getChartPosition().getLeft().getInPixels(pixelsPerMillimeter) - edgeLength - captionDistance;
			float xRight = positionData.getLeft().getInPixels(pixelsPerMillimeter) + 
					positionData.getChartPosition().getRightInPixels(pixelsPerMillimeter) + captionDistance;
			float halfLineWidth = 0.5f * f.getLineWidth().getInPixels(pixelsPerMillimeter);
			edgeLength -= 2 * halfLineWidth;
			float lineOffsetX = positionData.getLeft().getInPixels(pixelsPerMillimeter) + 
					positionData.getChartPosition().getLeft().getInPixels(pixelsPerMillimeter);
			float lineOffsetY = positionData.getTop().getInPixels(pixelsPerMillimeter) + 
					positionData.getChartPosition().getTop().getInPixels(pixelsPerMillimeter);
			float leftLineEndX = lineOffsetX - captionDistance;
			float rightLineEndX = positionData.getLeft().getInPixels(pixelsPerMillimeter) + 
					positionData.getChartPosition().getRightInPixels(pixelsPerMillimeter) + captionDistance;;
			
			if (!f.getCaptionsContentType().equals(PieChartLabelCaptionContentType.NONE)) {
				for (int i = 0; i < positionData.getCaptionPositions().size(); i++) {
					boolean left = i % 2 == 0;
					PieChartLabelPositionData.CaptionPositionData captionPosition = positionData.getCaptionPositions().get(i);
					PositionPaintUtils.paintText(g, pixelsPerMillimeter, label.getCaptionText(captionPosition.getCaptionIndex()), 
							f.getCaptionsTextFormats(), 
							positionData.getLeft().getInPixels(pixelsPerMillimeter) + captionPosition.getLeft().getInPixels(pixelsPerMillimeter), 
							positionData.getTop().getInPixels(pixelsPerMillimeter) + captionPosition.getTop().getInPixels(pixelsPerMillimeter) + 
							g.getFontMetrics(f.getCaptionsTextFormats().getFont(pixelsPerMillimeter)).getAscent());
					switch (f.getCaptionsLinkType()) {
						case COLORED_BOXES:
							Rectangle2D.Float r = new Rectangle2D.Float((left ? xLeft : xRight) + halfLineWidth, 
									positionData.getTop().getInPixels(pixelsPerMillimeter) + captionPosition.getTop().getInPixels(pixelsPerMillimeter) + halfLineWidth, 
									edgeLength, edgeLength);
							g.setColor(f.getPieColor(captionPosition.getCaptionIndex()));
							g.fill(r);
							g.setColor(f.getLineColor());
							g.draw(r);
							break;
						case STRAIGHT_LINES:
						case HORIZONTAL_LINES:
							g.setColor(f.getLineColor());
							float startY = lineOffsetY + captionPosition.getLineStartY().getInPixels(pixelsPerMillimeter);
							Path2D.Float path = new Path2D.Float();
							path.moveTo(lineOffsetX + captionPosition.getLineStartX().getInPixels(pixelsPerMillimeter), startY);
							if (f.getCaptionsLinkType().equals(PieChartLabelCaptionLinkType.HORIZONTAL_LINES)) {
								path.lineTo(left ? leftLineEndX + halfCaptionDistance : rightLineEndX - halfCaptionDistance, startY);
							}
							path.lineTo(left ? leftLineEndX : rightLineEndX, positionData.getTop().getInPixels(pixelsPerMillimeter) + 
									captionPosition.getCenterYInPixels(pixelsPerMillimeter));
							g.draw(path);
							break;
					}
				}
			}
		}
	}
	
	
	@Override
	protected void doPaint(Graphics2D g, float pixelsPerMillimeter, PieChartLabelPositionData positionData, PieChartLabel label) {
		Stroke stroke = g.getStroke();
		try {
	    g.setStroke(new BasicStroke(label.getFormats().getLineWidth().getInPixels(pixelsPerMillimeter)));
	    
	    //TODO Consider label margin
	    
	    // Paint title:
	    if (label.getFormats().isShowTitle()){
	    	PositionData titlePosition = positionData.getTitlePosition();
		    PositionPaintUtils.paintText(g, pixelsPerMillimeter, label.getData().formatValue(label.getFormats().getDecimalFormat()), 
		    		label.getFormats(), 
		    		positionData.getLeft().getInPixels(pixelsPerMillimeter) + titlePosition.getLeft().getInPixels(pixelsPerMillimeter), 
		    		positionData.getTop().getInPixels(pixelsPerMillimeter) + titlePosition.getTop().getInPixels(pixelsPerMillimeter) + 
		    		g.getFontMetrics(label.getFormats().getFont(pixelsPerMillimeter)).getAscent());
	    }
	    
	    // Paint chart:
			float lineWidth = label.getFormats().getLineWidth().getInPixels(pixelsPerMillimeter);
			float halfLineWidth = 0.5f * lineWidth;
			paintPieChart(g, pixelsPerMillimeter, 
					positionData.getLeft().getInPixels(pixelsPerMillimeter) + 
							positionData.getChartPosition().getLeft().getInPixels(pixelsPerMillimeter) + halfLineWidth, 
					positionData.getTop().getInPixels(pixelsPerMillimeter) + 
							positionData.getChartPosition().getTop().getInPixels(pixelsPerMillimeter) + halfLineWidth, 
					positionData.getChartPosition().getWidth().getInPixels(pixelsPerMillimeter) - lineWidth, 
					positionData.getChartPosition().getHeight().getInPixels(pixelsPerMillimeter) - lineWidth, 
					label);
			
			// Paint captions:
			paintCaptions(g, pixelsPerMillimeter, positionData, label);
		}
		finally {
			g.setStroke(stroke);
		}
	}

	
	@Override
	public Class<PieChartLabel> getLabelClass() {
		return PieChartLabel.class;
	}


	@Override
	public Class<PieChartLabelPositionData> getPositionDataClass() {
		return PieChartLabelPositionData.class;
	}
}
