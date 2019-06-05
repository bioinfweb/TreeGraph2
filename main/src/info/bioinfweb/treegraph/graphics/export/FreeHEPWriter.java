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
package info.bioinfweb.treegraph.graphics.export;


import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.format.DistanceDimension;
import info.bioinfweb.treegraph.graphics.positionpaint.PositionPaintFactory;
import info.bioinfweb.treegraph.graphics.positionpaint.TreePainter;
import info.bioinfweb.treegraph.gui.treeframe.TreeViewPanel;
import info.bioinfweb.commons.Math2;
import info.bioinfweb.commons.collections.ParameterMap;

import java.awt.Dimension;
import java.awt.RenderingHints;
import java.io.IOException;
import java.io.OutputStream;
import org.freehep.graphicsio.AbstractVectorGraphicsIO;



public abstract class FreeHEPWriter extends AbstractGraphicWriter 
    implements GraphicWriter {
	
	
	protected abstract AbstractVectorGraphicsIO createGraphics(
			OutputStream stream, Dimension dim);
	
	
	public void write(Document document, TreePainter painter, ParameterMap hints, 
			OutputStream stream) {
		
	  //TODO Diesen Abschnitt irgendwie in einen weiteren Vorfahren auslagern. (Wird in SVGTranscodeWriter auch verwendet.)
		DistanceDimension paintDim = document.getTree().getPaintDimension(
				PositionPaintFactory.getInstance().getType(painter));
	  float pixelsPerMillimeter = 
	  	  hints.getFloat(KEY_PIXELS_PER_MILLIMETER, TreeViewPanel.PIXELS_PER_MM_100);
	  
  	float width = pixelsPerMillimeter *	paintDim.getWidth().getInMillimeters();
  	float height = pixelsPerMillimeter *	paintDim.getHeight().getInMillimeters();
	  if (hints.containsKey(KEY_WIDTH) && hints.containsKey(KEY_HEIGHT)) {
	  	width = hints.getFloat(KEY_WIDTH, width);
	  	height = hints.getFloat(KEY_HEIGHT, height);
	  }
	  
	  float paintResolution;
	  if (hints.getBoolean(KEY_DIMENSIONS_IN_PIXELS, false)) {
	  	paintResolution = width / paintDim.getWidth().getInMillimeters();
	  }
	  else {
	  	width *= pixelsPerMillimeter;
	  	height *= pixelsPerMillimeter;
	  	paintResolution = TreeViewPanel.PIXELS_PER_MM_100 * 
          (width / paintDim.getWidth().getInPixels(TreeViewPanel.PIXELS_PER_MM_100));
	  }

	  AbstractVectorGraphicsIO g = createGraphics(stream, 
				new Dimension(Math2.roundUp(width), Math2.roundUp(height)));
		
  	g.startExport();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
  	g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
  	g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		painter.paintTree(g, document, null, null, paintResolution, 
				hints.getBoolean(KEY_TRANSPARENT, false));
		g.endExport();
		
		try {
			g.closeStream();
		}
		catch (IOException e) {
			e.printStackTrace();  //TODO Fehler nicht hier abfangen, sonder wie bei document.io l�sen.
		}
	}
}