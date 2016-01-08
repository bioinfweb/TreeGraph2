/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2015-2016  Ben Stöver, Sarah Wiechers
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



public class Legends {
	private static final int CAPACITY = 4;
	private static final int CAPACITY_INCREMENT = 4;
	
	                                                
  private Tree tree = null;
	private Vector<Legend> list = new Vector<Legend>(CAPACITY, CAPACITY_INCREMENT);

  
	public Legends(Tree tree) {
		super();
		this.tree = tree;
	}


	public Tree getTree() {
		return tree;
	}


	/**
	 * Inserts the specified legend to the list and calls <code>l.setLegends(this)</code>.
	 * @param l - the legend to be inserted
	 * @return the postion where the legend was inserted
	 */
	public int insert(Legend l) {
		int pos = 0;
		while ((pos < list.size()) && (list.get(pos).getFormats().getPosition() <= l.getFormats().getPosition())) {
			pos++;
		}
		list.insertElementAt(l, pos);
		l.setLegends(this);
		return pos;
	}
	
	
	/**
	 * Reinserts a legend to ensure the correct order of the stored legends. This method 
	 * should be called if the position of a legend changes.
	 * @param l - the legend to reinsert
	 * @return the new position of the legend or -1 if the legend was not present in the 
	 * first case. 
	 */
	public int reinsert(Legend l) {
		if (list.remove(l)) {
			return insert(l);
		}
		else {
			return -1;
		}
	}

	
	public void clear() {
		list.clear();
	}

	
	public boolean contains(Legend l) {
		return list.contains(l);
	}

	
	public Legend get(int index) {
		return list.get(index);
	}

	
	public int indexOf(Legend l) {
		return list.indexOf(l);
	}

	
	public boolean isEmpty() {
		return list.isEmpty();
	}

	
	public boolean remove(Legend l) {
		return list.remove(l);
	}

	
	public int size() {
		return list.size();
	}


	public <T> T[] toArray(T[] arr) {
		return list.toArray(arr);
	}
}