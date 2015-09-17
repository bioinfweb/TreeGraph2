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
package info.bioinfweb.treegraph.document.undo.edit;


import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.Legend;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.format.LegendFormats;

import java.util.HashMap;
import java.util.Map;



/**
 * @author Ben St&ouml;ver
 * @since 2.0.30
 */
public class LegendPaster {
	private Document document; 
  private Legend[] legends = null;
  private boolean uniqueNamesChanged = false;
  private Map<String, String> uniqueNameMap = new HashMap<String, String>();
  
  
	public LegendPaster(Document document, Legend[] legends) {
		super();
		this.document = document;
		this.legends = legends;
	}
  
  
	/**
	 * Returns a new unique name with is neither present in the associated document nor the
	 * unique name map. 
	 * @return
	 */
	private String newUniqueName() {
		String result;
		do {
			result = document.getTree().newUniqueName();
		} while (uniqueNameMap.containsValue(result));
		return result;
	}
	
	
	private void changeUniqueNamesSubtree(Node root) {
		String previousName = root.getUniqueName();
		root.setUniqueName(newUniqueName());
		uniqueNameMap.put(previousName, root.getUniqueName());
		
		for (int i = 0; i < root.getChildren().size(); i++) {
			changeUniqueNamesSubtree(root.getChildren().get(i));
		}
	}
	
	
	private void changeUniqueNamesLegends() {
		for (int i = 0; i < legends.length; i++) {
			LegendFormats f = legends[i].getFormats();
			for (int j = 0; j <= 1; j++) {
				if (f.getAnchorName(j) != null) {
					f.setAnchorName(j, uniqueNameMap.get(f.getAnchorName(j)));
				}
			}
		}
	}


	/**
	 * Changes the unique names of the subtree under <code>root</code> and the anchor names of the legends.
	 * If this method is called multiple times only the first call has an effect.
	 * @param root
	 */
	public void changeUniqueNames(Node root) {
		if (!uniqueNamesChanged) {
			changeUniqueNamesSubtree(root);
			changeUniqueNamesLegends();
			uniqueNamesChanged = true;
		}
	}
	
	
	public void pasteLegends() {
		for (int i = 0; i < legends.length; i++) {
			document.getTree().getLegends().insert(legends[i]);
		}
	}
	
	
	public void removeLegends() {
		for (int i = 0; i < legends.length; i++) {
			document.getTree().getLegends().remove(legends[i]);
		}
	}
}