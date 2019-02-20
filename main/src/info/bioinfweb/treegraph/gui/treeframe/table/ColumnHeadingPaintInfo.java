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
package info.bioinfweb.treegraph.gui.treeframe.table;



public class ColumnHeadingPaintInfo {
	private boolean[] lines;
	private boolean lastUnderParent;
	
	
	public ColumnHeadingPaintInfo(boolean[] lines, boolean isLastUnderParent) {
		super();
		this.lines = lines;
		this.lastUnderParent = isLastUnderParent;
	}

	
	public static ColumnHeadingPaintInfo createChildInstance(ColumnHeadingPaintInfo parentInfo, boolean lineOnLastLevel, boolean lastUnderParent, 
			boolean parentLastUnderGrandparent) {
		
		boolean[] lines = new boolean[parentInfo.lines.length + 1];
		for (int i = 0; i < parentInfo.lines.length; i++) {
			lines[i] = parentInfo.hasLine(i);
		}
		lines[lines.length - 1] = lineOnLastLevel;
		if (parentLastUnderGrandparent && (lines.length >= 3)) {
			lines[lines.length - 3] = false;  // Line starting at grandparent has ended.
		}
		
		return new ColumnHeadingPaintInfo(lines, lastUnderParent);
	}
	

	public boolean hasLine(int level) {
		if (level >= lines.length) {
			return false;
		}
		else {
			return lines[level];
		}
	}
	
	
	public void setLine(int level, boolean value) {
		lines[level] = value;
	}


	public boolean isLastUnderParent() {
		return lastUnderParent;
	}


	public void setLastUnderParent(boolean lastUnderParent) {
		this.lastUnderParent = lastUnderParent;
	}
	
	
	public int getLabelLevel() {
		return lines.length - 1;  // Instances are constructed in a way that the array only spans from the top to the current label.
	}
}
