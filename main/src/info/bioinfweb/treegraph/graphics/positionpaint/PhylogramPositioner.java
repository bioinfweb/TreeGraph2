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
package info.bioinfweb.treegraph.graphics.positionpaint;


import info.bioinfweb.treegraph.document.Branch;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.graphics.positionpaint.positiondata.PositionData;



public class PhylogramPositioner extends RectangularCladogramPositioner {
	private static PhylogramPositioner firstInstance = null;
	
	
	public PhylogramPositioner() {
		super();
		type = PositionPaintFactory.getInstance().getType(this);
	}


	public static PhylogramPositioner getInstance() {
		if (firstInstance == null) {
			firstInstance = new PhylogramPositioner();
		}
		return firstInstance;
	}
	
	
	/**
	 * This method is overwritten to ensure that no corner radius in painted.
	 * 
	 * @see info.bioinfweb.treegraph.graphics.positionpaint.RectangularCladogramPositioner#rescaleNodeWidth(info.bioinfweb.treegraph.document.Node, float)
	 */
	@Override
	protected float rescaleNodeWidth(Node node, float width) {
		if (node.isLeaf()) {
			return width;
		}
		else {
			return node.getFormats().getLineWidth().getInMillimeters();
		}
	}


	/**
	 * This method is overwritten to regard the stored branch lengths.
	 * 
	 * @see info.bioinfweb.treegraph.graphics.positionpaint.RectangularCladogramPositioner#rescaleBranchWidth(info.bioinfweb.treegraph.document.Branch, float)
	 */
	@Override
	protected float rescaleBranchWidth(Branch branch, float width) {
		if (branch.hasLength()) {
			if (branch.getTargetNode().hasParent() || document.getTree().getFormats().getShowRooted()) {
				float branchLength = (float)branch.getLength() * document.getTree().getFormats().getBranchLengthScale().getInMillimeters();
		  	if (branch.getTargetNode().hasParent()) {
		  		float nodeWidth = branch.getTargetNode().getParent().getPosition(type).getWidth().getInMillimeters();
		  		return ((branchLength + nodeWidth) * rescalingFactorX - nodeWidth) - nodeWidth;
		  	}
		  	else {
		  		return branchLength * rescalingFactorX;  // Leave space to display labels if they use more space than the branch.
		  	}
			}
			else {
				return 0f;
			}
		}
		else {
			return super.rescaleBranchWidth(branch, width);
		}
	}


	/**
	 * This method is overwritten to ensure that no corner radius in painted.
	 * 
	 * @see info.bioinfweb.treegraph.graphics.positionpaint.RectangularCladogramPositioner#calculateCornerRadiusShift(info.bioinfweb.treegraph.document.Node, float)
	 */
	@Override
	protected float calculateCornerRadiusShift(Node node, float y) {
		return 0f;
	}


	/**
	 * Always returns <code>false</code> because label blocks could overlap to the left in a phylogram.
	 */
	@Override
	protected boolean xToHigh(float x, PositionData pd) {
		return false;
	}
}