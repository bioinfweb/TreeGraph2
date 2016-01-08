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
package info.bioinfweb.treegraph.cmd;


import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.format.DistanceDimension;
import info.bioinfweb.treegraph.document.format.DistanceValue;
import info.bioinfweb.treegraph.document.io.ReadWriteFactory;
import info.bioinfweb.treegraph.document.io.ReadWriteFormat;
import info.bioinfweb.treegraph.document.io.ReadWriteParameterMap;
import info.bioinfweb.treegraph.graphics.export.GraphicFormat;
import info.bioinfweb.treegraph.graphics.export.GraphicWriter;
import info.bioinfweb.treegraph.graphics.export.GraphicWriterFactory;
import info.bioinfweb.treegraph.graphics.positionpaint.PositionPaintFactory;
import info.bioinfweb.treegraph.graphics.positionpaint.PositionPaintType;
import info.bioinfweb.treegraph.gui.dialogs.ResolutionInput;
import info.bioinfweb.treegraph.gui.treeframe.TreeViewPanel;
import info.bioinfweb.commons.CommandLineReader;
import info.bioinfweb.commons.collections.ParameterMap;

import java.io.File;



public class ImageGenerator {
	public static final String CLADOGRAM_OPTION = "-clad";
	public static final String PHYLOGRAM_OPTION = "-phyl";
	public static final String WIDTH_OPTION = "-width";
	public static final String HEIGHT_OPTION = "-height";
	public static final String RESOLUTION_OPTION = "-res";

	public static final String UNIT_PPM = "ppm";
	public static final String UNIT_PPI = "ppi";
	public static final String UNIT_MM = "mm";
	public static final String UNIT_PT = "pt";
	public static final String UNIT_PX = "px";

	
	/**
	 * Reads the resolution from the specified parameter and converts it to pixels per millimeter if
	 * necessary. 
	 * @param content - the string that contains value and unit
	 * @return
	 */
	private static float readRes(String content) {
		if (content.endsWith(UNIT_PPM)) {
			return Float.parseFloat(content.substring(0, content.length() - UNIT_PPM.length()));
		}
		else if (content.endsWith(UNIT_PPI)) {
			return ResolutionInput.ppiToPPM(Float.parseFloat(content.substring(0, content.length() - 
					UNIT_PPI.length())));
		}
		else {  // ppm is the default unit
			return Float.parseFloat(content);
		}
	}
	
	
	/**
	 * Reads a distance value (width or height) from the specified parameter in millimeters or pixels.
	 * Values in points are converted to millimeters. Additionally the
	 * {@link GraphicWriter#KEY_DIMENSIONS_IN_PIXELS} flag is added to the specified hints according to 
	 * the units specified by <code>content</code>.
	 * @param content - the string that contains value and unit
	 * @param pixelsPerMillimeter - the resolution of the image to be created
	 * @param hints
	 * @return
	 */
	private static float readDistance(String content, ParameterMap hints) {
		float value;
		if (content.endsWith(UNIT_PX)) {
			value = Float.parseFloat(content.substring(0, content.length() - UNIT_PX.length()));
		}
		else if (content.endsWith(UNIT_MM)) {
			value = Float.parseFloat(content.substring(0, content.length() - UNIT_MM.length()));
		}
		else if (content.endsWith(UNIT_PT)) {
			value = DistanceValue.pointsToMillimeters(Float.parseFloat(
					content.substring(0, content.length() - UNIT_PT.length())));
		}
		else {  // mm is the default unit
			value = Float.parseFloat(content);
		}
		hints.put(GraphicWriter.KEY_DIMENSIONS_IN_PIXELS, new Boolean(content.endsWith(UNIT_PX))); 
		return value;
	}
	
	
	public static boolean generate(CommandLineReader reader) {
  	ReadWriteFactory rwf = ReadWriteFactory.getInstance();
  	GraphicWriterFactory gwf = GraphicWriterFactory.getInstance();
  	
  	GraphicFormat format = gwf.formatByFileName(reader.getArg(2));
  	if (rwf.getFilter(ReadWriteFormat.XTG).validExtension(reader.getArg(1)) && (format != null)) {
  		try {
				ReadWriteParameterMap parameterMap = new ReadWriteParameterMap();
				parameterMap.putApplicationLogger(CmdLoadLogger.getInstance());
  			Document document = rwf.getReader(ReadWriteFormat.XTG).read(
  					new File(reader.getArg(1)), parameterMap);
  			
        PositionPaintType type = PositionPaintType.RECT_CLAD;
  			if (reader.contained(PHYLOGRAM_OPTION, 3) != -1) {
  				type = PositionPaintType.PHYLOGRAM; 
  			}
  			//TODO distancePerBranchLengthUnit lesen und ggf. berechnen 
  			PositionPaintFactory.getInstance().getPositioner(type).positionAll(document, 1f);
  			
  			ParameterMap hints = new ParameterMap();
  			float pixelsPerMillimeter = TreeViewPanel.PIXELS_PER_MM_100;
  			int pos = reader.contained(RESOLUTION_OPTION, 3);
  			if (pos != -1) {
  				pixelsPerMillimeter = readRes(reader.getArg(pos + 1));
  			}
				hints.put(GraphicWriter.KEY_PIXELS_PER_MILLIMETER, new Float(pixelsPerMillimeter));
  			
  			DistanceDimension d = document.getTree().getPaintDimension(type);
  			float aspectRatio = d.getWidth().getInMillimeters() / d.getHeight().getInMillimeters();
  			float width = d.getWidth().getInMillimeters();
  			float height = d.getHeight().getInMillimeters();
  			pos = reader.contained(WIDTH_OPTION, 3);
  			if (pos != -1) {
  				width = readDistance(reader.getArg(pos + 1), hints);
  				height = width / aspectRatio;
  			}
  			else {  // height nur lesen, wenn width nicht angegeben wurde
    			pos = reader.contained(HEIGHT_OPTION, 3);
    			if (pos != -1) {
    				height = readDistance(reader.getArg(pos + 1), hints);
    				width = height * aspectRatio;
    			}
    			else {
    				hints.put(GraphicWriter.KEY_DIMENSIONS_IN_PIXELS, new Boolean(false));  // default
    			}
  			}
				hints.put(GraphicWriter.KEY_WIDTH, width);
				hints.put(GraphicWriter.KEY_HEIGHT, height);
  			//TODO Transparenz angeben
  			
  			gwf.getWriter(format).write(document, PositionPaintFactory.getInstance().getPainter(type), 
  					hints, new File(reader.getArg(2)));
    		return true;
  		}
  		catch (NumberFormatException e) {
  			System.out.println("Invalid floating point parameter. (Either the number format or the " +
  					"unit is incorrect.)");
 			}
  		catch (OutOfMemoryError e) {
  			System.out.println("There is not enough memory avialable.\n" + 
						"You can try to run TreeGraph with the following command line options for the\n" + 
						" Java Virtual Machine from the TreeGraph installation directory:\n" + 
						"\"java -Xms32m -Xmx1024m -jar TreeGraph.jar [additional user defined parameters]\"");
  		}
  		catch (Exception e) {
  			System.out.println("An error occured.");
  		}
  	}
  	else {
  		System.out.println("Invalid parameters. (Possibly an invalid file extension was used.)");
  	}
  	System.out.println("Conversion could not complete.");
 		return false;
  }
}