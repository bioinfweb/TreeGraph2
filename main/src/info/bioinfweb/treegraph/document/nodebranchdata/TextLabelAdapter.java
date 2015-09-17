/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2015  Ben St�ver, Kai M�ller
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
package info.bioinfweb.treegraph.document.nodebranchdata;


import java.text.DecimalFormat;

import info.bioinfweb.treegraph.document.Label;
import info.bioinfweb.treegraph.document.Labels;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.TextElementData;
import info.bioinfweb.treegraph.document.TextLabel;
import info.bioinfweb.treegraph.document.format.TextFormats;



public class TextLabelAdapter extends AbstractIDElementAdapter implements NodeBranchDataAdapter {
	public TextLabelAdapter(String labelID, DecimalFormat defaultDecimalFormat) {
		super(labelID, defaultDecimalFormat);
	}


	public TextLabelAdapter(String id) {
		super(id);
	}


	@Override
	public String getName() {
		return NAME_PREFIX + "textLabelAdapter";
	}


	@Override
	public TextElementData getData(Node node) {
		Label l = getDataElement(node);
		if ((l != null) && (l instanceof TextLabel)) {
			return ((TextLabel)l).getData();
		}
		else {
			return null;
		}
	}


	@Override
	public boolean assignData(Node node, TextElementData data) {
		Label l = getDataElement(node);
		boolean result = (l != null) && (l instanceof TextLabel);
		if (result) {
			((TextLabel)l).getData().assign(data);
		}
		return result;
	}


	@Override
	public DecimalFormat getDecimalFormat(Node node) {
		Label l = node.getAfferentBranch().getLabels().get(getID());
		if ((l != null) && (l instanceof TextLabel)) {
			return ((TextLabel)l).getFormats().getDecimalFormat();
		}
		else {
			return super.getDecimalFormat(node);
		}
	}


	@Override
	protected void createData(Node node) {
		if (node.hasAfferentBranch()) {
			Labels labels = node.getAfferentBranch().getLabels();
			Label label = labels.get(getID());
			if ((label == null) || !(label instanceof TextLabel)) {
				label = new TextLabel(labels);
				label.setID(getID());
				((TextLabel)label).getFormats().setDecimalFormat(getDecimalFormat(node), TextFormats.DEFAULT_LOCALE);
				labels.add(label);
			}
		}
	}


	public void delete(Node node) {
		Labels labels = node.getAfferentBranch().getLabels();
		Label label = labels.get(getID());
		if (label != null) {
			labels.remove(label);
		}
	}


	public TextLabel getDataElement(Node node) {
		Label l = node.getAfferentBranch().getLabels().get(getID());
		if (l instanceof TextLabel) {
			return (TextLabel)l;
		}
		else {
			return null;
		}
	}


	public String toString() {
		return "Text labels with the ID \"" + getID() + "\"";
	}
}