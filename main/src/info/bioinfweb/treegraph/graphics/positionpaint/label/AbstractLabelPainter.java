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
import info.bioinfweb.treegraph.graphics.positionpaint.positiondata.PositionData;



public abstract class AbstractLabelPainter<L extends Label, P extends PositionData> implements LabelPainter<L, P> {
	@Override
	public void calculatePositionData(Label label, PositionData positionData) {
		doCalculatePositionData(getLabelClass().cast(label), getPositionDataClass().cast(positionData));
	}
	
	
	protected abstract void doCalculatePositionData(L label, P positionData);

	
	@Override
	public void paint(Graphics2D g, float pixelsPerMillimeter, PositionData pd, Label label) {
		doPaint(g, pixelsPerMillimeter, getPositionDataClass().cast(pd), getLabelClass().cast(label));
	}

	
	protected abstract void doPaint(Graphics2D g, float pixelsPerMillimeter, P positionData, L label);
}
