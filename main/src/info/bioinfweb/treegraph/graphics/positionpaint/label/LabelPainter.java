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
package info.bioinfweb.treegraph.graphics.positionpaint.label;


import java.awt.Graphics2D;

import info.bioinfweb.treegraph.document.Label;
import info.bioinfweb.treegraph.graphics.positionpaint.positiondata.PositionData;



/**
 * Instances implementing this interface are responsible for calculating the width and height and for painting a certain type
 * of label.
 * 
 * @author Ben St&ouml;ver
 * @since 2.13.0
 *
 * @param <L> the type of label to be painted
 * @param <P> the type of position data object to be used with labels of the type {@code L}
 */
public interface LabelPainter<L extends Label, P extends PositionData> {
	/**
	 * Implementations of this method should set the width and height of the position data object and all other relevant properties
	 * of label specific position data class instances. The left and top coordinate will be calculated by the positioner later and
	 * should be left unchanged.
	 * 
	 * @param label the label to be positioned
	 * @param positionData the position data object for {@code label}
	 * @throws ClassCastException if {@code label} or {@code positionData} are not instances of the classes returned by
	 *         {@link #getLabelClass()} and {@link #getPositionDataClass()}.
	 */
	public void calculatePositionData(Label label, PositionData positionData);
	
	/**
	 * Paints a label of the type supported by this class.
	 * 
	 * @param g the graphics context to paint the label to
	 * @param pixelsPerMillimeter the scale for the output of the label
	 * @param pd the position data object of the label
	 * @param label the label to be painted
	 * @throws ClassCastException if {@code label} or {@code positionData} are not instances of the classes returned by
	 *         {@link #getLabelClass()} and {@link #getPositionDataClass()}.
	 */
	public void paint(Graphics2D g, float pixelsPerMillimeter, PositionData pd, Label label);
	// The position data is provided here, although it could also be determined from the label instance if the position/paint type would
	// be specified instead. Since currently (and probably also in the future) painting of labels does not depend on the view mode the
	// parameter set was chosen this way, which would e.g. allow to remove the position data property from labels in the future.
	
	/**
	 * Return the class of label objects handled by this painter.
	 * 
	 * @return the label class
	 */
	public Class<L> getLabelClass();
	
	/**
	 * Return the class of position data objects to be used with labels if the type {@code L}.
	 * 
	 * @return the position data class
	 */
	public Class<P> getPositionDataClass();
}
