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

import info.bioinfweb.treegraph.document.IconLabel;
import info.bioinfweb.treegraph.document.format.IconLabelFormats;
import info.bioinfweb.treegraph.document.position.PositionData;
import info.bioinfweb.treegraph.graphics.positionpaint.label.icons.LabelIconMap;



public class IconLabelPainter extends AbstractGraphicalLabelPainter<IconLabel> {
	@Override
	public void paint(Graphics2D g, float pixelsPerMillimeter, PositionData pd, IconLabel label) {
		IconLabelFormats f = ((IconLabel)label).getFormats();
		LabelIconMap.getInstance().get(f.getIcon()).paint(g, 
				pd.getLeft().getInPixels(pixelsPerMillimeter), 
				pd.getTop().getInPixels(pixelsPerMillimeter), f, pixelsPerMillimeter);
	}

	
	@Override
	public Class<IconLabel> getLabelClass() {
		return IconLabel.class;
	}
}
