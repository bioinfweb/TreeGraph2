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
package info.bioinfweb.treegraph.document.nodebranchdata;


import java.text.DecimalFormat;



/**
 * Creates a new group of labels.
 * 
 * @author Ben St&ouml;ver
 */
public class NewTextLabelAdapter extends TextLabelAdapter implements NewNodeBranchDataAdapter {
	public NewTextLabelAdapter() {
		super("", new DecimalFormat());
	}

	
	public NewTextLabelAdapter(String labelID) {
	  super(labelID, new DecimalFormat());
  }


	public NewTextLabelAdapter(String labelID, DecimalFormat decimalFormat) {
	  super(labelID, decimalFormat);
  }


	@Override
	public boolean isNewColumn() {
		return true;
	}


	public void setID(String labelID) {
		id = labelID;
	}


	@Override
	public String toString() {
		return "New labels with the specified ID";
	}
}