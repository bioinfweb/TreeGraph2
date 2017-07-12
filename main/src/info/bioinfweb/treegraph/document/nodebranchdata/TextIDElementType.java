/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2011, 2013-2017  Ben Stöver, Sarah Wiechers, Kai Müller
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
package info.bioinfweb.treegraph.document.nodebranchdata;


import java.text.DecimalFormat;

import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.TextElementData;
import info.bioinfweb.treegraph.document.metadata.MetadataPath;



/** 
 * Enumerates all types of annotations that attach an instance of {@link TextElementData} to a {@link Node}. 
 * 
 *  @author Ben St&ouml;ver
 */
public enum TextIDElementType {
	TEXT_LABEL,
	HIDDEN_NODE_DATA,
	HIDDEN_BRANCH_DATA; 
//	LITERAL_METADATA,
//	RESOURCE_METADATA;
	
	
	public TextIDElementDataAdapter createAdapterInstance(String id, DecimalFormat decimalFormat) {
		switch (this) {
			case TEXT_LABEL:
				return new TextLabelAdapter(id, decimalFormat);
//			case LITERAL_METADATA:
//				return new LiteralMetadataAdapter(path);
//			case RESOURCE_METADATA:
//				return new ResourceMetadataAdapter(path);
			default:
				throw new InternalError("Unsupported target type " + this + " encountered. "
						+ "Please inform the TreeGraph developers on this bug.");
		}
	}
}