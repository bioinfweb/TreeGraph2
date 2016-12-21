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
package info.bioinfweb.treegraph.document;


import java.util.Vector;



public class LabelLine implements Cloneable {
	private static final int INITIAL_CAPACITY = 4; 
	private static final int CAPACITY_INCREMENT = 4; 

	
	private Vector<Label> line = new Vector<Label>(INITIAL_CAPACITY, CAPACITY_INCREMENT);
	
	
	public int insert(Label label) {
		int pos = 0;
		while ((pos < line.size()) && 
				(line.get(pos).getFormats().getLinePosition() <= label.getFormats().getLinePosition())) {
			
			pos++;
		}
		line.insertElementAt(label, pos);
		return pos;
	}
	
	
	public boolean remove(Label label) {
		return line.remove(label);
	}
	
	
	public Label get(int pos) {
		return line.get(pos);
	}
	
	
	/**
	 * Returns the index of the label at the given line position. If no label has 
	 * the given line position the element with the closest smaller position is 
	 * returned. If there is no such element too, -1 is returned. 
	 * @param linePos
	 * @return the index of the element in the line list.
	 */
	public int getIndexBeforeLinePos(double linePos) {
		int pos = 0;
		while ((pos < line.size()) && (linePos >= get(pos).getFormats().getLinePosition())) {
			pos++;
		}
	  return pos - 1;
	}
	
	
  public boolean contains(Label label) {
  	return line.contains(label);
  }
  
  
  public boolean contains(Class<? extends Label> labelClass) {
  	for (Label label : line) {
			if (labelClass.isInstance(label)) {
				return true;
			}
		}
  	return false;
  }
	
	
	public int size() {
		return line.size();
	}
	
	
	@Override
	protected LabelLine clone() {
		LabelLine result = new LabelLine();
		for (int i = 0; i < size(); i++) {
			result.insert(get(i).clone());
		}
		return result;
	}


	public int indexOf(Label label) {
		return line.indexOf(label);
	}
}