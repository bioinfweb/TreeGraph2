/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2011  Ben St�ver, Kai M�ller
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


import java.io.InputStream;
import java.util.HashMap;

import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.format.DistanceValue;
import info.bioinfweb.treegraph.document.format.CornerRadiusFormats;
import info.bioinfweb.treegraph.document.format.LineFormats;
import info.bioinfweb.treegraph.document.format.NodeFormats;
import info.bioinfweb.treegraph.document.format.LabelFormats;
import info.bioinfweb.treegraph.document.io.AbstractDocumentReader;
import info.bioinfweb.treegraph.document.io.TreeSelector;
import info.bioinfweb.treegraph.document.io.log.LoadLogger;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;



public class TGFReader extends AbstractDocumentReader {
	private static final int SECOUND_LINE_POS = 100;
	
	
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
	
	
	public Document read(InputStream stream, LoadLogger loadLogger, NodeBranchDataAdapter internalAdapter, 
			NodeBranchDataAdapter branchLengthsAdapter,	TreeSelector selector, 
			boolean translateInternalNodes) throws Exception {
		
		labelFormats = initLabelFormats();
		leafFormats = new NodeFormats();
		lineWidth.setInMillimeters(LineFormats.DEFAULT_LINE_WIDTH_IN_MM);
		cornerRadius.setInMillimeters(CornerRadiusFormats.STD_EDGE_RADIUS_IN_MM);
		
		return null;
	}
}