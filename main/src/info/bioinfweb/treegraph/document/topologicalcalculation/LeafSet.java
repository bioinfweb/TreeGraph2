/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2015-2016  Ben Stöver, Sarah Wiechers, Kai Müller
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


import info.bioinfweb.commons.Math2;



/**
 * Used to store which leaf nodes of a tree are contained in a certain subtree. Internally a compressed boolean field 
 * is used to store a value for each leaf node index. 
 * 
 * @author Ben St&ouml;ver
 * @since 2.0.33
 */
public class LeafSet {
	private int[] field;
	private int size;

	
	public LeafSet(int size) {
		super();
		
		field = new int[Math2.divAbove(size, Integer.SIZE)];
		this.size = size; 
		for (int i = 0; i < field.length; i++) {
			field[i] = 0;
		}
	}
	
	
	@Override
	public int hashCode() {
		int result = 0;
		for (int i = 0; i < field.length; i++) {
			result += field[i];
		}
		return result;
	}


	public boolean isChild(int pos) {
		return (field[pos / Integer.SIZE] & Math2.intPow(2, pos % Integer.SIZE)) != 0; 
	}
	
	
	public void setChild(int pos, boolean value) {
		if (value){
			field[pos / Integer.SIZE] = field[pos / Integer.SIZE] | Math2.intPow(2, pos % Integer.SIZE);
		}
		else {
			field[pos / Integer.SIZE] = field[pos / Integer.SIZE] & ~Math2.intPow(2, pos % Integer.SIZE);
	  }
	}
	
	
	public int size() {
		return size;
	}
	
	
	public int childCount() {
		int result = 0;
		for (int pos = 0; pos < size(); pos++) {
	    if (isChild(pos)) {
	    	result++;
	    }
    }
		return result;
	}


	public void addField(LeafSet other) {
		for (int i = 0; i < other.size(); i++) {
			if (other.isChild(i)) {
				setChild(i, true);
			}
		}
	}
	
	
	/**
	 * Performs a binary {@code AND} operation between this instance and the specified leaf set and returns the result 
	 * as new instance. This instance is not modified by this method.
	 * 
	 * @param other the leaf set to be added
	 * @return a new leaf set instance
	 */
	public LeafSet and(LeafSet other) {
		if (other.size() != size()) {
			throw new IllegalArgumentException();
		}
		else {
			LeafSet result = new LeafSet(size());
			for (int i = 0; i < field.length; i++) {
				result.field[i] = field[i] & other.field[i];
			}
			return result;
		}		
	}
	
	
	/**
	 * Compares this field or its complement to another. Note that both fields have to be of the 
	 * same size.
	 * 
	 * @param other - the leaf field to be compared
	 * @param complement - defines whether the original of this field or its complement shall be compared
	 * @return 0 if exactly the same leaves are contained in both fields, 
	 *         -1 if the other field does not contain all leaves that this field contains (if so the 
	 *         complement of this field is also tested before -1 is returned)
	 *         or a value greater than 0 if the other field contains more leaves than this one. (The return 
	 *         value then indicates the additional number of leaves contained in the other fields.)  
	 */
	public int compareTo(LeafSet other, boolean complement) {
		int additionalCount = 0;
		for (int i = 0; i < size(); i++) {
			boolean isChildHere = isChild(i);
			if (complement) {
				isChildHere = !isChildHere;
			}
			if (isChildHere) {
				if (!other.isChild(i)) {
					return -1;
				}
			}
			else {
				if (other.isChild(i)) {
					additionalCount++;
				}
			}
		}
		return additionalCount;
	}
	
	
	/**
	 * Returns <code>false</code> if this field (or its complement) contains any terminal the specified 
	 * field does not contain and this field (or its complement) contains at least one terminal less than
	 * the specified field. (Tests whether this field (or its complement) is in the subtree of the 
	 * specified field.)
	 * 
	 * @param parent a leaf set describing a subtree that shall be tested be be the parent of the subtree
	 *        represented by this instance
	 * @param complement Specify {@code true} here, if the complement of {@code parent} shall be compared
	 *        to this instance of {@code false} if parent shall be compared directly. 
	 * @return {@code true} if this instance represents a subset of the other leaf set, {@code false}
	 *         otherwise
	 */
	public boolean inSubtreeOf(LeafSet parent, boolean complement) {
		boolean oneLess = false;
		for (int i = 0; i < size(); i++) {
			boolean isChildHere = isChild(i);
			if (complement) {
				isChildHere = !isChildHere;
			}
			if (isChildHere && !parent.isChild(i)) {
				return false;
			}
			oneLess = oneLess || (!isChildHere && parent.isChild(i));
		}
		return oneLess;
	}


	public LeafSet complement() {
		LeafSet result = new LeafSet(size());
		for(int i = 0 ; i < field.length; i++ ){
			result.field[i] = ~field[i];
		}
		return result;
	}
	
	
	/**
	 * Tests if this field (or its complement) contains at least one element from the specified field and
	 * at least one which is not contained in <code>other</code>.
	 * 
	 * @param other
	 * @param complement
	 * @return
	 */
	public boolean containsAnyAndOther(LeafSet other, boolean complement) {
		boolean containsOther = false;
		boolean containsAny = false;
		for (int i = 0; i < size(); i++) {
			boolean isChildHere = isChild(i);
			if (complement) {
				isChildHere = !isChildHere;
			}
			containsAny = containsAny || (isChildHere && other.isChild(i));
			containsOther = containsOther || (isChildHere && !other.isChild(i));
			if (containsAny && containsOther) {
				return true;
			}
		}
		return false;
	}
	
	
	/**
	 * Returns {@code true} if all terminals contained in {@code subset} are also contained in this set.
	 * The complement of any of these sets is not tested.
	 * 
	 * @param subset - the other set to be compared
	 * @return {@code true} if this set contains at least all children that are contained in {@code other},
	 *         {@code false} if {@code subset} contains at least one child that is not contained in this set.
	 * @throws IllegalArgumentException - if the other set differs in size from this set.
	 */
	public boolean containsAll(LeafSet subset) {
		if (subset.size() == size()) {
			for (int i = 0; i < field.length; i++) {
		    if ((field[i] & subset.field[i]) != subset.field[i]) {
		    	return false;
		    }
	    }
			return true;
		}
		else {
			throw new IllegalArgumentException("The other set has a different size than this set. " +
					"Comparing sets of different sizes is not allowed.");
		}
	}


	@Override
	public boolean equals(Object other) {
		if (other instanceof LeafSet) {
			LeafSet otherField = (LeafSet)other;
			if (otherField.size() == size()) {
				if (size() > 0) {
					for (int i = 0; i < field.length; i++){
					 if (field[i] != otherField.field[i]){
							return false;
					 	}
					}
				  return true;
				}
			}
		}
		return false;
	}


	@Override
	public String toString() {
		StringBuffer result = new StringBuffer(size());
		for (int i = 0; i < size(); i++) {
			if (isChild(i)) {
				result.append("1");
			}
			else {
				result.append("0");
			}
		}
		return result.toString();
	}
}