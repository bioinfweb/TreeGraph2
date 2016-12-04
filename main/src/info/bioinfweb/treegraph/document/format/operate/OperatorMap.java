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
package info.bioinfweb.treegraph.document.format.operate;


import java.util.Collection;
import java.util.HashMap;



public class OperatorMap {
  private HashMap<Class, FormatOperator> operators = new HashMap<Class, FormatOperator>();
  
  
  public void addOperator(FormatOperator operator) {
  	operators.put(operator.getClass(), operator);  //TODO wird alter Wert �berschrieben?
  }
  
  
  public void clear() {
  	operators.clear();
  }


	public Collection<FormatOperator> values() {
		return operators.values();
	}


	public FormatOperator[] toArray() {
		return operators.values().toArray(new FormatOperator[0]);
	}
}