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
package info.bioinfweb.treegraph.document.metadata;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import info.bioinfweb.jphyloio.formats.xtg.XTGConstants;



public class MetadataPath  {
	private boolean isNode;  //TODO Should this be boolean?
	private List<MetadataPathElement> path = new ArrayList<MetadataPathElement>();
	private boolean isLiteral;  //TODO Should this be boolean?


	public MetadataPath(boolean isNode, boolean isLiteral) {
		super();
		this.isNode = isNode;
		this.isLiteral = isLiteral;
	}


	public boolean isNode() {
		return isNode;
	}
	
	
	public List<MetadataPathElement> getElementList() {
		return path;
	}
	

	public boolean isLiteral() {
		return isLiteral;
	}
		
	
//	@Override
//	protected MetadataPath clone() {
//		try {
//			MetadataPath result = (MetadataPath) super.clone();
//			result.path = new ArrayList<MetadataPathElement>();
//			for (MetadataPathElement child : getElementList()) {
//				result.getElementList().add(child);				
//			}
//			return result;
//		}
//		catch (CloneNotSupportedException e) {
//			throw new InternalError(e);
//		}
//	}


	@Override
	public String toString() {
		StringBuilder returnValue = new StringBuilder();
		for (MetadataPathElement metadataPathElement : path) {
			returnValue.append(metadataPathElement.toString() + " ");			
		}
		return returnValue.toString();
	}
	
	
//	public static void main(String[] args) {
//		MetadataPathElement a = new MetadataPathElement(XTGConstants.ATTR_FONT_FAMILY, 0);
//		MetadataPathElement b = new MetadataPathElement(XTGConstants.ATTR_LEGEND_SPACING, 0);
//		MetadataPathElement c = new MetadataPathElement(XTGConstants.ATTR_SCALE_BAR_INCREASE, 0);
//		MetadataPathElement d = new MetadataPathElement(XTGConstants.ATTR_FONT_FAMILY, 1);		
//		MetadataPath path = new MetadataPath(true, true);
//		
//		path.getElementList().add(a);
//		path.getElementList().add(b);
//		path.getElementList().add(c);
//		path.getElementList().add(d);
//		
//		String pathWay = path.toString();
//		System.out.println(pathWay);		
//	}
}
