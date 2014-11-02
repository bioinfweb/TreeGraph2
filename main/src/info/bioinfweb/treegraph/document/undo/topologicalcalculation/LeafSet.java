/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2014  Ben Stöver, Kai Müller
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
package info.bioinfweb.treegraph.document.undo.topologicalcalculation;


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
	 * Compares this field or its complement to another. Note that both fields have be of the 
	 * same size.
	 * 
	 * @param other - the leaf field to be compared
	 * @param complement - defines whether the original of this field or its complement shall be compared
	 * @return 0 if exactly the same leafs are contained in both fields, 
	 *         -1 if the other field does not contain all leafs that this field contains (if so the 
	 *         complement of this field is tested before -1 is returned)
	 *         or a value greater than 0 if the other field contains more leafs than this one including 
	 *         all leafs contained here. (The return value than indicated the additional number of leafs 
	 *         contained in the other fields.)  
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
	 * field  does not contain and this field (or its complement) contains at least one terminal less than
	 * the specified field. (Tests whether this field (or its complement) is in the subtree of the 
	 * specified field.)
	 * 
	 * @param other
	 * @param complement
	 * @return
	 */
	public boolean inSubtreeOf(LeafSet other, boolean complement) {
		boolean oneLess = false;
		for (int i = 0; i < size(); i++) {
			boolean isChildHere = isChild(i);
			if (complement) {
				isChildHere = !isChildHere;
			}
			if (isChildHere && !other.isChild(i)) {
				return false;
			}
			oneLess = oneLess || (!isChildHere && other.isChild(i));
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