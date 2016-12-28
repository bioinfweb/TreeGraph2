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


import info.bioinfweb.treegraph.document.TextLabel;
import info.bioinfweb.treegraph.document.format.DistanceDimension;
import info.bioinfweb.treegraph.document.format.TextLabelFormats;
import info.bioinfweb.treegraph.document.position.PositionData;
import info.bioinfweb.treegraph.graphics.positionpaint.PositionPaintUtils;

import java.awt.Graphics2D;



public class TextLabelPainter extends AbstractLabelPainter<TextLabel> {
	@Override
	protected DistanceDimension doCalculateDimension(TextLabel label) {
		return PositionPaintUtils.calculateTextDimension(label);
	}

	
	@Override
	protected void doPaint(Graphics2D g, float pixelsPerMillimeter, PositionData pd, TextLabel label) {
		TextLabelFormats f = label.getFormats(); 
		PositionPaintUtils.paintText(g, pixelsPerMillimeter, label.getData().formatValue(f.getDecimalFormat()), f, 
				pd.getLeft().getInPixels(pixelsPerMillimeter), 
				pd.getTop().getInPixels(pixelsPerMillimeter) + g.getFontMetrics(f.getFont(pixelsPerMillimeter)).getAscent());
	}


	@Override
	public Class<TextLabel> getLabelClass() {
		return TextLabel.class;
	}
}
