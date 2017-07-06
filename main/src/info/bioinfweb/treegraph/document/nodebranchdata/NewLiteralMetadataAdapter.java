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


import info.bioinfweb.treegraph.document.metadata.MetadataPath;



public class NewLiteralMetadataAdapter extends LiteralMetadataAdapter implements NodeBranchDataAdapter {
	public NewLiteralMetadataAdapter() {
		super(null); //TODO Can parameter be null?
	}
	

	public NewLiteralMetadataAdapter(MetadataPath path) {
		super(path);
	}	
	
	
	public void setPath(MetadataPath path) {
		this.path = path;
	}
	
	
	@Override
	public boolean isNewColumn() {
		return true;
	}


	@Override
	public String toString() {
		return "New literal metadata with the specified path";
	}
	
	
	//TODO This method is an override in other NewAdapters. Whose method does it override?
	public NodeBranchDataAdapter getPermanentAdapter() {
		return new LiteralMetadataAdapter(getPath());
	}
}
