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
package info.bioinfweb.treegraph.document.tools;


import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import info.bioinfweb.commons.Math2;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.Tree;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.VoidNodeBranchDataAdapter;



public class NodeBranchDataColumnAnalyzer {
	private static class ColumnCharacters {
		public boolean containsNumericValue = false;
		public boolean containsParsableValue = false;
		public boolean containsValueOutsideRange = false;
		public boolean containsNonParsableValue = false;
		public boolean containsEmptyNodes = false;  // Added for completeness. Is currently not used to determine the ColumnStatus.
		public boolean containsInternalValues = false;
		public boolean containsTerminalValues = false;
	}
	
	
	public static enum ColumnStatus { 
		ALL_NUMERIC_INTERNAL_IN_RANGE,
		ALL_NUMERIC_OR_PARSABLE_INTERNAL_IN_RANGE,
		ALL_NUMERIC_INTERNAL,
		ALL_NUMERIC_OR_PARSABLE_INTERNAL,
		ALL_NUMERIC_OR_PARSABLE,
		SOME_NUMERIC_OR_PARSABLE,
		NO_NUMERIC_OR_PARSABLE;  // The order needs to reflect the suitability to use the described column as a source for support values, where the first is the likeliest.
	}
	
	
	private static class ColumnStatusComparator implements Comparator<NodeBranchDataAdapter> {
		private Map<String, ColumnStatus> statusMap;

		public ColumnStatusComparator(Map<String, ColumnStatus> statusMap) {
			super();
			this.statusMap = statusMap;
		}

		@Override
		public int compare(NodeBranchDataAdapter column1, NodeBranchDataAdapter column2) {
			if (column1 instanceof VoidNodeBranchDataAdapter) {
				if (column2 instanceof VoidNodeBranchDataAdapter) {
					return 0;
				}
				else {
					return 1;
				}
			}
			else if (column2 instanceof VoidNodeBranchDataAdapter) {
				return -1;
			}
			else {
				return statusMap.get(column1.toString()).compareTo(statusMap.get(column2.toString()));  // The string representations of the adapters are used since these should be unique for both type and column ID and hashCode() and equals() are not properly implemented for all node/branch data adapter classes.
				                                                                                        //TODO That is probably not the case and the adapters could also be used directly.
			}
		}
	}
	
	
	private static void checkValueOutOfRange(double value, ColumnCharacters characters) {
		characters.containsValueOutsideRange = characters.containsValueOutsideRange || !Math2.isBetween(value, 0.0, 100.0);
	}
	
	
	private static void checkTopologicalCharacters(Node node, ColumnCharacters characters) {
		if (node.isLeaf()) {
			characters.containsTerminalValues = true;
		}
		else {
			characters.containsInternalValues = true;
		}
	}
	
	
	private static void analyzeColumnCharacters(Node root, NodeBranchDataAdapter column, ColumnCharacters characters) {
		if (column.isEmpty(root)) {
			characters.containsEmptyNodes = true;
		}
		else {
			if (column.isDecimal(root)) {
				characters.containsNumericValue = true;
				checkValueOutOfRange(column.getDecimal(root), characters);
				checkTopologicalCharacters(root, characters);
			}
			else {  // Textual value
				String text = column.getText(root);
				try {
					checkValueOutOfRange(Double.parseDouble(text), characters);  //TODO Consider ',' instead of '.'? How is that usually done in TG? Is there a tool method?
					characters.containsParsableValue = true;  // Must happen after the parsing, since it should not be set when there is an exception.
					checkTopologicalCharacters(root, characters);
				}
				catch (NumberFormatException e) {
					characters.containsNonParsableValue = true;
				}
			}
		}
		
		for (Node child : root.getChildren()) {
			analyzeColumnCharacters(child, column, characters);
		}
	}
	
	
	public static ColumnStatus analyzeColumnStatus(Tree tree, NodeBranchDataAdapter column) {
		ColumnCharacters characters = new ColumnCharacters();
		if (!tree.isEmpty()) {
			analyzeColumnCharacters(tree.getPaintStart(), column, characters);
			
			if (!characters.containsNonParsableValue) {
				if (characters.containsInternalValues && !characters.containsTerminalValues) {
					if (characters.containsNumericValue && !characters.containsParsableValue) {
						if (characters.containsValueOutsideRange) {
							return ColumnStatus.ALL_NUMERIC_INTERNAL;
						}
						else {
							return ColumnStatus.ALL_NUMERIC_INTERNAL_IN_RANGE;
						}
					}
					if (characters.containsNumericValue || characters.containsParsableValue) {
						if (characters.containsValueOutsideRange) {
							return ColumnStatus.ALL_NUMERIC_OR_PARSABLE_INTERNAL;
						}
						else {
							return ColumnStatus.ALL_NUMERIC_OR_PARSABLE_INTERNAL_IN_RANGE;
						}
					}
				}
				if (characters.containsTerminalValues) {
					return ColumnStatus.ALL_NUMERIC_OR_PARSABLE;
				}
			}
			if (characters.containsNumericValue || characters.containsParsableValue) {
				return ColumnStatus.SOME_NUMERIC_OR_PARSABLE;
			}
		}
		return ColumnStatus.NO_NUMERIC_OR_PARSABLE;
	}
	
	
	public static void sortColumnListByStatus(Tree tree, List<NodeBranchDataAdapter> list) {
		Map<String, ColumnStatus> statusMap = new HashMap<String, NodeBranchDataColumnAnalyzer.ColumnStatus>();	
		for (NodeBranchDataAdapter column : list) {
			statusMap.put(column.toString(), analyzeColumnStatus(tree, column));
		}
		Collections.sort(list, new ColumnStatusComparator(statusMap));
	}
}
