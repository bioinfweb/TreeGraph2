/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2011, 2013-2019  Ben Stöver, Sarah Wiechers, Kai Müller
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
package info.bioinfweb.treegraph.document.topologicalcalculation;


import info.bioinfweb.treegraph.document.Node;



/**
 * Bean class that stores information about the similarity of a subtree under a node to a subtree in another document.
 * 
 * @author Ben St&ouml;ver
 * @since 2.0.33
 */
public class NodeInfo {
  private Node node = null;
  private int additionalCount = -1;
  private boolean downwards = true;
  private Node alternativeNode = null;
  
  
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


	public boolean isDownwards() {
		return downwards;
	}


	public void setDownwards(boolean downwards) {
		this.downwards = downwards;
	}


	/**
	 * If an exact leaf set match is found ({@link #getAdditionalCount()} == 0), a second node might also match that leaf set in case of an 
	 * unrooted tree.
	 * <p>
	 * The direction of the second node would be {@code!}{@link #isDownwards()}.
	 * 
	 * @return the second matching node or {@code null}
	 */
	public Node getAlternativeNode() {
		return alternativeNode;
	}


	public void setAlternativeNode(Node alternativeNode) {
		this.alternativeNode = alternativeNode;
	}
}