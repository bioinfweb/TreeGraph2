/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2014  Ben Stöver, Kai Müller
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
package info.bioinfweb.treegraph.graphics.export.tiff;


import info.bioinfweb.treegraph.graphics.export.GraphicWriter;
import info.bioinfweb.treegraph.graphics.export.SVGTranscodeWriter;
import info.bioinfweb.commons.collections.ParameterMap;

import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.transcoder.image.TIFFTranscoder;



public class TIFFWriter extends SVGTranscodeWriter implements GraphicWriter {
	public static final String KEY_TIFF_COMPRESSION_METHOD = "tiffCompressionMethod";
	public static final String DEFAULT_COMPRESSION_METHOD = "none";

	
	public TIFFWriter() {
		super(TIFFTranscoder.class);
	}


	@Override
	protected void addTranscodingHints(Transcoder transcoder, ParameterMap writerHints) {
		super.addTranscodingHints(transcoder, writerHints);
		transcoder.addTranscodingHint(TIFFTranscoder.KEY_COMPRESSION_METHOD, 
				writerHints.getString(KEY_TIFF_COMPRESSION_METHOD, DEFAULT_COMPRESSION_METHOD));
	}
}