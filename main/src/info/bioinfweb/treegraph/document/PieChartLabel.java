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


import info.bioinfweb.treegraph.document.format.ElementFormats;
import info.bioinfweb.treegraph.document.format.PieChartLabelFormats;
import info.bioinfweb.treegraph.document.tools.IDManager;

import java.util.ArrayList;
import java.util.List;



/**
 * Represents a pie chart label.
 * 
 * @author Ben St&ouml;ver
 * @since 2.0.43
 */
public class PieChartLabel extends GraphicalLabel implements LineElement, TextElement {
	public static class SectionData implements Cloneable {
		private String valueColumnID = "";
		private String caption = "";
		
		public SectionData(String valueColumnID, String caption) {
			super();
			this.valueColumnID = valueColumnID;
			this.caption = caption;
		}

		public String getValueColumnID() {
			return valueColumnID;
		}
		
		public void setValueColumnID(String valueColumnID) {
			this.valueColumnID = valueColumnID;
		}
		
		public String getCaption() {
			return caption;
		}
		
		public void setCaption(String caption) {
			this.caption = caption;
		}

		@Override
		public String toString() {
			String caption = "";
			if ((getCaption() != null) && !getCaption().isEmpty()) {
				caption = " (" + getCaption() + ")";
			}
			return getValueColumnID() + caption;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((caption == null) ? 0 : caption.hashCode());
			result = prime * result
					+ ((valueColumnID == null) ? 0 : valueColumnID.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			SectionData other = (SectionData) obj;
			if (caption == null) {
				if (other.caption != null)
					return false;
			} else if (!caption.equals(other.caption))
				return false;
			if (valueColumnID == null) {
				if (other.valueColumnID != null)
					return false;
			} else if (!valueColumnID.equals(other.valueColumnID))
				return false;
			return true;
		}

		@Override
		public SectionData clone() {
			return new SectionData(getValueColumnID(), getCaption());
		}
	}
	
	
	private List<SectionData> sectionDataList = new ArrayList<>();
	private TextElementData title = new TextElementData();
	private PieChartLabelFormats formats = new PieChartLabelFormats(this);
	
	
	public PieChartLabel(Labels labels) {
		super(labels);
	}
	
	
	public List<SectionData> getSectionDataList() {
		return sectionDataList;
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
			TextElementData data = IDManager.getDataByID(node, getSectionDataList().get(index).getValueColumnID());
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
	 * 
	 * @return a array with the length {@link #valueCount()}
	 * @throws NoLinkedNodeException - if this label has no linked node (no linked {@link Labels} object).
	 */
	public double[] getPieChartAngles() {
		double[] result = new double[getSectionDataList().size()];
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
	public TextElementData getData() {
		return title;
	}


	@Override
	public void assignTextElementData(TextElement other) {
		getData().assign(other.getData());
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
		getSectionDataList().clear();
		for (SectionData sectionData : other.getSectionDataList()) {
			getSectionDataList().add(sectionData.clone());
		}
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