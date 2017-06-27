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



public abstract class MetadataNode implements Cloneable {
	private MetadataNode parent = null;
	
		
	public MetadataNode() {
		super();
	}


	public MetadataNode getParent() {
		return parent;
	}
	
	
	public void setParent(MetadataNode parent) {
		this.parent = parent;
	}


	@Override
	public MetadataNode clone() {
		try {
			MetadataNode result = (MetadataNode) super.clone();
			result.setParent(null);
			return result;
		}
		catch (CloneNotSupportedException e) {
			throw new InternalError(e);
		}
	}	
	
	
	//TODO What type of parameter for MetadataTree()?
//	public MetadataNode cloneWithSubtree() {
//		MetadataTree subtree = new MetadataTree();
//		MetadataNode result = clone();
//		for (int i = 0; i < subtree.getTreeChildren().size(); i++) {
//			MetadataNode child = subtree.getTreeChildren().get(i).cloneWithSubtree();
//			child.setParent(result);
//			subtree.getTreeChildren().add(child);
//		}
//		return result;
//	}
}
