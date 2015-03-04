/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2015  Ben Stöver, Kai Müller
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
package info.bioinfweb.treegraph.document.format.adapters.distance;


import info.bioinfweb.treegraph.document.Label;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.format.DistanceValue;
import info.bioinfweb.treegraph.document.format.LabelFormats;
import info.bioinfweb.treegraph.gui.dialogs.specialformats.AbstractLabelAdapter;



/**
 * @author Ben St&ouml;ver
 * @since 2.0.25
 */
public abstract class AbstractLabelDistanceAdapter extends AbstractLabelAdapter 
    implements DistanceAdapter {
	
	public AbstractLabelDistanceAdapter(String id) {
		super(id);
	}


	protected abstract DistanceValue getDistanceValue(LabelFormats formats);
	

	/**
	 * Returns the distance value of the label with the specified ID in millimeters.
	 * @param node
	 * @return the text height or {@link Float#NaN} if no label with this ID exists on the specified node
	 * @see info.bioinfweb.treegraph.document.format.adapters.distance.DistanceAdapter#getDistance(info.bioinfweb.treegraph.document.Node)
	 */
	public float getDistance(Node node) {
		Label label = node.getAfferentBranch().getLabels().get(getID());
		DistanceValue value = null;
		if (label != null) {
			value = getDistanceValue(label.getFormats());
		}
		
		if (value != null) {
			return value.getInMillimeters();
		}
		else {
			return Float.NaN;
		}
	}
	

	public void setDistance(float distance, Node node) {
		Label label = node.getAfferentBranch().getLabels().get(getID());
		DistanceValue value = null;
		if (label != null) {
			value = getDistanceValue(label.getFormats());
		}
		
		if (value != null) {
			value.setInMillimeters(distance);
		}
	}
}