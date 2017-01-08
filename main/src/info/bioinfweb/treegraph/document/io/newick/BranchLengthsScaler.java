/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2011, 2013-2017  Ben Stöver, Sarah Wiechers, Kai Müller
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
package info.bioinfweb.treegraph.document.io.newick;


import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.Tree;
import info.bioinfweb.treegraph.document.format.BranchFormats;
import info.bioinfweb.treegraph.document.format.ScaleBarFormats;
import info.bioinfweb.commons.Math2;



/**
 * Sets the branch lengths scale of a tree so that the contained branched have a specific average 
 * length. Additionally the small interval of the scale bar is set in a way that it equals about a 
 * specified value.
 *  
 * @author Ben St&ouml;ver
 * @since 2.0.24
 */
public class BranchLengthsScaler {	
  private int count = 0;
  private double sum = 0;
  
  
  private void calculateAverage(Node root) {
  	if (root.getAfferentBranch().hasLength()) {
  		count++;
  		sum += root.getAfferentBranch().getLength();
  	}
  	
  	for (int i = 0; i < root.getChildren().size(); i++) {
			calculateAverage(root.getChildren().get(i));
		}
  }
  
  
  /**
	 * Sets the branch lengths scale of a tree so that the contained branched have the specified average 
	 * length. Additionally the small interval of the scale bar is set in a way that it equals about a 
	 * specified value.
	 * 
   * @param tree
   * @param averageLength - the average length in millimeters
   * @param smallIntervalLength - the approximate desired length of a small interval of the scale bar
   *        in millimeters
   */
  public void setAverageScale(Tree tree, double averageLength, float smallIntervalLength) {
  	// Set average length:
  	if (tree.getPaintStart() != null) {
	  	count = 0;
	  	sum = 0;
	  	calculateAverage(tree.getPaintStart());
	  	if (count > 0) {
	  		tree.getFormats().getBranchLengthScale().setInMillimeters((float)(averageLength / (sum / count)));
	  	}
  	}
  	
  	// Set scale bar small interval:
  	tree.getScaleBar().getFormats().setSmallInterval(Math2.roundFirstSignificantDigit(
  			smallIntervalLength / tree.getFormats().getBranchLengthScale().getInMillimeters()));
  }
  
  
  /**
	 * Sets the branch lengths scale of a tree so that the contained branched have 
	 * {@link BranchFormats.DEFAULT_MIN_LENGTH} as their average length. Additionally the small interval 
	 * of the scale bar is set in a way that it equals about {@link ScaleBarFormats.DEFAULT_SMALL_INTERVAL_IN_MM}.
   * @param tree
   * @since 2.0.41
   */
  public void setDefaultAverageScale(Tree tree) {
  	setAverageScale(tree, BranchFormats.DEFAULT_MIN_LENGTH,	ScaleBarFormats.DEFAULT_SMALL_INTERVAL_IN_MM);
  }
}