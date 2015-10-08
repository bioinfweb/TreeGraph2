/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2015  Ben Stöver, Sarah Wiechers, Kai Müller
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
package info.bioinfweb.treegraph.document.io.nexus;


import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import info.bioinfweb.commons.SystemUtils;
import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.NodeType;
import info.bioinfweb.treegraph.document.TreeSerializer;
import info.bioinfweb.treegraph.document.io.AbstractDocumentWriter;
import info.bioinfweb.treegraph.document.io.ReadWriteParameterMap;
import info.bioinfweb.treegraph.document.io.newick.NewickStringWriter;



public class NexusWriter extends AbstractDocumentWriter {
	private String indention = "";

	
	public void write(Document document, OutputStream stream, ReadWriteParameterMap properties) throws Exception {
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stream));
		try {
			writer.write(NexusParser.FIRST_LINE.toUpperCase() + SystemUtils.LINE_SEPARATOR);
			
			Node[] leavesInCurrentTree = TreeSerializer.getElementsInSubtree(document.getTree().getPaintStart(), NodeType.LEAVES, Node.class);
			writeCommand(writer, NexusParser.BEGIN_COMMAND + " " + NexusParser.BLOCK_NAME_TAXA);
			increaseIndention();
			writeCommand(writer, NexusParser.DIMENSIONS_NAME + " " + NexusParser.DIMENSIONS_SUBCOMMAND_NTAX + NexusParser.KEY_VALUE_SEPERATOR + leavesInCurrentTree.length);
			writeLine(writer, NexusParser.TAXLABELS_NAME + " ");
			increaseIndention();
			for (int i = 0; i < leavesInCurrentTree.length; i++) {
				writeLine(writer, NewickStringWriter.formatName(leavesInCurrentTree[i], properties.getNodeBranchDataAdapter(ReadWriteParameterMap.KEY_LEAF_NODE_NAMES_ADAPTER, 
			  		document.getDefaultLeafAdapter()), properties.getBoolean(ReadWriteParameterMap.KEY_SPACES_AS_UNDERSCORE, false)));
			}
			writeLine(writer, NexusParser.COMMAND_END + "");
			decreaseIndention();
			decreaseIndention();
			writeCommand(writer, NexusParser.END_COMMAND);
			
			writeCommand(writer, NexusParser.BEGIN_COMMAND + " " + NexusParser.BLOCK_NAME_TREES);
			increaseIndention();
			writeLine(writer, NexusParser.TRANSL_TABLE_NAME + " ");
			increaseIndention();
			for (int i = 0; i < leavesInCurrentTree.length; i++) {
				writeLine(writer, (i + 1) + " " + NewickStringWriter.formatName(leavesInCurrentTree[i], properties.getNodeBranchDataAdapter(ReadWriteParameterMap.KEY_LEAF_NODE_NAMES_ADAPTER, 
			  		document.getDefaultLeafAdapter()), properties.getBoolean(ReadWriteParameterMap.KEY_SPACES_AS_UNDERSCORE, false)));
			}
			writeLine(writer, NexusParser.COMMAND_END + "");
			decreaseIndention();
			writeLine(writer, NexusParser.TREE_NAME + " " + "tree1 = " + NewickStringWriter.write(document.getTree(), properties));
			decreaseIndention();
			writeCommand(writer, NexusParser.END_COMMAND);
		}
		finally {
			writer.close();
		}
	}
	
	
	private void writeCommand(Writer writer, String content) throws IOException {
		writer.write(indention + content + NexusParser.COMMAND_END + SystemUtils.LINE_SEPARATOR);
	}
	
	
	private void writeLine(Writer writer, String content) throws IOException {
		writer.write(indention + content + SystemUtils.LINE_SEPARATOR);
	}
	
	
	private void increaseIndention() {
		indention += "\t";
	}
	
	
	private void decreaseIndention() {
		if (indention.length() > 0) {
			indention = indention.substring(1);
		}
	}
}