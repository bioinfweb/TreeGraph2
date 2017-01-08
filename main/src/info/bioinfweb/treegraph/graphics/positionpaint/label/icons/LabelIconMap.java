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
package info.bioinfweb.treegraph.graphics.positionpaint.label.icons;


import java.util.Collection;
import java.util.HashMap;
import java.util.Map;



/**
 * Stores a list of instances of all available label icons.
 * @author Ben St&ouml;ver
 * @since 2.0.25
 */
public class LabelIconMap {
	public static final LabelIcon UNKNOWN_ICON = new UnknownIcon();
	
	
	private static LabelIconMap firstInstance = null;
	
	
  private Map<String, LabelIcon> map = new HashMap<String, LabelIcon>();


	private LabelIconMap() {
		super();
		fill();
	}
	
	
	private void put(LabelIcon icon) {
		map.put(icon.id(), icon);
	}
	
	
	/**
	 * Adds all available icons to the map. (called in the constructor)
	 */
	private void fill() {
		put(new TriangleIcon());
		put(new RectangleIcon());
		put(new RoundRectangleIcon());
		put(new EllipseIcon());
		put(new HexagonIcon());
		put(new TiltedHexagonIcon());
		put(new OctagonIcon());
		put(new CrossIcon());
		put(new TiltedCrossIcon());
		put(new StarIcon());
		// Add more icons here
	}
  
	
	public static LabelIconMap getInstance() {
		if (firstInstance == null) {
			firstInstance = new LabelIconMap();
		}
		return firstInstance;
	}
	
	
	public String getDefaultID() {
		return StarIcon.ID;
	}


	public LabelIcon get(String id) {
		LabelIcon result = map.get(id);
		if (result == null) {
			result = UNKNOWN_ICON;
		}
		return result;
	}


	public int size() {
		return map.size();
	}


	public Collection<LabelIcon> values() {
		return map.values();
	}
}