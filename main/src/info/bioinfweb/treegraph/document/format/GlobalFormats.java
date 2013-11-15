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
package info.bioinfweb.treegraph.document.format;


import java.awt.Color;



/**
 * @author Ben St&ouml;ver
 */
public class GlobalFormats {
	public static final Color DEFAULT_BACKGROUNG_COLOR = Color.WHITE;
	public static final float DEFAULT_BRANCH_LENGTH_SCALE = 0.1f;
	public static final boolean DEFAULT_SHOW_SCALE_BAR = false;
	public static final boolean DEFAULT_SHOW_ROOTED = true;
	public static final boolean DEFAULT_ALIGN_LEGENDS_TO_SUBTREE = true;
	public static final boolean DEFAULT_POSITION_LABELS_TO_LEFT = true;
	
	
	private Color backgroundColor = DEFAULT_BACKGROUNG_COLOR;
	private Margin documentMargin = new Margin(2f);
	private DistanceValue branchLengthScale = new DistanceValue(DEFAULT_BRANCH_LENGTH_SCALE);  // Distance for a branch length = 1
	private boolean showScaleBar = DEFAULT_SHOW_SCALE_BAR;
	private boolean showRooted = DEFAULT_SHOW_ROOTED;
	private boolean alignLegendsToSubtree = DEFAULT_ALIGN_LEGENDS_TO_SUBTREE;
	private boolean positionLabelsToLeft = DEFAULT_POSITION_LABELS_TO_LEFT;

  
	public Color getBackgroundColor() {
		return backgroundColor;
	}


	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}


	public DistanceValue getBranchLengthScale() {
		return branchLengthScale;
	}


	public boolean getShowScaleBar() {
		return showScaleBar;
	}


	public void setShowScaleBar(boolean showScaleBar) {
		this.showScaleBar = showScaleBar;
	}


	public boolean getShowRooted() {
		return showRooted;
	}


	public void setShowRooted(boolean showRooted) {
		this.showRooted = showRooted;
	}


	public boolean getAlignLegendsToSubtree() {
		return alignLegendsToSubtree;
	}


	public void setAlignLegendsToSubtree(boolean alignLegendsToSubtree) {
		this.alignLegendsToSubtree = alignLegendsToSubtree;
	}


	public boolean getPositionLabelsToLeft() {
		return positionLabelsToLeft;
	}


	public void setPositionLabelsToLeft(boolean positionLabelsToLeft) {
		this.positionLabelsToLeft = positionLabelsToLeft;
	}


	public Margin getDocumentMargin() {
		return documentMargin;
	}


	public void assign(GlobalFormats other) {
		getDocumentMargin().assign(other.getDocumentMargin());
		getBranchLengthScale().assign(other.getBranchLengthScale());
		setShowScaleBar(other.getShowScaleBar());
		setShowRooted(other.getShowRooted());
		setAlignLegendsToSubtree(other.getAlignLegendsToSubtree());
		setPositionLabelsToLeft(other.getPositionLabelsToLeft());
		setBackgroundColor(other.getBackgroundColor());
	}


	@Override
	public GlobalFormats clone() {
		GlobalFormats result = new GlobalFormats();
		result.assign(this);
		return result;
	}
}