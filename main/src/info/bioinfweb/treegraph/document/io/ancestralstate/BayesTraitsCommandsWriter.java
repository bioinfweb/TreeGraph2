/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2011, 2013-2016  Ben Stöver, Sarah Wiechers, Kai Müller
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
package info.bioinfweb.treegraph.document.io.ancestralstate;


import info.bioinfweb.commons.SystemUtils;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.NodeType;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.tools.TreeSerializer;
import info.bioinfweb.treegraph.gui.mainframe.MainFrame;

import java.io.IOException;
import java.io.Writer;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Set;



public class BayesTraitsCommandsWriter {
	public static final String INTERNAL_NODE_NAME_PREFIX = "internalNode";
	public static final DecimalFormat DECIMAL_INTEGER_FORMAT = new DecimalFormat("#.########");
	
	
	public void write(Writer writer, String commandName, Node[] nodes, NodeBranchDataAdapter leavesAdapter, NodeBranchDataAdapter internalNodesAdapter) throws IOException {
		int nodeCount = 0;
		Set<String> internalNodeNameSet = new HashSet<String>();
		try {
			for (Node node : nodes) {
				if (!node.isLeaf() && !(node.equals(MainFrame.getInstance().getActiveTreeFrame().getTreeViewPanel().getDocument().getTree().getPaintStart()))) {
					String internalNodeName = internalNodesAdapter.getText(node);
					if (internalNodesAdapter.isDecimal(node)) {
						internalNodeName = INTERNAL_NODE_NAME_PREFIX + DECIMAL_INTEGER_FORMAT.format(internalNodesAdapter.getDecimal(node));
					}
					if ((internalNodeName != null) && !internalNodeName.equals("")) {
						if (internalNodeNameSet.add(internalNodeName)) {
							writeCommand(writer, commandName, node, internalNodeName, leavesAdapter, internalNodesAdapter);
						}
						else {
							int internalCount = 2;
							while (!internalNodeNameSet.add(internalNodeName + "_" + internalCount)) {
								internalCount++;
							}
							writeCommand(writer, commandName, node, internalNodeName + "_" + internalCount, leavesAdapter, internalNodesAdapter);
						}
					}
					else {
						nodeCount++;
						while (!internalNodeNameSet.add(INTERNAL_NODE_NAME_PREFIX + nodeCount)) {
							nodeCount++;
						}
						writeCommand(writer, commandName, node, INTERNAL_NODE_NAME_PREFIX + nodeCount, leavesAdapter, internalNodesAdapter);
					}
				}
			}
		}
		finally {
			writer.close();
		}
	}
	
	
	private void writeCommand(Writer writer, String commandType, Node node, String internalNodeName, NodeBranchDataAdapter leavesAdapter, 
			NodeBranchDataAdapter internalNodesAdapter) throws IOException {
		
		writer.write(commandType + internalNodeName);
		Node[] terminalNodes = TreeSerializer.getElementsInSubtree(node, NodeType.LEAVES, Node.class);
		for (Node terminal : terminalNodes) {
			String terminalName = leavesAdapter.getText(terminal);
			if (terminalName.contains(" ")) {
				terminalName = terminalName.replace(" ", "_");
			}
			writer.write(" " + terminalName);
		}
		writer.write(SystemUtils.LINE_SEPARATOR);
	}
}
