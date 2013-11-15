/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2013  Ben Stöver, Kai Müller
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
package info.bioinfweb.treegraph.document.io.tgf;


import java.io.BufferedInputStream;
import java.util.HashMap;

import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.format.DistanceValue;
import info.bioinfweb.treegraph.document.format.CornerRadiusFormats;
import info.bioinfweb.treegraph.document.format.LineFormats;
import info.bioinfweb.treegraph.document.format.NodeFormats;
import info.bioinfweb.treegraph.document.format.LabelFormats;
import info.bioinfweb.treegraph.document.io.AbstractDocumentReader;
import info.bioinfweb.treegraph.document.io.DocumentIterator;



public class TGFReader extends AbstractDocumentReader {
	private static final int SECOUND_LINE_POS = 100;
	
	
	public TGFReader() {
	  super(false);
  }


	private HashMap<String, LabelFormats> labelFormats = null;
	private NodeFormats leafFormats = null;
	private DistanceValue lineWidth = new DistanceValue();
	private DistanceValue cornerRadius = new DistanceValue();
	

	private HashMap<String, LabelFormats> initLabelFormats() {
		HashMap<String, LabelFormats> result = new HashMap<String, LabelFormats>();
		
		for (int lineNo = 0; lineNo <= 1; lineNo++) {
			LabelFormats f = new LabelFormats(null);
			f.setLineNumber(lineNo);
			result.put("u" + (2 * lineNo + 1), f);
			
			f = new LabelFormats(null);
			f.setLinePosition(SECOUND_LINE_POS);
			result.put("u" + (2 * lineNo + 2), f);

			f = new LabelFormats(null);
			f.setAbove(false);
			f.setLineNumber(lineNo);
			result.put("d" + (2 * lineNo + 1), f);
			
			f = new LabelFormats(null);
			f.setAbove(false);
			f.setLinePosition(SECOUND_LINE_POS);
			result.put("d" + (2 * lineNo + 2), f);
		}
		
		return result;
	}
	
	
	@Override
  public Document readDocument(BufferedInputStream stream) throws Exception {
		labelFormats = initLabelFormats();
		leafFormats = new NodeFormats();
		lineWidth.setInMillimeters(LineFormats.DEFAULT_LINE_WIDTH_IN_MM);
		cornerRadius.setInMillimeters(CornerRadiusFormats.STD_EDGE_RADIUS_IN_MM);
		
		return null;
  }


	@Override
  public DocumentIterator createIterator(BufferedInputStream stream) throws Exception {
	  // TODO Auto-generated method stub
	  return null;
  }	
}