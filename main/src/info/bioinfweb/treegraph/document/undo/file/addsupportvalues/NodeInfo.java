/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2014  Ben St�ver, Kai M�ller
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
package info.bioinfweb.treegraph.document.undo.file.addsupportvalues;


import info.bioinfweb.treegraph.document.Node;



/**
 * @author Ben St&ouml;ver
 * @since 2.0.33
 */
public class NodeInfo {
  private Node node = null;
  private int additionalCount = -1;
  private boolean downwards = true;
  
  
	public NodeInfo(Node node, int additionalCount, boolean downwards) {
		super();
		this.node = node;
		this.additionalCount = additionalCount;
		this.downwards = downwards;
	}


	public int getAdditionalCount() {
		return additionalCount;
	}


	public void setAdditionalCount(int additionalCount) {
		this.additionalCount = additionalCount;
	}


	public Node getNode() {
		return node;
	}


	public void setNode(Node node) {
		this.node = node;
	}


	public boolean getDownwards() {
		return downwards;
	}


	public void setDownwards(boolean downwards) {
		this.downwards = downwards;
	}
}