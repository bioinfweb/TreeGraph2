/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2013  Ben Stöver, Kai Müller
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
package info.bioinfweb.treegraph.document.format.adapters.color;


import info.bioinfweb.treegraph.document.IconLabel;
import info.bioinfweb.treegraph.document.Label;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.gui.dialogs.specialformats.AbstractLabelAdapter;

import java.awt.Color;



/**
 * @author Ben St&ouml;ver
 * @since 2.0.25
 */
public class LabelLineColorAdapter extends AbstractLabelAdapter implements ColorAdapter {
	public LabelLineColorAdapter(String id) {
		super(id);
	}

	
	public void setColor(Color color, Node node) {
		Label label = node.getAfferentBranch().getLabels().get(getID());
		if ((label != null) && (label instanceof IconLabel)) {
			((IconLabel)label).getFormats().setLineColor(color);
		}
	}

	
	public Color getColor(Node node) {
		Label label = node.getAfferentBranch().getLabels().get(getID());
		if ((label != null) && (label instanceof IconLabel)) {
			return ((IconLabel)label).getFormats().getLineColor();
		}
		else {
			return null;
		}
	}


	@Override
	public String toString() {
		return toString("Line colors");
	}
}