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
package info.bioinfweb.treegraph.document.io.xml;


import info.bioinfweb.treegraph.document.format.adapters.color.ColorAdapter;
import info.bioinfweb.treegraph.document.format.adapters.distance.DistanceAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.BranchLengthAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.HiddenBranchDataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeNameAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.TextLabelAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.UniqueNameAdapter;

import java.util.HashMap;



/**
 * Factory to create instances of classes implementing {@link NodeBranchDataAdapter}, {@link DistanceAdapter} or
 * {@link ColorAdapter}.
 * 
 * @author Ben St&ouml;ver
 * @since 2.0.46
 */
public class AdapterFactory {
  private static AdapterFactory firstInstance = null;
  
  private HashMap<String, Class<? extends NodeBranchDataAdapter>> dataAdapterMap = 
  	  new HashMap<String, Class<? extends NodeBranchDataAdapter>>();
  private HashMap<String, Class<? extends DistanceAdapter>> distanceAdapterMap = 
  	  new HashMap<String, Class<? extends DistanceAdapter>>();
  private HashMap<String, Class<? extends ColorAdapter>> colorAdapterMap = 
  	  new HashMap<String, Class<? extends ColorAdapter>>();
  
  
  protected AdapterFactory() {
		super();
		fillLists();
	}


  private void fillLists() {
  	// NodeBranchDataAdapters:
  	dataAdapterMap.put("BranchLength", BranchLengthAdapter.class);
  	dataAdapterMap.put("HiddenBranchData", HiddenBranchDataAdapter.class);
  	dataAdapterMap.put("HiddenNodeData", BranchLengthAdapter.class);
  	dataAdapterMap.put("NodeName", NodeNameAdapter.class);
  	dataAdapterMap.put("TextLabel", TextLabelAdapter.class);
  	dataAdapterMap.put("UniqueName", UniqueNameAdapter.class);
  }
  
  
	public static AdapterFactory getInstace() {
  	if (firstInstance == null) {
  		firstInstance = new AdapterFactory();
  	}
  	return firstInstance;
  }
}
