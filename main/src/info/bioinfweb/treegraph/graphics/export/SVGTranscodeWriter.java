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
package info.bioinfweb.treegraph.graphics.export;


import info.bioinfweb.treegraph.Main;
import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.format.DistanceDimension;
import info.bioinfweb.treegraph.graphics.positionpaint.PositionPaintFactory;
import info.bioinfweb.treegraph.graphics.positionpaint.TreePainter;
import info.bioinfweb.treegraph.gui.treeframe.TreeViewPanel;
import info.bioinfweb.commons.Math2;
import info.bioinfweb.commons.collections.ParameterMap;

import java.awt.Dimension;
import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGeneratorContext;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.transcoder.SVGAbstractTranscoder;
import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.svg2svg.SVGTranscoder;



/**
 * This class generates an image file from a tree document. Therefore it first generates an SVG file
 * and then transcodes it into the specified format (e.g. PNG or PDF).
 * 
 * @author Ben St&ouml;ver
 */
public class SVGTranscodeWriter extends AbstractGraphicWriter implements GraphicWriter {
  private Class<? extends Transcoder> transcoderClass = null;

  
	public SVGTranscodeWriter(Class<? extends Transcoder> transcoderClass) {
		super();
		this.transcoderClass = transcoderClass;
	}


	/**
	 * <p>This method can be overwritten to add more transcoding hints to the transcoder. This can e.g.
	 * be done by copying graphic writer hints to transcoding hints.</p>
	 * 
	 * <p>Note that <code>KEY_PIXEL_UNIT_TO_MILLIMETER</code>, <code>KEY_WIDTH</code>,
	 * <code>KEY_HEIGHT</code> and <code>KEY_BACKGROUND_COLOR</code> (the brackground color
	 * for formats which do not support transparency) have already been added.</p>
	 * 
	 * @param transcoder - the transcoder that transcodes from SVG to the respective image format
	 * @param writerHints - the hints that were specified when the <code>write</code>-method of this
	 *        writer was called
	 */
	protected void addTranscodingHints(Transcoder transcoder, ParameterMap writerHints) {}
	
	
	/**
	 * <p>Writes the given document to the given stream in the graphic format provided by the 
	 * transcoder-class specified in the constructor.</p>
	 * 
	 * <p>The resolution the image is exportet with is taken from the hint-value for 
	 * <code>KEY_PIXELS_PER_MILLIMETER</code>. Is this hint is not provided 
	 * <code>TreeViewPanel.PIXELS_PER_MM_100</code> is unsed instead.</p>
	 * 
	 * <p>The width and the height of the resulting image are taken from the hint-values for 
	 * <code>KEY_WIDTH</code> and <code>KEY_HEIGHT</code> if both of them are specified. If this is not
	 * the case, width and height are calculated from the resolution (see above) and the size of the 
	 * document. The width and height can be either defines in millimeters or in pixels. The unit can 
	 * be specified with the key <code>KEY_DIMENSIONS_IN_PIXELS</code>. (<code>false</code> is used by 
	 * default.)</p>
	 * 
	 * <p>The hint specified by <code>KEY_TRANSPARENT</code> is used to define the transparency of the
	 * image. (<code>false</code> if the default value if this hint is not given.)</p> 
	 * 
	 * @param document - the document to be exported 
	 * @param painter - the painter to display the document
	 * @param hints - the graphic writer hints to specify certain writing options
	 * @param stream - the output stream to write the graphic to
	 * 
	 * @see info.bioinfweb.treegraph.graphics.export.GraphicWriter#write(info.bioinfweb.treegraph.document.Document, info.bioinfweb.treegraph.graphics.positionpaint.TreePainter, ParameterMap, java.io.OutputStream)
	 */
	@Override
	public void write(Document document, TreePainter painter, ParameterMap hints, 
			OutputStream stream) throws Exception {
		
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
	  
	  SVGGeneratorContext context = SVGGeneratorContext.createDefault(
	  		GenericDOMImplementation.getDOMImplementation().createDocument("http://www.w3.org/2000/svg", "svg", null));
	  
	  context.setComment(" Generated by TreeGraph " + Main.getInstance().getVersion().toString() + 
	  		" with Apache Batik SVG Generator <" + Main.TG_URL + ">");
	  SVGGraphics2D svgGenerator = new SVGGraphics2D(context, hints.getBoolean(KEY_TEXT_AS_SHAPES, false));
	  svgGenerator.setSVGCanvasSize(new Dimension(Math2.roundUp(width), Math2.roundUp(height)));
	  
	  painter.paintTree(svgGenerator, document, null, paintResolution, 
	  		hints.getBoolean(KEY_TRANSPARENT, false));
	  StringWriter stringWriter = new StringWriter();
  	svgGenerator.stream(stringWriter);
  	
  	Transcoder t = transcoderClass.newInstance();
  	
	  t.addTranscodingHint(SVGAbstractTranscoder.KEY_PIXEL_UNIT_TO_MILLIMETER, 
	  		new Float(1f / pixelsPerMillimeter));
	  t.addTranscodingHint(SVGAbstractTranscoder.KEY_WIDTH, width);
	  t.addTranscodingHint(SVGAbstractTranscoder.KEY_HEIGHT, height);
	  addTranscodingHints(t, hints);
	
	  TranscoderInput input = new TranscoderInput(new StringReader(stringWriter.getBuffer().toString()));
	  TranscoderOutput output;
	  if (t instanceof SVGTranscoder) {  // Workaround notwendig, weil die verschiedenen Transcoder scheinbar nicht mit der jeweils anderen Form arbeiten.
	  	output = new TranscoderOutput(new OutputStreamWriter(stream, "UTF-8"));
	  }
	  else {
	  	output = new TranscoderOutput(new BufferedOutputStream(stream));
	  }
	  
  	t.transcode(input, output);
  	if (output.getOutputStream() != null) {
  		output.getOutputStream().close();
  	}
  	//stream.flush();
  	stream.close();
	}
}