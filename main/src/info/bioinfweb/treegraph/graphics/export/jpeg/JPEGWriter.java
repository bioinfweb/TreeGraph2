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
package info.bioinfweb.treegraph.graphics.export.jpeg;


import info.bioinfweb.treegraph.graphics.export.GraphicWriter;
import info.bioinfweb.treegraph.graphics.export.SVGTranscodeWriter;
import info.bioinfweb.commons.collections.ParameterMap;

import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.transcoder.image.JPEGTranscoder;



public class JPEGWriter extends SVGTranscodeWriter implements GraphicWriter {
	public static final float DEFAULT_QUALITY = 0.8f;
	public static final float MIN_QUALITY = 0.01f;
	public static final float MAX_QUALITY = 1f;
	public static final String KEY_JPEG_QUALITY = "jpegCompression";
	
	
	public JPEGWriter() {
		super(JPEGTranscoder.class);
	}


	@Override
	protected void addTranscodingHints(Transcoder transcoder, ParameterMap writerHints) {
		super.addTranscodingHints(transcoder, writerHints);
		transcoder.addTranscodingHint(JPEGTranscoder.KEY_QUALITY, 
				new Float(Math.max(MIN_QUALITY, Math.min(MAX_QUALITY, 
						writerHints.getFloat(KEY_JPEG_QUALITY, DEFAULT_QUALITY)))));
	}
}