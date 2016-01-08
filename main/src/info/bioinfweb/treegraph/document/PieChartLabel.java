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


import java.util.List;
import java.util.Vector;

import info.bioinfweb.treegraph.document.format.ElementFormats;
import info.bioinfweb.treegraph.document.format.PieChartLabelFormats;



/**
 * Represents a pie chart label.
 * 
 * @author Ben St&ouml;ver
 * @since 2.0.43
 */
public class PieChartLabel extends GraphicalLabel implements LineElement {
	private List<String> valueIDs = new Vector<String>();
	private PieChartLabelFormats formats = new PieChartLabelFormats(this);
	
	
	public PieChartLabel(Labels labels) {
		super(labels);
	}
	
	
	public void addValueID(int index, String id) {
		valueIDs.add(index, id);
	}


	public boolean addValueID(String id) {
		return valueIDs.add(id);
	}


	public String getValueID(int index) {
		return valueIDs.get(index);
	}


	public String removeValueID(int index) {
		return valueIDs.remove(index);
	}


	public String setValueID(int index, String id) {
		return valueIDs.set(index, id);
	}


	public void clearValueIDs() {
		valueIDs.clear();
	}


	public int valueCount() {
		return valueIDs.size();
	}
	
	
	/**
	 * Returns the value of the data column for the ID with the specified index. (The value is not normalized to 
	 * 100 % or 2 Pi)
	 * 
	 * @param index - the index of the ID to be used
	 * @return the numeric value or {@link Double#NaN} if no data with this ID is available at the specified node
	 *         or a textual value is stored
	 * @throws NoLinkedNodeException - if this label has no linked node (no linked {@link Labels} object).
	 * @see #getPieChartAngles()
	 * @see IDManager#getDataByID(Node, String)
	 */
	public double getValue(int index) {
		Node node = getLinkedNode();
		if (node == null) {
			throw new NoLinkedNodeException();
		}
		else {
			TextElementData data = IDManager.getDataByID(node, getValueID(index));
			if (data != null) {
				return data.getDecimal(); 
			}
			else {
				return Double.NaN;
			}
		}
	}
	
	
	/**
	 * Returns an array with angles the single value would make up in a resulting pie chart in degrees. 
	 * @return a array with the length {@link #valueCount()}
	 * @throws NoLinkedNodeException - if this label has no linked node (no linked {@link Labels} object).
	 */
	public double[] getPieChartAngles() {
		double[] result = new double[valueCount()];
		double sum = 0;
		for (int i = 0; i < result.length; i++) {
			result[i] = getValue(i);
			sum += result[i];
		}
		
		for (int i = 0; i < result.length; i++) {
			result[i] = result[i] / sum * 360.0;
		}
		return result;
	}
	

	@Override
	public PieChartLabelFormats getFormats() {
		return formats;
	}

	
	@Override
	public void setFormats(ElementFormats formats) {
		((PieChartLabelFormats)formats).setOwner(this);
		this.formats = (PieChartLabelFormats)formats;
		reinsert();
	}
	
	
	public void assignPieChartData(PieChartLabel other) {
		valueIDs.clear();
		valueIDs.addAll(other.valueIDs);
	}

	
	/**
	 * Returns a deep copy of this pie chart label. Note that the linked {@link Labels}-object
	 * of the returned object is <code>null</code> no matter if this object was linked or not.
	 */
	@Override
	public PieChartLabel clone() {
		PieChartLabel result = new PieChartLabel(null);
		result.assignLabelData(this);
		result.assignPieChartData(this);
		result.setFormats(getFormats().clone());
		return result;
	}
}