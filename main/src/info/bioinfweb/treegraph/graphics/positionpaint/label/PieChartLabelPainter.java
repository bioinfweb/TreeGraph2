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


import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;

import info.bioinfweb.treegraph.document.PieChartLabel;
import info.bioinfweb.treegraph.document.format.PieChartLabelFormats;
import info.bioinfweb.treegraph.document.position.PositionData;



public class PieChartLabelPainter extends AbstractGraphicalLabelPainter<PieChartLabel> {
	/**
	 * Tests of at least two of the specified angles are valid floating point values greater than zero.
	 * 
	 * @param angles - the an array of angles
	 * @return {@code true} if at least two valid angles were found
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
	
	
	@Override
	public void paint(Graphics2D g, float pixelsPerMillimeter, PositionData pd, PieChartLabel label) {
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
							Math.round(startAngle), Math.round(angles[j]), Arc2D.PIE);  // Rounding is necessary, as a workaround for rendering errors in SVG, if too small angles are written (see bug #104). (A higher precision of 0.1° already leads to deformed arcs.)
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

	
	@Override
	public Class<PieChartLabel> getLabelClass() {
		return PieChartLabel.class;
	}
}
