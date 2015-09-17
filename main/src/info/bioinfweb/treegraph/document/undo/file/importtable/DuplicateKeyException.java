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
package info.bioinfweb.treegraph.document.undo.file.importtable;


import java.util.List;
import java.util.Vector;



/**
 * This exception is thrown, if a name table file contains two or more identical
 * entries in the key column. (It is possible that the entries became identical
 * because of the selected option like ignoring whitespace or case sensitivity.)
 * 
 * @author Ben St&ouml;ver
 * @since 2.0.50
 */
public class DuplicateKeyException extends ImportTableException {
	private List<String> keys = new Vector<String>(8);
	
	
	public DuplicateKeyException() {
	  super("One or more keys are contained several times in the first column (key column) " +
	  		"of the imported table. Keys have to be unique.");
  }


	public boolean addKey(String key) {
	  return keys.add(key);
  }


	public List<String> getKeys() {
		return keys;
	}
}
