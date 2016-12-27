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
package info.bioinfweb.treegraph.graphics.positionpaint.label;


import java.awt.Graphics2D;

import info.bioinfweb.treegraph.document.Label;
import info.bioinfweb.treegraph.document.format.DistanceDimension;
import info.bioinfweb.treegraph.document.position.PositionData;



/**
 * Instances implementing this interface are responsible for calculating the width and height and for painting a certain type
 * of label.
 * 
 * @author Ben St&ouml;ver
 *
 * @param <L> the type of label to be painted
 */
public interface LabelPainter<L extends Label> {
	public DistanceDimension calculateDimension(Label label);
	
	public void paint(Graphics2D g, float pixelsPerMillimeter, PositionData pd, Label label);
	// The position data is provided here, although it could also be determined from the label instance if the position/paint type would
	// be specified instead. Since currently (and probably also in the future) painting of labels does not depend on the view mode the
	// parameter set was chosen this way, which would e.g. allow to remove the position data property from labels in the future.
	
	public Class<L> getLabelClass();
}
