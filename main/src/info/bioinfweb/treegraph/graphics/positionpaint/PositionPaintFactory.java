/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2015  Ben Stöver, Sarah Wiechers, Kai Müller
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


import java.util.*;



public class PositionPaintFactory {
	private static PositionPaintFactory firstInstance = null;
  private EnumMap<PositionPaintType, SinglePositionPaintFactory> factories = new EnumMap<PositionPaintType, SinglePositionPaintFactory>(PositionPaintType.class);
  
  
  private PositionPaintFactory() {
  	fillFactoryList();
  }
  
  
  public static PositionPaintFactory getInstance() {
  	if (firstInstance == null) {
  		firstInstance = new PositionPaintFactory();
  	}
  	return firstInstance;
  }
  
  
  private void fillFactoryList() {
  	factories.put(PositionPaintType.RECT_CLAD, new RectangularCladogramFactory());
  	factories.put(PositionPaintType.PHYLOGRAM, new PhylogramFactory());
  	// Hier neue Klassen hinzuf�gen.
  }
  
  
  public TreePositioner getPositioner(PositionPaintType type) {
 		return factories.get(type).getPositioner(); 
  }
  
  
  public TreePainter getPainter(PositionPaintType type) {
 		return factories.get(type).getPainter(); 
  }
  
  
  public String getName(PositionPaintType type) {
  	return factories.get(type).name();
  }
  
  
  public boolean needsBrancheLengths(PositionPaintType type) {
  	return factories.get(type).needsBranchLengths();
  }
  
  
  public PositionPaintType getType(TreePositioner positioner) {
  	for (PositionPaintType type: PositionPaintType.values()) {
			if (factories.get(type).isPositioner(positioner)) {
				return type;
			}
		}
  	return null;
  }
  
  
  public PositionPaintType getType(TreePainter painter) {
  	for (PositionPaintType type: PositionPaintType.values()) {
			if (factories.get(type).isPainter(painter)) {
				return type;
			}
		}
  	return null;
  }
  
  
  public static PositionPaintType getDefaultType() {
  	return PositionPaintType.RECT_CLAD;
  }
  
  
  public String[] names() {
  	String[] result = new String[size()];
  	for (int i = 0; i < size(); i++) {
			result[i] = factories.get(i).name();
		}
  	return result;
  }
  
  
  public int size() {
  	return factories.size();
  }
}