/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2011, 2013-2019  Ben Stöver, Sarah Wiechers, Kai Müller
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
package info.bioinfweb.treegraph.gui.treeframe;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.collections4.iterators.UnmodifiableIterator;



/**
 * Class managing all highlighting groups of a {@link TreeViewPanel}.
 * 
 * @author Ben St&ouml;ver
 * @since 2.16.0
 */
public class ElementHighlighting {
	private TreeViewPanel owner;
	private Map<String, HighlightedGroup> groups = new HashMap<String, HighlightedGroup>();
	
	
	public ElementHighlighting(TreeViewPanel owner) {
		super();
		this.owner = owner;
	}


	public TreeViewPanel getOwner() {
		return owner;
	}


	public HighlightedGroup get(String key) {
		return groups.get(key);
	}

	
	public boolean isEmpty() {
		return groups.isEmpty();
	}

	
	public HighlightedGroup put(String key, HighlightedGroup value) {
		HighlightedGroup result = groups.put(key, value);
		if (result != value) {
			if (result != null) {
				owner.fireSelectionChanged(result);  //TODO The source of this event is not be part of the model anymore. Ideally this would be indicated by an additional event property.
			}
			owner.fireSelectionChanged(value);
		}
		return result;
	}

	
	public HighlightedGroup remove(String key) {
		HighlightedGroup result = groups.remove(key);
		if (result != null) {
			owner.fireSelectionChanged(result);  //TODO The source of this event is not be part of the model anymore. Ideally this would be indicated by an additional event property.
		}
		return result;
	}


	public int size() {
		return groups.size();
	}
	
	
	public Iterator<String> keyIterator() {
		return UnmodifiableIterator.unmodifiableIterator(groups.keySet().iterator());  // If a modifiable iterator would be returned, events would have to be triggered by that instance.
	}
}
