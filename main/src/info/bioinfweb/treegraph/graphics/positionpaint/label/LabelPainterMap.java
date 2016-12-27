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


import info.bioinfweb.treegraph.document.Label;

import java.util.HashMap;
import java.util.Map;



public class LabelPainterMap {
	private static LabelPainterMap firstInstance = null;
	
	
	private Map<Class<? extends Label>, LabelPainter<? extends Label>> map = new HashMap<>();
	
	
	public static LabelPainterMap getInstance() {
		if (firstInstance == null) {
			firstInstance = new LabelPainterMap();
		}
		return firstInstance;
	}
	
	
	private LabelPainterMap() {
		super();
		fillMap();
	}
	
	
	private void fillMap() {
		put(new TextLabelPainter());
		put(new IconLabelPainter());
		put(new PieChartLabelPainter());
	}
	
	
	private void put(LabelPainter<?> painter) {
		map.put(painter.getLabelClass(), painter);
	}


	@SuppressWarnings("unchecked")
	public <L extends Label> LabelPainter<L> getLabelPainter(Class<L> labelClass) {
		return (LabelPainter<L>)map.get(labelClass);
	}
	
	
	@SuppressWarnings("unchecked")
	public <L extends Label> LabelPainter<L> getLabelPainter(L label) {
		return (LabelPainter<L>)getLabelPainter(label.getClass());
	}
}
